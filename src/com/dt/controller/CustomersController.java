/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;

import com.dt.application.Global;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert;

import com.dt.dto.*;
import com.dt.utils.*;
import java.io.IOException;
import java.math.BigDecimal;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.dt.dao.CustomersPersistence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Dinesh
 */
public class CustomersController implements TabContent {

    public Stage MainWindow;

    @FXML
    private ListView<CustomerWithState> lwCustomers;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfCity;
    @FXML
    private TextField tfPhoneNumbers;
    @FXML
    private TextField tfOpeningBalance;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnNew;
    @FXML
    private TextField tfSearchCustomer;
    @FXML private ImageView checkImage;

    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    private List<CustomerWithState> deletedCustomers = null;
    private TabPane tabPane;
    private ObservableList<CustomerWithState> customerList = null;
    private FilteredList<CustomerWithState> filteredList = null;

    /**
     * Initializes the controller class.
     */
    public void initialize() {

        btnEdit.disableProperty().bind(lwCustomers.getSelectionModel()
                .selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(lwCustomers.getSelectionModel()
                .selectedItemProperty().isNull());

        btnSave.disableProperty().bind(isDirty.not());

         bindUIToModel();
        
        tfSearchCustomer.textProperty().addListener((
                ObservableValue<? extends String> observable,
                String oldValue, String newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                filteredList.setPredicate(null);
            } else {
                filteredList.setPredicate((CustomerWithState t) -> 
                        t.getName().toLowerCase().contains(newValue.toLowerCase())
                );
            }
        });
        
         checkImage.managedProperty().bind(checkImage.visibleProperty());
        checkImage.visibleProperty().bind(checkImage.opacityProperty()
                .greaterThan(0.0));
        
    }

    @Override
    public boolean shouldClose() {
         if (isDirty.get()) {
            ButtonType response = shouldSaveUnsavedData();
            if (response == ButtonType.CANCEL) {
                return false;
            }

            if (response == ButtonType.YES) {
                return saveData(false);
            }

        }

        return true;
    }

    @Override
    public void putFocusOnNode() {
        ObservableList<CustomerWithState> items = lwCustomers.getItems();
        if (items != null && !items.isEmpty()) {
            lwCustomers.getSelectionModel().selectFirst();
            lwCustomers.requestFocus();
        } else {
            btnNew.requestFocus();;
        }
        
    }

    @Override
    public boolean loadData() {

       List<Customer> list = null;
        try {
            list = CustomersPersistence.getCustomers();
        } catch (Exception e) {
            String message = Utility.getDataFetchErrorText();
            Alert alert = Utility.getErrorAlert("Error Occurred",
                    "Error in Fetching Customers List", message, MainWindow);
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
        
       final List<CustomerWithState> customers = CustomerWithState.fromCustomers(list);
       customerList = 
               FXCollections.<CustomerWithState>observableList(customers,
                        (CustomerWithState customer) -> {
                            Observable[] array = new Observable[]{
                                customer.nameProperty(), customer.cityProperty(),
                                customer.phoneNumbersProperty(), 
                                customer.openingBalanceProperty(),
                                customer.balanceTypeProperty()
                            };

                            return array;
                        });

        filteredList = new FilteredList(customerList, null);
        lwCustomers.setItems(filteredList);
        
        customerList.addListener((ListChangeListener.Change<? extends CustomerWithState> c) -> {
            onCustomersListChanged(c);
        });
        
        tfSearchCustomer.disableProperty().bind(Bindings.isEmpty(customerList));

        isDirty.set(false);
        return true;
    }

    private void populateFields(CustomerWithState customer) {

        if (customer == null) {
            clearFields();
            return;
        }

        tfName.setText(customer.getName());
        String city = customer.getCity();
        if (city != null) {
            tfCity.setText(city);
        } else {
            tfCity.clear();
        }

        String numbers = customer.getPhoneNumbers();
        if (numbers != null) {
            tfPhoneNumbers.setText(numbers);
        } else {
            tfPhoneNumbers.clear();
        }

        BigDecimal balance = customer.getOpeningBalance();
        if (balance == null) {
            tfOpeningBalance.clear();
        } else {
            if (balance.signum() == 0) { //do NOT use the Equals method here.
                tfOpeningBalance.setText("0.00");
            } else {
                 String text = balance.toPlainString();
                 if (customer.getBalanceType() != null) {
                     text += " " + customer.getBalanceType().toString();
                }
                tfOpeningBalance.setText(text);
            }
        }
        
    }

    private void clearFields() {
        tfName.clear();
        tfCity.clear();
        tfOpeningBalance.clear();
        tfPhoneNumbers.clear();
    }

    @FXML
    private void onNewCustomerAction() throws IOException {

        Stage stage = getCustomerPopupWindow();
        stage.setTitle("New Customer");
        CustomerEditController controller = (CustomerEditController) stage.getUserData();

        stage.showAndWait();
        if (!controller.isOkayProperty().get()) {
            return;
        }

        CustomerWithState customer = controller.getCustomer();
        customer.setUpdateState(UpdateState.NEW);
        customerList.add(customer);
        lwCustomers.getSelectionModel().select(customer);
        lwCustomers.scrollTo(customer);
        lwCustomers.requestFocus();
        isDirty.set(true);
    }

    @FXML
    private void onEditCustomerAction() throws IOException {

        Stage stage = getCustomerPopupWindow();
        stage.setTitle("Edit Customer");
        CustomerEditController controller = (CustomerEditController) stage.getUserData();

        CustomerWithState customer = lwCustomers.getSelectionModel()
                .getSelectedItem();
        controller.populateFields(customer);

        stage.showAndWait();
        
        if (controller.isOkayProperty().get()) {
            customer = controller.getCustomer();
            if (customer.getUpdateState() != UpdateState.NEW) {
                customer.setUpdateState(UpdateState.UPDATED);
            }
            isDirty.set(true);
        }
        
        lwCustomers.requestFocus();

    }

    @FXML
    private void onDeleteCustomerAction() {
        CustomerWithState customer = lwCustomers.getSelectionModel()
                .getSelectedItem();
        String name = customer.getName();

        String message = "Are you sure that you want to delete the customer '"
                + name + "'?";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Sure to delete?");
        alert.initOwner(MainWindow);
         Global.styleAlertDialog(alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            ButtonType response = result.get();
            if (response == ButtonType.YES) {
                if (deletedCustomers == null) {
                    deletedCustomers = new ArrayList<>();
                }
                deletedCustomers.add(customer);
                customerList.remove(customer);
                isDirty.set(true);
            }
        }
    }

    private Stage getCustomerPopupWindow() throws IOException {

        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass()
                .getResource("/com/dt/view/CustomerEdit.fxml"));
        final BorderPane root = loader.<BorderPane>load();

        final Scene scene = new Scene(root);
        final CustomerEditController controller
                = loader.<CustomerEditController>getController();
        controller.customers = lwCustomers.getItems();

        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(MainWindow);
        stage.setUserData(controller);
         stage.getIcons().add(new Image("/resources/images/billing_32.png"));
        stage.setScene(scene);
        stage.setOnShown(event -> {
            controller.putFocusOnFirstNode();
        });

        return stage;
    }

    private void onCustomersListChanged(ListChangeListener.Change<? extends CustomerWithState> c) {
        if (c.next()) {
            if (c.wasUpdated()) {
//                CustomerWithState updatedCustomer = c.getList().get(c.getFrom());
//                populateFields(updatedCustomer);
               bindUIToModel();
            }
        }
    }

    @FXML
    private void onSaveAction() {
       if (!saveData(true)) {
           return;
       }
       
       final FadeTransition transition = new FadeTransition(Duration.seconds(3.0),
                checkImage);
        transition.setFromValue(1.0);
        transition.setToValue(0.0);
        transition.setOnFinished((ActionEvent event1) -> {
            lwCustomers.requestFocus();
        });
        
        try {
            transition.play();
        } catch (Exception e) {
           // do nothing
        }
    }

    @FXML
    private void onCloseAction() {
        if (shouldClose()) {
             closeTab();
        }
    }

    private boolean saveData(boolean toHouseKeep) {

        List<CustomerWithState> addedCustomers = new ArrayList<>();
        List<CustomerWithState> updatedCustomers = new ArrayList<>();

        UpdateState state = null;
        for (CustomerWithState c : lwCustomers.getItems()) {
            state = c.getUpdateState();
            if (state != null) {
                switch (c.getUpdateState()) {
                    case NEW:
                        addedCustomers.add(c);
                        break;
                    case UPDATED:
                        updatedCustomers.add(c);
                        break;
                }
            }

        }

        List<? extends Customer> deleted = (deletedCustomers != null) ? deletedCustomers
                : Collections.<CustomerWithState>emptyList();
        int[] autoIDs = null;

        try {
            autoIDs = CustomersPersistence.saveCustomers(addedCustomers, updatedCustomers,
                    deleted);
        } catch (Exception e) {
            String message = Utility.getDataSaveErrorText();
            Utility.beep();
            Alert alert = Utility.getErrorAlert("Error Occurred", "Error in Saving Data",
                    message, MainWindow);
            alert.showAndWait();
            return false;
        }

        if (toHouseKeep) {
            doHouseKeeping(autoIDs, addedCustomers, updatedCustomers);
        }

        isDirty.set(false);
        return true;
    }

    private void closeTab() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(tab); //close the current tab
    }

    private void doHouseKeeping(int[] autoIDs, List<CustomerWithState> newCustomers,
            List<CustomerWithState> updatedCustomers) {

        if (autoIDs != null) {
            int i = 0;
            for (CustomerWithState c : newCustomers) {
                c.setId(autoIDs[i++]);
                c.setUpdateState(UpdateState.NONE);
            }
        }

        if (updatedCustomers != null) {
            for (CustomerWithState c : updatedCustomers) {
                c.setUpdateState(UpdateState.NONE);
            }
        }

        if (deletedCustomers != null) {
            deletedCustomers.clear();
        }
    }
    
     @Override
    public void setMainWindow(Stage stage) {
        MainWindow = stage;
    }

    @Override
    public void setTabPane(TabPane pane) {
        this.tabPane = pane;
    }
    
    private ButtonType shouldSaveUnsavedData() {
            
        final String promptMessage = "There are unsaved changes in the Customers list.\n"
                    + "Save the changes before closing the tab?";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, promptMessage,
         ButtonType.YES, ButtonType.NO, ButtonType.CANCEL );
        alert.setHeaderText("Unsaved Customers List. Save now?");
        alert.setTitle("Unsaved Customers");
        alert.initOwner(MainWindow);
         Global.styleAlertDialog(alert);
         
        Optional<ButtonType> result = alert.showAndWait();
        if (! result.isPresent()) {
            return ButtonType.CANCEL;
        }

        return result.get();
    }
    
    private void bindUIToModel() {
      
       final ReadOnlyObjectProperty<CustomerWithState> selectedItemProperty = 
                lwCustomers.getSelectionModel().selectedItemProperty();
        
       tfName.textProperty().unbind();
        tfName.textProperty().bind(new StringBinding() {
            {
                super.bind(selectedItemProperty);
            }
            @Override
            protected String computeValue() {
                CustomerWithState customer = selectedItemProperty.get();
                if (customer == null) {
                    return "";
                }
                
                return customer.getName();
            }
        });

        tfCity.textProperty().unbind();
        tfCity.textProperty().bind(new StringBinding() {

            {
                super.bind(selectedItemProperty);
            }
            
           @Override
           protected String computeValue() {
               Customer customer = selectedItemProperty.get();
               if (customer == null) {
                   return "";
               } 
               String city = customer.getCity();
               return (city == null ? "" : city);
           }
       });
        
       tfPhoneNumbers.textProperty().unbind();
        tfPhoneNumbers.textProperty().bind(new StringBinding() {

            {
                super.bind(selectedItemProperty);
            }
            
           @Override
           protected String computeValue() {
               CustomerWithState customer = selectedItemProperty.get();
               if (customer == null) {
                   return "";
               }
              String phoneNumbers = customer.getPhoneNumbers();
              return (phoneNumbers == null ? "" : phoneNumbers);
           }
       });
        
        tfOpeningBalance.textProperty().unbind();
       tfOpeningBalance.textProperty().bind(new StringBinding() {

           {
               super.bind(selectedItemProperty);
           }
           
           @Override
           protected String computeValue() {
               CustomerWithState customer  = selectedItemProperty.get();
               if (customer == null) {
                   return "";
               }
               BigDecimal balance = customer.getOpeningBalance();
                if (balance == null) {
                    return "";
                } else if (balance.signum() == 0) { 
                        return "0.00";
                } else {
                    String text = IndianCurrencyFormatting.applyFormatting(balance);
                    if (customer.getBalanceType() != null) {
                        text += " " + customer.getBalanceType().toString();
                    }
                   return text;
                }
           }
       });
        
    }

}

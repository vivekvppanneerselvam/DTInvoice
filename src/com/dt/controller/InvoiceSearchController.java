/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;
import com.dt.application.Global;
import com.dt.dao.*;
import com.dt.utils.*;
import com.dt.dto.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import com.dt.dto.Customer;
import com.dt.dto.InvoiceSearchCriteria;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.collections.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.util.Callback;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import java.util.logging.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;



/**
 * @author vivek
 *
 */
public class InvoiceSearchController implements TabContent, EntityEditedListener {

    private final static Logger logger = 
            Logger.getLogger(InvoiceSearchController.class.getName());
    
    @FXML
    private Label criteriaSelectionError;
    @FXML
    private RadioButton invoiceNumberButton;
    @FXML
    private HBox invoiceNumberPanel;
    @FXML
    private RadioButton otherCriteriaButton;
    @FXML
    private VBox otherCriteriaPanel;
    @FXML
    private CheckBox invoiceDateButton;
    @FXML
    private HBox invoiceDatePanel;
    @FXML
    private VBox customersPanel;
    @FXML
    private CheckBox invoiceAmountButton;
    @FXML
    private HBox invoiceAmountPanel;
    @FXML
    private ToggleGroup searchCriteriaGroup;
    @FXML
    private Button searchInvoiceButton;
    @FXML
    private Label invoiceNumberError;
    @FXML
    private Label startDateError;
    @FXML
    private Label endDateError;
    @FXML
    private Label customersError;
    @FXML
    private Label startAmountError;
    @FXML
    private Label endAmountError;
    @FXML
    private TextField invoiceNumberField;
    @FXML
    private CheckListView<Customer> customersListView;
    @FXML
    private DatePicker startDateField;
    @FXML
    private DatePicker endDateField;
    @FXML
    private TextField startAmountField;
    @FXML
    private TextField endAmountField;
    @FXML
    private Label startAmountLabel;
    @FXML
    private Label endAmountLabel;
    @FXML
    private TableView<InvoiceSearchResult> tableView;
    @FXML
    private TableColumn<InvoiceSearchResult, Customer> customerColumn;
    @FXML
    private TableColumn<InvoiceSearchResult, LocalDate> invoiceDateColumn;
    @FXML
    private TableColumn<InvoiceSearchResult, String> invoiceNumberColumn;
     @FXML
    private TableColumn<InvoiceSearchResult, BigDecimal> invoiceAmountColumn;
    @FXML
    private TitledPane invoiceSearchResultPanel;
    @FXML
    private TitledPane invoiceSearchCriteriaPanel;
    @FXML
    private CheckBox selectAllCustomersButton;
    @FXML
    private RadioButton creditInvoiceButton;
    @FXML
    private VBox invoiceTypePanel;
    @FXML
    private CheckBox invoiceTypeButton;
    @FXML
    private RadioButton cashInvoiceButton;
    @FXML
    private Label invoiceTypeError;
    @FXML
    private Label invoiceTotalLabel;
    @FXML
    private Button deleteInvoiceButton;
    @FXML
    private Button editInvoiceButton;
    @FXML
    private Button printInvoiceButton;
    @FXML
    private TextField tfSearchCustomer;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final IntegerProperty matchingInvoicesCount = new SimpleIntegerProperty(0);
    private Stage mainWindow = null;
    private Label[] errorLabels = null;
    private TabPane tabPane = null;
    private UserPreferences userPreferences = null;
    private final LocalDate minDate = Global.getActiveFinancialYear()
            .getStartDate();
    private final LocalDate maxDate = Utility.minDate(Global.getActiveFinancialYear()
            .getEndDate(), LocalDate.now());
    private ObservableList<Customer> customersList = null;
    private FilteredList<Customer> filteredCustomerList = null;
    
    @Override
    public boolean shouldClose() {
        return true;
    }

    @Override
    public void putFocusOnNode() {
        invoiceNumberButton.requestFocus();
    }

    @Override
    public boolean loadData() {
        return loadCustomers();
    }

    @Override
    public void setMainWindow(Stage stage) {
        mainWindow = stage;
    }
    
    
    public void initialize() {
       
        invoiceNumberPanel.managedProperty().bind(invoiceNumberPanel.visibleProperty());
        invoiceNumberPanel.visibleProperty().bind(invoiceNumberButton.selectedProperty());
        
        invoiceDatePanel.managedProperty().bind(invoiceDatePanel.visibleProperty());
        invoiceDatePanel.visibleProperty().bind(invoiceDateButton.selectedProperty());
        
        otherCriteriaPanel.managedProperty().bind(otherCriteriaPanel.visibleProperty());
        otherCriteriaPanel.visibleProperty().bind(otherCriteriaButton.selectedProperty());
        
        invoiceTypePanel.managedProperty().bind(invoiceTypePanel.visibleProperty());
        invoiceTypePanel.visibleProperty().bind(invoiceTypeButton.selectedProperty());
        
        customersPanel.managedProperty().bind(customersPanel.visibleProperty());
        customersPanel.visibleProperty().bind(creditInvoiceButton.selectedProperty());
        
       invoiceAmountPanel.managedProperty().bind(invoiceAmountPanel.visibleProperty());
       invoiceAmountPanel.visibleProperty().bind(invoiceAmountButton.selectedProperty());
        
       searchInvoiceButton.disableProperty()
               .bind(searchCriteriaGroup.selectedToggleProperty().isNull());
       
       errorLabels = new Label[] {criteriaSelectionError, invoiceNumberError, startDateError, endDateError,
            startAmountError, endAmountError, customersError, invoiceTypeError};
       for(Label label : errorLabels) {
            label.managedProperty().bind(label.visibleProperty());
            label.visibleProperty().bind(label.textProperty().length().greaterThan(0));
       }
       
       startDateField.setValue(LocalDate.now());
       startDateField.setConverter(Utility.getDateStringConverter());
       startDateField.setDayCellFactory(this::getDateCell);
        
       endDateField.setValue(LocalDate.now());
       endDateField.setConverter(Utility.getDateStringConverter());
       endDateField.setDayCellFactory(this::getDateCell);
       
       //suffix Indian Rupee Symbol to the amount label
       startAmountLabel.setText(startAmountLabel.getText() +  "\u20B9");
       endAmountLabel.setText(endAmountLabel.getText() + "\u20B9");
       
       selectAllCustomersButton.setTooltip(new Tooltip("Checking this checkbox will select all customers."));
       
       editInvoiceButton.setMaxWidth(Double.MAX_VALUE);
       editInvoiceButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
       deleteInvoiceButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
      printInvoiceButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
      
      invoiceSearchResultPanel.managedProperty().bind(invoiceSearchResultPanel.visibleProperty());
      invoiceSearchResultPanel.visibleProperty().bind(matchingInvoicesCount.greaterThan(0));
      
       tfSearchCustomer.textProperty().addListener((
                ObservableValue<? extends String> observable,
                String oldValue, String newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                filteredCustomerList.setPredicate(null); //show all customers
            } else {
                filteredCustomerList.setPredicate((Customer t) -> 
                        t.getName().toLowerCase().contains(newValue.toLowerCase())
                );
            }
        });
      
      setTableCellFactories();
    }
    
    @FXML
    private void onSelectAllCustomersAction(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) source;
            if (checkBox.isSelected()) {
                customersListView.getCheckModel().checkAll();
                checkBox.getTooltip().setText("Unchecking this checkbox will deselect all customers");
            } else {
                customersListView.getCheckModel().clearChecks();
                checkBox.getTooltip().setText("Checking this checkbox will select all customers");
            }
        }
        
    }
    
    @FXML
    private void onInvoiceSearchAction(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        matchingInvoicesCount.set(0);
        InvoiceSearchCriteria criteria = getCriteria();
        List<InvoiceSearchResult> results  = null;
        
        try {
           results = InvoiceSearch.searchInvoice(criteria);
        } catch (Exception e) {
            Utility.beep();
            String message = "An Error occurred whilst searching database for the matching invoices!";
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error in searching for invoices");
            alert.initOwner(mainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return;
        }
        
        tableView.setItems(null);
        final ObservableList<InvoiceSearchResult> observableResults = 
         FXCollections.<InvoiceSearchResult>observableList(results, (InvoiceSearchResult result) -> {
             Observable[] observables = new Observable[] {
                 result.customerProperty(), result.invoiceDateProperty(),
                 result.invoiceAmountProperty()
             };
             return observables;
        });
             
        tableView.setItems(observableResults);
        int matchCount = results.size();
        
        if (matchCount > 0) {
            invoiceTotalLabel.setText(
                    String.format("Total of %d invoice(s) is \u20b9 %s", 
                    matchCount, 
                    IndianCurrencyFormatting.applyFormatting(getTotalAmount(results))));
            invoiceSearchCriteriaPanel.setExpanded(false);
            matchingInvoicesCount.set(matchCount);
            invoiceSearchResultPanel.setExpanded(true);
            tableView.getSelectionModel().selectFirst();
            tableView.scrollTo(0);
            tableView.requestFocus();
        } else {
            Utility.beep();
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                    "No invoice matched your search criteria.", ButtonType.OK);
            alert.setTitle("No Match Found");
            alert.setHeaderText("No matching invoice found");
            alert.initOwner(mainWindow);
            Global.styleAlertDialog(alert);
            alert.showAndWait();
        }
        
    }
    
    private InvoiceSearchCriteria getCriteria() {
        InvoiceSearchCriteria criteria = new InvoiceSearchCriteria();
        
        if (invoiceNumberButton.isSelected()) {
            criteria.setInvoiceNumber(invoiceNumberField.getText().trim());
        } else {
            if (invoiceDateButton.isSelected()) {
                criteria.setStartDate(startDateField.getValue());
                criteria.setEndDate(endDateField.getValue());
            }
            
            if (invoiceTypeButton.isSelected()) {
                if (cashInvoiceButton.isSelected()) {
                    criteria.setCashInvoice(true);
                } else {
                    if (creditInvoiceButton.isSelected()) {
                        List<Customer> customers = 
                                customersListView.getCheckModel().getCheckedItems();
                        criteria.setCustomers(customers);
                    }
                }
            }
            
            if (invoiceAmountButton.isSelected()) {
                BigDecimal amount = new BigDecimal(startAmountField.getText().trim());
                amount = amount.setScale(2, RoundingMode.HALF_UP);
                criteria.setStartAmount(amount);
                
                amount = new BigDecimal(endAmountField.getText().trim());
                amount = amount.setScale(2, RoundingMode.HALF_UP);
                criteria.setEndAmount(amount);
            }
        }
        
        return criteria;
    }
    
    private boolean validateInput() {
        clearErrorLabels();
        
      if (invoiceNumberButton.isSelected()) {
          return validateInvoiceNumber();
      }
      
      if (!(invoiceDateButton.isSelected() || invoiceAmountButton.isSelected() || 
              invoiceTypeButton.isSelected())) {
          criteriaSelectionError.setText("No Criterion selected!");
          return false;
      }
      
      boolean valid = true;
      
      if (invoiceTypeButton.isSelected()) {
          if (! (creditInvoiceButton.isSelected() || cashInvoiceButton.isSelected())) {
              invoiceTypeError.setText("Cash or credit invoice not selected!");
              valid = false;
          }
          
          if (creditInvoiceButton.isSelected() &&
              customersListView.checkModelProperty().getValue().isEmpty()) {
            customersError.setText("No Customer selected!");
            valid = false;
          }
          
      }
      
      if (invoiceDateButton.isSelected() && !validateInvoiceDate()) {
          valid = false;
      }
      
      if (invoiceAmountButton.isSelected() && !validateInvoiceAmount()) {
          valid = false;
      }
      
        return valid;
    }
    
    private boolean validateInvoiceDate() {
        boolean valid =  true;
        
       LocalDate startDate = startDateField.getValue();
       if (startDate == null) {
           startDateError.setText("Start Date not specified!");
           valid = false;
       } else if (startDate.isAfter(maxDate)) {
           startDateError.setText("Start Date can't be later than " +
                   maxDate.format(dateFormatter));
           valid = false;
       } else if (startDate.isBefore(minDate)) {
           startDateError.setText("Start Date can't be before than " + 
                   minDate.format(dateFormatter));
           valid = false;
       }
       
       LocalDate endDate = endDateField.getValue();
       if (endDate == null) {
           endDateError.setText("End Date not specified!");
           valid = false;
       } else if (endDate.isAfter(maxDate)) {
           endDateError.setText("End Date can't be later than " +
                   maxDate.format(dateFormatter));
           valid = false;
       } else if (endDate.isBefore(minDate)) {
           endDateError.setText("End Date can't be before than " + 
                   minDate.format(dateFormatter));
           valid = false;
       }
       
       if (valid && startDate.isAfter(endDate)) {
           startDateError.setText("Start Date can't be later than the end date!");
           valid = false;
       }
       
       return valid;
       
    }
    
    private boolean validateInvoiceAmount() {
        boolean valid = true;
        
        BigDecimal startAmount = null;
        String startAmountString = startAmountField.getText().trim();
        
        if (startAmountString.isEmpty()) {
            startAmountError.setText("Start Amount not specified!");
            valid = false;
        } else {
            try {
                startAmount = new BigDecimal(startAmountString);
            } catch (NumberFormatException e) {
                startAmountError.setText("Not a valid amount!");
                valid = false;
            }
            
            if (startAmount != null && startAmount.signum() == -1) {
                startAmountError.setText("Start Amount must be a positive number!");
                valid = false;
            }
        }
        
        BigDecimal endAmount = null;
        String endAmountString = endAmountField.getText().trim();
        
        if (endAmountString.isEmpty()) {
            endAmountError.setText("End Amount not specified!");
            valid = false;
        } else {
            try {
                endAmount = new BigDecimal(endAmountString);
            } catch (NumberFormatException e) {
                endAmountError.setText("Not a valid amount!");
                valid = false;
            }
            
            if (endAmount != null && endAmount.signum() == -1) {
                endAmountError.setText("End Amount must be a positive number!");
                valid = false;
            }
        }
        
        if (valid  && startAmount.compareTo(endAmount) == 1) {
            startAmountError.setText("Start amount can't be greater than the end amount!");
            valid = false;
        }
        
        return valid;
    }
    
    private boolean validateInvoiceNumber() {
        boolean valid = true;
         
        String numberString = invoiceNumberField.getText().trim();
        if (numberString.isEmpty()) {
            invoiceNumberError.setText("Invoice Number not specified!");
            valid = false;
        } else {
            int number = -1;
            try {
                number = Integer.parseUnsignedInt(numberString);
            } catch (NumberFormatException e) {
                invoiceNumberError.setText("Invalid Invoice Number!");
                valid = false;
            }

            if (number == 0) {
                invoiceNumberError.setText("Invoice Number can't be zero!");
                valid = false;
            }
        }
       
        return valid;
    }
    
    private void clearErrorLabels() {
        for (Label label : errorLabels) {
            label.setText("");
        }
    }
    
private boolean loadCustomers() {
       List<Customer> customers = null;
       
       try {
           customers = CustomersPersistence.getCustomers();
       } catch (Exception e) {
            String message = Utility.getDataFetchErrorText();
            Alert alert = Utility.getErrorAlert("Error Occurred",
                    "Error in Fetching Customers !", message, mainWindow);
            Utility.beep();
            alert.showAndWait();
            return false;
       }
       
       customersList = 
               FXCollections.<Customer>observableList(customers);
       filteredCustomerList =  new FilteredList<>(customersList, null);
       customersListView.setItems(filteredCustomerList);
       
       if (customers.isEmpty()) {
           selectAllCustomersButton.setDisable(true);
       }
       
       return true;
   }

  private DateCell getDateCell(DatePicker datePicker) {
     return Utility.getDateCell(datePicker, minDate, maxDate);
 }
  
    @FXML
    private void onInvoiceDeleteAction(ActionEvent event) {
        //get the selected invoice
      InvoiceSearchResult searchResult =  tableView.getSelectionModel().getSelectedItem();
      String invoiceNumber = searchResult.getInvoiceNumber();
      
       String promptMessage = "Are you sure to delete the invoice numbered " +
               invoiceNumber + " ?";
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Sure to delete the selected invoice ?");
            alert.setTitle("Confirm Invoice Deletion");
            alert.setContentText(promptMessage);
            alert.getButtonTypes().clear();
            alert.getButtonTypes()
                    .addAll(ButtonType.YES, ButtonType.NO);
            alert.initOwner(mainWindow);
             Global.styleAlertDialog(alert);

            Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent()) { //user dismissed the dialog without selecting any option
                return;
            }

            ButtonType response = result.get();
            if (response == ButtonType.NO) {
                return;
            }
            
            try {
                InvoiceSearch.deleteInvoice(Integer.valueOf(invoiceNumber));
            } catch (Exception e) {
                Utility.beep();
                String message = "Could not delete the invoice due to an error!";
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Occurred");
                alert.setHeaderText("Error in deleting the invoice");
                alert.setContentText(message);
                alert.initOwner(mainWindow);
                 Global.styleAlertDialog(alert);
                alert.showAndWait();
                return;
            }
            
            tableView.getItems().remove(searchResult);
            
    }
    
    @FXML
    private void onInvoiceEditAction(ActionEvent event) {
      //get the selected invoice
      InvoiceSearchResult searchResult =  tableView.getSelectionModel().getSelectedItem();
      String invoiceNumber = searchResult.getInvoiceNumber();
      
       String path =  "/com/dt/view/Invoice.fxml";
       FXMLLoader loader = new FXMLLoader();
        URL resource = this.getClass().getResource(path);
        loader.setLocation(resource);
        Parent rootPane = null;
        
        try {
            rootPane = loader.load();
        } catch (Exception e) {
            logger.logp(Level.SEVERE, InvoiceSearchController.class.getName(),
                  "onInvoiceEditAction", "Error in loading the invoice fxml file", e);
           Alert alert = Utility.getErrorAlert("Error Occurred", 
                   "Error Occurred in opening the invoice",
                   "Could not open the invoice due to an error.", mainWindow);
           Utility.beep();
           alert.showAndWait();
           return;
        }
        
        InvoiceController controller = (InvoiceController) loader.getController();
        controller.setMainWindow(mainWindow);
        if (!controller.loadData()) {
            return;
        }
        
        if (!controller.loadInvoice(invoiceNumber)) {
            return;
        }
        
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainWindow);
        
        stage.setTitle("Editing Invoice #" + invoiceNumber);
        stage.getIcons().add(new Image("/resources/images/billing_32.png"));
        controller.setMainWindow(stage);
        controller.addEntityEditedEventListener(this);
       
        Global.setStageDefaultDimensions(stage);
        
        Scene scene = new Scene(rootPane);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    private BigDecimal getInvoiceTotal(Invoice invoice) {
       List<InvoiceItem> invoiceItems =  invoice.getInvoiceItems();
       BigDecimal total = BigDecimal.ZERO;
       BigDecimal amount = null;
       
       for(InvoiceItem invoiceItem : invoiceItems) {
           amount = invoiceItem.getQuantity().multiply(invoiceItem.getRate())
                   .setScale(2, RoundingMode.HALF_UP);
           total = total.add(amount);
       }
       
       amount = invoice.getAdditionalCharge();
       if (amount != null) {
           total = total.add(amount);
       }
       
       amount = invoice.getDiscount();
       if (amount != null) {
           total = total.subtract(amount);
       }
       
       return total;
               
    }
   
    @Override
    public void setTabPane(TabPane pane) {
        this.tabPane = pane;
    }

    @Override
    public void onEntityEdited(EntityEditedEvent event) {
        Object editedEntity = event.getEntityEdited();
        Invoice invoice = (Invoice) editedEntity;
        
         InvoiceSearchResult searchResult =  tableView.getSelectionModel()
                 .getSelectedItem();
         searchResult.setInvoiceDate(invoice.getInvoiceDate());
         
         if (invoice.getIsCashInvoice()) {
             if (searchResult.getCustomer().getId() != 0) {
                 Customer customer = new Customer();
                 customer.setName("CASH");
                 searchResult.setCustomer(customer);
             } 
         } else {
             if (searchResult.getCustomer().getId() == 0) {
                 searchResult.setCustomer(invoice.getCustomer());
             }
         }
         
         searchResult.setInvoiceAmount(getInvoiceTotal(invoice));
    }
    
    @FXML
    private void onInvoicePrintAction(ActionEvent event) {
      final  InvoiceSearchResult selectedItem = tableView.getSelectionModel()
                .getSelectedItem();
       final Integer invoiceNumber =  Integer.valueOf(selectedItem.getInvoiceNumber());
       
       Invoice invoice = null;
        try {
            invoice = InvoicePersistence.getInvoice(invoiceNumber);
        } catch (Exception e) {
           String message = "An error occurred in fetching the invoice details" 
                   + " from the database.";
           Alert alert = Utility.getErrorAlert("Error Occurred", "Error in fetching data",
                   message, mainWindow);
           Utility.beep();
           alert.showAndWait();
           return;
        }
        
        if (userPreferences == null) {
            userPreferences = Global.getUserPreferences();
        }
        
        printInvoice(invoice);
       
    }
    
    private void printInvoice(Invoice invoice) {
       
        Map map = null;
        
        try {
            map = getReportParameters(invoice);            
        } catch (Exception e) {
           Utility.beep();
            String message = "An error occurred whilst collecting data to print the invoice!";
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error in retrieving data for printing the invoice");
            alert.initOwner(mainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return;
        }
        
        final String resourcePath = "/resources/reports/fxbilling_invoice.jasper";
        JasperPrint jasperPrint = null;
        
         try (InputStream reportStream = this.getClass().getResourceAsStream(resourcePath) )
         {
            jasperPrint = JasperFillManager.fillReport(reportStream, map, 
               new JRBeanCollectionDataSource(invoice.getInvoiceItems(), true));
         }
         catch (Exception e)
         {
             logger.logp(Level.SEVERE, InvoiceSearchController.class.getName(), 
                    "printInvoice", "Error in fillReport function", e);
            Utility.beep();
            String message = "An error occurred whilst preparing to print the invoice!";
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error in printing the invoice");
            alert.initOwner(mainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
             return;
         }

         if (userPreferences.getShowPrintPreview()) {
             JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Invoice");
            jasperViewer.setVisible(true);
         } else {
             boolean showPrintDialog = userPreferences.getShowPrintDialog();
            try {
               JasperPrintManager.printReport(jasperPrint, showPrintDialog); 
           } catch (Exception ex) {
              logger.logp(Level.SEVERE, InvoiceSearchController.class.getName(), 
                    "printInvoice", "Error in printReport function call", ex);
                Utility.beep();
                String message = "An error occurred whilst printing the invoice!";
                Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
                alert.setTitle("Invoice Printing Error");
                alert.setHeaderText("Error in printing the invoice");
                alert.initOwner(mainWindow);
                 Global.styleAlertDialog(alert);
                alert.showAndWait();
           }
            
         }
           
    }
    
    private Map getReportParameters(Invoice invoice)  throws Exception{
       HashMap map = new HashMap(12); //represents report parameters
       System.out.println("hehehe"+"test{" +
               "a=" + invoice.getInvoiceId()+"" +
               ", b=" + invoice.getInvoiceDate() + "" +
               ", c=" + invoice.getIsCashInvoice()+ "" +
               ", d=" + invoice.getBalance() + "" +
               ", e=" + invoice.getDiscount() + "" +
               ", f=" + invoice.getPaid() + "" +
               ", g=" + invoice.getPrevDueAmount() + "" +
               ", h=" + invoice.getTotalDueAmount() + "" +
               
               "}"
               );
        map.put("invoiceNumber",new Integer(invoice.getInvoiceId()));
        map.put("invoiceDate", invoice.getInvoiceDate());
        
        if (!invoice.getIsCashInvoice()) {
            Customer customer = invoice.getCustomer();
            map.put("customerName", customer.getName());
            
             String city = invoice.getCustomer().getCity();
                if (!(city == null || city.isEmpty())) {
                    map.put("customerCity", city);
                }
        }
        
        BigDecimal amount = invoice.getAdditionalCharge();
        if (amount != null) {
            map.put("additionalCharge", amount);
        }
        
        amount = invoice.getDiscount();
        if (amount != null) {
            map.put("discount", amount);
        }
        
        amount = invoice.getPaid();
		if(amount != null) {
			map.put("paid", amount);
		}
		
		amount = invoice.getPrevDueAmount();
		if(amount != null) {
			map.put("prevDueAmount", amount);
		}
		
		amount = invoice.getTotalDueAmount();
		if(amount != null) {
			map.put("totalDueAmount", amount);
		}
		
		amount = invoice.getBalance();
		if(amount != null) {
			map.put("balance", amount);
		}else {
			map.put("balance", BigDecimal.ZERO);
		}
		
		
		
        
        FirmDetails firmDetails = FirmDetailsPersistence.getData();
        if (firmDetails != null) {
            map.put("firmName", firmDetails.getFirmName());
            map.put("firmAddress", firmDetails.getAddress());
            String str = firmDetails.getPhoneNumbers();
            if (str != null) {
                map.put("firmPhoneNumbers", str);
            }
            
            str = firmDetails.getEmailAddress();
            if (str != null) {
                map.put("firmEmailAddress", str);
            }
            
            byte[] logoBytes = firmDetails.getLogo();
            if (logoBytes != null) {
                ByteArrayInputStream byteArrayStream = 
                        new ByteArrayInputStream(logoBytes);
                map.put("firmLogo", byteArrayStream);
            }
        }       
        
        
        return map;
    }
    
    private void setTableCellFactories() {
         //define a custom cell factory so as to format the data in the cell
       invoiceDateColumn.setCellFactory(new Callback<TableColumn<InvoiceSearchResult, LocalDate>, 
               TableCell<InvoiceSearchResult, LocalDate>>() {

            @Override
            public TableCell<InvoiceSearchResult, LocalDate> call(TableColumn<InvoiceSearchResult, LocalDate> param) {
               TableCell<InvoiceSearchResult, LocalDate> cell = new TableCell<InvoiceSearchResult, LocalDate>() {

                   @Override
                   protected void updateItem(LocalDate item, boolean empty) {
                       super.updateItem(item, empty);
                       this.setText(null); //cleanup job as the cells may be reused
                       if (!empty) {
                           String formattedDate = dateFormatter.format(item);
                           this.setText(formattedDate);
                           this.setTextAlignment(TextAlignment.CENTER);
                       }
                   }
                   
               };
               cell.setAlignment(Pos.CENTER);
               return cell;
            }
           
        });
       
        invoiceNumberColumn.setCellFactory(new Callback<TableColumn<InvoiceSearchResult, String>, 
               TableCell<InvoiceSearchResult, String>>() {

            @Override
            public TableCell<InvoiceSearchResult, String> call(TableColumn<InvoiceSearchResult, String> param) {
               TableCell<InvoiceSearchResult, String> cell = new TableCell<InvoiceSearchResult, String>() {

                   @Override
                   protected void updateItem(String item, boolean empty) {
                       super.updateItem(item, empty);
                       this.setText(null); //cleanup job as the cells may be reused
                       if (!empty) {
                           this.setText(item);
                           this.setTextAlignment(TextAlignment.CENTER);
                       }
                   }
                   
               };
               cell.setAlignment(Pos.CENTER);
               return cell;
            }
           
        });
        
        invoiceAmountColumn.setCellFactory(new Callback<TableColumn<InvoiceSearchResult, BigDecimal>, 
               TableCell<InvoiceSearchResult, BigDecimal>>() {

            @Override
            public TableCell<InvoiceSearchResult, BigDecimal> call(TableColumn<InvoiceSearchResult, BigDecimal> param) {
               TableCell<InvoiceSearchResult, BigDecimal> cell = new TableCell<InvoiceSearchResult, BigDecimal>() {

                   @Override
                   protected void updateItem(BigDecimal item, boolean empty) {
                       super.updateItem(item, empty);
                       this.setText(null); //cleanup job as the cells may be reused
                       if (!empty) {
                           this.setText(IndianCurrencyFormatting.applyFormatting(item));
                       }
                   }
                   
               };
               cell.getStyleClass().add("amount-cell");
               return cell;
            }
           
        });
        
        customerColumn.setCellFactory((TableColumn<InvoiceSearchResult, Customer> param) -> {
           final TableCell<InvoiceSearchResult, Customer> cell = 
                   new TableCell<InvoiceSearchResult, Customer>() {

               @Override
               protected void updateItem(Customer item, boolean empty) {
                   super.updateItem(item, empty); 
                   
                   setText(null);
                   setGraphic(null);
                   
                   if (item != null && !empty) {
                       setText(item.getName());
                   }
               }
               
           };
           cell.getStyleClass().add("name-cell");
           return cell;
       });
        
       
    }
    
    private BigDecimal getTotalAmount(List<InvoiceSearchResult> list) {
         BigDecimal total = BigDecimal.ZERO;
        for(InvoiceSearchResult result : list) {
            total = total.add(result.getInvoiceAmount());
        }
        
        return total;
    }
    
    @FXML
    private void onCloseButtonAction(ActionEvent event) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(tab); //close the current tab
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;

import com.dt.application.Global;
import com.dt.dao.ItemsPersistence;
import com.dt.dto.Item;
import com.dt.dto.ItemWithState;
import com.dt.dto.UpdateState;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;

import com.dt.utils.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Dinesh
 */
public final class ItemsController implements TabContent {

    private Stage MainWindow;
    private List<ItemWithState> deletedItems = null;
    private TabPane tabPane = null;
    private ObservableList<ItemWithState> itemstList = null;
    private FilteredList<ItemWithState> filteredList = null;
    
    @FXML
    private RadioButton rdoNew;
    @FXML
    private RadioButton rdoEdit;
    @FXML
    private TextField tfItem;
    @FXML
    private TextField tfSearchItem;
    @FXML
    private Label lblItemError;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private ListView<ItemWithState> listView;
    @FXML
    private ToggleGroup newOrEditToggle;
     @FXML private ImageView checkImage;
    
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    
    public void initialize() {
        newOrEditToggle.selectedToggleProperty().addListener(this::onToggleChanged);
        
        tfItem.textProperty().addListener(e -> btnAdd.setDisable(false));
        
        rdoEdit.disableProperty().bind(listView.getSelectionModel()
                .selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(listView.getSelectionModel()
                .selectedItemProperty().isNull());
        
        btnAdd.setOnAction(this::onAddButtonAction);
        
        lblItemError.managedProperty().bind(lblItemError.visibleProperty());
        lblItemError.visibleProperty().bind(lblItemError.textProperty()
                .length().greaterThanOrEqualTo(1));
        
        btnSave.disableProperty().bind(isDirty.not());
        
        listView.getSelectionModel().selectedItemProperty()
                .addListener(this::onSelectedRowChanged);
        setListViewCellFactory();
        
         tfSearchItem.textProperty().addListener((
                ObservableValue<? extends String> observable,
                String oldValue, String newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                filteredList.setPredicate(null);
            } else {
                filteredList.setPredicate((ItemWithState t) -> 
                        t.getItemName().toLowerCase().contains(newValue.toLowerCase())
                );
            }
        });
         
          checkImage.managedProperty().bind(checkImage.visibleProperty());
        checkImage.visibleProperty().bind(checkImage.opacityProperty()
                .greaterThan(0.0));
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
            tfItem.requestFocus();
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
            tfItem.requestFocus();
    }

    @Override
    public boolean loadData() {
        
        List<Item> list = null;
        try {
            list = ItemsPersistence.getItems();
        } catch (Exception e) {
            String message = Utility.getDataFetchErrorText();
            Alert alert = Utility.getErrorAlert("Error Occurred",
                    "Error in Loading Items List", message, MainWindow);
            Utility.beep();
            alert.showAndWait();
            return false;
        }
        
       final List<ItemWithState> items = ItemWithState.fromItems(list);
       itemstList = FXCollections.<ItemWithState>observableList(items,
                        (ItemWithState item) -> {
                            Observable[] array = new Observable[]{
                                item.itemNameProperty()
                            };

                            return array;
                        });

       filteredList = new FilteredList(itemstList, null);
        listView.setItems(filteredList);
        return true;
    }

    @Override
    public void setMainWindow(Stage stage) {
        MainWindow = stage;
    }
    
    private void onToggleChanged(ObservableValue<? extends Toggle> observable, 
            Toggle oldValue, Toggle newValue) {
        
        tfItem.clear();
        
        String text = (String) newValue.getUserData();
        if (text.equalsIgnoreCase("new")) {
            btnAdd.setText("_Add");
        } else{
            btnAdd.setText("_Update");
           ItemWithState item =  listView.getSelectionModel().getSelectedItem();
           tfItem.clear();
           lblItemError.setText("");
           if (item != null) {
               tfItem.setText(item.getItemName());
           }
        }
        
        btnAdd.setDisable(true);
        Platform.runLater(() -> tfItem.requestFocus());
    }
    
    private void onAddButtonAction(ActionEvent event) {
        if (!validateInput()) {
            Utility.beep();
            return;
        }
        
        String name = tfItem.getText().trim();
        
        if (rdoNew.isSelected()) {
            ItemWithState item = new ItemWithState();
            item.setItemName(name); 
            item.setUpdateState(UpdateState.NEW);
            itemstList.add(item);
            listView.getSelectionModel().select(item);
            listView.scrollTo(item);
            tfItem.clear();
            tfItem.requestFocus();
        } else {
            ItemWithState item = listView.getSelectionModel().getSelectedItem();
            item.setItemName(name);
            if (item.getUpdateState() != UpdateState.NEW) {
                item.setUpdateState(UpdateState.UPDATED);
            }
            listView.requestFocus();
        }
        
        btnAdd.setDisable(true);
        isDirty.set(true);
    }
    
    @FXML
    private void onDeleteItemAction(ActionEvent event) {
       ItemWithState item = listView.getSelectionModel()
                .getSelectedItem();
        String name = item.getItemName();
        String message = "Are you sure that you want to delete the item '"
                + name + "'?";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Sure to delete the selected item ?");
        alert.initOwner(MainWindow);
         Global.styleAlertDialog(alert);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            ButtonType response = result.get();
            if (response == ButtonType.YES) {
                if (deletedItems == null) {
                    deletedItems = new ArrayList<>();
                }
                deletedItems.add(item);
                itemstList.remove(item);
                isDirty.set(true);
                listView.requestFocus();
            }
        }
    }
    
    private boolean validateInput() {
     
        boolean valid = true;
        String text = tfItem.getText().trim();
        
        if (text.isEmpty()) {
            lblItemError.setText("Item name not entered!");
            valid = false;
        } else if (text.length() > 150) {
            lblItemError.setText("Item name length can not exceed 150 characters!");
            valid = false;
        } else if (isDuplicateItemName(text)){
            lblItemError.setText(String.format("Item name '%s' already exists!", text));
            valid = false;
        } else {
            lblItemError.setText("");
            valid = true;
        }
        return valid;
    }
    
    private boolean isDuplicateItemName(String itemName) {
        ItemWithState selected = null;
        if (rdoEdit.isSelected()) {
            selected = listView.getSelectionModel().getSelectedItem();
        }
        boolean duplicate = false;
        for(ItemWithState item : listView.getItems() ) {
            if (itemName.equalsIgnoreCase(item.getItemName())) {
                if (selected != null && !selected.equals(item)) {
                    duplicate = true;
                    break;
                } else if (selected == null) {
                    duplicate = true;
                    break;
                }
            }
        }
        return duplicate;
    }
    
     private boolean saveData(boolean toHouseKeep) {

        List<ItemWithState> addedItems = new ArrayList<>();
        List<ItemWithState> updatedItems = new ArrayList<>();

        UpdateState state = null;
        for (ItemWithState c : listView.getItems()) {
            state = c.getUpdateState();
            if (state != null) {
                switch (c.getUpdateState()) {
                    case NEW:
                        addedItems.add(c);
                        break;
                    case UPDATED:
                        updatedItems.add(c);
                        break;
                }
            }

        }

        List<? extends Item> deleted = (deletedItems != null) ? deletedItems
                : Collections.<ItemWithState>emptyList();
        int[] autoIDs = null;

        try {
            autoIDs = ItemsPersistence.saveItems(addedItems, updatedItems, deleted);
        } catch (Exception e) {
            String message = Utility.getDataSaveErrorText();
            Utility.beep();
            Alert alert = Utility.getErrorAlert("Error Occurred", "Error in Saving Data",
                    message, MainWindow);
            alert.showAndWait();
            return false;
        }

        if (toHouseKeep) {
            doHouseKeeping(autoIDs, addedItems, updatedItems);
        }

        isDirty.set(false);
        return true;
    }
     
     private void doHouseKeeping(int[] autoIDs, List<ItemWithState> newItems,
            List<ItemWithState> updatedItems) {

        if (autoIDs != null) {
            int i = 0;
            for (ItemWithState c : newItems) {
                c.setItemId(autoIDs[i++]);
                c.setUpdateState(UpdateState.NONE);
            }
        }

        if (updatedItems != null) {
            for (ItemWithState c : updatedItems) {
                c.setUpdateState(UpdateState.NONE);
            }
        }

        if (deletedItems != null) {
            deletedItems.clear();
        }
    }
     
     private void closeTab() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(tab); //close the current tab
    }
     
     public void onSelectedRowChanged(ObservableValue<? extends ItemWithState> observable,
                    ItemWithState oldValue, ItemWithState newValue) {
             if (rdoEdit.isSelected()) {
                 tfItem.clear();
                 if (newValue != null) {
                     tfItem.setText(newValue.getItemName());
                 }
             }
    }
     
      @Override
    public void setTabPane(TabPane pane) {
        this.tabPane = pane;
    }
    
    private void setListViewCellFactory() {
        listView.setCellFactory((ListView<ItemWithState> param) -> {
            final ListCell<ItemWithState> cell = new ListCell<ItemWithState> () {

                @Override
                protected void updateItem(ItemWithState item, boolean empty) {
                    super.updateItem(item, empty); 
                    
                    setText(null);
                    setGraphic(null);
                    
                    if (item != null && !empty) {
                        setText(item.toString());
                    }
                }
                
            };
            cell.getStyleClass().add("list-cell");
            return cell;
        });
    }
    
    private ButtonType shouldSaveUnsavedData() {
            final String promptMessage = "There are unsaved changes in the Items list.\n"
                    + "Save the changes before closing the tab?";
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, promptMessage,
             ButtonType.YES, ButtonType.NO, ButtonType.CANCEL );
            alert.setHeaderText("Unsaved Items List. Save now?");
            alert.setTitle("Unsaved Items");
            alert.initOwner(MainWindow);
            Global.styleAlertDialog(alert);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (! result.isPresent()) {
                return ButtonType.CANCEL;
            }
            
            return result.get();
    }
    
}

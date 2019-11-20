/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;

import com.dt.application.Global;
import com.dt.dao.Database;
import com.dt.dto.FinancialYear;
import com.dt.utils.TabContent;
import com.dt.utils.Utility;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;




/**
 * @author vivek
 *
 */
public class DeleteFinancialYearController implements TabContent {

    private Stage mainWindow;
    private TabPane tabPane;
    
    @FXML
    private ListView<FinancialYear> lvwFinancialYears;
     @FXML
    private Button btnDelete;
      @FXML
    private Button btnClose;
    
    public void initialize() {
         btnDelete.disableProperty().bind(lvwFinancialYears.getSelectionModel()
                .selectedItemProperty().isNull());
        
        lvwFinancialYears.setCellFactory((ListView<FinancialYear> param) -> {
            final ListCell<FinancialYear> cell = new ListCell<FinancialYear>() {

                @Override
                protected void updateItem(final FinancialYear item, boolean empty) {
                    super.updateItem(item, empty); 
                    
                    if (empty) {
                        setText(null);
                        setOnMouseClicked(null);
                    } else {
                        setText(item.toString());
                        setOnMouseClicked((MouseEvent event) -> {
                            if (event.getClickCount() == 2) {
                                deleteFinancialYear(item);
                            }
                        });
                    }
                    
                }
                
            };
            cell.getStyleClass().add("list-cell");
            return cell;
        });
        
        lvwFinancialYears.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER && 
                    !lvwFinancialYears.getSelectionModel().isEmpty()) {
                deleteFinancialYear(lvwFinancialYears.getSelectionModel()
                        .getSelectedItem());
            }
         });
    }    

    @Override
    public boolean shouldClose() {
        return true;
    }

    @Override
    public void putFocusOnNode() {
        lvwFinancialYears.requestFocus();
    }

    @Override
    public boolean loadData() {
        List<FinancialYear> years = null;
        
        try {
           years =  Global.getExistingFinancialYears(null);
        } catch (Exception e) {
            String message = "An error occurred while trying to read the " +
                    "existing financial years from the disk.";
            Utility.beep();
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error reading financial years from the disk");
            alert.initOwner(mainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
      
        final ObservableList<FinancialYear> observableList =
                FXCollections.<FinancialYear>observableList(years);
        lvwFinancialYears.setItems(observableList);
         if (!observableList.isEmpty()) {
            lvwFinancialYears.getSelectionModel().selectFirst();
        }
        return true;    
    }

    @Override
    public void setMainWindow(Stage stage) {
        mainWindow = stage;
    }

    @Override
    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }
    
    @FXML
    private void onCloseTabAction(ActionEvent event) {
        Tab currentTab =  tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(currentTab);
    }
    
    @FXML
    private void onDeleteFinancialYearAction(ActionEvent event) {
      
        final FinancialYear year = lvwFinancialYears.getSelectionModel()
               .getSelectedItem();
       
        deleteFinancialYear(year);
    }
    
    private boolean deleteYear(final FinancialYear year) {
        if (!Global.deleteFinancialYearFolder(year)) {
           final String message = "An error occurred in deleting the folder(directory) " +
                   "of the selected year." + 
                   "\n\nIt is suggested that you try deleting the year after " +
                   "restarting the application." +
                   "\n\nShould the problem persists, it is advisable to attempt this operation " +
                   "on running the application with Administrative Previliges." + 
                   "\n(Right-Click the application executable file and select the option " +
                   "'Run as Administrator' from the context-menu.)";
           final Alert alert = new Alert(Alert.AlertType.ERROR, message,
                   ButtonType.OK);
           alert.initOwner(mainWindow);
           alert.setTitle("Error in Deleting Folder");
           alert.setHeaderText("Error in deleting selected year's folder!");
           Utility.beep();
            Global.styleAlertDialog(alert);
           alert.showAndWait();
           return false;
       }
        
        return true;
    }
    
    private boolean confirmDeletion(final FinancialYear year) {
       final String message = "Are you sure to delete the financial year " + 
                year.toString() + "  ?";
       final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, 
               ButtonType.YES, ButtonType.NO);
       alert.initOwner(mainWindow);
       alert.setTitle("Confirm Deletion");
       alert.setHeaderText("Confirm to delete the year " + year.toString());
        Global.styleAlertDialog(alert);
        Optional<ButtonType> response = alert.showAndWait();
        if (!response.isPresent()) {
            return false;
        }
        
        return (response.get() == ButtonType.YES ? true : false);
    }
    
    private void deleteFinancialYear(final FinancialYear year) {
        
         if (!confirmDeletion(year)) {
           return;
       }
       
       final FinancialYear activeYear = Global.getActiveFinancialYear();
       
       //if selected year is currently opened
       if (activeYear != null && activeYear.equals(year)) {
           //close all tabs except this tab prior to deleting the year.
           tabPane.getTabs().retainAll(tabPane.getSelectionModel()
                   .getSelectedItem());
           
           //shut down the database of the selected year
           if (!Database.shutDownActiveYearDatabase()) {
               Utility.beep();
               final String message = "An error occurred in shutting down the database " 
                       + "of the selected year." + 
                       "\n\nYou can try deleting the year after restarting the application.";
               final Alert alert = new Alert(Alert.AlertType.ERROR, message, 
                       ButtonType.OK);
               alert.initOwner(mainWindow);
               alert.setTitle("Database Couldn't be Closed");
               alert.setHeaderText("Database of the selected year could not be closed!");
                Global.styleAlertDialog(alert);
               alert.showAndWait();
               return;
           }
       }
       
       if (!deleteYear(year)) {
           return;
       }
       
       lvwFinancialYears.getItems().remove(year);
       
       final String message = "The financial year " + year.toString() + 
                   " was successfully deleted!";
           final Alert alert = new Alert(Alert.AlertType.INFORMATION, message,
                   ButtonType.OK);
           alert.initOwner(mainWindow);
           alert.setTitle("Deletion Successful");
           alert.setHeaderText("Selected Year Successfully Deleted!!");
            Global.styleAlertDialog(alert);
           alert.showAndWait();
       
    }
    
}

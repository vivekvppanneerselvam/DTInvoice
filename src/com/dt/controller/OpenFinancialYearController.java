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
import java.util.List;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Dinesh
 */
public class OpenFinancialYearController implements TabContent {

    private static final Logger logger = 
            Logger.getLogger(OpenFinancialYearController.class.getName());
    
    private Stage mainWindow;
    private TabPane tabPane;
    
    @FXML
    private ListView<FinancialYear> lvwFinancialYears;
     @FXML
    private Button btnOpen;
      @FXML
    private Button btnClose;
    
    
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
      
        ObservableList<FinancialYear> observableList =
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
    
   /**
    * This method is automatically called by the JavaFX Runtime when 
    * loading the controller's associated view
    */
    public void initialize() {
        btnOpen.disableProperty().bind(lvwFinancialYears.getSelectionModel()
                .selectedItemProperty().isNull());
        
        lvwFinancialYears.setCellFactory((ListView<FinancialYear> param) -> {
            final ListCell<FinancialYear> cell = new ListCell<FinancialYear>() {

                @Override
                protected void updateItem(final FinancialYear item, boolean empty) {
                    super.updateItem(item, empty); 
                    
                    if (empty) {
                        setText(null);
                        setOnKeyPressed(null);
                        setOnMouseClicked(null);
                    } else {
                        setText(item.toString());
                        setOnMouseClicked((MouseEvent event) -> {
                            if (event.getClickCount() == 2) {
                                openYear(item);
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
                openYear(lvwFinancialYears.getSelectionModel().getSelectedItem());
            }
        });
    }
    
    @FXML
    private void onOpenFinancialYearAction(ActionEvent event) {
         final FinancialYear year = lvwFinancialYears.getSelectionModel()
                .getSelectedItem();
         
         openYear(year);
    }
    
    @FXML
    private void onCloseTabAction(ActionEvent event) {
      Tab currentTab =  tabPane.getSelectionModel().getSelectedItem();
      tabPane.getTabs().remove(currentTab);
    }
    
    private void openYear(final FinancialYear year) {
        
        
        final FinancialYear activeYear = Global.getActiveFinancialYear();
        if (activeYear != null && activeYear.equals(year)) {
            Utility.beep();
            final String message = "The year " + year.toString() + 
                    " is already opened!";
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, message,
                    ButtonType.OK);
            alert.setHeaderText("Selected year is already opened");
            alert.setTitle("Selected Year Already Open");
            alert.initOwner(mainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return;
        }
        
        if (activeYear != null && tabPane.getTabs().size() > 1) {
            final String message = "All the currently open tabs related to the year" +
                    activeYear.toString() + " will be closed before opening the " +
                    "selected year. \n\nIs this OK?";
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                    ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle("Confirm To Close Open Tabs");
            alert.setHeaderText("Please confirm to close open tabs");
            alert.initOwner(mainWindow);
             Global.styleAlertDialog(alert);
           Optional<ButtonType> response = alert.showAndWait();
           if (!response.isPresent() || response.get() == ButtonType.CANCEL) {
               return;
           }
           
           //close all open tabs except this tab
           final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
           if (!Global.closeTabs(tabPane, currentTab)) {
               return; //one of the open tabs refused to close
           }
        }
        
        if (activeYear != null) {
            Database.shutDownActiveYearDatabase();
        }
        
        if (!Database.openAsActiveYear(year)) {
            final String message = "An error occurred in opening the " +
                    "financial year " + year.toString() + " !";
            final Alert alert = new Alert(Alert.AlertType.ERROR, message,
                    ButtonType.OK);
            alert.initOwner(mainWindow);
            alert.setTitle("Error in Opening Year");
            alert.setHeaderText("Error in Opening the selected year !");
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return;
        }
        
        //close the tab
        btnClose.fire();
    }

    
}

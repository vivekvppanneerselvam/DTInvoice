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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.*;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;


/**
 * @author vivek
 *
 */
public class RestoreDatabaseController implements TabContent {
    
    private final static Logger logger = 
            Logger.getLogger(RestoreDatabaseController.class.getName());
     private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    
    private Stage mainWindow;
    private TabPane tabPane;
    
    @FXML
    private TextField tfDatabaseLocation;
    @FXML
    private ListView<FinancialYear> lvwYearsDiscovered;
    @FXML
    private Label lblNoYearSelected;
    @FXML
    private Button btnRestore;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnBrowseForFolder;
    @FXML
    private VBox centerPane;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        
       btnRestore.disableProperty().bind(isDirty.not());
       lvwYearsDiscovered.getSelectionModel().selectedItemProperty()
               .addListener((Observable observable) -> {
           isDirty.set(true);
       });
       
     
           lblNoYearSelected.managedProperty().bind(lblNoYearSelected.visibleProperty());
            lblNoYearSelected.visibleProperty().bind(lblNoYearSelected.textProperty()
               .length().greaterThanOrEqualTo(1));
      
      
      tfDatabaseLocation.textProperty().addListener((ObservableValue<? extends String> observable, 
              String oldValue, String newValue) -> {
          tfDatabaseLocation.getTooltip().setText(newValue);
       });
      
      centerPane.managedProperty().bind(centerPane.visibleProperty());
      centerPane.setVisible(false);
      
    }    

    @Override
    public boolean shouldClose() {
        return true;
    }

    @Override
    public void putFocusOnNode() {
        btnBrowseForFolder.requestFocus();
    }

    @Override
    public boolean loadData() {
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
    private void onBrowseForFolderAction(ActionEvent event) {
        
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder to Restore Database From");
        String path = tfDatabaseLocation.getText();
        if (path != null && !path.isEmpty()) {
            chooser.setInitialDirectory(new File(path));
        }
        
        File file = chooser.showDialog(mainWindow);
        if (file == null) {return;}
        
        tfDatabaseLocation.setText(file.getAbsolutePath());
        isDirty.set(true);
        
        //get finanical years found at the selected location
        final List<FinancialYear> years = getFinancialYears(file.getAbsolutePath());
        if (years == null) { 
            centerPane.setVisible(false);
            isDirty.set(false);
            return;
        }
        
        final ObservableList<FinancialYear> observableList = 
                FXCollections.<FinancialYear>observableList(years);
        lvwYearsDiscovered.setItems(observableList);
        centerPane.setVisible(true);
        
        final FinancialYear activeYear = Global.getActiveFinancialYear();
        if (activeYear != null) {
            if (years.indexOf(activeYear) >= 0) {
                lvwYearsDiscovered.getSelectionModel().select(activeYear);
                lvwYearsDiscovered.scrollTo(activeYear);
            }
        }
        
        lvwYearsDiscovered.requestFocus();
       
    }
    
    @FXML
    private void onCloseTabAction(ActionEvent event) {
        final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(currentTab);
    }
    
    @FXML
    private void onRestoreDatabaseAction(ActionEvent event) {
        
        final FinancialYear year = lvwYearsDiscovered.getSelectionModel()
                .getSelectedItem();
        
        final FinancialYear activeYear = Global.getActiveFinancialYear();
        if (activeYear != null && activeYear.equals(year)) {
            closeTabs(); //close all tabs except this tab
            Database.shutDownActiveYearDatabase();
        }
        
        final String path = tfDatabaseLocation.getText();
        boolean isSuccess = Database.restoreDatabase(year, path);
        
        if (isSuccess) {
            isDirty.set(false);
            final String message = "The database for the financial year " + 
                    year.toString() + " has been successfully restored !";
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, message,
                    ButtonType.OK);
            alert.initOwner(mainWindow);
            alert.setTitle("Success");
            alert.setHeaderText("Database Successfully Restored !!!");
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return;
        }
        
        //show failure message
        final String message = "An error occurred in restoring database of " +
                "the selected year.";
        final Alert alert = new Alert(Alert.AlertType.ERROR, message,
                    ButtonType.OK);
        alert.initOwner(mainWindow);
        alert.setTitle("Error");
        alert.setHeaderText("Database could not be restored due to an error !");
         Global.styleAlertDialog(alert);
        alert.showAndWait();
        
    }
    
    /**
     * Close all tabs except this tab. Tabs being closed are not prompted
     * to save any unsaved data
     */
    private void closeTabs() {
        final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().retainAll(currentTab);
    }
    
    private List<FinancialYear> getFinancialYears(final String path) {
        List<FinancialYear> years = null;
        
        try {
            years = Global.getExistingFinancialYears(path);
        } catch (Exception e) {
            final String message = "An error occurred while enumerating " +
                    "financial year(s) found in the selected folder. ";
            Utility.beep();
            final Alert alert = new Alert(Alert.AlertType.ERROR, message, 
                    ButtonType.OK);
            alert.initOwner(mainWindow);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error while reading through the contents of the Selected Folder !");
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return null;
        }
        
        if (years.isEmpty()) {
            final String message = "No Financial Year Database " +
                    "found in the selected folder ! ";
            Utility.beep();
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, message, 
                    ButtonType.OK);
            alert.initOwner(mainWindow);
            alert.setTitle("No Database Found");
            alert.setHeaderText("No Database found in the selected folder !");
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return null;
        }
        
        return years;
    }
    
}

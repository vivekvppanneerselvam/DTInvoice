/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;

import com.dt.application.Global;
import com.dt.dao.Database;
import com.dt.dto.FinancialYear;
import com.dt.utils.*;
import java.io.File;
import java.util.Optional;
import javafx.stage.Stage;
import java.util.logging.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;

/**
 * FXML Controller class
 *
 * @author Dinesh
 */
public class AppDataLocationController implements TabContent {

    private final static Logger logger
            = Logger.getLogger(AppDataLocationController.class.getName());

    private Stage mainWindow;
    private TabPane tabPane;
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);

    @FXML
    private TextField tfFolderPath;
    @FXML
    private Button btnBrowseForFolder;
    @FXML
    private Button btnSave;
    @FXML
    private CheckBox chkMoveDatabases;

    @Override
    public boolean shouldClose() {
       if (!isDirty.get()) {
           return true;
       }
       
       ButtonType response = shouldSaveUnsavedData();
       if (response == ButtonType.CANCEL) {
           return false;
       } else if (response == ButtonType.NO) {
           return true;
       } else return saveData();
    }

    @Override
    public void putFocusOnNode() {
        btnBrowseForFolder.requestFocus();
    }

    @Override
    public boolean loadData() {
        String path = Global.getAppDataPath();
        tfFolderPath.setText(path);
        return true;
    }

    @Override
    public void setMainWindow(Stage stage) {
        mainWindow = stage;
    }

    @Override
    public void setTabPane(TabPane pane) {
        this.tabPane = pane;
    }

    public void initialize() {
        btnSave.disableProperty().bind(isDirty.not());
        tfFolderPath.getTooltip().textProperty().bind(tfFolderPath.textProperty());
        chkMoveDatabases.managedProperty().bind(chkMoveDatabases.visibleProperty());
    }

    @FXML
    private void onBrowseForFolderAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder to Store Application Data");
        chooser.setInitialDirectory(new File(tfFolderPath.getText()));
        File file = chooser.showDialog(mainWindow);
        if (file != null) {
            String path = file.getAbsolutePath();
            tfFolderPath.setText(path);
            chkMoveDatabases.setVisible(true);
            isDirty.set(true);
        }
    }
    
    @FXML
    private void onSaveLocationAction(ActionEvent event) {
        boolean isSuccess = saveData();
        
        if (isSuccess) {
            isDirty.set(false);
            
            if (chkMoveDatabases.isSelected()) {
                final String message = "The existing databases, if any, " + 
                        "have been moved successfully to the new location !!";
                final Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                        message, ButtonType.OK);
                alert.initOwner(mainWindow);
                alert.setTitle("Success");
                alert.setHeaderText("Databases moved successfully to the new location !");
                Global.styleAlertDialog(alert);
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    private void onCloseTabAction(ActionEvent event) {
        if (shouldClose()) {
             Tab tab = tabPane.getSelectionModel().getSelectedItem();
            tabPane.getTabs().remove(tab);
        }
    }
    
    private boolean saveNewDataLocation(final String location) {
         boolean saved = Global.saveAppDataFolderPath(location);
         
        if (!saved) {
            Alert alert = new Alert(Alert.AlertType.ERROR, 
                    "An error occurred while saving the new data location !",
                    ButtonType.OK);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error in saving new data location !");
            alert.initOwner(mainWindow);
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
        }
        
        return saved;
    }
    
    private ButtonType shouldSaveUnsavedData() {
        String message = "The new application data path has not been saved yet."  
                + System.lineSeparator() + 
                "Would you like to save it before closing this tab?";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle("New Path Not Saved");
        alert.setHeaderText("Save the new path before closing this tab?");
        alert.initOwner(mainWindow);
         Global.styleAlertDialog(alert);
        
        Optional<ButtonType> response = alert.showAndWait();
        if (! response.isPresent()) {
            return  ButtonType.CANCEL;
        }
        
        return response.get();
    }
    
    private boolean saveData() {
        
        //user opted to move the existing databases to the new location
        if (chkMoveDatabases.isSelected()) { 
            final FinancialYear year = Global.getActiveFinancialYear();
            if (year != null && tabPane.getTabs().size() > 1) {
                final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
                if (!Global.closeTabs(tabPane, currentTab)) {
                    return false;
                }
            }
            if (year != null) {
                Database.shutDownActiveYearDatabase();
            }
        }
        
        final String newDataLocation = tfFolderPath.getText();
        final String currentDataLocation = Global.getAppDataPath();
        
        if (!saveNewDataLocation(newDataLocation)) {
            return false;
        }
        
        //user opted to move the existing databases to the new location
        if (chkMoveDatabases.isSelected()) { 
            if (! Global.moveDatabases(currentDataLocation, newDataLocation)) {
                final String message = "An error occurred in moving the " +
                        "databases from the current location to the new location !"
                        +"\n\nIt is suggested that you choose a different folder " +
                        "for storing database(s).";
                final Alert alert = new Alert(Alert.AlertType.ERROR, message, 
                        ButtonType.OK);
                alert.initOwner(mainWindow);
                alert.setTitle("Error in Moving Database(s)");
                alert.setHeaderText("Error in moving database(s) to the new location !");
                Utility.beep();
                Global.styleAlertDialog(alert);
                alert.showAndWait();
                
                //restore the old database location
                saveNewDataLocation(currentDataLocation);
                return false;
            }
        }
        
        return true;
    }

}

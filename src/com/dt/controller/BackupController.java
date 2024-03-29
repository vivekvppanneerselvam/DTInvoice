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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.util.logging.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


/**
 * @author vivek
 *
 */
public class BackupController implements TabContent {
    
    private final static Logger logger = 
            Logger.getLogger(BackupController.class.getName());

    private Stage mainWindow;
    private TabPane tabPane;
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    private Label[] errorLabels = null;
    private String backupPath = null;
    
    @FXML
    private ListView<FinancialYear> lvwExistingYears;
    @FXML
    private TextField tfBackupLocation;
    @FXML
    private Button btnBrowseForFolder;
    @FXML
    private Button btnBackup;
    @FXML
    private Label lblNoYearSelected;
    @FXML
    private Label lblFolderNotExists;
    
    
    /**
     * Initializes the controller class.
     */
    public void initialize() {
       btnBackup.disableProperty().bind(isDirty.not());
       lvwExistingYears.getSelectionModel().selectedItemProperty()
               .addListener((Observable observable) -> {
           isDirty.set(true);
       });
       
      errorLabels = new Label[] {lblNoYearSelected, lblFolderNotExists};
      
      for(Label label : errorLabels) {
           label.managedProperty().bind(label.visibleProperty());
            label.visibleProperty().bind(label.textProperty()
               .length().greaterThanOrEqualTo(1));
      }
      
      tfBackupLocation.textProperty().addListener((ObservableValue<? extends String> observable, 
              String oldValue, String newValue) -> {
          tfBackupLocation.getTooltip().setText(newValue);
       });
      
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
       
        String lastBackupLocation = Global.getLastBackupLocation();
        tfBackupLocation.setText(lastBackupLocation);
        
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
        lvwExistingYears.setItems(observableList);
        
        if ( !years.isEmpty()) {
            final FinancialYear activeYear = Global.getActiveFinancialYear();
            if (activeYear != null) {
                lvwExistingYears.getSelectionModel().select(activeYear);
                lvwExistingYears.scrollTo(activeYear);
            }
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
    
    private void clearErrorLabels() {
        for(Label label : errorLabels) {
            label.setText("");
        }
    }
    
    private boolean validateInput() {
       clearErrorLabels();
       
       boolean valid = true;
       
       if (lvwExistingYears.getSelectionModel().isEmpty()) {
           lblNoYearSelected.setText("No Financial Year Selected !");
           valid = false;
       }
       
       if (Files.notExists(Paths.get(tfBackupLocation.getText()), 
               LinkOption.NOFOLLOW_LINKS)) { 
           lblFolderNotExists.setText("The folder doesn't exist !");
           valid = false;
       }
           
       return valid;
    }
    
    @FXML
    private void onCloseTabAction(ActionEvent event) {
        final Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(currentTab);
    }
    
    @FXML
    private void onBackupDatabaseAction(ActionEvent event) {
       
        if (!validateInput()) {
            Utility.beep();
            return;
        }
        
        if (!backupDatabase()) {
            return;
        }
        
        Global.saveLastBackupLocation(tfBackupLocation.getText());
        isDirty.set(false);
        
        final FinancialYear year = lvwExistingYears.getSelectionModel()
                .getSelectedItem();
        final String message = "The database for the year " + year.toString()
              +  " has been successfully backed up to the path: \n\n" + 
                backupPath ;
        
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, message,
                ButtonType.OK);
        alert.initOwner(mainWindow);
        alert.setTitle("Success");
        alert.setHeaderText("Backup Successful");
         Global.styleAlertDialog(alert);
        alert.showAndWait();
        
       
    }
    
    @FXML
    private void onBrowseForFolderAction(ActionEvent event) {
         DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Folder for backup copy");
        chooser.setInitialDirectory(new File(tfBackupLocation.getText()));
        File file = chooser.showDialog(mainWindow);
        
        if (file != null) {
            tfBackupLocation.setText(file.getAbsolutePath());
            isDirty.set(true);
        }
    }
    
    private boolean backupDatabase() {
       
        final FinancialYear year = lvwExistingYears.getSelectionModel()
                .getSelectedItem();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        backupPath = tfBackupLocation.getText() + File.separator + 
                "fxbilling" + File.separator + "backup" + File.separator
                + LocalDate.now().format(formatter);
        final String path = backupPath + File.separator + year.toEpochMillis();
        
        try {
            Files.createDirectories(Paths.get(path));
        } catch (Exception e) {
            logger.logp(Level.SEVERE, BackupController.class.getName(), 
                    "backupDatabase", 
                    "Error in creating directories for database backup.", e);
            final String message = "An error occurred in creating required directories"
                    + " for database backup.";
            final Alert alert = new Alert(Alert.AlertType.ERROR, message, 
                    ButtonType.OK);
            alert.initOwner(mainWindow);
            alert.setTitle("Could Not Initiate Backup");
            alert.setHeaderText("Could not create required directories for database backup !");
            Utility.beep();
             Global.styleAlertDialog(alert);
             alert.showAndWait();
            return false;
        }
        
        if (!Database.backupDatabase(year, path)) {
            final String message = "An error occurred in backingup the database.";
            final Alert alert = new Alert(Alert.AlertType.ERROR, message, 
                    ButtonType.OK);
            alert.initOwner(mainWindow);
            alert.setTitle("Backup Failed");
            alert.setHeaderText("Error in Database Backup !");
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
        
        return true;
    }
    
}

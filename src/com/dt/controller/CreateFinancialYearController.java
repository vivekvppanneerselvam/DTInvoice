/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;

import com.dt.application.Global;
import static com.dt.application.Global.getExtendedAppDataPath;
import com.dt.dao.Database;
import com.dt.dto.FinancialYear;
import com.dt.utils.TabContent;
import com.dt.utils.Utility;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.*;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;



/**
 * FXML Controller class
 *
 * @author Dinesh
 */
public class CreateFinancialYearController implements TabContent {
    
    private static final Logger logger = Logger.getLogger(
            CreateFinancialYearController.class.getName());
    private final DateTimeFormatter dateTimeFormatter = 
            DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final LocalDate maxStartDate = LocalDate.now().plusMonths(1);
    private final LocalDate maxEndDate = maxStartDate.plusYears(1).minusDays(1);
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    private final ObservableList<FinancialYear> existingFinancialYears = 
                FXCollections.<FinancialYear>observableArrayList();
    
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private CheckBox chkTransferData;
    @FXML
    private ListView<FinancialYear> lvwExistingYears;
    @FXML
    private Button btnCreate;
    @FXML
    private Label lblStartDateError;
    @FXML
    private Label lblEndDateError;
    @FXML
    private Label lblExistingYearError;
    @FXML
    private VBox existingYearsPane;
    @FXML
    private Button btnClose;
    
    private Stage mainWindow = null;
    private TabPane tabPane = null;
    private LocalDate date = null;
    private Label[] errorLabels =  null;


    @Override
    public boolean shouldClose() {
        return true;
    }

    @Override
    public void putFocusOnNode() {
        dpStartDate.requestFocus();
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
      
       existingFinancialYears.addAll(years);
        return true;       
    }

    @Override
    public void setMainWindow(Stage stage) {
        mainWindow = stage;
    }

    @Override
    public void setTabPane(TabPane pane) {
        tabPane = pane;
    }

    public void initialize() {
        existingYearsPane.managedProperty().bind(existingYearsPane.visibleProperty());
        existingYearsPane.visibleProperty().bind(chkTransferData.selectedProperty());
        
        errorLabels = new Label[]{lblStartDateError, lblEndDateError, lblExistingYearError};
        for(Label label : errorLabels) {
             label.managedProperty().bind(label.visibleProperty());
            label.visibleProperty().bind(label.textProperty().length().greaterThan(0));
        }
        
        dpStartDate.setConverter(Utility.getDateStringConverter());
        dpStartDate.setDayCellFactory(this::getStartDateCell);
        dpStartDate.setValue(LocalDate.now());
        
        dpEndDate.setConverter(Utility.getDateStringConverter());
        dpEndDate.setDayCellFactory(this::getEndDateCell);
        dpEndDate.setValue(LocalDate.now().plusYears(1).minusDays(1));
        
        btnCreate.disableProperty().bind(isDirty.not());
        
        dpStartDate.valueProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
        dpEndDate.valueProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
        chkTransferData.selectedProperty().addListener((Observable observable) -> {
            isDirty.set(true);
        });
        
        lvwExistingYears.setItems(existingFinancialYears);
        BooleanBinding binding = Bindings.isEmpty(existingFinancialYears);
        chkTransferData.disableProperty().bind(binding);
    }
    
     private DateCell getStartDateCell(DatePicker datePicker) {
        return Utility.getDateCell(datePicker, null, maxStartDate);
    }
    
     private DateCell getEndDateCell(DatePicker datePicker) {
        return Utility.getDateCell(datePicker, null, maxEndDate);
    }
     
     private void clearErrorLabels() {
         for (Label label : errorLabels) {
             label.setText("");
         }
     }
     
     @FXML
     private void onCloseTabAction(ActionEvent event) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        tabPane.getTabs().remove(tab);
     }
     
     private boolean validateInput() {
         clearErrorLabels();
         boolean valid = true;
         
         final LocalDate startDate = dpStartDate.getValue();
         final LocalDate endDate = dpEndDate.getValue();
         
         if (startDate == null) {
             lblStartDateError.setText("Start Date not specified!");
             valid = false;
         }
         
         if (endDate == null) {
             lblEndDateError.setText("End Date not specified!");
             valid = false;
         }
         
         if (startDate != null && endDate != null) {
             if (startDate.compareTo(endDate) >= 0) {
                 lblEndDateError.setText("End Date must come after Start Date!");
                 valid = false;
             }
         }
         
         if (chkTransferData.isSelected()) {
             if (!lvwExistingYears.getItems().isEmpty() && 
                     lvwExistingYears.getSelectionModel().isEmpty()) {
              lblExistingYearError.setText("No existing financial year selected!");
              valid = false;
             }
             
             if (valid && !lvwExistingYears.getItems().isEmpty()) {
                 FinancialYear year = new FinancialYear(startDate, endDate);
                 FinancialYear overlappingYear = 
                         FinancialYear.getOverlappingYear(lvwExistingYears.getItems(), year);
                 
                 if (overlappingYear != null) {
                      lblExistingYearError.setText("The new financial year overlaps " +
                              "with the existing year '" +
                         overlappingYear.toString() + "'.");
                        valid = false;
                 } else {
                     FinancialYear existingYear = lvwExistingYears.
                             getSelectionModel().getSelectedItem();
                    if ( year.isBefore(existingYear)) {
                        lblExistingYearError.setText("The year to transfer data from " +
                                "must come before the proposed new year.");
                        valid = false;
                    }
                 }
                
             }
             
         }
         
         return valid;
     }
     
     @FXML
     private void onCreateFinancialYearAction(ActionEvent event) {
         
         if (!validateInput()) {
             Utility.beep();
             return;
         }
         
        final FinancialYear year = new FinancialYear(dpStartDate.getValue(),
                 dpEndDate.getValue());
         
        if (!createNewYearFolder(year)) {
            return;
        }
        
        if (!createDatabase(year)) {
            Global.deleteFinancialYearFolder(year);
            return;
        }
        
        if (chkTransferData.isSelected()) {
            if (!transferDataFromExistingYear(year)) {
                Global.deleteFinancialYearFolder(year);
                return;
            }
        }

        isDirty.set(false);
        
        boolean shouldOpen = shouldOpenTheNewYear(year);
        if (!shouldOpen) {btnClose.fire(); return;}
        
        if (openNewYear(year)) {
            btnClose.fire(); //close this tab
        }
         
     }
     
     private boolean openNewYear(final FinancialYear year) {
        
         final Tab currentTab =  tabPane.getSelectionModel().getSelectedItem();
        //close all tabs except the current tab
         if(!Global.closeTabs(tabPane, currentTab)) {
            return false;
        }
       
        Database.shutDownActiveYearDatabase();
        
        if(!Database.openAsActiveYear(year)) {
            String message = "An error occurred in connecting to the database " +
                    " of the newly created year " + year.toString();
            final Alert alert = new Alert(Alert.AlertType.ERROR, message, 
                 ButtonType.OK);
            alert.initOwner(mainWindow);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Couldn't connect to the new year database!");
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        } 
        
        return true;
       
     }
     
     public boolean shouldOpenTheNewYear(final FinancialYear year) {
         String message = "The financial year " + year.toString() + 
                 " was successfully created!!\n\n" + 
                 "Would you like to open it now? ";
         
         final FinancialYear activeYear = Global.getActiveFinancialYear();
         if (activeYear != null) {
             message += "\nAll the tabs related to the currently opened year " + 
                     activeYear.toString() + " will be closed first.";
         }
         
         final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                 ButtonType.YES, ButtonType.NO);
         alert.initOwner(mainWindow);
         alert.setTitle("Success");
         alert.setHeaderText("New Year Successfully Created! Open it now?");
          Global.styleAlertDialog(alert);
         
        Optional<ButtonType> response = alert.showAndWait();
        if (!response.isPresent()) {
            return false;
        }
        
        return (response.get() == ButtonType.YES ? true : false);
     }
     
     public boolean createDatabase(final FinancialYear year) {
          
          if (! Database.createDatabase(year)) {
              String message = "An error occurred in creating a database for the new year";
              Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK );
              alert.initOwner(mainWindow);
              alert.setTitle("Error Occurred");
              alert.setHeaderText("Error in Creating Database");
              Utility.beep();
               Global.styleAlertDialog(alert);
              alert.showAndWait();
              return false;
          }
          
          return true;
     }
     
     /**
     * Creates a folder in the disk corresponding to the specified financial year.
     * @param fy 
     * @return  The path corresponding to the newly created folder
     */
    public boolean createNewYearFolder(final FinancialYear fy) {
            
        if (!Global.createFinancialYearFolder(fy)) {
           Utility.beep();
           final Alert alert = new Alert(Alert.AlertType.ERROR, 
                   "An error occurred in creating a directory for the new year.", 
                   ButtonType.OK);
           alert.setTitle("Error Occurred");
           alert.setHeaderText("Error in creating a folder for the new year");
           alert.initOwner(mainWindow);
            Global.styleAlertDialog(alert);
           alert.showAndWait();
           return false;
        }
        
        return true;
        
    }
    
    /**
     * 
     * @param newConnection - Connection to the newly created database
     * @return - True if the data transfer was successful
     */
    private boolean transferDataFromExistingYear(final FinancialYear newYear) {
       
        //get the selected existing year
        final FinancialYear existingYear = lvwExistingYears.getSelectionModel()
                .getSelectedItem();
        
        if (! Database.transferData(newYear, existingYear)) {
            final String message = "An error occurred in transferring data from "
                    + "the existing year " + existingYear.toString() + 
                    " to the newly created year!" + 
                   "\nAll the work done to create the new year has been rolled back.";
            final Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.initOwner(mainWindow);
            alert.setTitle("Error in Transferring Data");
            alert.setHeaderText("Error in transferring data from the existing year to the new year!");
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
        
        return true;
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;

import com.dt.application.Global;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.beans.*;
import javafx.beans.property.*;
import com.dt.utils.*;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javafx.scene.image.*;
import com.dt.dao.FirmDetailsPersistence;
import com.dt.dto.FirmDetails;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.logging.*;

/**
 * FXML Controller class
 *
 * @author Dinesh
 */
public class FirmDetailsController implements TabContent    
{
      private static final Logger logger = 
            Logger.getLogger(FirmDetailsController.class.getName());

    public Stage MainWindow = null;
    private byte[] logoBytes = null;
    private TabPane tabPane = null;
    
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button deleteLogoButton;
    @FXML
    private Text firmNameError;
    @FXML
    private Text addressError;
    @FXML
    private TextField firmNameField;
    @FXML
    private TextArea addressField;
    @FXML
    private TextField phoneNumbersField;
    @FXML
    private TextField emailAddressField;
    @FXML
    private GridPane gridPane;
    @FXML
    private Button logoButton;
    @FXML
    private ImageView logoView;
   
   private BooleanProperty isDirty = new SimpleBooleanProperty(false);
   
    
    /**
     * Initializes the controller class.
     */
    public void initialize() {
        
        okButton.prefWidthProperty().bind(cancelButton.widthProperty());
        
        firmNameError.managedProperty().bind(firmNameError.visibleProperty());         
        addressError.managedProperty().bind(addressError.visibleProperty()); 
        
        firmNameError.visibleProperty()
                .bind(firmNameError.textProperty().length().greaterThanOrEqualTo(1));
        addressError.visibleProperty()
                .bind(addressError.textProperty().length().greaterThanOrEqualTo(1));
        
        firmNameField.textProperty().addListener(this::invalidated);
        addressField.textProperty().addListener(this::invalidated);
        emailAddressField.textProperty().addListener(this::invalidated);
        phoneNumbersField.textProperty().addListener(this::invalidated);
        
        okButton.disableProperty().bind(isDirty.not());
        
        //load a dummy logo file onto the ImageView control
        loadDummyLogoFile();
    }  
    
    @FXML
    private void onCancelCommand(ActionEvent event) {
        
       if (isDirty.get()) {
            ButtonType buttonType = shouldSaveUnsavedData();
            if (buttonType == ButtonType.CANCEL) {
                return; // no need to take any further action
            } else if (buttonType == ButtonType.YES) {
                if (!saveData()) {
                    return;
                }
            } 
        }
        
        closeTab();
               
    }
    
    @FXML
    private void onOKCommand(ActionEvent event) {
       
        if (! validateInput()) {
            Utility.beep();
            firmNameField.requestFocus();
            return;
        }
        
        boolean result = saveData();
         
        if (result) {
            closeTab();
        }
        
    }

    public void invalidated(Observable observable) {
        isDirty.set(true);
    }
    
    private void closeTab() {
        Tab tab = tabPane.selectionModelProperty().get()
                .selectedItemProperty().get();
        tabPane.getTabs().remove(tab); //close the current tab
    }

    @Override
    public boolean shouldClose() {
     
        if (isDirty.get()) {
            ButtonType response = shouldSaveUnsavedData();
            if (response == ButtonType.CANCEL) {
                return false;
            }

            if (response == ButtonType.YES) {
                return saveData();
            }

        }

        return true;
    }
    
    private boolean saveData() {
        
        FirmDetails firmDetails = new FirmDetails();
        firmDetails.setFirmName(firmNameField.getText().trim());
        firmDetails.setAddress(addressField.getText().trim());
        String phoneNumbers = phoneNumbersField.getText().trim();
        phoneNumbers = phoneNumbers.length() > 0 ? phoneNumbers : null;
        firmDetails.setPhoneNumbers(phoneNumbers);
        String emailAddress = emailAddressField.getText().trim();
        emailAddress = emailAddress.length() > 0 ? emailAddress : null;
        firmDetails.setEmailAddress(emailAddress);
        
        if (logoBytes != null) {
            firmDetails.setLogo(logoBytes);
        } else {
            firmDetails.setLogo(null);
        }
        
        try {
            FirmDetailsPersistence.saveData(firmDetails);
        } catch (Exception e) {
            String message = Utility.getDataSaveErrorText();
            Utility.beep();
            Alert alert = Utility.getErrorAlert("Error Occurred", 
                    "Error in Saving Data",
                    message, MainWindow);
            alert.showAndWait();
            return false;
        }
        
        return true;
    }

    @Override
    public void putFocusOnNode() {
        firmNameField.requestFocus();
    }
    
    @FXML
    private void onLogoSelectAction(ActionEvent actionEvent) throws IOException{
       
       FileChooser fileChooser = getFileChooser();
        
        File file = fileChooser.showOpenDialog(MainWindow);
        if (file != null && file.exists()) { 
            final long MAX_LENGTH = 512 * 1024; //512 KB
            if (file.length() > MAX_LENGTH ) {
                Alert alert = new Alert(Alert.AlertType.ERROR, 
                        "The selected file's size is greater than 512 KB.", ButtonType.OK);
                alert.setTitle("Invalid File Size");
                alert.setHeaderText("File size is too long.");
                Toolkit.getDefaultToolkit().beep();
                alert.initOwner(MainWindow);
                 Global.styleAlertDialog(alert);
                alert.showAndWait();
                return;
            }
            
            if (loadLogoFile(file)){
                deleteLogoButton.setDisable(false);
                isDirty.set(true);
            }            
            
        }     
        
    }
    
    private FileChooser getFileChooser() {
         FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Logo File");
        FileChooser.ExtensionFilter imageFilter = 
                new FileChooser.ExtensionFilter("image files", 
                "*.jpg", "*.png", "*.bmp", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        fileChooser.setSelectedExtensionFilter(imageFilter);
        
        return fileChooser;
    }
    
    private boolean loadLogoFile(File file) {
        
        byte[] bytes;
        
        try {
            bytes = Utility.getFileBytes(file); 
        } catch (Exception e) {
            logger.logp(Level.SEVERE, FirmDetailsController.class.getName(), 
                    "loadLogoFile", "Error in reading logo file", e);
            Utility.beep();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Reading Logo File");
            alert.setHeaderText("The logo file couldn't be read!");
            alert.setContentText("An error occurred in reading the selected logo file!");
            alert.initOwner(MainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
        
        try(ByteArrayInputStream instream = new ByteArrayInputStream(bytes)) {
            Image image = new Image(instream);
            logoView.setImage(image);
        } catch (Exception e) {
            logger.logp(Level.SEVERE, FirmDetailsController.class.getName(), 
                    "loadLogoFile", "Error in creating an image from the logo file", e);
            Utility.beep();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error in generating image from the logo file!");
            alert.setContentText("An error occurred in generating " + 
                    "an image from the selected logo file!");
            alert.initOwner(MainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
       
       logoBytes = bytes;       
       return true;
    }
    
    private void loadDummyLogoFile()  {
        String url = "/resources/images/no_logo.png";
        Image image = new Image(url);
        if (image != null && !image.isError()) {
            logoView.setImage(image);
        }
    }
    
    private boolean validateInput() {
        boolean valid = true;
        
        int nameLength = firmNameField.getText().trim().length();
        if (nameLength == 0) {
            firmNameError.setText("Firm name not specified!");
            valid = false;
        } else if (nameLength < 3 || nameLength > 70) {
            firmNameError.setText("Firm name should be between 3 and 70 characters in length.");
            valid = false;
        } else {
            firmNameError.setText("");
        }
        
        int addressLength = addressField.getText().trim().length();
         if (addressLength == 0) {
            addressError.setText("Firm's address not specified!");
            valid = false;
        } else if (addressLength < 10 || addressLength > 120) {
            addressError.setText("Firm address should be between 10 and 120 characters in length.");
            valid = false;
        } else {
            addressError.setText("");
        }
        
        return valid;
    }

    @Override
    public boolean loadData() {
        
        FirmDetails firmDetails = null;
        try{
            firmDetails = FirmDetailsPersistence.getData();
        }catch (Exception e) {
            String message = Utility.getDataFetchErrorText();
            Alert alert = Utility.getErrorAlert("Error Occurred",
                    "Error in Fetching Data", message, MainWindow);
            Utility.beep();
            alert.showAndWait();
            return false;
        }
        
        if (firmDetails == null) {return true;}
        boolean success = populateFields(firmDetails);
        isDirty.set(false);
        if (logoBytes != null) {
            deleteLogoButton.setDisable(false);
        }
        return success;
        
    }
    
    private boolean populateFields(FirmDetails firmDetails) {
        firmNameField.setText(firmDetails.getFirmName());
        addressField.setText(firmDetails.getAddress());
        
        String phoneNumbers  = firmDetails.getPhoneNumbers();
        if (phoneNumbers != null) {
            phoneNumbersField.setText(phoneNumbers);
        }
        
        String emailAddress = firmDetails.getEmailAddress();
        if (emailAddress != null) {
            emailAddressField.setText(emailAddress);
        }
        
        logoBytes = firmDetails.getLogo();
        if (logoBytes != null) {
            return loadLogoFile();
        }
        
        return true;
    }
    
    /**
     *@return - true if the logo is successfully loaded, otherwise false.
     */
    private boolean loadLogoFile() {
        
        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(logoBytes)) {
            Image image = new Image(inputStream);
            logoView.setImage(image);
        } catch (Exception e) {
            logger.logp(Level.SEVERE, FirmDetailsController.class.getName(), 
                    "loadLogoFile", "Error in generating logo image from the saved bytes", e);
            Utility.beep();
             Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error in generating image for the firm's logo!");
            alert.setContentText("An error occurred in generating " + 
                    "an image for the firm's logo!");
            alert.initOwner(MainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return false;
        }
        
        return true;
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
            
        final String promptMessage = "The Firm Details data is not saved.\n"
                   + "Save the data before closing the tab?";
           Alert alert = new Alert(Alert.AlertType.CONFIRMATION, promptMessage,
            ButtonType.YES, ButtonType.NO, ButtonType.CANCEL );
           alert.setHeaderText("Unsaved Firm Details. Save now?");
           alert.setTitle("Unsaved Firm Details");
           alert.initOwner(MainWindow);
            Global.styleAlertDialog(alert);

           Optional<ButtonType> result = alert.showAndWait();
           if (! result.isPresent()) {
               return ButtonType.CANCEL;
           }

           return result.get();
    }
     
    @FXML
    private void onDeleteLogoAction(ActionEvent event) {
        logoBytes = null;
        isDirty.set(true);
        loadDummyLogoFile();
        deleteLogoButton.setDisable(true);
    }
}

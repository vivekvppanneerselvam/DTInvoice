package com.dt.controller;

import com.dt.application.Global;
import com.dt.dao.FirmDetailsPersistence;
import com.dt.dto.FirmDetails;
import com.dt.utils.TabContent;
import com.dt.utils.Utility;

import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FirmDetailsController implements TabContent {

    private static final Logger logger =
            Logger.getLogger(
                    FirmDetailsController.class.getName()
            );

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
    private TextField firmSubNameField;

    @FXML
    private TextArea addressField;

    @FXML
    private TextField phoneNumbersField;

    @FXML
    private TextField emailAddressField;

    @FXML
    private CheckBox gstCheckBox;

    @FXML
    private TextField gstNumberField;

    @FXML
    private CheckBox fssaiCheckBox;

    @FXML
    private TextField fssaiNumberField;

    @FXML
    private TextField reviewUrlField;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button logoButton;

    @FXML
    private ImageView logoView;

    private BooleanProperty isDirty =
            new SimpleBooleanProperty(false);

    public void initialize() {

        okButton.prefWidthProperty()
                .bind(cancelButton.widthProperty());

        firmNameError.managedProperty()
                .bind(firmNameError.visibleProperty());

        addressError.managedProperty()
                .bind(addressError.visibleProperty());

        firmNameError.visibleProperty()
                .bind(
                        firmNameError.textProperty()
                                .length()
                                .greaterThanOrEqualTo(1)
                );

        addressError.visibleProperty()
                .bind(
                        addressError.textProperty()
                                .length()
                                .greaterThanOrEqualTo(1)
                );

        /*
         Change listeners
         */
        firmNameField.textProperty()
                .addListener(this::invalidated);

        firmSubNameField.textProperty()
                .addListener(this::invalidated);

        addressField.textProperty()
                .addListener(this::invalidated);

        phoneNumbersField.textProperty()
                .addListener(this::invalidated);

        emailAddressField.textProperty()
                .addListener(this::invalidated);

        gstNumberField.textProperty()
                .addListener(this::invalidated);

        fssaiNumberField.textProperty()
                .addListener(this::invalidated);

        reviewUrlField.textProperty()
                .addListener(this::invalidated);

        /*
         GST checkbox
         */
        gstCheckBox.selectedProperty().addListener(
                (obs, oldVal, newVal) -> {

                    gstNumberField.setDisable(
                            !newVal
                    );

                    if (!newVal) {
                        gstNumberField.clear();
                    }

                    isDirty.set(true);
                }
        );

        /*
         FSSAI checkbox
         */
        fssaiCheckBox.selectedProperty().addListener(
                (obs, oldVal, newVal) -> {

                    fssaiNumberField.setDisable(
                            !newVal
                    );

                    if (!newVal) {
                        fssaiNumberField.clear();
                    }

                    isDirty.set(true);
                }
        );

        gstNumberField.setDisable(true);

        fssaiNumberField.setDisable(true);

        okButton.disableProperty()
                .bind(isDirty.not());

        loadDummyLogoFile();
    }

    @FXML
    private void onCancelCommand(
            ActionEvent event
    ) {

        if (isDirty.get()) {

            ButtonType buttonType =
                    shouldSaveUnsavedData();

            if (buttonType == ButtonType.CANCEL) {

                return;

            } else if (
                    buttonType == ButtonType.YES
            ) {

                if (!saveData()) {
                    return;
                }
            }
        }

        closeTab();
    }

    @FXML
    private void onOKCommand(
            ActionEvent event
    ) {

        if (!validateInput()) {

            Utility.beep();

            firmNameField.requestFocus();

            return;
        }

        boolean result = saveData();

        if (result) {
            closeTab();
        }
    }

    public void invalidated(
            Observable observable
    ) {

        isDirty.set(true);
    }

    private void closeTab() {

        Tab tab =
                tabPane.selectionModelProperty()
                        .get()
                        .selectedItemProperty()
                        .get();

        tabPane.getTabs().remove(tab);
    }

    @Override
    public boolean shouldClose() {

        if (isDirty.get()) {

            ButtonType response =
                    shouldSaveUnsavedData();

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

        FirmDetails firmDetails =
                new FirmDetails();

        /*
         Firm name
         */
        firmDetails.setFirmName(
                firmNameField.getText().trim()
        );

        /*
         Firm sub name
         */
        String subName =
                firmSubNameField.getText().trim();

        firmDetails.setFirmSubName(
                subName.isEmpty()
                        ? null
                        : subName
        );

        /*
         Address
         */
        firmDetails.setAddress(
                addressField.getText().trim()
        );

        /*
         Phone numbers
         */
        String phoneNumbers =
                phoneNumbersField.getText().trim();

        firmDetails.setPhoneNumbers(
                phoneNumbers.isEmpty()
                        ? null
                        : phoneNumbers
        );

        /*
         Email
         */
        String emailAddress =
                emailAddressField.getText().trim();

        firmDetails.setEmailAddress(
                emailAddress.isEmpty()
                        ? null
                        : emailAddress
        );

        /*
         GST
         */
        if (gstCheckBox.isSelected()) {

            firmDetails.setGstNumber(
                    gstNumberField.getText().trim()
            );

        } else {

            firmDetails.setGstNumber(null);
        }

        /*
         FSSAI
         */
        if (fssaiCheckBox.isSelected()) {

            firmDetails.setFssaiNumber(
                    fssaiNumberField.getText().trim()
            );

        } else {

            firmDetails.setFssaiNumber(null);
        }

        /*
         Review URL
         */
        String reviewUrl =
                reviewUrlField.getText().trim();

        firmDetails.setReviewUrl(
                reviewUrl.isEmpty()
                        ? null
                        : reviewUrl
        );

        /*
         Logo
         */
        if (logoBytes != null) {

            firmDetails.setLogo(logoBytes);

        } else {

            firmDetails.setLogo(null);
        }

        try {

            FirmDetailsPersistence.saveData(
                    firmDetails
            );

        } catch (Exception e) {

            logger.logp(
                    Level.SEVERE,
                    FirmDetailsController.class.getName(),
                    "saveData",
                    "Error saving firm details",
                    e
            );

            Utility.beep();

            Alert alert =
                    Utility.getErrorAlert(
                            "Error Occurred",
                            "Error in Saving Data",
                            Utility.getDataSaveErrorText(),
                            MainWindow
                    );

            alert.showAndWait();

            return false;
        }

        isDirty.set(false);

        return true;
    }

    @Override
    public void putFocusOnNode() {
        firmNameField.requestFocus();
    }

    @Override
    public boolean loadData() {

        FirmDetails firmDetails = null;

        try {

            firmDetails =
                    FirmDetailsPersistence.getData();

        } catch (Exception e) {

            logger.logp(
                    Level.SEVERE,
                    FirmDetailsController.class.getName(),
                    "loadData",
                    "Error loading firm details",
                    e
            );

            Utility.beep();

            Alert alert =
                    Utility.getErrorAlert(
                            "Error Occurred",
                            "Error in Fetching Data",
                            Utility.getDataFetchErrorText(),
                            MainWindow
                    );

            alert.showAndWait();

            return false;
        }

        if (firmDetails == null) {
            return true;
        }

        boolean success =
                populateFields(firmDetails);

        isDirty.set(false);

        if (logoBytes != null) {
            deleteLogoButton.setDisable(false);
        }

        return success;
    }

    private boolean populateFields(
            FirmDetails firmDetails
    ) {

        /*
         Firm name
         */
        firmNameField.setText(
                firmDetails.getFirmName()
        );

        /*
         Sub name
         */
        if (firmDetails.getFirmSubName()
                != null) {

            firmSubNameField.setText(
                    firmDetails.getFirmSubName()
            );
        }

        /*
         Address
         */
        addressField.setText(
                firmDetails.getAddress()
        );

        /*
         Phone
         */
        if (firmDetails.getPhoneNumbers()
                != null) {

            phoneNumbersField.setText(
                    firmDetails.getPhoneNumbers()
            );
        }

        /*
         Email
         */
        if (firmDetails.getEmailAddress()
                != null) {

            emailAddressField.setText(
                    firmDetails.getEmailAddress()
            );
        }

        /*
         GST
         */
        if (firmDetails.getGstNumber()
                != null
                && !firmDetails.getGstNumber()
                .trim()
                .isEmpty()) {

            gstCheckBox.setSelected(true);

            gstNumberField.setDisable(false);

            gstNumberField.setText(
                    firmDetails.getGstNumber()
            );
        }

        /*
         FSSAI
         */
        if (firmDetails.getFssaiNumber()
                != null
                && !firmDetails.getFssaiNumber()
                .trim()
                .isEmpty()) {

            fssaiCheckBox.setSelected(true);

            fssaiNumberField.setDisable(false);

            fssaiNumberField.setText(
                    firmDetails.getFssaiNumber()
            );
        }

        /*
         Review URL
         */
        if (firmDetails.getReviewUrl()
                != null) {

            reviewUrlField.setText(
                    firmDetails.getReviewUrl()
            );
        }

        /*
         Logo
         */
        logoBytes = firmDetails.getLogo();

        if (logoBytes != null) {
            return loadLogoFile();
        }

        return true;
    }

    private boolean loadLogoFile() {

        try (
                ByteArrayInputStream inputStream =
                        new ByteArrayInputStream(
                                logoBytes
                        )
        ) {

            Image image =
                    new Image(inputStream);

            logoView.setImage(image);

        } catch (Exception e) {

            logger.logp(
                    Level.SEVERE,
                    FirmDetailsController.class.getName(),
                    "loadLogoFile",
                    "Error generating image",
                    e
            );

            Utility.beep();

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setTitle("Error");

            alert.setHeaderText(
                    "Unable to load logo image"
            );

            alert.setContentText(
                    "Error generating logo image."
            );

            alert.initOwner(MainWindow);

            Global.styleAlertDialog(alert);

            alert.showAndWait();

            return false;
        }

        return true;
    }

    @FXML
    private void onLogoSelectAction(
            ActionEvent actionEvent
    ) throws IOException {

        FileChooser fileChooser =
                getFileChooser();

        File file =
                fileChooser.showOpenDialog(
                        MainWindow
                );

        if (file != null && file.exists()) {

            final long MAX_LENGTH =
                    512 * 1024;

            if (file.length() > MAX_LENGTH) {

                Alert alert =
                        new Alert(
                                Alert.AlertType.ERROR,
                                "The selected file "
                                        + "size is greater "
                                        + "than 512 KB.",
                                ButtonType.OK
                        );

                alert.setTitle(
                        "Invalid File Size"
                );

                alert.setHeaderText(
                        "File size too large."
                );

                Toolkit.getDefaultToolkit()
                        .beep();

                alert.initOwner(MainWindow);

                Global.styleAlertDialog(alert);

                alert.showAndWait();

                return;
            }

            if (loadLogoFile(file)) {

                deleteLogoButton
                        .setDisable(false);

                isDirty.set(true);
            }
        }
    }

    private FileChooser getFileChooser() {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Select Logo File"
        );

        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.jpg",
                        "*.png",
                        "*.bmp",
                        "*.gif"
                );

        fileChooser.getExtensionFilters()
                .add(imageFilter);

        fileChooser.setSelectedExtensionFilter(
                imageFilter
        );

        return fileChooser;
    }

    private boolean loadLogoFile(
            File file
    ) {

        byte[] bytes;

        try {

            bytes =
                    Utility.getFileBytes(file);

        } catch (Exception e) {

            logger.logp(
                    Level.SEVERE,
                    FirmDetailsController.class.getName(),
                    "loadLogoFile",
                    "Error reading logo file",
                    e
            );

            Utility.beep();

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setTitle(
                    "Error Reading Logo"
            );

            alert.setHeaderText(
                    "Unable to read logo file."
            );

            alert.setContentText(
                    "Selected logo file "
                            + "could not be read."
            );

            alert.initOwner(MainWindow);

            Global.styleAlertDialog(alert);

            alert.showAndWait();

            return false;
        }

        try (
                ByteArrayInputStream instream =
                        new ByteArrayInputStream(
                                bytes
                        )
        ) {

            Image image =
                    new Image(instream);

            logoView.setImage(image);

        } catch (Exception e) {

            logger.logp(
                    Level.SEVERE,
                    FirmDetailsController.class.getName(),
                    "loadLogoFile",
                    "Error generating image",
                    e
            );

            Utility.beep();

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setTitle("Error");

            alert.setHeaderText(
                    "Unable to generate image."
            );

            alert.setContentText(
                    "Error generating "
                            + "logo image."
            );

            alert.initOwner(MainWindow);

            Global.styleAlertDialog(alert);

            alert.showAndWait();

            return false;
        }

        logoBytes = bytes;

        return true;
    }

    private void loadDummyLogoFile() {

        String url =
                "/resources/images/no_logo.png";

        Image image =
                new Image(url);

        if (image != null
                && !image.isError()) {

            logoView.setImage(image);
        }
    }

    private boolean validateInput() {

        boolean valid = true;

        /*
         Firm name
         */
        int nameLength =
                firmNameField
                        .getText()
                        .trim()
                        .length();

        if (nameLength == 0) {

            firmNameError.setText(
                    "Firm name not specified!"
            );

            valid = false;

        } else if (
                nameLength < 3
                        || nameLength > 70
        ) {

            firmNameError.setText(
                    "Firm name should be "
                            + "between 3 and 70 characters."
            );

            valid = false;

        } else {

            firmNameError.setText("");
        }

        /*
         Address
         */
        int addressLength =
                addressField
                        .getText()
                        .trim()
                        .length();

        if (addressLength == 0) {

            addressError.setText(
                    "Firm address not specified!"
            );

            valid = false;

        } else if (
                addressLength < 10
                        || addressLength > 120
        ) {

            addressError.setText(
                    "Firm address should be "
                            + "between 10 and 120 characters."
            );

            valid = false;

        } else {

            addressError.setText("");
        }

        /*
         GST validation
         */
        if (gstCheckBox.isSelected()) {

            if (gstNumberField
                    .getText()
                    .trim()
                    .isEmpty()) {

                Utility.beep();

                gstNumberField.requestFocus();

                return false;
            }
        }

        /*
         FSSAI validation
         */
        if (fssaiCheckBox.isSelected()) {

            if (fssaiNumberField
                    .getText()
                    .trim()
                    .isEmpty()) {

                Utility.beep();

                fssaiNumberField.requestFocus();

                return false;
            }
        }

        return valid;
    }

    @Override
    public void setMainWindow(
            Stage stage
    ) {

        MainWindow = stage;
    }

    @Override
    public void setTabPane(
            TabPane pane
    ) {

        this.tabPane = pane;
    }

    private ButtonType shouldSaveUnsavedData() {

        final String promptMessage =
                "The Firm Details data "
                        + "is not saved.\n"
                        + "Save before closing?";

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION,
                        promptMessage,
                        ButtonType.YES,
                        ButtonType.NO,
                        ButtonType.CANCEL
                );

        alert.setHeaderText(
                "Unsaved Firm Details"
        );

        alert.setTitle(
                "Unsaved Firm Details"
        );

        alert.initOwner(MainWindow);

        Global.styleAlertDialog(alert);

        Optional<ButtonType> result =
                alert.showAndWait();

        if (!result.isPresent()) {
            return ButtonType.CANCEL;
        }

        return result.get();
    }

    @FXML
    private void onDeleteLogoAction(
            ActionEvent event
    ) {

        logoBytes = null;

        isDirty.set(true);

        loadDummyLogoFile();

        deleteLogoButton.setDisable(true);
    }
}
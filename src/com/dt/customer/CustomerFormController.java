package com.dt.customer;

import com.dt.dao.DatabaseConnect;

import javafx.fxml.FXML;

import javafx.scene.control.*;

import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerFormController {

    // =====================================================
    // FXML
    // =====================================================

    @FXML
    private ToggleButton btnCash;

    @FXML
    private ToggleButton btnCredit;

    @FXML
    private ToggleButton btnGST;

    @FXML
    private VBox gstSection;

    @FXML
    private VBox creditSection;

    @FXML
    private TextField tfCustomerCode;

    @FXML
    private TextField tfCustomerName;

    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfAltPhone;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfGSTNumber;

    @FXML
    private TextArea tfAddress1;

    @FXML
    private TextArea tfAddress2;

    @FXML
    private TextField tfCity;

    @FXML
    private TextField tfState;

    @FXML
    private TextField tfPincode;

    @FXML
    private TextField tfOpeningBalance;

    @FXML
    private ComboBox<String> cbBalanceType;

    // =====================================================
    // DAO
    // =====================================================

    private CustomerDAO customerDAO;

    // =====================================================
    // DATA
    // =====================================================

    private CustomerModel editCustomer;

    // =====================================================
    // INITIALIZE
    // =====================================================

    private  Connection connection;
    public CustomerFormController(Connection connection){
        try {

            this.connection =
                    DatabaseConnect.getConnection();

            customerDAO =
                    new CustomerDAO(connection);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {


        initializeTypeToggle();

        initializeBalanceType();

        generateCustomerCode();
    }

    // =====================================================
    // TYPE TOGGLE
    // =====================================================

    private void initializeTypeToggle() {

        ToggleGroup group =
                new ToggleGroup();

        btnCash.setToggleGroup(group);

        btnCredit.setToggleGroup(group);

        btnGST.setToggleGroup(group);

        btnCash.setSelected(true);

        group.selectedToggleProperty().addListener((obs,
                                                    oldVal,
                                                    newVal) -> {

            hideSections();

            if (newVal == btnGST) {

                gstSection.setVisible(true);
                gstSection.setManaged(true);
            }

            if (newVal == btnCredit) {

                creditSection.setVisible(true);
                creditSection.setManaged(true);
            }
        });
    }

    private void hideSections() {

        gstSection.setVisible(false);
        gstSection.setManaged(false);

        creditSection.setVisible(false);
        creditSection.setManaged(false);
    }

    // =====================================================
    // BALANCE TYPE
    // =====================================================

    private void initializeBalanceType() {

        cbBalanceType.getItems().addAll(

                "Dr",
                "Cr"
        );

        cbBalanceType.getSelectionModel()
                .select("Dr");
    }

    // =====================================================
    // CODE
    // =====================================================

    private void generateCustomerCode() {

        tfCustomerCode.setText(

                "CUST-"

                        +

                        java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
    }

    // =====================================================
    // SET CUSTOMER
    // =====================================================

    public void setCustomer(CustomerModel customer) {

        this.editCustomer = customer;

        loadCustomerData();
    }

    // =====================================================
    // LOAD DATA
    // =====================================================

    private void loadCustomerData() {

        if (editCustomer == null) {

            return;
        }

        tfCustomerCode.setText(
                editCustomer.getCustomerCode()
        );

        tfCustomerName.setText(
                editCustomer.getCustomerName()
        );

        tfPhone.setText(
                editCustomer.getPhoneNo()
        );

        tfAltPhone.setText(
                editCustomer.getAltPhoneNo()
        );

        tfEmail.setText(
                editCustomer.getEmail()
        );

        tfGSTNumber.setText(
                editCustomer.getGstNumber()
        );

        tfAddress1.setText(
                editCustomer.getAddress1()
        );

        tfAddress2.setText(
                editCustomer.getAddress2()
        );

        tfCity.setText(
                editCustomer.getCity()
        );

        tfState.setText(
                editCustomer.getState()
        );

        tfPincode.setText(
                editCustomer.getPincode()
        );

        tfOpeningBalance.setText(

                String.valueOf(
                        editCustomer.getOpeningBalance()
                )
        );

        cbBalanceType.getSelectionModel()
                .select(
                        editCustomer.getBalanceType()
                );

        // TYPE

        String type =
                editCustomer.getCustomerType();

        if ("GST".equalsIgnoreCase(type)) {

            btnGST.setSelected(true);

            gstSection.setVisible(true);
            gstSection.setManaged(true);
        }

        else if ("Credit".equalsIgnoreCase(type)) {

            btnCredit.setSelected(true);

            creditSection.setVisible(true);
            creditSection.setManaged(true);
        }

        else {

            btnCash.setSelected(true);
        }
    }

    // =====================================================
    // SAVE
    // =====================================================

    @FXML
    private void onSave() {

        if (!validateForm()) {

            return;
        }

        try {

            CustomerModel customer =
                    buildCustomer();

            if (editCustomer == null) {

                customerDAO.insert(customer);

            } else {

                update(customer);
            }

            Alert alert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            alert.setHeaderText(
                    "Customer Saved Successfully"
            );

            alert.showAndWait();

            closeWindow();

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setHeaderText(
                    "Unable To Save Customer"
            );

            alert.setContentText(
                    e.getMessage()
            );

            alert.showAndWait();
        }
    }

    // =====================================================
    // BUILD
    // =====================================================

    private CustomerModel buildCustomer() {

        return new CustomerModel(

                editCustomer == null
                        ? 0
                        : editCustomer.getId(),

                getCustomerType(),

                tfCustomerCode.getText(),

                tfCustomerName.getText(),

                tfGSTNumber.getText(),

                tfPhone.getText(),

                tfAltPhone.getText(),

                tfEmail.getText(),

                tfAddress1.getText(),

                tfAddress2.getText(),

                tfCity.getText(),

                tfState.getText(),

                tfPincode.getText(),

                parseAmount(
                        tfOpeningBalance.getText()
                ),

                cbBalanceType.getValue()
        );
    }

    // =====================================================
    // TYPE
    // =====================================================

    private String getCustomerType() {

        if (btnGST.isSelected()) {

            return "GST";
        }

        if (btnCredit.isSelected()) {

            return "Credit";
        }

        return "Cash";
    }

    // =====================================================
    // VALIDATE
    // =====================================================

    private boolean validateForm() {

        if (tfCustomerName.getText().isBlank()) {

            showValidation(
                    "Customer name required"
            );

            return false;
        }

        if (tfPhone.getText().isBlank()) {

            showValidation(
                    "Phone number required"
            );

            return false;
        }

        if (btnGST.isSelected()
                &&
                tfGSTNumber.getText().isBlank()) {

            showValidation(
                    "GST Number required"
            );

            return false;
        }

        return true;
    }

    // =====================================================
    // VALIDATION ALERT
    // =====================================================

    private void showValidation(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setHeaderText(
                message
        );

        alert.showAndWait();
    }

    // =====================================================
    // PARSE
    // =====================================================

    private double parseAmount(String value) {

        try {

            if (value == null
                    ||
                    value.isBlank()) {

                return 0;
            }

            return Double.parseDouble(
                    value
            );

        } catch (Exception e) {

            return 0;
        }
    }

    // =====================================================
    // CANCEL
    // =====================================================

    @FXML
    private void onCancel() {

        closeWindow();
    }

    // =====================================================
    // CLOSE
    // =====================================================

    private void closeWindow() {

        Stage stage =
                (Stage)

                        tfCustomerName

                                .getScene()

                                .getWindow();

        stage.close();
    }

    private void update(CustomerModel customer)
            throws SQLException {

        String sql = """

            UPDATE CUSTOMER_MASTER

            SET

                CUSTOMER_TYPE=?,
                CUSTOMER_NAME=?,
                GST_NUMBER=?,
                PHONE_NO=?,
                ALT_PHONE_NO=?,
                EMAIL=?,
                ADDRESS1=?,
                ADDRESS2=?,
                CITY=?,
                STATE=?,
                PINCODE=?,
                OPENING_BALANCE=?,
                BALANCE_TYPE=?

            WHERE ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1,
                customer.getCustomerType());

        ps.setString(2,
                customer.getCustomerName());

        ps.setString(3,
                customer.getGstNumber());

        ps.setString(4,
                customer.getPhoneNo());

        ps.setString(5,
                customer.getAltPhoneNo());

        ps.setString(6,
                customer.getEmail());

        ps.setString(7,
                customer.getAddress1());

        ps.setString(8,
                customer.getAddress2());

        ps.setString(9,
                customer.getCity());

        ps.setString(10,
                customer.getState());

        ps.setString(11,
                customer.getPincode());

        ps.setDouble(12,
                customer.getOpeningBalance());

        ps.setString(13,
                customer.getBalanceType());

        ps.setInt(14,
                customer.getId());

        ps.executeUpdate();

        ps.close();
    }
}
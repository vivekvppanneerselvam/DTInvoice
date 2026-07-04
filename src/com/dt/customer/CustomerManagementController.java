package com.dt.customer;

import com.dt.dao.DatabaseConnect;
import com.dt.utils.TabContent;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.*;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerManagementController
        implements TabContent {

    // =====================================================
    // FORM
    // =====================================================

    @FXML
    private ToggleButton btnCash;

    @FXML
    private ToggleButton btnCredit;

    @FXML
    private ToggleButton btnGST;

    @FXML
    private TextField tfCustomerCode;

    @FXML
    private TextField tfCustomerName;

    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfAltPhone;

    @FXML
    private TextField tfGSTNumber;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextArea tfAddress1;

    @FXML
    private TextField tfOpeningBalance;

    @FXML
    private ComboBox<String> cbBalanceType;

    // =====================================================
    // SEARCH
    // =====================================================

    @FXML
    private TextField tfSearch;

    @FXML
    private ComboBox<String> cbType;

    // =====================================================
    // TABLE
    // =====================================================

    @FXML
    private TableView<CustomerModel> customerTable;

    @FXML
    private TableColumn<CustomerModel, String> colCode;

    @FXML
    private TableColumn<CustomerModel, String> colName;

    @FXML
    private TableColumn<CustomerModel, String> colPhone;

    @FXML
    private TableColumn<CustomerModel, String> colType;

    @FXML
    private TableColumn<CustomerModel, String> colGST;

    @FXML
    private TableColumn<CustomerModel, Double> colBalance;

    // =====================================================
    // DATA
    // =====================================================

    private CustomerDAO customerDAO;

    private final ObservableList<CustomerModel> customers =
            FXCollections.observableArrayList();

    private CustomerModel selectedCustomer;

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        System.out.println(
                "Customer Screen Initialize"
        );

        try {

            Connection connection =
                    DatabaseConnect.getConnection();

            customerDAO =
                    new CustomerDAO(connection);

            System.out.println(
                    "Database Connected"
            );

        } catch (SQLException e) {

            e.printStackTrace();

            showError(
                    "Database connection failed"
            );

            return;
        }

        initializeToggle();

        initializeCombo();

        initializeTable();

        initializeTableSelection();

        initializeSearch();

        loadCustomers();

        generateCustomerCode();

        System.out.println(
                "Customer Screen Loaded"
        );
    }

    // =====================================================
    // TOGGLE
    // =====================================================

    private void initializeToggle() {

        ToggleGroup group =
                new ToggleGroup();

        btnCash.setToggleGroup(group);

        btnCredit.setToggleGroup(group);

        btnGST.setToggleGroup(group);

        btnCash.setSelected(true);

        updateToggleStyle();

        group.selectedToggleProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> updateToggleStyle());
    }

    private void updateToggleStyle() {

        String active =

                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-radius: 0;";

        String inactive =

                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: #d1d5db;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-radius: 0;";

        btnCash.setStyle(inactive);

        btnCredit.setStyle(inactive);

        btnGST.setStyle(inactive);

        if (btnCash.isSelected()) {

            btnCash.setStyle(active);
        }

        if (btnCredit.isSelected()) {

            btnCredit.setStyle(active);
        }

        if (btnGST.isSelected()) {

            btnGST.setStyle(active);
        }
    }

    // =====================================================
    // COMBO
    // =====================================================

    private void initializeCombo() {

        cbBalanceType.getItems().addAll(
                "Dr",
                "Cr"
        );

        cbBalanceType.getSelectionModel()
                .select("Dr");

        cbType.getItems().addAll(
                "All",
                "Cash",
                "Credit",
                "GST"
        );

        cbType.getSelectionModel()
                .select("All");
    }

    // =====================================================
    // TABLE
    // =====================================================

    private void initializeTable() {

        colCode.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getCustomerCode()
                )
        );

        colName.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getCustomerName()
                )
        );

        colPhone.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getPhoneNo()
                )
        );

        colType.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getCustomerType()
                )
        );

        colGST.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getGstNumber()
                )
        );

        colBalance.setCellValueFactory(data ->

                new SimpleObjectProperty<>(
                        data.getValue().getOpeningBalance()
                )
        );

        customerTable.setItems(customers);

        customerTable.setRowFactory(tv -> {

            TableRow<CustomerModel> row =
                    new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2
                        &&
                        !row.isEmpty()) {

                    openLedger(
                            row.getItem()
                    );
                }
            });

            return row;
        });
    }

    // =====================================================
    // TABLE SELECTION
    // =====================================================

    private void initializeTableSelection() {

        customerTable.getSelectionModel()

                .selectedItemProperty()

                .addListener((obs,
                              oldVal,
                              customer) -> {

                    if (customer == null) {
                        return;
                    }

                    selectedCustomer = customer;

                    fillForm(customer);
                });
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void initializeSearch() {

        tfSearch.textProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> filterCustomers());

        cbType.valueProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> filterCustomers());
    }

    // =====================================================
    // LOAD
    // =====================================================

    private void loadCustomers() {

        try {

            customers.clear();

            customers.addAll(
                    customerDAO.findAll()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // FILTER
    // =====================================================

    private void filterCustomers() {

        try {

            List<CustomerModel> all =
                    customerDAO.findAll();

            String search =

                    tfSearch.getText()
                            .toLowerCase();

            String type =
                    cbType.getValue();

            List<CustomerModel> filtered =

                    all.stream()

                            .filter(c -> {

                                boolean matchesSearch =

                                        c.getCustomerName()
                                                .toLowerCase()
                                                .contains(search)

                                                ||

                                                c.getPhoneNo()
                                                        .toLowerCase()
                                                        .contains(search)

                                                ||

                                                c.getGstNumber()
                                                        .toLowerCase()
                                                        .contains(search);

                                boolean matchesType =

                                        type.equals("All")

                                                ||

                                                c.getCustomerType()
                                                        .equalsIgnoreCase(type);

                                return matchesSearch
                                        &&
                                        matchesType;
                            })

                            .collect(Collectors.toList());

            customers.setAll(filtered);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // FILL FORM
    // =====================================================

    private void fillForm(CustomerModel customer) {

        tfCustomerCode.setText(
                customer.getCustomerCode()
        );

        tfCustomerName.setText(
                customer.getCustomerName()
        );

        tfPhone.setText(
                customer.getPhoneNo()
        );

        tfAltPhone.setText(
                customer.getAltPhoneNo()
        );

        tfEmail.setText(
                customer.getEmail()
        );

        tfGSTNumber.setText(
                customer.getGstNumber()
        );

        tfAddress1.setText(
                customer.getAddress1()
        );

        tfOpeningBalance.setText(
                String.valueOf(
                        customer.getOpeningBalance()
                )
        );

        cbBalanceType.setValue(
                customer.getBalanceType()
        );

        if ("GST".equalsIgnoreCase(
                customer.getCustomerType()
        )) {

            btnGST.setSelected(true);

        } else if ("Credit".equalsIgnoreCase(
                customer.getCustomerType()
        )) {

            btnCredit.setSelected(true);

        } else {

            btnCash.setSelected(true);
        }

        updateToggleStyle();
    }

    // =====================================================
    // BUILD
    // =====================================================

    private CustomerModel buildCustomer() {

        CustomerModel customer =
                new CustomerModel();

        customer.setCustomerCode(
                tfCustomerCode.getText()
        );

        customer.setCustomerName(
                tfCustomerName.getText()
        );

        customer.setPhoneNo(
                tfPhone.getText()
        );

        customer.setAltPhoneNo(
                tfAltPhone.getText()
        );

        customer.setEmail(
                tfEmail.getText()
        );

        customer.setGstNumber(
                tfGSTNumber.getText()
        );

        customer.setAddress1(
                tfAddress1.getText()
        );

        customer.setOpeningBalance(
                parseDouble(
                        tfOpeningBalance.getText()
                )
        );

        customer.setBalanceType(
                cbBalanceType.getValue()
        );

        if (btnGST.isSelected()) {

            customer.setCustomerType("GST");

        } else if (btnCredit.isSelected()) {

            customer.setCustomerType("Credit");

        } else {

            customer.setCustomerType("Cash");
        }

        return customer;
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

            if (selectedCustomer == null) {

                customerDAO.insert(customer);

            } else {

                customer.setId(
                        selectedCustomer.getId()
                );

                customerDAO.update(customer);
            }

            loadCustomers();

            clearForm();

            showSuccess(
                    "Customer Saved Successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Save Customer"
            );
        }
    }

    // =====================================================
    // DELETE
    // =====================================================

    @FXML
    private void onDelete() {

        if (selectedCustomer == null) {

            showError(
                    "Select customer first"
            );

            return;
        }

        try {

            customerDAO.delete(
                    selectedCustomer.getId()
            );

            loadCustomers();

            clearForm();

            showSuccess(
                    "Customer Deleted"
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Delete"
            );
        }
    }

    // =====================================================
    // RESET
    // =====================================================

    @FXML
    private void onReset() {

        clearForm();
    }

    // =====================================================
    // CLEAR
    // =====================================================

    private void clearForm() {

        selectedCustomer = null;

        tfCustomerCode.clear();

        tfCustomerName.clear();

        tfPhone.clear();

        tfAltPhone.clear();

        tfEmail.clear();

        tfGSTNumber.clear();

        tfAddress1.clear();

        tfOpeningBalance.clear();

        cbBalanceType.getSelectionModel()
                .select("Dr");

        btnCash.setSelected(true);

        updateToggleStyle();

        generateCustomerCode();
    }

    // =====================================================
    // VALIDATE
    // =====================================================

    private boolean validateForm() {

        if (tfCustomerName.getText().isBlank()) {

            showError(
                    "Customer name required"
            );

            return false;
        }

        if (tfPhone.getText().isBlank()) {

            showError(
                    "Phone number required"
            );

            return false;
        }

        if (btnGST.isSelected()
                &&
                tfGSTNumber.getText().isBlank()) {

            showError(
                    "GST Number required"
            );

            return false;
        }

        return true;
    }

    // =====================================================
    // LEDGER
    // =====================================================

    private void openLedger(CustomerModel customer) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(
                "Open Ledger : "
                        +
                        customer.getCustomerName()
        );

        alert.showAndWait();
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private void generateCustomerCode() {

        tfCustomerCode.setText(
                "CUS-" +
                        java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
    }

    private double parseDouble(String value) {

        try {

            if (value == null
                    ||
                    value.isBlank()) {

                return 0;
            }

            return Double.parseDouble(value);

        } catch (Exception e) {

            return 0;
        }
    }

    private void showError(String msg) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(msg);

        alert.showAndWait();
    }

    private void showSuccess(String msg) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(msg);

        alert.showAndWait();
    }

    // =====================================================
    // TAB CONTENT
    // =====================================================

    @Override
    public boolean shouldClose() {
        return true;
    }

    @Override
    public void putFocusOnNode() {
    }

    @Override
    public boolean loadData() {
        return true;
    }

    @Override
    public void setMainWindow(Stage stage) {
    }

    @Override
    public void setTabPane(TabPane tabPane) {
    }
}
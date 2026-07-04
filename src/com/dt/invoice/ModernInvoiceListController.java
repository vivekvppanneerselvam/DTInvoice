package com.dt.invoice;

import com.dt.controller.ModernInvoiceController;
import com.dt.dao.DatabaseConnect;

import com.dt.utils.TabContent;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;

import java.time.LocalDate;

public class ModernInvoiceListController implements TabContent {

    // =====================================================
    // SEARCH
    // =====================================================

    @FXML
    private TextField tfSearch;

    @FXML
    private DatePicker dpFrom;

    @FXML
    private DatePicker dpTo;

    private TabPane tabPane;

    // =====================================================
    // INVOICE TABLE
    // =====================================================

    @FXML
    private TableView<InvoiceModel> invoiceTable;

    @FXML
    private TableColumn<InvoiceModel, String> colInvoiceNo;

    @FXML
    private TableColumn<InvoiceModel, String> colCustomer;

    @FXML
    private TableColumn<InvoiceModel, String> colDate;

    @FXML
    private TableColumn<InvoiceModel, Double> colTotal;

    // =====================================================
    // ITEM TABLE
    // =====================================================

    @FXML
    private TableView<InvoiceItemModel> itemsTable;

    @FXML
    private TableColumn<InvoiceItemModel, String> colItem;

    @FXML
    private TableColumn<InvoiceItemModel, Double> colQty;

    @FXML
    private TableColumn<InvoiceItemModel, Double> colPrice;

    @FXML
    private TableColumn<InvoiceItemModel, Double> colGST;

    @FXML
    private TableColumn<InvoiceItemModel, Double> colItemTotal;

    // =====================================================
    // DETAILS
    // =====================================================

    @FXML
    private Label lblInvoiceNo;

    @FXML
    private Label lblCustomer;

    @FXML
    private Label lblPhone;

    @FXML
    private Label lblGST;

    @FXML
    private Label lblGrandTotal;

    // =====================================================
    // DATA
    // =====================================================

    private final ObservableList<InvoiceModel> invoices =
            FXCollections.observableArrayList();

    private final ObservableList<InvoiceItemModel> invoiceItems =
            FXCollections.observableArrayList();

    private InvoiceDAO invoiceDAO;

    private InvoiceItemDAO invoiceItemDAO;

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        try {

            Connection connection =
                    DatabaseConnect.getConnection();

            invoiceDAO =
                    new InvoiceDAO(connection);

            invoiceItemDAO =
                    new InvoiceItemDAO(connection);

            initializeTables();

            loadTodayInvoices();

            invoiceTable.getSelectionModel()

                    .selectedItemProperty()

                    .addListener((obs, oldVal, newVal) -> {

                        if (newVal != null) {

                            loadInvoiceDetails(newVal);
                        }
                    });

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // TABLES
    // =====================================================

    private void initializeTables() {

        // =========================================
        // INVOICE TABLE
        // =========================================

        colInvoiceNo.setCellValueFactory(data ->

                new SimpleStringProperty(

                        data.getValue()
                                .getInvoiceNo()
                )
        );

        colCustomer.setCellValueFactory(data ->

                new SimpleStringProperty(

                        data.getValue()
                                .getCustomerName()
                )
        );

        colDate.setCellValueFactory(data ->

                new SimpleStringProperty(

                        data.getValue()
                                .getBillDate().toString()
                )
        );

        colTotal.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getGrandTotal()
                )
        );

        invoiceTable.setItems(invoices);

        // =========================================
        // ITEM TABLE
        // =========================================

        colItem.setCellValueFactory(data ->

                new SimpleStringProperty(

                        data.getValue()
                                .getItemName()
                )
        );

        colQty.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getQuantity()
                )
        );

        colPrice.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getPrice()
                )
        );

        colGST.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getGstPercent()
                )
        );

        colItemTotal.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getTotal()
                )
        );

        itemsTable.setItems(invoiceItems);
    }

    // =====================================================
    // LOAD TODAY
    // =====================================================

    private void loadTodayInvoices() {

        try {

            invoices.clear();

            invoices.addAll(

                    invoiceDAO.getTodayInvoices()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // LOAD DETAILS
    // =====================================================

    private void loadInvoiceDetails(InvoiceModel invoice) {

        try {

            lblInvoiceNo.setText(
                    invoice.getInvoiceNo()
            );

            lblCustomer.setText(
                    invoice.getCustomerName()
            );

            lblPhone.setText(
                    invoice.getCustomerPhone()
            );

            lblGST.setText(
                    invoice.getCustomerGST()
            );

            lblGrandTotal.setText(

                    "₹ "

                            +

                            invoice.getGrandTotal()
            );

            invoiceItems.clear();

            invoiceItems.addAll(

                    invoiceItemDAO.getByInvoice(
                            invoice.getId()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // SEARCH
    // =====================================================

    @FXML
    private void onSearch() {

        try {

            String keyword =
                    tfSearch.getText();

            LocalDate from =
                    dpFrom.getValue();

            LocalDate to =
                    dpTo.getValue();

            invoices.clear();

            // =====================================
            // SEARCH BY KEYWORD
            // =====================================

            if (keyword != null

                    &&

                    !keyword.isBlank()) {

                invoices.addAll(

                        invoiceDAO.search(
                                keyword
                        )
                );

                return;
            }

            // =====================================
            // DATE RANGE
            // =====================================

            if (from != null && to != null) {

                invoices.addAll(

                        invoiceDAO.getBetween(
                                from,
                                to
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // TODAY
    // =====================================================

    @FXML
    private void onToday() {

        loadTodayInvoices();
    }

    // =====================================================
    // REFRESH
    // =====================================================

    @FXML
    private void onRefresh() {

        tfSearch.clear();

        dpFrom.setValue(null);

        dpTo.setValue(null);

        loadTodayInvoices();
    }

    // =====================================================
    // DELETE
    // =====================================================

    @FXML
    private void onDelete() {

        try {

            InvoiceModel invoice =

                    invoiceTable.getSelectionModel()
                            .getSelectedItem();

            if (invoice == null) {

                return;
            }

            Alert alert =
                    new Alert(

                            Alert.AlertType.CONFIRMATION
                    );

            alert.setHeaderText(

                    "Delete Invoice "

                            +

                            invoice.getInvoiceNo()

                            +

                            " ?"
            );

            alert.showAndWait()

                    .ifPresent(btn -> {

                        if (btn == ButtonType.OK) {

                            try {

                                invoiceDAO.delete(
                                        invoice.getId()
                                );

                                loadTodayInvoices();

                                invoiceItems.clear();

                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        }
                    });

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // PRINT
    // =====================================================

    @FXML
    private void onPrint() {

        try {

            InvoiceModel invoice =

                    invoiceTable.getSelectionModel()
                            .getSelectedItem();

            if (invoice == null) {
                return;
            }

            // CALL YOUR EXISTING
            // PRINT LOGIC HERE

            System.out.println(

                    "PRINT : "

                            +

                            invoice.getInvoiceNo()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void onEditInvoice() {

        try {

            InvoiceModel invoice =

                    invoiceTable.getSelectionModel()
                            .getSelectedItem();

            if (invoice == null) {

                Alert alert =
                        new Alert(Alert.AlertType.WARNING);

                alert.setHeaderText(
                        "Select Invoice"
                );

                alert.setContentText(
                        "Please select invoice to edit."
                );

                alert.showAndWait();

                return;
            }

            FXMLLoader loader =
                    new FXMLLoader(

                            getClass()

                                    .getResource(
                                            "/com/dt/view/ModernInvoice.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            ModernInvoiceController controller =
                    loader.getController();

            controller.loadInvoiceForEdit(
                    invoice.getId()
            );

            Tab tab =
                    new Tab(

                            "Edit : "

                                    +

                                    invoice.getInvoiceNo()
                    );

            tab.setContent(root);

            tabPane.getTabs().add(tab);

            tabPane.getSelectionModel()
                    .select(tab);

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert =
                    new Alert(Alert.AlertType.ERROR);

            alert.setHeaderText(
                    "Unable To Open Invoice"
            );

            alert.setContentText(
                    e.getMessage()
            );

            alert.showAndWait();
        }
    }

    @Override
    public boolean shouldClose() {
        return false;
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

        this.tabPane = tabPane;
    }
}

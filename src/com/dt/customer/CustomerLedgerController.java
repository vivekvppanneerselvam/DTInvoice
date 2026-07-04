package com.dt.customer;

import com.dt.dao.DatabaseConnect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.paint.Color;

import java.sql.Connection;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerLedgerController {

    // =====================================================
    // FXML
    // =====================================================

    @FXML
    private Label lblCustomerName;

    @FXML
    private Label lblPhone;

    @FXML
    private Label lblTotalSales;

    @FXML
    private Label lblTotalReceived;

    @FXML
    private Label lblOutstanding;

    @FXML
    private DatePicker dpFrom;

    @FXML
    private DatePicker dpTo;

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<CustomerLedgerModel> ledgerTable;

    @FXML
    private TableColumn<CustomerLedgerModel, String> colDate;

    @FXML
    private TableColumn<CustomerLedgerModel, String> colType;

    @FXML
    private TableColumn<CustomerLedgerModel, String> colRef;

    @FXML
    private TableColumn<CustomerLedgerModel, Double> colDebit;

    @FXML
    private TableColumn<CustomerLedgerModel, Double> colCredit;

    @FXML
    private TableColumn<CustomerLedgerModel, Double> colBalance;

    @FXML
    private TableColumn<CustomerLedgerModel, String> colRemarks;

    // =====================================================
    // DAO
    // =====================================================

    private CustomerLedgerDAO ledgerDAO;

    // =====================================================
    // DATA
    // =====================================================

    private CustomerModel customer;

    private final ObservableList<CustomerLedgerModel> ledgerList =
            FXCollections.observableArrayList();

    private final List<CustomerLedgerModel> allEntries =
            new ArrayList<>();

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        try {

            Connection connection =
                    DatabaseConnect.getConnection();

            ledgerDAO =
                    new CustomerLedgerDAO(connection);

        } catch (Exception e) {

            e.printStackTrace();
        }

        initializeTable();

        initializeFilters();
    }

    // =====================================================
    // CUSTOMER
    // =====================================================

    public void setCustomer(CustomerModel customer) {

        this.customer = customer;

        lblCustomerName.setText(
                customer.getCustomerName()
        );

        lblPhone.setText(
                customer.getPhoneNo()
        );

        loadLedger();
    }

    // =====================================================
    // TABLE
    // =====================================================

    private void initializeTable() {

        colDate.setCellValueFactory(
                new PropertyValueFactory<>("entryDate")
        );

        colType.setCellValueFactory(
                new PropertyValueFactory<>("entryType")
        );

        colRef.setCellValueFactory(
                new PropertyValueFactory<>("refNo")
        );

        colDebit.setCellValueFactory(
                new PropertyValueFactory<>("debit")
        );

        colCredit.setCellValueFactory(
                new PropertyValueFactory<>("credit")
        );

        colBalance.setCellValueFactory(
                new PropertyValueFactory<>("balance")
        );

        colRemarks.setCellValueFactory(
                new PropertyValueFactory<>("remarks")
        );

        // ROW COLORS

        ledgerTable.setRowFactory(tv -> {

            TableRow<CustomerLedgerModel> row =
                    new TableRow<>();

            row.itemProperty().addListener((obs,
                                            oldVal,
                                            item) -> {

                if (item == null) {

                    row.setStyle("");

                    return;
                }

                if (item.getDebit() > 0) {

                    row.setStyle("""

                            -fx-background-color: #fef2f2;

                            """);

                } else {

                    row.setStyle("""

                            -fx-background-color: #f0fdf4;

                            """);
                }
            });

            return row;
        });

        // BALANCE COLOR

        colBalance.setCellFactory(col ->

                new TableCell<>() {

                    @Override
                    protected void updateItem(Double value,
                                              boolean empty) {

                        super.updateItem(value, empty);

                        if (empty || value == null) {

                            setText(null);

                            return;
                        }

                        setText("₹ " + value);

                        if (value > 0) {

                            setTextFill(
                                    Color.web("#dc2626")
                            );

                        } else {

                            setTextFill(
                                    Color.web("#16a34a")
                            );
                        }
                    }
                }
        );

        ledgerTable.setItems(ledgerList);
    }

    // =====================================================
    // FILTERS
    // =====================================================

    private void initializeFilters() {

        tfSearch.textProperty().addListener((obs,
                                             oldVal,
                                             newVal) -> {

            filterLedger();
        });

        dpFrom.setValue(
                LocalDate.now().minusMonths(1)
        );

        dpTo.setValue(
                LocalDate.now()
        );
    }

    // =====================================================
    // LOAD
    // =====================================================

    private void loadLedger() {

        try {

            allEntries.clear();

            allEntries.addAll(

                    ledgerDAO.getByCustomer(
                            customer.getId()
                    )
            );

            ledgerList.setAll(allEntries);

            calculateSummary();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // FILTER
    // =====================================================

    @FXML
    private void onFilter() {

        filterLedger();
    }

    private void filterLedger() {

        String keyword =
                tfSearch.getText()
                        .toLowerCase();

        List<CustomerLedgerModel> filtered =

                allEntries.stream()

                        .filter(entry ->

                                entry.getRefNo()
                                        .toLowerCase()
                                        .contains(keyword)

                                        ||

                                        entry.getRemarks()
                                                .toLowerCase()
                                                .contains(keyword)
                        )

                        .collect(Collectors.toList());

        ledgerList.setAll(filtered);

        calculateSummary();
    }

    // =====================================================
    // SUMMARY
    // =====================================================

    private void calculateSummary() {

        double totalSales =

                ledgerList.stream()

                        .mapToDouble(
                                CustomerLedgerModel::getDebit
                        )

                        .sum();

        double totalReceived =

                ledgerList.stream()

                        .mapToDouble(
                                CustomerLedgerModel::getCredit
                        )

                        .sum();

        double outstanding =
                totalSales - totalReceived;

        lblTotalSales.setText(
                "₹ " + totalSales
        );

        lblTotalReceived.setText(
                "₹ " + totalReceived
        );

        lblOutstanding.setText(
                "₹ " + outstanding
        );
    }

    // =====================================================
    // PRINT
    // =====================================================

    @FXML
    private void onPrint() {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setHeaderText(
                "Ledger Print Coming Soon"
        );

        alert.showAndWait();
    }
}
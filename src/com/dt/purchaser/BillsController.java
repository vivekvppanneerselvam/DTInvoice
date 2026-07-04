package com.dt.purchaser;

import com.dt.dao.DatabaseConnect;
import com.dt.utils.TabContent;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class BillsController implements TabContent {

    // =====================================================
    // FILTERS
    // =====================================================

    @FXML
    private DatePicker dpFrom;

    @FXML
    private DatePicker dpTo;

    @FXML
    private ComboBox<String> cbPurchaser;

    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private TextField tfSearch;

    // =====================================================
    // SUMMARY
    // =====================================================

    @FXML
    private Label lblTotalBills;

    @FXML
    private Label lblPendingAmount;

    @FXML
    private Label lblCollected;

    @FXML
    private Label lblOverdue;

    // =====================================================
    // TABLE
    // =====================================================

    @FXML
    private TableView<BillModel> billTable;

    @FXML
    private TableColumn<BillModel,String> colBillNo;

    @FXML
    private TableColumn<BillModel,String> colDate;

    @FXML
    private TableColumn<BillModel,String> colPurchaser;

    @FXML
    private TableColumn<BillModel,Double> colAmount;

    @FXML
    private TableColumn<BillModel,Double> colPaid;

    @FXML
    private TableColumn<BillModel,Double> colBalance;

    @FXML
    private TableColumn<BillModel,String> colStatus;

    @FXML
    private TableColumn<BillModel,Void> colActions;

    // =====================================================
    // VARIABLES
    // =====================================================

    private BillDao billDao;

    private final ObservableList<BillModel> billList =
            FXCollections.observableArrayList();

    private FilteredList<BillModel> filteredBills;

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        try {

            billDao =
                    new BillDao(
                            DatabaseConnect.getConnection()
                    );

            billDao.createTable();

            configureTable();

            loadBills();

            loadFilters();

            setupSearch();

            updateSummary();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // TABLE CONFIG
    // =====================================================

    private void configureTable() {

        colBillNo.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getBillNo()
                )
        );

        colDate.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getBillDate()
                )
        );

        colPurchaser.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getPurchaserName()
                )
        );

        colAmount.setCellValueFactory(
                c -> new SimpleObjectProperty<>(
                        c.getValue().getTotalAmount()
                )
        );

        colPaid.setCellValueFactory(
                c -> new SimpleObjectProperty<>(
                        c.getValue().getPaidAmount()
                )
        );

        colBalance.setCellValueFactory(
                c -> new SimpleObjectProperty<>(
                        c.getValue().getBalanceAmount()
                )
        );

        colStatus.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getPaymentStatus()
                )
        );

        addActionButtons();
    }

    // =====================================================
    // ACTION BUTTONS
    // =====================================================

    private void addActionButtons() {

        colActions.setCellFactory(param ->
                new TableCell<>() {

                    private final Button btnView =
                            new Button("View");

                    private final Button btnPayment =
                            new Button("Payment");

                    private final Button btnPrint =
                            new Button("Print");

                    {
                        btnView.setOnAction(e -> {

                            BillModel bill =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            showBillDetails(bill);
                        });

                        btnPayment.setOnAction(e -> {

                            BillModel bill =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            receivePayment(bill);
                        });

                        btnPrint.setOnAction(e -> {

                            BillModel bill =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            printBill(bill);
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty) {

                            setGraphic(null);

                        } else {

                            setGraphic(
                                    new javafx.scene.layout.HBox(
                                            5,
                                            btnView,
                                            btnPayment,
                                            btnPrint
                                    )
                            );
                        }
                    }
                });
    }

    // =====================================================
    // LOAD
    // =====================================================

    private void loadBills() {

        try {

            billList.clear();

            billList.addAll(
                    billDao.getAll()
            );

            filteredBills =
                    new FilteredList<>(
                            billList,
                            p -> true
                    );

            billTable.setItems(
                    filteredBills
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // FILTERS
    // =====================================================

    private void loadFilters() {

        cbStatus.getItems().addAll(
                "ALL",
                "PAID",
                "PARTIAL",
                "PENDING"
        );

        cbStatus.getSelectionModel()
                .selectFirst();

        cbPurchaser.getItems().clear();

        cbPurchaser.getItems().add("ALL");

        cbPurchaser.getItems().addAll(

                billList.stream()

                        .map(BillModel::getPurchaserName)

                        .distinct()

                        .sorted()

                        .collect(Collectors.toList())
        );

        cbPurchaser.getSelectionModel()
                .selectFirst();
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void setupSearch() {

        tfSearch.textProperty()
                .addListener((obs, oldVal, newVal)
                        -> applyFilter());
    }

    @FXML
    private void onApplyFilter() {

        applyFilter();
    }

    @FXML
    private void onResetFilter() {

        dpFrom.setValue(null);

        dpTo.setValue(null);

        cbStatus.getSelectionModel()
                .select("ALL");

        cbPurchaser.getSelectionModel()
                .select("ALL");

        tfSearch.clear();

        applyFilter();
    }

    private void applyFilter() {

        filteredBills.setPredicate(bill -> {

            String keyword =
                    tfSearch.getText() == null
                            ? ""
                            : tfSearch.getText()
                              .toLowerCase();

            boolean searchMatch =

                    bill.getBillNo()
                            .toLowerCase()
                            .contains(keyword)

                            ||

                            bill.getPurchaserName()
                                    .toLowerCase()
                                    .contains(keyword);

            boolean statusMatch =

                    cbStatus.getValue() == null

                            ||

                            cbStatus.getValue()
                                    .equals("ALL")

                            ||

                            bill.getPaymentStatus()
                                    .equalsIgnoreCase(
                                            cbStatus.getValue()
                                    );

            boolean purchaserMatch =

                    cbPurchaser.getValue() == null

                            ||

                            cbPurchaser.getValue()
                                    .equals("ALL")

                            ||

                            bill.getPurchaserName()
                                    .equals(
                                            cbPurchaser.getValue()
                                    );

            return searchMatch
                    && statusMatch
                    && purchaserMatch;
        });

        updateSummary();
    }

    // =====================================================
    // SUMMARY
    // =====================================================

    private void updateSummary() {

        lblTotalBills.setText(
                String.valueOf(
                        filteredBills.size()
                )
        );

        double pending =

                filteredBills.stream()

                        .mapToDouble(
                                BillModel::getBalanceAmount
                        )

                        .sum();

        lblPendingAmount.setText(
                "₹ " +
                        String.format(
                                "%.2f",
                                pending
                        )
        );

        double collected =

                filteredBills.stream()

                        .mapToDouble(
                                BillModel::getPaidAmount
                        )

                        .sum();

        lblCollected.setText(
                "₹ " +
                        String.format(
                                "%.2f",
                                collected
                        )
        );

        long overdue =

                filteredBills.stream()

                        .filter(
                                b -> b.getBalanceAmount() > 0
                        )

                        .count();

        lblOverdue.setText(
                String.valueOf(
                        overdue
                )
        );
    }

    // =====================================================
    // VIEW
    // =====================================================

    private void showBillDetails(
            BillModel bill) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setHeaderText(
                bill.getBillNo()
        );

        alert.setContentText(
                """
                Purchaser : %s

                Bill Date : %s

                Amount : %.2f

                Paid : %.2f

                Balance : %.2f

                Status : %s
                """
                        .formatted(
                                bill.getPurchaserName(),
                                bill.getBillDate(),
                                bill.getTotalAmount(),
                                bill.getPaidAmount(),
                                bill.getBalanceAmount(),
                                bill.getPaymentStatus()
                        )
        );

        alert.showAndWait();
    }

    private void receivePayment(
            BillModel bill) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setContentText(
                "Payment screen will be implemented."
        );

        alert.showAndWait();
    }

    private void printBill(
            BillModel bill) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setContentText(
                "Printing : " +
                        bill.getBillNo()
        );

        alert.showAndWait();
    }

    @FXML
    private void onAddBill() {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setContentText(
                "Open Purchase Invoice Screen"
        );

        alert.showAndWait();
    }

    public void loadPurchaserBills(
            int purchaserId,
            String purchaserName) {

        try {

            billList.clear();

            billList.addAll(

                    billDao.getByPurchaser(
                            purchaserId
                    )
            );

            billTable.setItems(
                    billList
            );

            cbPurchaser.setValue(
                    purchaserName
            );

            updateSummary();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // TAB CONTENT
    // =====================================================

    @Override
    public void setMainWindow(Stage stage) {
    }

    @Override
    public void setTabPane(TabPane tabPane) {
    }

    @Override
    public boolean loadData() {
        return true;
    }

    @Override
    public boolean shouldClose() {
        return true;
    }

    @Override
    public void putFocusOnNode() {

        if (tfSearch != null) {
            tfSearch.requestFocus();
        }
    }
}
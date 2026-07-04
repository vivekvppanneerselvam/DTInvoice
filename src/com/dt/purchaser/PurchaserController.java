package com.dt.purchaser;

import com.dt.dao.DatabaseConnect;
import com.dt.utils.TabContent;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PurchaserController implements TabContent {

    // =====================================================
    // FORM
    // =====================================================

    @FXML
    private TextField tfCode;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfMobile;

    @FXML
    private TextField tfGST;

    @FXML
    private TextArea tfAddress;

    @FXML
    private TextField tfPending;

    @FXML
    private TextField tfBills;

    @FXML
    private TextField tfPurchase;

    @FXML
    private TextField tfPaid;

    @FXML
    private TextField tfSearch;

    // =====================================================
    // SUMMARY
    // =====================================================

    @FXML
    private Label lblTotalPurchasers;

    @FXML
    private Label lblCreditPurchasers;

    @FXML
    private Label lblPendingAmount;

    // =====================================================
    // TABLE
    // =====================================================

    @FXML
    private TableView<PurchaserModel> purchaserTable;

    @FXML
    private TableColumn<PurchaserModel, String> colCode;

    @FXML
    private TableColumn<PurchaserModel, String> colName;

    @FXML
    private TableColumn<PurchaserModel, String> colMobile;

    @FXML
    private TableColumn<PurchaserModel, String> colGST;

    @FXML
    private TableColumn<PurchaserModel, Double> colPending;

    @FXML
    private TableColumn<PurchaserModel, Integer> colBills;

    @FXML
    private TableColumn<PurchaserModel, Double> colPurchase;

    @FXML
    private TableColumn<PurchaserModel, Double> colPaid;

    @FXML
    private TableColumn<PurchaserModel, String> colLastPurchase;

    // =====================================================
    // VARIABLES
    // =====================================================

    private PurchaserDAO purchaserDAO;

    private final ObservableList<PurchaserModel> purchaserList =
            FXCollections.observableArrayList();

    private PurchaserModel selectedPurchaser;

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        try {

            purchaserDAO =
                    new PurchaserDAO(
                            DatabaseConnect.getConnection()
                    );

            purchaserDAO.createTable();

            configureTable();

            loadPurchasers();

            setupSearch();

            setupSelection();

            generateNextCode();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // TABLE CONFIG
    // =====================================================

    private void configureTable() {

        colCode.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getPurchaserCode()
                )
        );

        colName.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getName()
                )
        );

        colMobile.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getMobile()
                )
        );

        colGST.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getGstin()
                )
        );

        colPending.setCellValueFactory(
                c -> new SimpleObjectProperty<>(
                        c.getValue().getPending()
                )
        );

        colBills.setCellValueFactory(
                c -> new SimpleObjectProperty<>(
                        c.getValue().getBills()
                )
        );

        colPurchase.setCellValueFactory(
                c -> new SimpleObjectProperty<>(
                        c.getValue().getTotalPurchaseAmount()
                )
        );

        colPaid.setCellValueFactory(
                c -> new SimpleObjectProperty<>(
                        c.getValue().getTotalPaidAmount()
                )
        );

        colLastPurchase.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getLastPurchase()
                )
        );
    }

    // =====================================================
    // LOAD
    // =====================================================

    private void loadPurchasers() {

        try {

            purchaserList.clear();

            purchaserList.addAll(
                    purchaserDAO.getAll()
            );

            purchaserTable.setItems(
                    purchaserList
            );

            updateSummary();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void setupSearch() {

        FilteredList<PurchaserModel> filtered =
                new FilteredList<>(
                        purchaserList,
                        p -> true
                );

        tfSearch.textProperty()
                .addListener((obs, oldVal, keyword) -> {

                    filtered.setPredicate(p -> {

                        if (keyword == null ||
                                keyword.isBlank()) {

                            return true;
                        }

                        String k =
                                keyword.toLowerCase();

                        return

                                p.getName()
                                        .toLowerCase()
                                        .contains(k)

                                        ||

                                        p.getMobile()
                                                .toLowerCase()
                                                .contains(k)

                                        ||

                                        p.getGstin()
                                                .toLowerCase()
                                                .contains(k);
                    });
                });

        purchaserTable.setItems(filtered);
        purchaserTable.setRowFactory(tv -> {

            TableRow<PurchaserModel> row =
                    new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2
                        && !row.isEmpty()) {

                    PurchaserModel purchaser =
                            row.getItem();

                    openPurchaserBills(purchaser);
                }
            });

            return row;
        });
    }

    private void openPurchaserBills(
            PurchaserModel purchaser) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/dt/purchaser/BillsManagement.fxml"
                            )
                    );

            Parent root = loader.load();

            BillsController controller =
                    loader.getController();

            controller.loadPurchaserBills(
                    purchaser.getId(),
                    purchaser.getName()
            );

            Stage stage = new Stage();

            stage.setTitle(
                    purchaser.getName() + " Bills"
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // ROW SELECT
    // =====================================================

    private void setupSelection() {

        purchaserTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, purchaser) -> {

                    if (purchaser == null)
                        return;

                    selectedPurchaser =
                            purchaser;

                    tfCode.setText(
                            purchaser.getPurchaserCode()
                    );

                    tfName.setText(
                            purchaser.getName()
                    );

                    tfMobile.setText(
                            purchaser.getMobile()
                    );

                    tfGST.setText(
                            purchaser.getGstin()
                    );

                    tfAddress.setText(
                            purchaser.getAddress()
                    );

                    tfPending.setText(
                            String.valueOf(
                                    purchaser.getPending()
                            )
                    );

                    tfBills.setText(
                            String.valueOf(
                                    purchaser.getBills()
                            )
                    );

                    tfPurchase.setText(
                            String.valueOf(
                                    purchaser.getTotalPurchaseAmount()
                            )
                    );

                    tfPaid.setText(
                            String.valueOf(
                                    purchaser.getTotalPaidAmount()
                            )
                    );
                });
    }

    // =====================================================
    // SAVE
    // =====================================================

    @FXML
    private void onSave() {

        try {

            if (tfName.getText().isBlank()) {

                showError(
                        "Purchaser Name Required"
                );

                return;
            }

            PurchaserModel purchaser =
                    new PurchaserModel(

                            selectedPurchaser == null
                                    ? 0
                                    : selectedPurchaser.getId(),

                            tfCode.getText(),

                            tfName.getText(),

                            tfGST.getText(),

                            tfMobile.getText(),

                            tfAddress.getText(),

                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            ""
                    );

            if (selectedPurchaser == null) {

                purchaserDAO.insert(
                        purchaser
                );

            } else {

                showInfo(
                        "Update functionality will be added after DAO update method enhancement."
                );
            }

            loadPurchasers();

            onReset();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    e.getMessage()
            );
        }
    }

    // =====================================================
    // DELETE
    // =====================================================

    @FXML
    private void onDelete() {

        if (selectedPurchaser == null) {

            showError(
                    "Select Purchaser First"
            );

            return;
        }

        showInfo(
                "Delete method will be added in DAO."
        );
    }

    // =====================================================
    // RESET
    // =====================================================

    @FXML
    private void onReset() {

        selectedPurchaser = null;

        tfName.clear();
        tfMobile.clear();
        tfGST.clear();
        tfAddress.clear();

        tfPending.clear();
        tfBills.clear();
        tfPurchase.clear();
        tfPaid.clear();

        purchaserTable
                .getSelectionModel()
                .clearSelection();

        generateNextCode();
    }

    // =====================================================
    // CODE GENERATOR
    // =====================================================

    private void generateNextCode() {

        tfCode.setText(
                "PUR-" +
                        String.format(
                                "%04d",
                                purchaserList.size() + 1
                        )
        );
    }

    // =====================================================
    // SUMMARY
    // =====================================================

    private void updateSummary() {

        lblTotalPurchasers.setText(
                String.valueOf(
                        purchaserList.size()
                )
        );

        long creditCount = purchaserList.stream()
                .filter(p ->
                        p.getPending() > 0
                )
                .count();

        lblCreditPurchasers.setText(
                String.valueOf(
                        creditCount
                )
        );

        double totalPending =
                purchaserList.stream()
                        .mapToDouble(
                                PurchaserModel::getPending
                        )
                        .sum();

        lblPendingAmount.setText(
                "₹ " +
                        String.format(
                                "%.2f",
                                totalPending
                        )
        );
    }

    // =====================================================
    // UTIL
    // =====================================================

    private void showInfo(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    private void showError(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setContentText(
                message
        );

        alert.showAndWait();
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
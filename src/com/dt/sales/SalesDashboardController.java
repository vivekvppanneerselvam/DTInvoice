package com.dt.sales;

import com.dt.dao.DatabaseConnect;

import com.dt.purchaser.PurchaserDAO;
import com.dt.utils.TabContent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.*;

import java.sql.Connection;

import java.time.LocalDate;

import java.time.LocalTime;

import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import java.util.Map;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class SalesDashboardController implements TabContent  {

    // =====================================================
    // DAO
    // =====================================================

    private Connection connection;

    private SalesAnalyticsDAO analyticsDAO;

    // =====================================================
    // DATA
    // =====================================================

    private final ObservableList<SalesAnalyticsModel>
            salesList =
            FXCollections.observableArrayList();

    private String fromDate;

    private String toDate;

    // =====================================================
    // FILTERS
    // =====================================================

    @FXML
    private ToggleButton btnToday;

    @FXML
    private ToggleButton btnYesterday;

    @FXML
    private ToggleButton btnWeek;

    @FXML
    private ToggleButton btnMonth;

    @FXML
    private ToggleButton btn30Days;

    @FXML
    private ToggleButton btnYear;

    @FXML
    private ToggleButton btnFY;

    @FXML
    private ToggleButton btnCustom;

    @FXML
    private DatePicker dpFrom;

    @FXML
    private DatePicker dpTo;

    // =====================================================
    // SUMMARY RIBBON
    // =====================================================

    @FXML
    private Label lblSales;

    @FXML
    private Label lblGrossProfit;

    @FXML
    private Label lblNetProfit;

    @FXML
    private Label lblBills;

    @FXML
    private Label lblOutstanding;

    @FXML
    private Label lblExpenses;

    @FXML
    private Label lblSalesGrowth;

    @FXML
    private Label lblProfitGrowth;

    // =====================================================
// SALES TABLE
// =====================================================

    @FXML
    private TableView<SalesAnalyticsModel> salesTable;

    @FXML
    private TableColumn<SalesAnalyticsModel,String> colInvoice;

    @FXML
    private TableColumn<SalesAnalyticsModel,String> colCustomer;

    @FXML
    private TableColumn<SalesAnalyticsModel,String> colSaleType;

    @FXML
    private TableColumn<SalesAnalyticsModel,String> colPayment;

    @FXML
    private TableColumn<SalesAnalyticsModel,Double> colSales;

    @FXML
    private TableColumn<SalesAnalyticsModel,Double> colProfit;

    @FXML
    private TableColumn<SalesAnalyticsModel,Double> colGST;

    @FXML
    private TableColumn<SalesAnalyticsModel,Double> colBalance;

    @FXML
    private TableColumn<SalesAnalyticsModel,String> colDate;

// =====================================================
// TOP CUSTOMERS
// =====================================================

    @FXML
    private TableView<TopCustomerModel> topCustomersTable;

    @FXML
    private TableColumn<TopCustomerModel,Integer> colCustomerRank;

    @FXML
    private TableColumn<TopCustomerModel,String> colCustomerName;

    @FXML
    private TableColumn<TopCustomerModel,Integer> colCustomerBills;

    @FXML
    private TableColumn<TopCustomerModel,Double> colCustomerSales;

// =====================================================
// TOP PURCHASERS
// =====================================================

    @FXML
    private TableView<TopPurchaserModel> topPurchasersTable;

    @FXML
    private TableColumn<TopPurchaserModel,Integer> colPurchaserRank;

    @FXML
    private TableColumn<TopPurchaserModel,String> colPurchaserName;

    @FXML
    private TableColumn<TopPurchaserModel,Integer> colPurchaseBills;

    @FXML
    private TableColumn<TopPurchaserModel,Double> colPurchaseValue;

    // =====================================================
    // FOOTER
    // =====================================================

    @FXML
    private Label lblFooterSales;

    @FXML
    private Label lblFooterProfit;

    @FXML
    private Label lblFooterExpense;

    @FXML
    private Label lblFooterGST;

    @FXML
    private Label lblFooterOutstanding;

    @FXML
    private Label lblFooterAvgBill;

    @FXML
    private Label lblFooterRecovery;

    @FXML
    private Label lblLastRefresh;

    // =====================================================
    // CHARTS
    // =====================================================

    @FXML
    private LineChart<String, Number> salesTrendChart;

    @FXML
    private PieChart paymentChart;

    @FXML
    private PieChart gstChart;

    @FXML
    private PieChart categoryChart;

    @FXML
    private ProgressIndicator businessHealthIndicator;

    @FXML
    private Label lblHealthScore;

    @FXML
    private Label lblHealthStatus;

    // =====================================================
// CHARTS
// =====================================================



// =====================================================
// PAYMENT LABELS
// =====================================================

    @FXML
    private Label lblUPIAmount;

    @FXML
    private Label lblCashAmount;

    @FXML
    private Label lblCardAmount;

    @FXML
    private Label lblCreditAmount;

    @FXML
    private Label lblTopPaymentMode;

    @FXML
    private Label lblTopPaymentPercent;

// =====================================================
// GST LABELS
// =====================================================

    @FXML
    private Label lblGSTBills;

    @FXML
    private Label lblNonGSTBills;

    @FXML
    private Label lblGSTSales;

    @FXML
    private Label lblGSTCollected;


    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        try {

            connection =
                    DatabaseConnect.getConnection();


            analyticsDAO =
                    new SalesAnalyticsDAO(
                            connection
                    );

            setDefaultDates();

            loadDashboard();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // DEFAULT DATES
    // =====================================================

    private void setDefaultDates() {

        LocalDate today =
                LocalDate.now();

        fromDate =
                today.toString();

        toDate =
                today.toString();

        dpFrom.setValue(today);

        dpTo.setValue(today);
    }

    // =====================================================
    // LOAD DASHBOARD
    // =====================================================

    private void loadDashboard() {

        try {

            DashboardSummaryModel summary =

                    analyticsDAO
                            .getDashboardSummary(
                                    fromDate,
                                    toDate
                            );

            loadSummary(summary);

            loadFooter(summary);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // SUMMARY RIBBON
    // =====================================================

    private void loadSummary(
            DashboardSummaryModel summary) {

        lblSales.setText(
                "₹ " +
                        format(
                                summary.getTotalSales()
                        )
        );

        lblGrossProfit.setText(
                "₹ " +
                        format(
                                summary.getGrossProfit()
                        )
        );

        lblNetProfit.setText(
                "₹ " +
                        format(
                                summary.getNetProfit()
                        )
        );

        lblBills.setText(
                String.valueOf(
                        summary.getTotalInvoices()
                )
        );

        lblOutstanding.setText(
                "₹ " +
                        format(
                                summary.getOutstandingAmount()
                        )
        );

        lblExpenses.setText(
                "₹ " +
                        format(
                                summary.getTotalExpense()
                        )
        );

        lblSalesGrowth.setText(
                "Current Period"
        );

        lblProfitGrowth.setText(
                summary.getHealthStatus()
        );
    }

    // =====================================================
    // FOOTER SUMMARY
    // =====================================================

    private void loadFooter(
            DashboardSummaryModel summary) {

        lblFooterSales.setText(
                "₹ " +
                        format(
                                summary.getTotalSales()
                        )
        );

        lblFooterProfit.setText(
                "₹ " +
                        format(
                                summary.getGrossProfit()
                        )
        );

        lblFooterExpense.setText(
                "₹ " +
                        format(
                                summary.getTotalExpense()
                        )
        );

        lblFooterGST.setText(
                "₹ " +
                        format(
                                summary.getTotalGST()
                        )
        );

        lblFooterOutstanding.setText(
                "₹ " +
                        format(
                                summary.getOutstandingAmount()
                        )
        );

        lblFooterAvgBill.setText(
                "₹ " +
                        format(
                                summary.getAverageBillValue()
                        )
        );

        lblFooterRecovery.setText(

                String.format(
                        "%.1f%%",
                        summary.getRecoveryPercentage()
                )
        );

        lblLastRefresh.setText(

                "Updated : " +

                        LocalTime.now()
                                .withNano(0)
        );
    }

    // =====================================================
    // FORMAT
    // =====================================================

    private String format(
            double value) {

        return String.format(
                "%,.2f",
                value
        );

}



// =====================================================
// TABLE INITIALIZATION
// =====================================================

private void initializeTables() {

    // SALES TABLE

    colInvoice.setCellValueFactory(
            data -> new SimpleStringProperty(
                    data.getValue().getInvoiceNo()
            ));

    colCustomer.setCellValueFactory(
            data -> new SimpleStringProperty(
                    data.getValue().getCustomerName()
            ));

    colSaleType.setCellValueFactory(
            data -> new SimpleStringProperty(
                    data.getValue().getSaleType()
            ));

    colPayment.setCellValueFactory(
            data -> new SimpleStringProperty(
                    data.getValue().getPaymentMode()
            ));

    colSales.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getGrandTotal()
            ));

    colProfit.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getProfit()
            ));

    colGST.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getGstTotal()
            ));

    colBalance.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getBalanceAmount()
            ));

    colDate.setCellValueFactory(
            data -> new SimpleStringProperty(
                    data.getValue().getBillDate()
            ));

    // TOP CUSTOMERS

    colCustomerRank.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getRank()
            ));

    colCustomerName.setCellValueFactory(
            data -> new SimpleStringProperty(
                    data.getValue().getCustomerName()
            ));

    colCustomerBills.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getTotalBills()
            ));

    colCustomerSales.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getTotalSales()
            ));

    // TOP PURCHASERS

    colPurchaserRank.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getRank()
            ));

    colPurchaserName.setCellValueFactory(
            data -> new SimpleStringProperty(
                    data.getValue().getPurchaserName()
            ));

    colPurchaseBills.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getBillCount()
            ));

    colPurchaseValue.setCellValueFactory(
            data -> new SimpleObjectProperty<>(
                    data.getValue().getPurchaseAmount()
            ));

    // SALES TABLE ROW HIGHLIGHT

    salesTable.setRowFactory(tv -> {

        TableRow<SalesAnalyticsModel> row =
                new TableRow<>();

        row.itemProperty().addListener(
                (obs, oldVal, item) -> {

                    if (item == null) {

                        row.setStyle("");

                    } else if (item.getBalanceAmount() > 0) {

                        row.setStyle(
                                "-fx-background-color:#fff7ed;"
                        );

                    } else {

                        row.setStyle("");
                    }
                });

        return row;
    });
}

// =====================================================
// LOAD SALES DATA
// =====================================================

private void loadSalesData() {

    try {

        salesList.clear();

        List<SalesAnalyticsModel> data =
                analyticsDAO.getCustomSales(
                        fromDate,
                        toDate
                );

        salesList.addAll(data);

        salesTable.setItems(salesList);

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// LOAD TOP CUSTOMERS
// =====================================================

private void loadTopCustomers() {

    try {

        List<TopCustomerModel> customers =
                analyticsDAO.getTopCustomers(
                        fromDate,
                        toDate
                );

        topCustomersTable.setItems(
                FXCollections.observableArrayList(
                        customers
                )
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// LOAD TOP PURCHASERS
// =====================================================

private void loadTopPurchasers() {

    try {

        List<TopPurchaserModel> purchasers =
                analyticsDAO.getTopPurchasers();

        topPurchasersTable.setItems(
                FXCollections.observableArrayList(
                        purchasers
                )
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// UPDATE loadDashboard()
// =====================================================





// =====================================================
// LOAD CHARTS
// =====================================================

private void loadAnalyticsCharts(
        DashboardSummaryModel summary) {

    loadSalesTrendChart();

    loadPaymentChart();

    loadGSTChart();

    loadCategoryChart();

    loadBusinessHealth(summary);
}

// =====================================================
// SALES TREND
// =====================================================

private void loadSalesTrendChart() {

    try {

        salesTrendChart.getData().clear();

        XYChart.Series<String, Number> salesSeries =
                new XYChart.Series<>();

        salesSeries.setName("Sales");

        XYChart.Series<String, Number> profitSeries =
                new XYChart.Series<>();

        profitSeries.setName("Profit");

        XYChart.Series<String, Number> purchaseSeries =
                new XYChart.Series<>();

        purchaseSeries.setName("Purchase");

        List<SalesAnalyticsModel> sales =

                analyticsDAO.getCustomSales(
                        fromDate,
                        toDate
                );

        for (SalesAnalyticsModel s : sales) {

            salesSeries.getData().add(

                    new XYChart.Data<>(

                            s.getBillDate(),

                            s.getGrandTotal()
                    )
            );

            profitSeries.getData().add(

                    new XYChart.Data<>(

                            s.getBillDate(),

                            s.getProfit()
                    )
            );

            purchaseSeries.getData().add(

                    new XYChart.Data<>(

                            s.getBillDate(),

                            s.getPurchaseTotal()
                    )
            );
        }

        salesTrendChart.getData().addAll(

                salesSeries,

                profitSeries,

                purchaseSeries
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// PAYMENT ANALYSIS
// =====================================================

private void loadPaymentChart() {

    try {

        paymentChart.getData().clear();

        Map<String, Double> map =

                analyticsDAO.getPaymentAnalysis(
                        fromDate,
                        toDate
                );

        double total = 0;

        String topMode = "";

        double topAmount = 0;

        for (Map.Entry<String, Double> entry :
                map.entrySet()) {

            paymentChart.getData().add(

                    new PieChart.Data(

                            entry.getKey(),

                            entry.getValue()
                    )
            );

            total += entry.getValue();

            if (entry.getValue() > topAmount) {

                topAmount =
                        entry.getValue();

                topMode =
                        entry.getKey();
            }

            switch (entry.getKey().toUpperCase()) {

                case "UPI" ->

                        lblUPIAmount.setText(

                                "₹ " +
                                        format(
                                                entry.getValue()
                                        )
                        );

                case "CASH" ->

                        lblCashAmount.setText(

                                "₹ " +
                                        format(
                                                entry.getValue()
                                        )
                        );

                case "CARD" ->

                        lblCardAmount.setText(

                                "₹ " +
                                        format(
                                                entry.getValue()
                                        )
                        );

                case "CREDIT" ->

                        lblCreditAmount.setText(

                                "₹ " +
                                        format(
                                                entry.getValue()
                                        )
                        );
            }
        }

        lblTopPaymentMode.setText(
                topMode
        );

        double percentage =

                total == 0

                        ? 0

                        : (topAmount / total) * 100;

        lblTopPaymentPercent.setText(

                String.format(
                        "%.1f%%",
                        percentage
                )
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// GST ANALYSIS
// =====================================================

private void loadGSTChart() {

    try {

        gstChart.getData().clear();

        Map<String, Double> gstData =

                analyticsDAO.getGSTAnalysis(
                        fromDate,
                        toDate
                );

        for (Map.Entry<String, Double> entry :
                gstData.entrySet()) {

            gstChart.getData().add(

                    new PieChart.Data(

                            entry.getKey(),

                            entry.getValue()
                    )
            );
        }

        DashboardSummaryModel summary =

                analyticsDAO.getDashboardSummary(
                        fromDate,
                        toDate
                );

        lblGSTBills.setText(

                String.valueOf(
                        summary.getGstBills()
                )
        );

        lblNonGSTBills.setText(

                String.valueOf(
                        summary.getNonGstBills()
                )
        );

        lblGSTSales.setText(

                "₹ " +

                        format(

                                gstData.getOrDefault(
                                        "GST Sales",
                                        0.0
                                )
                        )
        );

        lblGSTCollected.setText(

                "₹ " +

                        format(
                                summary.getTotalGST()
                        )
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// CATEGORY ANALYSIS
// =====================================================

private void loadCategoryChart() {

    try {

        categoryChart.getData().clear();

        List<CategorySalesModel> categories =

                analyticsDAO.getCategorySales(
                        fromDate,
                        toDate
                );

        for (CategorySalesModel category :
                categories) {

            categoryChart.getData().add(

                    new PieChart.Data(

                            category.getCategoryName(),

                            category.getSalesAmount()
                    )
            );
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// BUSINESS HEALTH
// =====================================================

private void loadBusinessHealth(
        DashboardSummaryModel summary) {

    try {

        HealthScoreModel health =

                analyticsDAO.getBusinessHealth(
                        summary
                );

        businessHealthIndicator.setProgress(

                health.getOverallScore()
                        / 100.0
        );

        lblHealthScore.setText(

                String.format(
                        "%.0f / 100",
                        health.getOverallScore()
                )
        );

        lblHealthStatus.setText(
                health.getStatus()
        );

        if ("Excellent".equalsIgnoreCase(
                health.getStatus())) {

            lblHealthStatus.setStyle(
                    "-fx-text-fill:#16a34a;-fx-font-weight:bold;"
            );

        } else if ("Good".equalsIgnoreCase(
                health.getStatus())) {

            lblHealthStatus.setStyle(
                    "-fx-text-fill:#2563eb;-fx-font-weight:bold;"
            );

        } else if ("Average".equalsIgnoreCase(
                health.getStatus())) {

            lblHealthStatus.setStyle(
                    "-fx-text-fill:#f59e0b;-fx-font-weight:bold;"
            );

        } else {

            lblHealthStatus.setStyle(
                    "-fx-text-fill:#dc2626;-fx-font-weight:bold;"
            );
        }

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// INSIGHTS PANEL
// =====================================================

@FXML
private ListView<BusinessInsightModel> insightsList;

// =====================================================
// LOAD INSIGHTS
// =====================================================

private void loadInsights(
        DashboardSummaryModel summary) {

    try {

        List<BusinessInsightModel> insights =

                analyticsDAO.getBusinessInsights(
                        summary
                );

        insightsList.setItems(

                FXCollections.observableArrayList(
                        insights
                )
        );

        insightsList.setCellFactory(
                lv -> new ListCell<>() {

                    @Override
                    protected void updateItem(
                            BusinessInsightModel item,
                            boolean empty) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty || item == null) {

                            setText(null);

                            setGraphic(null);

                            return;
                        }

                        setText(

                                item.getIcon()

                                        + " "

                                        + item.getTitle()

                                        + "\n"

                                        + item.getDescription()
                        );
                    }
                });

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// TODAY FILTER
// =====================================================

@FXML
private void onToday() {

    LocalDate today =
            LocalDate.now();

    fromDate =
            today.toString();

    toDate =
            today.toString();

    dpFrom.setValue(today);

    dpTo.setValue(today);

    refreshDashboard();
}

// =====================================================
// WEEK FILTER
// =====================================================

@FXML
private void onWeek() {

    LocalDate today =
            LocalDate.now();

    fromDate =
            today.minusDays(7)
                    .toString();

    toDate =
            today.toString();

    dpFrom.setValue(
            today.minusDays(7)
    );

    dpTo.setValue(today);

    refreshDashboard();
}

// =====================================================
// MONTH FILTER
// =====================================================

@FXML
private void onMonth() {

    LocalDate today =
            LocalDate.now();

    fromDate =
            today.withDayOfMonth(1)
                    .toString();

    toDate =
            today.toString();

    dpFrom.setValue(
            today.withDayOfMonth(1)
    );

    dpTo.setValue(today);

    refreshDashboard();
}

// =====================================================
// LAST 30 DAYS
// =====================================================

@FXML
private void onLast30Days() {

    LocalDate today =
            LocalDate.now();

    fromDate =
            today.minusDays(30)
                    .toString();

    toDate =
            today.toString();

    dpFrom.setValue(
            today.minusDays(30)
    );

    dpTo.setValue(today);

    refreshDashboard();
}

// =====================================================
// YEAR FILTER
// =====================================================

@FXML
private void onYear() {

    LocalDate today =
            LocalDate.now();

    fromDate =
            today.withDayOfYear(1)
                    .toString();

    toDate =
            today.toString();

    dpFrom.setValue(
            today.withDayOfYear(1)
    );

    dpTo.setValue(today);

    refreshDashboard();
}

// =====================================================
// CUSTOM FILTER
// =====================================================

@FXML
private void onFilter() {

    if (dpFrom.getValue() == null ||
            dpTo.getValue() == null) {

        showWarning(
                "Select Date Range"
        );

        return;
    }

    fromDate =
            dpFrom.getValue()
                    .toString();

    toDate =
            dpTo.getValue()
                    .toString();

    refreshDashboard();
}

// =====================================================
// REFRESH DASHBOARD
// =====================================================

@FXML
private void onRefresh() {

    refreshDashboard();
}

private void refreshDashboard() {

    try {

        DashboardSummaryModel summary =

                analyticsDAO
                        .getDashboardSummary(
                                fromDate,
                                toDate
                        );

        loadSummary(summary);

        loadFooter(summary);

        loadSalesData();

        loadTopCustomers();

        loadTopPurchasers();

        loadAnalyticsCharts(summary);

        loadInsights(summary);

        lblLastRefresh.setText(

                "Updated : "

                        + LocalTime.now()
                        .withNano(0)
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

// =====================================================
// EXPORT EXCEL
// =====================================================

@FXML
private void onExportExcel() {

    Alert alert =
            new Alert(
                    Alert.AlertType.INFORMATION
            );

    alert.setHeaderText(
            "Export"
    );

    alert.setContentText(
            "Excel Export Coming Soon"
    );

    alert.showAndWait();
}

// =====================================================
// PRINT REPORT
// =====================================================

@FXML
private void onPrintReport() {

    Alert alert =
            new Alert(
                    Alert.AlertType.INFORMATION
            );

    alert.setHeaderText(
            "Print"
    );

    alert.setContentText(
            "Print Dashboard Coming Soon"
    );

    alert.showAndWait();
}

// =====================================================
// EXPORT REPORT
// =====================================================

@FXML
private void onExport() {

    onExportExcel();
}

// =====================================================
// WARNING
// =====================================================

private void showWarning(
        String message) {

    Alert alert =
            new Alert(
                    Alert.AlertType.WARNING
            );

    alert.setHeaderText(
            "Warning"
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


    }
}




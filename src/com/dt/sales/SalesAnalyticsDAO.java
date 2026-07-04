package com.dt.sales;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SalesAnalyticsDAO {

    private final Connection connection;

    public SalesAnalyticsDAO(Connection connection) {

        this.connection = connection;

        createAnalyticsView();
    }
    private List<SalesAnalyticsModel> buildList(
            ResultSet rs)
            throws SQLException {

        List<SalesAnalyticsModel> list =
                new ArrayList<>();

        while (rs.next()) {

            list.add(

                    new SalesAnalyticsModel(

                            rs.getInt("ID"),

                            rs.getString("INVOICE_NO"),

                            rs.getString("CUSTOMER_NAME"),

                            rs.getString("SALE_TYPE"),

                            rs.getString("PAYMENT_MODE"),

                            rs.getString("BILL_DATE"),

                            rs.getDouble("GRAND_TOTAL"),

                            rs.getDouble("PAID_AMOUNT"),

                            rs.getDouble("BALANCE_AMOUNT"),

                            rs.getDouble("GST_TOTAL"),

                            rs.getDouble("PURCHASE_TOTAL"),

                            rs.getDouble("PROFIT"),

                            rs.getDouble("PROFIT_PERCENTAGE"),

                            rs.getInt("GST_ENABLED") == 1,

                            rs.getInt("CREDIT_SALE") == 1
                    )
            );
        }

        return list;
    }

    private void createAnalyticsView() {

        try {
            debugPurchaseCalculation();
            Statement stmt =
                    connection.createStatement();

            stmt.execute(
                    "DROP VIEW IF EXISTS SALES_ANALYTICS_VIEW"
            );

            String sql = """


CREATE VIEW SALES_ANALYTICS_VIEW AS

SELECT

    i.ID,

    i.CUSTOMER_ID,

    i.INVOICE_NO,

    COALESCE(
        c.CUSTOMER_NAME,
        'Walk-In'
    ) AS CUSTOMER_NAME,

    i.SALE_TYPE,

    i.PAYMENT_MODE,

    i.BILL_DATE,

    i.GRAND_TOTAL,

    i.PAID_AMOUNT,

    i.BALANCE_AMOUNT,

    i.GST_TOTAL,

    CASE
        WHEN i.GST_TOTAL > 0 THEN 1
        ELSE 0
    END AS GST_ENABLED,

    CASE
        WHEN i.BALANCE_AMOUNT > 0 THEN 1
        ELSE 0
    END AS CREDIT_SALE,

    COALESCE(
        cost.PURCHASE_TOTAL,
        0
    ) AS PURCHASE_TOTAL,

    (
        i.GRAND_TOTAL
        -
        COALESCE(
            cost.PURCHASE_TOTAL,
            0
        )
    ) AS PROFIT,

    CASE

        WHEN i.GRAND_TOTAL = 0

        THEN 0

        ELSE

            (
                (
                    i.GRAND_TOTAL
                    -
                    COALESCE(
                        cost.PURCHASE_TOTAL,
                        0
                    )
                )
                /
                i.GRAND_TOTAL
            ) * 100

    END AS PROFIT_PERCENTAGE

FROM INVOICES i

LEFT JOIN CUSTOMER_MASTER c
ON c.ID = i.CUSTOMER_ID

LEFT JOIN (

    SELECT

        ii.INVOICE_ID,

        SUM(

            ii.QUANTITY

            *

            COALESCE(

                (

                    SELECT im.PURCHASE_PRICE

                    FROM ITEM_MEASUREMENTS im

                    WHERE im.ITEM_ID = ii.ITEM_ID

                    ORDER BY im.ID

                    LIMIT 1

                ),

                0

            )

        ) AS PURCHASE_TOTAL

    FROM INVOICE_ITEMS ii

    GROUP BY ii.INVOICE_ID

) cost

ON cost.INVOICE_ID = i.ID;

        """;

            stmt.execute(sql);

            stmt.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    // =====================================================
    // DASHBOARD SUMMARY
    // =====================================================

    public DashboardSummaryModel getDashboardSummary(
            String fromDate,
            String toDate)
            throws SQLException {

        DashboardSummaryModel summary =
                new DashboardSummaryModel();

        String sql = """

SELECT

    COUNT(*) TOTAL_INVOICES,

    COALESCE(SUM(GRAND_TOTAL),0) TOTAL_SALES,

    COALESCE(SUM(PURCHASE_TOTAL),0) TOTAL_PURCHASE,

    COALESCE(SUM(PROFIT),0) TOTAL_PROFIT,

    COALESCE(SUM(GST_TOTAL),0) TOTAL_GST,

    COALESCE(SUM(BALANCE_AMOUNT),0) OUTSTANDING,

    COALESCE(SUM(PAID_AMOUNT),0) COLLECTED,

    COALESCE(
        SUM(
            CASE
            WHEN GST_ENABLED = 1
            THEN 1
            ELSE 0
            END
        ),0
    ) GST_BILLS,

    COALESCE(
        SUM(
            CASE
            WHEN GST_ENABLED = 0
            THEN 1
            ELSE 0
            END
        ),0
    ) NON_GST_BILLS

FROM SALES_ANALYTICS_VIEW

WHERE DATE(BILL_DATE)
BETWEEN DATE(?)
AND DATE(?)

""";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, fromDate);
        ps.setString(2, toDate);

        ResultSet rs =
                ps.executeQuery();

        if (rs.next()) {

            double sales =
                    rs.getDouble(
                            "TOTAL_SALES"
                    );

            double purchase =
                    rs.getDouble(
                            "TOTAL_PURCHASE"
                    );



            double collected =
                    rs.getDouble(
                            "COLLECTED"
                    );

            double outstanding =
                    rs.getDouble(
                            "OUTSTANDING"
                    );

            int invoices =
                    rs.getInt(
                            "TOTAL_INVOICES"
                    );

            summary.setTotalSales(sales);

            summary.setTotalPurchase(
                    purchase
            );
System.out.println("hahah"+ purchase);
            double grossProfit =
                    sales - purchase;

            summary.setGrossProfit(
                    grossProfit
            );

            summary.setNetProfit(
                    grossProfit
            );

            summary.setTotalGST(
                    rs.getDouble(
                            "TOTAL_GST"
                    )
            );

            summary.setOutstandingAmount(
                    outstanding
            );

            summary.setCollectedAmount(
                    collected
            );

            summary.setTotalInvoices(
                    invoices
            );

            summary.setGstBills(
                    rs.getInt(
                            "GST_BILLS"
                    )
            );

            summary.setNonGstBills(
                    rs.getInt(
                            "NON_GST_BILLS"
                    )
            );

            summary.setAverageBillValue(

                    invoices == 0

                            ? 0

                            : sales / invoices
            );

            summary.setRecoveryPercentage(

                    sales == 0

                            ? 0

                            : (collected / sales) * 100
            );
        }

        rs.close();
        ps.close();

        return summary;
    }

    // =====================================================
    // TODAY SALES
    // =====================================================

    public List<SalesAnalyticsModel>
    getTodaySales()
            throws SQLException {

        String sql = """

SELECT *

FROM SALES_ANALYTICS_VIEW

WHERE DATE(BILL_DATE)
= DATE('now')

ORDER BY ID DESC

""";

        return executeQuery(sql);
    }

    // =====================================================
    // WEEK SALES
    // =====================================================

    public List<SalesAnalyticsModel>
    getWeekSales()
            throws SQLException {

        String sql = """

SELECT *

FROM SALES_ANALYTICS_VIEW

WHERE DATE(BILL_DATE)
>= DATE('now','-7 day')

ORDER BY ID DESC

""";

        return executeQuery(sql);
    }

    // =====================================================
    // MONTH SALES
    // =====================================================

    public List<SalesAnalyticsModel>
    getMonthSales()
            throws SQLException {

        String sql = """

SELECT *

FROM SALES_ANALYTICS_VIEW

WHERE strftime('%Y-%m', BILL_DATE)
=
strftime('%Y-%m','now')

ORDER BY ID DESC

""";

        return executeQuery(sql);
    }

    // =====================================================
    // CUSTOM RANGE
    // =====================================================

    public List<SalesAnalyticsModel> getCustomSales(
            String from,
            String to)
            throws SQLException {

        String sql = """

SELECT *

FROM SALES_ANALYTICS_VIEW

WHERE DATE(BILL_DATE)

BETWEEN DATE(?)

AND DATE(?)

ORDER BY ID DESC

""";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, from);
        ps.setString(2, to);

        ResultSet rs =
                ps.executeQuery();

        List<SalesAnalyticsModel> list =
                buildList(rs);

        rs.close();
        ps.close();

        return list;
    }
    public void debugPurchaseCalculation() {

        try {

            System.out.println("\n========================================");
            System.out.println("PURCHASE COST DEBUG");
            System.out.println("========================================");

            String sql = """

SELECT

    ii.INVOICE_ID,

    ii.ITEM_ID,

    ii.MEASUREMENT_ID,

    ii.QUANTITY,

    im.ID AS MEASURE_ID,

    im.ITEM_ID AS MEASURE_ITEM_ID,

    im.QUANTITY AS PACK_QTY,

    im.UNIT,

    im.SELLING_PRICE,

    im.PURCHASE_PRICE,

    (
        ii.QUANTITY *
        COALESCE(im.PURCHASE_PRICE,0)
    ) AS COST

FROM INVOICE_ITEMS ii

LEFT JOIN ITEM_MEASUREMENTS im
ON ii.ITEM_ID = im.ITEM_ID

ORDER BY
    ii.INVOICE_ID,
    ii.ITEM_ID,
    im.ID

LIMIT 200

""";

            Statement stmt =
                    connection.createStatement();

            ResultSet rs =
                    stmt.executeQuery(sql);

            while (rs.next()) {

                System.out.println(
                        "Invoice=" +
                                rs.getInt("INVOICE_ID") +

                                " | Item=" +
                                rs.getInt("ITEM_ID") +

                                " | Measurement=" +
                                rs.getInt("MEASUREMENT_ID") +

                                " | MeasureRow=" +
                                rs.getInt("MEASURE_ID") +

                                " | Unit=" +
                                rs.getString("UNIT") +

                                " | Qty=" +
                                rs.getDouble("QUANTITY") +

                                " | Purchase=" +
                                rs.getDouble("PURCHASE_PRICE") +

                                " | Cost=" +
                                rs.getDouble("COST")
                );
            }

            rs.close();
            stmt.close();

            System.out.println("========================================\n");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    // =====================================================
    // COMMON QUERY
    // =====================================================

    private List<SalesAnalyticsModel>
    executeQuery(String sql)
            throws SQLException {

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(sql);

        List<SalesAnalyticsModel> list =
                buildList(rs);

        rs.close();
        stmt.close();

        return list;
    }

    // =====================================================
    // BUILD MODEL
    // =====================================================



    public List<TopCustomerModel> getTopCustomers(
            String from,
            String to)
            throws SQLException {

        List<TopCustomerModel> list =
                new ArrayList<>();


        String sql = """

SELECT
    ID as CUSTOMER_ID,

    CUSTOMER_NAME,

    COUNT(*) BILLS,

    SUM(GRAND_TOTAL) SALES,

    SUM(PROFIT) PROFIT,

    SUM(BALANCE_AMOUNT) OUTSTANDING

FROM SALES_ANALYTICS_VIEW

WHERE DATE(BILL_DATE)

BETWEEN DATE(?)

AND DATE(?)

GROUP BY CUSTOMER_NAME

ORDER BY SALES DESC

LIMIT 10

""";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, from);

        ps.setString(2, to);

        ResultSet rs =
                ps.executeQuery();

        double totalSales = 0;

        while (rs.next()) {

            totalSales +=
                    rs.getDouble("SALES");
        }

        rs.close();

        ps.close();

        ps =
                connection.prepareStatement(sql);

        ps.setString(1, from);

        ps.setString(2, to);

        rs =
                ps.executeQuery();

        int rank = 1;

        while (rs.next()) {

            double sales =
                    rs.getDouble("SALES");

            double contribution =

                    totalSales == 0

                            ? 0

                            : (sales / totalSales) * 100;

            list.add(

                    new TopCustomerModel(

                            rank++,

                            rs.getInt(
                                    "CUSTOMER_ID"
                            ),

                            rs.getString(
                                    "CUSTOMER_NAME"
                            ),

                            rs.getInt(
                                    "BILLS"
                            ),

                            sales,

                            rs.getDouble(
                                    "PROFIT"
                            ),

                            rs.getDouble(
                                    "OUTSTANDING"
                            ),

                            contribution
                    )
            );
        }

        rs.close();

        ps.close();

        return list;
    }

    public List<TopPurchaserModel> getTopPurchasers()
            throws SQLException {

        List<TopPurchaserModel> list =
                new ArrayList<>();

        String sql = """

    SELECT

        p.ID,

        p.PURCHASER_NAME,

        p.TOTAL_BILLS,

        p.TOTAL_PURCHASE_AMOUNT,

        p.TOTAL_PAID_AMOUNT,

        p.PENDING_AMOUNT

    FROM PURCHASERS p

    ORDER BY
    p.TOTAL_PURCHASE_AMOUNT DESC

    LIMIT 10

    """;

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(sql);

        int rank = 1;

        while (rs.next()) {

            list.add(

                    new TopPurchaserModel(

                            rank++,

                            rs.getInt("ID"),

                            rs.getString("PURCHASER_NAME"),

                            rs.getInt("TOTAL_BILLS"),

                            rs.getDouble(
                                    "TOTAL_PURCHASE_AMOUNT"
                            ),

                            rs.getDouble(
                                    "TOTAL_PAID_AMOUNT"
                            ),

                            rs.getDouble(
                                    "PENDING_AMOUNT"
                            ),

                            0
                    )
            );
        }

        rs.close();
        stmt.close();

        return list;
    }


    public List<CategorySalesModel>
    getCategorySales(
            String from,
            String to)
            throws SQLException {

        List<CategorySalesModel> list
                = new ArrayList<>();

        String sql = """

SELECT

    c.CATEGORY_NAME,

    SUM(ii.TOTAL) SALES,

    COUNT(*) ITEM_COUNT

FROM INVOICE_ITEMS ii

INNER JOIN NEW_ITEMS i
ON ii.ITEM_ID = i.ID

INNER JOIN CATEGORIES c
ON i.CATEGORY_ID = c.ID

INNER JOIN INVOICES inv
ON inv.ID = ii.INVOICE_ID

WHERE DATE(inv.BILL_DATE)

BETWEEN DATE(?)

AND DATE(?)

GROUP BY c.CATEGORY_NAME

ORDER BY SALES DESC

""";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, from);
        ps.setString(2, to);

        ResultSet rs =
                ps.executeQuery();

        double totalSales = 0;

        while (rs.next()) {

            totalSales += rs.getDouble("SALES");
        }

        rs.close();
        ps.close();

        ps = connection.prepareStatement(sql);

        ps.setString(1, from);
        ps.setString(2, to);

        rs = ps.executeQuery();

        while (rs.next()) {

            double sales =
                    rs.getDouble("SALES");

            double percentage =

                    totalSales == 0

                            ? 0

                            : (sales / totalSales) * 100;

            list.add(

                    new CategorySalesModel(

                            rs.getString(
                                    "CATEGORY_NAME"
                            ),

                            sales,

                            rs.getInt(
                                    "ITEM_COUNT"
                            ),

                            percentage
                    )
            );
        }

        rs.close();
        ps.close();

        return list;
    }

    public HealthScoreModel getBusinessHealth(
            DashboardSummaryModel summary) {

        double salesScore = 0;
        double profitScore = 0;
        double collectionScore = 0;
        double expenseScore = 0;

        // Sales Score (25)

        if (summary.getTotalSales() > 100000) {

            salesScore = 25;

        } else if (summary.getTotalSales() > 50000) {

            salesScore = 20;

        } else if (summary.getTotalSales() > 10000) {

            salesScore = 15;

        } else {

            salesScore = 5;
        }

        // Profit Score (25)

        if (summary.getGrossProfit() > 50000) {

            profitScore = 25;

        } else if (summary.getGrossProfit() > 25000) {

            profitScore = 20;

        } else if (summary.getGrossProfit() > 10000) {

            profitScore = 15;

        } else {

            profitScore = 5;
        }

        // Collection Score (25)

        collectionScore =
                Math.min(
                        summary.getRecoveryPercentage() / 4,
                        25
                );

        // Expense Score (25)

        if (summary.getTotalExpense() == 0) {

            expenseScore = 25;

        } else {

            double ratio =

                    (summary.getTotalExpense()
                            /
                            Math.max(
                                    summary.getTotalSales(),
                                    1
                            )) * 100;

            if (ratio < 10) {

                expenseScore = 25;

            } else if (ratio < 20) {

                expenseScore = 20;

            } else if (ratio < 30) {

                expenseScore = 15;

            } else {

                expenseScore = 5;
            }
        }

        double overall =

                salesScore
                        + profitScore
                        + collectionScore
                        + expenseScore;

        String status;

        if (overall >= 85) {

            status = "Excellent";

        } else if (overall >= 70) {

            status = "Good";

        } else if (overall >= 50) {

            status = "Average";

        } else {

            status = "Poor";
        }

        return new HealthScoreModel(

                salesScore,

                profitScore,

                collectionScore,

                expenseScore,

                overall,

                status
        );
    }

    public List<BusinessInsightModel>
    getBusinessInsights(
            DashboardSummaryModel summary) {

        List<BusinessInsightModel> list =
                new ArrayList<>();

        // Recovery

        if (summary.getRecoveryPercentage() < 70) {

            list.add(

                    new BusinessInsightModel(

                            "COLLECTION",

                            "Collection Needs Attention",

                            "Recovery rate below 70%",

                            "WARNING",

                            "⚠"
                    )
            );
        }

        // Profit

        if (summary.getGrossProfit() > 0) {

            list.add(

                    new BusinessInsightModel(

                            "PROFIT",

                            "Business Running Profitably",

                            "Positive gross profit recorded",

                            "SUCCESS",

                            "✓"
                    )
            );
        }

        // Outstanding

        if (summary.getOutstandingAmount() > 50000) {

            list.add(

                    new BusinessInsightModel(

                            "CREDIT",

                            "Outstanding Amount High",

                            "Review credit customers",

                            "CRITICAL",

                            "⛔"
                    )
            );
        }

        // GST

        if (summary.getTotalGST() > 0) {

            list.add(

                    new BusinessInsightModel(

                            "GST",

                            "GST Sales Recorded",

                            "GST invoices available",

                            "SUCCESS",

                            "₹"
                    )
            );
        }

        // Top Customer

        if (summary.getTopCustomer() != null) {

            list.add(

                    new BusinessInsightModel(

                            "CUSTOMER",

                            "Top Customer",

                            summary.getTopCustomer(),

                            "INFO",

                            "👤"
                    )
            );
        }

        return list;
    }

    public String getTopCustomer(
            String from,
            String to)
            throws SQLException {

        String sql = """

        SELECT

            CUSTOMER_NAME

        FROM SALES_ANALYTICS_VIEW

        WHERE DATE(BILL_DATE)

        BETWEEN DATE(?)

        AND DATE(?)

        GROUP BY CUSTOMER_NAME

        ORDER BY
        SUM(GRAND_TOTAL) DESC

        LIMIT 1

        """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, from);

        ps.setString(2, to);

        ResultSet rs =
                ps.executeQuery();

        String customer = null;

        if (rs.next()) {

            customer =
                    rs.getString(
                            "CUSTOMER_NAME"
                    );
        }

        rs.close();
        ps.close();

        return customer;
    }
    public Map<String, Double>
    getPaymentAnalysis(
            String from,
            String to)
            throws SQLException {

        Map<String, Double> map =
                new LinkedHashMap<>();

        String sql = """

        SELECT

            PAYMENT_MODE,

            SUM(GRAND_TOTAL) TOTAL

        FROM SALES_ANALYTICS_VIEW

        WHERE DATE(BILL_DATE)

        BETWEEN DATE(?)

        AND DATE(?)

        GROUP BY PAYMENT_MODE

        ORDER BY TOTAL DESC

        """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, from);
        ps.setString(2, to);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            map.put(

                    rs.getString("PAYMENT_MODE"),

                    rs.getDouble("TOTAL")
            );
        }

        rs.close();
        ps.close();

        return map;
    }

    public Map<String, Double>
    getGSTAnalysis(
            String from,
            String to)
            throws SQLException {

        Map<String, Double> map =
                new LinkedHashMap<>();

        String sql = """

        SELECT

            SUM(
                CASE
                WHEN GST_ENABLED = 1
                THEN GRAND_TOTAL
                ELSE 0
                END
            ) GST_SALES,

            SUM(
                CASE
                WHEN GST_ENABLED = 0
                THEN GRAND_TOTAL
                ELSE 0
                END
            ) NON_GST_SALES

        FROM SALES_ANALYTICS_VIEW

        WHERE DATE(BILL_DATE)

        BETWEEN DATE(?)

        AND DATE(?)

        """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, from);
        ps.setString(2, to);

        ResultSet rs =
                ps.executeQuery();

        if (rs.next()) {

            map.put(
                    "GST",
                    rs.getDouble("GST_SALES")
            );

            map.put(
                    "Non GST",
                    rs.getDouble("NON_GST_SALES")
            );
        }

        rs.close();
        ps.close();

        return map;
    }

}

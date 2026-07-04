package com.dt.invoice;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    private final Connection connection;

    public InvoiceDAO(Connection connection)
            throws SQLException {

        this.connection = connection;

        createTables();
    }

    // =====================================================
    // INSERT
    // =====================================================

    public void insert(InvoiceModel invoice)
            throws SQLException {

        String sql = """

                INSERT INTO INVOICES(

                    INVOICE_NO,
                    CUSTOMER_ID,
                    SALE_TYPE,
                    PAYMENT_MODE,
                    SUB_TOTAL,
                    DISCOUNT,
                    DELIVERY_CHARGE,
                    GST_TOTAL,
                    GRAND_TOTAL,
                    PAID_AMOUNT,
                    BALANCE_AMOUNT,
                    PAYMENT_STATUS,
                    NOTES

                )

                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

                """;

        PreparedStatement ps =

                connection.prepareStatement(sql);

        ps.setString(1, invoice.getInvoiceNo());

        ps.setInt(2, invoice.getCustomerId());

        ps.setString(3, invoice.getSaleType());

        ps.setString(4, invoice.getPaymentMode());

        ps.setDouble(5, invoice.getSubTotal());

        ps.setDouble(6, invoice.getDiscount());

        ps.setDouble(7, invoice.getDeliveryCharge());

        ps.setDouble(8, invoice.getGstTotal());

        ps.setDouble(9, invoice.getGrandTotal());

        ps.setDouble(10, invoice.getPaidAmount());

        ps.setDouble(11, invoice.getBalanceAmount());

        ps.setString(12, invoice.getPaymentStatus());

        ps.setString(13, invoice.getNotes());

        ps.executeUpdate();

        ps.close();
    }

    // =====================================================
    // LAST ID
    // =====================================================

    public int getLastInsertedId()
            throws SQLException {

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(

                        "SELECT last_insert_rowid()"
                );

        int id = 0;

        if (rs.next()) {

            id = rs.getInt(1);
        }

        rs.close();

        stmt.close();

        return id;
    }

    private void createTables()
            throws SQLException {

        String sql = """

        CREATE TABLE IF NOT EXISTS INVOICE_ITEMS (

            ID INTEGER PRIMARY KEY AUTOINCREMENT,

            INVOICE_ID INTEGER,

            ITEM_ID INTEGER,

            ITEM_NAME VARCHAR(200),

            QUANTITY DOUBLE DEFAULT 0,

            PRICE DOUBLE DEFAULT 0,

            GST_PERCENT DOUBLE DEFAULT 0,

            GST_AMOUNT DOUBLE DEFAULT 0,

            TOTAL DOUBLE DEFAULT 0

        )

        """;

        Statement stmt =
                connection.createStatement();

        stmt.execute(sql);

        // =========================================
        // SAFE MIGRATIONS
        // =========================================

        addColumnIfNotExists(
                stmt,
                "INVOICE_ITEMS",
                "MEASUREMENT_ID",
                "INTEGER DEFAULT 0"
        );

        addColumnIfNotExists(
                stmt,
                "INVOICE_ITEMS",
                "UNIT",
                "VARCHAR(20)"
        );

        stmt.close();
    }

    private void addColumnIfNotExists(Statement stmt,
                                      String table,
                                      String column,
                                      String type) {

        try {

            stmt.execute(

                    "ALTER TABLE "

                            +

                            table

                            +

                            " ADD COLUMN "

                            +

                            column

                            +

                            " "

                            +

                            type
            );

            System.out.println(
                    "Added column : " + column
            );

        } catch (SQLException e) {

            // COLUMN ALREADY EXISTS

            if (!e.getMessage()

                    .contains("duplicate column")) {

                e.printStackTrace();
            }
        }
    }

    public List<InvoiceModel> getTodayInvoices()
            throws SQLException {

        List<InvoiceModel> list =
                new ArrayList<>();

        String sql = """

            SELECT I.*,
                   C.CUSTOMER_NAME,
                   C.PHONE_NO,
                   C.GST_NUMBER,
                   C.ADDRESS1

            FROM INVOICES I

            LEFT JOIN CUSTOMER_MASTER C
            ON I.CUSTOMER_ID = C.ID

            WHERE DATE(I.BILL_DATE)=DATE('now','localtime')

            ORDER BY I.ID DESC

            """;

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(sql);

        while (rs.next()) {

            list.add(buildInvoice(rs));
        }

        rs.close();

        stmt.close();

        return list;
    }

    public List<InvoiceModel> search(String keyword)
            throws SQLException {

        List<InvoiceModel> list =
                new ArrayList<>();

        String sql = """

            SELECT I.*,
                   C.CUSTOMER_NAME,
                   C.PHONE_NO,
                   C.GST_NUMBER,
                   C.ADDRESS1

            FROM INVOICES I

            LEFT JOIN CUSTOMER_MASTER C
            ON I.CUSTOMER_ID = C.ID

            WHERE

                LOWER(I.INVOICE_NO)
                LIKE ?

                OR

                LOWER(C.CUSTOMER_NAME)
                LIKE ?

                OR

                LOWER(C.PHONE_NO)
                LIKE ?

                OR

                LOWER(C.GST_NUMBER)
                LIKE ?

            ORDER BY I.ID DESC

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        String search =
                "%" + keyword.toLowerCase() + "%";

        ps.setString(1, search);

        ps.setString(2, search);

        ps.setString(3, search);

        ps.setString(4, search);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            list.add(buildInvoice(rs));
        }

        rs.close();

        ps.close();

        return list;
    }

    public List<InvoiceModel> getBetween(LocalDate from,
                                         LocalDate to)
            throws SQLException {

        List<InvoiceModel> list =
                new ArrayList<>();

        String sql = """

            SELECT I.*,
                   C.CUSTOMER_NAME,
                   C.PHONE_NO,
                   C.GST_NUMBER,
                   C.ADDRESS1

            FROM INVOICES I

            LEFT JOIN CUSTOMER_MASTER C
            ON I.CUSTOMER_ID = C.ID

            WHERE DATE(I.BILL_DATE)
            BETWEEN ? AND ?

            ORDER BY I.ID DESC

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(
                1,
                from.toString()
        );

        ps.setString(
                2,
                to.toString()
        );

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            list.add(buildInvoice(rs));
        }

        rs.close();

        ps.close();

        return list;
    }

    public void delete(int invoiceId)
            throws SQLException {

        connection.setAutoCommit(false);

        try {

            // =====================================
            // DELETE ITEMS
            // =====================================

            PreparedStatement ps1 =

                    connection.prepareStatement("""

                DELETE FROM INVOICE_ITEMS

                WHERE INVOICE_ID=?

                """);

            ps1.setInt(
                    1,
                    invoiceId
            );

            ps1.executeUpdate();

            ps1.close();

            // =====================================
            // DELETE PAYMENTS
            // =====================================

            PreparedStatement ps2 =

                    connection.prepareStatement("""

                DELETE FROM PAYMENTS

                WHERE INVOICE_ID=?

                """);

            ps2.setInt(
                    1,
                    invoiceId
            );

            ps2.executeUpdate();

            ps2.close();

            // =====================================
            // DELETE INVOICE
            // =====================================

            PreparedStatement ps3 =

                    connection.prepareStatement("""

                DELETE FROM INVOICES

                WHERE ID=?

                """);

            ps3.setInt(
                    1,
                    invoiceId
            );

            ps3.executeUpdate();

            ps3.close();

            connection.commit();

        } catch (Exception e) {

            connection.rollback();

            throw e;

        } finally {

            connection.setAutoCommit(true);
        }
    }

    private InvoiceModel buildInvoice(ResultSet rs)
            throws SQLException {

        InvoiceModel invoice =
                new InvoiceModel();

        invoice.setId(
                rs.getInt("ID")
        );

        invoice.setInvoiceNo(
                rs.getString("INVOICE_NO")
        );

        invoice.setCustomerId(
                rs.getInt("CUSTOMER_ID")
        );

        invoice.setCustomerAddress(
                rs.getString("ADDRESS1")
        );

        invoice.setSaleType(
                rs.getString("SALE_TYPE")
        );

        invoice.setPaymentMode(
                rs.getString("PAYMENT_MODE")
        );

        String billDate =
                rs.getString("BILL_DATE");

        if (billDate != null) {

            DateTimeFormatter formatter =

                    DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd HH:mm:ss"
                    );

            LocalDateTime dateTime =

                    LocalDateTime.parse(
                            billDate,
                            formatter
                    );

            invoice.setBillDate(
                    dateTime
            );
        }

        invoice.setSubTotal(
                rs.getDouble("SUB_TOTAL")
        );

        invoice.setDiscount(
                rs.getDouble("DISCOUNT")
        );

        invoice.setDeliveryCharge(
                rs.getDouble("DELIVERY_CHARGE")
        );

        invoice.setGSTTotal(
                rs.getDouble("GST_TOTAL")
        );

        invoice.setGrandTotal(
                rs.getDouble("GRAND_TOTAL")
        );

        invoice.setPaidAmount(
                rs.getDouble("PAID_AMOUNT")
        );

        invoice.setBalanceAmount(
                rs.getDouble("BALANCE_AMOUNT")
        );

        invoice.setPaymentStatus(
                rs.getString("PAYMENT_STATUS")
        );

        invoice.setNotes(
                rs.getString("NOTES")
        );

        // =====================================
        // CUSTOMER EXTRA
        // =====================================

        invoice.setCustomerName(
                rs.getString("CUSTOMER_NAME")
        );

        invoice.setCustomerPhone(
                rs.getString("PHONE_NO")
        );

        invoice.setCustomerGST(
                rs.getString("GST_NUMBER")
        );

        return invoice;
    }

    public InvoiceModel getById(int invoiceId)
            throws SQLException {

        String sql = """

            SELECT I.*,
                   C.CUSTOMER_NAME,
                   C.PHONE_NO,
                   C.GST_NUMBER,
                   C.ADDRESS1

            FROM INVOICES I

            LEFT JOIN CUSTOMER_MASTER C
            ON I.CUSTOMER_ID = C.ID

            WHERE I.ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(
                1,
                invoiceId
        );

        ResultSet rs =
                ps.executeQuery();

        InvoiceModel invoice = null;

        if (rs.next()) {

            invoice = buildInvoice(rs);
        }

        rs.close();

        ps.close();

        return invoice;
    }

    public void update(InvoiceModel invoice)
            throws SQLException {

        String sql = """

            UPDATE INVOICES

            SET

                CUSTOMER_ID=?,
                SALE_TYPE=?,
                PAYMENT_MODE=?,
                SUB_TOTAL=?,
                DISCOUNT=?,
                DELIVERY_CHARGE=?,
                GST_TOTAL=?,
                GRAND_TOTAL=?,
                PAID_AMOUNT=?,
                BALANCE_AMOUNT=?,
                PAYMENT_STATUS=?,
                NOTES=?

            WHERE ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(
                1,
                invoice.getCustomerId()
        );

        ps.setString(
                2,
                invoice.getSaleType()
        );

        ps.setString(
                3,
                invoice.getPaymentMode()
        );

        ps.setDouble(
                4,
                invoice.getSubTotal()
        );

        ps.setDouble(
                5,
                invoice.getDiscount()
        );

        ps.setDouble(
                6,
                invoice.getDeliveryCharge()
        );

        ps.setDouble(
                7,
                invoice.getGSTTotal()
        );

        ps.setDouble(
                8,
                invoice.getGrandTotal()
        );

        ps.setDouble(
                9,
                invoice.getPaidAmount()
        );

        ps.setDouble(
                10,
                invoice.getBalanceAmount()
        );

        ps.setString(
                11,
                invoice.getPaymentStatus()
        );

        ps.setString(
                12,
                invoice.getNotes()
        );

        ps.setInt(
                13,
                invoice.getId()
        );

        ps.executeUpdate();

        ps.close();
    }
}
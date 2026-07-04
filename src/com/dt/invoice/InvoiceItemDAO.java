package com.dt.invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO {

    private final Connection connection;

    public InvoiceItemDAO(Connection connection)
            throws SQLException {

        this.connection = connection;

        createTables();
    }

    public void insert(InvoiceItemModel item)
            throws SQLException {

        String sql = """

                INSERT INTO INVOICE_ITEMS(

                    INVOICE_ID,
                    ITEM_ID,
                    ITEM_NAME,
                    QUANTITY,
                    UNIT,
                    PRICE,
                    GST_PERCENT,
                    GST_AMOUNT,
                    TOTAL

                )

                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, item.getInvoiceId());

        ps.setInt(2, item.getItemId());

        ps.setString(3, item.getItemName());

        ps.setDouble(4, item.getQuantity());

        ps.setString(5, item.getUnit());

        ps.setDouble(6, item.getPrice());

        ps.setDouble(7, item.getGstPercent());

        ps.setDouble(8, item.getGstAmount());

        ps.setDouble(9, item.getTotal());

        ps.executeUpdate();

        ps.close();
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

                UNIT VARCHAR(20),

                PRICE DOUBLE DEFAULT 0,

                GST_PERCENT DOUBLE DEFAULT 0,

                GST_AMOUNT DOUBLE DEFAULT 0,

                TOTAL DOUBLE DEFAULT 0

            )

            """;

        Statement stmt =
                connection.createStatement();

        stmt.execute(sql);

        stmt.close();
    }

    public List<InvoiceItemModel> getByInvoice(int invoiceId)
            throws SQLException {

        List<InvoiceItemModel> list =
                new ArrayList<>();

        String sql = """

            SELECT *

            FROM INVOICE_ITEMS

            WHERE INVOICE_ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(
                1,
                invoiceId
        );

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            InvoiceItemModel item =
                    new InvoiceItemModel();

            item.setId(
                    rs.getInt("ID")
            );

            item.setInvoiceId(
                    rs.getInt("INVOICE_ID")
            );

            item.setItemId(
                    rs.getInt("ITEM_ID")
            );

            item.setMeasurementId(
                    rs.getInt("MEASUREMENT_ID")
            );

            item.setItemName(
                    rs.getString("ITEM_NAME")
            );

            item.setQuantity(
                    rs.getDouble("QUANTITY")
            );

            item.setUnit(
                    rs.getString("UNIT")
            );

            item.setPrice(
                    rs.getDouble("PRICE")
            );

            item.setGstPercent(
                    rs.getDouble("GST_PERCENT")
            );

            item.setGstAmount(
                    rs.getDouble("GST_AMOUNT")
            );

            item.setTotal(
                    rs.getDouble("TOTAL")
            );

            list.add(item);
        }

        rs.close();

        ps.close();

        return list;
    }

    public void deleteByInvoice(int invoiceId)
            throws SQLException {

        String sql = """

            DELETE FROM INVOICE_ITEMS

            WHERE INVOICE_ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(
                1,
                invoiceId
        );

        ps.executeUpdate();

        ps.close();
    }
}
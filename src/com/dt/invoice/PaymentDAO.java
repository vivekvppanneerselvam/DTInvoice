package com.dt.invoice;

import java.sql.*;

public class PaymentDAO {

    private final Connection connection;

    public PaymentDAO(Connection connection)
            throws SQLException {

        this.connection = connection;

        createTables();
    }

    // INSERT PAYMENT

    public void insert(PaymentModel payment)
            throws SQLException {

        String sql = """

                INSERT INTO PAYMENTS(

                    INVOICE_ID,
                    CUSTOMER_ID,
                    PAYMENT_MODE,
                    PAID_AMOUNT,
                    REMARKS

                )

                VALUES (?, ?, ?, ?, ?)

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1,
                payment.getInvoiceId());

        ps.setInt(2,
                payment.getCustomerId());

        ps.setString(3,
                payment.getPaymentMode());

        ps.setDouble(4,
                payment.getPaidAmount());

        ps.setString(5,
                payment.getRemarks());

        ps.executeUpdate();

        ps.close();
    }

    private void createTables()
            throws SQLException {

        String sql = """

            CREATE TABLE IF NOT EXISTS PAYMENTS (

                ID INTEGER PRIMARY KEY AUTOINCREMENT,

                INVOICE_ID INTEGER,

                CUSTOMER_ID INTEGER,

                PAYMENT_MODE VARCHAR(30),

                PAID_AMOUNT DOUBLE DEFAULT 0,

                PAYMENT_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                REMARKS VARCHAR(500)

            )

            """;

        Statement stmt =
                connection.createStatement();

        stmt.execute(sql);

        stmt.close();
    }

    public void deleteByInvoice(int invoiceId)
            throws SQLException {

        String sql = """

            DELETE FROM PAYMENTS

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

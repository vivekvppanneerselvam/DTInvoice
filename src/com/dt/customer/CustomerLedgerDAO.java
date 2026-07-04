package com.dt.customer;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerLedgerDAO {

    private final Connection connection;

    public CustomerLedgerDAO(Connection connection) throws SQLException {

        this.connection = connection;
        createTables();
    }

    // INSERT LEDGER ENTRY

    public void insert(int customerId,
                       String entryType,
                       String refNo,
                       double debit,
                       double credit,
                       double balance,
                       String remarks)
            throws SQLException {

        String sql = """

                INSERT INTO CUSTOMER_LEDGER(

                    CUSTOMER_ID,
                    ENTRY_TYPE,
                    REF_NO,
                    DEBIT,
                    CREDIT,
                    BALANCE,
                    REMARKS

                )

                VALUES (?, ?, ?, ?, ?, ?, ?)

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, customerId);

        ps.setString(2, entryType);

        ps.setString(3, refNo);

        ps.setDouble(4, debit);

        ps.setDouble(5, credit);

        ps.setDouble(6, balance);

        ps.setString(7, remarks);

        ps.executeUpdate();

        ps.close();
    }

    public List<CustomerLedgerModel> getByCustomer(int customerId)
            throws SQLException {

        List<CustomerLedgerModel> list =
                new ArrayList<>();

        String sql = """

            SELECT *

            FROM CUSTOMER_LEDGER

            WHERE CUSTOMER_ID=?

            ORDER BY ENTRY_DATE DESC

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1,
                customerId);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            list.add(

                    new CustomerLedgerModel(

                            rs.getInt("ID"),

                            rs.getInt("CUSTOMER_ID"),

                            rs.getString("ENTRY_DATE"),

                            rs.getString("ENTRY_TYPE"),

                            rs.getString("REF_NO"),

                            rs.getDouble("DEBIT"),

                            rs.getDouble("CREDIT"),

                            rs.getDouble("BALANCE"),

                            rs.getString("REMARKS")
                    )
            );
        }

        rs.close();

        ps.close();

        return list;
    }

    private void createTables()
            throws SQLException {

        String sql = """

            CREATE TABLE IF NOT EXISTS CUSTOMER_LEDGER (

                ID INTEGER PRIMARY KEY AUTOINCREMENT,

                CUSTOMER_ID INTEGER,

                ENTRY_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,

                ENTRY_TYPE VARCHAR(50),

                REF_NO VARCHAR(100),

                DEBIT REAL DEFAULT 0,

                CREDIT REAL DEFAULT 0,

                BALANCE REAL DEFAULT 0,

                REMARKS VARCHAR(500)

            )

            """;

        Statement stmt =
                connection.createStatement();

        stmt.execute(sql);

        stmt.close();
    }
}

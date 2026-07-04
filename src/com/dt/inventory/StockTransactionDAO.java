package com.dt.inventory;

import java.sql.*;

public class StockTransactionDAO {

    private final Connection connection;

    public StockTransactionDAO(Connection connection) {

        this.connection = connection;
    }

    // INSERT STOCK TRANSACTION

    public void insert(int measurementId,
                       String type,
                       double quantity,
                       String remarks)
            throws SQLException {

        String sql = """

                INSERT INTO STOCK_TRANSACTIONS(

                    MEASUREMENT_ID,
                    TRANSACTION_TYPE,
                    QUANTITY,
                    REMARKS

                )

                VALUES (?, ?, ?, ?)

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1,
                measurementId);

        ps.setString(2,
                type);

        ps.setDouble(3,
                quantity);

        ps.setString(4,
                remarks);

        ps.executeUpdate();

        ps.close();
    }
}

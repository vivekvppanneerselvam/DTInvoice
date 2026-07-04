package com.dt.inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeasurementDAO {

    private final Connection connection;

    public MeasurementDAO(Connection connection) {

        this.connection = connection;

        createTables();
    }

    // =========================================
    // CREATE TABLES
    // =========================================

    private void createTables() {

        try {

            Statement stmt =
                    connection.createStatement();

            stmt.execute("""

                    CREATE TABLE IF NOT EXISTS ITEM_MEASUREMENTS (

                        ID INTEGER PRIMARY KEY AUTOINCREMENT,

                        ITEM_ID INTEGER,

                        QUANTITY REAL,

                        UNIT TEXT,

                        SELLING_PRICE REAL,

                        PURCHASE_PRICE REAL

                    )

                    """);

            stmt.execute("""

                    CREATE TABLE IF NOT EXISTS STOCK (

                        ID INTEGER PRIMARY KEY AUTOINCREMENT,

                        MEASUREMENT_ID INTEGER,

                        CURRENT_STOCK REAL DEFAULT 0

                    )

                    """);

            stmt.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================
    // INSERT
    // =========================================

    public void insert(ItemMeasurement measurement)
            throws SQLException {

        String sql = """

                INSERT INTO ITEM_MEASUREMENTS(

                    ITEM_ID,
                    QUANTITY,
                    UNIT,
                    SELLING_PRICE,
                    PURCHASE_PRICE

                )

                VALUES (?, ?, ?, ?, ?)

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1,
                measurement.getItemId());

        ps.setDouble(2,
                measurement.getQuantity());

        ps.setString(3,
                measurement.getUnit());

        ps.setDouble(4,
                measurement.getSellingPrice());

        ps.setDouble(5,
                measurement.getPurchasePrice());

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // GET BY ITEM
    // =========================================

    public List<ItemMeasurement> getByItem(int itemId)
            throws SQLException {

        List<ItemMeasurement> list =
                new ArrayList<>();

        String sql = """

                SELECT

                    m.*,

                    s.CURRENT_STOCK

                FROM ITEM_MEASUREMENTS m

                LEFT JOIN STOCK s
                ON m.ID = s.MEASUREMENT_ID

                WHERE m.ITEM_ID=?

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1,
                itemId);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            list.add(

                    new ItemMeasurement(

                            rs.getInt("ID"),

                            rs.getInt("ITEM_ID"),

                            rs.getDouble("QUANTITY"),

                            rs.getString("UNIT"),

                            rs.getDouble("SELLING_PRICE"),

                            rs.getDouble("PURCHASE_PRICE"),

                            rs.getDouble("CURRENT_STOCK")
                    )
            );
        }

        rs.close();
        ps.close();

        return list;
    }

    // =========================================
    // DELETE
    // =========================================

    public void delete(int id)
            throws SQLException {

        String sql =
                "DELETE FROM ITEM_MEASUREMENTS WHERE ID=?";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, id);

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // UPDATE STOCK
    // =========================================

    public void updateAllStock(int measurementId,
                            double stock)
            throws SQLException {

        String checkSql =
                "SELECT ID FROM STOCK WHERE MEASUREMENT_ID=?";

        PreparedStatement checkPs =
                connection.prepareStatement(checkSql);

        checkPs.setInt(1,
                measurementId);

        ResultSet rs =
                checkPs.executeQuery();

        if (rs.next()) {

            PreparedStatement updatePs =
                    connection.prepareStatement(

                            """
                            UPDATE STOCK
                            SET CURRENT_STOCK=?
                            WHERE MEASUREMENT_ID=?
                            """
                    );

            updatePs.setDouble(1,
                    stock);

            updatePs.setInt(2,
                    measurementId);

            updatePs.executeUpdate();

            updatePs.close();

        } else {

            PreparedStatement insertPs =
                    connection.prepareStatement(

                            """
                            INSERT INTO STOCK(
                                MEASUREMENT_ID,
                                CURRENT_STOCK
                            )
                            VALUES (?, ?)
                            """
                    );

            insertPs.setInt(1,
                    measurementId);

            insertPs.setDouble(2,
                    stock);

            insertPs.executeUpdate();

            insertPs.close();
        }

        rs.close();
        checkPs.close();
    }

    public void reduceStock(int measurementId,
                            double quantity)
            throws SQLException {

        String sql = """

        UPDATE STOCK

        SET CURRENT_STOCK =
            CURRENT_STOCK - ?

        WHERE MEASUREMENT_ID=?

        """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setDouble(
                1,
                quantity
        );

        ps.setInt(
                2,
                measurementId
        );

        ps.executeUpdate();

        ps.close();
    }

    public void updateStock(ItemMeasurement measurement)
            throws SQLException {

        String sql = """

            UPDATE ITEM_MEASUREMENTS

            SET CURRENT_STOCK=?

            WHERE ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setDouble(
                1,
                measurement.getCurrentStock()
        );

        ps.setInt(
                2,
                measurement.getId()
        );

        ps.executeUpdate();

        ps.close();
    }

    public void restoreStock(int measurementId,
                             double quantity)
            throws SQLException {

        String sql = """

            UPDATE STOCK

            SET CURRENT_STOCK =
                CURRENT_STOCK + ?

            WHERE MEASUREMENT_ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setDouble(
                1,
                quantity
        );

        ps.setInt(
                2,
                measurementId
        );

        ps.executeUpdate();

        ps.close();
    }

    public ItemMeasurement getById(int id)
            throws SQLException {

        String sql = """

        SELECT

            m.*,

            s.CURRENT_STOCK

        FROM ITEM_MEASUREMENTS m

        LEFT JOIN STOCK s
        ON m.ID = s.MEASUREMENT_ID

        WHERE m.ID=?

        """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, id);

        ResultSet rs =
                ps.executeQuery();

        ItemMeasurement measurement = null;

        if (rs.next()) {

            measurement = build(rs);
        }

        rs.close();

        ps.close();

        return measurement;
    }

    private ItemMeasurement build(ResultSet rs)
            throws SQLException {

        return new ItemMeasurement(

                rs.getInt("ID"),

                rs.getInt("ITEM_ID"),

                rs.getDouble("QUANTITY"),

                rs.getString("UNIT"),

                rs.getDouble("SELLING_PRICE"),

                rs.getDouble("PURCHASE_PRICE"),

                rs.getDouble("CURRENT_STOCK")
        );
    }
}
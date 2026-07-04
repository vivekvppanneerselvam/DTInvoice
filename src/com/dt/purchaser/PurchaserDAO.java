package com.dt.purchaser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaserDAO {

    private final Connection connection;

    public PurchaserDAO(Connection connection) {
        this.connection = connection;
    }

    // =====================================================
    // CREATE TABLE
    // =====================================================

    public void createTable() throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS purchasers (

                id INTEGER PRIMARY KEY AUTOINCREMENT,

                purchaser_code TEXT UNIQUE,

                purchaser_name TEXT NOT NULL,

                gstin TEXT,

                mobile TEXT,

                address TEXT,

                pending_amount REAL DEFAULT 0,

                total_bills INTEGER DEFAULT 0,

                pending_bills INTEGER DEFAULT 0,

                settled_bills INTEGER DEFAULT 0,

                total_purchase_amount REAL DEFAULT 0,

                total_paid_amount REAL DEFAULT 0,

                last_purchase_date TEXT,

                created_date TEXT
            )
            """;

        try (Statement stmt =
                     connection.createStatement()) {

            stmt.execute(sql);
        }
    }

    // =====================================================
    // INSERT
    // =====================================================

    public void insert(PurchaserModel purchaser)
            throws SQLException {

        String sql = """
            INSERT INTO purchasers (

                purchaser_code,
                purchaser_name,
                gstin,
                mobile,
                address,
                pending_amount,
                total_bills,
                pending_bills,
                settled_bills,
                total_purchase_amount,
                total_paid_amount,
                last_purchase_date,
                created_date

            )

            VALUES (
                ?,?,?,?,?,?,
                ?,?,?,?,?,?,
                datetime('now')
            )
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1,
                    purchaser.getPurchaserCode());

            ps.setString(2,
                    purchaser.getName());

            ps.setString(3,
                    purchaser.getGstin());

            ps.setString(4,
                    purchaser.getMobile());

            ps.setString(5,
                    purchaser.getAddress());

            ps.setDouble(6,
                    purchaser.getPending());

            ps.setInt(7,
                    purchaser.getBills());

            ps.setInt(8,
                    purchaser.getPendingBills());

            ps.setInt(9,
                    purchaser.getSettledBills());

            ps.setDouble(10,
                    purchaser.getTotalPurchaseAmount());

            ps.setDouble(11,
                    purchaser.getTotalPaidAmount());

            ps.setString(12,
                    purchaser.getLastPurchase());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<PurchaserModel> getAll()
            throws SQLException {

        List<PurchaserModel> list =
                new ArrayList<>();

        String sql =
                """
                SELECT *
                FROM purchasers
                ORDER BY purchaser_name
                """;

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            while (rs.next()) {

                list.add(

                        new PurchaserModel(

                                rs.getInt("id"),

                                rs.getString(
                                        "purchaser_code"),

                                rs.getString(
                                        "purchaser_name"),

                                rs.getString(
                                        "gstin"),

                                rs.getString(
                                        "mobile"),

                                rs.getString(
                                        "address"),

                                rs.getDouble(
                                        "pending_amount"),

                                rs.getInt(
                                        "total_bills"),

                                rs.getInt(
                                        "pending_bills"),

                                rs.getInt(
                                        "settled_bills"),

                                rs.getDouble(
                                        "total_purchase_amount"),

                                rs.getDouble(
                                        "total_paid_amount"),

                                rs.getString(
                                        "last_purchase_date")
                        )
                );
            }
        }

        return list;
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    public PurchaserModel getById(int id)
            throws SQLException {

        String sql =
                """
                SELECT *
                FROM purchasers
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return new PurchaserModel(

                            rs.getInt("id"),

                            rs.getString(
                                    "purchaser_code"),

                            rs.getString(
                                    "purchaser_name"),

                            rs.getString(
                                    "gstin"),

                            rs.getString(
                                    "mobile"),

                            rs.getString(
                                    "address"),

                            rs.getDouble(
                                    "pending_amount"),

                            rs.getInt(
                                    "total_bills"),

                            rs.getInt(
                                    "pending_bills"),

                            rs.getInt(
                                    "settled_bills"),

                            rs.getDouble(
                                    "total_purchase_amount"),

                            rs.getDouble(
                                    "total_paid_amount"),

                            rs.getString(
                                    "last_purchase_date")
                    );
                }
            }
        }

        return null;
    }

    // =====================================================
    // UPDATE DETAILS
    // =====================================================

    public void update(PurchaserModel purchaser)
            throws SQLException {

        String sql = """
            UPDATE purchasers

            SET

                purchaser_name = ?,
                gstin = ?,
                mobile = ?,
                address = ?

            WHERE id = ?
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1,
                    purchaser.getName());

            ps.setString(2,
                    purchaser.getGstin());

            ps.setString(3,
                    purchaser.getMobile());

            ps.setString(4,
                    purchaser.getAddress());

            ps.setInt(5,
                    purchaser.getId());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // UPDATE FINANCIALS
    // =====================================================

    public void updateFinancials(
            PurchaserModel purchaser)
            throws SQLException {

        String sql = """
            UPDATE purchasers

            SET

                pending_amount=?,
                total_bills=?,
                pending_bills=?,
                settled_bills=?,
                total_purchase_amount=?,
                total_paid_amount=?,
                last_purchase_date=?

            WHERE id=?
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setDouble(1,
                    purchaser.getPending());

            ps.setInt(2,
                    purchaser.getBills());

            ps.setInt(3,
                    purchaser.getPendingBills());

            ps.setInt(4,
                    purchaser.getSettledBills());

            ps.setDouble(5,
                    purchaser.getTotalPurchaseAmount());

            ps.setDouble(6,
                    purchaser.getTotalPaidAmount());

            ps.setString(7,
                    purchaser.getLastPurchase());

            ps.setInt(8,
                    purchaser.getId());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void delete(int id)
            throws SQLException {

        String sql =
                "DELETE FROM purchasers WHERE id=?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();
        }
    }

    // =====================================================
    // NEXT CODE
    // =====================================================

    public String getNextCode()
            throws SQLException {

        String sql =
                """
                SELECT COUNT(*) + 1
                FROM purchasers
                """;

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            if (rs.next()) {

                return "PUR-" +
                        String.format(
                                "%04d",
                                rs.getInt(1)
                        );
            }
        }

        return "PUR-0001";
    }
}
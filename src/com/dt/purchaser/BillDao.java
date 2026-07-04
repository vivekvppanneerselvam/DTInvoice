package com.dt.purchaser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDao {

    private final Connection connection;

    public BillDao(Connection connection) {

        this.connection = connection;
    }

    // =====================================================
    // CREATE TABLE
    // =====================================================

    public void createTable() throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS bills (

                id INTEGER PRIMARY KEY AUTOINCREMENT,

                bill_no TEXT UNIQUE,

                purchaser_id INTEGER,

                bill_date TEXT,

                total_amount REAL DEFAULT 0,

                paid_amount REAL DEFAULT 0,

                balance_amount REAL DEFAULT 0,

                payment_status TEXT,

                created_date TEXT,

                FOREIGN KEY(purchaser_id)
                REFERENCES purchasers(id)
            )
            """;

        try (Statement stmt =
                     connection.createStatement()) {

            stmt.execute(sql);
        }
    }

    // =====================================================
    // INSERT BILL
    // =====================================================

    public void insert(BillModel bill)
            throws SQLException {

        String sql = """
            INSERT INTO bills (

                bill_no,
                purchaser_id,
                bill_date,
                total_amount,
                paid_amount,
                balance_amount,
                payment_status,
                created_date

            )

            VALUES (

                ?,?,?,?,?,?,?,
                datetime('now')
            )
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1,
                    bill.getBillNo());

            ps.setInt(2,
                    bill.getPurchaserId());

            ps.setString(3,
                    bill.getBillDate());

            ps.setDouble(4,
                    bill.getTotalAmount());

            ps.setDouble(5,
                    bill.getPaidAmount());

            ps.setDouble(6,
                    bill.getBalanceAmount());

            ps.setString(7,
                    bill.getPaymentStatus());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<BillModel> getAll()
            throws SQLException {

        List<BillModel> list =
                new ArrayList<>();

        String sql = """
            SELECT b.*,
                   p.purchaser_name

            FROM bills b

            LEFT JOIN purchasers p
            ON b.purchaser_id = p.id

            ORDER BY b.id DESC
            """;

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            while (rs.next()) {

                list.add(mapRow(rs));
            }
        }

        return list;
    }

    // =====================================================
    // GET BY ID
    // =====================================================

    public BillModel getById(int id)
            throws SQLException {

        String sql = """
            SELECT b.*,
                   p.purchaser_name

            FROM bills b

            LEFT JOIN purchasers p
            ON b.purchaser_id = p.id

            WHERE b.id = ?
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    // =====================================================
    // UPDATE PAYMENT
    // =====================================================

    public void updatePayment(
            int billId,
            double paidAmount,
            double balanceAmount,
            String status)
            throws SQLException {

        String sql = """
            UPDATE bills

            SET

                paid_amount=?,
                balance_amount=?,
                payment_status=?

            WHERE id=?
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setDouble(1,
                    paidAmount);

            ps.setDouble(2,
                    balanceAmount);

            ps.setString(3,
                    status);

            ps.setInt(4,
                    billId);

            ps.executeUpdate();
        }
    }

    // =====================================================
    // DELETE BILL
    // =====================================================

    public void delete(int id)
            throws SQLException {

        String sql =
                "DELETE FROM bills WHERE id=?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();
        }
    }

    // =====================================================
    // DATE RANGE
    // =====================================================

    public List<BillModel> getByDateRange(
            String fromDate,
            String toDate)
            throws SQLException {

        List<BillModel> list =
                new ArrayList<>();

        String sql = """
            SELECT b.*,
                   p.purchaser_name

            FROM bills b

            LEFT JOIN purchasers p
            ON b.purchaser_id = p.id

            WHERE date(b.bill_date)
            BETWEEN date(?) AND date(?)

            ORDER BY b.bill_date DESC
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1,
                    fromDate);

            ps.setString(2,
                    toDate);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    list.add(
                            mapRow(rs)
                    );
                }
            }
        }

        return list;
    }

    // =====================================================
    // PURCHASER BILLS
    // =====================================================

    public List<BillModel> getByPurchaser(
            int purchaserId)
            throws SQLException {

        List<BillModel> list =
                new ArrayList<>();

        String sql = """
            SELECT b.*,
                   p.purchaser_name

            FROM bills b

            LEFT JOIN purchasers p
            ON b.purchaser_id = p.id

            WHERE purchaser_id=?

            ORDER BY bill_date DESC
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1,
                    purchaserId);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    list.add(
                            mapRow(rs)
                    );
                }
            }
        }

        return list;
    }

    // =====================================================
    // PENDING BILLS
    // =====================================================

    public List<BillModel> getPendingBills()
            throws SQLException {

        List<BillModel> list =
                new ArrayList<>();

        String sql = """
            SELECT b.*,
                   p.purchaser_name

            FROM bills b

            LEFT JOIN purchasers p
            ON b.purchaser_id = p.id

            WHERE balance_amount > 0

            ORDER BY bill_date DESC
            """;

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            while (rs.next()) {

                list.add(
                        mapRow(rs)
                );
            }
        }

        return list;
    }

    // =====================================================
    // PAID BILLS
    // =====================================================

    public List<BillModel> getPaidBills()
            throws SQLException {

        List<BillModel> list =
                new ArrayList<>();

        String sql = """
            SELECT b.*,
                   p.purchaser_name

            FROM bills b

            LEFT JOIN purchasers p
            ON b.purchaser_id = p.id

            WHERE balance_amount <= 0

            ORDER BY bill_date DESC
            """;

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            while (rs.next()) {

                list.add(
                        mapRow(rs)
                );
            }
        }

        return list;
    }

    // =====================================================
    // NEXT BILL NUMBER
    // =====================================================

    public String getNextBillNo()
            throws SQLException {

        String sql =
                "SELECT COUNT(*) + 1 FROM bills";

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            if (rs.next()) {

                return "BILL-" +
                        String.format(
                                "%05d",
                                rs.getInt(1)
                        );
            }
        }

        return "BILL-00001";
    }

    // =====================================================
    // ROW MAPPER
    // =====================================================

    private BillModel mapRow(ResultSet rs)
            throws SQLException {

        return new BillModel(

                rs.getInt("id"),

                rs.getString("bill_no"),

                rs.getInt("purchaser_id"),

                rs.getString("purchaser_name"),

                rs.getString("bill_date"),

                rs.getDouble("total_amount"),

                rs.getDouble("paid_amount"),

                rs.getDouble("balance_amount"),

                rs.getString("payment_status")
        );
    }
}
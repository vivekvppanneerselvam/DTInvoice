package com.dt.misc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    private final Connection connection;

    public ExpenseDAO(Connection connection) {

        this.connection = connection;
    }

    // =====================================================
    // CREATE TABLE
    // =====================================================

    public void createTable() throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS expenses (

                id INTEGER PRIMARY KEY AUTOINCREMENT,

                expense_no TEXT UNIQUE,

                expense_date TEXT,

                expense_type TEXT,

                vendor_name TEXT,

                amount REAL DEFAULT 0,

                remarks TEXT,

                attachment_path TEXT,

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

    public void insert(ExpenseModel expense)
            throws SQLException {

        String sql = """
            INSERT INTO expenses (

                expense_no,
                expense_date,
                expense_type,
                vendor_name,
                amount,
                remarks,
                attachment_path,
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
                    expense.getExpenseNo());

            ps.setString(2,
                    expense.getExpenseDate());

            ps.setString(3,
                    expense.getExpenseType());

            ps.setString(4,
                    expense.getVendorName());

            ps.setDouble(5,
                    expense.getAmount());

            ps.setString(6,
                    expense.getRemarks());

            ps.setString(7,
                    expense.getAttachmentPath());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // UPDATE
    // =====================================================

    public void update(ExpenseModel expense)
            throws SQLException {

        String sql = """
            UPDATE expenses

            SET

                expense_date=?,
                expense_type=?,
                vendor_name=?,
                amount=?,
                remarks=?,
                attachment_path=?

            WHERE id=?
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1,
                    expense.getExpenseDate());

            ps.setString(2,
                    expense.getExpenseType());

            ps.setString(3,
                    expense.getVendorName());

            ps.setDouble(4,
                    expense.getAmount());

            ps.setString(5,
                    expense.getRemarks());

            ps.setString(6,
                    expense.getAttachmentPath());

            ps.setInt(7,
                    expense.getId());

            ps.executeUpdate();
        }
    }

    // =====================================================
    // DELETE
    // =====================================================

    public void delete(int id)
            throws SQLException {

        String sql =
                "DELETE FROM expenses WHERE id=?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();
        }
    }

    // =====================================================
    // GET ALL
    // =====================================================

    public List<ExpenseModel> getAll()
            throws SQLException {

        List<ExpenseModel> list =
                new ArrayList<>();

        String sql = """
            SELECT *
            FROM expenses
            ORDER BY expense_date DESC,id DESC
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
    // TODAY EXPENSES
    // =====================================================

    public List<ExpenseModel> getTodayExpenses()
            throws SQLException {

        List<ExpenseModel> list =
                new ArrayList<>();

        String sql = """
            SELECT *
            FROM expenses
            WHERE date(expense_date)=date('now')
            ORDER BY id DESC
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
    // DATE RANGE
    // =====================================================

    public List<ExpenseModel> getByDateRange(
            String fromDate,
            String toDate)
            throws SQLException {

        List<ExpenseModel> list =
                new ArrayList<>();

        String sql = """
            SELECT *
            FROM expenses
            WHERE date(expense_date)
            BETWEEN date(?)
            AND date(?)
            ORDER BY expense_date DESC
            """;

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    // =====================================================
    // TOTAL EXPENSE
    // =====================================================

    public double getTotalExpense()
            throws SQLException {

        String sql =
                "SELECT IFNULL(SUM(amount),0) FROM expenses";

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            if (rs.next()) {

                return rs.getDouble(1);
            }
        }

        return 0;
    }

    // =====================================================
    // TODAY TOTAL
    // =====================================================

    public double getTodayTotal()
            throws SQLException {

        String sql = """
            SELECT IFNULL(SUM(amount),0)

            FROM expenses

            WHERE date(expense_date)=date('now')
            """;

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            if (rs.next()) {

                return rs.getDouble(1);
            }
        }

        return 0;
    }

    // =====================================================
    // NEXT NUMBER
    // =====================================================

    public String getNextExpenseNo()
            throws SQLException {

        String sql =
                "SELECT COUNT(*)+1 FROM expenses";

        try (Statement stmt =
                     connection.createStatement();

             ResultSet rs =
                     stmt.executeQuery(sql)) {

            if (rs.next()) {

                return "EXP-" +
                        String.format(
                                "%05d",
                                rs.getInt(1)
                        );
            }
        }

        return "EXP-00001";
    }

    // =====================================================
    // ROW MAPPER
    // =====================================================

    private ExpenseModel mapRow(ResultSet rs)
            throws SQLException {

        return new ExpenseModel(

                rs.getInt("id"),

                rs.getString("expense_no"),

                rs.getString("expense_date"),

                rs.getString("expense_type"),

                rs.getString("vendor_name"),

                rs.getDouble("amount"),

                rs.getString("remarks"),

                rs.getString("attachment_path")
        );
    }
}

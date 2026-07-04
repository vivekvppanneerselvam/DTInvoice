package com.dt.inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubCategoryDAO {

    private final Connection connection;

    public SubCategoryDAO(Connection connection) {

        this.connection = connection;

        createTable();
    }

    // =========================================
    // CREATE TABLE
    // =========================================

    private void createTable() {

        try {

            Statement stmt =
                    connection.createStatement();

            stmt.execute("""

                    CREATE TABLE IF NOT EXISTS SUB_CATEGORIES (

                        ID INTEGER PRIMARY KEY AUTOINCREMENT,

                        CATEGORY_ID INTEGER,

                        SUB_CATEGORY_NAME TEXT

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

    public void insert(SubCategoryModel sub)
            throws SQLException {

        String sql = """

                INSERT INTO SUB_CATEGORIES(

                    CATEGORY_ID,
                    SUB_CATEGORY_NAME

                )

                VALUES(?, ?)

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1,
                sub.getCategoryId());

        ps.setString(2,
                sub.getSubCategoryName());

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // UPDATE
    // =========================================

    public void update(SubCategoryModel sub)
            throws SQLException {

        String sql = """

                UPDATE SUB_CATEGORIES

                SET SUB_CATEGORY_NAME=?

                WHERE ID=?

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1,
                sub.getSubCategoryName());

        ps.setInt(2,
                sub.getId());

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // DELETE
    // =========================================

    public void delete(int id)
            throws SQLException {

        String sql =
                "DELETE FROM SUB_CATEGORIES WHERE ID=?";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1,
                id);

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // GET ALL
    // =========================================

    public List<SubCategoryModel> getAll()
            throws SQLException {

        List<SubCategoryModel> list =
                new ArrayList<>();

        String sql = """

                SELECT

                    s.ID,
                    s.CATEGORY_ID,
                    s.SUB_CATEGORY_NAME,

                    c.CATEGORY_NAME

                FROM SUB_CATEGORIES s

                LEFT JOIN CATEGORIES c
                ON s.CATEGORY_ID = c.ID

                ORDER BY s.SUB_CATEGORY_NAME

                """;

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(sql);

        while (rs.next()) {

            list.add(

                    new SubCategoryModel(

                            rs.getInt("ID"),

                            rs.getInt("CATEGORY_ID"),

                            rs.getString("CATEGORY_NAME"),

                            rs.getString("SUB_CATEGORY_NAME")
                    )
            );
        }

        rs.close();
        stmt.close();

        return list;
    }

    // =========================================
    // GET BY CATEGORY
    // =========================================

    public List<SubCategoryModel> getByCategory(int categoryId)
            throws SQLException {

        List<SubCategoryModel> list =
                new ArrayList<>();

        String sql = """

                SELECT

                    s.ID,
                    s.CATEGORY_ID,
                    s.SUB_CATEGORY_NAME,

                    c.CATEGORY_NAME

                FROM SUB_CATEGORIES s

                LEFT JOIN CATEGORIES c
                ON s.CATEGORY_ID = c.ID

                WHERE s.CATEGORY_ID=?

                ORDER BY s.SUB_CATEGORY_NAME

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1,
                categoryId);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            list.add(

                    new SubCategoryModel(

                            rs.getInt("ID"),

                            rs.getInt("CATEGORY_ID"),

                            rs.getString("CATEGORY_NAME"),

                            rs.getString("SUB_CATEGORY_NAME")
                    )
            );
        }

        rs.close();
        ps.close();

        return list;
    }
}
package com.dt.inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private final Connection connection;

    public CategoryDAO(Connection connection) {

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

                    CREATE TABLE IF NOT EXISTS CATEGORIES (

                        ID INTEGER PRIMARY KEY AUTOINCREMENT,

                        CATEGORY_NAME TEXT

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

    public void insert(CategoryModel category)
            throws SQLException {

        String sql =
                "INSERT INTO CATEGORIES(CATEGORY_NAME) VALUES(?)";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1,
                category.getCategoryName());

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // UPDATE
    // =========================================

    public void update(CategoryModel category)
            throws SQLException {

        String sql =
                "UPDATE CATEGORIES SET CATEGORY_NAME=? WHERE ID=?";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1,
                category.getCategoryName());

        ps.setInt(2,
                category.getId());

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // DELETE
    // =========================================

    public void delete(int id)
            throws SQLException {

        String sql =
                "DELETE FROM CATEGORIES WHERE ID=?";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, id);

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // GET ALL
    // =========================================

    public List<CategoryModel> getAll()
            throws SQLException {

        List<CategoryModel> list =
                new ArrayList<>();

        String sql =
                "SELECT * FROM CATEGORIES ORDER BY CATEGORY_NAME";

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(sql);

        while (rs.next()) {

            list.add(

                    new CategoryModel(

                            rs.getInt("ID"),

                            rs.getString("CATEGORY_NAME")
                    )
            );
        }

        rs.close();
        stmt.close();

        return list;
    }
}
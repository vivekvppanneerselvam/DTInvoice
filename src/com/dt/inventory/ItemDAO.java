package com.dt.inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    private final Connection connection;

    public ItemDAO(Connection connection) {

        this.connection = connection;
        createTables();
    }

    // =========================================
    // INSERT ITEM
    // =========================================

    public void insert(ItemModel item)
            throws SQLException {

        String sql = """

                INSERT INTO NEW_ITEMS(

                    ITEM_CODE,
                    ITEM_NAME,
                    CATEGORY_ID,
                    SUB_CATEGORY_ID,
                    IMAGE_PATH,
                    GST_ENABLED,
                    GST_PERCENTAGE

                )

                VALUES (?, ?, ?, ?, ?, ?, ?)

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1,
                item.getItemCode());

        ps.setString(2,
                item.getItemName());

        ps.setInt(3,
                item.getCategoryId());

        if (item.getSubCategoryId() == 0) {

            ps.setNull(4,
                    Types.INTEGER);

        } else {

            ps.setInt(4,
                    item.getSubCategoryId());
        }

        ps.setString(5,
                item.getImagePath());

        ps.setInt(6,
                item.isGstEnabled() ? 1 : 0);

        ps.setDouble(7,
                item.getGstPercentage());

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // UPDATE ITEM
    // =========================================

    public void update(ItemModel item)
            throws SQLException {

        String sql = """

                UPDATE NEW_ITEMS

                SET

                    ITEM_CODE=?,
                    ITEM_NAME=?,
                    CATEGORY_ID=?,
                    SUB_CATEGORY_ID=?,
                    IMAGE_PATH=?,
                    GST_ENABLED=?,
                    GST_PERCENTAGE=?

                WHERE ID=?

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1,
                item.getItemCode());

        ps.setString(2,
                item.getItemName());

        ps.setInt(3,
                item.getCategoryId());

        if (item.getSubCategoryId() == 0) {

            ps.setNull(4,
                    Types.INTEGER);

        } else {

            ps.setInt(4,
                    item.getSubCategoryId());
        }

        ps.setString(5,
                item.getImagePath());

        ps.setInt(6,
                item.isGstEnabled() ? 1 : 0);

        ps.setDouble(7,
                item.getGstPercentage());

        ps.setInt(8,
                item.getId());

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // DELETE ITEM
    // =========================================

    public void delete(int id)
            throws SQLException {

        String sql =
                "DELETE FROM NEW_ITEMS WHERE ID=?";

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, id);

        ps.executeUpdate();

        ps.close();
    }

    // =========================================
    // GET ALL ITEMS
    // =========================================

    public List<ItemModel> getAll()
            throws SQLException {

        List<ItemModel> list =
                new ArrayList<>();

        String sql = """

                SELECT

                    i.ID,
                    i.ITEM_CODE,
                    i.ITEM_NAME,
                    i.CATEGORY_ID,
                    i.SUB_CATEGORY_ID,
                    i.IMAGE_PATH,
                    i.GST_ENABLED,
                    i.GST_PERCENTAGE,

                    c.CATEGORY_NAME,

                    s.SUB_CATEGORY_NAME

                FROM NEW_ITEMS i

                LEFT JOIN CATEGORIES c
                ON i.CATEGORY_ID = c.ID

                LEFT JOIN SUB_CATEGORIES s
                ON i.SUB_CATEGORY_ID = s.ID

                ORDER BY i.ITEM_NAME

                """;

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(sql);

        while (rs.next()) {

            ItemModel item =

                    new ItemModel(

                            rs.getInt("ID"),

                            rs.getString("ITEM_CODE"),

                            rs.getString("ITEM_NAME"),

                            rs.getInt("CATEGORY_ID"),

                            rs.getInt("SUB_CATEGORY_ID"),

                            rs.getString("CATEGORY_NAME"),

                            rs.getString("SUB_CATEGORY_NAME"),

                            rs.getString("IMAGE_PATH"),

                            rs.getInt("GST_ENABLED") == 1,

                            rs.getDouble("GST_PERCENTAGE")
                    );

            list.add(item);
        }

        rs.close();
        stmt.close();

        return list;
    }

    // =========================================
    // GET ITEM BY ID
    // =========================================

    public ItemModel getById(int id)
            throws SQLException {

        String sql = """

                SELECT

                    i.ID,
                    i.ITEM_CODE,
                    i.ITEM_NAME,
                    i.CATEGORY_ID,
                    i.SUB_CATEGORY_ID,
                    i.IMAGE_PATH,
                    i.GST_ENABLED,
                    i.GST_PERCENTAGE,

                    c.CATEGORY_NAME,

                    s.SUB_CATEGORY_NAME

                FROM NEW_ITEMS i

                LEFT JOIN CATEGORIES c
                ON i.CATEGORY_ID = c.ID

                LEFT JOIN SUB_CATEGORIES s
                ON i.SUB_CATEGORY_ID = s.ID

                WHERE i.ID=?

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, id);

        ResultSet rs =
                ps.executeQuery();

        ItemModel item = null;

        if (rs.next()) {

            item =

                    new ItemModel(

                            rs.getInt("ID"),

                            rs.getString("ITEM_CODE"),

                            rs.getString("ITEM_NAME"),

                            rs.getInt("CATEGORY_ID"),

                            rs.getInt("SUB_CATEGORY_ID"),

                            rs.getString("CATEGORY_NAME"),

                            rs.getString("SUB_CATEGORY_NAME"),

                            rs.getString("IMAGE_PATH"),

                            rs.getInt("GST_ENABLED") == 1,

                            rs.getDouble("GST_PERCENTAGE")
                    );
        }

        rs.close();
        ps.close();

        return item;
    }

    // =========================================
    // SEARCH ITEMS
    // =========================================

    public List<ItemModel> search(String keyword)
            throws SQLException {

        List<ItemModel> list =
                new ArrayList<>();

        String sql = """

                SELECT

                    i.ID,
                    i.ITEM_CODE,
                    i.ITEM_NAME,
                    i.CATEGORY_ID,
                    i.SUB_CATEGORY_ID,
                    i.IMAGE_PATH,
                    i.GST_ENABLED,
                    i.GST_PERCENTAGE,

                    c.CATEGORY_NAME,

                    s.SUB_CATEGORY_NAME

                FROM NEW_ITEMS i

                LEFT JOIN CATEGORIES c
                ON i.CATEGORY_ID = c.ID

                LEFT JOIN SUB_CATEGORIES s
                ON i.SUB_CATEGORY_ID = s.ID

                WHERE

                    LOWER(i.ITEM_NAME) LIKE ?

                    OR

                    LOWER(i.ITEM_CODE) LIKE ?

                ORDER BY i.ITEM_NAME

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        String searchValue =
                "%" + keyword.toLowerCase() + "%";

        ps.setString(1,
                searchValue);

        ps.setString(2,
                searchValue);

        ResultSet rs =
                ps.executeQuery();

        while (rs.next()) {

            list.add(

                    new ItemModel(

                            rs.getInt("ID"),

                            rs.getString("ITEM_CODE"),

                            rs.getString("ITEM_NAME"),

                            rs.getInt("CATEGORY_ID"),

                            rs.getInt("SUB_CATEGORY_ID"),

                            rs.getString("CATEGORY_NAME"),

                            rs.getString("SUB_CATEGORY_NAME"),

                            rs.getString("IMAGE_PATH"),

                            rs.getInt("GST_ENABLED") == 1,

                            rs.getDouble("GST_PERCENTAGE")
                    )
            );
        }

        rs.close();
        ps.close();

        return list;
    }

    private void createTables() {

        try {

            Statement stmt =
                    connection.createStatement();

            stmt.execute("""

            CREATE TABLE IF NOT EXISTS NEW_ITEMS (

                ID INTEGER PRIMARY KEY AUTOINCREMENT,

                ITEM_CODE TEXT,

                ITEM_NAME TEXT,

                CATEGORY_ID INTEGER,

                SUB_CATEGORY_ID INTEGER,

                IMAGE_PATH TEXT,

                GST_ENABLED INTEGER DEFAULT 0,

                GST_PERCENTAGE REAL DEFAULT 0

            )

        """);

            stmt.execute("""

            CREATE TABLE IF NOT EXISTS CATEGORIES (

                ID INTEGER PRIMARY KEY AUTOINCREMENT,

                CATEGORY_NAME TEXT

            )

        """);

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

    public int getLastInsertedId()
            throws SQLException {

        String sql =
                "SELECT last_insert_rowid()";

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(sql);

        int id = 0;

        if (rs.next()) {

            id = rs.getInt(1);
        }

        rs.close();

        stmt.close();

        return id;
    }
}
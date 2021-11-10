/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dao;

import java.util.*;
import java.sql.*;
import com.dt.dto.*;
import java.util.logging.*;


/**
 *
 * @author Dinesh
 */
public abstract class ItemsPersistence {

    private final static Logger logger = 
            Logger.getLogger(ItemsPersistence.class.getName());
    
    public static List<Item> getItems() throws Exception {

        ArrayList<Item> items = null;
        Connection connection = Database.getActiveYearConnection();
        int rowCount = getNumberOfItems(connection);

        items = new ArrayList<>(rowCount);
        Item item = null;

        try (Statement s = connection.createStatement()) {
            String sql = "SELECT * FROM items ORDER BY name";
            try (ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    item = new Item();
                    item.setItemId(rs.getInt(1));
                    item.setItemName(rs.getString(2));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            logger.logp(Level.SEVERE, ItemsPersistence.class.getName(),
                    "getItems", "Error in getting the items list", e);
            throw e;
        }

        return items;
    }

    private static int getNumberOfItems(Connection connection)
            throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = "SELECT count(id) FROM items";
            try (ResultSet rs = statement.executeQuery(sql)) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            logger.logp(Level.SEVERE, ItemsPersistence.class.getName(),
                    "getNumberOfItems", "Error in getting the number of items", ex);
            throw ex;
        }
    }

    /**
     *
     * @param add - The list of items to insert into the database
     * @param update - The list of item records to be updated in the
     * database
     * @param delete - The list of items to be deleted from the database
     * @return - The list of auto-generated items' identity numbers
     * @throws SQLException
     */
    public static int[] saveItems(List<? extends Item> add,
            List<? extends Item> update,
            List<? extends Item> delete) throws SQLException {

        Connection connection = Database.getActiveYearConnection();

        String insertSQL = "INSERT INTO items VALUES (DEFAULT, ?)";
        String updateSQL = "UPDATE items SET name = ? " 
                + "WHERE id = ?";
        String deleteSQL = "DELETE FROM items WHERE id = ?";
        int[] autoIDs = null;

        try {
            connection.setAutoCommit(false);

            if (!add.isEmpty()) {
                try (PreparedStatement psInsert = connection.prepareStatement(insertSQL,
                    new String[]{"ID"})) {
                    autoIDs = new int[add.size()];
                    int i = 0;
                    for (Item c : add) {
                        psInsert.setString(1, c.getItemName());
                        psInsert.executeUpdate();
                        try (ResultSet rs = psInsert.getGeneratedKeys()) {
                            while (rs.next()) {
                                autoIDs[i++] = rs.getInt(1);
                            }
                        }
                    } // end of for..each loop

                } //end of try with resources block for the insert statements
            }
            
            if (!update.isEmpty()) {
                 try (PreparedStatement psUpdate = connection.prepareStatement(updateSQL)) {
                    for (Item c : update) {
                        psUpdate.setString(1, c.getItemName());
                        psUpdate.setInt(2, c.getItemId());
                        psUpdate.addBatch();
                    }
                    psUpdate.executeBatch();
                }
            }
           
            if (!delete.isEmpty()) {
                 try (PreparedStatement psDelete = connection.prepareStatement(deleteSQL)) {
                    for (Item c : delete) {
                        psDelete.setInt(1, c.getItemId());
                        psDelete.addBatch();
                    }
                    psDelete.executeBatch();
                }
            }

            connection.commit();
            

        } catch (Exception e) {
            logger.logp(Level.SEVERE, ItemsPersistence.class.getName(),
                    "saveItems", "Error in saving the items list", e);
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally  {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }

        return autoIDs;
    }

}

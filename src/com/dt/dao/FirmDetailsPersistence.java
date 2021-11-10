/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dao;
import com.dt.dto.FirmDetails;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.logging.*;

/**
 *
 * @author Dinesh
 */
public abstract class FirmDetailsPersistence {
    private final static Logger logger = 
            Logger.getLogger(FirmDetailsPersistence.class.getName());
                    
    public static void saveData(FirmDetails firmDetails) throws SQLException {
        Connection connection = Database.getActiveYearConnection();
        int recordCount = getRecordCount(connection);
        String sql = null;
        if (recordCount == 0) {
            sql = "INSERT INTO firm_details VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE firm_details SET firm_name = ?, address = ?, " +
                    "phone_numbers = ?, email_address = ?, logo = ?";
        }
                 
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, firmDetails.getFirmName());
            ps.setString(2, firmDetails.getAddress());
           String phoneNumbers = firmDetails.getPhoneNumbers();
           if (phoneNumbers == null) {
               ps.setNull(3, java.sql.Types.VARCHAR);
           } else {
               ps.setString(3, phoneNumbers);
           }
           String emailAddress = firmDetails.getEmailAddress();
           if (emailAddress == null) {
               ps.setNull(4, java.sql.Types.VARCHAR);
           } else {
               ps.setString(4, emailAddress);
           }
           
           byte[] bytes = firmDetails.getLogo();
           if (bytes == null) {
               ps.setNull(5, java.sql.Types.BLOB);
           } else {
               ps.setBlob(5, new ByteArrayInputStream(bytes), bytes.length);
           }
           
           ps.executeUpdate();
        } catch (Exception e) {
             logger.logp(Level.SEVERE, FirmDetailsPersistence.class.getName(),
                    "saveData", "Error in saving firm details", e);
            throw e;
        }
        
    }
    
    public static FirmDetails getData() throws SQLException {
       
        String sql = "SELECT * FROM firm_details";
        Connection connection = Database.getActiveYearConnection();
        
        FirmDetails firmDetails = null;
        try(Statement s = connection.createStatement()){
            
            try(ResultSet result = s.executeQuery(sql)) {
                if (!result.next()) { //record may not exist
                    return null;
                }
                 firmDetails = new FirmDetails();
                firmDetails.setFirmName(result.getString(1));
                firmDetails.setAddress(result.getString(2));
                String text = result.getString(3); //phone numbers
                if (!result.wasNull()){
                    firmDetails.setPhoneNumbers(text);
                }
                text = result.getString(4); //email address
                if (!result.wasNull()) {
                    firmDetails.setEmailAddress(text);
                }
                
                Blob blob = result.getBlob(5); //firm's logo
                if (!result.wasNull()) {
                    int length = (int) blob.length();
                    byte[] bytes = blob.getBytes(1L, length);
                    firmDetails.setLogo(bytes);
                }
            } 
        } catch(Exception e) {
             logger.logp(Level.SEVERE, FirmDetailsPersistence.class.getName(),
                    "getData", "Error in getting firm details", e);
            throw e;
        }
        
        return firmDetails;
        
    }
    
    private static int getRecordCount(Connection connection) throws SQLException{
        try(Statement s = connection.createStatement()){
            String sql = "SELECT COUNT(firm_name) FROM firm_details";
            try(ResultSet resultSet = s.executeQuery(sql)) {
                 resultSet.next();
                 return resultSet.getInt(1);
            }
        }catch(Exception e) {
             logger.logp(Level.SEVERE, FirmDetailsPersistence.class.getName(),
                    "getRecordCount", "Error in getting record count", e);
            throw e;
        }
    }
    
}

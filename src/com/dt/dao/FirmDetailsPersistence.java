package com.dt.dao;

import com.dt.dto.FirmDetails;

import java.io.ByteArrayInputStream;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dinesh
 */
public abstract class FirmDetailsPersistence {

    private final static Logger logger =
            Logger.getLogger(
                    FirmDetailsPersistence.class.getName()
            );

    public static void saveData(
            FirmDetails firmDetails
    ) throws SQLException {

        Connection connection =
                Database.getActiveYearConnection();

    /*
     Create missing columns
     */
        createColumnsIfNeeded(connection);

        int recordCount =
                getRecordCount(connection);

        String sql;

        if (recordCount == 0) {

        /*
         INSERT
         */
            sql =
                    "INSERT INTO firm_details ("
                            + "firm_name, "
                            + "address, "
                            + "phone_numbers, "
                            + "email_address, "
                            + "logo, "
                            + "firm_sub_name, "
                            + "gst_number, "
                            + "fssai_number, "
                            + "review_url"
                            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        } else {

        /*
         UPDATE EXISTING SINGLE ROW
         */
            sql =
                    "UPDATE firm_details SET "
                            + "firm_name = ?, "
                            + "address = ?, "
                            + "phone_numbers = ?, "
                            + "email_address = ?, "
                            + "logo = ?, "
                            + "firm_sub_name = ?, "
                            + "gst_number = ?, "
                            + "fssai_number = ?, "
                            + "review_url = ? "
                            + "WHERE firm_name IS NOT NULL";
        }

        try (
                PreparedStatement ps =
                        connection.prepareStatement(sql)
        ) {

        /*
         =================================================
         BASIC DETAILS
         =================================================
         */

            ps.setString(
                    1,
                    firmDetails.getFirmName()
            );

            ps.setString(
                    2,
                    firmDetails.getAddress()
            );

        /*
         =================================================
         PHONE
         =================================================
         */

            String phoneNumbers =
                    firmDetails.getPhoneNumbers();

            if (phoneNumbers == null
                    || phoneNumbers.trim().isEmpty()) {

                ps.setNull(
                        3,
                        java.sql.Types.VARCHAR
                );

            } else {

                ps.setString(
                        3,
                        phoneNumbers
                );
            }

        /*
         =================================================
         EMAIL
         =================================================
         */

            String emailAddress =
                    firmDetails.getEmailAddress();

            if (emailAddress == null
                    || emailAddress.trim().isEmpty()) {

                ps.setNull(
                        4,
                        java.sql.Types.VARCHAR
                );

            } else {

                ps.setString(
                        4,
                        emailAddress
                );
            }

        /*
         =================================================
         LOGO
         =================================================
         */

            byte[] bytes =
                    firmDetails.getLogo();

            if (bytes == null) {

                ps.setNull(
                        5,
                        java.sql.Types.BLOB
                );

            } else {

                ps.setBlob(
                        5,
                        new ByteArrayInputStream(bytes),
                        bytes.length
                );
            }

        /*
         =================================================
         SUB NAME
         =================================================
         */

            String subName =
                    firmDetails.getFirmSubName();

            if (subName == null
                    || subName.trim().isEmpty()) {

                ps.setNull(
                        6,
                        java.sql.Types.VARCHAR
                );

            } else {

                ps.setString(
                        6,
                        subName
                );
            }

        /*
         =================================================
         GST
         =================================================
         */

            String gstNumber =
                    firmDetails.getGstNumber();

            if (gstNumber == null
                    || gstNumber.trim().isEmpty()) {

                ps.setNull(
                        7,
                        java.sql.Types.VARCHAR
                );

            } else {

                ps.setString(
                        7,
                        gstNumber
                );
            }

        /*
         =================================================
         FSSAI
         =================================================
         */

            String fssaiNumber =
                    firmDetails.getFssaiNumber();

            if (fssaiNumber == null
                    || fssaiNumber.trim().isEmpty()) {

                ps.setNull(
                        8,
                        java.sql.Types.VARCHAR
                );

            } else {

                ps.setString(
                        8,
                        fssaiNumber
                );
            }

        /*
         =================================================
         REVIEW URL
         =================================================
         */

            String reviewUrl =
                    firmDetails.getReviewUrl();

            if (reviewUrl == null
                    || reviewUrl.trim().isEmpty()) {

                ps.setNull(
                        9,
                        java.sql.Types.VARCHAR
                );

            } else {

                ps.setString(
                        9,
                        reviewUrl
                );
            }

        /*
         =================================================
         EXECUTE
         =================================================
         */

            int updatedRows =
                    ps.executeUpdate();

            System.out.println(
                    "Firm Details Saved : "
                            + updatedRows
            );

        } catch (Exception e) {

            logger.logp(
                    Level.SEVERE,
                    FirmDetailsPersistence.class.getName(),
                    "saveData",
                    "Error in saving firm details",
                    e
            );

            throw e;
        }
    }

    public static FirmDetails getData()
            throws SQLException {

        String sql =
                "SELECT * FROM firm_details";

        Connection connection =
                Database.getActiveYearConnection();

        /*
         Create missing columns
         */
        createColumnsIfNeeded(connection);

        FirmDetails firmDetails = null;

        try (
                Statement s =
                        connection.createStatement()
        ) {

            try (
                    ResultSet result =
                            s.executeQuery(sql)
            ) {

                if (!result.next()) {
                    return null;
                }

                firmDetails =
                        new FirmDetails();

                /*
                 Firm name
                 */
                firmDetails.setFirmName(
                        result.getString("firm_name")
                );

                /*
                 Address
                 */
                firmDetails.setAddress(
                        result.getString("address")
                );

                /*
                 Phone numbers
                 */
                String text =
                        result.getString(
                                "phone_numbers"
                        );

                if (!result.wasNull()) {

                    firmDetails.setPhoneNumbers(
                            text
                    );
                }

                /*
                 Email
                 */
                text =
                        result.getString(
                                "email_address"
                        );

                if (!result.wasNull()) {

                    firmDetails.setEmailAddress(
                            text
                    );
                }

                /*
                 Logo
                 */
                Blob blob =
                        result.getBlob("logo");

                if (!result.wasNull()) {

                    int length =
                            (int) blob.length();

                    byte[] bytes =
                            blob.getBytes(
                                    1L,
                                    length
                            );

                    firmDetails.setLogo(bytes);
                }

                /*
                 Firm sub name
                 */
                text =
                        result.getString(
                                "firm_sub_name"
                        );

                if (!result.wasNull()) {

                    firmDetails.setFirmSubName(
                            text
                    );
                }

                /*
                 GST number
                 */
                text =
                        result.getString(
                                "gst_number"
                        );

                if (!result.wasNull()) {

                    firmDetails.setGstNumber(
                            text
                    );
                }

                /*
                 FSSAI number
                 */
                text =
                        result.getString(
                                "fssai_number"
                        );

                if (!result.wasNull()) {

                    firmDetails.setFssaiNumber(
                            text
                    );
                }

                /*
                 Review URL
                 */
                text =
                        result.getString(
                                "review_url"
                        );

                if (!result.wasNull()) {

                    firmDetails.setReviewUrl(
                            text
                    );
                }
            }

        } catch (Exception e) {

            logger.logp(
                    Level.SEVERE,
                    FirmDetailsPersistence.class.getName(),
                    "getData",
                    "Error in getting firm details",
                    e
            );

            throw e;
        }

        return firmDetails;
    }

    private static int getRecordCount(
            Connection connection
    ) throws SQLException {

        try (
                Statement s =
                        connection.createStatement()
        ) {

            String sql =
                    "SELECT COUNT(firm_name) "
                            + "FROM firm_details";

            try (
                    ResultSet resultSet =
                            s.executeQuery(sql)
            ) {

                resultSet.next();

                return resultSet.getInt(1);
            }

        } catch (Exception e) {

            logger.logp(
                    Level.SEVERE,
                    FirmDetailsPersistence.class.getName(),
                    "getRecordCount",
                    "Error in getting record count",
                    e
            );

            throw e;
        }
    }

    /*
     ==================================================
     AUTO ALTER TABLE SECTION
     ==================================================
     */
    private static void createColumnsIfNeeded(
            Connection connection
    ) {

        addColumnIfNotExists(
                connection,
                "firm_sub_name",
                "VARCHAR(100)"
        );

        addColumnIfNotExists(
                connection,
                "gst_number",
                "VARCHAR(30)"
        );

        addColumnIfNotExists(
                connection,
                "fssai_number",
                "VARCHAR(30)"
        );

        addColumnIfNotExists(
                connection,
                "review_url",
                "VARCHAR(500)"
        );
    }

    private static void addColumnIfNotExists(
            Connection connection,
            String columnName,
            String columnDefinition
    ) {

        String sql =
                "ALTER TABLE firm_details "
                        + "ADD COLUMN "
                        + columnName
                        + " "
                        + columnDefinition;

        try (
                Statement statement =
                        connection.createStatement()
        ) {

            statement.executeUpdate(sql);

            logger.info(
                    "Added column: "
                            + columnName
            );

        } catch (Exception e) {

            /*
             Ignore because column
             probably already exists.
             */

            logger.fine(
                    "Column already exists: "
                            + columnName
            );
        }
    }
}
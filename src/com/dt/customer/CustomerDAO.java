package com.dt.customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private final Connection connection;

    public CustomerDAO(Connection connection) throws SQLException {

        this.connection = connection;

        createTables();
    }

    // INSERT

    public void insert(CustomerModel customer)
            throws SQLException {

        String sql = """

                INSERT INTO CUSTOMER_MASTER(

                    CUSTOMER_TYPE,
                    CUSTOMER_CODE,
                    CUSTOMER_NAME,
                    GST_NUMBER,
                    PHONE_NO,
                    ALT_PHONE_NO,
                    EMAIL,
                    ADDRESS1,
                    ADDRESS2,
                    CITY,
                    STATE,
                    PINCODE,
                    OPENING_BALANCE,
                    BALANCE_TYPE

                )

                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

                """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1, customer.getCustomerType());

        ps.setString(2, customer.getCustomerCode());

        ps.setString(3, customer.getCustomerName());

        ps.setString(4, customer.getGstNumber());

        ps.setString(5, customer.getPhoneNo());

        ps.setString(6, customer.getAltPhoneNo());

        ps.setString(7, customer.getEmail());

        ps.setString(8, customer.getAddress1());

        ps.setString(9, customer.getAddress2());

        ps.setString(10, customer.getCity());

        ps.setString(11, customer.getState());

        ps.setString(12, customer.getPincode());

        ps.setDouble(13, customer.getOpeningBalance());

        ps.setString(14, customer.getBalanceType());

        ps.executeUpdate();

        ps.close();
    }

    // GET ALL

    public List<CustomerModel> getAll()
            throws SQLException {

        List<CustomerModel> list =
                new ArrayList<>();

        String sql = """

                SELECT *

                FROM CUSTOMER_MASTER

                ORDER BY CUSTOMER_NAME

                """;

        Statement stmt =
                connection.createStatement();

        ResultSet rs =
                stmt.executeQuery(sql);

        while (rs.next()) {

            list.add(

                    new CustomerModel(

                            rs.getInt("ID"),

                            rs.getString("CUSTOMER_TYPE"),

                            rs.getString("CUSTOMER_CODE"),

                            rs.getString("CUSTOMER_NAME"),

                            rs.getString("GST_NUMBER"),

                            rs.getString("PHONE_NO"),

                            rs.getString("ALT_PHONE_NO"),

                            rs.getString("EMAIL"),

                            rs.getString("ADDRESS1"),

                            rs.getString("ADDRESS2"),

                            rs.getString("CITY"),

                            rs.getString("STATE"),

                            rs.getString("PINCODE"),

                            rs.getDouble("OPENING_BALANCE"),

                            rs.getString("BALANCE_TYPE")
                    )
            );
        }

        rs.close();

        stmt.close();

        return list;
    }

    public void createTables() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS CUSTOMER_MASTER (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    CUSTOMER_TYPE VARCHAR(20),
                    CUSTOMER_CODE VARCHAR(50) NOT NULL,
                    CUSTOMER_NAME VARCHAR(200) NOT NULL,
                    GST_NUMBER VARCHAR(20),
                    PHONE_NO VARCHAR(20),
                    ALT_PHONE_NO VARCHAR(20),
                    EMAIL VARCHAR(500),
                    ADDRESS1 VARCHAR(500),
                    ADDRESS2 VARCHAR(500),
                    CITY VARCHAR(100),
                    STATE VARCHAR(100),
                    PINCODE VARCHAR(10),
                    OPENING_BALANCE REAL DEFAULT 0.0,
                    BALANCE_TYPE VARCHAR(20)
                )
                """;

        Statement stmt =
                connection.createStatement();

        stmt.execute(sql);

        stmt.close();

    }

    public List<CustomerModel> findAll()
            throws SQLException {

        return getAll();
    }

    public void update(CustomerModel customer)
            throws SQLException {

        String sql = """

            UPDATE CUSTOMER_MASTER

            SET

                CUSTOMER_TYPE=?,
                CUSTOMER_CODE=?,
                CUSTOMER_NAME=?,
                GST_NUMBER=?,
                PHONE_NO=?,
                ALT_PHONE_NO=?,
                EMAIL=?,
                ADDRESS1=?,
                ADDRESS2=?,
                CITY=?,
                STATE=?,
                PINCODE=?,
                OPENING_BALANCE=?,
                BALANCE_TYPE=?

            WHERE ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setString(1,
                customer.getCustomerType());

        ps.setString(2,
                customer.getCustomerCode());

        ps.setString(3,
                customer.getCustomerName());

        ps.setString(4,
                customer.getGstNumber());

        ps.setString(5,
                customer.getPhoneNo());

        ps.setString(6,
                customer.getAltPhoneNo());

        ps.setString(7,
                customer.getEmail());

        ps.setString(8,
                customer.getAddress1());

        ps.setString(9,
                customer.getAddress2());

        ps.setString(10,
                customer.getCity());

        ps.setString(11,
                customer.getState());

        ps.setString(12,
                customer.getPincode());

        ps.setDouble(13,
                customer.getOpeningBalance());

        ps.setString(14,
                customer.getBalanceType());

        ps.setInt(15,
                customer.getId());

        ps.executeUpdate();

        ps.close();
    }

    public void delete(int id)
            throws SQLException {

        String sql = """

            DELETE FROM CUSTOMER_MASTER

            WHERE ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, id);

        ps.executeUpdate();

        ps.close();
    }

    public CustomerModel getById(int id)
            throws SQLException {

        String sql = """

            SELECT *

            FROM CUSTOMER_MASTER

            WHERE ID=?

            """;

        PreparedStatement ps =
                connection.prepareStatement(sql);

        ps.setInt(1, id);

        ResultSet rs =
                ps.executeQuery();

        CustomerModel customer = null;

        if (rs.next()) {

            customer = build(rs);
        }

        rs.close();

        ps.close();

        return customer;
    }

    private CustomerModel build(ResultSet rs)
            throws SQLException {

        return new CustomerModel(

                rs.getInt("ID"),

                rs.getString("CUSTOMER_TYPE"),

                rs.getString("CUSTOMER_CODE"),

                rs.getString("CUSTOMER_NAME"),

                rs.getString("GST_NUMBER"),

                rs.getString("PHONE_NO"),

                rs.getString("ALT_PHONE_NO"),

                rs.getString("EMAIL"),

                rs.getString("ADDRESS1"),

                rs.getString("ADDRESS2"),

                rs.getString("CITY"),

                rs.getString("STATE"),

                rs.getString("PINCODE"),

                rs.getDouble("OPENING_BALANCE"),

                rs.getString("BALANCE_TYPE")
        );
    }


}
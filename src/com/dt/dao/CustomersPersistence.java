/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dao;

import java.util.*;
import java.sql.*;
import com.dt.dto.*;

import javafx.beans.property.ReadOnlyStringWrapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

/**
 *
 * @author Dinesh
 */
public abstract class CustomersPersistence {

	private final static Logger logger = 
			Logger.getLogger(CustomersPersistence.class.getName());

	public static List<Customer> getCustomers() throws Exception {

		ArrayList<Customer> customers = null;
		Connection connection = Database.getActiveYearConnection();
		int rowCount = getNumberOfCustomers(connection);

		customers = new ArrayList<>(rowCount);
		Customer customer = null;
		String balanceType = null;

		try (Statement s = connection.createStatement()) {
			String sql = "SELECT cust_id, name, add1, add2, city, tamil_name, tamil_add1, tamil_add2, tamil_add3, phone_numbers, opening_balance, balance_type FROM customers ORDER BY name";
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					customer = new Customer();
					customer.setId(rs.getInt(1));
					customer.setName(rs.getString(2));
					customer.setAddress1(rs.getString(3));
					customer.setAddress2(rs.getString(4));
					customer.setCity(rs.getString(5));
					customer.setTamilName(rs.getString(6));
					customer.setTamilAddress1(rs.getString(7));
					customer.setTamilAddress2(rs.getString(8));
					customer.setTamilAddress3(rs.getString(9));
					customer.setPhoneNumbers(rs.getString(10));
					customer.setOpeningBalance(rs.getBigDecimal(11));
					balanceType = rs.getString(12);

					if (!rs.wasNull()) {
						if (balanceType.equalsIgnoreCase("c")) {
							customer.setBalanceType(BalanceType.CREDIT);
						} else if (balanceType.equalsIgnoreCase("d")) {
							customer.setBalanceType(BalanceType.DEBIT);
						}
					}
					customers.add(customer);
				}
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, CustomersPersistence.class.getName(),
					"getCustomers", "Error getting Customers List", e);
			throw e;
		}

		return customers;
	}

	private static int getNumberOfCustomers(Connection connection)
			throws SQLException {
		try (Statement statement = connection.createStatement()) {
			String sql = "SELECT count(cust_id) FROM customers";
			try (ResultSet rs = statement.executeQuery(sql)) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException ex) {
			logger.logp(Level.SEVERE, CustomersPersistence.class.getName(),
					"getNumberOfCustomers", "Error getting number of customers", ex);
			throw ex;
		}
	}

	/**
	 *
	 * @param add - The list of customers to insert into the database
	 * @param update - The list of customer records to be updated in the
	 * database
	 * @param delete - The list of customers to be deleted from the database
	 * @return - The list of auto-generated customers' identity numbers
	 * @throws SQLException
	 */
	public static int[] saveCustomers(List<? extends Customer> add,
			List<? extends Customer> update,
			List<? extends Customer> delete) throws SQLException {

		Connection connection = Database.getActiveYearConnection();

		String newInsertSQL = "INSERT INTO customers (CUST_ID, NAME, CITY, PHONE_NUMBERS, OPENING_BALANCE,  BALANCE_TYPE, ADD1, ADD2, TAMIL_ADD1, TAMIL_ADD2, TAMIL_ADD3,TAMIL_NAME) "
				+ "VALUES (DEFAULT, ?,?,?, ?,?,?, ?,?,?, ?,?)";
		//String insertSQL = "INSERT INTO customers VALUES (DEFAULT, ?, ?, ?, ?, ?)";
		String updateSQL = "UPDATE customers SET name = ?, city = ?, "
				+ "phone_numbers = ?, opening_balance = ?, balance_type = ? "
				+" ADD1 = ?, ADD2 = ?, TAMIL_ADD1 = ?, TAMIL_ADD2 = ?, TAMIL_ADD3 = ?,TAMIL_NAME = ? "                
				+ "WHERE cust_id = ?";
		String deleteSQL = "DELETE FROM customers WHERE cust_id = ?";
		int[] autoIDs = null;

		try {
			connection.setAutoCommit(false);

			if (!add.isEmpty()) {
				try (PreparedStatement psInsert = connection.prepareStatement(newInsertSQL,
						new String[]{"CUST_ID"})) {
					autoIDs = new int[add.size()];
					int i = 0;

					for (Customer c : add) {
						fillParameterValues(psInsert, c, false);
						psInsert.executeUpdate();
						try (ResultSet rs = psInsert.getGeneratedKeys()) {
							if (rs != null) {
								while (rs.next()) {
									autoIDs[i++] = rs.getInt(1);
								}
							}
						}
					} // end of for..each loop

				} //end of try with resources block for the insert statements
			}

			if (!update.isEmpty()) {
				try (PreparedStatement psUpdate = connection.prepareStatement(updateSQL)) {
					for (Customer c : update) {
						fillParameterValues(psUpdate, c, true);
						psUpdate.addBatch();
					}
					psUpdate.executeBatch();
				}
			}

			if (!delete.isEmpty()) {
				try (PreparedStatement psDelete = connection.prepareStatement(deleteSQL)) {
					for (Customer c : delete) {
						psDelete.setInt(1, c.getId());
						psDelete.addBatch();
					}
					psDelete.executeBatch();
				}
			}

			connection.commit();
			connection.setAutoCommit(true);

		} catch (Exception e) {
			logger.logp(Level.SEVERE, CustomersPersistence.class.getName(),
					"saveCustomers", "Error in saving customers list", e);
			connection.rollback();
		}

		return autoIDs;
	}

	private static void fillParameterValues(PreparedStatement ps, Customer c,
			boolean isUpdate) throws SQLException {

		ps.setString(1, c.getName());

		String city = c.getCity();
		if (city == null) {
			ps.setNull(2, Types.VARCHAR);
		} else {
			ps.setString(2, city);
		}

		String phoneNumbers = c.getPhoneNumbers();
		if (phoneNumbers == null) {
			ps.setNull(3, Types.VARCHAR);
		} else {
			ps.setString(3, phoneNumbers);
		}

		BigDecimal amount = c.getOpeningBalance();
		if (amount == null) {
			ps.setNull(4, Types.DECIMAL);
		} else {
			ps.setBigDecimal(4, amount);
		}

		BalanceType balanceType = c.getBalanceType();

		if (balanceType == null) {
			ps.setNull(5, Types.CHAR);
		} else if (balanceType == BalanceType.CREDIT) {
			ps.setString(5, "C");
		} else if (balanceType == BalanceType.DEBIT) {
			ps.setString(5, "D");
		}



		String add1 = c.getAddress1();
		String add2 = c.getAddress2();		
		String tamilAdd1 = c.getTamilAddress1();
		String tamilAdd2 = c.getTamilAddress2();
		String tamilAdd3 = c.getTamilAddress3();
		String tamilName = c.getTamilName();
		if (add1 == null)  ps.setNull(6, Types.VARCHAR);
		else  ps.setString(6, add1);
		if (add2 == null)  ps.setNull(7, Types.VARCHAR);
		else  ps.setString(7, add2);        
		if (tamilAdd1 == null)  ps.setNull(8, Types.VARCHAR);
		else  ps.setString(8, tamilAdd1);
		if (tamilAdd2 == null)  ps.setNull(9, Types.VARCHAR);
		else  ps.setString(9, tamilAdd2);
		if (tamilAdd3 == null)  ps.setNull(10, Types.VARCHAR);
		else  ps.setString(10, tamilAdd3);
		if (tamilName == null)  ps.setNull(11, Types.VARCHAR);
		else  ps.setString(11, tamilName);


		if (isUpdate) {
			ps.setInt(12, c.getId());
		}
	}

	public static BigDecimal getCustomerBalance(int customerId) throws Exception {
		return getCustomerBalance(customerId, null);
	}

	public static BigDecimal getCustomerBalance(int customerId, LocalDate endDate) 
			throws Exception {
		String sql = null;
		if (endDate == null) {
			sql = "VALUES customer_balance(?, null)";
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String formattedDate = endDate.format(formatter);
			sql = "VALUES customer_balance(?, '" + formattedDate + "')";
		}

		Connection connection = null;
		BigDecimal balance = null;

		try {
			connection = Database.getActiveYearConnection();
			try(PreparedStatement ps = connection.prepareStatement(sql)) {
				ps.setInt(1, customerId);
				try(ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						balance = rs.getBigDecimal(1);
					}
				}

			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, CustomersPersistence.class.getName(),
					"getCustomerBalance", "Error in getting customer's balance.", e);
			throw e;
		}

		return balance;
	}

	public static BigDecimal getCustomerOpeningBalance(int customerId) 
			throws Exception {
		String sql = "VALUES customer_opening_balance(?)";
		Connection connection = null;
		BigDecimal balance = null;

		try {
			connection = Database.getActiveYearConnection();
			try(PreparedStatement ps = connection.prepareStatement(sql)) {
				ps.setInt(1, customerId);
				try(ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						balance = rs.getBigDecimal(1);
					}
				}

			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, CustomersPersistence.class.getName(),
					"getCustomerOpeningBalance", 
					"Error in getting customer's opening balance", e);
			throw e;
		}

		return balance;
	}
}

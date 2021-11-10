/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dao;

import com.dt.dto.*;
import com.dt.utils.Utility;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

/**
 *
 * @author Dinesh
 */
public abstract class InvoicePersistence {

	private final static Logger logger = 
			Logger.getLogger(InvoicePersistence.class.getName());

	public static void saveInvoice(Invoice invoice) throws Exception {

		int invoiceId = invoice.getInvoiceId();
		boolean updateInvoice = (invoiceId != 0);
		Connection connection = null;

		try {
			connection = Database.getActiveYearConnection();
			connection.setAutoCommit(false);

			if (updateInvoice) {
				updateInvoice(invoice, connection);
				deleteInvoiceItems(invoice, connection);
			} else {
				int id = saveNewInvoice(invoice, connection);
				invoice.setInvoiceId(id);
			}

			saveInvoiceItems(invoice, connection);
			if(!invoice.getIsCashInvoice()) {
				updateCustDueAmount(invoice, connection);
			}
			connection.commit();

		} catch (Exception e) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(),
					"saveInvoice", "Error in saving the invoice", e);
			if (connection != null) {
				connection.rollback();
			}
			throw e;
		}finally {
			if (connection != null) {
				connection.setAutoCommit(true);
			}
		}

	}

	private static void deleteInvoiceItems(Invoice invoice, Connection connection)
			throws Exception {
		String sql = "DELETE FROM invoice_items WHERE invoice_id = " + 
				invoice.getInvoiceId();

		try(Statement s = connection.createStatement()) {
			s.executeUpdate(sql);
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(),
					"deleteInvoiceItems", "Error in deleting invoice items", ex);
			throw ex;
		}
	}

	private static void updateCustDueAmount(Invoice invoice, Connection connection)throws Exception{
		String sql = "UPDATE CUSTOMERS set OPENING_BALANCE = ? " +                
				"WHERE cust_id = ?";
		try(PreparedStatement ps = connection.prepareStatement(sql)) {
			BigDecimal totalDueAmount = invoice.getTotalDueAmount();
			if (totalDueAmount == null) {
				ps.setNull(1, Types.DECIMAL);
			} else {
				ps.setBigDecimal(1, totalDueAmount);
			}			
			int id = invoice.getCustomer().getId();
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(),
					"updateCustDueAmount", "Error in updating the customer due amount", ex);
			throw ex;
		}
	}

	private static void updateInvoice(Invoice invoice, Connection connection)
			throws Exception {

		String sql = "UPDATE invoices set invoicedate = ?, iscashinvoice = ?, " +
				"customerid = ?, discount = ?, additionalcharge = ?, paid = ?, previousdue = ?, totaldue = ? " +
				"WHERE id = ?";

		try(PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setDate(1, new Date(Utility.getEpochMilli(invoice.getInvoiceDate())));
			ps.setBoolean(2, invoice.getIsCashInvoice());

			if (!invoice.getIsCashInvoice()) {
				int id = invoice.getCustomer().getId();
				ps.setInt(3, id);
			} else {
				ps.setNull(3, Types.INTEGER);
			}

			BigDecimal discount = invoice.getDiscount();
			if (discount == null) {
				ps.setNull(4, Types.DECIMAL);
			} else {
				ps.setBigDecimal(4, discount);
			}
			BigDecimal charge = invoice.getAdditionalCharge();
			if (charge == null) {
				ps.setNull(5, Types.DECIMAL);
			} else {
				ps.setBigDecimal(5, charge);
			}
			BigDecimal paid = invoice.getPaid();
			if (paid == null) {
				ps.setNull(6, Types.DECIMAL);
			} else {
				ps.setBigDecimal(6, paid);
			}
			BigDecimal prevDueAmount = invoice.getPrevDueAmount();
			if (prevDueAmount == null) {
				ps.setNull(7, Types.DECIMAL);
			} else {
				ps.setBigDecimal(7, prevDueAmount);
			}
			BigDecimal totalDueAmount = invoice.getTotalDueAmount();
			if (totalDueAmount == null) {
				ps.setNull(8, Types.DECIMAL);
			} else {
				ps.setBigDecimal(8, totalDueAmount);
			}
			

			ps.setInt(9, invoice.getInvoiceId());

			ps.executeUpdate();
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(),
					"updateInvoice", "Error in updating the invoice", ex);
			throw ex;
		}
	}

	private static void saveInvoiceItems(Invoice invoice, Connection connection) 
			throws Exception{

		String itemSQL = "INSERT INTO invoice_items values (?, ?, ?, ?, ?)";
		int invoiceId = invoice.getInvoiceId();

		try(PreparedStatement ps = connection.prepareStatement(itemSQL)) {
			for(InvoiceItem item: invoice.getInvoiceItems()) {
				ps.setInt(1, invoiceId);
				ps.setInt(2, item.getItem().getItemId());
				ps.setBigDecimal(3, item.getRate());
				ps.setInt(4, item.getUnit().getUnitId());
				ps.setBigDecimal(5, item.getQuantity());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(),
					"saveInvoiceItems", "Error in saving invoice items", ex);
			throw ex;
		}
	}

	/**
	 * Saves a new invoice as oppose to updating an existing invoice
	 * @param invoice - Invoice data
	 * @param connection - Database connection
	 * @return - Id of the new invoice returned by the database
	 * @throws Exception 
	 */
	public static int saveNewInvoice(Invoice invoice, Connection connection )
			throws Exception {

		String insertSQL = "INSERT INTO invoices values (default, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int invoiceId = 0;

		try (PreparedStatement ps = connection.prepareStatement(insertSQL,
				new String[]{"ID"})) {
			ps.setDate(1, new Date(Utility.getEpochMilli(invoice.getInvoiceDate())));
			ps.setBoolean(2, invoice.getIsCashInvoice());
			if (!invoice.getIsCashInvoice()) {
				int id = invoice.getCustomer().getId();
				ps.setInt(3, id);
			} else {
				ps.setNull(3, Types.INTEGER);
			}
			BigDecimal discount = invoice.getDiscount();
			if (discount == null) {
				ps.setNull(4, Types.DECIMAL);
			} else {
				ps.setBigDecimal(4, discount);
			}
			BigDecimal charge = invoice.getAdditionalCharge();
			if (charge == null) {
				ps.setNull(5, Types.DECIMAL);
			} else {
				ps.setBigDecimal(5, charge);
			}
			BigDecimal paid = invoice.getPaid();
			if (paid == null) {
				ps.setNull(6, Types.DECIMAL);
			} else {
				ps.setBigDecimal(6, paid);
			}
			
			ps.setNull(7, Types.DECIMAL);
			
			BigDecimal prevDueAmount = invoice.getPrevDueAmount();
			if (prevDueAmount == null) {
				ps.setNull(8, Types.DECIMAL);
			} else {
				ps.setBigDecimal(8, prevDueAmount);
			}
			BigDecimal totalDueAmount = invoice.getTotalDueAmount();
			if (totalDueAmount == null) {
				ps.setNull(9, Types.DECIMAL);
			} else {
				ps.setBigDecimal(9, totalDueAmount);
			}
			ps.executeUpdate();
			try(ResultSet rs = ps.getGeneratedKeys()) {
				rs.next();
				invoiceId = rs.getInt(1);
			}
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(),
					"saveNewInvoice", "Error in saving the new invoice", ex);
			throw ex;
		}

		return invoiceId;
	}


	public static Invoice getInvoice(int invoiceNumber) throws Exception {
		Connection connection = Database.getActiveYearConnection();
		Invoice invoice = new Invoice();
		invoice.setInvoiceId(invoiceNumber);

		List<InvoiceItem> invoiceItems = getInvoiceItems(connection, invoiceNumber);
		invoice.setInvoiceItems(invoiceItems);

		String sql = "SELECT i.*, c.name, c.city FROM invoices i " + 
				"LEFT JOIN customers c ON i.customerid = c.cust_id "
				+ " WHERE i.id = " + invoiceNumber;
		try(Statement s = connection.createStatement()) {
			try(ResultSet rs = s.executeQuery(sql)) {
				if (!rs.next()) {
					return null;
				}
				invoice.setInvoiceDate(rs.getDate(2).toLocalDate());
				invoice.setIsCashInvoice(rs.getBoolean(3));
				if (!invoice.getIsCashInvoice()) {
					Customer customer = new Customer();
					customer.setId(rs.getInt(4));
					customer.setName(rs.getString(11));
					String city = rs.getString(12);
					if (!rs.wasNull()) {
						customer.setCity(city);
					}
					invoice.setCustomer(customer);
				}
				BigDecimal amount = rs.getBigDecimal(5); //discount
				if (!rs.wasNull()) {
					invoice.setDiscount(amount);
				}
				amount = rs.getBigDecimal(6); //additional charge
				if (!rs.wasNull()) {
					invoice.setAdditionalCharge(amount);
				}
				amount = rs.getBigDecimal(7); //Paid
				if (!rs.wasNull()) {
					invoice.setPaid(amount);
				}
				amount = rs.getBigDecimal(8); //Balance
				if (!rs.wasNull()) {
					invoice.setBalance(amount);
				}
				amount = rs.getBigDecimal(9); //Prev. Due amount
				if (!rs.wasNull()) {
					invoice.setPrevDueAmount(amount);
				}
				amount = rs.getBigDecimal(10); //Total Due Amount
				if (!rs.wasNull()) {
					invoice.setTotalDueAmount(amount);
				}

			}
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(),
					"getInvoice", "Error in getting invoice details", ex);
			throw ex;
		}

		return invoice;

	}

	public static List<InvoiceItem> getInvoiceItems(Connection connection, int invoiceNumber) 
			throws Exception {
		List<InvoiceItem> invoiceItems = new ArrayList<>(10);
		String sql = "SELECT inv.*, i.name, mu.name " +
				"FROM invoice_items inv " +
				"INNER JOIN items i ON inv.item_id = i.id " +
				"INNER JOIN measurement_units mu on inv.measurement_unit = mu.id " 
				+ " WHERE invoice_id = " + invoiceNumber;

		InvoiceItem invoiceItem = null;
		Item item = null;
		MeasurementUnit unit = null;

		try(Statement s = connection.createStatement())  {
			try(ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					invoiceItem = new InvoiceItem();
					item = new Item();
					item.setItemId(rs.getInt(2)); //item id
					item.setItemName(rs.getString(6)); //item name
					invoiceItem.setItem(item);
					invoiceItem.setRate(rs.getBigDecimal(3)); //rate
					unit = new MeasurementUnit();
					unit.setUnitId(rs.getInt(4)); //measurement unit
					unit.setUnitName(rs.getString(7)); //unit name
					invoiceItem.setUnit(unit);
					invoiceItem.setQuantity(rs.getBigDecimal(5)); // quantity
					invoiceItems.add(invoiceItem);
				}
			}
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(),
					"getInvoiceItems", "Error in getting invoice items", ex);
			throw ex;
		}

		return invoiceItems;
	}
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.*;


/**
 * @author vivek
 *
 */
public class Invoice {

    private ReadOnlyIntegerWrapper invoiceId;
    private ReadOnlyObjectWrapper<LocalDate> invoiceDate;
    private ReadOnlyBooleanWrapper isCashInvoice;
    private ReadOnlyObjectWrapper<Customer> customer;
    private ReadOnlyObjectWrapper<BigDecimal> discount;
    private ReadOnlyObjectWrapper<BigDecimal> additionalCharge;
    private ReadOnlyObjectWrapper<BigDecimal> prevDueAmount;
   	private ReadOnlyObjectWrapper<BigDecimal> totalDueAmount;     
    private ReadOnlyObjectWrapper<BigDecimal> paid;
    private ReadOnlyObjectWrapper<BigDecimal> balance;
    private List<InvoiceItem> invoiceItems;
    
    public Invoice() {
        invoiceId = new ReadOnlyIntegerWrapper(0);
        invoiceDate = new ReadOnlyObjectWrapper<>(null);
        isCashInvoice = new ReadOnlyBooleanWrapper(true);
        customer = new ReadOnlyObjectWrapper<>(null);
        discount = new ReadOnlyObjectWrapper<>(null);
        additionalCharge = new ReadOnlyObjectWrapper<>(null);
        paid = new ReadOnlyObjectWrapper<>(null);
        prevDueAmount = new ReadOnlyObjectWrapper<>(null);
        totalDueAmount = new ReadOnlyObjectWrapper<>(null);
        balance = new ReadOnlyObjectWrapper<>(null);
        invoiceItems = new ArrayList<>();
    }
    
    
    public BigDecimal getBalance() {
		return balance.get();
	}

	public void setBalance(BigDecimal value) {
		balance.set(value);
	}

    public BigDecimal getPrevDueAmount() {
		return prevDueAmount.get();
	}


	public void setPrevDueAmount(BigDecimal value) {
		prevDueAmount.set(value);
	}


	public BigDecimal getTotalDueAmount() {
		return totalDueAmount.get();
	}


	public void setTotalDueAmount(BigDecimal value) {
		totalDueAmount.set(value);
	}


	public BigDecimal getPaid() {
        return paid.get();
    }
	public void setPaid(BigDecimal value) {
		paid.set(value);
	}
	
	

    



	public LocalDate getInvoiceDate() {
        return invoiceDate.get();
    }

    public void setInvoiceDate(LocalDate date) {
        invoiceDate.set(date);
    }

    public ReadOnlyObjectProperty<LocalDate> invoiceDateProperty() {
        return invoiceDate.getReadOnlyProperty();
    }

    public boolean getIsCashInvoice() {
        return isCashInvoice.get();
    }

    public void setIsCashInvoice(boolean value) {
        isCashInvoice.set(value);
    }

    public ReadOnlyBooleanProperty isCashInvoiceProperty() {
        return isCashInvoice.getReadOnlyProperty();
    }

    public int getInvoiceId() {
        return invoiceId.get();
    }

    public void setInvoiceId(int id) {
        invoiceId.set(id);
    }

    public ReadOnlyIntegerProperty invoiceIdProperty() {
        return invoiceId.getReadOnlyProperty();
    }

    public Customer getCustomer() {
        return customer.get();
    }

    public void setCustomer(Customer c) {
        customer.set(c);
    }

    public ReadOnlyObjectProperty<Customer> customerProperty() {
        return customer.getReadOnlyProperty();
    }

    public BigDecimal getDiscount() {
        return discount.get();
    }

    public void setDiscount(BigDecimal value) {
        discount.set(value);
    }

    public ReadOnlyObjectProperty<BigDecimal> discountProperty() {
        return discount.getReadOnlyProperty();
    }

    public BigDecimal getAdditionalCharge() {
        return additionalCharge.get();
    }

    public void setAdditionalCharge(BigDecimal charge) {
        additionalCharge.set(charge);
    }

    public ReadOnlyObjectProperty<BigDecimal> additionalChargeProperty() {
        return additionalCharge.getReadOnlyProperty();
    }
    
    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }
    
    public void setInvoiceItems(List<InvoiceItem> items) {
        invoiceItems = items;
    }

    @Override
    public int hashCode() {
        return invoiceId.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Invoice)) {
            return false;
        }

        Invoice invoice = (Invoice) obj;
        return invoice.getInvoiceId() == invoiceId.get();
    }
    
    

}

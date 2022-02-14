/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dto;

import java.math.BigDecimal;
import javafx.beans.property.*;


/**
 * @author vivek
 *
 */
public class Customer {
	private final ReadOnlyIntegerWrapper id;
	private final ReadOnlyStringWrapper name;
	private final ReadOnlyStringWrapper city; 
	private final ReadOnlyStringWrapper phoneNumbers;
	private final ReadOnlyObjectWrapper<BigDecimal> openingBalance; 
	private final ReadOnlyObjectWrapper<BalanceType> balanceType;

	//NEW
	private final ReadOnlyStringWrapper address1; 
	private final ReadOnlyStringWrapper address2; 
	private final ReadOnlyStringWrapper tamilAddress1; 
	private final ReadOnlyStringWrapper tamilAddress2; 
	private final ReadOnlyStringWrapper tamilAddress3;
	private final ReadOnlyStringWrapper tamilName;

	public Customer() {
		id = new ReadOnlyIntegerWrapper(this, "id", 0);
		name = new ReadOnlyStringWrapper(this, "name");
		city = new ReadOnlyStringWrapper(this, "city");
		phoneNumbers = new ReadOnlyStringWrapper(this, "phoneNumbers");
		openingBalance =  new ReadOnlyObjectWrapper<>(this, "openingBalance");
		balanceType = new ReadOnlyObjectWrapper<>(this, "balanceType");
		
		address1 = new ReadOnlyStringWrapper(this, "address1");
		address2= new ReadOnlyStringWrapper(this, "address2");
		tamilAddress1= new ReadOnlyStringWrapper(this, "tamilAddress1");
		tamilAddress2= new ReadOnlyStringWrapper(this, "tamilAddress2");
		tamilAddress3= new ReadOnlyStringWrapper(this, "tamilAddress3");
		tamilName= new ReadOnlyStringWrapper(this, "tamilName");
	}
	
	
	public String getAddress1() {
		return address1.get();
	}

	public void setAddress1(String value) {
		address1.set(value);
	}

	
	public String getAddress2() {
		return address2.get();
	}

	public void setAddress2(String value) {
		address2.set(value);
	}

	
	public String getTamilAddress1() {
		return tamilAddress1.get();
	}

	public void setTamilAddress1(String value) {
		tamilAddress1.set(value);
	}
	
	public String getTamilAddress2() {
		return tamilAddress2.get();
	}

	public void setTamilAddress2(String value) {
		tamilAddress2.set(value);
	}
	
	public String getTamilAddress3() {
		return tamilAddress3.get();
	}

	public void setTamilAddress3(String value) {
		tamilAddress3.set(value);
	}
	
	public String getTamilName() {
		return tamilName.get();
	}

	public void setTamilName(String value) {
		tamilName.set(value);
	}



	
	
	
	public Customer(Customer customer) {
		this();
		id.set(customer.getId());
		name.set(customer.getName());
		city.set(customer.getCity());
		phoneNumbers.set(customer.getPhoneNumbers());
		openingBalance.set(customer.getOpeningBalance());
		balanceType.set(customer.getBalanceType());
		address1.set(customer.getAddress1()); 
		address2.set(customer.getAddress2());
		tamilAddress1.set(customer.getTamilAddress1()); 
		tamilAddress2.set(customer.getTamilAddress2()); 
		tamilAddress3.set(customer.getTamilAddress3());
		tamilName.set(customer.getTamilName());
	}

	public int getId() {
		return id.get();
	}

	public void setId(int value) {
		id.set(value);
	}

	public ReadOnlyIntegerProperty idProperty() {
		return id.getReadOnlyProperty();
	}

	public String getName() {
		return name.get();
	}

	public void setName(String value) {
		name.set(value);
	}

	public ReadOnlyStringProperty nameProperty() {
		return name.getReadOnlyProperty();
	}

	public String getPhoneNumbers() {
		return phoneNumbers.get();
	}

	public void setPhoneNumbers(String value) {
		phoneNumbers.set(value);
	}

	public ReadOnlyStringProperty phoneNumbersProperty() {
		return phoneNumbers.getReadOnlyProperty();
	}

	public String getCity() {
		return city.get();
	}

	public void setCity(String value) {
		city.set(value);
	}

	public ReadOnlyStringProperty cityProperty() {
		return city.getReadOnlyProperty();
	}

	public BigDecimal getOpeningBalance() {
		return openingBalance.get();
	}

	public void setOpeningBalance(BigDecimal value) {
		openingBalance.set(value);
	}

	public ReadOnlyObjectProperty<BigDecimal> openingBalanceProperty() {
		return openingBalance.getReadOnlyProperty();
	}

	public BalanceType getBalanceType() {
		return balanceType.get();
	}

	public void setBalanceType(BalanceType value) {
		balanceType.set(value);
	}

	public ReadOnlyObjectProperty<BalanceType> balanceTypeProperty() {
		return balanceType.getReadOnlyProperty();
	}

	@Override
	public String toString() {
		if (name.get() != null) {
			return name.get();
		}
		return super.toString(); 
	}

	@Override
	public int hashCode() {
		int code =  id.get();
		if (code != 0) {
			return 0;
		}

		String custName = name.get();
		if (custName != null) {
			return custName.hashCode();
		}

		return super.hashCode();

	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (! (obj instanceof Customer)) {
			return false;
		}

		Customer cust = (Customer) obj;
		if (this.getId() == 0 && cust.getId() == 0) {
			return this.getName().equalsIgnoreCase(cust.getName());
		}

		return (this.getId() == cust.getId());

	}





}



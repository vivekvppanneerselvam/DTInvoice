-- ============================

-- This file was created using Derby's dblook utility.
-- Timestamp: 2016-09-23 12:17:00.024
-- Source database is: e:\learning\javafx\fxbilling\database\db
-- Connection URL is: jdbc:derby:e:\learning\javafx\fxbilling\database\db
-- appendLogs: false

-- ----------------------------------------------
-- DDL Statements for schemas
-- ----------------------------------------------

CREATE SCHEMA "DTINVIOCE";

-- ----------------------------------------------
-- DDL Statements for functions
-- ----------------------------------------------

CREATE FUNCTION "APP"."CUSTOMER_OPENING_BALANCE" ("CUSTOMERID" INTEGER) RETURNS DECIMAL(11,2) LANGUAGE JAVA PARAMETER STYLE JAVA READS SQL DATA CALLED ON NULL INPUT EXTERNAL NAME 'fom.dt.storeproc.DBMethods.getCustomerOpeningBalance' ;

CREATE FUNCTION "APP"."CUSTOMER_BALANCE" ("CUSTOMERID" INTEGER,"ENDDATESTRING" CHAR(10)) RETURNS DECIMAL(11,2) LANGUAGE JAVA PARAMETER STYLE JAVA READS SQL DATA CALLED ON NULL INPUT EXTERNAL NAME 'com.dt.storeproc.DBMethods.getCustomerBalance' ;

CREATE FUNCTION "APP"."INVOICE_TOTAL" ("INVOICENUMBER" INTEGER) RETURNS DECIMAL(11,2) LANGUAGE JAVA PARAMETER STYLE JAVA READS SQL DATA CALLED ON NULL INPUT EXTERNAL NAME 'com.dt.storeproc.DBMethods.getInvoiceTotal' ;

-- ----------------------------------------------
-- DDL Statements for tables
-- ----------------------------------------------
CREATE TABLE "APP"."USER_DETAILS" ("USER_ID" VARCHAR(70) NOT NULL, "USER_NAME" VARCHAR(120) NOT NULL, "USER_ROLE" VARCHAR(70) NOT NULL,  "PASSWORD" VARCHAR(70) NOT NULL);

CREATE TABLE "APP"."FIRM_DETAILS" ("FIRM_NAME" VARCHAR(70) NOT NULL, "ADDRESS" VARCHAR(120) NOT NULL, "PHONE_NUMBERS" VARCHAR(120), "EMAIL_ADDRESS" VARCHAR(50), "LOGO" BLOB(524288));

CREATE TABLE "APP"."MEASUREMENT_UNITS" ("ID" INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "NAME" VARCHAR(40) NOT NULL, "ABBREVIATION" VARCHAR(15));

CREATE TABLE "APP"."CUSTOMERS" ("CUST_ID" INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "NAME" VARCHAR(70) NOT NULL, "ADD1" VARCHAR(500), "ADD2" VARCHAR(500), "EMAIL_ADD" VARCHAR(300),  "CITY" VARCHAR(40), "TAMIL_NAME" VARCHAR(70), "TAMIL_ADD1" VARCHAR(500), "TAMIL_ADD2" VARCHAR(500), "TAMIL_ADD3" VARCHAR(500), "PHONE_NUMBERS" VARCHAR(120),  "PHONE_NO2" VARCHAR(120), "CELL_NO" VARCHAR(120), "OPENING_BALANCE" DECIMAL(10,2), "BALANCE_TYPE" CHAR(1));

CREATE TABLE "APP"."ITEMS" ("ID" INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "NAME" VARCHAR(150) NOT NULL);

CREATE TABLE "APP"."PAYMENTS" ("ID" INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "CUSTOMERID" INTEGER NOT NULL, "PAYMENTDATE" DATE NOT NULL, "AMOUNT" DECIMAL(12,2), "PAYMENTMODE" VARCHAR(20) NOT NULL);

CREATE TABLE "APP"."INVOICES" ("ID" INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "INVOICEDATE" DATE NOT NULL, "ISCASHINVOICE" BOOLEAN NOT NULL, "CUSTOMERID" INTEGER, "DISCOUNT" DECIMAL(8,2), "ADDITIONALCHARGE" DECIMAL(8,2));

CREATE TABLE "APP"."INVOICE_ITEMS" ("INVOICE_ID" INTEGER NOT NULL, "ITEM_ID" INTEGER NOT NULL, "RATE" DECIMAL(8,2) NOT NULL, "MEASUREMENT_UNIT" INTEGER NOT NULL, "QUANTITY" DECIMAL(9,3) NOT NULL);

CREATE TABLE "APP"."PAYMENT_DETAILS" ("PAYMENTID" INTEGER NOT NULL, "INSTRUMENTNUMBER" VARCHAR(15) NOT NULL, "INSTRUMENTDATE" DATE NOT NULL, "DRAWANATBANK" VARCHAR(100), "AMOUNTREALISED" BOOLEAN DEFAULT false);

-- ----------------------------------------------
-- DDL Statements for indexes
-- ----------------------------------------------

CREATE INDEX "APP"."IDX_INVOICES_INVOICEDATE" ON "APP"."INVOICES" ("INVOICEDATE");

CREATE INDEX "APP"."IDX_INVOICES_ISCASHINVOICE" ON "APP"."INVOICES" ("ISCASHINVOICE");

CREATE INDEX "APP"."IDX_PAYMENT_DETAILS_AMOUNTREALISED" ON "APP"."PAYMENT_DETAILS" ("AMOUNTREALISED");

CREATE INDEX "APP"."IDX_PAYMENTS_PAYMENTMODE" ON "APP"."PAYMENTS" ("PAYMENTMODE");

CREATE INDEX "APP"."IDX_PAYMENTS_AMOUNT" ON "APP"."PAYMENTS" ("AMOUNT");

CREATE INDEX "APP"."IDX_PAYMENTS_PAYMENTDATE" ON "APP"."PAYMENTS" ("PAYMENTDATE");

-- ----------------------------------------------
-- DDL Statements for keys
-- ----------------------------------------------

-- PRIMARY/UNIQUE
ALTER TABLE "APP"."INVOICES" ADD CONSTRAINT "INVOICES_PK_ID" PRIMARY KEY ("ID");

ALTER TABLE "APP"."CUSTOMERS" ADD CONSTRAINT "SQL160228221537110" PRIMARY KEY ("CUST_ID");

ALTER TABLE "APP"."CUSTOMERS" ADD CONSTRAINT "UNIQUE_NAME" UNIQUE ("NAME");

ALTER TABLE "APP"."MEASUREMENT_UNITS" ADD CONSTRAINT "PK_UOM" PRIMARY KEY ("ID");

ALTER TABLE "APP"."MEASUREMENT_UNITS" ADD CONSTRAINT "UNQ_UOM_NAME" UNIQUE ("NAME");

ALTER TABLE "APP"."ITEMS" ADD CONSTRAINT "ITEMS_PK_ID" PRIMARY KEY ("ID");

ALTER TABLE "APP"."ITEMS" ADD CONSTRAINT "ITEMS_UNQ_NAME" UNIQUE ("NAME");

ALTER TABLE "APP"."PAYMENTS" ADD CONSTRAINT "PAYMENTS_PK_ID" PRIMARY KEY ("ID");

ALTER TABLE "APP"."INVOICE_ITEMS" ADD CONSTRAINT "INVOICE_ITEMS_PK" PRIMARY KEY ("INVOICE_ID", "ITEM_ID");

-- FOREIGN
ALTER TABLE "APP"."INVOICES" ADD CONSTRAINT "INVOICES_FK_CUSTOMERID" FOREIGN KEY ("CUSTOMERID") REFERENCES "APP"."CUSTOMERS" ("CUST_ID") ON DELETE RESTRICT ON UPDATE NO ACTION;

ALTER TABLE "APP"."PAYMENT_DETAILS" ADD CONSTRAINT "PAYMENT_DETAILS_FK_PAYMENTID" FOREIGN KEY ("PAYMENTID") REFERENCES "APP"."PAYMENTS" ("ID") ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE "APP"."PAYMENTS" ADD CONSTRAINT "PAYMENTS_FK_CUSTOMERID" FOREIGN KEY ("CUSTOMERID") REFERENCES "APP"."CUSTOMERS" ("CUST_ID") ON DELETE RESTRICT ON UPDATE NO ACTION;

ALTER TABLE "APP"."INVOICE_ITEMS" ADD CONSTRAINT "INVOICE_ITEMS_FK_INVOICE_ID" FOREIGN KEY ("INVOICE_ID") REFERENCES "APP"."INVOICES" ("ID") ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE "APP"."INVOICE_ITEMS" ADD CONSTRAINT "INVOICE_ITEMS_FK_ITEM_ID" FOREIGN KEY ("ITEM_ID") REFERENCES "APP"."ITEMS" ("ID") ON DELETE RESTRICT ON UPDATE NO ACTION;

ALTER TABLE "APP"."INVOICE_ITEMS" ADD CONSTRAINT "INVOICE_ITEMS_FK_UOM" FOREIGN KEY ("MEASUREMENT_UNIT") REFERENCES "APP"."MEASUREMENT_UNITS" ("ID") ON DELETE RESTRICT ON UPDATE NO ACTION;

-- ----------------------------------------------
-- DDL Statements for checks
-- ----------------------------------------------

ALTER TABLE "APP"."CUSTOMERS" ADD CONSTRAINT "VALID_BALANCE_TYPE" CHECK (upper(balance_type) in ('C', 'D'));

ALTER TABLE "APP"."PAYMENTS" ADD CONSTRAINT "PAYMENTS_CK_AMOUNT" CHECK (amount > 0.0);

ALTER TABLE "APP"."PAYMENTS" ADD CONSTRAINT "PAYMENTS_CHECK_PAYMENTMODE" CHECK (lower(paymentMode) in          ('cash', 'cheque', 'dd','banktransfer'));

ALTER TABLE "APP"."INVOICES" ADD "PAID" DECIMAL(12,2);  
ALTER TABLE "APP"."INVOICES" ADD "BALANCE" DECIMAL(12,2);
ALTER TABLE "APP"."INVOICES" ADD "PREVIOUSDUE" DECIMAL(12,2);
ALTER TABLE "APP"."INVOICES" ADD "TOTALDUE" DECIMAL(12,2); 


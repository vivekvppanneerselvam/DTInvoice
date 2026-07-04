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
-- =========================================
-- CATEGORY TABLE
-- =========================================

CREATE TABLE "APP"."CATEGORIES" (

                                ID INTEGER NOT NULL
                                    GENERATED ALWAYS AS IDENTITY
                                        (START WITH 1, INCREMENT BY 1),

                                CATEGORY_NAME VARCHAR(150) NOT NULL,

                                CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                PRIMARY KEY (ID)
);

-- =========================================
-- SUB CATEGORY TABLE
-- =========================================

CREATE TABLE "APP"."SUB_CATEGORIES" (

                                    ID INTEGER NOT NULL
                                        GENERATED ALWAYS AS IDENTITY
                                            (START WITH 1, INCREMENT BY 1),

                                    CATEGORY_ID INTEGER,

                                    SUB_CATEGORY_NAME VARCHAR(150) NOT NULL,

                                    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                    PRIMARY KEY (ID),

                                    CONSTRAINT FK_SUB_CATEGORY
                                        FOREIGN KEY (CATEGORY_ID)
                                            REFERENCES "APP"."CATEGORIES"(ID)
);

-- =========================================
-- NEW ITEMS TABLE
-- =========================================

CREATE TABLE "APP"."NEW_ITEMS" (

                               ID INTEGER NOT NULL
                                   GENERATED ALWAYS AS IDENTITY
                                       (START WITH 1, INCREMENT BY 1),

                               ITEM_CODE VARCHAR(50) NOT NULL,

                               ITEM_NAME VARCHAR(200) NOT NULL,

                               CATEGORY_ID INTEGER,

                               SUB_CATEGORY_ID INTEGER,

                               IMAGE_PATH VARCHAR(500),

                               GST_ENABLED SMALLINT DEFAULT 0,

                               GST_PERCENTAGE DOUBLE DEFAULT 0,

                               CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                               PRIMARY KEY (ID),

                               CONSTRAINT UK_NEW_ITEM_CODE
                                   UNIQUE (ITEM_CODE),

                               CONSTRAINT FK_NEW_ITEM_CATEGORY
                                   FOREIGN KEY (CATEGORY_ID)
                                       REFERENCES "APP"."CATEGORIES"(ID),

                               CONSTRAINT FK_NEW_ITEM_SUB_CATEGORY
                                   FOREIGN KEY (SUB_CATEGORY_ID)
                                       REFERENCES "APP"."SUB_CATEGORIES"(ID)
);

-- =========================================
-- ITEM VARIANTS TABLE
-- =========================================

CREATE TABLE "APP"."ITEM_VARIANTS" (

                                   ID INTEGER NOT NULL
                                       GENERATED ALWAYS AS IDENTITY
                                           (START WITH 1, INCREMENT BY 1),

                                   ITEM_ID INTEGER NOT NULL,

                                   VARIANT_NAME VARCHAR(150),

                                   CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                   PRIMARY KEY (ID),

                                   CONSTRAINT FK_VARIANT_ITEM
                                       FOREIGN KEY (ITEM_ID)
                                           REFERENCES APP.NEW_ITEMS(ID)
);

-- =========================================
-- ITEM MEASUREMENTS TABLE
-- =========================================

CREATE TABLE "APP"."ITEM_MEASUREMENTS" (

                                       ID INTEGER NOT NULL
                                           GENERATED ALWAYS AS IDENTITY
                                               (START WITH 1, INCREMENT BY 1),

                                       ITEM_ID INTEGER NOT NULL,

                                       VARIANT_ID INTEGER,

                                       QUANTITY DOUBLE NOT NULL,

                                       UNIT VARCHAR(20) NOT NULL,

                                       SELLING_PRICE DOUBLE DEFAULT 0,

                                       PURCHASE_PRICE DOUBLE DEFAULT 0,

                                       BARCODE VARCHAR(100),

                                       CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                       PRIMARY KEY (ID),

                                       CONSTRAINT FK_MEASUREMENT_ITEM
                                           FOREIGN KEY (ITEM_ID)
                                               REFERENCES APP.NEW_ITEMS(ID),

                                       CONSTRAINT FK_MEASUREMENT_VARIANT
                                           FOREIGN KEY (VARIANT_ID)
                                               REFERENCES "APP"."ITEM_VARIANTS"(ID)
);

-- =========================================
-- STOCK TABLE
-- =========================================

CREATE TABLE "APP"."NEW_STOCK" (

                               ID INTEGER NOT NULL
                                   GENERATED ALWAYS AS IDENTITY
                                       (START WITH 1, INCREMENT BY 1),

                               MEASUREMENT_ID INTEGER NOT NULL,

                               CURRENT_STOCK DOUBLE DEFAULT 0,

                               MINIMUM_STOCK DOUBLE DEFAULT 0,

                               LAST_UPDATED TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                               PRIMARY KEY (ID),

                               CONSTRAINT FK_NEW_STOCK_MEASUREMENT
                                   FOREIGN KEY (MEASUREMENT_ID)
                                       REFERENCES "APP"."ITEM_MEASUREMENTS"(ID)
);

-- =========================================
-- STOCK TRANSACTIONS
-- =========================================

CREATE TABLE "APP"."STOCK_TRANSACTIONS" (

                                        ID INTEGER NOT NULL
                                            GENERATED ALWAYS AS IDENTITY
                                                (START WITH 1, INCREMENT BY 1),

                                        MEASUREMENT_ID INTEGER NOT NULL,

                                        TRANSACTION_TYPE VARCHAR(20),

                                        QUANTITY DOUBLE,

                                        REMARKS VARCHAR(500),

                                        CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                        PRIMARY KEY(ID),

                                        CONSTRAINT FK_STOCK_TXN_MEASUREMENT
                                            FOREIGN KEY (MEASUREMENT_ID)
                                                REFERENCES "APP"."ITEM_MEASUREMENTS"(ID)
);

-- =========================================
-- PURCHASERS
-- =========================================

CREATE TABLE "APP"."PURCHASERS" (

                                ID INTEGER NOT NULL
                                    GENERATED ALWAYS AS IDENTITY
                                        (START WITH 1, INCREMENT BY 1),

                                PURCHASER_CODE VARCHAR(50),

                                PURCHASER_NAME VARCHAR(200) NOT NULL,

                                GSTIN VARCHAR(50),

                                MOBILE VARCHAR(30),

                                ADDRESS VARCHAR(500),

                                PENDING_AMOUNT DOUBLE DEFAULT 0,

                                TOTAL_BILLS INTEGER DEFAULT 0,

                                PENDING_BILLS INTEGER DEFAULT 0,

                                SETTLED_BILLS INTEGER DEFAULT 0,

                                TOTAL_PURCHASE_AMOUNT DOUBLE DEFAULT 0,

                                TOTAL_PAID_AMOUNT DOUBLE DEFAULT 0,

                                LAST_PURCHASE_DATE VARCHAR(50),

                                CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                PRIMARY KEY(ID),

                                CONSTRAINT UK_PURCHASER_CODE
                                    UNIQUE(PURCHASER_CODE),

                                CONSTRAINT UK_PURCHASER_GST
                                    UNIQUE(GSTIN)
);

-- =========================================
-- BILLS
-- =========================================

CREATE TABLE "APP"."BILLS" (

                           ID INTEGER NOT NULL
                               GENERATED ALWAYS AS IDENTITY
                                   (START WITH 1, INCREMENT BY 1),

                           BILL_NO VARCHAR(100),

                           PURCHASER_ID INTEGER,

                           BILL_DATE VARCHAR(50),

                           TOTAL_AMOUNT DOUBLE,

                           PAID_AMOUNT DOUBLE DEFAULT 0,

                           BALANCE_AMOUNT DOUBLE DEFAULT 0,

                           PAYMENT_STATUS VARCHAR(50),

                           CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                           PRIMARY KEY(ID),

                           CONSTRAINT UK_BILL_NO
                               UNIQUE(BILL_NO),

                           CONSTRAINT FK_BILL_PURCHASER
                               FOREIGN KEY(PURCHASER_ID)
                                   REFERENCES "APP"."PURCHASERS"(ID)
);

-- =========================================
-- BILL ITEMS
-- =========================================

CREATE TABLE "APP"."BILL_ITEMS" (

                                ID INTEGER NOT NULL
                                    GENERATED ALWAYS AS IDENTITY
                                        (START WITH 1, INCREMENT BY 1),

                                BILL_ID INTEGER,

                                ITEM_ID INTEGER,

                                ITEM_NAME VARCHAR(200),

                                QTY DOUBLE,

                                RATE DOUBLE,

                                AMOUNT DOUBLE,

                                PRIMARY KEY(ID),

                                CONSTRAINT FK_BILL_ITEM_BILL
                                    FOREIGN KEY(BILL_ID)
                                        REFERENCES "APP"."BILLS"(ID),

                                CONSTRAINT FK_BILL_ITEM_ITEM
                                    FOREIGN KEY(ITEM_ID)
                                        REFERENCES APP.NEW_ITEMS(ID)
);

-- =========================================
-- PAYMENTS NEW
-- =========================================

CREATE TABLE "APP"."PAYMENTS_NEW" (

                                  ID INTEGER NOT NULL
                                      GENERATED ALWAYS AS IDENTITY
                                          (START WITH 1, INCREMENT BY 1),

                                  BILL_ID INTEGER,

                                  PURCHASER_ID INTEGER,

                                  PAYMENT_DATE VARCHAR(50),

                                  AMOUNT DOUBLE,

                                  PAYMENT_MODE VARCHAR(50),

                                  REFERENCE_NO VARCHAR(100),

                                  CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  PRIMARY KEY(ID),

                                  CONSTRAINT FK_PAYMENT_BILL
                                      FOREIGN KEY(BILL_ID)
                                          REFERENCES "APP"."BILLS"(ID),

                                  CONSTRAINT FK_PAYMENT_PURCHASER
                                      FOREIGN KEY(PURCHASER_ID)
                                          REFERENCES "APP"."PURCHASERS"(ID)
);


-- =========================================
-- CUSTOMERS
-- =========================================

CREATE TABLE "APP"."CUSTOMERS" (

                                   ID INTEGER NOT NULL
                                       GENERATED ALWAYS AS IDENTITY
                                           (START WITH 1, INCREMENT BY 1),

                                   CUSTOMER_CODE VARCHAR(50),

                                   CUSTOMER_NAME VARCHAR(200) NOT NULL,

                                   GST_NO VARCHAR(50),

                                   PHONE_NO VARCHAR(30),

                                   ALT_PHONE_NO VARCHAR(30),

                                   EMAIL VARCHAR(200),

                                   ADDRESS1 VARCHAR(500),

                                   ADDRESS2 VARCHAR(500),

                                   CITY VARCHAR(100),

                                   PINCODE VARCHAR(20),

                                   IS_GST SMALLINT DEFAULT 0,

                                   IS_CREDIT SMALLINT DEFAULT 0,

                                   OPENING_BALANCE DOUBLE DEFAULT 0,

                                   CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                   PRIMARY KEY(ID)
);

-- =========================================
-- CUSTOMER LEDGER
-- =========================================

CREATE TABLE "APP"."CUSTOMER_LEDGER" (

                                         ID INTEGER NOT NULL
                                             GENERATED ALWAYS AS IDENTITY
                                                 (START WITH 1, INCREMENT BY 1),

                                         CUSTOMER_ID INTEGER,

                                         ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                         ENTRY_TYPE VARCHAR(20),

                                         REF_NO VARCHAR(100),

                                         DEBIT DOUBLE DEFAULT 0,

                                         CREDIT DOUBLE DEFAULT 0,

                                         BALANCE DOUBLE DEFAULT 0,

                                         REMARKS VARCHAR(500),

                                         PRIMARY KEY(ID),

                                         CONSTRAINT FK_LEDGER_CUSTOMER
                                             FOREIGN KEY (CUSTOMER_ID)
                                                 REFERENCES APP.CUSTOMERS(ID)
);

-- =========================================
-- INVOICES
-- =========================================

CREATE TABLE "APP"."INVOICES" (

                                  ID INTEGER NOT NULL
                                      GENERATED ALWAYS AS IDENTITY
                                          (START WITH 1, INCREMENT BY 1),

                                  INVOICE_NO VARCHAR(100),

                                  CUSTOMER_ID INTEGER,

                                  SALE_TYPE VARCHAR(20),

                                  PAYMENT_MODE VARCHAR(30),

                                  BILL_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  SUB_TOTAL DOUBLE DEFAULT 0,

                                  DISCOUNT DOUBLE DEFAULT 0,

                                  DELIVERY_CHARGE DOUBLE DEFAULT 0,

                                  GST_TOTAL DOUBLE DEFAULT 0,

                                  GRAND_TOTAL DOUBLE DEFAULT 0,

                                  PAID_AMOUNT DOUBLE DEFAULT 0,

                                  BALANCE_AMOUNT DOUBLE DEFAULT 0,

                                  PAYMENT_STATUS VARCHAR(30),

                                  NOTES VARCHAR(500),

                                  CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  PRIMARY KEY(ID),

                                  CONSTRAINT FK_INVOICE_CUSTOMER
                                      FOREIGN KEY (CUSTOMER_ID)
                                          REFERENCES APP.CUSTOMERS(ID)
);

-- =========================================
-- INVOICE ITEMS
-- =========================================

CREATE TABLE "APP"."INVOICE_ITEMS" (

                                       ID INTEGER NOT NULL
                                           GENERATED ALWAYS AS IDENTITY
                                               (START WITH 1, INCREMENT BY 1),

                                       INVOICE_ID INTEGER,

                                       ITEM_ID INTEGER,

                                       VARIANT_ID INTEGER,

                                       MEASUREMENT_ID INTEGER,

                                       ITEM_NAME VARCHAR(200),

                                       VARIANT_NAME VARCHAR(200),

                                       QUANTITY DOUBLE DEFAULT 0,

                                       UNIT VARCHAR(20),

                                       PRICE DOUBLE DEFAULT 0,

                                       GST_PERCENT DOUBLE DEFAULT 0,

                                       GST_AMOUNT DOUBLE DEFAULT 0,

                                       TOTAL DOUBLE DEFAULT 0,

                                       PRIMARY KEY(ID),

                                       CONSTRAINT FK_INV_ITEM_INVOICE
                                           FOREIGN KEY (INVOICE_ID)
                                               REFERENCES APP.INVOICES(ID),

                                       CONSTRAINT FK_INV_ITEM_ITEM
                                           FOREIGN KEY (ITEM_ID)
                                               REFERENCES APP.NEW_ITEMS(ID),

                                       CONSTRAINT FK_INV_ITEM_VARIANT
                                           FOREIGN KEY (VARIANT_ID)
                                               REFERENCES APP.ITEM_VARIANTS(ID),

                                       CONSTRAINT FK_INV_ITEM_MEASUREMENT
                                           FOREIGN KEY (MEASUREMENT_ID)
                                               REFERENCES APP.ITEM_MEASUREMENTS(ID)
);

-- =========================================
-- PAYMENTS
-- =========================================

CREATE TABLE "APP"."PAYMENTS" (

                                  ID INTEGER NOT NULL
                                      GENERATED ALWAYS AS IDENTITY
                                          (START WITH 1, INCREMENT BY 1),

                                  INVOICE_ID INTEGER,

                                  CUSTOMER_ID INTEGER,

                                  PAYMENT_MODE VARCHAR(30),

                                  PAID_AMOUNT DOUBLE DEFAULT 0,

                                  PAYMENT_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  REMARKS VARCHAR(500),

                                  PRIMARY KEY(ID),

                                  CONSTRAINT FK_PAYMENT_INVOICE
                                      FOREIGN KEY (INVOICE_ID)
                                          REFERENCES APP.INVOICES(ID),

                                  CONSTRAINT FK_PAYMENT_CUSTOMER
                                      FOREIGN KEY (CUSTOMER_ID)
                                          REFERENCES APP.CUSTOMERS(ID)
);


-- =========================================
-- INVOICES
-- =========================================

CREATE TABLE "APP"."INVOICES" (

                                  ID INTEGER NOT NULL
                                      GENERATED ALWAYS AS IDENTITY
                                          (START WITH 1, INCREMENT BY 1),

                                  INVOICE_NO VARCHAR(100),

                                  CUSTOMER_ID INTEGER,

                                  SALE_TYPE VARCHAR(20),

                                  PAYMENT_MODE VARCHAR(30),

                                  BILL_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  SUB_TOTAL DOUBLE DEFAULT 0,

                                  DISCOUNT DOUBLE DEFAULT 0,

                                  DELIVERY_CHARGE DOUBLE DEFAULT 0,

                                  GST_TOTAL DOUBLE DEFAULT 0,

                                  GRAND_TOTAL DOUBLE DEFAULT 0,

                                  PAID_AMOUNT DOUBLE DEFAULT 0,

                                  BALANCE_AMOUNT DOUBLE DEFAULT 0,

                                  PAYMENT_STATUS VARCHAR(30),

                                  NOTES VARCHAR(500),

                                  CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  PRIMARY KEY(ID),

                                  CONSTRAINT FK_INVOICE_CUSTOMER
                                      FOREIGN KEY (CUSTOMER_ID)
                                          REFERENCES APP.CUSTOMERS(ID)
);

-- =========================================
-- INVOICE ITEMS
-- =========================================

CREATE TABLE "APP"."INVOICE_ITEMS" (

                                       ID INTEGER NOT NULL
                                           GENERATED ALWAYS AS IDENTITY
                                               (START WITH 1, INCREMENT BY 1),

                                       INVOICE_ID INTEGER,

                                       ITEM_ID INTEGER,

                                       VARIANT_ID INTEGER,

                                       MEASUREMENT_ID INTEGER,

                                       ITEM_NAME VARCHAR(200),

                                       VARIANT_NAME VARCHAR(200),

                                       QUANTITY DOUBLE DEFAULT 0,

                                       UNIT VARCHAR(20),

                                       PRICE DOUBLE DEFAULT 0,

                                       GST_PERCENT DOUBLE DEFAULT 0,

                                       GST_AMOUNT DOUBLE DEFAULT 0,

                                       TOTAL DOUBLE DEFAULT 0,

                                       PRIMARY KEY(ID),

                                       CONSTRAINT FK_INV_ITEM_INVOICE
                                           FOREIGN KEY (INVOICE_ID)
                                               REFERENCES APP.INVOICES(ID),

                                       CONSTRAINT FK_INV_ITEM_ITEM
                                           FOREIGN KEY (ITEM_ID)
                                               REFERENCES APP.NEW_ITEMS(ID),

                                       CONSTRAINT FK_INV_ITEM_VARIANT
                                           FOREIGN KEY (VARIANT_ID)
                                               REFERENCES APP.ITEM_VARIANTS(ID),

                                       CONSTRAINT FK_INV_ITEM_MEASUREMENT
                                           FOREIGN KEY (MEASUREMENT_ID)
                                               REFERENCES APP.ITEM_MEASUREMENTS(ID)
);

-- =========================================
-- PAYMENTS
-- =========================================

CREATE TABLE "APP"."PAYMENTS" (

                                  ID INTEGER NOT NULL
                                      GENERATED ALWAYS AS IDENTITY
                                          (START WITH 1, INCREMENT BY 1),

                                  INVOICE_ID INTEGER,

                                  CUSTOMER_ID INTEGER,

                                  PAYMENT_MODE VARCHAR(30),

                                  PAID_AMOUNT DOUBLE DEFAULT 0,

                                  PAYMENT_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  REMARKS VARCHAR(500),

                                  PRIMARY KEY(ID),

                                  CONSTRAINT FK_PAYMENT_INVOICE
                                      FOREIGN KEY (INVOICE_ID)
                                          REFERENCES APP.INVOICES(ID),

                                  CONSTRAINT FK_PAYMENT_CUSTOMER
                                      FOREIGN KEY (CUSTOMER_ID)
                                          REFERENCES APP.CUSTOMERS(ID)
);

CREATE VIEW SALES_ANALYTICS_VIEW AS

SELECT

    i.ID,

    i.INVOICE_NO,

    i.SALE_TYPE,

    i.PAYMENT_MODE,

    i.GRAND_TOTAL,

    i.PAID_AMOUNT,

    i.BALANCE_AMOUNT,

    i.GST_TOTAL,

    i.CREATED_AT,

    (

        SELECT IFNULL(

                       SUM(
                               ii.QUANTITY
                                   *
                               ii.PURCHASE_PRICE
                       ),

                       0
               )

        FROM INVOICE_ITEMS ii

        WHERE ii.INVOICE_ID = i.ID

    ) AS PURCHASE_TOTAL,

    (

        i.GRAND_TOTAL -

        (

            SELECT IFNULL(

                           SUM(
                                   ii.QUANTITY
                                       *
                                   ii.PURCHASE_PRICE
                           ),

                           0
                   )

            FROM INVOICE_ITEMS ii

            WHERE ii.INVOICE_ID = i.ID
        )

        ) AS PROFIT

FROM INVOICES i;


CREATE TABLE PURCHASER_PAYMENTS (

                                    ID INTEGER PRIMARY KEY AUTOINCREMENT,

                                    PURCHASER_ID INTEGER NOT NULL,

                                    BILL_ID INTEGER,

                                    PAYMENT_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                    PAYMENT_MODE VARCHAR(30),

                                    AMOUNT DECIMAL(12,2),

                                    REMARKS VARCHAR(500),

                                    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE EXPENSE_MASTER (

                                ID INTEGER PRIMARY KEY AUTOINCREMENT,

                                EXPENSE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                EXPENSE_TYPE VARCHAR(100),

                                EXPENSE_NAME VARCHAR(200),

                                PAYMENT_MODE VARCHAR(30),

                                AMOUNT DECIMAL(12,2),

                                REMARKS VARCHAR(500),

                                CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE DAILY_CLOSING (

                               ID INTEGER PRIMARY KEY AUTOINCREMENT,

                               CLOSING_DATE DATE,

                               OPENING_CASH DECIMAL(12,2),

                               CASH_SALES DECIMAL(12,2),

                               UPI_SALES DECIMAL(12,2),

                               CARD_SALES DECIMAL(12,2),

                               CREDIT_SALES DECIMAL(12,2),

                               TOTAL_EXPENSE DECIMAL(12,2),

                               TOTAL_COLLECTION DECIMAL(12,2),

                               NET_CASH DECIMAL(12,2),

                               CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE VIEW LOW_STOCK_ITEMS AS

SELECT

    i.ID,

    i.ITEM_NAME,

    m.QUANTITY,

    m.UNIT,

    s.CURRENT_STOCK

FROM NEW_ITEMS i

         LEFT JOIN ITEM_MEASUREMENTS m
                   ON i.ID = m.ITEM_ID

         LEFT JOIN NEW_STOCK s
                   ON m.ID = s.MEASUREMENT_ID

WHERE s.CURRENT_STOCK <= 5;

CREATE VIEW TOP_SELLING_ITEMS AS

SELECT

    ni.ITEM_NAME,

    SUM(ii.QUANTITY) AS TOTAL_QTY,

    SUM(ii.TOTAL_AMOUNT) AS TOTAL_SALES

FROM INVOICE_ITEMS ii

         LEFT JOIN NEW_ITEMS ni
                   ON ii.ITEM_ID = ni.ID

GROUP BY ni.ITEM_NAME

ORDER BY TOTAL_QTY DESC;


CREATE VIEW CUSTOMER_OUTSTANDING AS

SELECT

    c.ID,

    c.CUSTOMER_NAME,

    c.PHONE_NO,

    SUM(i.BALANCE_AMOUNT) AS OUTSTANDING

FROM CUSTOMER_MASTER c

         LEFT JOIN INVOICES i
                   ON c.ID = i.CUSTOMER_ID

GROUP BY c.ID;

CREATE VIEW PROFIT_ANALYTICS AS

SELECT

    i.ID,

    i.INVOICE_NO,

    i.GRAND_TOTAL,

    (

        SELECT IFNULL(

                       SUM(
                               ii.QUANTITY
                                   *
                               ii.PURCHASE_PRICE
                       ),

                       0
               )

        FROM INVOICE_ITEMS ii

        WHERE ii.INVOICE_ID = i.ID

    ) AS PURCHASE_TOTAL,

    (

        i.GRAND_TOTAL -

        (

            SELECT IFNULL(

                           SUM(
                                   ii.QUANTITY
                                       *
                                   ii.PURCHASE_PRICE
                           ),

                           0
                   )

            FROM INVOICE_ITEMS ii

            WHERE ii.INVOICE_ID = i.ID
        )

        ) AS PROFIT

FROM INVOICES i;

CREATE VIEW MONTHLY_SALES AS

SELECT

    strftime('%Y-%m', CREATED_AT) AS MONTH,

    SUM(GRAND_TOTAL) AS TOTAL_SALES,

    SUM(PAID_AMOUNT) AS TOTAL_RECEIVED,

    SUM(BALANCE_AMOUNT) AS TOTAL_PENDING

FROM INVOICES

GROUP BY strftime('%Y-%m', CREATED_AT);

CREATE VIEW GST_REPORT AS

SELECT

    INVOICE_NO,

    CUSTOMER_ID,

    GST_TOTAL,

    GRAND_TOTAL,

    CREATED_AT

FROM INVOICES

WHERE SALE_TYPE='GST';

CREATE VIEW STOCK_VALUATION AS

SELECT

    ni.ITEM_NAME,

    m.UNIT,

    s.CURRENT_STOCK,

    m.PURCHASE_PRICE,

    (

        s.CURRENT_STOCK
            *
        m.PURCHASE_PRICE

        ) AS STOCK_VALUE

FROM NEW_STOCK s

         LEFT JOIN ITEM_MEASUREMENTS m
                   ON s.MEASUREMENT_ID = m.ID

         LEFT JOIN NEW_ITEMS ni
                   ON m.ITEM_ID = ni.ID;
--=========================
 --REPORTS
--==========================

CREATE TABLE REPORT_MASTER (

                               ID INTEGER PRIMARY KEY AUTOINCREMENT,

                               REPORT_NAME VARCHAR(150),

                               REPORT_TYPE VARCHAR(100),

                               GENERATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                               GENERATED_BY VARCHAR(100),

                               FILE_PATH VARCHAR(500)
);

CREATE TABLE INVENTORY_AUDIT (

                                 ID INTEGER PRIMARY KEY AUTOINCREMENT,

                                 ITEM_ID INTEGER,

                                 MEASUREMENT_ID INTEGER,

                                 OLD_STOCK DECIMAL(12,2),

                                 NEW_STOCK DECIMAL(12,2),

                                 ACTION_TYPE VARCHAR(50),

                                 REMARKS VARCHAR(500),

                                 CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE CASH_REGISTER (

                               ID INTEGER PRIMARY KEY AUTOINCREMENT,

                               OPENING_DATE TIMESTAMP,

                               OPENING_BALANCE DECIMAL(12,2),

                               CLOSING_BALANCE DECIMAL(12,2),

                               CASH_IN DECIMAL(12,2),

                               CASH_OUT DECIMAL(12,2),

                               STATUS VARCHAR(30),

                               CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE EXPENSE_CATEGORY (

                                  ID INTEGER PRIMARY KEY AUTOINCREMENT,

                                  CATEGORY_NAME VARCHAR(100)
);


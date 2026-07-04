package com.dt.controller;

import com.dt.customer.CustomerDAO;
import com.dt.customer.CustomerModel;
import com.dt.dao.DatabaseConnect;

import com.dt.dao.FirmDetailsPersistence;
import com.dt.dto.FirmDetails;
import com.dt.inventory.*;

import com.dt.invoice.*;
import com.dt.modern.model.CartItem;

import com.dt.modern.model.ItemCardController;
import com.dt.utils.QRCodeGenerator;
import com.dt.utils.TabContent;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.stream.Collectors;

public class ModernInvoiceController
        implements TabContent {

    // =====================================================
    // FXML
    // =====================================================
    @FXML
    private Label lblInvoiceNumber;

    @FXML
    private Label lblInvoiceMode;
    @FXML
    private ComboBox<String> cbPaymentMode;
    private Integer editingInvoiceId = null;
    private InvoiceDAO invoiceDAO;

    private InvoiceItemDAO invoiceItemDAO;

    private PaymentDAO paymentDAO;

    @FXML
    private FlowPane itemsContainer;

    @FXML
    private TextField tfSearch;

    @FXML
    private VBox customerSection;

    @FXML
    private ToggleButton btnCash;

    @FXML
    private ToggleButton btnCredit;

    @FXML
    private ToggleButton btnGST;

    @FXML
    private TextField tfCustomerSearch;

    @FXML
    private TextField tfCustomerName;

    @FXML
    private TextField tfCustomerPhone;

    @FXML
    private TextField tfGSTNumber;

    @FXML
    private TextArea tfCustomerAddress;

    @FXML
    private TreeView<String> categoriesTree;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> colItem;

    @FXML
    private TableColumn<CartItem, Double> colQty;

    @FXML
    private TableColumn<CartItem, Double> colPrice;

    @FXML
    private TableColumn<CartItem, Double> colGST;

    @FXML
    private TableColumn<CartItem, Double> colGSTAmount;

    @FXML
    private TableColumn<CartItem, Double> colTotal;

    @FXML
    private TextField tfDiscount;

    @FXML
    private TextField tfDeliveryCharge;

    @FXML
    private TextField tfPaidAmount;

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblTax;

    @FXML
    private Label lblBalance;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblDateTime;

    // =====================================================
    // DAO
    // =====================================================
    private CustomerModel selectedCustomer;
    private ToggleGroup saleTypeGroup;
    private ItemDAO itemDAO;

    private MeasurementDAO measurementDAO;

    private CategoryDAO categoryDAO;

    private SubCategoryDAO subCategoryDAO;

    private CustomerDAO customerDAO;

    // =====================================================
    // DATA
    // =====================================================

    private final ObservableList<ItemModel> allItems =
            FXCollections.observableArrayList();

    private final ObservableList<CartItem> cartItems =
            FXCollections.observableArrayList();

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        try {

            Connection connection =
                    DatabaseConnect.getConnection();
            invoiceDAO =
                    new InvoiceDAO(connection);

            invoiceItemDAO =
                    new InvoiceItemDAO(connection);

            paymentDAO =
                    new PaymentDAO(connection);
            itemDAO =
                    new ItemDAO(connection);

            measurementDAO =
                    new MeasurementDAO(connection);

            categoryDAO =
                    new CategoryDAO(connection);

            subCategoryDAO =
                    new SubCategoryDAO(connection);
            customerDAO =
                    new CustomerDAO(connection);
            cbPaymentMode.getItems().addAll(
                    "Cash",
                    "UPI",
                    "Card",
                    "Bank Transfer"
            );

            cbPaymentMode.getSelectionModel()
                    .select("Cash");
            initializeDateTime();

            initializeSaleType();

            initializeCart();

            initializeListeners();

            loadItemsFromDB();

            loadCategoriesFromDB();
            initializeInvoiceNumber();

             renderItems(allItems);

            refreshCart();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // DATE TIME
    // =====================================================

    private void initializeDateTime() {

        if (lblDateTime == null) {

            System.out.println("lblDateTime NULL");

            return;
        }

        lblDateTime.setText(

                LocalDateTime.now()

                        .format(

                                DateTimeFormatter.ofPattern(
                                        "dd MMM yyyy hh:mm a"
                                )
                        )
        );
    }

    // =====================================================
    // SALE TYPE
    // =====================================================

    private void initializeSaleType() {

        saleTypeGroup =
                new ToggleGroup();

        btnCash.setToggleGroup(
                saleTypeGroup
        );

        btnCredit.setToggleGroup(
                saleTypeGroup
        );

        btnGST.setToggleGroup(
                saleTypeGroup
        );

        btnCash.setSelected(true);

        updateSaleTypeUI();

        updateCustomerVisibility();

        saleTypeGroup.selectedToggleProperty()

                .addListener((obs, oldVal, newVal) -> {

                    updateCustomerVisibility();

                    updateSaleTypeUI();
                });
    }

    // =====================================================
    // CART TABLE
    // =====================================================

    private void initializeCart() {

        colItem.setCellValueFactory(data ->

                new SimpleStringProperty(

                        data.getValue().getItemName()

                                +

                                "\n"

                                +

                                data.getValue().getMeasurementName()
                )
        );

        colQty.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getQty()
                )
        );

        colPrice.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getRate()
                )
        );

        colGST.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getGst()
                )
        );

        colGSTAmount.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getGSTAmount()
                )
        );

        colTotal.setCellValueFactory(data ->

                new SimpleObjectProperty<>(

                        data.getValue()
                                .getTotal()
                )
        );

        cartTable.setItems(cartItems);
    }

    // =====================================================
    // LISTENERS
    // =====================================================

    private void initializeListeners() {

        tfSearch.textProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> {

                    List<ItemModel> filtered =

                            allItems.stream()

                                    .filter(item ->

                                            item.getItemName()

                                                    .toLowerCase()

                                                    .contains(

                                                            newVal
                                                                    .toLowerCase()
                                                    )
                                    )

                                    .collect(Collectors.toList());

                    renderItems(filtered);
                });

        tfDiscount.textProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> refreshCart());

        tfDeliveryCharge.textProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> refreshCart());

        tfPaidAmount.textProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> calculateBalance());

        categoriesTree.getSelectionModel()

                .selectedItemProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> {

                    if (newVal == null) {
                        return;
                    }

                    String category =
                            newVal.getValue();

                    if (category.equals("All")) {

                        renderItems(allItems);

                    } else {

                        List<ItemModel> filtered =

                                allItems.stream()

                                        .filter(i ->

                                                i.getCategoryName()
                                                        .equals(category)

                                                        ||

                                                        i.getSubCategoryName()
                                                                .equals(category)
                                        )

                                        .toList();

                        renderItems(filtered);
                    }
                });
    }

    // =====================================================
    // LOAD ITEMS
    // =====================================================

    private void loadItemsFromDB()
            throws Exception {

        allItems.clear();

        allItems.addAll(
                itemDAO.getAll()
        );
    }

    // =====================================================
    // CATEGORY TREE
    // =====================================================

    private void loadCategoriesFromDB()
            throws Exception {

        TreeItem<String> root =
                new TreeItem<>("All");

        root.setExpanded(true);

        List<CategoryModel> categories =
                categoryDAO.getAll();

        for (CategoryModel category
                : categories) {

            TreeItem<String> categoryNode =

                    new TreeItem<>(

                            category.getCategoryName()
                    );

            List<SubCategoryModel> subCategories =

                    subCategoryDAO.getByCategory(
                            category.getId()
                    );

            for (SubCategoryModel sub
                    : subCategories) {

                categoryNode.getChildren().add(

                        new TreeItem<>(

                                sub.getSubCategoryName()
                        )
                );
            }

            root.getChildren().add(categoryNode);
        }

        categoriesTree.setRoot(root);

        categoriesTree.setShowRoot(true);
    }

    // =====================================================
    // RENDER ITEMS
    // =====================================================

    private void renderItems(List<ItemModel> items) {

        itemsContainer.getChildren().clear();

        for (ItemModel item : items) {

            try {

                FXMLLoader loader =

                        new FXMLLoader(

                                getClass()

                                        .getResource(
                                                "/com/dt/modern/model/ItemCard.fxml"
                                        )
                        );

                VBox card =
                        loader.load();

                ItemCardController controller =
                        loader.getController();

                List<ItemMeasurement> measurements =

                        measurementDAO.getByItem(
                                item.getId()
                        );

                controller.setData(

                        item,

                        measurements,

                        this
                );

                itemsContainer.getChildren()
                        .add(card);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    // =====================================================
    // ADD TO CART
    // =====================================================

    public void addToCart(ItemModel item,
                          ItemMeasurement measurement,
                          double qty,
                          double price,
                          double gst) {

        Optional<CartItem> existing =

                cartItems.stream()

                        .filter(c ->

                                c.getItemId()

                                        ==

                                        item.getId()

                                        &&

                                        c.getMeasurementId()

                                                ==

                                                measurement.getId()
                        )

                        .findFirst();

        if (existing.isPresent()) {

            existing.get().setQty(

                    existing.get().getQty()

                            +

                            qty
            );

        } else {

            CartItem cart =
                    new CartItem();

            cart.setItemId(
                    item.getId()
            );

            cart.setMeasurementId(
                    measurement.getId()
            );

            cart.setItemName(
                    item.getItemName()
            );

            cart.setMeasurementName(

                    measurement.getQuantity()

                            +

                            " "

                            +

                            measurement.getUnit()
            );

            cart.setQty(qty);

            cart.setRate(price);

            cart.setGst(gst);

            cart.setUnit(
                    measurement.getUnit()
            );

            cartItems.add(cart);
        }

        refreshCart();
    }

    // =====================================================
    // REFRESH
    // =====================================================

    @FXML
    private void onRefreshCart() {

        refreshCart();
    }

    private void refreshCart() {

        cartTable.refresh();

        double subtotal = 0;

        double gstTotal = 0;

        for (CartItem c : cartItems) {

            double itemTotal =

                    c.getQty()

                            *

                            c.getRate();

            double gstAmount =

                    itemTotal *

                            c.getGst()

                            / 100;

            subtotal += itemTotal;

            gstTotal += gstAmount;
        }

        double discount =
                parseAmount(
                        tfDiscount.getText()
                );

        double delivery =
                parseAmount(
                        tfDeliveryCharge.getText()
                );

        double grandTotal =

                subtotal +
                        gstTotal -
                        discount +
                        delivery;

        lblSubtotal.setText(

                "₹ " +

                        String.format(
                                "%.2f",
                                subtotal
                        )
        );

        lblTax.setText(

                "₹ " +

                        String.format(
                                "%.2f",
                                gstTotal
                        )
        );

        lblTotal.setText(

                "₹ " +

                        String.format(
                                "%.2f",
                                grandTotal
                        )
        );

        calculateBalance();
    }

    // =====================================================
    // BALANCE
    // =====================================================

    private void calculateBalance() {

        double total =

                parseAmount(

                        lblTotal.getText()
                                .replace("₹", "")
                );

        double paid =

                parseAmount(
                        tfPaidAmount.getText()
                );

        double balance =
                total - paid;

        lblBalance.setText(

                "₹ " +

                        String.format(
                                "%.2f",
                                balance
                        )
        );
    }

    // =====================================================
    // SAVE
    // =====================================================

    @FXML
    private void onSaveInvoice() throws SQLException {
        if (editingInvoiceId != null) {

            List<InvoiceItemModel> oldItems =

                    invoiceItemDAO.getByInvoice(
                            editingInvoiceId
                    );

            for (InvoiceItemModel old : oldItems) {

                measurementDAO.restoreStock(

                        old.getMeasurementId(),

                        old.getQuantity()
                );
            }

            invoiceItemDAO.deleteByInvoice(
                    editingInvoiceId
            );

            paymentDAO.deleteByInvoice(
                    editingInvoiceId
            );
        }
        if (cartItems.isEmpty()) {

            showError(
                    "Cart Is Empty"
            );

            return;
        }

        try {

            Connection connection =
                    DatabaseConnect.getConnection();

            connection.setAutoCommit(false);

            // =================================================
            // INVOICE MODEL
            // =================================================

            InvoiceModel invoice =
                    new InvoiceModel();

            String invoiceNo;

            if (editingInvoiceId == null) {

                invoiceNo =

                        "INV-"

                                +

                                java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            } else {

                invoiceNo =
                        lblInvoiceNumber.getText();
            }

            invoice.setInvoiceNo(
                    invoiceNo
            );

            invoice.setCustomerId(

                    selectedCustomer != null

                            ?

                            selectedCustomer.getId()

                            :

                            0
            );

            // =================================================
            // SALE TYPE
            // =================================================

            String saleType = "Cash";

            if (btnGST.isSelected()) {

                saleType = "GST";
            }

            else if (btnCredit.isSelected()) {

                saleType = "Credit";
            }

            invoice.setSaleType(
                    saleType
            );

            // =================================================
            // PAYMENT MODE
            // =================================================

            invoice.setPaymentMode(
                    cbPaymentMode.getValue()
            );

            // =================================================
            // TOTALS
            // =================================================

            double subTotal =

                    parseAmount(

                            lblSubtotal.getText()
                                    .replace("₹", "")
                    );

            double gstTotal =

                    parseAmount(

                            lblTax.getText()
                                    .replace("₹", "")
                    );

            double grandTotal =

                    parseAmount(

                            lblTotal.getText()
                                    .replace("₹", "")
                    );

            double paidAmount =

                    parseAmount(
                            tfPaidAmount.getText()
                    );

            double balanceAmount =
                    grandTotal - paidAmount;

            invoice.setSubTotal(
                    subTotal
            );

            invoice.setDiscount(

                    parseAmount(
                            tfDiscount.getText()
                    )
            );

            invoice.setDeliveryCharge(

                    parseAmount(
                            tfDeliveryCharge.getText()
                    )
            );

            invoice.setGstTotal(
                    gstTotal
            );

            invoice.setGrandTotal(
                    grandTotal
            );

            invoice.setPaidAmount(
                    paidAmount
            );

            invoice.setBalanceAmount(
                    balanceAmount
            );

            invoice.setPaymentStatus(

                    balanceAmount <= 0

                            ?

                            "PAID"

                            :

                            "PENDING"
            );

            invoice.setNotes("");

            // =================================================
            // SAVE INVOICE
            // =================================================

            int invoiceId;

            if (editingInvoiceId == null) {

                invoiceDAO.insert(invoice);

                invoiceId =
                        invoiceDAO.getLastInsertedId();

            } else {

                invoice.setId(
                        editingInvoiceId
                );

                invoiceDAO.update(invoice);

                invoiceId =
                        editingInvoiceId;
            }


            for (CartItem cart : cartItems) {

                InvoiceItemModel item =
                        new InvoiceItemModel();

                item.setInvoiceId(
                        invoiceId
                );

                item.setItemId(
                        cart.getItemId()
                );

                item.setMeasurementId(
                        cart.getMeasurementId()
                );

                item.setItemName(
                        cart.getItemName()
                );

                item.setQuantity(
                        cart.getQty()
                );

                item.setUnit(
                        cart.getUnit()
                );

                item.setPrice(
                        cart.getRate()
                );

                item.setGstPercent(
                        cart.getGst()
                );

                double subtotal =

                        cart.getQty()
                                *
                                cart.getRate();

                double gstAmount =

                        subtotal
                                *
                                cart.getGst()
                                / 100;

                item.setGstAmount(
                        gstAmount
                );

                item.setTotal(
                        subtotal + gstAmount
                );

                invoiceItemDAO.insert(item);
            }

            // =================================================
            // SAVE ITEMS
            // =================================================

            for (CartItem cart : cartItems) {

                try {

                    measurementDAO.reduceStock(

                            cart.getMeasurementId(),

                            cart.getQty()
                    );

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            // =================================================
            // SAVE PAYMENT
            // =================================================

            if (paidAmount > 0) {

                PaymentModel payment =
                        new PaymentModel();

                payment.setInvoiceId(
                        invoiceId
                );

                payment.setCustomerId(

                        selectedCustomer != null

                                ?

                                selectedCustomer.getId()

                                :

                                0
                );

                payment.setPaymentMode(
                        cbPaymentMode.getValue()
                );

                payment.setPaidAmount(
                        paidAmount
                );

                payment.setRemarks(
                        "Invoice Payment"
                );

                paymentDAO.insert(payment);
            }


            // =================================================
            // COMMIT
            // =================================================

            connection.commit();

            // =================================================
            // PRINT
            // =================================================

            printInvoice(
                    invoiceNo
            );

            // =================================================
            // SUCCESS
            // =================================================

            showSuccess(
                    "Invoice Saved Successfully"
            );

            clearInvoice();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Save Invoice"
            );
        }
    }

    @FXML
    private void onSaveAndPrint() {

        try {

            onSaveInvoice();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Save Invoice"
            );
        }
    }

    private void printInvoice(String invoiceNo) {

        try {

            Map<String, Object> map =
                    new HashMap<>();

            // =================================================
            // DATE & TIME
            // =================================================

            LocalDateTime now =
                    LocalDateTime.now();

            map.put(
                    "invoiceNumber",
                    invoiceNo
            );

            map.put(
                    "invoiceDate",
                    now.format(
                            DateTimeFormatter.ofPattern(
                                    "dd-MM-yyyy"
                            )
                    )
            );

            map.put(
                    "invoiceTime",
                    now.format(
                            DateTimeFormatter.ofPattern(
                                    "hh:mm a"
                            )
                    )
            );

            // =================================================
            // CUSTOMER
            // =================================================

            map.put(
                    "customerName",
                    tfCustomerName.getText() == null
                            || tfCustomerName.getText().trim().isEmpty()
                            ? "Cash Customer"
                            : tfCustomerName.getText()
            );

            map.put(
                    "customerPhone",
                    tfCustomerPhone.getText() == null
                            ? ""
                            : tfCustomerPhone.getText()
            );

            map.put(
                    "customerAddress",
                    tfCustomerAddress.getText() == null
                            ? ""
                            : tfCustomerAddress.getText()
            );

            map.put(
                    "customerGST",
                    tfGSTNumber.getText()
            );

            // =================================================
            // PAYMENT SUMMARY
            // =================================================

            double subTotal =
                    parseAmount(
                            lblSubtotal.getText()
                                    .replace("₹", "")
                    );

            double gstTotal =
                    parseAmount(
                            lblTax.getText()
                                    .replace("₹", "")
                    );

            double grandTotal =
                    parseAmount(
                            lblTotal.getText()
                                    .replace("₹", "")
                    );

            double paidAmount =
                    parseAmount(
                            tfPaidAmount.getText()
                    );

            double balanceAmount =
                    grandTotal - paidAmount;

            double discount =
                    parseAmount(
                            tfDiscount.getText()
                    );

            double deliveryCharge =
                    parseAmount(
                            tfDeliveryCharge.getText()
                    );

            map.put(
                    "subTotal",
                    subTotal
            );

            map.put(
                    "gstAmount",
                    gstTotal
            );

            map.put(
                    "billAmount",
                    grandTotal
            );

            map.put(
                    "discount",
                    discount
            );

            map.put(
                    "deliveryCharge",
                    deliveryCharge
            );

            map.put(
                    "paid",
                    paidAmount
            );

            map.put(
                    "balance",
                    balanceAmount
            );

            // =================================================
            // FIRM DETAILS
            // =================================================

            FirmDetails firmDetails =
                    FirmDetailsPersistence.getData();

            if (firmDetails != null) {

                // =============================================
                // BASIC DETAILS
                // =============================================

                map.put(
                        "firmName",
                        firmDetails.getFirmName()
                );

                map.put(
                        "firmSubName",
                        firmDetails.getFirmSubName()
                );

                map.put(
                        "firmAddress",
                        firmDetails.getAddress()
                );

                map.put(
                        "firmPhoneNumbers",
                        firmDetails.getPhoneNumbers()
                );

                map.put(
                        "firmEmailAddress",
                        firmDetails.getEmailAddress()
                );

                // =============================================
                // GST
                // =============================================

                map.put(
                        "gstNumber",
                        firmDetails.getGstNumber()
                );

                System.out.println("hahaha"+ firmDetails.getGstNumber() + "asf"+  firmDetails.getFssaiNumber() );

                // =============================================
                // FSSAI
                // =============================================

                map.put(
                        "fssaiNumber",
                        firmDetails.getFssaiNumber()
                );

                // =============================================
                // LOGO
                // =============================================

                byte[] logoBytes =
                        firmDetails.getLogo();

                if (logoBytes != null) {

                    map.put(
                            "firmLogo",
                            new ByteArrayInputStream(
                                    logoBytes
                            )
                    );
                }

                // =============================================
                // QR CODE
                // =============================================

                if (firmDetails.getReviewUrl()
                        != null
                        &&
                        !firmDetails.getReviewUrl()
                                .trim()
                                .isEmpty()) {

                    map.put(
                            "qrCodeImage",
                            new ByteArrayInputStream(
                                    QRCodeGenerator.generateQRCode(
                                            firmDetails.getReviewUrl()
                                    )
                            )
                    );
                }
            }

            // =================================================
            // LOAD REPORT
            // =================================================
            System.out.println("GST => " + map.get("gstNumber"));
            System.out.println("FSSAI => " + map.get("fssaiNumber"));
            System.out.println("Phone => " + map.get("firmPhoneNumbers"));
            System.out.println("SubName => " + map.get("firmSubName"));
            InputStream jasperStream =

                    getClass()

                            .getResourceAsStream(

                                    "/resources/reports/new_dt_invoice_final1.jasper"
                            );
            System.out.println(
                    getClass().getResource(
                            "/resources/reports/new_dt_invoice_final1.jasper"
                    )
            );
            JasperReport jasperReport =

                    (JasperReport)

                            JRLoader.loadObject(
                                    jasperStream
                            );

            // =================================================
            // FILL REPORT
            // =================================================
//            map.put("firmSubName",
//                    "WHOLESALE SPICES & MERCHANT");
//
//            map.put("firmPhoneNumbers",
//                    "9876543210");
//
//            map.put("gstNumber",
//                    "33ABCDE1234F1Z5");
//
//            map.put("fssaiNumber",
//                    "12345678901234");
//
//            map.put("customerName",
//                    "Dinesh");
//
//            map.put("customerPhone",
//                    "9876543210");
//
//            map.put("customerAddress",
//                    "Triplicane Chennai");
//
//            map.put("invoiceNumber",
//                    "INV-001");
//
//            map.put("invoiceDate",
//                    "23-05-2026");
//
//            map.put("invoiceTime",
//                    "09:30 PM");
            JasperPrint jasperPrint =

                    JasperFillManager.fillReport(

                            jasperReport,

                            map,

                            new JRBeanCollectionDataSource(
                                    cartItems
                            )
                    );

            // =================================================
            // VIEW REPORT
            // =================================================

            JasperViewer.viewReport(
                    jasperPrint,
                    false
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Print Invoice"
            );
        }
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private double parseAmount(String value) {

        try {

            if (value == null ||
                    value.isBlank()) {

                return 0;
            }

            return Double.parseDouble(value);

        } catch (Exception e) {

            return 0;
        }
    }

    private void showError(String msg) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(msg);

        alert.showAndWait();
    }

    private void updateSaleTypeUI() {

        String active =

                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-radius: 0;";

        String inactive =

                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-radius: 0;" +
                        "-fx-border-color: #d1d5db;";

        btnCash.setStyle(inactive);

        btnCredit.setStyle(inactive);

        btnGST.setStyle(inactive);

        if (btnCash.isSelected()) {

            btnCash.setStyle(active);
        }

        if (btnCredit.isSelected()) {

            btnCredit.setStyle(active);
        }

        if (btnGST.isSelected()) {

            btnGST.setStyle(active);
        }
    }

    @FXML
    private void onSearchCustomer() {

        String search =

                tfCustomerSearch.getText();

        if (search == null
                ||
                search.isBlank()) {

            showError(
                    "Enter Customer Name / Phone / GST"
            );

            return;
        }

        try {

            List<CustomerModel> customers =
                    customerDAO.getAll();

            CustomerModel customer =

                    customers.stream()

                            .filter(c ->

                                    c.getCustomerName()
                                            .toLowerCase()
                                            .contains(
                                                    search.toLowerCase()
                                            )

                                            ||

                                            c.getPhoneNo()
                                                    .contains(search)

                                            ||

                                            (c.getGstNumber() != null
                                                    ?

                                                    c.getGstNumber().toLowerCase()

                                                    :

                                                    "")
                                                    .contains(
                                                            search.toLowerCase()
                                                    )
                            )

                            .findFirst()

                            .orElse(null);

            if (customer == null) {

                showError(
                        "Customer Not Found"
                );

                return;
            }
            selectedCustomer = customer;
            // =============================================
            // FILL CUSTOMER
            // =============================================

            tfCustomerName.setText(
                    customer.getCustomerName()
            );

            tfCustomerPhone.setText(
                    customer.getPhoneNo()
            );

            tfGSTNumber.setText(
                    customer.getGstNumber()
            );

            String address =

                    customer.getAddress1()

                            +

                            "\n"

                            +

                            customer.getCity();

            tfCustomerAddress.setText(
                    address
            );

            // =============================================
            // AUTO SELECT SALE TYPE
            // =============================================

            if ("GST".equalsIgnoreCase(
                    customer.getCustomerType()
            )) {

                saleTypeGroup.selectToggle(btnGST);

            }

            else if ("Credit".equalsIgnoreCase(
                    customer.getCustomerType()
            )) {

                saleTypeGroup.selectToggle(btnCredit);

            }

            else {

                saleTypeGroup.selectToggle(btnCash);
            }

            updateSaleTypeUI();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Search Customer"
            );
        }
    }

    @FXML
    private void onAddCustomer() {

        try {

            // =============================================
            // VALIDATION
            // =============================================

            String customerName =
                    tfCustomerName.getText();

            String phone =
                    tfCustomerPhone.getText();

            String gst =
                    tfGSTNumber.getText();

            if (customerName == null
                    ||
                    customerName.isBlank()) {

                showError(
                        "Customer Name Required"
                );

                return;
            }

            if (btnCredit.isSelected()) {

                if (phone == null
                        ||
                        phone.isBlank()) {

                    showError(
                            "Phone Number Required"
                    );

                    return;
                }
            }

            if (btnGST.isSelected()) {

                if (phone == null
                        ||
                        phone.isBlank()
                        ||
                        gst == null
                        ||
                        gst.isBlank()) {

                    showError(
                            "GST Number and Phone Required"
                    );

                    return;
                }
            }

            // =============================================
            // CUSTOMER TYPE
            // =============================================

            String customerType = "Cash";

            if (btnCredit.isSelected()) {

                customerType = "Credit";
            }

            if (btnGST.isSelected()) {

                customerType = "GST";
            }

            // =============================================
            // CREATE MODEL
            // =============================================

            CustomerModel customer =
                    new CustomerModel(

                            selectedCustomer != null

                                    ?

                                    selectedCustomer.getId()

                                    :

                                    0,

                            customerType,

                            "CUS-" +

                                    java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(),

                            customerName,

                            gst,

                            phone,

                            "",

                            "",

                            tfCustomerAddress.getText(),

                            "",

                            "",

                            "",

                            "",

                            0,

                            "DR"
                    );

            // =============================================
            // SAVE / UPDATE
            // =============================================

            if (selectedCustomer == null) {

                customerDAO.insert(customer);

                showSuccess(
                        "Customer Added Successfully"
                );

            } else {

                customerDAO.update(customer);

                showSuccess(
                        "Customer Updated Successfully"
                );
            }

            // =============================================
            // RESET
            // =============================================

            selectedCustomer = customer;

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Save Customer"
            );
        }
    }

    private void showSuccess(String msg) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(msg);

        alert.showAndWait();
    }

    public void loadInvoiceForEdit(int invoiceId) {

        try {

            editingInvoiceId = invoiceId;

            InvoiceModel invoice =
                    invoiceDAO.getById(invoiceId);

            if (invoice.getCustomerId() > 0) {

                selectedCustomer =
                        customerDAO.getById(
                                invoice.getCustomerId()
                        );
            }

            if (invoice == null) {

                showError(
                        "Invoice Not Found"
                );

                return;
            }

            // =====================================================
            // CUSTOMER
            // =====================================================

            tfCustomerName.setText(

                    invoice.getCustomerName() != null

                            ?

                            invoice.getCustomerName()

                            :

                            ""
            );

            tfCustomerPhone.setText(

                    invoice.getCustomerPhone() != null

                            ?

                            invoice.getCustomerPhone()

                            :

                            ""
            );

            tfGSTNumber.setText(

                    invoice.getCustomerGST() != null

                            ?

                            invoice.getCustomerGST()

                            :

                            ""
            );

            tfCustomerAddress.setText(

                    invoice.getCustomerAddress() != null

                            ?

                            invoice.getCustomerAddress()

                            :

                            ""
            );

            // =====================================================
            // SALE TYPE
            // =====================================================

            if ("GST".equalsIgnoreCase(
                    invoice.getSaleType()
            )) {

                saleTypeGroup.selectToggle(
                        btnGST
                );

            }

            else if ("Credit".equalsIgnoreCase(
                    invoice.getSaleType()
            )) {

                saleTypeGroup.selectToggle(
                        btnCredit
                );

            }

            else {

                saleTypeGroup.selectToggle(
                        btnCash
                );
            }

            updateCustomerVisibility();

            updateSaleTypeUI();

            // =====================================================
            // PAYMENT
            // =====================================================

            if (invoice.getPaymentMode() != null) {

                cbPaymentMode.getSelectionModel()

                        .select(
                                invoice.getPaymentMode()
                        );
            }

            tfDiscount.setText(

                    String.valueOf(
                            invoice.getDiscount()
                    )
            );

            tfDeliveryCharge.setText(

                    String.valueOf(
                            invoice.getDeliveryCharge()
                    )
            );

            tfPaidAmount.setText(

                    String.valueOf(
                            invoice.getPaidAmount()
                    )
            );

            // =====================================================
            // CART
            // =====================================================

            List<InvoiceItemModel> items =

                    invoiceItemDAO.getByInvoice(
                            invoiceId
                    );

            cartItems.clear();

            for (InvoiceItemModel item : items) {

                CartItem cart =
                        new CartItem();

                cart.setItemId(
                        item.getItemId()
                );

                cart.setMeasurementId(
                        item.getMeasurementId()
                );

                cart.setItemName(
                        item.getItemName()
                );

                if (item.getMeasurementId() > 0) {

                    try {

                        ItemMeasurement measurement =

                                measurementDAO.getById(
                                        item.getMeasurementId()
                                );

                        if (measurement != null) {

                            cart.setMeasurementId(
                                    measurement.getId()
                            );

                            cart.setMeasurementName(

                                    measurement.getQuantity()

                                            +

                                            " "

                                            +

                                            measurement.getUnit()
                            );

                            cart.setUnit(
                                    measurement.getUnit()
                            );

                        } else {

                            recoverMeasurement(cart, item);
                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                        recoverMeasurement(cart, item);
                    }

                } else {

                    recoverMeasurement(cart, item);
                }

                cart.setQty(
                        item.getQuantity()
                );

                cart.setRate(
                        item.getPrice()
                );

                cart.setGst(
                        item.getGstPercent()
                );

                cart.setUnit(
                        item.getUnit()
                );

                cartItems.add(cart);
            }

            cartTable.refresh();

            // =====================================================
            // INVOICE LABEL
            // =====================================================

            lblInvoiceNumber.setText(
                    invoice.getInvoiceNo()
            );

            lblInvoiceMode.setText(
                    "EDIT"
            );

            lblInvoiceMode.setStyle(

                    "-fx-background-color: #dc2626;"

                            +

                            "-fx-text-fill: white;"

                            +

                            "-fx-padding: 3 10 3 10;"

                            +

                            "-fx-font-size: 11px;"

                            +

                            "-fx-font-weight: bold;"
            );

            // =====================================================
            // TOTALS
            // =====================================================

            refreshCart();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Load Invoice"
            );
        }
    }

    private void recoverMeasurement(CartItem cart,
                                    InvoiceItemModel item) {

        try {

            List<ItemMeasurement> measurements =

                    measurementDAO.getByItem(
                            item.getItemId()
                    );

            Optional<ItemMeasurement> match =

                    measurements.stream()

                            .filter(m ->

                                    Double.compare(

                                            m.getSellingPrice(),

                                            item.getPrice()

                                    ) == 0
                            )

                            .findFirst();

            if (match.isPresent()) {

                ItemMeasurement m = match.get();

                cart.setMeasurementId(
                        m.getId()
                );

                cart.setMeasurementName(

                        m.getQuantity()

                                +

                                " "

                                +

                                m.getUnit()
                );

                cart.setUnit(
                        m.getUnit()
                );

            } else {

                cart.setMeasurementName(
                        item.getUnit()
                );

                cart.setUnit(
                        item.getUnit()
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            cart.setMeasurementName(
                    item.getUnit()
            );

            cart.setUnit(
                    item.getUnit()
            );
        }
    }

    private void clearInvoice() {
        editingInvoiceId = null;
        cartItems.clear();

        cartTable.refresh();

        tfDiscount.clear();

        tfDeliveryCharge.clear();

        tfPaidAmount.clear();

        tfCustomerSearch.clear();

        tfCustomerName.clear();

        tfCustomerPhone.clear();

        tfGSTNumber.clear();

        tfCustomerAddress.clear();

        selectedCustomer = null;
        lblInvoiceNumber.setText(
                "NEW"
        );

        lblInvoiceMode.setText(
                "NEW"
        );

        lblInvoiceMode.setStyle(

                "-fx-background-color: #16a34a;"

                        +

                        "-fx-text-fill: white;"

                        +

                        "-fx-padding: 3 10 3 10;"

                        +

                        "-fx-font-size: 11px;"

                        +

                        "-fx-font-weight: bold;"
        );
        btnCash.setSelected(true);
        refreshCart();
    }

    private void initializeInvoiceNumber() {

        lblInvoiceNumber.setText(
                "NEW"
        );

        lblInvoiceMode.setText(
                "NEW"
        );

        lblInvoiceMode.setStyle(

                "-fx-background-color: #16a34a;"

                        +

                        "-fx-text-fill: white;"

                        +

                        "-fx-padding: 3 10 3 10;"

                        +

                        "-fx-font-size: 11px;"

                        +

                        "-fx-font-weight: bold;"
        );
    }

    private void updateCustomerVisibility() {

        boolean showCustomer =

                btnCredit.isSelected()

                        ||

                        btnGST.isSelected();

        customerSection.setVisible(
                showCustomer
        );

        customerSection.setManaged(
                showCustomer
        );
    }
    // =====================================================
    // TAB CONTENT
    // =====================================================

    @Override
    public boolean shouldClose() {
        return false;
    }

    @Override
    public void putFocusOnNode() {
    }

    @Override
    public boolean loadData() {
        return true;
    }

    @Override
    public void setMainWindow(Stage stage) {
    }

    @Override
    public void setTabPane(TabPane tabPane) {
    }
}
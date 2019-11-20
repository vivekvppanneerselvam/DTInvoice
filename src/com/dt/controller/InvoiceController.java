/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;

import com.dt.application.Global;
import com.dt.dto.*;
import com.dt.utils.*;
import com.dt.dao.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.view.JasperViewer;
import java.util.logging.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author Dinesh
 */
public class InvoiceController implements TabContent {

	private static final Logger logger = 
			Logger.getLogger(InvoiceController.class.getName());
	private final BooleanProperty isDirty = new SimpleBooleanProperty(false);

	private Stage mainWindow;
	private int invoiceNumber = 0;
	private TabPane tabPane = null;
	private ArrayList<EntityEditedListener> eventListeners = null;
	private UserPreferences userPreferences = null;

	@FXML
	private DatePicker dpInvoiceDate;
	@FXML
	private Button btnSave;
	@FXML
	private RadioButton creditInvoice;
	@FXML
	private RadioButton cashInvoice;
	@FXML
	private HBox customersContainer;
	@FXML
	private TableView<InvoiceItem> tableView;
	@FXML
	private RadioButton rdEdit;
	@FXML
	private RadioButton rdNew;
	@FXML
	private Button btnDelete;
	@FXML
	private Label lblItemError;
	@FXML
	private Label lblUnitError;
	@FXML
	private Label lblRateError;
	@FXML
	private Label lblQuantityError;
	@FXML
	private Label lblDateError;
	@FXML
	private Label lblCustomerError;
	@FXML
	private Label lblDiscountError;
	@FXML
	private Label lblChargeError;
	@FXML
	private Label lblNoItemError;
	@FXML
	private TextField tfItem;
	@FXML
	private ComboBox<MeasurementUnit> cbUnit;
	@FXML
	private TextField tfCustomer;
	@FXML
	private TextField tfRate;
	@FXML
	private TextField tfQuantity;
	@FXML
	private TextField tfAmount;
	@FXML
	private TextField tfTotal;
	@FXML
	private TextField tfDiscount;
	@FXML
	private TextField tfCharge;
	@FXML
	private TextField tfNetAmount;
	@FXML
	private Button btnAdd;
	@FXML
	private ToggleGroup cashOrCreditToggle;
	@FXML
	private ToggleGroup newOrEditToggle;
	@FXML
	private TableColumn<InvoiceItem, BigDecimal> rateColumn;
	@FXML
	private TableColumn<InvoiceItem, BigDecimal> quantityColumn;
	@FXML
	private TableColumn<InvoiceItem, BigDecimal> amountColumn;
	@FXML
	private TableColumn<InvoiceItem, Item> itemColumn;
	@FXML
	private TableColumn<InvoiceItem, MeasurementUnit> unitColumn;

	@FXML
	private CheckBox chkPrintOnSave;
	@FXML private ImageView checkImage;

	private final DecimalFormat quantityFormat = new DecimalFormat("#,##0.000");
	private final DateTimeFormatter dateFormatter = 
			DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private final LocalDate minDate = Global.getActiveFinancialYear().getStartDate();
	private final LocalDate maxDate = Utility.minDate(Global.getActiveFinancialYear()
			.getEndDate(), LocalDate.now());

	private final ObservableList<InvoiceItem> invoiceItems
	= FXCollections.<InvoiceItem>observableArrayList((InvoiceItem item) -> {
		Observable[] array = new Observable[]{
				item.itemProperty(), item.unitProperty(),
				item.rateProperty(), item.quantityProperty()
		};
		return array;
	});
	private List<Customer> customers = null;
	private List<Item> items = null;
	private AutoCompletionBinding<Customer> customerCompletion = null;
	private AutoCompletionBinding<Item> itemCompletion = null;

	/**
	 * Initializes the controller class.
	 */
	 public void initialize() {

		 if (!Global.getActiveFinancialYear().getEndDate().
				 isBefore(LocalDate.now())) {
			 dpInvoiceDate.setValue(LocalDate.now());
		 }
		 dpInvoiceDate.setConverter(Utility.getDateStringConverter());
		 dpInvoiceDate.setDayCellFactory(this::getDateCell);

		 btnSave.disableProperty().bind(isDirty.not());

		 customersContainer.managedProperty().bind(customersContainer.visibleProperty());
		 customersContainer.visibleProperty().bind(creditInvoice.selectedProperty());

		 rdEdit.disableProperty().bind(tableView.getSelectionModel()
				 .selectedItemProperty().isNull());

		 btnDelete.disableProperty().bind(tableView.getSelectionModel()
				 .selectedItemProperty().isNull());

		 lblItemError.managedProperty().bind(lblItemError.visibleProperty());
		 lblItemError.visibleProperty().bind(lblItemError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 lblUnitError.managedProperty().bind(lblUnitError.visibleProperty());
		 lblUnitError.visibleProperty().bind(lblUnitError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 lblRateError.managedProperty().bind(lblRateError.visibleProperty());
		 lblRateError.visibleProperty().bind(lblRateError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 lblQuantityError.managedProperty().bind(lblQuantityError.visibleProperty());
		 lblQuantityError.visibleProperty().bind(lblQuantityError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 lblDateError.managedProperty().bind(lblDateError.visibleProperty());
		 lblDateError.visibleProperty().bind(lblDateError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 lblCustomerError.managedProperty().bind(lblCustomerError.visibleProperty());
		 lblCustomerError.visibleProperty().bind(lblCustomerError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 lblDiscountError.managedProperty().bind(lblDiscountError.visibleProperty());
		 lblDiscountError.visibleProperty().bind(lblDiscountError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 lblChargeError.managedProperty().bind(lblChargeError.visibleProperty());
		 lblChargeError.visibleProperty().bind(lblChargeError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 lblNoItemError.managedProperty().bind(lblNoItemError.visibleProperty());
		 lblNoItemError.visibleProperty().bind(lblNoItemError.textProperty()
				 .length().greaterThanOrEqualTo(1));

		 tfItem.textProperty().addListener((observable, oldValue, newValue) -> {
			 btnAdd.setDisable(false);
		 });

		 cbUnit.getSelectionModel().selectedItemProperty()
		 .addListener((observable, oldValue, newValue) -> {
			 btnAdd.setDisable(false);
		 });

		 tfRate.textProperty().addListener((observable, oldValue, newValue) -> {
			 btnAdd.setDisable(false);
		 });

		 tfQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
			 btnAdd.setDisable(false);
		 });

		 cashOrCreditToggle.selectedToggleProperty().addListener((observable,
				 oldValue, newValue) -> {
					 isDirty.set(true);
					 onInvoiceTypeChanged(newValue);
				 });

		 tfRate.focusedProperty().addListener((observable, oldValue, newValue) -> {
			 if (!newValue) {
				 setInvoiceItemAmount();
			 }
		 });

		 tfQuantity.focusedProperty().addListener((observable, oldValue, newValue) -> {
			 if (!newValue) {
				 setInvoiceItemAmount();
			 }
		 });

		 setTableCellValueFactory();
		 setTableCellFactories();
		 tableView.setItems(invoiceItems);

		 tfDiscount.focusedProperty().addListener((observable, oldValue, newValue) -> {
			 if (!newValue) {
				 updateBillAmount();
			 }
		 });

		 tfCharge.focusedProperty().addListener((observable, oldValue, newValue) -> {
			 if (!newValue) {
				 updateBillAmount();
			 }
		 });

		 invoiceItems.addListener(new ListChangeListener<InvoiceItem>() {

			 @Override
			 public void onChanged(ListChangeListener.Change<? extends InvoiceItem> c) {
				 isDirty.set(true);
				 updateBillAmount();
			 }
		 });

		 btnAdd.textProperty().bind(new When(rdEdit.selectedProperty())
				 .then("Edit").otherwise("Add"));

		 newOrEditToggle.selectedToggleProperty().addListener((observable,
				 oldValue, newValue) -> {
					 clearItemFields();
					 if (newValue.equals(rdEdit)) {
						 populateItemDetails();
					 }
					 Platform.runLater(() -> tfItem.requestFocus());
				 });

		 tfDiscount.textProperty().addListener((observable, oldValue, newValue) -> {
			 isDirty.set(true);
		 });

		 tfCharge.textProperty().addListener((observable, oldValue, newValue) -> {
			 isDirty.set(true);
		 });

		 tfCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
			 isDirty.set(true);
		 });

		 dpInvoiceDate.valueProperty().addListener((observable, oldDate, newDate) -> {

			 isDirty.set(true);

			 LocalDate today = LocalDate.now();
			 //              LocalDate lastDate = today.minusMonths(1).plusDays(1);

			 if (newDate == null || newDate.isAfter(today)) {
				 dpInvoiceDate.setValue(today);
			 }
		 });

		 checkImage.managedProperty().bind(checkImage.visibleProperty());
		 checkImage.visibleProperty().bind(checkImage.opacityProperty()
				 .greaterThan(0.0));

		 cbUnit.focusedProperty().addListener(new ChangeListener<Boolean>() {

			 @Override
			 public void changed(ObservableValue<? extends Boolean> observable,
					 Boolean oldValue, Boolean newValue) {
				 if (newValue) {
					 cbUnit.show();
				 }
			 }
		 });

	 }

	 private void populateItemDetails() {
		 InvoiceItem item = tableView.getSelectionModel().getSelectedItem();
		 Item obj = item.getItem();
		 tfItem.setText(obj.getItemName());
		 tfItem.setUserData(obj);
		 cbUnit.getSelectionModel().select(item.getUnit());
		 BigDecimal rate = item.getRate();
		 tfRate.setText(rate.toPlainString());
		 BigDecimal quantity = item.getQuantity();
		 tfQuantity.setText(quantity.toPlainString());
		 BigDecimal amount = rate.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
		 tfAmount.setText(amount.toPlainString());
	 }

	 private void clearItemFields() {
		 tfItem.clear();
		 tfItem.setUserData(null);
		 cbUnit.getSelectionModel().clearSelection();
		 tfRate.clear();
		 tfQuantity.clear();
		 tfAmount.clear();
	 }

	 @Override
	 public boolean shouldClose() {
		 if (isDirty.get()) {
			 ButtonType response = shouldSaveUnsavedData();
			 if (response == ButtonType.CANCEL) {
				 return false;
			 }

			 if (response == ButtonType.YES) {
				 return saveInvoice();
			 }

		 }

		 return true;
	 }

	 @Override
	 public void putFocusOnNode() {
		 dpInvoiceDate.requestFocus();
	 }

	 @Override
	 public boolean loadData() {
		 boolean success = loadItems();
		 if (!success) {
			 return false;
		 }
		 if (!loadUnits()) {
			 return false;
		 }

		 userPreferences = Global.getUserPreferences();
		 chkPrintOnSave.setSelected(userPreferences.getPrintOnSave());

		 return true;
	 }

	 @Override
	 public void setMainWindow(Stage stage) {
		 mainWindow = stage;

		 /*
		  * Object obj = stage.getUserData(); if (obj != null && (obj instanceof String))
		  * { if (obj.toString().equalsIgnoreCase("subwindow")) {
		  * stage.setOnCloseRequest((WindowEvent event) -> { if (!isDirty.get()) {
		  * mainWindow.close(); return; }
		  * 
		  * ButtonType buttonType = shouldSaveUnsavedData(); if (buttonType ==
		  * ButtonType.CANCEL) { event.consume(); } else if (buttonType == ButtonType.NO)
		  * { mainWindow.close(); } else if (saveInvoice()) { mainWindow.close(); } }); }
		  * }
		  */
	 }

	 private DateCell getDateCell(DatePicker datePicker) {
		 return Utility.getDateCell(datePicker, minDate, maxDate);
	 }

	 private boolean loadItems() {

		 try {
			 items = ItemsPersistence.getItems();
		 } catch (Exception e) {
			 String message = Utility.getDataFetchErrorText();
			 Alert alert = Utility.getErrorAlert("Error Occurred",
					 "Error in Fetching Items", message, mainWindow);
			 Utility.beep();
			 alert.showAndWait();
			 return false;
		 }

		 if (itemCompletion != null) {
			 itemCompletion.dispose();
		 }

		 itemCompletion = TextFields.<Item>bindAutoCompletion(tfItem, items);
		 return true;

	 }

	 private boolean loadUnits() {
		 List<MeasurementUnit> units = null;

		 try {
			 units = MeasurementUnitPersistence.getMeasurementUnits();
		 } catch (Exception e) {
			 String message = Utility.getDataFetchErrorText();
			 Alert alert = Utility.getErrorAlert("Error Occurred",
					 "Error in Fetching Measurement Units", message, mainWindow);
			 Utility.beep();
			 alert.showAndWait();
			 return false;
		 }

		 ObservableList<MeasurementUnit> unitsList
		 = FXCollections.<MeasurementUnit>observableList(units);
		 cbUnit.setItems(unitsList);
		 return true;
	 }

	 private boolean loadCustomers() {

		 try {
			 customers = CustomersPersistence.getCustomers();
		 } catch (Exception e) {
			 String message = Utility.getDataFetchErrorText();
			 Alert alert = Utility.getErrorAlert("Error Occurred",
					 "Error in Fetching Customers List !", message, mainWindow);
			 Utility.beep();
			 alert.showAndWait();
			 return false;
		 }

		 if (customerCompletion != null) {
			 customerCompletion.dispose();
		 }

		 customerCompletion = 
				 TextFields.<Customer>bindAutoCompletion(tfCustomer, customers);
		 return true;
	 }

	 private void onInvoiceTypeChanged(Toggle toggle) {
		 RadioButton invoiceType = (RadioButton) toggle;
		 if (invoiceType.equals(creditInvoice)) {
			 if (customerCompletion == null) {
				 loadCustomers();
			 }
		 }
		 isDirty.set(true);
	 }

	 private boolean validateInvoiceItem() {
		 clearItemErrorFields();

		 boolean valid = true;

		 Item item = null;
		 final String text = tfItem.getText();
		 if (text == null || text.trim().isEmpty()) {
			 lblItemError.setText("Item not specified !");
			 valid = false;
		 } else { //check whether it is a valid item or not
			 Optional<Item> findFirst = items.stream().filter((Item t) -> 
			 t.getItemName().equalsIgnoreCase(text)
					 ).findFirst();
			 if (!findFirst.isPresent()) {
				 lblItemError.setText("No item matches this name !");
				 valid = false;
			 } else {
				 item = findFirst.get();
				 tfItem.setUserData(item);
			 }
		 }

		 final Item itm = item;

		 if (itm != null) {
			 if (rdNew.isSelected()) {
				 boolean isMatch = invoiceItems.stream().anyMatch(i -> i.getItem().equals(itm));
				 if (isMatch) {
					 lblItemError.setText(String.format("Item '%s' already added to the invoice!", itm));
					 valid = false;
				 }
			 } else { //edit item is selected
				 long count = invoiceItems.stream().filter((InvoiceItem invoiceItem) -> {
					 return invoiceItem.getItem().equals(itm);
				 }
						 ).count();
				 if (count > 1) {
					 lblItemError.setText(String.format("Item '%s' already added to the invoice!", itm));
					 valid = false;
				 }
			 }
		 }

		 MeasurementUnit unit = cbUnit.getSelectionModel().getSelectedItem();
		 if (unit == null) {
			 lblUnitError.setText("Unit not selected!");
			 valid = false;
		 }

		 String rate = tfRate.getText().trim();
		 if (rate.isEmpty()) {
			 lblRateError.setText("Rate not specified!");
			 valid = false;
		 } else {
			 BigDecimal amount = null;
			 try {
				 amount = new BigDecimal(rate);
			 } catch (Exception e) {
				 lblRateError.setText("Not a valid rate!");
				 valid = false;
			 }

			 if (amount != null) {
				 if (amount.signum() != 1) {
					 lblRateError.setText("Rate must be a positive number!");
					 valid = false;
				 }
			 }
		 }

		 String quantity = tfQuantity.getText().trim();
		 if (quantity.isEmpty()) {
			 lblQuantityError.setText("Quantity not specified!");
			 valid = false;
		 } else {
			 BigDecimal value = null;
			 try {
				 value = new BigDecimal(quantity);
			 } catch (Exception e) {
				 lblQuantityError.setText("Not a valid quantity!");
				 valid = false;
			 }

			 if (value != null) {
				 if (value.signum() != 1) {
					 lblQuantityError.setText("Quantity must be a positive number!");
					 valid = false;
				 }
			 }
		 }

		 return valid;
	 }

	 @FXML
	 private void onInvoiceItemAddAction(ActionEvent event) {
		 if (!validateInvoiceItem()) {
			 return;
		 }

		 InvoiceItem item = null;
		 if (rdEdit.isSelected()) {
			 item = tableView.getSelectionModel().getSelectedItem();
		 } else {
			 item = new InvoiceItem();
		 }

		 item.setItem((Item) tfItem.getUserData());
		 item.setUnit(cbUnit.getSelectionModel().getSelectedItem());
		 BigDecimal rate = new BigDecimal(tfRate.getText().trim(),
				 MathContext.DECIMAL64);
		 rate.setScale(2, RoundingMode.HALF_UP);
		 item.setRate(rate);
		 BigDecimal quantity = new BigDecimal(tfQuantity.getText().trim(),
				 MathContext.DECIMAL64);
		 quantity.setScale(3, RoundingMode.HALF_UP);
		 item.setQuantity(quantity);

		 if (rdNew.isSelected()) {
			 invoiceItems.add(item);
			 tableView.scrollTo(item);
			 tableView.getSelectionModel().select(item);

			 tfItem.clear();
			 tfItem.setUserData(null);
			 cbUnit.getSelectionModel().clearSelection();
			 tfRate.clear();
			 tfQuantity.clear();
			 tfAmount.clear();
		 }

		 isDirty.set(true);
		 btnAdd.setDisable(true);
		 tfItem.requestFocus();

	 }

	 private void setInvoiceItemAmount() {
		 BigDecimal rate = BigDecimal.ZERO;
		 BigDecimal quantity = BigDecimal.ZERO;

		 String rateString = tfRate.getText().trim();
		 String quantityString = tfQuantity.getText().trim();

		 if (rateString.isEmpty()) {
			 rate = BigDecimal.ZERO;
		 } else {
			 try {
				 rate = new BigDecimal(rateString);
			 } catch (Exception e) {
				 rate = BigDecimal.ZERO;
			 }
		 }

		 if (quantityString.isEmpty()) {
			 quantity = BigDecimal.ZERO;
		 } else {
			 try {
				 quantity = new BigDecimal(quantityString);
			 } catch (Exception e) {
				 quantity = BigDecimal.ZERO;
			 }
		 }

		 BigDecimal amount = quantity.multiply(rate).setScale(2, RoundingMode.HALF_UP);
		 tfAmount.setText(IndianCurrencyFormatting.applyFormatting(amount));
	 }

	 private void updateBillAmount() {
		 BigDecimal total = BigDecimal.ZERO;
		 BigDecimal amount = null;

		 for (InvoiceItem item : invoiceItems) {
			 amount = item.getRate().multiply(item.getQuantity()).setScale(2,
					 RoundingMode.HALF_UP);
			 total = total.add(amount);
		 }

		 tfTotal.setText(IndianCurrencyFormatting.applyFormatting(total));
		 BigDecimal discount = BigDecimal.ZERO;
		 String discountString = tfDiscount.getText().trim();
		 if (!discountString.isEmpty()) {
			 try {
				 discount = new BigDecimal(discountString);
				 discount = discount.abs().setScale(2, RoundingMode.HALF_UP);
			 } catch (Exception e) {
			 }
		 }

		 total = total.subtract(discount);

		 BigDecimal charge = BigDecimal.ZERO;
		 String chargeString = tfCharge.getText().trim();
		 if (!chargeString.isEmpty()) {
			 try {
				 charge = new BigDecimal(chargeString);
				 charge = charge.abs().setScale(2, RoundingMode.HALF_UP);
			 } catch (Exception e) {
			 }
		 }

		 total = total.add(charge);
		 tfNetAmount.setText(IndianCurrencyFormatting.applyFormatting(total));
	 }

	 @FXML
	 private void onInvoiceItemDeleteAction(ActionEvent event) {
		 InvoiceItem item = tableView.getSelectionModel().getSelectedItem();

		 String message = "Are you sure to delete the item '" + item.getItem()
		 + "' from the invoice?";
		 Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
				 ButtonType.YES, ButtonType.NO);
		 alert.setTitle("Confirm Item Deletion");
		 alert.setHeaderText("Sure to delete?");
		 alert.initOwner(mainWindow);
		 alert.initModality(Modality.WINDOW_MODAL);
		 Global.styleAlertDialog(alert);
		 Optional<ButtonType> response = alert.showAndWait();
		 if (response.isPresent()) {
			 invoiceItems.remove(item);
		 }
	 }

	 private boolean validateInput() {
		 clearInvoiceErrorFields();

		 boolean valid = true;

		 final LocalDate date = dpInvoiceDate.getValue();
		 if (date == null) {
			 lblDateError.setText("Invoice Date not specified!");
			 valid = false;
		 } else if (date.isBefore(minDate)) {
			 lblDateError.setText("Invoice Date can't be before the date " + 
					 minDate.format(dateFormatter));
			 valid = false;
		 } else if (date.isAfter(LocalDate.now())) {
			 lblDateError.setText("Invoice Date can't be later than " + 
					 maxDate.format(dateFormatter));
			 valid = false;
		 }

		 if (creditInvoice.isSelected()){
			 final String text = tfCustomer.getText();
			 if (text == null || text.trim().isEmpty()) {
				 lblCustomerError.setText("Customer not specified !");
				 valid = false;
			 } else { //check whether it is a valid customer or not
				 Optional<Customer> findFirst = customers.stream().filter((Customer t) -> 
				 t.getName().equalsIgnoreCase(text)
						 ).findFirst();
				 if (!findFirst.isPresent()) {
					 lblCustomerError.setText("No customer matches this name !");
					 valid = false;
				 } else {
					 tfCustomer.setUserData(findFirst.get());
				 }
			 }
		 }

		 String discountString = tfDiscount.getText().trim();
		 if (!discountString.isEmpty()) {
			 BigDecimal discount = null;
			 try {
				 discount = new BigDecimal(discountString);
				 lblDiscountError.setText("");
			 } catch (Exception e) {
				 valid = false;
				 lblDiscountError.setText("Not a number!");
			 }

			 if (discount != null) {
				 if (discount.signum() == -1) {
					 valid = false;
					 lblDiscountError.setText("Negative discount!");
				 }
			 }
		 }

		 String chargeString = tfCharge.getText().trim();
		 if (!chargeString.isEmpty()) {
			 BigDecimal charge = null;
			 try {
				 charge = new BigDecimal(chargeString);
				 lblChargeError.setText("");
			 } catch (Exception e) {
				 valid = false;
				 lblChargeError.setText("Not a number!");
			 }

			 if (charge != null) {
				 if (charge.signum() == -1) {
					 valid = false;
					 lblChargeError.setText("Negative charge!");
				 }
			 }
		 }

		 if (invoiceItems.isEmpty()) {
			 valid = false;
			 lblNoItemError.setText("No item added to the invoice!");
		 }

		 return valid;
	 }

	 @FXML
	 private void onSaveInvoiceAction(ActionEvent event) {
		 if (!saveInvoice()) {
			 return;
		 }

		 if (this.invoiceNumber != 0) {
			 mainWindow.close();
			 return;
		 }

		 clearSlate();
		 isDirty.set(false);

		 final FadeTransition transition = new FadeTransition(Duration.seconds(3.0),
				 checkImage);
		 transition.setFromValue(1.0);
		 transition.setToValue(0.0);
		 transition.setOnFinished((ActionEvent event1) -> {
			 tfItem.requestFocus();
		 });

		 try {
			 transition.play();
		 } catch (Exception e) {
			 // do nothing
		 }

	 }

	 private boolean saveInvoice() {
		 if (!validateInput()) {
			 Utility.beep();
			 return false;
		 }

		 Invoice invoice = getInvoiceFromInput();

		 try {
			 InvoicePersistence.saveInvoice(invoice);
		 } catch (Exception e) {
			 String message = "An error occurred in saving the invoice!";
			 Alert alert = new Alert(Alert.AlertType.ERROR, message,
					 ButtonType.OK);
			 alert.setTitle("Error Occurred");
			 alert.setHeaderText("Error in saving invoice");
			 alert.initOwner(mainWindow);
			 Utility.beep();
			 Global.styleAlertDialog(alert);
			 alert.showAndWait();
			 return false;
		 }

		 if (this.invoiceNumber != 0) {
			 fireEntityEditedEvent(invoice);
		 }

		 if (chkPrintOnSave.isSelected()) {
			 printInvoice(invoice);
		 }

		 return true;
	 }

	 private void clearSlate() {
		 cashInvoice.setSelected(true);
		 tfCustomer.clear();
		 tfCustomer.setUserData(null);
		 rdNew.setSelected(true);
		 invoiceItems.clear();
		 tfTotal.clear();
		 tfDiscount.clear();
		 tfCharge.clear();
		 tfNetAmount.clear();
		 clearItemFields();
		 clearItemErrorFields();
	 }

	 private Invoice getInvoiceFromInput() {
		 Invoice invoice = new Invoice();

		 if (this.invoiceNumber != 0) {
			 invoice.setInvoiceId(invoiceNumber);
		 }

		 invoice.setInvoiceDate(dpInvoiceDate.getValue());
		 if (creditInvoice.isSelected()) {
			 invoice.setIsCashInvoice(false);
			 Customer customer = (Customer) tfCustomer.getUserData();
			 invoice.setCustomer(customer);
		 }

		 String discountString = tfDiscount.getText().trim();
		 if (!discountString.isEmpty()) {
			 BigDecimal discount = new BigDecimal(discountString);
			 discount = discount.abs().setScale(2, RoundingMode.HALF_UP);
			 invoice.setDiscount(discount);
		 }

		 String chargeString = tfCharge.getText().trim();
		 if (!chargeString.isEmpty()) {
			 BigDecimal charge = new BigDecimal(chargeString);
			 charge = charge.abs().setScale(2, RoundingMode.HALF_UP);
			 invoice.setAdditionalCharge(charge);
		 }

		 invoice.getInvoiceItems().addAll(invoiceItems);
		 return invoice;
	 }

	 private void clearItemErrorFields() {
		 lblItemError.setText("");
		 lblUnitError.setText("");
		 lblRateError.setText("");
		 lblQuantityError.setText("");
	 }

	 private void clearInvoiceErrorFields() {
		 lblDateError.setText("");
		 lblCustomerError.setText("");
		 lblNoItemError.setText("");
		 lblDiscountError.setText("");
		 lblChargeError.setText("");
	 }

	 @FXML
	 private void onCustomersRefreshAction(ActionEvent event) {
		 loadCustomers();
	 }

	 @FXML
	 private void onItemsRefreshAction(ActionEvent event) {
		 loadItems();
	 }

	 @FXML
	 private void onUnitsRefreshAction(ActionEvent event) {
		 loadUnits();
	 }

	 @FXML
	 private void onCloseTabAction(ActionEvent event) {
		 if (shouldClose()) {
			 if (this.invoiceNumber != 0) { //updating the invoice
				 mainWindow.close();
			 } else {
				 closeTab();
			 }
		 }

	 }

	 private void closeTab() {
		 Tab tab = tabPane.getSelectionModel().getSelectedItem();
		 tabPane.getTabs().remove(tab); //close the current tab
	 }

	 private ButtonType shouldSaveUnsavedData() {
		 String message = "The invoice is unsaved.\n"
				 + "Would you like to save it before closing this window?";
		 Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
				 ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		 alert.setTitle("Unsaved Invoice");
		 alert.setHeaderText("Save invoice before closing window?");
		 alert.initOwner(mainWindow);
		 Global.styleAlertDialog(alert);

		 Optional<ButtonType> response = alert.showAndWait();
		 if (! response.isPresent()) {
			 return ButtonType.CANCEL;
		 }

		 return response.get();

	 }

	 public boolean loadInvoice(String invoiceId) {
		 this.invoiceNumber = Integer.valueOf(invoiceId);
		 Invoice invoice = null;

		 try {
			 invoice = InvoicePersistence.getInvoice(invoiceNumber);
		 } catch (Exception e) {
			 String message = "Couldn't fetch the invoice data from the database!";
			 Alert alert = Utility.getErrorAlert("Error Occurred",
					 "Error in fetching data", message, mainWindow);
			 Utility.beep();
			 alert.showAndWait();
			 return false;
		 }

		 populateControls(invoice);
		 isDirty.set(false);
		 return true;
	 }

	 /**
	  * Populates the controls with an existing invoice data.
	  *
	  * @param invoice - an existing invoice meant to be edited
	  */
	 private void populateControls(Invoice invoice) {
		 dpInvoiceDate.setValue(invoice.getInvoiceDate());

		 if (invoice.getIsCashInvoice()) {
			 cashInvoice.setSelected(true);
		 } else {
			 creditInvoice.setSelected(true);
			 Customer customer = invoice.getCustomer();
			 tfCustomer.setText(customer.getName());
			 tfCustomer.setUserData(customer);
		 }

		 List<InvoiceItem> items = invoice.getInvoiceItems();

		 invoiceItems.addAll(items);

		 setInvoiceTotal(invoice);
	 }

	 private void setInvoiceTotal(Invoice invoice) {
		 BigDecimal total = BigDecimal.ZERO;
		 BigDecimal itemAmount = null;

		 for (InvoiceItem invoiceItem : invoice.getInvoiceItems()) {
			 itemAmount = invoiceItem.getQuantity().multiply(invoiceItem.getRate())
					 .setScale(2, RoundingMode.HALF_UP);
			 total = total.add(itemAmount);
		 }

		 tfTotal.setText(total.toPlainString());

		 BigDecimal amount = invoice.getAdditionalCharge();
		 if (amount != null) {
			 tfCharge.setText(amount.toPlainString());
			 total = total.add(amount);
		 }

		 amount = invoice.getDiscount();
		 if (amount != null) {
			 tfDiscount.setText(amount.toPlainString());
			 total = total.subtract(amount);
		 }

		 tfNetAmount.setText(total.toPlainString());
	 }

	 /**
	  * Populates item names in the Item class instances
	  *
	  * @param invoiceItems
	  */

	 /**
	  * Populates names in the MeasurementUnit instances
	  *
	  * @param invoiceItems
	  */

	 @Override
	 public void setTabPane(TabPane pane) {
		 this.tabPane = pane;
	 }

	 public void addEntityEditedEventListener(EntityEditedListener listener) {
		 if (eventListeners == null) {
			 eventListeners = new ArrayList<>(2);
		 }

		 if (!eventListeners.contains(listener)) {
			 eventListeners.add(listener);
		 }

	 }

	 public void removeEntityEditedEventListener(EntityEditedListener listener) {
		 if (eventListeners != null) {
			 if (eventListeners.contains(listener)) {
				 eventListeners.remove(listener);
			 }
		 }
	 }

	 private void fireEntityEditedEvent(Object entityEdited) {
		 if (eventListeners == null || eventListeners.isEmpty()) {
			 return;
		 }

		 final EntityEditedEvent event = new EntityEditedEvent(this, entityEdited);

		 for (EntityEditedListener listener : eventListeners) {
			 listener.onEntityEdited(event);
		 }
	 }

	 private void printInvoice(Invoice invoice) {
		 JasperReport jasperReport = null;
		 JasperPrint jasperPrint = null;
		 Map map = null;        
		 try {
			 map = getReportParameters(invoice);
			 InputStream reportStream = InvoiceController.class.getResourceAsStream("/resources/reports/dt_invoice.jrxml");
			 //jasperReport = JasperCompileManager.compileReport(reportStream);
			 jasperReport = JasperCompileManager.compileReport(reportStream);
			 JRSaver.saveObject(jasperReport, "dt_invoice.jasper");
			 //InputStream reportStream = getFileAsStream("/reports/fxbilling_invoice.jasper");
			 jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(invoice.getInvoiceItems(), true));
			 if (userPreferences.getShowPrintPreview()) {
				 JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
				 jasperViewer.setTitle("Invoice");
				 jasperViewer.setVisible(true);
			 } else {
				 //command for printing the report
				 try {
					 //false means don't show the print dialog. Send output instead directly to the default printer
					 boolean showPrintDialog = userPreferences.getShowPrintDialog();
					 JasperPrintManager.printReport(jasperPrint, showPrintDialog); 
				 } catch (Exception ex) {
					 logger.logp(Level.SEVERE, InvoiceController.class.getName(), 
							 "printInvoice", "Error in printReport function call", ex);
					 Utility.beep();
					 String message = "An error occurred whilst printing the invoice!";
					 Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
					 alert.setTitle("Invoice Printing Error");
					 alert.setHeaderText("Error in printing the invoice");
					 alert.initOwner(mainWindow);
					 Global.styleAlertDialog(alert);
					 alert.showAndWait();
				 }
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
			 //    		Utility.beep();
			 //    		String message = "An error occurred whilst collecting data to print the invoice!";
			 //    		Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
			 //    		alert.setTitle("Error Occurred");
			 //    		alert.setHeaderText("Error in retrieving data for printing the invoice");
			 //    		alert.initOwner(mainWindow);
			 //    		Global.styleAlertDialog(alert);
			 //    		alert.showAndWait();
			 //    		return;
		 }

	 }

	 private InputStream getFileAsStream(String filePath) throws FileNotFoundException {
		 InputStream inStream = null;
		 File file = new File(filePath);
		 if (file.exists()) {
			 inStream = new FileInputStream(file);
		 } else {
			 inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		 }
		 return inStream;
	 }



	 private Map getReportParameters(Invoice invoice)  throws Exception{
		 HashMap map = new HashMap(12); //represents report parameters

		 map.put("invoiceNumber",new Integer(invoice.getInvoiceId()));
		 map.put("invoiceDate", invoice.getInvoiceDate());

		 if (!invoice.getIsCashInvoice()) {
			 Customer customer = invoice.getCustomer();
			 map.put("customerName", customer.getName());

			 String city = invoice.getCustomer().getCity();
			 if (!(city == null || city.isEmpty())) {
				 map.put("customerCity", city);
			 }
		 }

		 BigDecimal amount = invoice.getAdditionalCharge();
		 if (amount != null) {
			 map.put("additionalCharge", amount);
		 }

		 amount = invoice.getDiscount();
		 if (amount != null) {
			 map.put("discount", amount);
		 }

		 FirmDetails firmDetails = FirmDetailsPersistence.getData();
		 if (firmDetails != null) {
			 map.put("firmName", firmDetails.getFirmName());
			 map.put("firmAddress", firmDetails.getAddress());
			 String str = firmDetails.getPhoneNumbers();
			 if (str != null) {
				 map.put("firmPhoneNumbers", str);
			 }

			 str = firmDetails.getEmailAddress();
			 if (str != null) {
				 map.put("firmEmailAddress", str);
			 }

			 byte[] logoBytes = firmDetails.getLogo();
			 if (logoBytes != null) {
				 ByteArrayInputStream byteArrayStream = 
						 new ByteArrayInputStream(logoBytes);
				 map.put("firmLogo", byteArrayStream);
			 }
		 }


		 return map;
	 }

	 private void setTableCellValueFactory() {
		 amountColumn.setCellValueFactory((TableColumn.CellDataFeatures<InvoiceItem, BigDecimal> param) -> {
			 InvoiceItem invoiceItem = param.getValue();
			 ObjectBinding<BigDecimal> binding = new ObjectBinding<BigDecimal>() {
				 {
					 super.bind(invoiceItem.rateProperty(), invoiceItem.quantityProperty());
				 }

				 @Override
				 protected BigDecimal computeValue() {
					 return invoiceItem.getRate().multiply(invoiceItem.getQuantity())
							 .setScale(2, RoundingMode.HALF_UP);
				 }
			 };
			 return binding;
		 });
	 }

	 private void setTableCellFactories() {

		 final Callback<TableColumn<InvoiceItem, BigDecimal>, TableCell<InvoiceItem, BigDecimal>> callback
		 = new Callback<TableColumn<InvoiceItem, BigDecimal>, TableCell<InvoiceItem, BigDecimal>>() {

			 @Override
			 public TableCell<InvoiceItem, BigDecimal> call(TableColumn<InvoiceItem, BigDecimal> param) {
				 TableCell<InvoiceItem, BigDecimal> tableCell = new TableCell<InvoiceItem, BigDecimal>() {

					 @Override
					 protected void updateItem(BigDecimal item, boolean empty) {
						 super.updateItem(item, empty);
						 if (empty) {
							 super.setText(null);
						 } else {
							 super.setText(IndianCurrencyFormatting.applyFormatting(item));
						 }
					 }

				 };
				 tableCell.getStyleClass().add("numeric-cell");
				 return tableCell;
			 }
		 };

		 rateColumn.setCellFactory(callback);
		 amountColumn.setCellFactory(callback);

		 quantityColumn.setCellFactory((TableColumn<InvoiceItem, BigDecimal> param) -> {
			 TableCell<InvoiceItem, BigDecimal> tableCell = new TableCell<InvoiceItem, BigDecimal>() {

				 @Override
				 protected void updateItem(BigDecimal item, boolean empty) {
					 super.updateItem(item, empty);
					 if (empty) {
						 super.setText(null);
					 } else {
						 super.setText(quantityFormat.format(item));
					 }
				 }

			 };
			 tableCell.getStyleClass().add("numeric-cell");
			 return tableCell;
		 });

		 itemColumn.setCellFactory((TableColumn<InvoiceItem, Item> param) -> {
			 final TableCell<InvoiceItem, Item> cell = new TableCell<InvoiceItem, Item>() {

				 @Override
				 protected void updateItem(Item item, boolean empty) {
					 super.updateItem(item, empty); 
					 if (empty) {
						 setText(null);
					 } else {
						 setText(item.getItemName());
					 }
				 }

			 };
			 cell.getStyleClass().add("character-cell");
			 return cell;
		 });

		 unitColumn.setCellFactory((TableColumn<InvoiceItem, MeasurementUnit> param) -> {
			 final TableCell<InvoiceItem, MeasurementUnit> cell = 
					 new TableCell<InvoiceItem, MeasurementUnit>() {

				 @Override
				 protected void updateItem(MeasurementUnit item, boolean empty) {
					 super.updateItem(item, empty); 
					 if (empty) {
						 setText(null);
					 } else {
						 setText(item.getUnitName());
					 }
				 }

			 };
			 cell.getStyleClass().add("character-cell");
			 return cell;
		 });


	 }

}

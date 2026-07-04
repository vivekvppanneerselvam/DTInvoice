package com.dt.misc;

import com.dt.dao.DatabaseConnect;
import com.dt.utils.TabContent;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ExpenseController implements TabContent {

    // =====================================================
    // LEFT SIDE
    // =====================================================

    @FXML
    private TextField tfExpenseNo;

    @FXML
    private DatePicker dpExpenseDate;

    @FXML
    private ToggleButton btnFuel;

    @FXML
    private ToggleButton btnFood;

    @FXML
    private ToggleButton btnTransport;

    @FXML
    private ToggleButton btnInternet;

    @FXML
    private ToggleButton btnElectricity;

    @FXML
    private ToggleButton btnCourier;

    @FXML
    private ToggleButton btnPacking;

    @FXML
    private ToggleButton btnMaintenance;

    @FXML
    private ToggleButton btnMisc;

    @FXML
    private ToggleButton btnOther;

    @FXML
    private Label lblOther;

    @FXML
    private TextField tfOtherExpense;

    @FXML
    private TextField tfVendor;

    @FXML
    private TextField tfAmount;

    @FXML
    private TextArea tfRemarks;

    @FXML
    private Label lblAttachment;

    // =====================================================
    // RIGHT SIDE
    // =====================================================

    @FXML
    private ComboBox<String> cbDateFilter;

    @FXML
    private ComboBox<String> cbTypeFilter;

    @FXML
    private TextField tfSearch;

    @FXML
    private TableView<ExpenseModel> expenseTable;

    @FXML
    private TableColumn<ExpenseModel, String> colDate;

    @FXML
    private TableColumn<ExpenseModel, String> colType;

    @FXML
    private TableColumn<ExpenseModel, String> colVendor;

    @FXML
    private TableColumn<ExpenseModel, Double> colAmount;

    @FXML
    private TableColumn<ExpenseModel, String> colRemarks;

    @FXML
    private TableColumn<ExpenseModel, String> colAttachment;

    @FXML
    private Label lblEntries;

    @FXML
    private Label lblTotalExpense;

    // =====================================================
    // VARIABLES
    // =====================================================

    private ExpenseDAO expenseDAO;

    private final ObservableList<ExpenseModel> expenseList =
            FXCollections.observableArrayList();

    private FilteredList<ExpenseModel> filteredExpenses;

    private ExpenseModel selectedExpense;

    private String attachmentPath;

    private ToggleGroup expenseGroup;

    @FXML
    private TableColumn<ExpenseModel, Void> colAction;

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        try {

            expenseDAO =
                    new ExpenseDAO(
                            DatabaseConnect.getConnection()
                    );

            expenseDAO.createTable();

            initializeDate();

            initializeExpenseButtons();

            initializeFilters();

            configureTable();

            loadExpenses();

            setupSearch();

            generateExpenseNo();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void initializeDate() {

        dpExpenseDate.setValue(
                LocalDate.now()
        );

        dpExpenseDate.setDayCellFactory(
                picker -> new DateCell() {

                    @Override
                    public void updateItem(
                            LocalDate date,
                            boolean empty) {

                        super.updateItem(
                                date,
                                empty
                        );

                        setDisable(
                                empty
                                        ||
                                        date.isAfter(
                                                LocalDate.now()
                                        )
                        );
                    }
                });
    }

    private void initializeExpenseButtons() {

        expenseGroup =
                new ToggleGroup();

        btnFuel.setToggleGroup(expenseGroup);
        btnFood.setToggleGroup(expenseGroup);
        btnTransport.setToggleGroup(expenseGroup);
        btnInternet.setToggleGroup(expenseGroup);
        btnElectricity.setToggleGroup(expenseGroup);
        btnCourier.setToggleGroup(expenseGroup);
        btnPacking.setToggleGroup(expenseGroup);
        btnMaintenance.setToggleGroup(expenseGroup);
        btnMisc.setToggleGroup(expenseGroup);
        btnOther.setToggleGroup(expenseGroup);

        lblOther.setVisible(false);
        lblOther.setManaged(false);

        tfOtherExpense.setVisible(false);
        tfOtherExpense.setManaged(false);

        btnOther.selectedProperty()
                .addListener((obs,
                              oldValue,
                              selected) -> {

                    lblOther.setVisible(selected);
                    lblOther.setManaged(selected);

                    tfOtherExpense.setVisible(selected);
                    tfOtherExpense.setManaged(selected);
                });
    }

    private void initializeFilters() {

        cbDateFilter.getItems().addAll(

                "Today",
                "Yesterday",
                "Last 7 Days",
                "This Month",
                "All"
        );

        cbDateFilter.setValue(
                "Today"
        );

        cbTypeFilter.getItems().addAll(

                "All",

                "Fuel",
                "Food",
                "Transport",
                "Internet",
                "EB Bill",
                "Courier",
                "Packing",
                "Maintenance",
                "Misc"
        );

        cbTypeFilter.setValue(
                "All"
        );
    }

    private void configureTable() {
        addActionButtons();
        colDate.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getExpenseDate()
                )
        );

        colType.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getExpenseType()
                )
        );

        colVendor.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getVendorName()
                )
        );

        colAmount.setCellValueFactory(
                c -> new SimpleObjectProperty<>(
                        c.getValue().getAmount()
                )
        );

        colRemarks.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getRemarks()
                )
        );

        colAttachment.setCellValueFactory(
                c -> new SimpleStringProperty(

                        c.getValue()
                                .hasAttachment()

                                ? "View"

                                : "-"
                )
        );
    }



    private void setupSearch() {

        tfSearch.textProperty()
                .addListener((obs, oldVal, newVal)
                        -> applyFilters());

        cbDateFilter.valueProperty()
                .addListener((obs, oldVal, newVal)
                        -> applyFilters());

        cbTypeFilter.valueProperty()
                .addListener((obs, oldVal, newVal)
                        -> applyFilters());
    }

    private void applyFilters() {

        filteredExpenses.setPredicate(expense -> {

            String keyword =

                    tfSearch.getText() == null
                            ? ""
                            : tfSearch.getText()
                              .toLowerCase();

            boolean searchMatch =

                    expense.getExpenseType()
                            .toLowerCase()
                            .contains(keyword)

                            ||

                            expense.getVendorName()
                                    .toLowerCase()
                                    .contains(keyword)

                            ||

                            expense.getRemarks()
                                    .toLowerCase()
                                    .contains(keyword);

            boolean typeMatch =

                    cbTypeFilter.getValue()
                            .equals("All")

                            ||

                            expense.getExpenseType()
                                    .equalsIgnoreCase(
                                            cbTypeFilter.getValue()
                                    );

            return searchMatch && typeMatch;
        });

        updateSummary();
    }

    @FXML
    private void onChooseFile() {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Choose Expense Bill"
        );

        File file =
                chooser.showOpenDialog(null);

        if (file != null) {

            attachmentPath =
                    file.getAbsolutePath();

            lblAttachment.setText(
                    file.getName()
            );
        }
    }

    private String getSelectedExpenseType() {

        Toggle selected =
                expenseGroup.getSelectedToggle();

        if (selected == null) {

            return "";
        }

        ToggleButton button =
                (ToggleButton) selected;

        if (button == btnOther) {

            return tfOtherExpense
                    .getText()
                    .trim();
        }

        return button.getText();
    }

    private void clearForm() {

        expenseGroup.selectToggle(null);

        tfOtherExpense.clear();

        tfVendor.clear();

        tfAmount.clear();

        tfRemarks.clear();

        lblAttachment.setText("");

        attachmentPath = null;

        dpExpenseDate.setValue(
                LocalDate.now()
        );

        lblOther.setVisible(false);
        lblOther.setManaged(false);

        tfOtherExpense.setVisible(false);
        tfOtherExpense.setManaged(false);
    }

    private void generateExpenseNo() {

        try {

            tfExpenseNo.setText(
                    expenseDAO.getNextExpenseNo()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void updateSummary() {

        lblEntries.setText(
                String.valueOf(
                        filteredExpenses.size()
                )
        );

        double total =

                filteredExpenses.stream()

                        .mapToDouble(
                                ExpenseModel::getAmount
                        )

                        .sum();

        lblTotalExpense.setText(

                "₹ " +

                        String.format(
                                "%.2f",
                                total
                        )
        );
    }

    private void deleteExpense(ExpenseModel expense) {

        try {

            Alert confirm =
                    new Alert(
                            Alert.AlertType.CONFIRMATION
                    );

            confirm.setTitle("Delete Expense");

            confirm.setHeaderText(
                    "Delete Selected Expense?"
            );

            confirm.setContentText(
                    "Expense No : " +
                            expense.getExpenseNo()
                            +
                            "\n\nThis action cannot be undone."
            );

            ButtonType result =
                    confirm.showAndWait()
                            .orElse(ButtonType.CANCEL);

            if (result != ButtonType.OK) {

                return;
            }

            expenseDAO.delete(
                    expense.getId()
            );

            // Clear selected item if deleting current row

            if (selectedExpense != null
                    &&
                    selectedExpense.getId()
                            == expense.getId()) {

                selectedExpense = null;

                clearForm();

                generateExpenseNo();
            }

            // Refresh table

            loadExpenses();

            updateSummary();

            showInfo(
                    "Expense deleted successfully."
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to delete expense.\n\n"
                            + e.getMessage()
            );
        }
    }

    private void showInfo(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    private void showError(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    private void editExpense(
            ExpenseModel expense) {

        try {

            selectedExpense = expense;

            tfExpenseNo.setText(
                    expense.getExpenseNo()
            );

            dpExpenseDate.setValue(
                    LocalDate.parse(
                            expense.getExpenseDate()
                    )
            );

            tfVendor.setText(
                    expense.getVendorName()
            );

            tfAmount.setText(
                    String.valueOf(
                            expense.getAmount()
                    )
            );

            tfRemarks.setText(
                    expense.getRemarks()
            );

            attachmentPath =
                    expense.getAttachmentPath();

            if (attachmentPath != null
                    &&
                    !attachmentPath.isBlank()) {

                lblAttachment.setText(
                        new File(
                                attachmentPath
                        ).getName()
                );

            } else {

                lblAttachment.setText("");
            }

            selectExpenseType(
                    expense.getExpenseType()
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    e.getMessage()
            );
        }
    }
    private void selectExpenseType(String expenseType) {

        // Reset

        expenseGroup.selectToggle(null);

        lblOther.setVisible(false);
        lblOther.setManaged(false);

        tfOtherExpense.setVisible(false);
        tfOtherExpense.setManaged(false);

        tfOtherExpense.clear();

        if (expenseType == null ||
                expenseType.isBlank()) {

            return;
        }

        switch (expenseType.trim().toLowerCase()) {

            case "fuel" ->

                    expenseGroup.selectToggle(btnFuel);

            case "food" ->

                    expenseGroup.selectToggle(btnFood);

            case "transport" ->

                    expenseGroup.selectToggle(btnTransport);

            case "internet" ->

                    expenseGroup.selectToggle(btnInternet);

            case "eb bill",
                 "electricity" ->

                    expenseGroup.selectToggle(btnElectricity);

            case "courier" ->

                    expenseGroup.selectToggle(btnCourier);

            case "packing" ->

                    expenseGroup.selectToggle(btnPacking);

            case "maintenance" ->

                    expenseGroup.selectToggle(btnMaintenance);

            case "misc" ->

                    expenseGroup.selectToggle(btnMisc);

            default -> {

                // Custom Expense Type

                expenseGroup.selectToggle(btnOther);

                lblOther.setVisible(true);
                lblOther.setManaged(true);

                tfOtherExpense.setVisible(true);
                tfOtherExpense.setManaged(true);

                tfOtherExpense.setText(
                        expenseType
                );
            }
        }
    }
    private void addActionButtons() {

        colAction.setCellFactory(param ->
                new TableCell<>() {

                    private final Button btnEdit =
                            new Button("Edit");

                    private final Button btnDelete =
                            new Button("Delete");

                    private final Button btnBill =
                            new Button("View Bill");

                    {
                        btnEdit.setOnAction(e -> {

                            ExpenseModel expense =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            editExpense(expense);
                        });

                        btnDelete.setOnAction(e -> {

                            ExpenseModel expense =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            deleteExpense(expense);
                        });

                        btnBill.setOnAction(e -> {

                            ExpenseModel expense =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            openAttachment(expense);
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty) {

                            setGraphic(null);

                        } else {

                            HBox box =
                                    new HBox(
                                            5,
                                            btnEdit,
                                            btnDelete,
                                            btnBill
                                    );

                            setGraphic(box);
                        }
                    }
                });
    }

    private void openAttachment(
            ExpenseModel expense) {

        try {

            String path =
                    expense.getAttachmentPath();

            if (path == null ||
                    path.isBlank()) {

                showInfo(
                        "No attachment available."
                );

                return;
            }

            File file =
                    new File(path);

            if (!file.exists()) {

                showError(
                        "Attachment file not found.\n\n"
                                + path
                );

                return;
            }

            if (!Desktop.isDesktopSupported()) {

                showError(
                        "Desktop operation is not supported."
                );

                return;
            }

            Desktop.getDesktop().open(file);

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to open attachment.\n\n"
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void onSaveExpense() {

        try {

            String expenseType =
                    getSelectedExpenseType();

            if (expenseType == null ||
                    expenseType.isBlank()) {

                showError(
                        "Please select Expense Type"
                );

                return;
            }

            if (tfAmount.getText() == null ||
                    tfAmount.getText().isBlank()) {

                showError(
                        "Please enter Amount"
                );

                return;
            }

            double amount;

            try {

                amount = Double.parseDouble(
                        tfAmount.getText().trim()
                );

            } catch (Exception ex) {

                showError(
                        "Invalid Amount"
                );

                return;
            }

            if (amount <= 0) {

                showError(
                        "Amount must be greater than 0"
                );

                return;
            }

            ExpenseModel expense =
                    new ExpenseModel(

                            selectedExpense == null
                                    ? 0
                                    : selectedExpense.getId(),

                            tfExpenseNo.getText(),

                            dpExpenseDate
                                    .getValue()
                                    .toString(),

                            expenseType,

                            tfVendor.getText(),

                            amount,

                            tfRemarks.getText(),

                            attachmentPath
                    );

            if (selectedExpense == null) {

                expenseDAO.insert(
                        expense
                );

                showInfo(
                        "Expense Saved Successfully"
                );

            } else {

                expenseDAO.update(
                        expense
                );

                showInfo(
                        "Expense Updated Successfully"
                );
            }

            selectedExpense = null;

            clearForm();

            generateExpenseNo();

            loadExpenses();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    e.getMessage()
            );
        }
    }

    private void loadExpenses() {

        try {

            expenseList.clear();

            String filter =
                    cbDateFilter.getValue();

            if (filter == null ||
                    filter.equals("Today")) {

                expenseList.addAll(
                        expenseDAO.getTodayExpenses()
                );

            } else {

                expenseList.addAll(
                        expenseDAO.getAll()
                );
            }

            filteredExpenses =
                    new FilteredList<>(
                            expenseList,
                            p -> true
                    );

            expenseTable.setItems(
                    filteredExpenses
            );

            updateSummary();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void selectExpenseButton(
            String type) {

        for (Toggle toggle :
                expenseGroup.getToggles()) {

            ToggleButton btn =
                    (ToggleButton) toggle;

            if (btn.getText()
                    .equalsIgnoreCase(type)) {

                expenseGroup.selectToggle(btn);

                return;
            }
        }

        expenseGroup.selectToggle(btnOther);

        tfOtherExpense.setText(type);
    }

    @FXML
    private void onReset() {

        selectedExpense = null;

        clearForm();

        generateExpenseNo();
    }

    @Override
    public void setMainWindow(Stage stage) {
    }

    @Override
    public void setTabPane(TabPane tabPane) {
    }

    @Override
    public boolean loadData() {
        return true;
    }

    @Override
    public boolean shouldClose() {
        return true;
    }

    @Override
    public void putFocusOnNode() {

        if (tfAmount != null) {

            tfAmount.requestFocus();
        }
    }

}

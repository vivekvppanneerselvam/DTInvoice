package com.dt.inventory;

import com.dt.dao.DatabaseConnect;
import com.dt.utils.TabContent;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.*;

import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import java.sql.Connection;

import java.util.List;
import java.util.stream.Collectors;

public class ModernItemsController
        implements TabContent {

    @FXML
    private ComboBox<String> cbFilter;
    @FXML
    private ComboBox<CategoryModel> cbFilterCategory;

    @FXML
    private TreeView<String> categoryTree;

    @FXML
    private TextField tfSubCategory;

    // =====================================================
    // LEFT FORM
    // =====================================================

    @FXML
    private TextField tfItemCode;

    @FXML
    private TextField tfItemName;

    @FXML
    private ComboBox<CategoryModel> cbCategory;

    @FXML
    private ComboBox<SubCategoryModel> cbSubCategory;

    @FXML
    private CheckBox chkGST;

    @FXML
    private TextField tfGST;

    @FXML
    private Label lblImage;

    // =====================================================
    // MEASUREMENT TABLE
    // =====================================================

    @FXML
    private TableView<ItemMeasurement> measurementTable;

    @FXML
    private TableColumn<ItemMeasurement, Double> colQty;

    @FXML
    private TableColumn<ItemMeasurement, String> colUnit;

    @FXML
    private TableColumn<ItemMeasurement, Double> colSelling;

    @FXML
    private TableColumn<ItemMeasurement, Double> colPurchase;

    @FXML
    private TableColumn<ItemMeasurement, Double> colStock;

    // =====================================================
    // RIGHT TABLE
    // =====================================================

    @FXML
    private TextField tfSearch;



    @FXML
    private TableView<ItemModel> itemTable;

    @FXML
    private TableColumn<ItemModel, String> colCodeMain;

    @FXML
    private TableColumn<ItemModel, String> colNameMain;

    @FXML
    private TableColumn<ItemModel, String> colCategoryMain;

    @FXML
    private TableColumn<ItemModel, String> colSubCategoryMain;

    @FXML
    private TableColumn<ItemModel, String> colGSTMain;

    @FXML
    private TableColumn<ItemModel, Double> colPriceMain;

    @FXML
    private TableColumn<ItemModel, Double> colStockMain;

    @FXML
    private TableColumn<ItemModel, String> colStatusMain;

    // =====================================================
    // DAO
    // =====================================================

    private ItemDAO itemDAO;

    private MeasurementDAO measurementDAO;

    private CategoryDAO categoryDAO;

    private SubCategoryDAO subCategoryDAO;

    // =====================================================
    // DATA
    // =====================================================

    private final ObservableList<ItemModel> items =
            FXCollections.observableArrayList();

    private final ObservableList<ItemMeasurement> measurements =
            FXCollections.observableArrayList();

    private ItemModel selectedItem;

    private String imagePath;

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

        try {

            Connection connection =
                    DatabaseConnect.getConnection();

            itemDAO =
                    new ItemDAO(connection);

            measurementDAO =
                    new MeasurementDAO(connection);

            categoryDAO =
                    new CategoryDAO(connection);

            subCategoryDAO =
                    new SubCategoryDAO(connection);

            initializeCombo();

            initializeCategoryListener();

            initializeMeasurementTable();

            initializeItemTable();

            initializeSearch();

            initializeSelection();

            loadItems();

            generateItemCode();

            initializeCategoryTree();
            initializeSelection();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // COMBO
    // =====================================================

    private void initializeCombo()
            throws Exception {

        List<CategoryModel> categories =
                categoryDAO.getAll();

        cbCategory.getItems().addAll(categories);

        cbFilterCategory.getItems().addAll(categories);

        cbSubCategory.getItems().addAll(
                subCategoryDAO.getAll()
        );

        cbFilter.getItems().addAll(
                "All",
                "GST Items",
                "Non GST"
        );

        cbFilter.getSelectionModel()
                .select("All");
    }

    // =====================================================
    // MEASUREMENT TABLE
    // =====================================================

    private void initializeMeasurementTable() {

        colQty.setCellValueFactory(data ->

                new SimpleObjectProperty<>(
                        data.getValue().getQuantity()
                )
        );

        colUnit.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getUnit()
                )
        );

        colSelling.setCellValueFactory(data ->

                new SimpleObjectProperty<>(
                        data.getValue().getSellingPrice()
                )
        );

        colPurchase.setCellValueFactory(data ->

                new SimpleObjectProperty<>(
                        data.getValue().getPurchasePrice()
                )
        );

        colStock.setCellValueFactory(data ->

                new SimpleObjectProperty<>(
                        data.getValue().getCurrentStock()
                )
        );

        measurementTable.setItems(measurements);
    }

    // =====================================================
    // ITEM TABLE
    // =====================================================

    private void initializeItemTable() {

        colCodeMain.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getItemCode()
                )
        );

        colNameMain.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getItemName()
                )
        );

        colCategoryMain.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getCategoryName()
                )
        );

        colSubCategoryMain.setCellValueFactory(data ->

                new SimpleStringProperty(
                        data.getValue().getSubCategoryName()
                )
        );

        colGSTMain.setCellValueFactory(data ->

                new SimpleStringProperty(

                        data.getValue().isGstEnabled()

                                ?

                                data.getValue()
                                .getGstPercentage() + "%"

                                :

                                "No GST"
                )
        );

        colPriceMain.setCellValueFactory(data -> {

            try {

                List<ItemMeasurement> list =
                        measurementDAO.getByItem(
                                data.getValue().getId()
                        );

                if (list.isEmpty()) {

                    return new SimpleObjectProperty<>(0.0);
                }

                return new SimpleObjectProperty<>(

                        list.get(0)
                                .getSellingPrice()
                );

            } catch (Exception e) {

                return new SimpleObjectProperty<>(0.0);
            }
        });

        colStockMain.setCellValueFactory(data -> {

            try {

                List<ItemMeasurement> list =
                        measurementDAO.getByItem(
                                data.getValue().getId()
                        );

                double stock =

                        list.stream()

                                .mapToDouble(
                                        ItemMeasurement::getCurrentStock
                                )

                                .sum();

                return new SimpleObjectProperty<>(stock);

            } catch (Exception e) {

                return new SimpleObjectProperty<>(0.0);
            }
        });

        colStatusMain.setCellValueFactory(data -> {

            try {

                List<ItemMeasurement> list =
                        measurementDAO.getByItem(
                                data.getValue().getId()
                        );

                double stock =

                        list.stream()

                                .mapToDouble(
                                        ItemMeasurement::getCurrentStock
                                )

                                .sum();

                return new SimpleStringProperty(

                        stock > 0

                                ?

                                "Available"

                                :

                                "Out Of Stock"
                );

            } catch (Exception e) {

                return new SimpleStringProperty(
                        "Unknown"
                );
            }
        });

        itemTable.setItems(items);
    }

    // =====================================================
    // SELECTION
    // =====================================================

    private void initializeSelection() {

        itemTable.getSelectionModel()

                .selectedItemProperty()

                .addListener((obs,
                              oldVal,
                              item) -> {

                    if (item == null) {
                        return;
                    }

                    selectedItem = item;

                    fillForm(item);
                });
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void initializeSearch() {

        tfSearch.textProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> filterItems());

        cbFilter.valueProperty()

                .addListener((obs,
                              oldVal,
                              newVal) -> filterItems());
    }

    // =====================================================
    // LOAD
    // =====================================================

    private void loadItems()
            throws Exception {

        items.clear();

        items.addAll(
                itemDAO.getAll()
        );
    }

    // =====================================================
    // FILTER
    // =====================================================

    private void filterItems() {

        try {

            List<ItemModel> all =
                    itemDAO.getAll();

            String search =
                    tfSearch.getText()
                            .toLowerCase();

            String filter =
                    cbFilter.getValue();

            List<ItemModel> filtered =

                    all.stream()

                            .filter(item -> {

                                boolean matchesSearch =

                                        item.getItemName()
                                                .toLowerCase()
                                                .contains(search)

                                                ||

                                                item.getItemCode()
                                                        .toLowerCase()
                                                        .contains(search);

                                boolean matchesFilter =

                                        filter.equals("All")

                                                ||

                                                (

                                                        filter.equals("GST Items")

                                                                &&

                                                                item.isGstEnabled()
                                                )

                                                ||

                                                (

                                                        filter.equals("Non GST")

                                                                &&

                                                                !item.isGstEnabled()
                                                );

                                return matchesSearch
                                        &&
                                        matchesFilter;
                            })

                            .collect(Collectors.toList());

            items.setAll(filtered);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // FILL FORM
    // =====================================================

    private void fillForm(ItemModel item) {

        tfItemCode.setText(
                item.getItemCode()
        );

        tfItemName.setText(
                item.getItemName()
        );

        chkGST.setSelected(
                item.isGstEnabled()
        );

        tfGST.setText(

                String.valueOf(
                        item.getGstPercentage()
                )
        );

        imagePath =
                item.getImagePath();

        lblImage.setText(

                imagePath != null

                        ?

                        "Image Selected"

                        :

                        "No Image"
        );

        // CATEGORY

        for (CategoryModel category :
                cbCategory.getItems()) {

            if (category.getId()
                    ==
                    item.getCategoryId()) {

                cbCategory.setValue(category);

                break;
            }
        }

        // SUB CATEGORY

        for (SubCategoryModel sub :
                cbSubCategory.getItems()) {

            if (sub.getId()
                    ==
                    item.getSubCategoryId()) {

                cbSubCategory.setValue(sub);

                break;
            }
        }

        // MEASUREMENTS

        try {

            measurements.clear();

            measurements.addAll(

                    measurementDAO.getByItem(
                            item.getId()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // ADD MEASUREMENT
    // =====================================================

    @FXML
    private void onAddMeasurement() {

        Dialog<ItemMeasurement> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Add Measurement"
        );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(

                        ButtonType.OK,
                        ButtonType.CANCEL
                );

        GridPane grid =
                new GridPane();

        grid.setHgap(10);

        grid.setVgap(10);

        TextField tfQty =
                new TextField();

        ComboBox<String> cbUnit =
                new ComboBox<>();

        cbUnit.getItems().addAll(
                "PCS",
                "BOX",
                "KG",
                "LTR",
                "PACK"
        );

        cbUnit.getSelectionModel()
                .select("PCS");

        TextField tfSelling =
                new TextField();

        TextField tfPurchase =
                new TextField();

        TextField tfStock =
                new TextField();

        grid.add(new Label("Qty"), 0, 0);
        grid.add(tfQty, 1, 0);

        grid.add(new Label("Unit"), 0, 1);
        grid.add(cbUnit, 1, 1);

        grid.add(new Label("Selling"), 0, 2);
        grid.add(tfSelling, 1, 2);

        grid.add(new Label("Purchase"), 0, 3);
        grid.add(tfPurchase, 1, 3);

        grid.add(new Label("Stock"), 0, 4);
        grid.add(tfStock, 1, 4);

        dialog.getDialogPane()
                .setContent(grid);

        dialog.setResultConverter(button -> {

            if (button == ButtonType.OK) {

                return new ItemMeasurement(

                        0,
                        0,

                        parseDouble(
                                tfQty.getText()
                        ),

                        cbUnit.getValue(),

                        parseDouble(
                                tfSelling.getText()
                        ),

                        parseDouble(
                                tfPurchase.getText()
                        ),

                        parseDouble(
                                tfStock.getText()
                        )
                );
            }

            return null;
        });

        dialog.showAndWait()

                .ifPresent(measurements::add);
    }

    // =====================================================
    // DELETE MEASUREMENT
    // =====================================================

    @FXML
    private void onDeleteMeasurement() {

        ItemMeasurement selected =

                measurementTable.getSelectionModel()
                        .getSelectedItem();

        if (selected != null) {

            measurements.remove(selected);
        }
    }

    // =====================================================
    // IMAGE
    // =====================================================

    @FXML
    private void onUploadImage() {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Choose Item Image"
        );

        File file =
                chooser.showOpenDialog(null);

        if (file != null) {

            imagePath =
                    file.getAbsolutePath();

            lblImage.setText(
                    file.getName()
            );
        }
    }

    // =====================================================
    // SAVE
    // =====================================================

    @FXML
    private void onSave() {

        try {

            ItemModel item =
                    buildItem();

            if (item == null) {
                return;
            }

            int itemId;

            if (selectedItem == null) {

                itemDAO.insert(item);

                itemId =
                        itemDAO.getLastInsertedId();

            } else {

                item.setId(
                        selectedItem.getId()
                );

                itemDAO.update(item);

                itemId =
                        selectedItem.getId();
            }

            // SAVE MEASUREMENTS

            for (ItemMeasurement measurement
                    : measurements) {

                measurement.setItemId(itemId);

                measurementDAO.insert(measurement);
            }

            loadItems();

            clearForm();

            showSuccess(
                    "Item Saved Successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable To Save Item"
            );
        }
    }

    // =====================================================
    // DELETE
    // =====================================================

    @FXML
    private void onDelete() {

        if (selectedItem == null) {

            showError(
                    "Select Item First"
            );

            return;
        }

        try {

            itemDAO.delete(
                    selectedItem.getId()
            );

            loadItems();

            clearForm();

            showSuccess(
                    "Item Deleted"
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // RESET
    // =====================================================

    @FXML
    private void onReset() {

        clearForm();
    }

    // =====================================================
    // BUILD
    // =====================================================

    private ItemModel buildItem() {

        if (cbCategory.getValue() == null) {

            showError(
                    "Please Select Category"
            );

            return null;
        }

        ItemModel item =
                new ItemModel();

        item.setItemCode(
                tfItemCode.getText()
        );

        item.setItemName(
                tfItemName.getText()
        );

        item.setCategoryId(
                cbCategory.getValue().getId()
        );

        item.setCategoryName(
                cbCategory.getValue()
                        .getCategoryName()
        );

        if (cbSubCategory.getValue() != null) {

            item.setSubCategoryId(
                    cbSubCategory.getValue()
                            .getId()
            );

            item.setSubCategoryName(
                    cbSubCategory.getValue()
                            .getSubCategoryName()
            );
        }

        item.setImagePath(
                imagePath
        );

        item.setGstEnabled(
                chkGST.isSelected()
        );

        item.setGstPercentage(

                chkGST.isSelected()

                        ?

                        parseDouble(
                                tfGST.getText()
                        )

                        :

                        0
        );

        return item;
    }

    // =====================================================
    // CLEAR
    // =====================================================

    private void clearForm() {

        selectedItem = null;

        tfItemCode.clear();

        tfItemName.clear();

        tfGST.clear();

        chkGST.setSelected(false);

        cbCategory.getSelectionModel()
                .clearSelection();

        cbSubCategory.getSelectionModel()
                .clearSelection();

        measurements.clear();

        imagePath = null;

        lblImage.setText(
                "No Image Selected"
        );

        generateItemCode();
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private void generateItemCode() {

        tfItemCode.setText(

                "ITM-"

                        +

                        java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
    }

    private double parseDouble(String value) {

        try {

            if (value == null
                    ||
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

    private void showSuccess(String msg) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(msg);

        alert.showAndWait();
    }

    // =====================================================
    // TAB CONTENT
    // =====================================================

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

        if (tfSearch != null) {

            tfSearch.requestFocus();
        }
    }

    private void initializeCategoryListener() {

        cbCategory.valueProperty()

                .addListener((obs,
                              oldVal,
                              category) -> {

                    if (category == null) {
                        return;
                    }

                    try {

                        cbSubCategory.getItems().clear();

                        cbSubCategory.getItems().addAll(

                                subCategoryDAO.getByCategory(
                                        category.getId()
                                )
                        );

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                });
    }

    @FXML
    private void onAddCategory() {

        TextInputDialog dialog =
                new TextInputDialog();

        dialog.setTitle("Add Category");

        dialog.setHeaderText(
                "Enter Category Name"
        );

        dialog.showAndWait()

                .ifPresent(name -> {

                    try {

                        CategoryModel category =
                                new CategoryModel();

                        category.setCategoryName(name);

                        categoryDAO.insert(category);

                        initializeCategoryTree();

                        initializeCombo();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                });
    }

    @FXML
    private void onUpdateSubCategory() {

        showSuccess(
                "Update Feature Ready"
        );
    }

    @FXML
    private void onDeleteSubCategory() {

        showSuccess(
                "Delete Feature Ready"
        );
    }

    private void initializeCategoryTree()
            throws Exception {

        TreeItem<String> root =
                new TreeItem<>("All Categories");

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

        categoryTree.setRoot(root);

        categoryTree.setShowRoot(false);
    }

    @FXML
    private void onAddSubCategory() {

        TreeItem<String> selected =

                categoryTree.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            showError(
                    "Select Category"
            );

            return;
        }

        try {

            String categoryName =
                    selected.getValue();

            CategoryModel category =

                    categoryDAO.getAll()

                            .stream()

                            .filter(c ->

                                    c.getCategoryName()
                                            .equals(categoryName)
                            )

                            .findFirst()

                            .orElse(null);

            if (category == null) {

                showError(
                        "Select Valid Category"
                );

                return;
            }

            SubCategoryModel sub =
                    new SubCategoryModel();

            sub.setCategoryId(
                    category.getId()
            );

            sub.setSubCategoryName(
                    tfSubCategory.getText()
            );

            subCategoryDAO.insert(sub);

            initializeCategoryTree();

            tfSubCategory.clear();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
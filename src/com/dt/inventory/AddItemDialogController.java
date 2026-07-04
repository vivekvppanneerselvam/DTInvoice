package com.dt.inventory;

import com.dt.inventory.CategoryDAO;
import com.dt.inventory.CategoryModel;
import com.dt.dao.DatabaseConnect;
import com.dt.inventory.SubCategoryDAO;
import com.dt.inventory.SubCategoryModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;

public class AddItemDialogController {

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
    private TableView<ItemMeasurement> measurementTable;

    private final ObservableList<ItemMeasurement> measurements =
            FXCollections.observableArrayList();

    private String imagePath;

    private ItemDAO itemDAO;

    private MeasurementDAO measurementDAO;

    @FXML
    public void initialize() {

        try {

            Connection connection =
                    DatabaseConnect.getConnection();

            itemDAO = new ItemDAO(connection);

            measurementDAO = new MeasurementDAO(connection);

            CategoryDAO categoryDAO =
                    new CategoryDAO(connection);

            SubCategoryDAO subCategoryDAO =
                    new SubCategoryDAO(connection);

            cbCategory.getItems().addAll(
                    categoryDAO.getAll()
            );

            cbSubCategory.getItems().addAll(
                    subCategoryDAO.getAll()
            );

            measurementTable.setItems(measurements);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // IMAGE

    @FXML
    private void onUploadImage() {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle("Choose Item Image");

        File file = chooser.showOpenDialog(null);

        if (file != null) {

            imagePath = file.getAbsolutePath();
        }
    }

    // ADD MEASUREMENT

    @FXML
    private void onAddMeasurement() {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle("Add Measurement");

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);

        TextField tfQty = new TextField();
        TextField tfUnit = new TextField();
        TextField tfSelling = new TextField();
        TextField tfPurchase = new TextField();
        TextField tfStock = new TextField();

        grid.add(new Label("Qty"), 0, 0);
        grid.add(tfQty, 1, 0);

        grid.add(new Label("Unit"), 0, 1);
        grid.add(tfUnit, 1, 1);

        grid.add(new Label("Selling"), 0, 2);
        grid.add(tfSelling, 1, 2);

        grid.add(new Label("Purchase"), 0, 3);
        grid.add(tfPurchase, 1, 3);

        grid.add(new Label("Stock"), 0, 4);
        grid.add(tfStock, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
        );

        dialog.showAndWait().ifPresent(result -> {

            if (result == ButtonType.OK) {

                measurements.add(

                        new ItemMeasurement(
                                0,
                                0,
                                Double.parseDouble(tfQty.getText()),
                                tfUnit.getText(),
                                Double.parseDouble(tfSelling.getText()),
                                Double.parseDouble(tfPurchase.getText()),
                                Double.parseDouble(tfStock.getText())
                        )
                );
            }
        });
    }

    // DELETE MEASUREMENT

    @FXML
    private void onDeleteMeasurement() {

        ItemMeasurement selected =
                measurementTable.getSelectionModel()
                        .getSelectedItem();

        if (selected != null) {

            measurements.remove(selected);
        }
    }

    // SAVE ITEM

    @FXML
    private void onSave() {

        try {

            ItemModel item =
                    new ItemModel(
                            0,
                            tfItemCode.getText(),
                            tfItemName.getText(),
                            cbCategory.getValue().getId(),
                            cbSubCategory.getValue() != null
                                    ? cbSubCategory.getValue().getId()
                                    : 0,
                            cbCategory.getValue().getCategoryName(),
                            cbSubCategory.getValue() != null
                                    ? cbSubCategory.getValue().getSubCategoryName()
                                    : "",
                            imagePath,
                            chkGST.isSelected(),
                            chkGST.isSelected()
                                    ? Double.parseDouble(tfGST.getText())
                                    : 0
                    );

            itemDAO.insert(item);

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setHeaderText("Item Saved Successfully");

            alert.showAndWait();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}


package com.dt.inventory;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;

public class EditItemDialogController {

    @FXML
    private TextField tfItemCode;

    @FXML
    private TextField tfItemName;

    @FXML
    private ComboBox<?> cbCategory;

    @FXML
    private ComboBox<?> cbSubCategory;

    @FXML
    private CheckBox chkGST;

    @FXML
    private TextField tfGST;

    @FXML
    private TableView<ItemMeasurement> measurementTable;

    private final ObservableList<ItemMeasurement> measurements =
            FXCollections.observableArrayList();

    private ItemModel currentItem;

    private String imagePath;

    public void setItem(ItemModel item) {

        this.currentItem = item;

        tfItemCode.setText(item.getItemCode());

        tfItemName.setText(item.getItemName());

        chkGST.setSelected(item.isGstEnabled());

        tfGST.setText(
                String.valueOf(
                        item.getGstPercentage()
                )
        );

        imagePath = item.getImagePath();
    }

    @FXML
    private void onUploadImage() {

        FileChooser chooser =
                new FileChooser();

        File file =
                chooser.showOpenDialog(null);

        if (file != null) {

            imagePath =
                    file.getAbsolutePath();
        }
    }

    @FXML
    private void onAddMeasurement() {

        openMeasurementDialog(null);
    }

    @FXML
    private void onEditMeasurement() {

        ItemMeasurement selected =
                measurementTable.getSelectionModel()
                        .getSelectedItem();

        if (selected != null) {

            openMeasurementDialog(selected);
        }
    }

    @FXML
    private void onDeleteMeasurement() {

        ItemMeasurement selected =
                measurementTable.getSelectionModel()
                        .getSelectedItem();

        if (selected != null) {

            measurements.remove(selected);
        }
    }

    private void openMeasurementDialog(ItemMeasurement measurement) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle("Measurement");

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);

        TextField tfQty = new TextField();
        TextField tfUnit = new TextField();
        TextField tfSelling = new TextField();
        TextField tfPurchase = new TextField();
        TextField tfStock = new TextField();

        if (measurement != null) {

            tfQty.setText(
                    String.valueOf(
                            measurement.getQuantity()
                    )
            );

            tfUnit.setText(
                    measurement.getUnit()
            );

            tfSelling.setText(
                    String.valueOf(
                            measurement.getSellingPrice()
                    )
            );

            tfPurchase.setText(
                    String.valueOf(
                            measurement.getPurchasePrice()
                    )
            );

            tfStock.setText(
                    String.valueOf(
                            measurement.getCurrentStock()
                    )
            );
        }

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

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.OK,
                        ButtonType.CANCEL
                );

        dialog.showAndWait().ifPresent(result -> {

            if (result == ButtonType.OK) {

                ItemMeasurement itemMeasurement =
                        new ItemMeasurement(
                                measurement != null
                                        ? measurement.getId()
                                        : 0,
                                currentItem.getId(),
                                Double.parseDouble(tfQty.getText()),
                                tfUnit.getText(),
                                Double.parseDouble(tfSelling.getText()),
                                Double.parseDouble(tfPurchase.getText()),
                                Double.parseDouble(tfStock.getText())
                        );

                if (measurement == null) {

                    measurements.add(itemMeasurement);

                } else {

                    int index =
                            measurements.indexOf(measurement);

                    measurements.set(index,
                            itemMeasurement);
                }
            }
        });
    }

    @FXML
    private void onUpdate() {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(
                "Item Updated Successfully"
        );

        alert.showAndWait();
    }
}
package com.dt.modern.model;


import com.dt.controller.ModernInvoiceController;
import com.dt.inventory.ItemMeasurement;
import com.dt.inventory.ItemModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.util.List;

public class ItemCardController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label lblItemName;

    @FXML
    private Label lblCategory;

    @FXML
    private ComboBox<ItemMeasurement> cbMeasurement;

    @FXML
    private TextField tfQty;

    @FXML
    private TextField tfPrice;

    @FXML
    private TextField tfGST;

    @FXML
    private Label lblStock;

    @FXML
    private Button btnEditPrice;

    @FXML
    private Button btnAdd;

    private ItemModel item;

    private List<ItemMeasurement> measurements;

    private ModernInvoiceController parent;

    public void setData(ItemModel item,
                        List<ItemMeasurement> measurements,
                        ModernInvoiceController parent) {

        this.item = item;

        this.measurements = measurements;

        this.parent = parent;

        lblItemName.setText(
                item.getItemName()
        );

        lblCategory.setText(
                item.getCategoryName()
        );

        tfGST.setText(
                String.valueOf(
                        item.getGstPercentage()
                )
        );

        // IMAGE

        try {

            if (item.getImagePath() != null) {

                Image image =
                        new Image(

                                new FileInputStream(
                                        item.getImagePath()
                                )
                        );

                imageView.setImage(image);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        // MEASUREMENTS

        cbMeasurement.getItems().addAll(
                measurements
        );

        if (!measurements.isEmpty()) {

            cbMeasurement.getSelectionModel()
                    .selectFirst();

            updateMeasurement();
        }

        cbMeasurement.setOnAction(e ->

                updateMeasurement()
        );

        // EDIT PRICE

        btnEditPrice.setOnAction(e -> {

            tfPrice.setEditable(
                    !tfPrice.isEditable()
            );
        });

        // ADD

        btnAdd.setOnAction(e ->

                addToCart()
        );
    }

    private void updateMeasurement() {

        ItemMeasurement measurement =

                cbMeasurement.getValue();

        if (measurement == null) {
            return;
        }

        tfPrice.setText(

                String.valueOf(
                        measurement.getSellingPrice()
                )
        );

        lblStock.setText(

                "Stock : "

                        +

                        measurement.getCurrentStock()

                        +

                        " "

                        +

                        measurement.getUnit()
        );
    }

    private void addToCart() {

        try {

            ItemMeasurement measurement =
                    cbMeasurement.getValue();

            if (measurement == null) {

                return;
            }

            double qty =

                    Double.parseDouble(
                            tfQty.getText()
                    );

            double price =

                    Double.parseDouble(
                            tfPrice.getText()
                    );

            double gst =

                    Double.parseDouble(
                            tfGST.getText()
                    );

            parent.addToCart(

                    item,

                    measurement,

                    qty,

                    price,

                    gst
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}

package com.dt.modern.model;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class CartCell extends ListCell<CartItem> {

    @Override
    protected void updateItem(CartItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        HBox root = new HBox(10);

        Label name = new Label(item.getItemName());
        Label qty = new Label("x" + item.getQty());
        Label price = new Label("₹ " + (item.getRate() * item.getQty()));

        root.getChildren().addAll(name, qty, price);
        root.getStyleClass().add("cart-cell");

        setGraphic(root);
    }
}

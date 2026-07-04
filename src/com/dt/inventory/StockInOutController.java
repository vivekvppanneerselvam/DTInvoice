package com.dt.inventory;


import javafx.fxml.FXML;
import javafx.scene.control.*;

public class StockInOutController {

    @FXML
    private ComboBox<String> cbMeasurement;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private TextField tfQuantity;

    @FXML
    private TextArea tfRemarks;

    @FXML
    public void initialize() {

        cbType.getItems().addAll(
                "STOCK_IN",
                "STOCK_OUT"
        );
    }

    @FXML
    private void onSave() {

        String type =
                cbType.getValue();

        double qty =
                Double.parseDouble(
                        tfQuantity.getText()
                );

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(
                "Stock Transaction Saved"
        );

        alert.setContentText(
                type + " : " + qty
        );

        alert.showAndWait();
    }
}

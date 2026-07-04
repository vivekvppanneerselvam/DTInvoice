package com.dt.style;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;

public class MetroTextFieldSkin extends StackPane {

    private final TextField textField;
    private final StackPane rightButton;
    private final Region rightButtonGraphic;

    public MetroTextFieldSkin() {
        textField = new TextField();

        rightButton = new StackPane();
        rightButton.getStyleClass().add("right-button");

        rightButtonGraphic = new Region();
        rightButtonGraphic.getStyleClass().add("right-button-graphic");

        rightButton.getChildren().add(rightButtonGraphic);

        getChildren().addAll(textField, rightButton);
        StackPane.setAlignment(rightButton, Pos.CENTER_RIGHT);

        setupListeners();
    }

    private void setupListeners() {

        // show button only when text exists
        textField.textProperty().addListener((obs, oldVal, newVal) ->
                rightButton.setVisible(newVal != null && !newVal.isEmpty())
        );

        // clear text on click
        rightButton.setOnMousePressed(e -> textField.clear());
    }

    public TextField getTextField() {
        return textField;
    }
}
package com.dt.inventory;




        import com.dt.dao.DatabaseConnect;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.fxml.FXML;
        import javafx.scene.control.*;
        import javafx.scene.layout.*;

        import java.sql.Connection;
        import java.util.List;
        import java.util.Optional;
        import java.util.stream.Collectors;

        public class CategoryController {

        @FXML
        private TextField tfSearch;

        @FXML
        private FlowPane categoryContainer;

        private final ObservableList<CategoryModel> categories =
        FXCollections.observableArrayList();

        private CategoryDAO dao;

        @FXML
        public void initialize() {

        try {

        Connection connection =
        DatabaseConnect.getConnection();

        dao = new CategoryDAO(connection);

        loadCategories();

        tfSearch.textProperty().addListener((obs,
        oldVal,
        newVal) -> {

        List<CategoryModel> filtered =
        categories.stream()

        .filter(c ->

        c.getCategoryName()
        .toLowerCase()
        .contains(newVal.toLowerCase())
        )

        .collect(Collectors.toList());

        renderCategories(filtered);
        });

        } catch (Exception e) {

        e.printStackTrace();
        }
        }

        // ------------------------------------
        // LOAD
        // ------------------------------------

        private void loadCategories()
        throws Exception {

        categories.clear();

        categories.addAll(
        dao.getAll()
        );

        renderCategories(categories);
        }

        // ------------------------------------
        // RENDER
        // ------------------------------------

        private void renderCategories(List<CategoryModel> list) {

        categoryContainer.getChildren().clear();

        for (CategoryModel category : list) {

        categoryContainer.getChildren().add(
        createCard(category)
        );
        }
        }

        // ------------------------------------
        // CARD
        // ------------------------------------

        private VBox createCard(CategoryModel category) {

        VBox card = new VBox(12);

        card.setPrefWidth(320);

        card.setStyle("""
                -fx-background-color: white;
                -fx-padding: 18;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #e5e7eb;
                """);

        Label name = new Label(
        category.getCategoryName()
        );

        name.setStyle("""
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                """);

        FlowPane actions = new FlowPane();

        actions.setHgap(10);

        Button btnEdit =
        new Button("Edit");

        Button btnDelete =
        new Button("Delete");

        // EDIT

        btnEdit.setOnAction(e -> {

        TextInputDialog dialog =
        new TextInputDialog(
        category.getCategoryName()
        );

        dialog.setHeaderText(
        "Update Category"
        );

        dialog.showAndWait().ifPresent(nameValue -> {

        try {

        category.setCategoryName(nameValue);

        dao.update(category);

        loadCategories();

        } catch (Exception ex) {

        ex.printStackTrace();
        }
        });
        });

        // DELETE

        btnDelete.setOnAction(e -> {

        Alert alert =
        new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText(
        "Delete Category?"
        );

        Optional<ButtonType> result =
        alert.showAndWait();

        if (result.isPresent()
        && result.get() == ButtonType.OK) {

        try {

        dao.delete(category.getId());

        loadCategories();

        } catch (Exception ex) {

        ex.printStackTrace();
        }
        }
        });

        actions.getChildren().addAll(
        btnEdit,
        btnDelete
        );

        card.getChildren().addAll(
        name,
        actions
        );

        return card;
        }

        // ------------------------------------
        // ADD CATEGORY
        // ------------------------------------

        @FXML
        private void onAddCategory() {

        TextInputDialog dialog =
        new TextInputDialog();

        dialog.setHeaderText(
        "Add Category"
        );

        dialog.setContentText(
        "Category Name"
        );

        dialog.showAndWait().ifPresent(name -> {

        try {

        dao.insert(
        new CategoryModel(
        0,
        name
        )
        );

        loadCategories();

        } catch (Exception e) {

        e.printStackTrace();
        }
        });
        }
        }



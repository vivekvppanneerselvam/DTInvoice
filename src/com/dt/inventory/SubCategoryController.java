package com.dt.inventory;



        import com.dt.inventory.CategoryDAO;
        import com.dt.inventory.CategoryModel;
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

        public class SubCategoryController {

        @FXML
        private TextField tfSearch;

        @FXML
        private FlowPane subCategoryContainer;

        private final ObservableList<SubCategoryModel> subCategories =
        FXCollections.observableArrayList();

        private SubCategoryDAO dao;

        private CategoryDAO categoryDAO;

        @FXML
        public void initialize() {

        try {

        Connection connection =
                DatabaseConnect.getConnection();

        dao = new SubCategoryDAO(connection);

        categoryDAO = new CategoryDAO(connection);

        loadSubCategories();

        tfSearch.textProperty().addListener((obs,
        oldVal,
        newVal) -> {

        List<SubCategoryModel> filtered =
        subCategories.stream()

        .filter(s ->

        s.getSubCategoryName()
        .toLowerCase()
        .contains(newVal.toLowerCase())
        )

        .collect(Collectors.toList());

        renderSubCategories(filtered);
        });

        } catch (Exception e) {

        e.printStackTrace();
        }
        }

        private void loadSubCategories()
        throws Exception {

        subCategories.clear();

        subCategories.addAll(
        dao.getAll()
        );

        renderSubCategories(subCategories);
        }

        private void renderSubCategories(List<SubCategoryModel> list) {

        subCategoryContainer.getChildren().clear();

        for (SubCategoryModel sub : list) {

        subCategoryContainer.getChildren().add(
        createCard(sub)
        );
        }
        }

        private VBox createCard(SubCategoryModel sub) {

        VBox card = new VBox(10);

        card.setPrefWidth(320);

        card.setStyle("""
                -fx-background-color: white;
                -fx-padding: 18;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #e5e7eb;
                """);

        Label name = new Label(
        sub.getSubCategoryName()
        );

        name.setStyle("""
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                """);

        Label category = new Label(
        "Category: " + sub.getCategoryName()
        );

        FlowPane actions = new FlowPane();

        actions.setHgap(10);

        Button btnEdit =
        new Button("Edit");

        Button btnDelete =
        new Button("Delete");

        btnEdit.setOnAction(e -> {

        TextInputDialog dialog =
        new TextInputDialog(
        sub.getSubCategoryName()
        );

        dialog.setHeaderText(
        "Update Sub Category"
        );

        dialog.showAndWait().ifPresent(value -> {

        try {

        sub.setSubCategoryName(value);

        dao.update(sub);

        loadSubCategories();

        } catch (Exception ex) {

        ex.printStackTrace();
        }
        });
        });

        btnDelete.setOnAction(e -> {

        Alert alert =
        new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText(
        "Delete Sub Category?"
        );

        Optional<ButtonType> result =
        alert.showAndWait();

        if (result.isPresent()
        && result.get() == ButtonType.OK) {

        try {

        dao.delete(sub.getId());

        loadSubCategories();

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
        category,
        actions
        );

        return card;
        }

        @FXML
        private void onAddSubCategory() {

        try {

        Dialog<ButtonType> dialog =
        new Dialog<>();

        dialog.setTitle("Add Sub Category");

        ComboBox<CategoryModel> cbCategory =
        new ComboBox<>();

        cbCategory.getItems().addAll(
        categoryDAO.getAll()
        );

        TextField tfName =
        new TextField();

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Category"), 0, 0);
        grid.add(cbCategory, 1, 0);

        grid.add(new Label("Sub Category"), 0, 1);
        grid.add(tfName, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane()
        .getButtonTypes()
        .addAll(ButtonType.OK,
        ButtonType.CANCEL);

        Optional<ButtonType> result =
        dialog.showAndWait();

        if (result.isPresent()
        && result.get() == ButtonType.OK) {

        dao.insert(

        new SubCategoryModel(

        0,

        cbCategory.getValue().getId(),

        cbCategory.getValue().getCategoryName(),

        tfName.getText()
        )
        );

        loadSubCategories();
        }

        } catch (Exception e) {

        e.printStackTrace();
        }
        }
        }



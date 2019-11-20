package com.dt.application;

import com.dt.dao.Database;
import com.dt.dto.FinancialYear;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.*;


public class DTInvoiceApplication extends Application {
	private final static Logger logger = Logger.getLogger(DTInvoiceApplication.class.getName());
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws  Exception {

		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/dt/view/Login.fxml"));
		Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getClassLoader().getResource("resources/JMetroLightTheme.css").toExternalForm());
		InputStream iconStream = getClass().getResourceAsStream("/resources/images/icon.png");
		Image icon = new Image(iconStream);
		primaryStage.getIcons().add(icon);			
		primaryStage.setTitle("DTInvoice");
		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	

}

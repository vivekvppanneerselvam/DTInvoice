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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.*;


public class DTInvoiceApplication extends Application {
	private final static Logger logger = Logger.getLogger(DTInvoiceApplication.class.getName());

	@Override
	public void init() throws Exception {
		super.init();
		initLogger();    
		initDB();
		String pathString =  Global.getAppDataPath() + File.separator+ Global.APP_NAME;
		
		/*
		 * String appDataPath = Global.getAppDataPath(); try {
		 * System.setProperty("derby.system.home", appDataPath); } catch (Exception e) {
		 * logger.logp(Level.SEVERE, Main.class.getName(), "init",
		 * "Error in setting the derby.system.home property", e); } if
		 * (Global.getUserPreferences().getAutoOpenLastOpenedYear()) { final
		 * FinancialYear year = Global.getLastOpenedFinancialYear(); if (year != null) {
		 * Database.openAsActiveYear(year); } }
		 */     
	}


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
		primaryStage.setMaximized(false);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	private static void initDB() {
		String pathString =  Global.getAppDataPath() + File.separator+ Global.APP_NAME+ File.separator;
		Path path = Paths.get(pathString);
		try {
			if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectories(path);
			}		
			File file = new File(DTInvoiceApplication.class.getClassLoader().getResource("resources/dtinvoice.db").getFile());
		System.out.println(pathString + file.getName());
			Files.copy(file.toPath(), (new File(pathString + file.getName())).toPath(),
					StandardCopyOption.REPLACE_EXISTING);
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Couldn't create the db directory", e);
			return;
		}		
                
	}
	private static void initLogger() {
		Path path = createLogFolder();
		if (path == null) {return;}
		String fileName = path.toAbsolutePath() + File.separator+ "log-%g.xml"; //g stands for generation number. Numbering starts from 0;
		FileHandler fileHandler = null;
		try {
			//specify file handler to create 5 rotating files, if required, of max 1 MB each.
			fileHandler = new FileHandler(fileName, 1024 * 1024, 5, true);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't create the log file handler's instance", e);
			return;
		}
		Logger rootLogger = Logger.getLogger("");
		rootLogger.addHandler(fileHandler);
	}

	private static Path createLogFolder() {
		String userHomeDir = null;
		Logger logger = Logger.getGlobal();
		try {
			userHomeDir = System.getenv("LOCALAPPDATA");
			if (userHomeDir == null) {
				userHomeDir = System.getenv("USERPROFILE");
			}
			if (userHomeDir == null) {
				userHomeDir = System.getenv("user.home");
			}
		} catch (SecurityException e) {
			logger.log(Level.SEVERE, "Couldn't get the user home diretory", e);
			return null;
		}
		if (userHomeDir == null) {
			userHomeDir = "/";
		}
		String pathString = userHomeDir + File.separator + "dtinvoice" + File.separator + "log";
		Path path = Paths.get(pathString);
		try {
			if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
				Files.createDirectories(path);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Couldn't create the log directory", e);
			return null;
		}
		return path;
	}



}

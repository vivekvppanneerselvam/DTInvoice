/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.controller;

import com.dt.application.Global;
import com.dt.dao.Database;
import com.dt.utils.TabContent;
import com.dt.utils.Utility;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

/**
 *
 * @author Dinesh
 */
public class HomeController {

    public Stage MainWindow = null;

    private final static Logger logger
            = Logger.getLogger(HomeController.class.getName());
    private final static String INVOICE_VIEW_FILE_NAME = "Invoice";

    @FXML
    private ToolBar toolbar;
    @FXML
    private TabPane tabPane;
    @FXML
    private Label lblCreateInvoice;
    @FXML
    private Label lblSearchInvoice;
    @FXML
    private Label lblReceivePayment;
    @FXML
    private Label lblSearchPayment;
    @FXML
    private Label lblCustomers;
    @FXML
    private Label lblCustomerAccount;
    @FXML
    private Label lblItems;
    @FXML
    private Label lblMeasurementUnits;
    @FXML
    private Label lblBackup;
    @FXML
    private MenuItem firmDetailsMenuItem;
    @FXML
    private MenuItem measurementUnitsMenuItem;
    @FXML
    private MenuItem itemsMenuItem;
    @FXML
    private MenuItem createInvoiceMenuItem;
    @FXML
    private MenuItem searchInvoiceMenuItem;
    @FXML
    private MenuItem paymentReceivedMenuItem;
    @FXML
    private MenuItem paymentSearchMenuItem;
    @FXML
    private MenuItem customersMenuItem;
    @FXML
    private MenuItem customerAccountMenuItem;
    @FXML
    private MenuItem backupDatabaseMenuItem;
    @FXML
    private MenuItem userPreferencesMenuItem;
    @FXML
    private MenuItem closeFinancialYearMenuItem;
    @FXML
    private MenuItem compactDatabaseMenuItem;

    
    public void initialize() {
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends Tab> observable,
                                Tab oldValue, Tab newValue) -> {
                    if (newValue != null) {
                        Platform.runLater(() -> {
                            Object object = newValue.getProperties().get("controller");
                            if (object != null) {
                                ((TabContent) object).putFocusOnNode();
                            }
                        });
                    }
                });

        toolbar.managedProperty().bind(toolbar.visibleProperty());

        final Label[] labels = new Label[]{
            lblCreateInvoice, lblSearchInvoice, lblReceivePayment, lblSearchPayment,
            lblCustomers, lblCustomerAccount, lblItems, 
            lblMeasurementUnits, lblBackup
        };

        for (Label label : labels) {
            label.disableProperty().bind(Global.activeYearProperty().isNull());
        }
        
        final MenuItem[] menuItems = new MenuItem[] {
            firmDetailsMenuItem, measurementUnitsMenuItem,
            itemsMenuItem, createInvoiceMenuItem, searchInvoiceMenuItem,
            paymentReceivedMenuItem, paymentSearchMenuItem,
            customersMenuItem, customerAccountMenuItem, backupDatabaseMenuItem,
            closeFinancialYearMenuItem, compactDatabaseMenuItem
        };
        
        for(MenuItem menuItem : menuItems) {
            menuItem.disableProperty().bind(Global.activeYearProperty().isNull());
        }
        
    }

    @FXML
    private void onExitCommand(ActionEvent event) {
      /* DO NOT USE MainWindow.close() */
       MainWindow.fireEvent(new WindowEvent(MainWindow,
               WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    private void onFirmDetailsCommand(ActionEvent event) {
        addTab("FirmDetails", "Firm Details");
    }

    @FXML
    private void onCustomersAction(ActionEvent event) {
        addTab("Customers", "Customers");
    }

    @FXML
    private void onMeasurementUnitsCommmand(ActionEvent event) {
        addTab("UnitsOfMeasurement", "Units of Measurement");
    }

    @FXML
    private void onItemsCommmand(ActionEvent event) {
        addTab("Items", "Items");
    }

    @FXML
    private void onCreateInvoiceAction(ActionEvent event) {
        addTab("Invoice", "New Invoice");
    }

    @FXML
    private void onSearchInvoiceAction(ActionEvent event) {
        addTab("InvoiceSearch", "Invoice Search");
    }

    @FXML
    private void onReceivePaymentAction(ActionEvent event) {
        addTab("Payment", "Receive Payment");
    }

    @FXML
    private void onSearchPaymentAction(ActionEvent event) {
        addTab("PaymentsSearch", "Payments Search");
    }

    @FXML
    private void onCustomerAccountAction(ActionEvent event) {
        addTab("CustomerAccountLookup", "Customer Account");
    }

    @FXML
    private void onApplicationDataLocationAction(ActionEvent event) {
        addTab("AppDataLocation", "Application Data Location");
    }

    @FXML
    private void onCreateFinancialYearAction(ActionEvent event) {
        addTab("CreateFinancialYear", "New Financial Year");
    }
    
    @FXML
    private void onOpenFinancialYearAction(ActionEvent event) {
        addTab("OpenFinancialYear", "Open Financial Year");
    }
    
    @FXML
    private void onDeleteFinancialYearAction(ActionEvent event) {
        addTab("DeleteFinancialYear", "Delete Financial Year");
    }

    @FXML
    private void onUserPreferencesAction(ActionEvent event) {
        addTab("UserPreferences", "User Preferences");
    }
    
    @FXML
    private void onCloseFinancialYearAction(ActionEvent event) {
         if (!closeAllTabs()) {
             return;
         }
         
         Database.shutDownActiveYearDatabase();
    }
    
    @FXML
    private void onBackupDatabaseAction(ActionEvent event) {
        addTab("Backup", "Database Backup");
    }
    
     @FXML
    private void onRestoreDatabaseAction(ActionEvent event) {
        addTab("RestoreDatabase", "Database Restore");
    }
    
     @FXML
    private void onAboutDialogAction(ActionEvent event) {
        addTab("About", "About");
    }

    private void addTab(final String fxmlFileName, final String title) {

        final String KEY = "fxml";

        /*Ensure that no second instance of a view other than that of Invoice
         view is instantiated*/
        if (!fxmlFileName.equalsIgnoreCase(INVOICE_VIEW_FILE_NAME)) { //view other than Invoice view
            ObservableList<Tab> tabs = tabPane.getTabs();
            for (Tab tabInstance : tabs) {
                if (tabInstance.getProperties().get(KEY).toString()
                        .equalsIgnoreCase(fxmlFileName)) { //view already instantiated
                    tabPane.getSelectionModel().select(tabInstance);
                    return;
                }
            }
        }

        final String viewPath = "/com/dt/view/" + fxmlFileName + ".fxml";

        FXMLLoader loader = new FXMLLoader();
        URL resource = this.getClass().getResource(viewPath);
        loader.setLocation(resource);
        Parent rootPane = null;
        try {
            rootPane = loader.load();
        } catch (IOException e) {
            logger.logp(Level.SEVERE, HomeController.class.getName(),
                    "addTab", "Error in loading the view file " + fxmlFileName, e);
            Utility.beep();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Occurred");
            alert.setHeaderText("Error in creating user interface");
            alert.setContentText("An error occurred in creating user interface "
                    + "for the selected command");
            alert.initOwner(MainWindow);
             Global.styleAlertDialog(alert);
            alert.showAndWait();
            return;
        }

       final TabContent controller = (TabContent) loader.getController();
        controller.setMainWindow(MainWindow);
        controller.setTabPane(tabPane);

        if (!controller.loadData()) {
            return;
        }

        Tab tab = new Tab();
        tab.getProperties().put("controller", controller);
        tab.getProperties().put(KEY, fxmlFileName);
        tab.setContent(rootPane);
        tab.setText(title);
        setContextMenu(tab);

        tab.setOnCloseRequest((Event event1) -> {
            if (!controller.shouldClose()) {
                event1.consume();
            }
        });
        
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        controller.putFocusOnNode();
    }

    private void setContextMenu(final Tab tab) {

        final MenuItem closeTabItem = new MenuItem("Close Tab");
        final MenuItem closeOtherTabsItem = new MenuItem("Close Other Tabs");
        final MenuItem closeAllTabsItem = new MenuItem("Close All Tabs");

        final ContextMenu contextMenu = new ContextMenu(closeTabItem, closeOtherTabsItem,
                closeAllTabsItem);

        setCloseTabAction(tab, closeTabItem);
        setCloseOtherTabsAction(tab, closeOtherTabsItem);
        setCloseAllTabsAction(tab, closeAllTabsItem);

        tab.setContextMenu(contextMenu);
    }

    private void setCloseAllTabsAction(final Tab tab, final MenuItem menuItem) {
        final EventHandler<ActionEvent> eventHandler = (ActionEvent event) -> {
            closeAllTabs();
        };

        menuItem.setOnAction(eventHandler);
    }

    private void setCloseOtherTabsAction(final Tab tab, final MenuItem menuItem) {
        final EventHandler<ActionEvent> eventHandler = (ActionEvent event) -> {
            final TabPane tabPane = tab.getTabPane();
            Global.closeTabs(tabPane, tab);
        };

        menuItem.setOnAction(eventHandler);
    }

    private void setCloseTabAction(final Tab tab, final MenuItem menuItem) {

        final EventHandler<ActionEvent> eventHandler = (ActionEvent event) -> {
            final TabPane tabPane = tab.getTabPane();
            tabPane.getSelectionModel().select(tab);
            TabContent controller = (TabContent) tab.getProperties().get("controller");
            if (controller.shouldClose()) {
                tabPane.getTabs().remove(tab);
            }
        };

        menuItem.setOnAction(eventHandler);
    }

    public boolean closeAllTabs() {
        final ObservableList<Tab> tabs = tabPane.getTabs();
        final List<Tab> tabsToRemove = new ArrayList<>(tabs.size());

        for (Tab tabControl : tabs) {
            tabPane.getSelectionModel().select(tabControl);
            TabContent controller = (TabContent) tabControl.getProperties().get("controller");
            if (!controller.shouldClose()) {
                return false;
            } else {
                tabsToRemove.add(tabControl); //mark this tab to be removed
            }
        }

        tabs.removeAll(tabsToRemove); //actually remove the tags here
        return true;
    }

    @FXML
    private void onHideToolbarAction(ActionEvent event) {
        CheckMenuItem menuItem = (CheckMenuItem) event.getSource();
        toolbar.setVisible(!menuItem.isSelected());
    }
    
    @FXML
    private void onCreateInvoiceCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
             createInvoiceMenuItem.fire();
         }
    }
    
    @FXML
    private void onSearchInvoicesCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
             searchInvoiceMenuItem.fire();
         }
    }
    
    @FXML
    private void onReceivePaymentCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
             paymentReceivedMenuItem.fire();
         }
    }
    
    @FXML
    private void onSearchPaymentsCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
             paymentSearchMenuItem.fire();
         }
    }
    
   @FXML
    private void onCustomersCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
            customersMenuItem.fire();
         }
    }
    
    @FXML
    private void onCustomerAccountCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
            customerAccountMenuItem.fire();
         }
    }
    
   @FXML
    private void onItemsCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
            itemsMenuItem.fire();
         }
    }
    
    @FXML
    private void onMeasurementUnitsCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
            measurementUnitsMenuItem.fire();
         }
    }
    
   @FXML
    private void onUserSettingsCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
            userPreferencesMenuItem.fire();
         }
    }
    
    @FXML
    private void onBackupCommand(MouseEvent event) {
         if (event.getButton() == MouseButton.PRIMARY && 
                 event.getClickCount() == 1) {
            backupDatabaseMenuItem.fire();
         }
    }
    
    @FXML
    private void onCompactDatabaseAction(final ActionEvent event) {
        MainWindow.getScene().setCursor(Cursor.WAIT);
        Platform.runLater(() -> {
            compactDatabase();
        });
    }
    
    private void compactDatabase() {
         boolean isSuccess = Database.compactDatabase();
          MainWindow.getScene().setCursor(Cursor.DEFAULT);
        
        if (isSuccess) {
            final String message = "The database was successfully compacted !!";
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, message, 
                    ButtonType.OK);
            alert.initOwner(MainWindow);
            alert.setTitle("Success");
            alert.setHeaderText("Database Successfully Compacted !!!");
             Global.styleAlertDialog(alert);
            alert.showAndWait();
        } else {
            final String message = "An error occurred while compacting the database." + 
                    "\nThe operation was aborted.";
            final Alert alert = new Alert(Alert.AlertType.ERROR, message,
                    ButtonType.OK);
            alert.initOwner(MainWindow);
            alert.setTitle("Compacting Unsuccessful");
            alert.setHeaderText("Database Compaction Failed !");
            Utility.beep();
             Global.styleAlertDialog(alert);
            alert.showAndWait();
        }
        
    }
    
}

package tabControllers;

/**
 * Tab 2 document controller.
 * Handles actions on inputs. Main functions
 * are displaying graphs, searches, accordion.
 */

import coinClasses.CoinHistory;
import coinClasses.CoinRankApi;
import coinClasses.ConnectToDatabase;
import coinClasses.ParseCoinName;
import coinClasses.SingleCoin;
import coinTrack.FXMLDocumentController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Kyle
 */
public class Tab2Controller implements Initializable{
    private CoinHistory coinHistory;
    private LinkedHashMap<String, Integer> historyMap;
    private LinkedHashMap<Double, String> singleHistoryMap;
    private int coinsToGraph = 25;
    private boolean isSingleCoinScan;
    private CoinRankApi coinList;
    private String timeSelection;
    private int tabSelection;
    private int pieChartCoins;
    private final ObservableList<String> TIMES = FXCollections.
            observableArrayList("24h", "7d", "30d", "1y", "5y");
    private final ObservableList<String> NUMCOINS = FXCollections.
            observableArrayList("3", "5", "7", "15", "25", "35", "50");
    private final ObservableList<String> BUBBLES = FXCollections.
            observableArrayList("24h", "7d", "30d", "1y", "5y");
    private static Stage mainPage2;
    private String uname;
    private LinkedList<String> onlineUsers;
    private LinkedList<String> friendList;

    protected Scene scene;
    @FXML protected TextField usernamePhone;
    @FXML protected PasswordField txtPassword;
    @FXML protected Label lblStatus;
    @FXML private TabPane graphTabPane;
    @FXML private Tab barChartTab;
    @FXML private Tab pieChartTab;

    // Accordion
    @FXML private ListView onlineUsersListT2;
    @FXML private ListView savedCoinsListT2;
    @FXML private ListView friendsListT2;
    @FXML private ContextMenu cm;

    // Bottom portion Tab 2
    @FXML private SplitMenuButton splitMenuT2;
    @FXML private TextField searchFieldT2;
    @FXML private SplitMenuButton numCoins;
    @FXML private TextArea txtAreaT2;
    @FXML private Spinner<String> spinnerT2;
    @FXML private ComboBox<String> comboBox;
    @FXML private Button scanBtnT2;
    @FXML private Button searchBtnT2;
    @FXML private Text messageText;

    // Bar Chart
    @FXML private BarChart barChart;
    private XYChart.Series series1;
    private XYChart.Series series4;
    private ObservableList<XYChart.Series<String, Number>> barChartData;
    private ObservableList<XYChart.Series<String, Number>> barChartData2;

    // Pie Chart
    @FXML
    protected PieChart pieChart;
    protected ObservableList<PieChart.Data> pieChartData;

    /**
     * This handles the scan button on tab 2.
     * It first determines which graphTab is selected
     * then clears the existing data if any.
     * @param event
     */
    @FXML
    private void handleScanT2(ActionEvent event) {
        if (graphTabPane.getSelectionModel().getSelectedItem() == barChartTab) {
            System.out.println("bar chart selected");
            barChart.getData().clear();
            barChart.layout();
            // Get the comboBox entry
            boolean isComboBoxEmpty = comboBox.getSelectionModel().isEmpty();
            if (!isComboBoxEmpty) {
                timeSelection = (String)comboBox.getValue();
            } else {
                timeSelection = "7d";
            }
            // Make a CoinHistory api call
            coinHistory = new CoinHistory();
            displayMultiCoinGraph();

        } else if (graphTabPane.getSelectionModel().getSelectedItem() == pieChartTab) {
            System.out.println("pie chart selected");
            pieChart.getData().clear();
            pieChart.layout();
            coinList = new CoinRankApi();
            coinList.run();
            displayPieChart();
        } else {
            System.out.println("bubble chart selected");
        }

    }

    /**
     * Handles the search button on tab 2.
     * This button is not applicable on the pieChartTab
     * @param event
     */
    @FXML
    private void handleSearchT2(ActionEvent event) {
        if (graphTabPane.getSelectionModel().getSelectedItem() == barChartTab) {
            System.out.println("bar chart selected");
            barChart.getData().clear();
            barChart.layout();
            barChartData.clear();
            series1.getData().clear();
            barChartData2.clear();
            series4.getData().clear();
            // timeSelection
            boolean isComboBoxEmpty = comboBox.getSelectionModel().isEmpty();
            if (isComboBoxEmpty) {
                // Default timeframe will be 7 days.
                timeSelection = "7d";
            } else {
                timeSelection = (String)comboBox.getValue();
            }
            // Call method to search for coin and display its graph.
            displaySingleCoinGraph();
        } else if (graphTabPane.getSelectionModel().getSelectedItem() == pieChartTab) {
            System.out.println("pie chart selected");
        } else {
            System.out.println("bubble chart selected");
        }
    }

    /**
     * Clears data from all graphs.
     *
     * This DOES NOT work 100%. New graphs spawn on top of old graphs.
     *
     * @param event
     */
    @FXML
    private void handleClearT2(ActionEvent event) {
        barChart.getData().clear();
        barChart.layout();
        pieChart.getData().clear();
        pieChart.layout();
        searchFieldT2.setText("");
        barChartData2.clear();
        series4.getData().clear();
    }
 
    /**
     * Test button, might add it permanently.
     * Shows the avaliable list of coins you can search for.
     * @param event
     */
    @FXML
    private void handleTest(ActionEvent event) {
        File coinFile = new File("src\\coinClasses\\coinNamesIds.txt");
        System.out.println(coinFile.toString());
        try {
            Scanner scanner = new Scanner(coinFile);
            String lines = "";
            // Scan the file
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                lines += line + "\n";
            }
            txtAreaT2.setText(lines);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseCoinName.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Changes depending on what graph tab is open.
     * @param event
     */
    @FXML
    private void handleComboBox(ActionEvent event) {
        timeSelection = comboBox.getValue();
    }

    /**
     * Handle log out button click.
     * Takes you back to log in screen.
     * @param event
     */
    @FXML
    private void handleLogOutT2(ActionEvent event) {
        System.out.println("logging out");
        Parent root;
            try {
                Tab2Controller.mainPage2 = new Stage();
                setOnlineStatus(coinTrack.FXMLDocumentController.uname, 0);
                root = FXMLLoader.load(getClass().getClassLoader().getResource("coinTrack/FXMLLogin.fxml"));
                this.scene = new Scene(root);
                Tab2Controller.mainPage2.setScene(this.scene);
                Tab2Controller.mainPage2.show();
                closeOldStage();
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    /**
     * Change a users online status. i.e. when they log off .
     * @param _uname
     * @param _status
     */
    private void setOnlineStatus(String _uname, int _status) {
        ConnectToDatabase conn = new ConnectToDatabase();
        conn.setUserOnlineStatus(_uname, _status);
        conn.close();
    }

    /**
     * Display historical data for a single coin
     */
    private void displaySingleCoinGraph() {
        System.out.println("displaying graph");
        // Get the string/int from the text field.
        if (searchFieldT2.getText().isEmpty()){
            searchFieldT2.setText("Coin name or id");
        } else {
            char ch = searchFieldT2.getText().charAt(0);
            if (Character.isAlphabetic(ch)){
                CoinHistory coinHist = new CoinHistory(0, searchFieldT2.getText(), timeSelection);
                singleHistoryMap = coinHist.getSingleHistory();
            } else {
                int temp = Integer.parseInt(searchFieldT2.getText());
                CoinHistory coinHist = new CoinHistory(temp, "", timeSelection);
                singleHistoryMap = coinHist.getSingleHistory();
            }
        }
        // Prevent old data from showing back up  --  NOT WORKING
        barChartData2.clear();
        series4.getData().clear();
        // Add entries from singleHistoryMap into series1
        for (Map.Entry<Double, String> entry : singleHistoryMap.entrySet()) {
            long tempLong = Long.parseLong(entry.getValue());
            Date d = new Date(tempLong);
            String date = "" + d;
            series4.getData().add(new XYChart.Data(
                    date, entry.getKey()));
        }
        // Add series1 to the barChartData, then add that to the barChart
        barChart.setTitle("Viewing the past " + timeSelection);
        barChartData2.add(series4);
        barChart.setData(barChartData2);

        /**
         * THIS IS FOR TESTING.
         * THIS IS NOT MY CODE.
         *
         * Implements scrolling using the mouse wheel on the graph.
         */
        final double SCALE_DELTA = 1.1;
        barChart.setOnScroll(new EventHandler<ScrollEvent>() {
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }
                // Keep the scale ratio as you zoom in/out
                double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
                barChart.setScaleX(barChart.getScaleX() * scaleFactor);
                barChart.setScaleY(barChart.getScaleY() * scaleFactor);
            }
        });

        barChart.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    barChart.setScaleX(1.0);
                    barChart.setScaleY(1.0);
                }
            }
        });
    }

    /**
     * Display / Rank all coin prices
     */
    private void displayMultiCoinGraph() {
        System.out.println("displaying graph");
//        coinHistory.join();
        historyMap = coinHistory.getPriceDate();
        int count = 0;
        historyMap.entrySet().forEach((entry) -> {
            series1.getData().add(new XYChart.Data(
                    entry.getKey(), entry.getValue()));
        });
        barChartData.add(series1);
        barChart.setData(barChartData);
    }

    /**
     * Creates a LinkedList of SingleCoins
     */
    private void displayPieChart() {
        // Make sure the thread is finished
        coinList.join();
        LinkedList<SingleCoin> temp = coinList.getSortedCoinList();
        pieChartCoins = Integer.parseInt(comboBox.getValue());
        // Loops over SingleCoin list and adds data to pieChart
        for (int i = 0; i <= pieChartCoins-1; i++) {
            SingleCoin coin = temp.get(i);
            double price = Double.parseDouble(coin.getPrice());
            // Allow 5 decimal places
            double rounded = (double)Math.round(price * 100000d) / 100000d;
            pieChartData.add(new PieChart.Data(coin.getName(), rounded));
        }
        pieChart.setData(pieChartData);
    }

    /**
     * Call database returning a list of all users who are online.
     */
    private void addOnlineUsersToList() {
        ConnectToDatabase conn = new ConnectToDatabase();
        this.onlineUsers = conn.getOnlineUsers();
        conn.close();
        for (int i = 0; i < this.onlineUsers.size(); i++) {
            onlineUsersListT2.getItems().add(this.onlineUsers.get(i));
        }
    }

    /**
     * This creates the right click menu on the onlineUsers list. 
     * It also maps each button to an action.
     */
    private void createListCells() {
        onlineUsersListT2.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem addFriendItem = new MenuItem();
            addFriendItem.textProperty().bind(Bindings.format("Add Friend"));
            addFriendItem.setOnAction(event -> {
                ConnectToDatabase conn = new ConnectToDatabase();
                String friendName = cell.getItem();
                if (friendName.equals(this.uname)) {
                    txtAreaT2.setText("Wow, so lonely. Can't add yourself as a friend..");
                } else {
                    conn.addFriend(this.uname, friendName);
                    txtAreaT2.setText("Added " + friendName + " to friend list!");
                }
                conn.close();
            });
            MenuItem sendMessageItem = new MenuItem();
            sendMessageItem.textProperty().bind(Bindings.format("Send Message"));
            sendMessageItem.setOnAction(event -> {
                // Send a message to a friend
            });
            contextMenu.getItems().addAll(addFriendItem, sendMessageItem);
            cell.textProperty().bind(cell.itemProperty());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        });
    }

    /**
     * Call database returning a list of friends.
     */
    private void addFriendsToList() {
        ConnectToDatabase conn = new ConnectToDatabase();
        this.friendList = conn.getFriendList(this.uname);
        conn.close();
        for (int i = 0; i < this.friendList.size(); i++) {
            friendsListT2.getItems().add(this.friendList.get(i));
        }
    }

    private void createFriendListCells() {
        friendsListT2.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem addFriendItem = new MenuItem();
            addFriendItem.textProperty().bind(Bindings.format("Share coin"));
            addFriendItem.setOnAction(event -> {
                ConnectToDatabase conn = new ConnectToDatabase();
                String friendName = cell.getItem();
                // Do stuff
                conn.close();
            });
            MenuItem sendMessageItem = new MenuItem();
            sendMessageItem.textProperty().bind(Bindings.format("Send Message"));
            sendMessageItem.setOnAction(event -> {
                // Send a message to a friend
            });
            MenuItem removeFriendItem = new MenuItem();
            sendMessageItem.textProperty().bind(Bindings.format("Remove Friend"));
            sendMessageItem.setOnAction(event -> {
                // Send a message to a friend
            });
            contextMenu.getItems().addAll(addFriendItem, sendMessageItem, removeFriendItem);
            cell.textProperty().bind(cell.itemProperty());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        });
    }
 
    /**
     * Returns and closes the main stage from the class where it was created
     */
    private void closeOldStage() {
        coinTrack.FXMLDocumentController.mainStage.close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String uname = coinTrack.FXMLDocumentController.uname;
        messageText.setText("Hello " + uname);
        this.uname = coinTrack.FXMLDocumentController.uname;
        messageText.setText("Hello " + uname);
        createListCells();
        addOnlineUsersToList();
        addFriendsToList();
        onlineUsersListT2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    cm.show(onlineUsersListT2, event.getScreenX(), event.getScreenY());
                }
            }

        });
        // Initialize the barChart arrays they will pull data from.
        barChartData = FXCollections.observableArrayList();
        barChartData2 = FXCollections.observableArrayList();
        pieChartData = FXCollections.observableArrayList();
        pieChart.setTitle("Coin Prices");
        series1 = new BarChart.Series<>();
        series4 = new BarChart.Series<>();
        series1.setName("Data");
        series4.setName("Prices");
        // Tab listener. Detects which graph tab is selected
        graphTabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab oldTab, Tab currentTab) {
                System.out.println("Tab Change");
                if (currentTab == barChartTab) {
                    comboBox.setValue("Timeframe");
                    tabSelection = 1;
                    scanBtnT2.setDisable(true);
                    searchBtnT2.setDisable(false);
                    comboBox.setItems(TIMES);
                } else if (currentTab == pieChartTab) {
                    comboBox.setValue("Number of coins");
                    tabSelection = 2;
                    searchBtnT2.setDisable(true);
                    scanBtnT2.setDisable(false);
                    comboBox.setItems(NUMCOINS);
                } else {
                    comboBox.setValue("Bubbles");
                    tabSelection = 3;
                    comboBox.setItems(BUBBLES);
                }
            }
        });
        // Set the default comboBox values
        comboBox.setValue("Timeframe");
        comboBox.setItems(TIMES);
        scanBtnT2.setDisable(true);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tabControllers.assistantControllers.graphs;

import coinClasses.UserCoin;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 *
 * @author Kyle
 */
public class BarChartClass implements interfaces.GraphInterface, interfaces.BarChartInterface{
    
    private BarChart barChart;
    private LinkedList<LinkedHashMap<Double, String>> linkedMap;
    private int numCoins;
    private LinkedList<UserCoin> userCoinList;
    private LinkedList<String> colors;
    private LinkedList<XYChart.Series> serLst;
    private XYChart.Series series;
    private LinkedHashMap<Double, String> singleHistoryMap;
    private String timeSelection;
    
    /**
     * Constructor for bar chart used in Tab 2.
     *
     * @param _barChart, the bar chart object
     * @param _linkedMap, linkedList of hash maps. Each linkedHashMap represents
     * a single coin that will be displayed using its prices (double) and dates (String).
     * @param _numCoins, the number of coins to be displayed
     * @param _userCoinList
     */
    public BarChartClass(BarChart _barChart, LinkedList<LinkedHashMap<Double, String>> _linkedMap,
            int _numCoins, LinkedList<UserCoin> _userCoinList) {
        this.barChart = _barChart;
        this.linkedMap = _linkedMap;
        this.numCoins = _numCoins;
        this.userCoinList = _userCoinList;
        this.barChart.getData().clear();
        multiCoinData();
    }
    
    /**
     * Constructor for bar chart used in Tab 3, "dashboard".
     * @param _barChartData
     * @param _series
     * @param _singleHistoryMap
     * @param _timeSelection
     * @param _bc
     */
    public BarChartClass(XYChart.Series _series, LinkedHashMap<Double, String> _singleHistoryMap,
                            String _timeSelection, BarChart _bc) {
        this.series = _series;
        this.singleHistoryMap = _singleHistoryMap;
        this.timeSelection = _timeSelection;
        this.barChart = _bc;
        this.barChart.getData().clear();
        singleCoinData();
    }

    /**
     * Display multi coin graph.
     */
    @Override
    public void displayGraph() {
        this.barChart.getData().addAll(this.serLst);
    }

    /**
     * Display single coin graph.
     */
    public void displaySingleGraph() {
//        this.barChartData.add(this.series);
//        this.barChart.setData(this.barChartData);
        this.barChart.getData().add(this.series);
        alternateColors("green", "red");
        scaleGraph();
    }

    /**
     * Create data series for the single coin bar chart.
     * Used on Tab 2.
     */
    private void singleCoinData() {
        this.series.getData().clear();
        // Add entries from singleHistoryMap into series1
        for (Map.Entry<Double, String> entry : this.singleHistoryMap.entrySet()) {
            long tempLong = Long.parseLong(entry.getValue());
            Date d = new Date(tempLong);
            String date = "" + d;
            double price = entry.getKey();
            this.series.getData().add(new XYChart.Data(date, price));
        }
        // Add series1 to the barChartData, then add that to the barChart
        this.barChart.setTitle("Viewing the past " + this.timeSelection);
    }

    /**
     * Create data series for the multi coin bar chart. 
     * Used on Tab 3, "dashboard".
     */
    private void multiCoinData() {
        this.serLst = new LinkedList<>();
        this.colors = new LinkedList<>();
        int len = this.linkedMap.size();
        int iter = 0;
        for (int i = 0; i < this.linkedMap.size(); i++){
            XYChart.Series series = new XYChart.Series();
            int count = 0;
            for (Map.Entry<Double, String> entry : this.linkedMap.get(i).entrySet()) {
                if (count > this.numCoins){break;}
                long tempLong = Long.parseLong(entry.getValue());
                Date d = new Date(tempLong);
                String date = "" + d;
                double price = entry.getKey();
                series.getData().add(new XYChart.Data("", price)); // date
                count++;
            }
            series.setName(this.userCoinList.get(iter).getName());
            iter++;
            this.serLst.add(series);
            // Create random colors for each bar
            Random rand = new Random();
            this.colors.add(String.format("#%06x", rand.nextInt(0xffffff + 1)));
        }
    }

    @Override
    public void colorGraph() {
        this.linkedMap.forEach((item) -> {
            int count = 0;
            for (Map.Entry<Double, String> entry : item.entrySet()) {
                if (count >= this.numCoins) {break;}
                double price = entry.getKey();
                Node n = this.barChart.lookup(".data" + count + ".chart-bar");
                n.setStyle("-fx-bar-fill: " + colors.get(count));
                count++;
            }
        });
    }

    @Override
    public void alternateColors(String _upColor, String _downColor) {
        double lastPrice = 0;
        int count = 0;
        // A way to color the bars in the bargraph green or red.
        for (Map.Entry<Double, String> entry : this.singleHistoryMap.entrySet()) {
            double price = entry.getKey();
            if (count < this.singleHistoryMap.size()){
                if (price > lastPrice) {
                    Node n = this.barChart.lookup(".data" + count + ".chart-bar");
                    n.setStyle("-fx-bar-fill: " + _upColor);
                } else {
                    Node n = this.barChart.lookup(".data" + count + ".chart-bar");
                    n.setStyle("-fx-bar-fill: " + _downColor);
                }
            }
        }
    }

    /**
    * Implements scrolling using the mouse wheel on the graph.
    */
    @Override
    public void scaleGraph() {
        double delta = 1.1;
        this.barChart.setOnScroll((ScrollEvent event) -> {
            event.consume();
            if (event.getDeltaY() == 0) {
                return;
            }
            // Keep the scale ratio as you zoom in/out
            double factor = (event.getDeltaY() > 0) ? delta : 1 / delta;
            barChart.setScaleX(barChart.getScaleX() * factor);
            barChart.setScaleY(barChart.getScaleY() * factor);
        });
        this.barChart.setOnMousePressed((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                barChart.setScaleX(1.0);
                barChart.setScaleY(1.0);
            }
        });
    }

    @Override
    public LinkedList<String> getElements() {
        return null;
    }
    
}
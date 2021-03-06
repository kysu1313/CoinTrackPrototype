package models;
/**
 * This class accesses the Coin History api.
 * The main function of the class is to
 * loop through the url's in the api call
 * to retrieve historical data on each coin.
 *
 * - Kyle
 */

import controllers.AlertMessages;
import interfaces.CoinHistoryInterface;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public final class CoinHistory implements Runnable, CoinHistoryInterface{

    private LinkedList<SingleCoin> history;
    private LinkedHashMap<String, Integer> historyMap;
    private LinkedHashMap<Double, String> singleHistoryMap;
    private ConnectToApi response;
    private static final int NUM_COINS = 70;
    private boolean isBadTimeframe;
    private double change;
    private Thread t;
    private SingleCoin single;
    private int coinId;
    private HashMap<Integer, LinkedHashMap<Double, String>> allCoins;
    private String time;
    private final boolean DEBUG = controllers.Tab1Controller.DEBUG;

    public CoinHistory() {
        start();
    }

    /**
     * This constructor makes a single api call using
     * the provided coin id.
     * @param _id
     * @param _name
     * @param _timeFrame
     */
    public CoinHistory(int _id, String _name, String _timeFrame) {
        this.singleHistoryMap = new LinkedHashMap<>();
        this.coinId = _id;
        this.time = _timeFrame;
        if (!_name.equals("")){
            ParseCoinName newName = new ParseCoinName(_name);
            coinId = newName.getId();
        }
        String url = "https://coinranking1.p.rapidapi.com/coin/" + coinId + "/history/" + time + "";
        // Call API connector class
        this.response = new ConnectToApi(url,
                "coinranking1.p.rapidapi.com",
                "310c3610fcmsheb7636d5c15a024p1a11dajsnf459d4f82cfc");
    }

    public void getData() {
        if (this.response.getStatus() && this.response.getJsonObject().has("data")){
            this.isBadTimeframe = false;
            if (DEBUG){System.out.println(this.response.getJsonObject().toString());}
            JSONObject resp = response.getJsonObject();
            JSONObject data = resp.getJSONObject("data");
            // If the coin does not have data for given timeframe, break.
            if (data.has("change") && !data.isNull("change")) {
                this.isBadTimeframe = false;
                this.change = data.getDouble("change");
                // JSONArray containing price history for the particular coin.
                JSONArray histArr = data.getJSONArray("history");
                // Create the individual SingleCoinHistory object
                // Pass the JSONArray and the id of the coin.
                this.single = new SingleCoin(histArr, this.coinId);
                // Loop through the response adding data to a hashMap
                for (int i = 0; i < histArr.length(); i++) {
                    JSONObject temp = histArr.getJSONObject(i);
                    Double price = temp.getDouble("price");
                    String date = "" + temp.getInt("timestamp");
                    this.singleHistoryMap.put(price, date);
                }
            } else {
                this.change = 0.0;
                this.isBadTimeframe = true;
                AlertMessages.showErrorMessage("Large Timeframe", "Unfortunately this cryptocurrency does not have data going back " + time);
            }
        }
    }

    @Override
    public void run() {
        this.history = new LinkedList<>();
        if (this.DEBUG){System.out.println("Calling coinRank history api");}
        // Increment the coin "id" for each call. Not sure how many coins there are though.
        for (int i = 1; i < NUM_COINS; i++) {
            String url = "https://coinranking1.p.rapidapi.com/coin/" + i + "/history/7d";
            // For each response, extract body, data, and change
            JSONObject resp = new ConnectToApi(url, 
                    "coinranking1.p.rapidapi.com",
                    "310c3610fcmsheb7636d5c15a024p1a11dajsnf459d4f82cfc").getJsonObject();
            JSONObject data = resp.getJSONObject("data");
            this.change = data.getDouble("change");
            // JSONArray containing price history for the particular coin.
            JSONArray histArr = data.getJSONArray("history");
            // This creates the individual SingleCoinHistory objects
            // Pass the JSONArray and the id of the coin.
            SingleCoin single = new SingleCoin(histArr, i);
            // Pause api calls to prevent timeouts.
            this.history.add(single);
            try {
                TimeUnit.SECONDS.sleep((long) 0.03);
            } catch (InterruptedException ex) {
                Logger.getLogger(CoinHistory.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Create a new thread if the thread
     * is not already started.
     */
    public void start() {
        if (DEBUG){System.out.println("Starting Thread");}
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    /**
     * Wait for the thread to complete before
     * calling the methods for data.
     */
    public void join() {
        try {
            t.join();
            if (DEBUG){System.out.println("Join Thread");}
        } catch (InterruptedException ex) {
            Logger.getLogger(CoinRankApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Check for valid price data from api response.
     * @param response
     * @return
     */
    public boolean checkValidData() {
        if (this.response.getStatus() && this.response.getJsonObject().has("data")){
            this.isBadTimeframe = false;
            if (DEBUG){System.out.println(this.response.getJsonObject().toString());}
            JSONObject resp = response.getJsonObject();
            JSONObject data = new JSONObject();
            data = resp.getJSONObject("data");
            // If the coin does not have data for given timeframe, break.
            if (data.has("change") && !data.isNull("change")) {
                return true;
            }
        }
        return false;
    }

    // ========== GETTERS ==========

    /**
     * Check if the thread is alive
     * @return
     */
    public boolean isAlive() {
        return t.isAlive();
    }

    public int getID() {
        return this.coinId;
    }

    public LinkedHashMap<Double, String> getSingleHistory() {
        return this.singleHistoryMap;
    }

    public boolean getIsBadTimeframe() {
        return this.isBadTimeframe;
    }

    @Override
    public LinkedHashMap<String, Integer> getPriceDate() {
        return this.historyMap;
    }

    @Override
    public double getChange() {
        return this.change;
    }

    @Override
    public LinkedList<SingleCoin> getHistory() {
        return this.history;
    }
}

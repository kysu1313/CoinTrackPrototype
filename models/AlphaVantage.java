package models;

/**
 * This class calls the Alpha Vantage API and stores the data using the
 * AlphaSingleCoin.java class.
 * @author Kyle
 */

import com.BarData;
import interfaces.DailyWeeklyInterface;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
//import org.jfree.data.xy.OHLCDataItem;
import org.json.JSONObject;


public class AlphaVantage implements DailyWeeklyInterface{

    private final boolean DEBUG = controllers.Tab1Controller.DEBUG;
    private final String ENDPOINT = "alpha-vantage.p.rapidapi.com";
    private final String KEY = "81757143b0msh4237319349ebdffp1da0c0jsn4cc7c80702be";
    private String symbol;
    private LinkedList<LinkedHashMap<String, String>> dailySeries;
    private LinkedList<LinkedHashMap<String, String>> weeklySeries;
    private List<BarData> barData;

    /**
     * Constructor, saves symbol of coin for API call.
     * @param _symbol
     */
    public AlphaVantage(String _symbol) {
        this.symbol = _symbol;
        if (DEBUG) {System.out.println("Connecting to Alpha Vantage API...");}
    }

    /**
     * Returns linked list of daily (open, high, low, and close) values.
     * @return
     */
    @Override
    public LinkedList<LinkedHashMap<String, String>> getDaily() {
        String url = "https://alpha-vantage.p.rapidapi.com/query?market=CNY&symbol=" + this.symbol + "&function=DIGITAL_CURRENCY_DAILY";
        ConnectToApi api = new ConnectToApi(url, this.ENDPOINT, this.KEY);
        JSONObject job = api.getJsonObject();
        JSONObject innerJob = job.getJSONObject("Time Series (Digital Currency Daily)");
        Iterator<String> keys = innerJob.keys();
        this.dailySeries = new LinkedList<>();
        while (keys.hasNext()) {
            LinkedHashMap<String, String> temp = new LinkedHashMap<>();
            System.out.println(keys.next());
            JSONObject ob = innerJob.getJSONObject(keys.next());
            Iterator<String> inKeys = ob.keys();
            while (inKeys.hasNext()) {
                temp.put(inKeys.next(), ob.getString(inKeys.next()));
            }
            this.dailySeries.add(temp);
        }
        return this.dailySeries;
    }

    /**
     * Returns linked list of weekly (open, high, low, and close) values.
     * @return
     */
    @Override
    public LinkedList<LinkedHashMap<String, String>> getWeekly() {
        String url = "https://alpha-vantage.p.rapidapi.com/query?function=DIGITAL_CURRENCY_WEEKLY&symbol=" + this.symbol + "&market=CNY";
        ConnectToApi api = new ConnectToApi(url, this.ENDPOINT, this.KEY);
        JSONObject job = api.getJsonObject();
        JSONObject innerJob = job.getJSONObject("Time Series (Digital Currency Weekly)");
        Iterator<String> keys = innerJob.keys();
        this.weeklySeries = new LinkedList<>();
        while (keys.hasNext()) {
            LinkedHashMap<String, String> temp = new LinkedHashMap<>();
            System.out.println(keys.next());
            JSONObject ob = innerJob.getJSONObject(keys.next());
            Iterator<String> inKeys = ob.keys();
            while (inKeys.hasNext()) {
                temp.put(inKeys.next(), ob.getString(inKeys.next()));
            }
            this.weeklySeries.add(temp);
        }
        return this.weeklySeries;
    }

    /**
     * Create json file for testing.
     * @param _innerObj
     */
    private void makeJSONFile(JSONObject _innerObj) {
        try {
            PrintWriter writer = new PrintWriter("alpha-coin.txt");
            writer.write(_innerObj.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * This method IS NOT COMPLETE.
     *
     * This was for the candle chart but that had some major issues.
     * The package was originally for java swing and was difficult
     * to attempting to port to javafx. So don't look too closely at this... pls..
     * @return
     * @throws ParseException
     */
    public List<BarData> getBarData() throws ParseException {
        String url = "https://alpha-vantage.p.rapidapi.com/query?market=CNY&symbol=" + this.symbol + "&function=DIGITAL_CURRENCY_DAILY";
        ConnectToApi api = new ConnectToApi(url, this.ENDPOINT, this.KEY);
        JSONObject job = api.getJsonObject();
        JSONObject innerJob = job.getJSONObject("Time Series (Digital Currency Daily)");
        Iterator<String> keys = innerJob.keys();
        this.dailySeries = new LinkedList<>();
        this.barData = new ArrayList<>();
        List<Date> dates = new ArrayList<>();
        while (keys.hasNext()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = formatter.parse(keys.next());
            dates.add(date);
        }
        Collections.sort(dates, new Comparator<Date>(){
            @Override
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });
        System.out.println(dates);
        int count = 0;
        keys = innerJob.keys();
        LinkedList<HashMap<String, String>> dataMap = new LinkedList<>();
        while (keys.hasNext()) {
            HashMap<String, String> item = new HashMap<>();
            LinkedHashMap<String, String> temp = new LinkedHashMap<>();
            JSONObject ob = innerJob.getJSONObject(keys.next());
            System.out.println(dates.get(count));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            GregorianCalendar gc = new GregorianCalendar();
            GregorianCalendar now = new GregorianCalendar();
            item.put("date", keys.next());
            item.put("open", ob.getString("1b. open (USD)"));
            item.put("high", ob.getString("2b. high (USD)"));
            item.put("low", ob.getString("3b. low (USD)"));
            item.put("close", ob.getString("4b. close (USD)"));
            item.put("volume", ob.getString("5. volume"));
            dataMap.add(item);
            gc.setTime(dates.get(count));
            count++;
        }
        System.out.println("===================================");

        for (int i = 0; i < dataMap.size()-1; i++) { // Loop over dates list
            for (int j = 0; j < dataMap.size()-1; j++) { // loop over dataMap list
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date tempDate = formatter.parse(dataMap.get(j).get("date"));

                if (tempDate.equals(dates.get(j))) {
                    System.out.println(tempDate + " == " + dates.get(j));
                    GregorianCalendar gc = new GregorianCalendar();
                    GregorianCalendar now = new GregorianCalendar();
                    gc.setTime(tempDate);

                    double open = Double.parseDouble(dataMap.get(i).get("open"));
                    double high = Double.parseDouble(dataMap.get(i).get("high"));
                    double low = Double.parseDouble(dataMap.get(i).get("low"));
                    double close = Double.parseDouble(dataMap.get(i).get("close"));
                    double dubVolume = Double.parseDouble(dataMap.get(i).get("volume"));
                    long volume = (new Double(dubVolume)).longValue();
                    System.out.println("date: " + gc);
                    BarData bd = new BarData(gc, open, high, low, close, volume);
                    this.barData.add(bd);
                }
            }
        }
        return this.barData;
    }
}

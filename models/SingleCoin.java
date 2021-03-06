package models;

/**
 * This class represents a single coin. It has
 * all of the relevant information associated
 * with a specific cryptocurrency.
 * @author Kyle
 */
import interfaces.GlobalClassInterface;
import interfaces.SingleCoinInterface;
import java.util.LinkedHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class SingleCoin implements SingleCoinInterface, GlobalClassInterface{

    private JSONObject coinList;
    private int id;
    private String uuid;
    private String slug;
    private String symbol;
    private String name;
    private String description;
    private String color;
    private String iconType;
    private String iconUrl;
    private String websiteUrl;
    private boolean confirmedSupply;
    private int numberOfMarkets;
    private int numberOfExchanges;
    private String type;
    private int volume;
    private int marketCap;
    private String price;
    private int circulatingSupply;
    private int totalSupply;
    private boolean approvedSupply;
    private int firstSeen;
    private double change;
    private int rank;
    private String stringChange;
    private JSONArray object;
    private int coinId;
    private LinkedHashMap<Double, String> coinList2;
    private final boolean DEBUG = controllers.Tab1Controller.DEBUG;

    /**
     * Constructor for JSONObject.
     * Used in CoinRankApi class
     * @param job
     */
    public SingleCoin(JSONObject job) {
        this.coinList = job;

        this.id = this.coinList.getInt("id");
        this.uuid = this.coinList.getString("uuid").replace("'", "");
        this.slug = this.coinList.getString("slug");
        this.symbol = this.coinList.getString("symbol");
        this.name = this.coinList.getString("name");
        this.iconType = this.coinList.getString("iconType");
        this.iconUrl = this.coinList.getString("iconUrl");
        this.confirmedSupply = this.coinList.getBoolean("confirmedSupply");
        this.numberOfMarkets = this.coinList.getInt("numberOfMarkets");
        this.numberOfExchanges = this.coinList.getInt("numberOfExchanges");
        this.type = this.coinList.getString("type");
        this.marketCap = this.coinList.getInt("marketCap");
        this.price = this.coinList.getString("price");
        this.approvedSupply = this.coinList.getBoolean("approvedSupply");
        this.firstSeen = this.coinList.getInt("firstSeen");
        this.rank = this.coinList.getInt("rank");
        this.change = this.coinList.getDouble("change");
        this.volume = this.coinList.getInt("volume");

        this.stringChange = "" + this.change;
    }

    /**
     * This constructor is used for testing purposes.
     * @param _price
     */
    public SingleCoin(String _price) {
        this.price = _price;
    }

    public SingleCoin() {}

    /**
     * Constructor for JSONArray.
     * Used in CoinHistory class to pull data for a coin using its id.
     * @param _jar
     * @param _id
     */
    public SingleCoin (JSONArray _jar, int _id) {
        this.object = _jar;
        this.coinId = _id;
        if (coinList2 == null) {
            coinList2 = new LinkedHashMap<>();
        }
        // Loop that adds historical data to appropriate data vields
        for (int i = 0; i < this.object.length(); i++) {
            JSONObject cn = this.object.getJSONObject(i);
            double price = Double.parseDouble(cn.getString("price"));
            String date = Integer.toString(cn.getInt("timestamp"));
            this.coinList2.put(price, date);
        }
    }

    // ========== SETTERS ==========

    public void updatePrice(String newPrice) {
        this.price = newPrice;
    }

    // ========== GETTERS ==========

    public LinkedHashMap<Double, String> getCoinHistory() {
        return this.coinList2;
    }

    @Override
    public int getId() {
        return coinList.getInt("id");
    }

    @Override
    public String getUuid() {
        return coinList.getString("uuid");
    }

    @Override
    public String getSlug() {
        return coinList.getString("slug");
    }

    @Override
    public String getSymbol() {
        return coinList.getString("symbol");
    }

    @Override
    public String getName() {
        return coinList.getString("name");
    }

    @Override
    public String getDescription() {
        return coinList.getString("description");
    }

    @Override
    public String getColor() {
        return coinList.getString("color");
    }

    @Override
    public String getIconType() {
        return coinList.getString("iconType");
    }

    @Override
    public String getIconUrl() {
        return coinList.getString("iconUrl");
    }

    @Override
    public String getWebsiteUrl() {
        return coinList.getString("websiteUrl");
    }

    @Override
    public boolean getConfirmedSupply() {
        return coinList.getBoolean("confirmedSupply");
    }

    @Override
    public int getNumberOfMarkets() {
        return coinList.getInt("numberOfMarkets");
    }

    @Override
    public int getNumberOfExchanges() {
        return coinList.getInt("numberOfExchanges");
    }

    @Override
    public String getType() {
        return coinList.getString("type");
    }

    @Override
    public int getVolume() {
        return coinList.getInt("volume");
    }

    @Override
    public int getMarketCap() {
        return coinList.getInt("marketCap");
    }

    @Override
    public String getCoinMarketCap() {
        return coinList.getString("marketCap");
    }

    @Override
    public String getPrice() {
        return coinList.getString("price");
    }

    @Override
    public int getCirculatingSupply() {
        return coinList.getInt("circulatingSupply");
    }

    @Override
    public int getTotalSupply() {
        return coinList.getInt("totalSupply");
    }

    @Override
    public boolean getApprovedSupply() {
        return coinList.getBoolean("approvedSupply");
    }

    @Override
    public int getFirstSeen() {
        return coinList.getInt("firstSeen");
    }

    @Override
    public double getChange() {
        return coinList.getDouble("change");
    }

    @Override
    public int getRank() {
        return coinList.getInt("rank");
    }
    
    public String getStringChange() {
        return this.stringChange;
    }

    @Override
    public String getClassName() {
        return "SingleCoin";
    }

    public String getTestPrice() {
        return this.price;
    }
}

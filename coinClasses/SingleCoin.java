package coinClasses;

import interfaces.SingleCoinInterface;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class represents a single coin. It has
 * all of the relevant information associated
 * with a specific cryptocurrency.
 * @author Kyle
 */
public class SingleCoin implements SingleCoinInterface{
    
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

    /**
     * Constructor
     * @param job 
     */
    public SingleCoin(JSONObject job) {
        coinList = job;
        
        id = coinList.getInt("id");
        uuid = coinList.getString("uuid");
        slug = coinList.getString("slug");
        symbol = coinList.getString("symbol");
        name = coinList.getString("name");
//        description = coinList.getString("description");  // TODO: Fix This
//        color = coinList.getString("color");
        iconType = coinList.getString("iconType");
        iconUrl = coinList.getString("iconUrl");
        price = coinList.getString("price");
        rank = coinList.getInt("rank");
        change = coinList.getDouble("change");
        volume = coinList.getInt("volume");
        
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
    public String getConfirmedSupply() {
        return coinList.getString("confirmedSupply");
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
        return coinList.getInt("supply");
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

    
}
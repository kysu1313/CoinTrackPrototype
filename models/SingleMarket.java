package models;

/**
 * This class represents a single market.
 * A market is an exchange for one or many types of cryptocurrencies.
 * @author Kyle
 */
import interfaces.GlobalClassInterface;
import org.json.JSONObject;

public class SingleMarket implements GlobalClassInterface{
    
    JSONObject arr;
    
    private int id;
    private String uuid;
    private int rank;
    private String baseSymbol;
    private String sourceName;
    private String sourceIconUrl;
    private int tickerCreatedAt;
    private int tickerClose;
    private double tickerBaseVolume;
    private double marketShare;
    private double price;
    private double volume;
    private final boolean DEBUG = controllers.Tab1Controller.DEBUG;
    
    public SingleMarket(JSONObject jar) {
        arr = jar;
        fillValues();
    }

    private void fillValues() {
        
    }
    
    public void setId() {
        this.id = arr.getInt("id");
    }
    
    public void setUuid() {
        this.uuid = arr.getString("uuid");
    }
    
    public void setRank() {
        this.rank = arr.getInt("rank");
    }
    
    public void setBaseSymbol() {
        this.baseSymbol = arr.getString("baseSymbol");
    }

    @Override
    public String getClassName() {
        return "SingleMarket";
    }
    
}

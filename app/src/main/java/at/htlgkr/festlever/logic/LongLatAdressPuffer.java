package at.htlgkr.festlever.logic;


import java.util.HashMap;
import java.util.Map;

public class LongLatAdressPuffer {

    //KEY = long;lat
    static Map<String, String> addresses = new HashMap<>();

    public String getAddress(double lon, double lat){
        return addresses.get(lon + ";" + lat);
    }

    public void storeAdress(double lon, double lat, String address){
        addresses.put(lon + ";" + lat, address);
    }

    public boolean isStored(double lon, double lat){
        return addresses.containsKey(lon + ";" + lat);
    }

}

package at.htlgkr.festlever.logic.locationiqtasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocationAsyncTask extends AsyncTask<String, String, List<String>> {

    @Override
    protected List<String> doInBackground(String... strings) {
        String address = strings[0];
        String API_KEY = "42ef307840de8a";
        List<String> longlat = new ArrayList<>();
        try{
            URL url = new URL("https://eu1.locationiq.com/v1/search.php?key="+API_KEY+"&q="+address+"&format=json");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type","application/json");
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream is = connection.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }
                JSONArray jsonArray = new JSONArray(responseStrBuilder.toString());
                String lat = jsonArray.getJSONObject(0).getString("lat");
                String lng = jsonArray.getJSONObject(0).getString("lon");
                longlat.add(lat);
                longlat.add(lng);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return longlat;
    }
}
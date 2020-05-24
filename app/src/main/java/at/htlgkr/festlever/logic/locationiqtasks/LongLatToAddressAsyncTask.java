package at.htlgkr.festlever.logic.locationiqtasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LongLatToAddressAsyncTask extends AsyncTask<Double, Integer, String> {
    private final String TAG = "GetAdressFromLongLat";

    @Override
    protected String doInBackground(Double... doubles) {

        String json = "";
        String API_KEY = "42ef307840de8a";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://eu1.locationiq.com/v1/reverse.php?key=" + API_KEY+"&lat=" + doubles[0] + "&lon=" + doubles[1] + "&format=json").openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                json = readResponseStream(reader);
                Log.d(TAG, "doInBackground: " + json);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return new JSONObject(json).getJSONObject("address").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;


    }

    private String readResponseStream(BufferedReader reader) throws IOException {
        Log.d(TAG, "entered readResponseStreaulat");
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ( (line=reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}

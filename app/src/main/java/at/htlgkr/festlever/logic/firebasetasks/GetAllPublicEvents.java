package at.htlgkr.festlever.logic.firebasetasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.htlgkr.festlever.objects.Event;

public class GetAllPublicEvents extends AsyncTask<String, Integer, List<Event>> {
    private final String TAG = "GetAllPublicEvents";

    @Override
    protected List<Event> doInBackground(String... strings) {
        String json = "";


        //GET from site
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://festlever-ab8ab.firebaseio.com/events-oeffentlich.json").openConnection();
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



        //CAST in EVENT list
        List<Event> events = new ArrayList<>();
        Gson gson = new Gson();

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iterator = jsonObject.keys();

            while (iterator.hasNext()) {
                String key = iterator.next();
                events.add(gson.fromJson(jsonObject.getString(key), Event.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return events;
    }

    private String readResponseStream(BufferedReader reader) throws IOException {
        Log.d(TAG, "entered readResponseStreaulat");
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}

package at.htlgkr.festlever.logic.firebasetasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
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

import at.htlgkr.festlever.objects.User;

public class GetAllUsers extends AsyncTask<String, Integer, List<User>> {
    private final String TAG = "GetAllUsers";

    @Override
    protected List<User> doInBackground(String... strings) {
        String json = "";


        //GET from site
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://festlever-ab8ab.firebaseio.com/benutzer.json").openConnection();
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



        //CAST in USER list
        List<User> users = new ArrayList<>();
        Gson gson = new Gson();

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iterator = jsonObject.keys();

            while (iterator.hasNext()) {
                String key = iterator.next();
                users.add(gson.fromJson(jsonObject.getString(key), User.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
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

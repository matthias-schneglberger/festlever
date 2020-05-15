package at.htlgkr.festlever.logic;

//import com.google.firebase.analytics.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


import at.htlgkr.festlever.objects.User;
import at.htlgkr.festlever.logic.firebasetasks.*;

public class FireBaseCommunication {
    private final String TAG = "FireBaseCommunication";
    DatabaseReference dbaseRef;

    public FireBaseCommunication(){
        dbaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public boolean registerUser(User user){ //Working
        return dbaseRef.child("benutzer").child(user.getUsername()).setValue(new GsonBuilder().create().toJson(user)).isComplete();
    }

    public List<User> getAllUsers(){ //Working
        GetAllUsers getAllUsers = new GetAllUsers();
        getAllUsers.execute();

        try {
            return getAllUsers.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }





}

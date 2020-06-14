package at.htlgkr.festlever.logic;

//import com.google.firebase.analytics.*;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;
import at.htlgkr.festlever.logic.firebasetasks.*;

public class FireBaseCommunication {
    private final String TAG = "FireBaseCommunication";
    private static List<User> usersPuffer;
    private static List<Event> eventsPuffer;
    DatabaseReference dbaseRef;
    FirebaseFirestore firebaseFirestore;

    public FireBaseCommunication(){
        dbaseRef = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public boolean registerUser(User user){ // Working
        boolean exist = false;
        List<User> userList = getAllUsers();
        for(User u: userList){
            if(u.getUsername().equals(user.getUsername())){
                exist = true;
            }
        }
        if(!exist){
            dbaseRef.child("benutzer").child(user.getUsername()).setValue(new GsonBuilder().create().toJson(user)).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            });
        }
        return !exist;
    }

    public void updateUser(User user){
        dbaseRef.child("benutzer").child(user.getUsername()).setValue(new GsonBuilder().create().toJson(user));
    }

    public void updateEvent(Event event){
        dbaseRef.child("events").child(event.getId()).setValue(new GsonBuilder().create().toJson(event));
    }

    public List<User> getAllUsers(){
        if(usersPuffer == null){
            return getAllUsers(true);
        }
        return usersPuffer;
    }

    public List<User> getAllUsers(boolean noPuffer){ //Working
        GetAllUsers getAllUsers = new GetAllUsers();
        getAllUsers.execute();

        try {
            usersPuffer = getAllUsers.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return usersPuffer;
    }

    public boolean createEvent(Event event){
        List<Event> events = getAllEvents();
        if(events.contains(event))
            return false;
        dbaseRef.child("events").child(event.getId()).setValue(new GsonBuilder().create().toJson(event));
        return true;
    }

    public boolean deleteEvent(String eventID){
        dbaseRef.child("events").child(eventID).removeValue();
        return true;
    }

    public List<Event> getAllEvents(){
        if(eventsPuffer == null){
            eventsPuffer = getAllEvents(true);
        }
        return eventsPuffer;
    }

    public List<Event> getAllEvents(boolean noPuffer){
        try {
            eventsPuffer =  new GetAllEvents().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return eventsPuffer;
    }

}

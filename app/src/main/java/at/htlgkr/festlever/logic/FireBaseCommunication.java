package at.htlgkr.festlever.logic;

//import com.google.firebase.analytics.*;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
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

    public boolean createEvent(Event event, boolean publicEvent){

        if(publicEvent){
            List<Event> events = getAllPublicEvents();
            if(events.contains(event))
                return false;
            dbaseRef.child("events-oeffentlich").child(event.getId()).setValue(new GsonBuilder().create().toJson(event));
        }
        else{
            List<Event> events = getAllPrivateEvents();
            if(events.contains(event))
                return false;
            dbaseRef.child("events-privat").child(event.getId()).setValue(new GsonBuilder().create().toJson(event));
        }



        return true;
    }


    public List<Event> getAllPublicEvents(){
        GetAllPublicEvents getAllPublicEvents = new GetAllPublicEvents();
        getAllPublicEvents.execute();

        try {
            return getAllPublicEvents.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Event> getAllPrivateEvents(){
        GetAllPrivateEvents getAllPrivateEvents = new GetAllPrivateEvents();
        getAllPrivateEvents.execute();

        try {
            return getAllPrivateEvents.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Event> getAllEvents(){
        List<Event> events = new ArrayList<>();

        events.addAll(getAllPublicEvents());
        events.addAll(getAllPrivateEvents());

        return events;
    }





}

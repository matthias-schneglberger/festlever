package at.htlgkr.festlever.logic;

//import com.google.firebase.analytics.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;


import at.htlgkr.festlever.objects.User;
import at.htlgkr.festlever.logic.firebasetasks.*;

public class FireBaseCommunication {
    private final String TAG = "FireBaseCommunication";
    DatabaseReference dbaseRef;

    public FireBaseCommunication(){
        dbaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void registerUser(User user){
        dbaseRef.child("benutzer").child(user.getUsername()).setValue(user);
    }

    public User getUser(String username){
        GetAllUsers getAllUsers = new GetAllUsers();
        getAllUsers.execute(username);

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

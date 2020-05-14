package at.htlgkr.festlever.logic;

//import com.google.firebase.analytics.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import at.htlgkr.festlever.objects.User;

public class FireBaseCommunication {
    DatabaseReference dbaseRef;

    public FireBaseCommunication(){
        dbaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void registerUser(User user){
        dbaseRef.child("benutzer").child(user.getUsername()).setValue(user);
    }




}

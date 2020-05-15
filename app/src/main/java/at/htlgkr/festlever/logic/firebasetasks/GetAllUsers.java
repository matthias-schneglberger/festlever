package at.htlgkr.festlever.logic.firebasetasks;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import at.htlgkr.festlever.objects.User;

public class GetAllUsers extends AsyncTask<String, Integer, User> implements ValueEventListener {
    private String username;
    private DatabaseReference dbaseRef;
    private boolean finished = false;
    private User realUser;

    @Override
    protected User doInBackground(String... strings) {
        username = strings[0];
        dbaseRef = FirebaseDatabase.getInstance().getReference();



        DatabaseReference dbr = dbaseRef.child("benutzer");
        dbr.addValueEventListener(this);


        while(!finished){}



        return realUser;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            User user = postSnapshot.getValue(User.class);
            if(user.getUsername().equals(username)){
                realUser = user;
                finished = true;
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        finished = true;
    }
}

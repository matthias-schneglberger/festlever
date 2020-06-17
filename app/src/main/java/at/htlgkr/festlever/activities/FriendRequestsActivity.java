package at.htlgkr.festlever.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.adapter.Adapter_friendRequests;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.User;

public class FriendRequestsActivity extends AppCompatActivity {
    private final String TAG = "FriendRequestsActivity";

    private User user;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private List<User> allusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
        }catch (NullPointerException ignored){}

        ListView listView = findViewById(R.id.activity_friend_requests_listView);
        List<User> requests = fireBaseCommunication.getAllUsers().stream().filter(a -> user.getFriendRequests().contains(a.getUsername())).collect(Collectors.toList());
        listView.setAdapter(new Adapter_friendRequests(this, R.layout.activity_friend_requests_user_listitem,requests, user));
    }
}

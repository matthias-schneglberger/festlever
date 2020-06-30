package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.adapter.Adapter_eventRequests;
import at.htlgkr.festlever.adapter.Adapter_friendRequests;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class EventRequestsActivity extends AppCompatActivity {
    private final String TAG = "EventRequestsActivity";

    private User user;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_requests);

        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
        }catch (NullPointerException ignored){}

        fireBaseCommunication.getAllUsers(true);
        user = MainActivity.user;
        ListView listView = findViewById(R.id.activity_event_requests_listView);
        listView.setEmptyView(findViewById(R.id.activity_event_requests_empty));
        List<Event> requests = fireBaseCommunication.getAllEvents(true).stream().filter(a -> user.getEventRequests().contains(a.getId())).collect(Collectors.toList());
        listView.setAdapter(new Adapter_eventRequests(this, R.layout.activity_event_requests_user_listitem,requests,fireBaseCommunication.getAllUsers(), user));
    }
}

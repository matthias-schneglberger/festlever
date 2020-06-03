package at.htlgkr.festlever.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.adapter.Adapter_event;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.*;

public class ShowProfileActivity extends AppCompatActivity {
    private final String TAG = "ShowProfileActivity";
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private User user;
    private List<Event> allEvents;
    private List<User> allUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
            Log.d(TAG, "onCreate: Current User logged in: " + user.getUsername());
        }catch (NullPointerException ignored){}

        allEvents = fireBaseCommunication.getAllEvents().stream().filter(a -> a.getCreater().equals(user.getUsername()) && a.isPublic()).collect(Collectors.toList());
        allUser = fireBaseCommunication.getAllUsers();

        TextView username = findViewById(R.id.activity_show_profile_username);
        username.setText(user.getUsername());

        ListView listView = findViewById(R.id.activity_show_profile_listview);
        listView.setAdapter(new Adapter_event(getApplicationContext(),R.layout.fragment_main_listview_item,allEvents, allUser, false, user));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(), EventDetailsActivity.class).putExtra("user",user).putExtra("event",event));
            }
        });
    }
}

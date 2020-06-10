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
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.adapter.Adapter_event;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.*;
import at.htlgkr.festlever.ui.main.SectionsPagerAdapter;

public class ShowProfileActivity extends AppCompatActivity {
    private final String TAG = "ShowProfileActivity";
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private User user;
    private List<Event> allEventsProvided;
    private List<User> allUser;
    private List<Event> allEvents;

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


        allEventsProvided = fireBaseCommunication.getAllEvents().stream().filter(a -> a.getCreater().equals(user.getUsername()) && a.isPublic()).collect(Collectors.toList());
        allUser = fireBaseCommunication.getAllUsers();
        allEvents = fireBaseCommunication.getAllEvents();

        TextView username = findViewById(R.id.activity_show_profile_username);
        TextView acceptedEventsView = findViewById(R.id.activity_show_profile_acceptedEvents);
        TextView providedEventsView = findViewById(R.id.activity_show_profile_providedEvents);
//        ListView listView = findViewById(R.id.activity_show_profile_listview);
        username.setText(user.getUsername());
//        listView.setAdapter(new Adapter_event(getApplicationContext(),R.layout.fragment_main_listview_item,allEvents, allUser, false, user));

        int acceptedEvents = 0;
        int providedEvents = 0;
        for(Event e : allEvents){
            if(e.getAcceptUser().contains(user.getUsername())){
                acceptedEvents++;
            }
            if(e.getCreater().equals(user.getUsername())){
                providedEvents++;
            }
        }
        acceptedEventsView.setText(String.valueOf(acceptedEvents));
        providedEventsView.setText(String.valueOf(providedEvents));


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),user);
//        ViewPager viewPager = findViewById(R.id.activity_show_profile_view_pager);
//        viewPager.setAdapter(sectionsPagerAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Event event = (Event) parent.getItemAtPosition(position);
//                startActivity(new Intent(getApplicationContext(), EventDetailsActivity.class).putExtra("user",user).putExtra("event",event));
//            }
//        });
    }
}

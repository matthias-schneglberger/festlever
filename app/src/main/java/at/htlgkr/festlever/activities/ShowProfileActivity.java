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

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.adapter.Adapter_event;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.*;
import at.htlgkr.festlever.ui.main.SectionsPagerAdapter;
import at.htlgkr.festlever.ui.main.ShowProfilePagerAdapter;

public class ShowProfileActivity extends AppCompatActivity {
    private final String TAG = "ShowProfileActivity";

    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private User user;
    private List<Event> allEventsProvided;
    private List<User> allUser;
    private List<Event> allEvents;

    TextView username, acceptedEventsView, providedEventsView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
        }catch (NullPointerException ignored){}

        allUser = fireBaseCommunication.getAllUsers();
        allEvents = fireBaseCommunication.getAllEvents();
        allEventsProvided = allEvents.stream().filter(a -> a.getCreater().equals(user.getUsername()) && a.isPublic()).collect(Collectors.toList());

        initializeViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.acitivty_show_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(user.getUsername());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



//        username.setText(user.getUsername());

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
//        acceptedEventsView.setText(String.valueOf(acceptedEvents));
//        providedEventsView.setText(String.valueOf(providedEvents));

        //Fragment
        ShowProfilePagerAdapter showProfilePagerAdapter = new ShowProfilePagerAdapter(this, getSupportFragmentManager(),user);
        TabLayout tabs = findViewById(R.id.activity_show_profile_tabs);
        ViewPager viewPager = findViewById(R.id.activity_show_profile_view_pager);

        viewPager.setAdapter(showProfilePagerAdapter);
        tabs.setupWithViewPager(viewPager);

    }

    void initializeViews(){
//        username = findViewById(R.id.activity_show_profile_username);
//        acceptedEventsView = findViewById(R.id.activity_show_profile_acceptedEvents);
//        providedEventsView = findViewById(R.id.activity_show_profile_providedEvents);
    }
}

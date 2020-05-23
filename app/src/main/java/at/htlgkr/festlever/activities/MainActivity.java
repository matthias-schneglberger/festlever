package at.htlgkr.festlever.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.objects.User;
import at.htlgkr.festlever.ui.main.MainFragment;
import at.htlgkr.festlever.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    public static User user;
    private MainFragment publicFragment;
    private MainFragment privateFragment;
    private MainFragment myEventsFragment;

    File rememberMeFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //RememberMe-File
        rememberMeFile = new File(getApplicationContext().getFilesDir().getPath().toString() + "/rememberMe.txt");
        if(!rememberMeFile.exists()){
            try {
                rememberMeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Set up Fragments
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),user);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        //Get Fragments
        publicFragment = (MainFragment) sectionsPagerAdapter.getItem(0);
        privateFragment = (MainFragment) sectionsPagerAdapter.getItem(1);
        myEventsFragment = (MainFragment) sectionsPagerAdapter.getItem(2);

        //Set up Tabs
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //Fab Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });
    }

    void createEvent(){
        startActivity(new Intent(this,CreateEventActivity.class).putExtra("user",user));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options_menue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.settings:

                break;
            case R.id.findFriends:
                startActivity(new Intent(this,FindFriendsActivity.class).putExtra("user",user));
                break;
            case R.id.friendRequests:
                startActivity(new Intent(this,FriendRequestsActivity.class).putExtra("user",user));
                break;
            case R.id.eventRequests:
                startActivity(new Intent(this,EventRequestsActivity.class).putExtra("user",user));
                break;
            case R.id.logout:
                logout();
                break;
            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void logout(){ // Working
        try (PrintWriter writer = new PrintWriter(new FileWriter(rememberMeFile, false))){
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
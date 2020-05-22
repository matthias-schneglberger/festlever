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
    private User user;
    private MainFragment publicFragment;
    private MainFragment privateFragment;
    private MainFragment myEventsFragment;

    File rememberMeFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rememberMeFile = new File(getApplicationContext().getFilesDir().getPath().toString() + "/rememberMe.txt");
        if(!rememberMeFile.exists()){
            try {
                rememberMeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.get("user");
        Log.d(TAG, "onCreate: Current User logged in: " + user.getUsername());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),user);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        publicFragment = (MainFragment) sectionsPagerAdapter.getItem(0);
        privateFragment = (MainFragment) sectionsPagerAdapter.getItem(1);
        myEventsFragment = (MainFragment) sectionsPagerAdapter.getItem(2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

                break;
            case R.id.friendRequests:

                break;
            case R.id.eventRequests:

                break;
            case R.id.logout:
                logout();
                break;
            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void logout(){
        try (PrintWriter writer = new PrintWriter(new FileWriter(rememberMeFile, false))){
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
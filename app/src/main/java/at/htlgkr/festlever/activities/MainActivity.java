package at.htlgkr.festlever.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.interfaces.IFragmentUpdateListView;
import at.htlgkr.festlever.objects.User;
import at.htlgkr.festlever.preferences.MySettingsActivity;
import at.htlgkr.festlever.ui.main.MainFragment;
import at.htlgkr.festlever.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    public static User user;
    public static String searchTerm = "";

    private List<IFragmentUpdateListView> iFragmentUpdateListViewList = new ArrayList<>();

    public List<IFragmentUpdateListView> getIFragmentUpdateListView() {
        return iFragmentUpdateListViewList;
    }

    public void setIFragmentUpdateListView(int index, IFragmentUpdateListView iFragmentUpdateListView) {
        if(iFragmentUpdateListViewList.size()==3){
            iFragmentUpdateListViewList.set(index, iFragmentUpdateListView);
        }
        else{
            iFragmentUpdateListViewList.add(index,iFragmentUpdateListView);
        }
    }

    private final int REQUEST_CREATE_CHANGE = 2;

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
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(sectionsPagerAdapter);


        //Set up Tabs
        TabLayout tabs = findViewById(R.id.activity_show_profile_tabs);
        tabs.setupWithViewPager(viewPager);

        //Fab Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });

        //Search View
        SearchView searchView = findViewById(R.id.activity_main_searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchTerm = s;
                return true;
            }
        });

    }

    void createEvent(){
        startActivityForResult(new Intent(this, EventCreateChangeActivity.class).putExtra("user",user),REQUEST_CREATE_CHANGE);
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
            case R.id.myProfil:
                startActivity(new Intent(this,ShowProfileActivity.class).putExtra("user",user));
                break;
            case R.id.settings:
                startActivity(new Intent(this, MySettingsActivity.class));
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

    void logout(){
        try (PrintWriter writer = new PrintWriter(new FileWriter(rememberMeFile, false))){
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CREATE_CHANGE){
            if(resultCode == RESULT_OK){
                getIFragmentUpdateListView().get(0).refreshListView();
                getIFragmentUpdateListView().get(1).refreshListView();
                getIFragmentUpdateListView().get(2).refreshListView();
            }
        }
    }
}


package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.adapter.Adapter_findFriends;
import at.htlgkr.festlever.adapter.Adapter_inviteToEvent;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class InviteToEventActivity extends AppCompatActivity {
    private final String TAG = "InviteToEventActivity";
    private User user;
    private Event event;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private List<User> allusers;
    private String searchTerm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        ListView listView = findViewById(R.id.activity_find_friends_allUsers);
        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
            event = (Event) bundle.get("event");
            Log.d(TAG, "onCreate: Current User logged in: " + user.getUsername());
        }catch (NullPointerException ignored){}

        allusers = fireBaseCommunication.getAllUsers();

        //Searchview
        SearchView searchView = findViewById(R.id.activity_find_friends_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                newSearchTerm(s, listView);
                return true;
            }
        });

        //init update
        newSearchTerm("", listView);

        //ListView OnClickListener

    }

    public void newSearchTerm(String searchterm, ListView listView){
        this.searchTerm = searchterm;

        List<User> tmpUsers = allusers.stream().filter(n -> n.getUsername().contains(searchterm)).collect(Collectors.toList());
        tmpUsers = tmpUsers.stream().filter(n -> !n.getUsername().equals(user.getUsername())).collect(Collectors.toList());

        listView.setAdapter(new Adapter_inviteToEvent(this, R.layout.activity_find_friends_user_listitem, tmpUsers, event));
    }
}

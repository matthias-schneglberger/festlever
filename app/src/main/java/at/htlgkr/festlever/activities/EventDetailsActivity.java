package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class EventDetailsActivity extends AppCompatActivity {
    private final String TAG = "EventDetailsActivity";
    private User user;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
            event = (Event) bundle.get("event");
        }catch (NullPointerException ignored){}
    }
}

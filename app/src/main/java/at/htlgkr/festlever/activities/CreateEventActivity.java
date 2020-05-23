package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class CreateEventActivity extends AppCompatActivity {
    private final String TAG = "CreateEventActivity";
    private User user;
    private boolean eventIsPublic = true;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        final ConstraintLayout publicLayout = findViewById(R.id.activity_create_event_publicLayout);
        final ConstraintLayout privateLayout = findViewById(R.id.activity_create_event_privateLayout);
        Button createButton = findViewById(R.id.activity_create_event_buttonCreate);
        final EditText editText_title = findViewById(R.id.activity_create_event_title);
        final EditText editText_address = findViewById(R.id.activity_create_event_address);
        final EditText editText_entrance = findViewById(R.id.activity_create_event_entrance);
        final EditText editText_date = findViewById(R.id.activity_create_event_date);




        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
            Log.d(TAG, "onCreate: Current User logged in: " + user.getUsername());
        }catch (NullPointerException ignored){}




        publicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicLayout.setBackgroundColor(getResources().getColor(R.color.addEventInputBackground));
                privateLayout.setBackground(getDrawable(R.drawable.background_add_event_input_white));

                eventIsPublic = true;
            }
        });

        privateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicLayout.setBackground(getDrawable(R.drawable.background_add_event_input_white));
                privateLayout.setBackgroundColor(getResources().getColor(R.color.addEventInputBackground));

                eventIsPublic = false;
            }
        });


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event tmpEvent = new Event();
                tmpEvent.setTitle(editText_title.getText().toString());

                //Implement some MAGIC here!//

                tmpEvent.generateID();


                fireBaseCommunication.createEvent(tmpEvent, eventIsPublic);

            }
        });

    }
}

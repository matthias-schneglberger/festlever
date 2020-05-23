package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.objects.User;

public class CreateEventActivity extends AppCompatActivity {
    private final String TAG = "CreateEventActivity";
    private User user;
    private boolean eventIsPublic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
            Log.d(TAG, "onCreate: Current User logged in: " + user.getUsername());
        }catch (NullPointerException ignored){}


        final ConstraintLayout publicLayout = findViewById(R.id.activity_create_event_publicLayout);
        final ConstraintLayout privateLayout = findViewById(R.id.activity_create_event_privateLayout);

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

    }
}

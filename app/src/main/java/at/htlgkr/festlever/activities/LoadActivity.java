package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.services.UpdateService;

public class LoadActivity extends AppCompatActivity {
    FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        progressBar = findViewById(R.id.activity_load_progressBar);
        updateProgressBar(0);
        Thread thread = new Thread(this::doInBackground);
        thread.start();
    }

    public void doInBackground(){

        // do smth here

        updateProgressBar(25);

        // TODO network available

        updateProgressBar(50);

        startService(new Intent(this, UpdateService.class));

        fireBaseCommunication.getAllUsers();


        updateProgressBar(75);

        fireBaseCommunication.getAllEvents();


        updateProgressBar(100);


        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void updateProgressBar(int value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(value);
            }
        });
    }
}

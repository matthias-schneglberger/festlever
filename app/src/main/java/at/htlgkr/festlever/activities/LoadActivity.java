package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.services.UpdateService;


public class LoadActivity extends AppCompatActivity {
    private final String TAG = "LoadActivity";

    FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    ProgressBar progressBar;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        context = this;
        progressBar = findViewById(R.id.activity_load_progressBar);
        updateProgressBar(0);
        Thread thread = new Thread(this::doInBackground);
        thread.start();
    }

    public void doInBackground(){
        //Update
        updateProgressBar(0);

        if(!isInternetAvailable()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Keine Internetverbindung!", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        //Update
        updateProgressBar(33);

        startService(new Intent(this, UpdateService.class));

        fireBaseCommunication.getAllUsers();

        //Update
        updateProgressBar(66);

        fireBaseCommunication.getAllEvents();

        //Update
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

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        try{
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } catch (Exception e){
            return false;
        }
    }
}

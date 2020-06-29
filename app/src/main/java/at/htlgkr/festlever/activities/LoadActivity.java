package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.ImagePuffer;
import at.htlgkr.festlever.logic.LongLatAdressPuffer;
import at.htlgkr.festlever.logic.locationiqtasks.LongLatToAddressAsyncTask;
import at.htlgkr.festlever.objects.Event;
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

        fireBaseCommunication.getAllUsers(true);

        //Update
        updateProgressBar(66);

        List<Event> allEvents = fireBaseCommunication.getAllEvents(true);

        LongLatAdressPuffer longLatAdressPuffer = new LongLatAdressPuffer();
        ImagePuffer imagePuffer = new ImagePuffer();


        for(Event event : allEvents){
            //Image Puffer
            if(event.getImage()!=null){
                if(!imagePuffer.isStored(event.getImage())){
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReference();

                    storageReference.child(event.getImage()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                            imagePuffer.storeImage(event.getImage(), bitmap);
                        }
                    });
                }
            }

            //Address Puffer
            if(!longLatAdressPuffer.isStored(event.getLongitude(), event.getLatitude())){
                try {
                    String input = new LongLatToAddressAsyncTask().execute(event.getLatitude(), event.getLongitude()).get();
                    if(input!=null){
                        JSONObject jsonObject = new JSONObject(input);
                        String address = jsonObject.getString("road") + " " + jsonObject.getString("house_number") + ", " + jsonObject.getString("postcode");
                        longLatAdressPuffer.storeAdress(event.getLongitude(), event.getLatitude(), address);
                    }
                } catch (ExecutionException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
            }

        }


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

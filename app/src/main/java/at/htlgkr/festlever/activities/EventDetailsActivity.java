package at.htlgkr.festlever.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.ImagePuffer;
import at.htlgkr.festlever.logic.LongLatAdressPuffer;
import at.htlgkr.festlever.logic.locationiqtasks.LongLatToAddressAsyncTask;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class EventDetailsActivity extends AppCompatActivity {
    private final String TAG = "EventDetailsActivity";

    private User user;
    private Event event;
    private ImageView image;
    private TextView eventName;
    private TextView eventAddress;
    private TextView dayOfEvent;
    private TextView monthOfEvent;
    private TextView description;
    private Button acceptButton;
    private TextView friendsAccepted;
    private TextView timeUntilEvent;
    private TextView accepts;
    private TextView entrance;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

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

        ImagePuffer imagePuffer = new ImagePuffer();

        initializeViews();

        if(imagePuffer.isStored(event.getImage())){
            image.setImageBitmap(imagePuffer.getImage(event.getImage()));
        }
        else{
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();

            try {
                storageReference.child(event.getImage()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image.setImageBitmap(bitmap);

                        imagePuffer.storeImage(event.getImage(), bitmap);
                    }
                });
            }
            catch (Exception e){}
        }
        //name
        eventName.setText(event.getTitle());

        //day
        dayOfEvent.setText(event.getDate().substring(0,2));

        //month
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter month_date = DateTimeFormatter.ofPattern("MMM", Locale.GERMAN);
        monthOfEvent.setText(month_date.format(LocalDate.parse(event.getDate(),dtf)));

        //time until event
        timeUntilEvent.setText(ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(event.getDate(),dtf)) + " Tage");

        //address
        LongLatAdressPuffer longLatAdressPuffer = new LongLatAdressPuffer();
        if(longLatAdressPuffer.isStored(event.getLongitude(), event.getLatitude())){
            eventAddress.setText(longLatAdressPuffer.getAddress(event.getLongitude(), event.getLatitude()));
        }
        else{
            try {
                String input = new LongLatToAddressAsyncTask().execute(event.getLatitude(), event.getLongitude()).get();
                if(input!=null){
                    JSONObject jsonObject = new JSONObject(input);
                    String address = jsonObject.getString("road") + " " + jsonObject.getString("house_number") + ", " + jsonObject.getString("postcode");
                    eventAddress.setText(address);
                    longLatAdressPuffer.storeAdress(event.getLongitude(), event.getLatitude(), address);
                }
            } catch (ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        }

        eventAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse(event.getLocation());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        //Desc
        description.setText(event.getDescription());

        //accepts
        accepts.setText(event.getAcceptUser().size()+"");

        //entrance
        entrance.setText(event.getEntrance()+" €");

        //friends
        List<String> friendsList = event.getAcceptUser().stream().filter(a -> user.getFriends().contains(a)).collect(Collectors.toList());
        friendsAccepted.setText(friendsList.size() + " Freunde nehmen teil");

        friendsAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EventDetailsActivity.this);
                ListView listView = new ListView(EventDetailsActivity.this);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EventDetailsActivity.this, android.R.layout.simple_list_item_1, friendsList);
                listView.setAdapter(arrayAdapter);
                alertDialog.setTitle("Freunde");
                alertDialog.setView(listView);
                alertDialog.setNeutralButton("Schließen",null);
                alertDialog.show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String username = (String) parent.getItemAtPosition(position);
                        startActivity(new Intent(EventDetailsActivity.this,ShowProfileActivity.class).putExtra("user",fireBaseCommunication.getAllUsers().stream().filter(a -> a.getUsername().equals(username)).findFirst().get()));
                    }
                });
            }
        });

        //accept Button
        if(event.getAcceptUser().contains(user.getUsername())){
            acceptButton.setText("Nicht mehr Teilnehmen");
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptButtonClicked();
            }
        });
    }

    void initializeViews(){
        image = findViewById(R.id.activity_event_details_imageView);
        eventName = findViewById(R.id.activity_event_details_nameOfEvent);
        eventAddress = findViewById(R.id.activity_event_details_eventAddress);
        dayOfEvent = findViewById(R.id.activity_event_details_day);
        monthOfEvent = findViewById(R.id.activity_event_details_month);
        description = findViewById(R.id.activity_event_details_description);
        acceptButton = findViewById(R.id.activity_event_details_acceptEvent);
        friendsAccepted = findViewById(R.id.activity_event_details_friendAccepted);
        timeUntilEvent = findViewById(R.id.activity_event_details_timeUntilEvent);
        accepts = findViewById(R.id.activity_event_details_accepts);
        entrance = findViewById(R.id.activity_event_details_entrance);
    }

    public void acceptButtonClicked(){
        int acceptNum = Integer.parseInt(accepts.getText().toString());
        List<String> accepted = event.getAcceptUser();
        if(!accepted.contains(user.getUsername())){
            accepted.add(user.getUsername());
            accepts.setText(String.valueOf(acceptNum+1));
            acceptButton.setText("Nicht mehr Teilnehmen");
            Toast.makeText(getApplicationContext(), "Du nimmst jetzt an diesem Event teil",Toast.LENGTH_SHORT).show();

            event.setAcceptUser(accepted);
            setResult(RESULT_OK);
            fireBaseCommunication.createEvent(event);
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(event.getTitle());
            alertDialog.setMessage("Bist du dir sicher, dass du an diesem Event nicht mehr teilnehmen willst?");
            alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    acceptButton.setText("Teilnehmen");
                    accepted.remove(user.getUsername());
                    accepts.setText(String.valueOf(acceptNum-1));
                    Toast.makeText(EventDetailsActivity.this, "Du nimmst jetzt nicht mehr an diesem Event teil",Toast.LENGTH_SHORT).show();

                    event.setAcceptUser(accepted);
                    setResult(RESULT_OK);
                    fireBaseCommunication.createEvent(event);
                }
            });
            alertDialog.setNegativeButton("Nein",null);
            alertDialog.show();
        }


    }
}

package at.htlgkr.festlever.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.LongLatAdressPuffer;
import at.htlgkr.festlever.logic.locationiqtasks.AdressToLongLatAsyncTask;
import at.htlgkr.festlever.logic.locationiqtasks.LongLatToAddressAsyncTask;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class EventCreateChangeActivity extends AppCompatActivity {
    private final String TAG = "CreateEventActivity";

    private User user;
    private Event event;

    private boolean inChangeMode = true;
    private boolean eventIsPublic = true;

    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    LongLatAdressPuffer longLatAdressPuffer = new LongLatAdressPuffer();

    private ConstraintLayout publicLayout;
    private ConstraintLayout privateLayout;

    private EditText editText_title;
    private EditText editText_address;
    private EditText editText_entrance;
    private EditText editText_date;
    private TextInputEditText textInputEditText_description;

    private Button uploadImageButton;
    private Button createButton;

    private final int PICK_IMAGE_REQUEST = 22;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String storagePath;

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Initialize Firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

       //Initialize Views
        initializeViews();

        //Get User and Event
        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
            event = (Event) bundle.get("event");
        }catch (NullPointerException ignored){}

        //Public-Switch
        publicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateToPublic();
            }
        });

        //Private-Switch
        privateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateToPrivate();
            }
        });

        //Create-Button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inChangeMode){
                    changeEvent();
                }
                else{
                    createEvent();
                }
            }
        });

        //Upload-Image-Button
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Wählen Sie Ihr Bild aus"),PICK_IMAGE_REQUEST);
            }
        });

        //DatePickerDialog
        editText_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(editText_date.isFocused()){
                    LocalDateTime ldt = LocalDateTime.now();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                    DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (datePicker, i, i1, i2) -> {
                        LocalDateTime localDateTime = LocalDateTime.of(i, i1 + 1, i2, 0, 0);
                        editText_date.setText(dtf.format(localDateTime));
                    }, ldt.getYear(), ldt.getMonthValue() - 1, ldt.getDayOfMonth());

                    datePickerDialog.show();
                }
            }
        });

        //If Event is null, it creates, else it changes one
        if(event==null){
            inChangeMode = false;
        }

        if(inChangeMode){
            fillItems(event);
            createButton.setText("Änderungen speichern");
            uploadImageButton.setText("Bild ändern");
        }
    }

    void initializeViews(){
        publicLayout = findViewById(R.id.activity_create_event_publicLayout);
        privateLayout = findViewById(R.id.activity_create_event_privateLayout);

        createButton = findViewById(R.id.activity_create_event_buttonCreate);
        uploadImageButton = findViewById(R.id.activity_create_event_image);

        editText_title = findViewById(R.id.activity_create_event_title);
        editText_address = findViewById(R.id.activity_create_event_address);
        editText_entrance = findViewById(R.id.activity_create_event_entrance);
        editText_date = findViewById(R.id.activity_create_event_date);
        textInputEditText_description = findViewById(R.id.activity_create_event_description);
    }

    void fillItems(Event event){
        if(event.isPublic()){
            updateToPublic();
        }
        else{
            updateToPrivate();
        }

        //Set title
        editText_title.setText(event.getTitle());

        //Set Address
        if(longLatAdressPuffer.isStored(event.getLongitude(), event.getLatitude())){
            editText_address.setText(longLatAdressPuffer.getAddress(event.getLongitude(), event.getLatitude()));
        }
        else{
            try {
                String input = new LongLatToAddressAsyncTask().execute(event.getLatitude(), event.getLongitude()).get();
                if(input!=null){
                    JSONObject jsonObject = new JSONObject(input);
                    String address = jsonObject.getString("road") + " " + jsonObject.getString("house_number") + ", " + jsonObject.getString("postcode");
                    editText_address.setText(address);
                    longLatAdressPuffer.storeAdress(event.getLongitude(), event.getLatitude(), address);
                }
            } catch (ExecutionException | JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Set entrance
        editText_entrance.setText(event.getEntrance()+"");

        //Set date
        editText_date.setText(event.getDate());

        //Set description
        textInputEditText_description.setText(event.getDescription());

        //Set Image
        storagePath = event.getImage();
    }

    void updateToPublic(){
        publicLayout.setBackgroundColor(getResources().getColor(R.color.addEventInputBackground));
        privateLayout.setBackground(getDrawable(R.drawable.background_add_event_input_white));
        eventIsPublic = true;
    }

    void updateToPrivate(){
        publicLayout.setBackground(getDrawable(R.drawable.background_add_event_input_white));
        privateLayout.setBackgroundColor(getResources().getColor(R.color.addEventInputBackground));
        eventIsPublic = false;
    }

    void createEvent(){
        Log.d(TAG, "createEvent: ");

        if(!validate()){
            onCreateFailed();
            return;
        }

        Event tmpEvent = new Event();

        uploadImage(filePath);

        final ProgressDialog progressDialog = new ProgressDialog(EventCreateChangeActivity.this, R.style.Theme_Design_BottomSheetDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Erstellen...");
        progressDialog.show();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tmpEvent.setTitle(editText_title.getText().toString());
                AdressToLongLatAsyncTask adressToLongLatAsyncTask = new AdressToLongLatAsyncTask();
                adressToLongLatAsyncTask.execute(editText_address.getText().toString());
                List<String> longlat = new ArrayList<>();
                try {
                    longlat = adressToLongLatAsyncTask.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                if(longlat.isEmpty()){
                    onCreateFailed();
                    return;
                }

                //Set address
                tmpEvent.setLatitude(Double.valueOf(longlat.get(0)));
                tmpEvent.setLongitude(Double.valueOf(longlat.get(1)));

                //Set date
                tmpEvent.setDate(editText_date.getText().toString());

                //Set entrance
                tmpEvent.setEntrance(Double.valueOf(editText_entrance.getText().toString()));

                //Set image
                tmpEvent.setImage(storagePath);

                //Set description
                tmpEvent.setDescription(textInputEditText_description.getText().toString());

                //Set creator
                tmpEvent.setCreater(user.getUsername());

                //Set if public
                tmpEvent.setPublic(eventIsPublic);
                try {
                    tmpEvent.setRegion(new JSONObject(new LongLatToAddressAsyncTask().execute(Double.valueOf(longlat.get(0)),Double.valueOf(longlat.get(1))).get()).getString("country"));
                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                //Generate id
                tmpEvent.generateID();

                if(fireBaseCommunication.createEvent(tmpEvent)){
                    onCreateSuccess();
                }
                else{
                    onCreateFailed();
                }
                progressDialog.dismiss();
            }
        },1000);
    }

    void changeEvent(){
        Log.d(TAG, "changeEvent: ");
        if(!validate()){
            onCreateFailed();
            return;
        }

        uploadImage(filePath);

        final ProgressDialog progressDialog = new ProgressDialog(EventCreateChangeActivity.this, R.style.Theme_Design_BottomSheetDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Änderungen speichern...");
        progressDialog.show();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Set title
                event.setTitle(editText_title.getText().toString());

                //Set Address
                AdressToLongLatAsyncTask adressToLongLatAsyncTask = new AdressToLongLatAsyncTask();
                adressToLongLatAsyncTask.execute(editText_address.getText().toString());
                List<String> longlat = new ArrayList<>();
                try {
                    longlat = adressToLongLatAsyncTask.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if(longlat.isEmpty()){
                    onCreateFailed();
                    return;
                }

                event.setLatitude(Double.valueOf(longlat.get(0)));
                event.setLongitude(Double.valueOf(longlat.get(1)));

                //Store address
                longLatAdressPuffer.storeAdress(Double.valueOf(longlat.get(1)),Double.valueOf(longlat.get(0)), editText_address.getText().toString());

                //Set address
                event.setDate(editText_date.getText().toString());

                //Set entrance
                event.setEntrance(Double.valueOf(editText_entrance.getText().toString()));

                //Set image
                event.setImage(storagePath);

                //Set description
                event.setDescription(textInputEditText_description.getText().toString());

                //Set creator
                event.setCreater(user.getUsername());

                //Set if public
                event.setPublic(eventIsPublic);


                if(fireBaseCommunication.createEvent(event)){
                    onCreateSuccess();
                }
                else{
                    onCreateFailed();
                }
                progressDialog.dismiss();
            }
        },1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImageButton.setText("Bild ändern");
        }
    }

    void uploadImage(Uri filePath){
        storagePath = "images/"+ UUID.randomUUID().toString();
        StorageReference ref = storageReference.child(storagePath);

        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EventCreateChangeActivity.this, "Bild hochgeladen", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EventCreateChangeActivity.this, "Bild hochladen fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean validate() { // Working
        boolean valid = true;

        String check_editText_title = editText_title.getText().toString();
        String check_editText_address = editText_address.getText().toString();
        String check_editText_entrance = editText_entrance.getText().toString();
        String check_editText_date = editText_date.getText().toString();

        if (check_editText_title.isEmpty()) {
            editText_title.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else {
            editText_title.setError(null);
        }

        if (check_editText_address.isEmpty()) {
            editText_address.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else {
            editText_address.setError(null);
        }

        if (check_editText_entrance.isEmpty()) {
            editText_entrance.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else {
            editText_entrance.setError(null);
        }

        if (check_editText_date.isEmpty()) {
            editText_date.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else {
            editText_date.setError(null);
        }
        return valid;
    }

    void onCreateSuccess(){
        createButton.setEnabled(true);
        setResult(RESULT_OK);
        finish();
    }

    void onCreateFailed(){
        Toast.makeText(getApplicationContext(), "Fehlgeschlagen", Toast.LENGTH_LONG).show();
        createButton.setEnabled(true);
    }
}

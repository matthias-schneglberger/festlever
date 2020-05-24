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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.locationiqtasks.AdressToLongLatAsyncTask;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class CreateEventActivity extends AppCompatActivity {
    private final String TAG = "CreateEventActivity";
    private User user;
    private boolean eventIsPublic = true;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    EditText editText_title;
    EditText editText_address;
    EditText editText_entrance;
    EditText editText_date;

    Button uploadImageButton;
    Button createButton;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    FirebaseStorage storage;
    StorageReference storageReference;

    String storagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final ConstraintLayout publicLayout = findViewById(R.id.activity_create_event_publicLayout);
        final ConstraintLayout privateLayout = findViewById(R.id.activity_create_event_privateLayout);

        createButton = findViewById(R.id.activity_create_event_buttonCreate);
        uploadImageButton = findViewById(R.id.activity_create_event_image);

        editText_title = findViewById(R.id.activity_create_event_title);
        editText_address = findViewById(R.id.activity_create_event_address);
        editText_entrance = findViewById(R.id.activity_create_event_entrance);
        editText_date = findViewById(R.id.activity_create_event_date);

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
                createEvent();
            }
        });

        uploadImageButton.setText("Bild hochladen ...");
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Wählen Sie Ihr Bild aus"),PICK_IMAGE_REQUEST);

            }
        });

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
    }

    void createEvent(){ // Working
        Log.d(TAG, "createEvent: ");
        if(!validate()){
            onCreateFailed();
            return;
        }

        Event tmpEvent = new Event();

        final ProgressDialog progressDialog = new ProgressDialog(CreateEventActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Erstellen...");
        progressDialog.show();

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
        tmpEvent.setLatitude(Double.valueOf(longlat.get(0)));
        tmpEvent.setLongitude(Double.valueOf(longlat.get(1)));
        tmpEvent.generateID();
        tmpEvent.setDate(editText_date.getText().toString());
        tmpEvent.setEntrance(Double.valueOf(editText_entrance.getText().toString()));
        tmpEvent.setImage(storagePath);

        if(fireBaseCommunication.createEvent(tmpEvent, eventIsPublic)){
            onCreateSuccess();
        }
        else{
            onCreateFailed();
        }
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            uploadImage(filePath);
        }
    }

    void uploadImage(Uri filePath){ // Working
        final ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Hochladen...");
        progressDialog.show();

        storagePath = "images/"+ UUID.randomUUID().toString();
        StorageReference ref = storageReference.child(storagePath);

        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(CreateEventActivity.this, "Bild hochgeladen", Toast.LENGTH_SHORT).show();
                uploadImageButton.setText("Bild ändern");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CreateEventActivity.this, "Bild hochladen fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Hochladen " + (int)progress + "%");
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

    void onCreateSuccess(){ // Working
        createButton.setEnabled(true);
        finish();
    }

    void onCreateFailed(){ // Working
        Toast.makeText(getApplicationContext(), "Fehlgeschlagen", Toast.LENGTH_LONG).show();
        createButton.setEnabled(true);
    }
}

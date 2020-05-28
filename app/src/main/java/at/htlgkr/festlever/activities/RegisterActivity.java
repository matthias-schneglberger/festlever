package at.htlgkr.festlever.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.PasswordToHash;
import at.htlgkr.festlever.objects.User;

public class RegisterActivity extends AppCompatActivity {
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private PasswordToHash passwordToHash = new PasswordToHash();
    private MessageDigest messageDigest;
    private final String TAG = "RegisterActivity";

    private EditText email;
    private EditText username;
    private EditText password;
    private Button registerButton;
    private TextView loginText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();

        //Register-Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        //To-Login-Switch
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Switch to Login");
                finish();
            }
        });

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    void initializeViews(){ // Working
        Log.d(TAG, "initializeViews");
        email = findViewById(R.id.activity_register_email);
        username = findViewById(R.id.activity_register_username);
        password = findViewById(R.id.activity_register_password);
        registerButton = findViewById(R.id.activity_register_register_button);
        loginText = findViewById(R.id.activity_register_to_login_text);
    }

    void register(){ // Working
        Log.d(TAG, "register");

        if(!validate()){
            onRegisterFailed();
            return;
        }

        registerButton.setEnabled(false);

        final String check_email = email.getText().toString();
        final String check_username = username.getText().toString();

        //Hash-Password
        final String check_password = passwordToHash.bytesToHex(messageDigest.digest(password.getText().toString().getBytes()));

        //Register-Logic

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.Theme_Design_BottomSheetDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Account wird erstellt...");
        progressDialog.show();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                User user = new User(check_username,check_password,check_email);
                if(fireBaseCommunication.registerUser(user)){
                    onRegisterSuccess(user);
                }
                else{
                    onRegisterFailed();
                }
                progressDialog.dismiss();
            }
        },100);
    }

    boolean validate() { // Working
        boolean valid = true;

        String check_email = email.getText().toString();
        String check_username = username.getText().toString();
        String check_password = password.getText().toString();

        if (check_email.isEmpty()) {
            email.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(check_email).matches()) {
            email.setError("Es muss eine gültige E-Mail Adresse sein");
            valid = false;
        } else {
            email.setError(null);
        }

        if (check_username.isEmpty()) {
            username.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else if (check_username.trim().contains(" ")) {
            username.setError("Der Benutzername darf keine Leerzeichen enthalten");
            valid = false;
        } else {
            username.setError(null);
        }

        if (check_password.isEmpty()) {
            password.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else  if (password.length() < 4) {
            password.setError("Das Passwort muss mindestens 4 Zeichen lang sein");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    void onRegisterSuccess(User user){ // Working
        registerButton.setEnabled(true);
        setResult(RESULT_OK,null);
        startActivity(new Intent(this, MainActivity.class));
        MainActivity.user = user;
        finish();
    }

    void onRegisterFailed(){ // Working
        Toast.makeText(getBaseContext(), "Registrieren fehlgeschlagen - Benutzername existiert bereits", Toast.LENGTH_LONG).show();
        registerButton.setEnabled(true);
    }

}

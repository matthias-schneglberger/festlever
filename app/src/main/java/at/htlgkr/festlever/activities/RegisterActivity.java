package at.htlgkr.festlever.activities;

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

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;

public class RegisterActivity extends AppCompatActivity {
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
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
    }

    void initializeViews(){
        Log.d(TAG, "initializeViews");
        email = findViewById(R.id.activity_register_email);
        username = findViewById(R.id.activity_register_username);
        password = findViewById(R.id.activity_register_password);
        registerButton = findViewById(R.id.activity_register_register_button);
        loginText = findViewById(R.id.activity_register_to_login_text);
    }

    void register(){
        Log.d(TAG, "register");

        if(!validate()){
            onRegisterFailed();
            return;
        }

        registerButton.setEnabled(false);

        String check_email = email.getText().toString();
        String check_username = username.getText().toString();
        String check_password = password.getText().toString();

        //Register-Logic
    }

    public boolean validate() {
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

    void onRegisterSuccess(){
        registerButton.setEnabled(true);
        setResult(RESULT_OK,null);
        finish();
    }

    void onRegisterFailed(){
        Toast.makeText(getBaseContext(), "Registrieren fehlgeschlagen", Toast.LENGTH_LONG).show();
        registerButton.setEnabled(true);
    }
}

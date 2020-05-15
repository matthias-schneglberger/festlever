package at.htlgkr.festlever.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
import at.htlgkr.festlever.objects.User;

public class LoginActivity extends AppCompatActivity {
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private EditText username_email;
    private EditText password;
    private CheckBox rememberMe;
    private Button registerButton;
    private Button loginButton;
    private TextView passwordForgotten;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();

        checkIfUserIsInRememberFile();
        //Register-Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Switch to Register");
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        //Login-Button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        //Password-Forgotten
        passwordForgotten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    void initializeViews(){
        Log.d(TAG, "initializeViews");
        username_email = findViewById(R.id.activity_login_username_email);
        password = findViewById(R.id.activity_login_password);
        rememberMe = findViewById(R.id.activity_login_checkbox);
        registerButton = findViewById(R.id.activity_login_register_button);
        loginButton = findViewById(R.id.activity_login_button);
        passwordForgotten = findViewById(R.id.activity_login_password_forgotten);
    }

    void login(){
        Log.d(TAG, "login");

        if(!validate()){
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        String check_username_email = username_email.getText().toString();
        String check_password = password.getText().toString();

        //Login Implementation + Checkbox
    }

    public boolean validate() {
        boolean valid = true;

        String check_username_email = username_email.getText().toString();
        String check_password = password.getText().toString();

        if (check_username_email.isEmpty()) {
            username_email.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else {
            username_email.setError(null);
        }

        if (check_password.isEmpty()) {
            password.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        }else if (password.length() < 4) {
            password.setError("Das Passwort muss mindestens 4 Zeichen lang sein");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    void onLoginSuccess(){
        loginButton.setEnabled(true);
        finish();
    }

    void onLoginFailed(){
        Toast.makeText(getBaseContext(), "Login fehlgeschlagen", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    void changePassword(){
        Log.d(TAG, "changePassword");
        //Change Password
    }

    void checkIfUserIsInRememberFile(){
        //Skip Login Try
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SIGNUP){
            if(resultCode == RESULT_OK){
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

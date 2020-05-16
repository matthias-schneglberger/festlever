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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.PasswordToHash;
import at.htlgkr.festlever.objects.User;

public class LoginActivity extends AppCompatActivity {
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private PasswordToHash passwordToHash = new PasswordToHash();
    private MessageDigest messageDigest;
    private final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private EditText username_email;
    private EditText password;
    private CheckBox rememberMe;
    private Button registerButton;
    private Button loginButton;
    private TextView passwordForgotten;

    File rememberMeFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rememberMeFile = new File("rememberMe.txt");
        if(!rememberMeFile.exists()){
            try {
                rememberMeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        initializeViews();

        checkIfUserIsInRememberFile(rememberMeFile);
        //Register-Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Switch to Register");
                startActivityForResult(new Intent(getApplicationContext(), RegisterActivity.class), REQUEST_SIGNUP);
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

        final String check_username_email = username_email.getText().toString();
        final String check_password = passwordToHash.bytesToHex(messageDigest.digest(password.getText().toString().getBytes()));

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Einloggen...");
        progressDialog.show();

        List<User> userList = fireBaseCommunication.getAllUsers();
        User user = new User();
        for(User u: userList){
            if((check_username_email.equals(u.getUsername()) && check_password.equals(check_password)) || (check_username_email.equals(u.getEmail()) && check_password.equals(check_password))){
                user = u;
            }
        }
        if(user.getUsername()!=null&&user.getEmail()!=null&&user.getPassword()!=null){
            onLoginSuccess(user);
            if(rememberMe.isChecked()){
                writeToRememberMeFile(user,rememberMeFile);
            }
        }
        else{
            onLoginFailed();
        }
        progressDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;

        String check_username_email = username_email.getText().toString();
        String check_password = password.getText().toString();

        if (check_username_email.isEmpty()) {
            username_email.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        } else if (check_username_email.trim().contains(" ")) {
            username_email.setError("Dieses Feld darf kein Leerzeichen enthalten");
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

    void onLoginSuccess(User user){
        loginButton.setEnabled(true);
        startActivity(new Intent(this, MainActivity.class).putExtra("user",user));
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

    void checkIfUserIsInRememberFile(File file){
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String json = reader.readLine();
            Gson gson = new Gson();
            User user = gson.fromJson(json, User.class);
            List<User> userList = fireBaseCommunication.getAllUsers();
            for (User u: userList){
                if(u.getUsername().equals(user.getUsername()) && u.getEmail().equals(user.getEmail()) && u.getPassword().equals(user.getPassword())){
                    onLoginSuccess(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeToRememberMeFile(User user, File file){
        Gson gson = new Gson();
        String json = gson.toJson(user);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))){
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

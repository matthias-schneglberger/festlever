package at.htlgkr.festlever.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.PasswordToHash;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class NewPasswordActivity extends AppCompatActivity {
    private final String TAG = "NewPasswordActivity";

    private User user;

    private EditText passwordInput;
    private EditText checkIfPasswordIsCorrect;
    private Button acceptButton;

    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        initalizeViews();

        try{
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            user = (User) bundle.get("user");
        }catch (NullPointerException ignored){}

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    void initalizeViews(){
        passwordInput = findViewById(R.id.activity_new_password_passwordInput);
        checkIfPasswordIsCorrect = findViewById(R.id.activity_new_password_checkIfPasswordIsCorrect);
        acceptButton = findViewById(R.id.activity_new_password_accept);
    }

    void changePassword(){
        if(validate()){
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            user.setPassword(new PasswordToHash().bytesToHex(messageDigest.digest(passwordInput.getText().toString().getBytes())));
            fireBaseCommunication.updateUser(user);
            setResult(RESULT_OK,null);
            startActivity(new Intent(this, MainActivity.class));
            MainActivity.user = user;
            finish();
        }
    }

    boolean validate() {
        boolean valid = true;

        if (passwordInput.getText().toString().isEmpty()) {
            passwordInput.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        }else if (passwordInput.getText().toString().length() < 4) {
            passwordInput.setError("Das Passwort muss mindestens 4 Zeichen lang sein");
            valid = false;
        } else {
            passwordInput.setError(null);
        }

        if (checkIfPasswordIsCorrect.getText().toString().isEmpty()) {
            checkIfPasswordIsCorrect.setError("Dieses Feld muss ausgefüllt sein");
            valid = false;
        }else if (checkIfPasswordIsCorrect.getText().toString().length() < 4) {
            checkIfPasswordIsCorrect.setError("Das Passwort muss mindestens 4 Zeichen lang sein");
            valid = false;
        } else {
            checkIfPasswordIsCorrect.setError(null);
        }

        if(!passwordInput.getText().toString().equals(checkIfPasswordIsCorrect.getText().toString())){
            passwordInput.setError("Die Passwörter stimmen nicht überein");
            checkIfPasswordIsCorrect.setError("Die Passwörter stimmen nicht überein");
            valid = false;
        }
        else {
            checkIfPasswordIsCorrect.setError(null);
            passwordInput.setError(null);
        }

        return valid;
    }
}

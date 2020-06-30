package at.htlgkr.festlever.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.mail.GMailSender;
import at.htlgkr.festlever.objects.User;

public class ForgotPasswordActivity extends AppCompatActivity {
    private final String TAG = "ForgottPasswortActivity";

    FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    private EditText emailInput;
    private Button sendEmail;
    private TextView timer;
    private EditText codeInput;
    private Button acceptButton;
    private Button sendEmailAgain;
    private ProgressBar progressBar;

    CountDownTimer countDownTimer;

    private final String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";

    private String code;
    private long timeLeft = 300000;
    int tries;

    private User user;
    private List<User> userList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        code = "";
        tries = 3;
        user = new User();

        userList = fireBaseCommunication.getAllUsers(true);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailInput.getText().toString().isEmpty() && userList.stream().filter(a -> a.getEmail().equals(emailInput.getText().toString())).collect(Collectors.toList()).size() == 1){
                    code = generateRandomString();
                    sendEmailWithCode(emailInput.getText().toString());
                    startTimer();
                    sendEmail.setClickable(false);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Fehler",Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendEmailAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailAgain.setClickable(false);
                sendEmailWithCode(emailInput.getText().toString());
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compareCodeInputWithCode();
            }
        });
    }

    void compareCodeInputWithCode(){
        if(codeInput.getText().toString().equals(code)){
            codeInput.setError(null);
            for(User u : userList){
                if(u.getEmail().equals(emailInput.getText().toString())){
                    startActivity(new Intent(this, NewPasswordActivity.class).putExtra("user",u));
                    countDownTimer.cancel();
                    finish();
                    break;
                }
            }
        }
        else{
            if(tries == 1){
                Toast.makeText(getApplicationContext(), "Keine Versuche mehr möglich", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                finish();
            }
            else{
                tries--;
                Toast.makeText(getApplicationContext(), "Falsche Eingabe. Sie haben noch " + tries + " Versuche übgrig",Toast.LENGTH_SHORT).show();
                codeInput.setError("Falsche Eingabe");
            }
        }
    }

    void initializeViews(){
        emailInput = findViewById(R.id.activity_forgot_password_email);
        sendEmail = findViewById(R.id.activity_forgot_password_sendEmail);
        timer = findViewById(R.id.activity_forgot_password_timer);
        codeInput = findViewById(R.id.activity_forgot_password_code);
        acceptButton = findViewById(R.id.activity_forgot_password_accept);
        sendEmailAgain = findViewById(R.id.activity_forgot_password_sendEmailAgain);
        progressBar = findViewById(R.id.activity_forgot_password_progressBar);
    }

    void sendEmailWithCode(String email){
        try {
            GMailSender sender = new GMailSender("festlever.project@gmail.com","2lC4^%8yjeJNr%n9");
            sender.sendMail("Passwort zurücksetzen", "Verifizierungscode = " + code, "festlever.project@gmail.com",email);
            Toast.makeText(getApplicationContext(), "E-Mail wurde an " + email + " gesendet",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    String generateRandomString(){
        StringBuilder sb = new StringBuilder(6);
        for(int i = 0; i < 6; i++){
            sb.append(AlphaNumericString.charAt((int)(AlphaNumericString.length() * Math.random())));
        }
        return sb.toString();
    }

    void startTimer(){
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onFinish() {
                timer.setTextColor(R.color.negativeColor);
                Toast.makeText(getApplicationContext(), "Zeit abgelaufen!",Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                finish();
            }
        }.start();
    }

    void updateTimer(){
        String timelefta = "" + (int) timeLeft / 60000 +  ":";
        if((int) timeLeft % 60000 / 1000 < 10) timelefta += "0";
        timelefta += (int) timeLeft % 60000 / 1000;

        progressBar.setProgress((int) timeLeft / 1000);

        timer.setText(timelefta);
    }
}

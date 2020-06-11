package at.htlgkr.festlever.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.mail.GMailSender;

public class ForgotPasswordActivity extends AppCompatActivity {
    private final String TAG = "ForgottPasswortActivity";

    FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    private EditText emailInput;
    private Button sendEmail;
    private TextView timer;
    private EditText codeInput;
    private Button acceptButton;
    private Button sendEmailAgain;

    private final String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

    private String code;
    private long timeLeft = 300000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        code = "";

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!emailInput.getText().toString().isEmpty() && fireBaseCommunication.getAllUsers().stream().filter(a -> a.getEmail().equals(emailInput)).collect(Collectors.toList()).size() == 1){
                    sendEmailWithCode(emailInput.getText().toString());
                    sendEmail.setClickable(false);
//                }
            }
        });
    }

    void initializeViews(){
        emailInput = findViewById(R.id.activity_forgot_password_email);
        sendEmail = findViewById(R.id.activity_forgot_password_sendEmail);
        timer = findViewById(R.id.activity_forgot_password_timer);
        codeInput = findViewById(R.id.activity_forgot_password_code);
        acceptButton = findViewById(R.id.activity_forgot_password_accept);
        sendEmailAgain = findViewById(R.id.activity_forgot_password_sendEmailAgain);
    }

    void sendEmailWithCode(String email){
        code = generateRandomString();

        try {
            GMailSender sender = new GMailSender("festlever.project@gmail.com","2lC4^%8yjeJNr%n9");
            sender.sendMail("Passwort zurücksetzen", "Verifizierungscode = " + code, "festlever.project@gmail.com",email);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        startTimer();
    }

    String generateRandomString(){
        StringBuilder sb = new StringBuilder(6);
        for(int i = 0; i < 6; i++){
            sb.append(AlphaNumericString.charAt((int)(AlphaNumericString.length() * Math.random())));
        }
        return sb.toString();
    }

    void startTimer(){
        CountDownTimer countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onFinish() {
                timer.setTextColor(R.color.negativeColor);

            }
        }.start();
    }

    void updateTimer(){
        String timelefta = "" + (int) timeLeft / 60000 +  ":";
        if((int) timeLeft % 60000 / 1000 < 10) timelefta += "0";
        timelefta += (int) timeLeft % 60000 / 1000;

        timer.setText(timelefta);
    }
}

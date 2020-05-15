package at.htlgkr.festlever.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.User;

public class LoginActivity extends AppCompatActivity {
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: " + fireBaseCommunication.getUser("mschneglberger").getUsername());

    }
}

package at.htlgkr.festlever.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import at.htlgkr.festlever.logic.FireBaseCommunication;

public class UpdateService extends Service {

    Thread thread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        thread = new Thread(this::doInBackground);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }

    private void doInBackground(){
        while(true){
            long whaitUntil = System.currentTimeMillis() + 300000;
            while(System.currentTimeMillis() < whaitUntil){}

            new FireBaseCommunication().getAllEvents(true);
            new FireBaseCommunication().getAllUsers(true);
        }
    }
}

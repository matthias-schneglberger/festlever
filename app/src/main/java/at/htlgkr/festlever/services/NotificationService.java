package at.htlgkr.festlever.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "festleverNotificationChannel";
    private static final String EVENTID_FILE = "eventNotifications.txt";
    private static final String USERNAME_FILE = "userNotifications.txt";
    private static final String TAG = "NotificationService";

    private Thread worker;
    private SharedPreferences prefs;
    private String usernameOfCurrentUser;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        worker = new Thread(this::doInBackground);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        clearFiles();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("username")) {
            usernameOfCurrentUser = intent.getStringExtra("username");
        }
//        else{
//            throw new Exception("kein Benutzername als .putExtra");
//        }
        worker.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        worker.interrupt();
        worker = null;
        super.onDestroy();
    }

    private void doInBackground(){ //Threaded
        CharSequence name = getString(R.string.service_notification_channelName);
        String description = getString(R.string.service_notification_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(
                NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        boolean requestsNotify = prefs.getBoolean("preference_requests", true);
        boolean startEventNotify = prefs.getBoolean("preference_startEvents", true);

        while(requestsNotify || startEventNotify){

            FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
            List<Event> events = fireBaseCommunication.getAllEvents();
            List<User> users = fireBaseCommunication.getAllUsers();

            if(requestsNotify){

                //Friend Request
                for(User u : users){
                    if(u.getUsername().equals(usernameOfCurrentUser)){
                        if(!u.getFriendRequests().isEmpty()/* && is noch nicht sfjasdlf*/){
                            for(String friendReq : u.getFriendRequests()){

                                if(!userIsInFile(friendReq)){
                                    addToUserFile(friendReq);
                                    Notification notification = new NotificationCompat.Builder(
                                            this, CHANNEL_ID)
                                            .setSmallIcon(android.R.drawable.star_big_on)
                                            .setColor(Color.RED)
                                            .setContentTitle("Neue Freundschaftsanfrage von:" + friendReq)
                                            .setContentText(friendReq + " hat dir eine Freundschaftsanfrage gesendet!")
                                            .setWhen(System.currentTimeMillis())
                                            .setPriority(NotificationCompat.PRIORITY_HIGH).build();

                                    notificationManager.notify(4711, notification);
                                }


                            }
                        }
                    }
                }

                //Event Request
                for(User u : users){
                    if(u.getUsername().equals(usernameOfCurrentUser)){
                        if(!u.getEventRequests().isEmpty()/* && is noch nicht sfjasdlf*/){
                            for(String eventReq : u.getEventRequests()){
                                if(!eventIsInFile(eventReq)){
                                    addToEventFile(eventReq);
                                    Notification notification = new NotificationCompat.Builder(
                                            this, CHANNEL_ID)
                                            .setSmallIcon(android.R.drawable.star_big_on)
                                            .setColor(Color.RED)
                                            .setContentTitle("Neue Eventanfrage")
                                            .setContentText("Du wurdest zu einem Event eingeladen!")
                                            .setWhen(System.currentTimeMillis())
                                            .setPriority(NotificationCompat.PRIORITY_HIGH).build();

                                    notificationManager.notify(4711, notification);
                                }


                            }
                        }
                    }
                }

            }


            //start Event Notify
            if(startEventNotify){
                for(Event event : events){
                    if(event.getAcceptUser().contains(usernameOfCurrentUser)){
                        if(LocalDate.parse(event.getDate(),DateTimeFormatter.ofPattern("dd.MM.yyyy")).isEqual(LocalDate.now()) && eventIsInFile(event.getId())){
                            addToEventFile(event.getId());
                            Notification notification = new NotificationCompat.Builder(
                                    this, CHANNEL_ID)
                                    .setSmallIcon(android.R.drawable.star_big_on)
                                    .setColor(Color.RED)
                                    .setContentTitle(event.getTitle() + " ist heute")
                                    .setWhen(System.currentTimeMillis())
                                    .setPriority(NotificationCompat.PRIORITY_HIGH).build();

                            notificationManager.notify(4711, notification);

                        }
                    }
                }
            }

            //Whait 5 mins (in while --> no whait in android?? --> I'll google)
//            try {
//                wait(300000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            long whaitUntil = System.currentTimeMillis() + 300000;
            while(System.currentTimeMillis() < whaitUntil){}
        }
    }

    public void addToEventFile(String eventId){

        try {
            FileOutputStream fos = openFileOutput(EVENTID_FILE, MODE_PRIVATE | MODE_APPEND);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fos));
            out.println(eventId + "\n");
            out.flush();
            out.close();
        } catch (FileNotFoundException exp) {
            Log.d(TAG, exp.getStackTrace().toString());
        }
    }
    public void addToUserFile(String username){
        try {
            FileOutputStream fos = openFileOutput(USERNAME_FILE, MODE_PRIVATE | MODE_APPEND);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fos));
            out.println(username + "\n");
            out.flush();
            out.close();
        } catch (FileNotFoundException exp) {
            Log.d(TAG, exp.getStackTrace().toString());
        }
    }

    public boolean eventIsInFile(String eventId){
        try {
            FileInputStream fis = openFileInput(EVENTID_FILE);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = in.readLine()) != null ) {
                buffer.append(line);
            }
            in.close();
            return buffer.toString().contains(eventId);
        } catch (IOException exp) {
            Log.d(TAG, exp.getStackTrace().toString());
        }
        return false;
    }
    public boolean userIsInFile(String username){
        try {
            FileInputStream fis = openFileInput(USERNAME_FILE);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = in.readLine()) != null ) {
                buffer.append(line);
            }
            in.close();
            return buffer.toString().contains(username);
        } catch (IOException exp) {
            Log.d(TAG, exp.getStackTrace().toString());
        }
        return false;
    }

    public void clearEventFile(){
        try {
            FileOutputStream fos = openFileOutput(EVENTID_FILE, MODE_PRIVATE);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fos));
            out.println("");
            out.flush();
            out.close();
        } catch (FileNotFoundException exp) {
            Log.d(TAG, exp.getStackTrace().toString());
        }
    }
    public void clearUserFile(){
        try {
            FileOutputStream fos = openFileOutput(USERNAME_FILE, MODE_PRIVATE);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fos));
            out.println("");
            out.flush();
            out.close();
        } catch (FileNotFoundException exp) {
            Log.d(TAG, exp.getStackTrace().toString());
        }
    }

    public void clearFiles(){
        clearEventFile();
        clearUserFile();
    }
}

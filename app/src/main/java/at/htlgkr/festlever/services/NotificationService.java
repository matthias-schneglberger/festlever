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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "festleverNotificationChannel";
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
        worker = new Thread(this::doWork);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

    private void doWork(){
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
                        if(!u.getFriendRequests().isEmpty() /* && is noch nicht sfjasdlf*/){
                            for(String friendReq : u.getFriendRequests()){
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

                //Event Request
                for(User u : users){
                    if(u.getUsername().equals(usernameOfCurrentUser)){
                        if(!u.getEventRequests().isEmpty() /* && is noch nicht sfjasdlf*/){
                            for(String eventReq : u.getEventRequests()){
                                Notification notification = new NotificationCompat.Builder(
                                        this, CHANNEL_ID)
                                        .setSmallIcon(android.R.drawable.star_big_on)
                                        .setColor(Color.RED)
                                        .setContentTitle("Neue Eventanfrage zu:" + eventReq)
                                        .setContentText("Du wurdest zu einem Event eingeladen!")
                                        .setWhen(System.currentTimeMillis())
                                        .setPriority(NotificationCompat.PRIORITY_HIGH).build();

                                notificationManager.notify(4711, notification);
                            }
                        }
                    }
                }

            }

            if(startEventNotify){


                for(Event event : events){
                    if(event.getAcceptUser().contains(usernameOfCurrentUser)){
                        if(LocalDate.parse(event.getDate(),DateTimeFormatter.ofPattern("dd.MM.yyyy")).isEqual(LocalDate.now())){

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
}

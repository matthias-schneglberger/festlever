package at.htlgkr.festlever.logic;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class UserEventsPuffer {

    public FragmentRefresh getFragmentRefreshListener() {
        return fragmentRefresh;
    }

    public void setFragmentRefreshView(FragmentRefresh fragmentRefresh) {
        this.fragmentRefresh = fragmentRefresh;
    }

    private FragmentRefresh fragmentRefresh;

    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    static List<User> users = new ArrayList<>();

    static List<Event> events = new ArrayList<>();

    public List<User> getUsers(){
        return users;
    }

    public List<Event> getEvents(){
        return events;
    }

    public UserEventsPuffer(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        users = fireBaseCommunication.getAllUsers();
//                        events = fireBaseCommunication.getAllEvents();
//                        update();
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
    }

    void update(){
        if(getFragmentRefreshListener()!=null){
            getFragmentRefreshListener().onEventsRefresh();
            getFragmentRefreshListener().onUserRefresh();
        }
    }

    public interface FragmentRefresh{
        void onUserRefresh();
        void onEventsRefresh();
        void onUserEventsRefresh();
    }
}
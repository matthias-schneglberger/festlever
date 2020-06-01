package at.htlgkr.festlever.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.activities.MainActivity;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Adapter_eventRequests extends BaseAdapter {
    private List<Event> events = new ArrayList();
    private List<User> users = new ArrayList();
    private int layoutId;
    private LayoutInflater inflater;
    private User user;
    private Context context;

    private Button acceptButton;
    private Button rejectButton;

    public Adapter_eventRequests(Context ctx, int layoutId, List<Event> events, List<User> users, User user) {
        this.events = events;
        this.users = users;
        this.layoutId = layoutId;
        this.user = user;
        context = ctx;
        this.inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Event event = events.get(i);
        User createrUser = new User();
        for (User u : users) {
            if (u.getUsername().equals(event.getCreater())) {
                createrUser = u;
            }
        }

        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;
        ((TextView) listItem.findViewById(R.id.activity_event_requests_user_listitem_username)).setText(createrUser.getUsername());
        //init Buttons
        acceptButton = listItem.findViewById(R.id.activity_event_requests_user_listitem_accept);
        rejectButton = listItem.findViewById(R.id.activity_event_requests_user_listitem_reject);

        //accept Button
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAcceptRequest(user, event);

                TextView feedbackView = listItem.findViewById(R.id.activity_event_requests_user_listitem_feedbackBox);
                acceptButton.setVisibility(View.INVISIBLE);
                rejectButton.setVisibility(View.INVISIBLE);
                feedbackView.setVisibility(View.VISIBLE);
                feedbackView.setText("Angenommen");
                feedbackView.setBackgroundResource(R.color.positiveColor);
            }
        });

        //reject Button

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRejectRequest(user, event);

                TextView feedbackView = listItem.findViewById(R.id.activity_event_requests_user_listitem_feedbackBox);
                acceptButton.setVisibility(View.INVISIBLE);
                rejectButton.setVisibility(View.INVISIBLE);
                feedbackView.setVisibility(View.VISIBLE);
                feedbackView.setText("Abgelehnt");
                feedbackView.setBackgroundResource(R.color.negativeColor);
            }
        });

        return listItem;
    }

    private void sendAcceptRequest(User user, Event event) {
        FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
        List<String> eventRequests = user.getEventRequests();
        List<String> eventsJoined = user.getJoinedEvents();
        if (eventRequests.contains(event.getId()) && !eventsJoined.contains(event.getId())) {
            eventRequests.remove(event.getId());
            user.setEventRequests(eventRequests);
            eventsJoined.add(event.getId());
            user.setJoinedEvents(eventsJoined);
            List<String> acceptUser = event.getAcceptUser();
            acceptUser.add(user.getUsername());
            event.setAcceptUser(acceptUser);

            fireBaseCommunication.updateUser(user);
            fireBaseCommunication.updateEvent(event);

            MainActivity.user = user;

            Snackbar.make(((Activity) context).findViewById(R.id.activity_event_requests_listView), "Du nimmst jetzt Teil", BaseTransientBottomBar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(((Activity) context).findViewById(R.id.activity_event_requests_listView), "Annehmen fehlgeschlagen", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    private void sendRejectRequest(User user, Event event) {
        FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
        List<String> eventRequests = user.getEventRequests();

        if (eventRequests.contains(event.getId())) {

            eventRequests.remove(event.getId());
            user.setEventRequests(eventRequests);
            fireBaseCommunication.updateUser(user);

            MainActivity.user = user;

            Snackbar.make(((Activity) context).findViewById(R.id.activity_event_requests_listView), "Eventanfrage abgelehnt", BaseTransientBottomBar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(((Activity) context).findViewById(R.id.activity_event_requests_listView), "Fehler beim ablehnen", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }
}
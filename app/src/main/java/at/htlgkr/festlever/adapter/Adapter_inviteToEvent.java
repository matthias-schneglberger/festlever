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
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Adapter_inviteToEvent extends BaseAdapter {
    private List<User> users = new ArrayList();
    private int layoutId;
    private LayoutInflater inflater;
    private Event event;
    private Context context;

    public Adapter_inviteToEvent(Context ctx, int layoutId, List<User> users, Event event) {
        this.users = users;
        this.layoutId = layoutId;
        this.event = event;
        context = ctx;
        this.inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        User user = users.get(i);
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;
        ((TextView) listItem.findViewById(R.id.activity_find_friends_user_listitem_username)).setText(user.getUsername());

        //request Button
        Button requestButton;
        requestButton = listItem.findViewById(R.id.activity_find_friends_user_listitem_friendsRequest);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(user);
            }
        });

        if(user.getEventRequests().contains(event.getId())){
            requestButton.setText("Gesendet");
            requestButton.setClickable(false);
        }
        if(user.getJoinedEvents().contains(event.getId())){
            requestButton.setText("Nimmt teil");
            requestButton.setClickable(false);
        }

        return listItem;
    }

    private void sendRequest(User user){
        FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
        List<String> eventRequests = user.getEventRequests();

        if(eventRequests.contains(event.getId())){
            Snackbar.make(((Activity)context).findViewById(R.id.activity_find_friends_allUsers), user.getUsername() + " wurde bereits eine gesendet", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        else{
            eventRequests.add(event.getId());
            user.setEventRequests(eventRequests);
            fireBaseCommunication.updateUser(user);

            Snackbar.make(((Activity)context).findViewById(R.id.activity_find_friends_allUsers), user.getUsername() + " wurde eine Einladung gesendet", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }
}

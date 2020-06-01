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
import at.htlgkr.festlever.objects.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Adapter_friendRequests extends BaseAdapter {
    private List<User> users = new ArrayList();
    private int layoutId;
    private LayoutInflater inflater;
    private User origUser;
    private Context context;
    private Button acceptButton;
    private Button rejectButton;
    public Adapter_friendRequests(Context ctx, int layoutId, List<User> users, User origUser) {
        this.users = users;
        this.layoutId = layoutId;
        this.origUser = origUser;
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
        ((TextView) listItem.findViewById(R.id.activity_friend_requests_user_listitem_username)).setText(user.getUsername());

        //accept Button

        acceptButton = listItem.findViewById(R.id.activity_friend_requests_user_listitem_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAcceptRequest(user);
            }
        });

        //reject Button
        rejectButton = listItem.findViewById(R.id.activity_friend_requests_user_listitem_reject);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRejectRequest(user);
            }
        });

        return listItem;
    }

    private void sendAcceptRequest(User user){
        FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
        List<String> friendRequests = origUser.getFriendRequests();
        List<String> friends = origUser.getFriends();
        if(friendRequests.contains(user.getUsername()) && !friends.contains(user.getUsername())){
            friendRequests.remove(user.getUsername());
            origUser.setFriendRequests(friendRequests);
            friends.add(user.getUsername());
            origUser.setFriends(friends);
            List<String> userFriends = user.getFriends();
            userFriends.add(origUser.getUsername());
            user.setFriends(userFriends);
            fireBaseCommunication.updateUser(user);
            fireBaseCommunication.updateUser(origUser);

            MainActivity.user = origUser;

            rejectButton.setVisibility(View.INVISIBLE);
            acceptButton.setClickable(false);
            acceptButton.setText("Angenommen");
            Snackbar.make(((Activity)context).findViewById(R.id.activity_friend_requests_listView), "Du und " + user.getUsername() + " seid jetzt befreundet", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        else{
            Snackbar.make(((Activity)context).findViewById(R.id.activity_friend_requests_listView), "Annehmen fehlgeschlagen", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    private void sendRejectRequest(User user){
        FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
        List<String> friendRequests = origUser.getFriendRequests();

        if(friendRequests.contains(user.getUsername())){

            friendRequests.remove(user.getUsername());
            origUser.setFriendRequests(friendRequests);
            fireBaseCommunication.updateUser(origUser);

            MainActivity.user = origUser;

            acceptButton.setVisibility(View.GONE);
            rejectButton.setClickable(false);
            rejectButton.setText("Abgelehnt");
            Snackbar.make(((Activity)context).findViewById(R.id.activity_friend_requests_listView), "Freundschaftsanfrage abgelehnt", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        else{
            Snackbar.make(((Activity)context).findViewById(R.id.activity_friend_requests_listView), "Fehler beim ablehnen", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }
}

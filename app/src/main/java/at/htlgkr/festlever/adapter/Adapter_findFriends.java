package at.htlgkr.festlever.adapter;

import android.annotation.SuppressLint;
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

public class Adapter_findFriends extends BaseAdapter {
    private final String TAG = "Adapter_findFriends";

    private List<User> users = new ArrayList();
    private int layoutId;
    private LayoutInflater inflater;
    private User origUser;
    private Context context;

    Button requestButton;

    public Adapter_findFriends(Context ctx, int layoutId, List<User> users, User origUser) {
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

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        User user = users.get(i);
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;
        ((TextView) listItem.findViewById(R.id.activity_find_friends_user_listitem_username)).setText(user.getUsername());

        //request Button
        requestButton = listItem.findViewById(R.id.activity_find_friends_user_listitem_friendsRequest);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(user, view);
            }
        });

        if(MainActivity.user.getFriends().contains(user.getUsername())){
            requestButton.setText("Freunde");
            requestButton.setBackgroundColor(R.color.alreadyColor);
            requestButton.setClickable(false);
        }
        else if(user.getFriendRequests().contains(MainActivity.user.getUsername())){
            requestButton.setText("Gesendet");
            requestButton.setBackgroundColor(R.color.alreadyColor);
            requestButton.setClickable(false);
        }

        return listItem;
    }

    @SuppressLint("ResourceAsColor")
    private void sendRequest(User user, View view){
        FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
        List<String> friendRequests = user.getFriendRequests();

        if(friendRequests.contains(origUser.getUsername())){
            Snackbar.make(((Activity)context).findViewById(R.id.activity_find_friends_allUsers), "Bereits eine Anfrage an " + user.getUsername() + " versendet", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        else{


            Button requestButton2 = view.findViewById(R.id.activity_find_friends_user_listitem_friendsRequest);
            requestButton2.setText("Gesendet");
            requestButton2.setBackgroundColor(R.color.alreadyColor);
            requestButton2.setClickable(false);

            friendRequests.add(origUser.getUsername());
            user.setFriendRequests(friendRequests);
            fireBaseCommunication.updateUser(user);

            Snackbar.make(((Activity)context).findViewById(R.id.activity_find_friends_allUsers), user.getUsername() + " wurde eine Anfrage gesendet", BaseTransientBottomBar.LENGTH_SHORT).show();

        }
    }
}

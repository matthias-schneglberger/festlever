package at.htlgkr.festlever.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.activities.EventDetailsActivity;
import at.htlgkr.festlever.adapter.Adapter_event;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_USER = "section_user";

    private int index;
    private User user;
    private View view;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    static MainFragment newInstance(int index, User user) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        bundle.putSerializable(ARG_SECTION_USER,user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get User and Index from Bundle
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        user = (User) getArguments().getSerializable(ARG_SECTION_USER);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView eventsView = view.findViewById(R.id.fragment_main_event_listView);

        switch (index){
            case 0:
                eventsView.setAdapter(new Adapter_event(view.getContext(), R.layout.fragment_main_listview_item, fireBaseCommunication.getAllEvents().stream().filter(a -> a.isPublic()).collect(Collectors.toList()), fireBaseCommunication.getAllUsers(), false,user));
                break;

            case 1:
                eventsView.setAdapter(new Adapter_event(view.getContext(), R.layout.fragment_main_listview_item, fireBaseCommunication.getAllEvents().stream().filter(a -> !a.isPublic() && a.getAcceptUser().contains(user.getUsername())).collect(Collectors.toList()), fireBaseCommunication.getAllUsers(), false,user));
                break;

            case 2:
                eventsView.setAdapter(new Adapter_event(view.getContext(), R.layout.fragment_main_listview_item, fireBaseCommunication.getAllEvents().stream().filter(a -> a.getCreater().equals(user.getUsername())).collect(Collectors.toList()), fireBaseCommunication.getAllUsers(), true,user));
                break;
        }

        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                startActivity(new Intent(getActivity(), EventDetailsActivity.class).putExtra("user",user).putExtra("event",event));
            }
        });

        return view;
    }
}
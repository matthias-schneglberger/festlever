package at.htlgkr.festlever.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.activities.EventDetailsActivity;
import at.htlgkr.festlever.adapter.Adapter_event;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.UserEventsPuffer;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

public class ShowProfileFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_USER = "section_user";

    private int index;
    private User user;
    private View view;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();
    private UserEventsPuffer userEventsPuffer = new UserEventsPuffer();

    private List<Event> eventList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Event> currentDisplayedEvents = new ArrayList<>();

    ListView eventsView;

    public ShowProfileFragment() {
        // Required empty public constructor
    }

    static ShowProfileFragment newInstance(int index, User user) {
        ShowProfileFragment fragment = new ShowProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        bundle.putSerializable(ARG_SECTION_USER,user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        index = getArguments().getInt(ARG_SECTION_NUMBER);
        user = (User) getArguments().getSerializable(ARG_SECTION_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_show_profile, container, false);

        eventsView = view.findViewById(R.id.fragment_show_profile_listView);

        eventList = fireBaseCommunication.getAllEvents();
        userList = fireBaseCommunication.getAllUsers();

        setUpListView();

        //SwipeRefreshLayout
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.fragment_show_profile_event_pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    public void run(){
                        update();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }).start();

            }
        });

        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                startActivity(new Intent(getActivity(), EventDetailsActivity.class).putExtra("user",user).putExtra("event",event));
            }
        });

        return view;
    }

    void setUpListView(){
        switch (index){
            case 0:
                currentDisplayedEvents = eventList.stream().filter(a -> a.getCreater().equals(user.getUsername())).collect(Collectors.toList());
                break;
            case 1:
                currentDisplayedEvents = eventList.stream().filter(a -> a.getAcceptUser().contains(user.getUsername())).collect(Collectors.toList());
                break;
        }
        eventsView.setAdapter(new Adapter_event(view.getContext(), R.layout.fragment_main_listview_item, currentDisplayedEvents, userList, false,user));
    }

    public void update(){
        eventList = fireBaseCommunication.getAllEvents();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUpListView();
            }
        });
    }
}

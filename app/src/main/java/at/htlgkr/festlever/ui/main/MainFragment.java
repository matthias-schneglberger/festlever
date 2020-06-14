package at.htlgkr.festlever.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.activities.EventDetailsActivity;
import at.htlgkr.festlever.activities.MainActivity;
import at.htlgkr.festlever.adapter.Adapter_event;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.UserEventsPuffer;
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
    private UserEventsPuffer userEventsPuffer = new UserEventsPuffer();

    private List<Event> eventList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Event> currentDisplayedEvents = new ArrayList<>();

    Thread searchViewThread;

    private SharedPreferences prefs;
    private String region = "";

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    static MainFragment newInstance(int index, User user) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        bundle.putSerializable(ARG_SECTION_USER,user);
        fragment.setArguments(bundle);
        return fragment;
    }

    public MainFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get User and Index from Bundle
        index = getArguments().getInt(ARG_SECTION_NUMBER);
        user = (User) getArguments().getSerializable(ARG_SECTION_USER);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        region = prefs.getString("preference_region","Austria");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView eventsView = view.findViewById(R.id.fragment_main_event_listView);

        eventList = fireBaseCommunication.getAllEvents();
        userList = fireBaseCommunication.getAllUsers();

        setUpListView();

        searchViewThread = new Thread(this::doInBackground);
        searchViewThread.start();


        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                startActivity(new Intent(getActivity(), EventDetailsActivity.class).putExtra("user",user).putExtra("event",event));
            }
        });

        //SwipeRefreshLayout
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.fragment_main_event_pullToRefresh);
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

        return view;
    }

    @Override
    public void onDestroy() {
        searchViewThread.interrupt();
        searchViewThread = null;
        super.onDestroy();
    }

    void setUpListView(){
        ListView eventsView = view.findViewById(R.id.fragment_main_event_listView);
        boolean editEnabled = false;

        switch (index){
            case 0:
                currentDisplayedEvents = eventList.stream().filter(a -> a.isPublic() && a.getRegion().equals(region) && !LocalDate.parse(a.getDate(),dtf).isBefore(LocalDate.now())).collect(Collectors.toList());
                break;
            case 1:
                currentDisplayedEvents = eventList.stream().filter(a -> !a.isPublic() && a.getAcceptUser().contains(user.getUsername()) && a.getRegion().equals(region) && !LocalDate.parse(a.getDate(),dtf).isBefore(LocalDate.now())).collect(Collectors.toList());
                break;
            case 2:
                currentDisplayedEvents = eventList.stream().filter(a -> a.getCreater().equals(user.getUsername()) && a.getRegion().equals(region)).collect(Collectors.toList());
                editEnabled = true;
                break;
        }

        eventsView.setAdapter(new Adapter_event(view.getContext(), R.layout.fragment_main_listview_item, currentDisplayedEvents, userList, editEnabled,user));
    }

    public void update(){
        eventList = fireBaseCommunication.getAllEvents(true);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUpListView();
            }
        });
    }


    public void newSearchTerm(String searchTerm){
        if(getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Event> searchedEvents = currentDisplayedEvents.stream().filter(n -> n.getTitle().toLowerCase().contains(searchTerm.toLowerCase())).collect(Collectors.toList());

                ListView eventsView = view.findViewById(R.id.fragment_main_event_listView);


                if(index == 2){
                    eventsView.setAdapter(new Adapter_event(view.getContext(), R.layout.fragment_main_listview_item, searchedEvents, userList, true,user));
                }
                else{
                    eventsView.setAdapter(new Adapter_event(view.getContext(), R.layout.fragment_main_listview_item, searchedEvents, userList, false,user));
                }

            }
        });
    }

    private void doInBackground(){ //Threaded
        String lastSearchTerm = "";
        while(true){
            if(!MainActivity.searchTerm.equals(lastSearchTerm)){
                lastSearchTerm = MainActivity.searchTerm;
                newSearchTerm(MainActivity.searchTerm);
            }
        }
    }

}
package at.htlgkr.festlever.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import at.htlgkr.festlever.R;
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
    private ArrayList<Event> eventsList;

    public static MainFragment newInstance(int index, User user) {
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
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        index = bundle.getInt(ARG_SECTION_USER);
        user = (User) bundle.getSerializable(ARG_SECTION_USER);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);



        return root;
    }
}
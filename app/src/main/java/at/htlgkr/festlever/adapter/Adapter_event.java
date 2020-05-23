package at.htlgkr.festlever.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.objects.Event;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Adapter_event extends BaseAdapter {
    private List<Event> events = new ArrayList<>();
    private int layoutId;
    private LayoutInflater inflater;
    private boolean editsEnabled;

    public Adapter_event(Context ctx, int layoutId, List<Event> events, boolean editsEnabled) {
        this.events = events;
        this.layoutId = layoutId;
        this.editsEnabled = editsEnabled;
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
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;
        ImageView imageView = listItem.findViewById(R.id.fragment_main_listview_item_imageView);
        TextView eventName = listItem.findViewById(R.id.fragment_main_listview_item_nameOfEvent);
        TextView eventAddress = listItem.findViewById(R.id.fragment_main_listview_item_eventAddress);
        TextView timeUntilEvent = listItem.findViewById(R.id.fragment_main_listview_item_timeUntilEvent);
        TextView accepts = listItem.findViewById(R.id.fragment_main_listview_item_accepts);
        TextView entrance = listItem.findViewById(R.id.fragment_main_listview_item_entrance);
        TextView day = listItem.findViewById(R.id.fragment_main_listview_item_day);
        TextView month = listItem.findViewById(R.id.fragment_main_listview_item_month);
        ImageButton editButton = listItem.findViewById(R.id.fragment_main_listview_item_editButton);

        //My Event
        if(editsEnabled){
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        //Set all items
        if(event.getImage()!=null){
//            imageView.setImageBitmap(event.getImage());
        }
        eventName.setText(event.getTitle());
        //Event get Address ----------------
        //Event get timeUntil Event --------
//        accepts.setText(event.getAcceptUser().size()+"");
//        entrance.setText(event.getEntrance()+"");
//        day.setText(event.getDate().substring(0,2));
        //Event get Month ------------------

        return listItem;
    }
}

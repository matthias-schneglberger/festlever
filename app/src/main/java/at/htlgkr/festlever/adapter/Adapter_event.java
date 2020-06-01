package at.htlgkr.festlever.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import at.htlgkr.festlever.R;
import at.htlgkr.festlever.activities.EventCreateChangeActivity;
import at.htlgkr.festlever.activities.InviteToEventActivity;
import at.htlgkr.festlever.logic.FireBaseCommunication;
import at.htlgkr.festlever.logic.ImagePuffer;
import at.htlgkr.festlever.logic.LongLatAdressPuffer;
import at.htlgkr.festlever.logic.locationiqtasks.LongLatToAddressAsyncTask;
import at.htlgkr.festlever.objects.Event;
import at.htlgkr.festlever.objects.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Adapter_event extends BaseAdapter {
    private List<Event> events = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private int layoutId;
    private LayoutInflater inflater;
    private boolean editsEnabled;
    private Context context;
    private User user;
    private FireBaseCommunication fireBaseCommunication = new FireBaseCommunication();

    public Adapter_event(Context ctx, int layoutId, List<Event> events, List<User> users, boolean editsEnabled, User user) {
        this.context = ctx;
        this.events = events;
        this.layoutId = layoutId;
        this.editsEnabled = editsEnabled;
        this.inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.user = user;
        this.users = users;
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
        TextView numOfFriends = listItem.findViewById(R.id.fragment_main_listview_item_numOfFriends);

//        imageView.setVisibility(View.GONE);

        //My Event Buttons
        if(editsEnabled){
            editButton.setVisibility(View.VISIBLE);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu menu = new PopupMenu(context, view);
                    menu.inflate(R.menu.event_options_menue);
                    menu.setOnMenuItemClickListener( (item) -> {
                        if(item.getTitle().equals("Freunde einladen")){
                            context.startActivity(new Intent(context, InviteToEventActivity.class).putExtra("user",user).putExtra("event",event));
                        }
                        else if(item.getTitle().equals("Event bearbeiten")){
                            context.startActivity(new Intent(context, EventCreateChangeActivity.class).putExtra("event",event).putExtra("user",user));
                        }

                        else if(item.getTitle().equals("Event löschen")){
                            fireBaseCommunication.deleteEvent(event.getId());
                        }


                        return true;
                    });
                    menu.show();
                }
            });
        }

        //Set Image

        ImagePuffer imagePuffer = new ImagePuffer();

        if(event.getImage()!=null){
            if(imagePuffer.isStored(event.getImage())){
                imageView.setImageBitmap(imagePuffer.getImage(event.getImage()));
            }
            else{
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference();

//                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
                        storageReference.child(event.getImage()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                                imageView.setImageBitmap(bitmap);

                                imagePuffer.storeImage(event.getImage(), bitmap);
                            }
                        });
//                    }
//                });



            }


        }

        //Set Address
        LongLatAdressPuffer longLatAdressPuffer = new LongLatAdressPuffer();

        if(longLatAdressPuffer.isStored(event.getLongitude(), event.getLatitude())){
            eventAddress.setText(longLatAdressPuffer.getAddress(event.getLongitude(), event.getLatitude()));
        }
        else{
            LongLatToAddressAsyncTask longLatToAddressAsyncTask = new LongLatToAddressAsyncTask();
            longLatToAddressAsyncTask.execute(event.getLatitude(),event.getLongitude());
            try {
                String address = longLatToAddressAsyncTask.get();
                if(address!=null){
                    JSONObject jsonObject = new JSONObject(address);
                    String addressString = jsonObject.getString("road") + " " + jsonObject.getString("house_number") + ", " + jsonObject.getString("postcode");
                    eventAddress.setText(addressString);

                    longLatAdressPuffer.storeAdress(event.getLongitude(), event.getLatitude(), addressString);
                }
            } catch (ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }
        }


        //Set Title
        eventName.setText(event.getTitle());

        //Set Accepts
        accepts.setText(event.getAcceptUser().size()+"");

        //Set Entrance
        entrance.setText(event.getEntrance()+" €");

        //Set Day
        day.setText(event.getDate().substring(0,2));

        //Set Month
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter month_date = DateTimeFormatter.ofPattern("MMM", Locale.GERMAN);
        month.setText(month_date.format(LocalDate.parse(event.getDate(),dtf)));

        //Set Time Until Event begins
        timeUntilEvent.setText(ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(event.getDate(),dtf)) + " Tage");

        //Set who is in
        numOfFriends.setText(event.getAcceptUser().stream().filter(a -> user.getFriends().contains(a)).collect(Collectors.toList()).size() + " Freunde nehmen teil");

        return listItem;
    }
}

package at.htlgkr.festlever.objects;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Event implements Serializable {
    private Bitmap image;
    String title;
    double latitude;
    double longitude;
    String date;
    double entrance;
    int accept;
    List<String> acceptUser = new ArrayList<>();

    public Event() {}

    public Event(Bitmap image, String title, double latitude, double longitude, String date, double entrance, int accept, List<String> acceptUser) {
        this.image = image;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.entrance = entrance;
        this.accept = accept;
        this.acceptUser = acceptUser;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getEntrance() {
        return entrance;
    }

    public void setEntrance(double entrance) {
        this.entrance = entrance;
    }

    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    public List<String> getAcceptUser() {
        return acceptUser;
    }

    public void setAcceptUser(List<String> acceptUser) {
        this.acceptUser = acceptUser;
    }
}

package at.htlgkr.festlever.objects;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import at.htlgkr.festlever.logic.PasswordToHash;

public class Event implements Serializable {
    private String id = "";
    private Bitmap image;
    private String title;
    private double latitude;
    private double longitude;
    private String date;
    private double entrance;
    private List<String> acceptUser = new ArrayList<>();

    public Event() {}

    public Event(Bitmap image, String title, double latitude, double longitude, String date, double entrance) {
        this.image = image;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.entrance = entrance;


        generateID();
    }

    public void generateID(){
        try {
            PasswordToHash passwordToHash = new PasswordToHash();
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            id = passwordToHash.bytesToHex(md.digest((title + System.currentTimeMillis()).getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    public List<String> getAcceptUser() {
        return acceptUser;
    }

    public void setAcceptUser(List<String> acceptUser) {
        this.acceptUser = acceptUser;
    }
}

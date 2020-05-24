package at.htlgkr.festlever.logic;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImagePuffer {

    static Map<String, Bitmap> images = new HashMap<>();

    public Bitmap getImage(String url){
        return images.get(url);
    }

    public void storeImage(String url, Bitmap image){
        images.put(url, image);
    }

    public boolean isStored(String url){
        return images.containsKey(url);
    }


}

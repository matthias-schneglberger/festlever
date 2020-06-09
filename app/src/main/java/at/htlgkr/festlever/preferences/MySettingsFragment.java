package at.htlgkr.festlever.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import at.htlgkr.festlever.R;

public class MySettingsFragment extends PreferenceFragmentCompat {
    private final String TAG = "MySettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
//        Preference prefDistance = findPreference("check_box_preference_distance");
//        prefDistance.setOnPreferenceChangeListener( ((preference, newValue) -> {
//            Log.d(TAG, "onCreatePreferences: " + preference.getKey() + " --> "+ newValue.toString());
//            if(preference.getKey().equals("check_box_preference_distance")){
//                try{
//                    Integer newDistance = Integer.valueOf(newValue.toString());
//                    return true;
//                }catch (Exception e){return false;}
//            }
//            return true;
//        }));
    }
}

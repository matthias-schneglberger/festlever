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
    }
}

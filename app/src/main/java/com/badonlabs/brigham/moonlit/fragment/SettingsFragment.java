package com.badonlabs.brigham.moonlit.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.badonlabs.brigham.moonlit.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_device);
    }

}

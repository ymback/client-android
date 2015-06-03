package me.ltype.lightreader.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import me.ltype.lightreader.R;

/**
 * Created by ltype on 2015/6/2.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static String LOG_TAG = "SettingsFragment";

    private ListPreference fontSizePre;
    private ListPreference lineSpacingPre;
    private SwitchPreference screenAlwaysPre;
    private SwitchPreference nightModelPre;
    private Preference checkVersionPre;
    private Preference clearCachePre;
    private Preference clearDataPre;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        /*mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mSharedPreferences.edit();*/

        fontSizePre = (ListPreference) findPreference(getString(R.string.setting_font_size));
        lineSpacingPre = (ListPreference) findPreference(getString(R.string.setting_line_spacing));
        screenAlwaysPre = (SwitchPreference) findPreference(getString(R.string.setting_screen_always));
        nightModelPre = (SwitchPreference) findPreference(getString(R.string.setting_night_model));
        checkVersionPre = findPreference(getString(R.string.setting_check_version));
        clearCachePre = findPreference(getString(R.string.setting_clear_cache));
        clearDataPre = findPreference(getString(R.string.setting_clear_data));

        fontSizePre.setOnPreferenceClickListener(this);
        fontSizePre.setOnPreferenceChangeListener(this);
        lineSpacingPre.setOnPreferenceClickListener(this);
        lineSpacingPre.setOnPreferenceChangeListener(this);
        screenAlwaysPre.setOnPreferenceClickListener(this);
        screenAlwaysPre.setOnPreferenceChangeListener(this);
        nightModelPre.setOnPreferenceClickListener(this);
        nightModelPre.setOnPreferenceChangeListener(this);
        checkVersionPre.setOnPreferenceClickListener(this);
        checkVersionPre.setOnPreferenceChangeListener(this);
        clearCachePre.setOnPreferenceClickListener(this);
        clearCachePre.setOnPreferenceChangeListener(this);
        clearDataPre.setOnPreferenceClickListener(this);
        clearDataPre.setOnPreferenceChangeListener(this);


    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.setting_font_size))) {
            return true;
        } else if (preference.getKey().equals(getString(R.string.setting_line_spacing))) {
            return true;
        } else if (preference.getKey().equals(getString(R.string.setting_screen_always))) {
            return true;
        } else if (preference.getKey().equals(getString(R.string.setting_night_model))) {
            return true;
        } else if (preference.getKey().equals(getString(R.string.setting_check_version))) {
            return true;
        } else if (preference.getKey().equals(getString(R.string.setting_clear_cache))) {
            return true;
        } else if (preference.getKey().equals(getString(R.string.setting_clear_data))) {
            return true;
        }
        return false;
    }

}

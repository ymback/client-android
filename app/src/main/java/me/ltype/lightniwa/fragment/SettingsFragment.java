package me.ltype.lightniwa.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import java.io.File;

import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.util.FileUtils;
import me.ltype.lightniwa.R;

import static me.ltype.lightniwa.util.Util.checkUpdate;

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
        if (preference.getKey().equals(getString(R.string.setting_check_version))) {
            checkUpdate(getActivity(), true);
            return true;
        } else if (preference.getKey().equals(getString(R.string.setting_clear_cache))) {
            File cacheDir = getActivity().getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
               if (FileUtils.clearFolder(cacheDir, System.currentTimeMillis()) > 0) {
                   Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(getActivity(), "未成功删除数据", Toast.LENGTH_SHORT).show();
               }
            }
            return true;
        } else if (preference.getKey().equals(getString(R.string.setting_clear_data))) {
            com.rey.material.app.Dialog.Builder builder = null;
            builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    super.onPositiveActionClicked(fragment);
                    File bookDir = new File(Constants.BOOK_DIR);
                    if (bookDir != null && bookDir.isDirectory()) {
                        ProgressDialog progressBar = new ProgressDialog(getActivity());
                        progressBar.setTitle("清空数据");
                        progressBar.setMessage("删除中...");
                        progressBar.setIndeterminate(true);
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.setCancelable(false);
                        progressBar.show();
                        if (FileUtils.clearFolder(bookDir, System.currentTimeMillis()) > 0) {
                            progressBar.dismiss();
                            Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.dismiss();
                            Toast.makeText(getActivity(), "未成功删除数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };

            ((SimpleDialog.Builder)builder).message("删除所有书籍")
                    .title("清空数据")
                    .positiveAction("确定")
                    .negativeAction("取消");

            DialogFragment fragment = DialogFragment.newInstance(builder);
            ActionBarActivity mActivity = (ActionBarActivity) getActivity();
            fragment.show(mActivity.getSupportFragmentManager(), null);
            return true;
        }
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

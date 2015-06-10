package me.ltype.lightreader.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;

import me.drakeet.materialdialog.MaterialDialog;
import me.ltype.lightreader.R;
import me.ltype.lightreader.activity.SettingActivity;
import me.ltype.lightreader.constant.Constants;
import me.ltype.lightreader.util.FileUtils;

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

    private MaterialDialog mMaterialDialog;

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
            RequestQueue mQueue = Volley.newRequestQueue(getActivity());
            StringRequest checkReq = new StringRequest(
                    Request.Method.GET,
                    "http://ltype.me/api/version",
                    response -> {
                        try {
                            if (JSON.parseObject(response).getInteger("versionCode") > getActivity().getPackageManager().getPackageInfo("me.ltype.lightreader", 0).versionCode){
                                mMaterialDialog = new MaterialDialog(getActivity())
                                        .setTitle("检查更新")
                                        .setMessage("有新版本，是否更新？")
                                        .setPositiveButton("确定", v -> {
                                            mMaterialDialog.dismiss();
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(JSON.parseObject(response).getString("url")));
                                            startActivity(intent);
                                        })
                                        .setNegativeButton("取消", v -> {
                                            mMaterialDialog.dismiss();
                                        });
                                mMaterialDialog.show();
                            } else {
                                Toast.makeText(getActivity(), "已是最新版本", Toast.LENGTH_SHORT).show();
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show());
            mQueue.add(checkReq);
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
            mMaterialDialog = new MaterialDialog(getActivity())
                .setTitle("清空数据")
                .setMessage("删除所有书籍")
                .setPositiveButton("确定", v -> {
                    mMaterialDialog.dismiss();
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
                })
                .setNegativeButton("取消", v -> {
                    mMaterialDialog.dismiss();
                });
            mMaterialDialog.show();
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

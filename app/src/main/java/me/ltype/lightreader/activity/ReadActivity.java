package me.ltype.lightreader.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import me.drakeet.materialdialog.MaterialDialog;
import me.ltype.lightreader.R;
import me.ltype.lightreader.adapter.ReadListAdapter;

/**
 * Created by ltype on 2015/5/17.
 */
public class ReadActivity  extends ActionBarActivity {
    private  static  String LOG_TAG = "ReadActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mScrollPositionKey;
    private Bundle bundle;
    private MaterialDialog mMaterialDialog;
    private int mScrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_read);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_view_read);
        mRecyclerView.setHasFixedSize(true);
        if (sharedPreferences.getBoolean(getString(R.string.setting_night_model), false)) {
            mRecyclerView.setBackgroundResource(R.drawable.content_dark_bg);
        }
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new ReadListAdapter(this));

        bundle = getIntent().getExtras();
        mScrollPositionKey = bundle.getString("bookId") + "_" + bundle.getString("volumeId") + "_" + bundle.getString("chapterId");
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sp_bookmarks), Context.MODE_PRIVATE);
        mScrollPosition = sharedPref.getInt(mScrollPositionKey, 0);
        if (mScrollPosition > 0) {
            mLayoutManager.scrollToPosition(mScrollPosition);
            if (sharedPreferences.getBoolean(getString(R.string.setting_auto_load_bookmark), true)) {
                Toast.makeText(getApplicationContext(), "已载入书签", Toast.LENGTH_SHORT).show();
            } else {
                mMaterialDialog = new MaterialDialog(this)
                        .setTitle(getResources().getString(R.string.bookmarks))
                        .setMessage(getResources().getString(R.string.continue_read))
                        .setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                                mLayoutManager.scrollToPosition(mScrollPosition);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
            }
        }

    }
    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sp_bookmarks), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        int mScrollPosition = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        editor.putInt(mScrollPositionKey, mScrollPosition);
        editor.commit();
    }
}

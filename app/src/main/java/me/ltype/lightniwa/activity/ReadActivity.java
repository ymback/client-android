package me.ltype.lightniwa.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import me.ltype.lightniwa.adapter.ReadListAdapter;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.db.LightNiwaDataStore.Bookmarks;

/**
 * Created by ltype on 2015/5/17.
 */
public class ReadActivity  extends ActionBarActivity implements View.OnTouchListener {
    private static String LOG_TAG = "ReadActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Bundle mBundle;
    private List<String> chapterIdList;
    private SharedPreferences sharedPreferences;

    private ContentResolver mResolver;
    private long lastTouchTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_read);
        mResolver = getContentResolver();
        mBundle = getIntent().getExtras();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        chapterIdList = mBundle.getStringArrayList("chapterIdList");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_view_read);
        mRecyclerView.setHasFixedSize(true);
        if (sharedPreferences.getBoolean(getString(R.string.setting_night_model), false)) {
            mRecyclerView.setBackgroundResource(R.drawable.content_dark_bg);
        }
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setOnTouchListener(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReadListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        loadBookmarks(mBundle.getInt("chapterIndex"));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        storeBookmarks(mBundle.getInt("chapterIndex"), ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int screenWidth  = getResources().getDisplayMetrics().widthPixels;
                float rawX = event.getRawX();
                int chapterIndex = mBundle.getInt("chapterIndex");
                if (rawX > screenWidth * 0.9) {
                    if (!checkTouchTime()) return false;
                    if (++chapterIndex < chapterIdList.size()) {
                        storeBookmarks(chapterIndex - 1, ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition());
                        mBundle.putInt("chapterIndex", chapterIndex);
                        getIntent().replaceExtras(mBundle);
                        mAdapter = new ReadListAdapter(this);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        loadBookmarks(chapterIndex);
                    } else {
                        Toast.makeText(v.getContext(), "已到最后一章", Toast.LENGTH_SHORT).show();
                    }
                } else if (rawX < screenWidth * 0.1) {
                    if (!checkTouchTime()) return false;
                    if (--chapterIndex >= 0) {
                        storeBookmarks(chapterIndex + 1, ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition());
                        mBundle.putInt("chapterIndex", chapterIndex);
                        getIntent().replaceExtras(mBundle);
                        mAdapter = new ReadListAdapter(this);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        loadBookmarks(chapterIndex);
                    } else {
                        Toast.makeText(v.getContext(), "已到最前一章", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return false;
    }

    private boolean checkTouchTime() {
        if(lastTouchTime + 2000 <= System.currentTimeMillis()) {
            lastTouchTime = System.currentTimeMillis();
            Toast.makeText(this, "再次点击切换章节", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadBookmarks(int chapterIndex) {
        Cursor cursor = mResolver.query(Bookmarks.CONTENT_URI, new String[]{Bookmarks.POSITION},
                Bookmarks.CHAPTER_ID + " = " + chapterIdList.get(chapterIndex), null, null);
        if (cursor != null && cursor.moveToFirst()){
            if (sharedPreferences.getBoolean(getString(R.string.setting_auto_load_bookmark), true)) {
                mRecyclerView.scrollToPosition(cursor.getInt(0));
                Toast.makeText(getApplicationContext(), "已载入书签", Toast.LENGTH_SHORT).show();
            } else {
                /*mMaterialDialog = new MaterialDialog(this)
                        .setTitle(getResources().getString(R.string.bookmarks))
                        .setMessage(getResources().getString(R.string.continue_read))
                        .setPositiveButton(getResources().getString(R.string.ok), v -> {
                            mMaterialDialog.dismiss();
                            mRecyclerView.scrollToPosition(cursor.getInt(0));
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), v -> {
                            mMaterialDialog.dismiss();
                        });
                mMaterialDialog.show();*/
            }
        }
    }

    private void storeBookmarks(int chapterIndex, int position) {
        position = position > 0 ? position : 1;
        ContentValues values = new ContentValues();
        values.put(Bookmarks.BOOK_ID, mBundle.getString("bookId"));
        values.put(Bookmarks.VOLUME_ID, mBundle.getString("volumeId"));
        values.put(Bookmarks.CHAPTER_ID, chapterIdList.get(chapterIndex));
        values.put(Bookmarks.POSITION, position);
        values.put(Bookmarks.UPDATE_TIME, System.currentTimeMillis());
        mResolver.insert(Bookmarks.CONTENT_URI, values);
    }
}

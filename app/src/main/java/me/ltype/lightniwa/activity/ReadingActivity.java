package me.ltype.lightniwa.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import java.util.List;

import me.ltype.lightniwa.adapter.ReadingListViewAdapter;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.db.LightNiwaDataStore.Bookmarks;

/**
 * Created by ltype on 2015/5/17.
 */
public class ReadingActivity extends ActionBarActivity {
    private static String LOG_TAG = "ReadingActivity";
    private Bundle mBundle;
    private List<String> chapterIdList;
    private SharedPreferences sharedPreferences;

    private ListView listView;
    private BaseAdapter mAdapter;
    private ContentResolver mResolver;
    private long lastTouchTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_list_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mResolver = getContentResolver();
        mBundle = getIntent().getExtras();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        chapterIdList = mBundle.getStringArrayList("chapterIdList");
        mAdapter = new ReadingListViewAdapter(this);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);
        if (sharedPreferences.getBoolean(getString(R.string.setting_night_model), false)) {
            listView.setBackgroundResource(R.drawable.content_dark_bg);
        }
        loadBookmarks(mBundle.getInt("chapterIndex"));

        listView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    float rawX = event.getRawX();
                    int chapterIndex = mBundle.getInt("chapterIndex");
                    if (rawX > screenWidth * 0.9) {
                        if (!checkTouchTime()) return false;
                        if (++chapterIndex < chapterIdList.size()) {
                            storeBookmarks(chapterIndex - 1);
                            mBundle.putInt("chapterIndex", chapterIndex);
                            getIntent().replaceExtras(mBundle);
                            mAdapter = new ReadingListViewAdapter(v.getContext());
                            listView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            loadBookmarks(chapterIndex);
                        } else {
                            Toast.makeText(v.getContext(), "已到最后一章", Toast.LENGTH_SHORT).show();
                        }
                    } else if (rawX < screenWidth * 0.1) {
                        if (!checkTouchTime()) return false;
                        if (--chapterIndex >= 0) {
                            storeBookmarks(chapterIndex + 1);
                            mBundle.putInt("chapterIndex", chapterIndex);
                            getIntent().replaceExtras(mBundle);
                            mAdapter = new ReadingListViewAdapter(v.getContext());
                            listView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            loadBookmarks(chapterIndex);
                        } else {
                            Toast.makeText(v.getContext(), "已到最前一章", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
            return false;
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        storeBookmarks(mBundle.getInt("chapterIndex"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    private boolean checkTouchTime() {
        if(lastTouchTime + 2000 <= System.currentTimeMillis()) {
            lastTouchTime = System.currentTimeMillis();
            Toast.makeText(this, "再次点击切换章节", Toast.LENGTH_SHORT).show();
            return false;
        }
        lastTouchTime = 0;
        return true;
    }

    private void loadBookmarks(int chapterIndex) {
        Cursor cursor = mResolver.query(Bookmarks.CONTENT_URI, new String[]{Bookmarks.POSITION},
                Bookmarks.CHAPTER_ID + " = " + chapterIdList.get(chapterIndex), null, null);
        if (cursor != null && cursor.moveToFirst()){
            if (sharedPreferences.getBoolean(getString(R.string.setting_auto_load_bookmark), true)) {
                listView.setSelection(cursor.getInt(0));
                Toast.makeText(getApplicationContext(), "已载入书签", Toast.LENGTH_SHORT).show();
            } else {
                com.rey.material.app.Dialog.Builder builder = null;
                builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        listView.setSelection(cursor.getInt(0));
                        super.onPositiveActionClicked(fragment);
                    }
                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                ((SimpleDialog.Builder)builder).message(getResources().getString(R.string.continue_read))
                        .title(getResources().getString(R.string.bookmarks))
                        .positiveAction(getResources().getString(R.string.ok))
                        .negativeAction(getResources().getString(R.string.cancel));

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        }
    }

    private void storeBookmarks(int chapterIndex) {
        int position = listView.getFirstVisiblePosition() > 0 ? listView.getFirstVisiblePosition() : 0;
        ContentValues values = new ContentValues();
        values.put(Bookmarks.BOOK_ID, mBundle.getString("bookId"));
        values.put(Bookmarks.VOLUME_ID, mBundle.getString("volumeId"));
        values.put(Bookmarks.CHAPTER_ID, chapterIdList.get(chapterIndex));
        values.put(Bookmarks.POSITION, position);
        values.put(Bookmarks.UPDATE_TIME, System.currentTimeMillis());
        mResolver.insert(Bookmarks.CONTENT_URI, values);
    }
}

package me.ltype.lightreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.ltype.lightreader.R;
import me.ltype.lightreader.constant.Constants;
import me.ltype.lightreader.util.FileUtils;

/**
 * Created by ltype on 2015/5/16.
 */
public class ReadListAdapter extends RecyclerView.Adapter<ReadListAdapter.ViewHolder> implements View.OnTouchListener {
    private static String LOG_TAG = "ReadListAdapter";
    private LayoutInflater inflater;
    private Activity activity;
    private List<String> contentList;
    private String bookId;
    private String volumeId;
    private int chapterIndex;
    private List<String> chapterIdList;
    private float fontSize;
    private float lineSpacing;
    private boolean nightModel;
    private Bundle mBundle;
    private long touchTime = 0;

    public ReadListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        if (sharedPreferences.getBoolean(activity.getString(R.string.setting_screen_always), false)) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        fontSize = Float.valueOf(sharedPreferences.getString(activity.getString(R.string.setting_font_size), "16"));
        lineSpacing = Float.valueOf(sharedPreferences.getString(activity.getString(R.string.setting_line_spacing), "4"));
        nightModel = sharedPreferences.getBoolean(activity.getString(R.string.setting_night_model), false);
        mBundle = activity.getIntent().getExtras();
        if(!mBundle.isEmpty()) {
            bookId = mBundle.getString("bookId");
            volumeId = mBundle.getString("volumeId");
            chapterIndex = mBundle.getInt("chapterIndex");
            chapterIdList = mBundle.getStringArrayList("chapterIdList");
        }
        contentList =  FileUtils.getContentList(bookId, volumeId, chapterIdList.get(chapterIndex));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int screenWidth  = activity.getResources().getDisplayMetrics().widthPixels;
                float rawX = event.getRawX();
                if (rawX > screenWidth * 0.9) {
                    if (checkTouchTime()) return true;
                    if (chapterIndex + 1 < chapterIdList.size()) {
                        contentList =  FileUtils.getContentList(bookId, volumeId, chapterIdList.get(++chapterIndex));
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(v.getContext(), "已到最后一章", Toast.LENGTH_SHORT).show();
                    }
                } else if (rawX < screenWidth * 0.1) {
                    if (checkTouchTime()) return true;
                    if (chapterIndex - 1 >= 0) {
                        contentList =  FileUtils.getContentList(bookId, volumeId, chapterIdList.get(--chapterIndex));
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(v.getContext(), "已到最前一章", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public ReadListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_read, parent, false);
        currentView.setOnTouchListener(this);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        TextView textView = (TextView) viewHolder.mView.findViewById(R.id.text_view_read);
        textView.setTextSize(fontSize);
        if (nightModel) {
            textView.setTextColor(activity.getResources().getColor(R.color.night_model_text));
        }
        ImageView imageView = (ImageView) viewHolder.mView.findViewById(R.id.image_view_read);
        if (contentList.get(i).startsWith("/" + volumeId + "/img/") && contentList.get(i).endsWith(".jpg")) {
            File imgFile = new  File(Constants.BOOK_DIR + File.separator + bookId + contentList.get(i));
            if(imgFile.exists()){
                Bitmap mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(mBitmap);
            }
            textView.setText(" ");
        } else {
            textView.setText("\t\t\t\t" + contentList.get(i));
            Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
            imageView.setImageDrawable(transparentDrawable);
        }

    }

    @Override
    public long getItemId(int i) {
        return Long.getLong(contentList.get(i));
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    private boolean checkTouchTime() {
        if(touchTime + 2000 <= System.currentTimeMillis()) {
            touchTime = System.currentTimeMillis();
            Toast.makeText(activity, "再次点击切换章节", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}

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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import me.ltype.lightreader.R;
import me.ltype.lightreader.constant.Constants;
import me.ltype.lightreader.util.FileUtils;

/**
 * Created by ltype on 2015/5/16.
 */
public class ReadListAdapter extends RecyclerView.Adapter<ReadListAdapter.ViewHolder> {
    private static String LOG_TAG = "ReadListAdapter";
    private LayoutInflater inflater;
    private Activity activity;
    private List<String> contentList;
    private String bookId;
    private String volumeId;
    private String chapterId;
    private float fontSize;
    private float lineSpacing;
    private boolean nightModel;

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
        Bundle bundle = activity.getIntent().getExtras();
        if(!bundle.isEmpty()) {
            bookId = bundle.getString("bookId");
            volumeId = bundle.getString("volumeId");
            chapterId = bundle.getString("chapterId");
        }
        this.contentList =  FileUtils.getContentList(bookId, volumeId, chapterId);
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
            File imgFile = new  File(Environment.getExternalStorageDirectory().getPath() + Constants.BOOK_DIR + File.separator + bookId + contentList.get(i));
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

}

package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.util.BitmapCache;
import me.ltype.lightniwa.util.FileUtils;

/**
 * Created by ltype on 2015/6/20.
 */
public class ReadingListViewAdapter extends BaseAdapter {
    private String LOG_TAG = "ReadingListViewAdapter";
    private Context mContext;
    private List<String> contentList = new ArrayList<>();
    private String bookId;
    private String volumeId;
    private int chapterIndex;
    private List<String> chapterIdList;
    private float fontSize;
    private float lineSpacing;
    private boolean nightModel;
    private Bundle mBundle;

    public ReadingListViewAdapter(Context context) {
        this.mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (sharedPreferences.getBoolean(mContext.getString(R.string.setting_screen_always), false)) {
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        fontSize = Float.valueOf(sharedPreferences.getString(mContext.getString(R.string.setting_font_size), "16"));
        lineSpacing = Float.valueOf(sharedPreferences.getString(mContext.getString(R.string.setting_line_spacing), "4"));
        nightModel = sharedPreferences.getBoolean(mContext.getString(R.string.setting_night_model), false);
        mBundle = ((Activity) mContext).getIntent().getExtras();
        if(!mBundle.isEmpty()) {
            bookId = mBundle.getString("bookId");
            volumeId = mBundle.getString("volumeId");
            chapterIndex = mBundle.getInt("chapterIndex");
            chapterIdList = mBundle.getStringArrayList("chapterIdList");
        }
        contentList =  FileUtils.getContentList(bookId, volumeId, chapterIdList.get(chapterIndex));
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Object getItem(int position) {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (contentList.get(i).startsWith("/" + volumeId + "/img/") && contentList.get(i).endsWith(".jpg")) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setClickable(false);
            File imgFile = new  File(Constants.BOOK_DIR + File.separator + bookId + contentList.get(i));
            if(imgFile.exists()){
                Bitmap mBitmap = BitmapCache.getBitmap(imgFile.getAbsolutePath());
                if(mBitmap == null) {
                    Bitmap tmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    mBitmap = BitmapCache.putBitmap(imgFile.getAbsolutePath(), tmp);
                }
                imageView.setImageBitmap(mBitmap);
            }
            return imageView;
        } else {
            TextView textView = new TextView(parent.getContext());
            textView.setClickable(false);
            textView.setTextSize(fontSize);
            textView.setPadding(16, 0, 16, 0);
            if (nightModel) {
                textView.setTextColor(mContext.getResources().getColor(R.color.night_model_text));
            }
            textView.setText("\t\t\t\t" + contentList.get(i));
            return textView;
        }
    }

    public class ContentTextView extends EditText {
        public ContentTextView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        public ContentTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
        }

        public ContentTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected boolean getDefaultEditable() {
            return false;
        }
    }
}

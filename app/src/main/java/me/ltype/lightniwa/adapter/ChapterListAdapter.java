package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.ltype.lightniwa.db.LightNiwaDataStore;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.activity.ReadingActivity;
import me.ltype.lightniwa.model.Chapter;

/**
 * Created by ltype on 2015/5/16.
 */
public class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.ViewHolder> {
    private static String LOG_TAG = "VolumeListAdapter";
    private ContentResolver mResolver;
    private Activity activity;
    private List<Chapter> chapterList = new ArrayList<>();
    private List<String> chapterIdList;

    public ChapterListAdapter(Activity activity, String volumeId) {
        this.activity = activity;

        mResolver = activity.getContentResolver();
        Cursor cursor = mResolver.query(LightNiwaDataStore.Chapters.CONTENT_URI, LightNiwaDataStore.Chapters.COLUMNS, LightNiwaDataStore.Chapters.VOLUME_ID + "=?", new String[]{volumeId}, null);
        if (cursor != null && cursor.moveToFirst()){
            while(!cursor.isAfterLast()) {
                Chapter chapter = new Chapter();
                chapter.setBookId(cursor.getString(cursor.getColumnIndex("book_id")));
                chapter.setVolumeId(cursor.getString(cursor.getColumnIndex("volume_id")));
                chapter.setId(cursor.getString(cursor.getColumnIndex("chapter_id")));
                chapter.setIndex(cursor.getString(cursor.getColumnIndex("chapter_index")));
                chapter.setName(cursor.getString(cursor.getColumnIndex("name")));
                chapterList.add(chapter);
                cursor.moveToNext();
            }
            cursor.close();
        }

        chapterIdList = new ArrayList<>();
        for (Chapter chapter : chapterList) {
            chapterIdList.add(chapter.getId());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public ChapterListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        TextView textView = (TextView) viewHolder.mView.findViewById(R.id.title_chapter);
        textView.setText(chapterList.get(i).getName());

        viewHolder.mView.setOnClickListener(view -> {
            Intent intent =  new Intent(viewHolder.mView.getContext() ,ReadingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("bookId", chapterList.get(i).getBookId());
            bundle.putString("volumeId", chapterList.get(i).getVolumeId());
            bundle.putInt("chapterIndex", i);
            bundle.putStringArrayList("chapterIdList", (ArrayList<String>) chapterIdList);
            intent.putExtras(bundle);
            viewHolder.mView.getContext().startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        });

    }

    @Override
    public long getItemId(int i) {
        return Long.getLong(chapterList.get(i).getBookId());
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }
}

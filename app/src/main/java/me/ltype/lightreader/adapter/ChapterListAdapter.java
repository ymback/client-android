package me.ltype.lightreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.ltype.lightreader.R;
import me.ltype.lightreader.activity.ReadActivity;
import me.ltype.lightreader.model.Chapter;
import me.ltype.lightreader.model.Volume;
import me.ltype.lightreader.util.FileUtils;

/**
 * Created by ltype on 2015/5/16.
 */
public class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.ViewHolder> {
    private static String LOG_TAG = "VolumeListAdapter";
    private LayoutInflater inflater;
    private Activity activity;
    private Map<Long, Volume> volumeMap;
    private List<Chapter> chapterList;
    private List<String> chapterIdList;

    public ChapterListAdapter(Activity activity, String bookId, String volumeId) {
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.chapterList =  FileUtils.getChapterList(bookId, volumeId);
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
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chapter, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        TextView textView = (TextView) viewHolder.mView.findViewById(R.id.title_chapter);
        textView.setText(chapterList.get(i).getTitle());

        viewHolder.mView.setOnClickListener(view -> {
            Intent intent =  new Intent(viewHolder.mView.getContext() ,ReadActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("bookId", chapterList.get(i).getBookId());
            bundle.putString("volumeId", chapterList.get(i).getVolumeId());
            bundle.putInt("chapterIndex", i);
            bundle.putStringArrayList("chapterIdList", (ArrayList<String>) chapterIdList);
            intent.putExtras(bundle);
            viewHolder.mView.getContext().startActivity(intent);
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

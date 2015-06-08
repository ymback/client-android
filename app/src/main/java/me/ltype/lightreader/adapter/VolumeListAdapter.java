package me.ltype.lightreader.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import me.ltype.lightreader.R;
import me.ltype.lightreader.activity.MainActivity;
import me.ltype.lightreader.constant.Constants;
import me.ltype.lightreader.fragment.ChapterFragment;
import me.ltype.lightreader.model.Book;
import me.ltype.lightreader.model.Volume;
import me.ltype.lightreader.util.FileUtils;

/**
 * Created by ltype on 2015/5/16.
 */
public class VolumeListAdapter extends RecyclerView.Adapter<VolumeListAdapter.ViewHolder> {
    private static String LOG_TAG = "VolumeListAdapter";
    private MainActivity activity;
    private List<Volume> volumeList;
    private Book book;

    public VolumeListAdapter(Activity activity) {
        this.activity = (MainActivity) activity;
        Bundle bundle = activity.getIntent().getExtras();
        String bookId = bundle.getString("bookId");
        book = FileUtils.getBookMap().get(bookId);
        this.volumeList = FileUtils.getVolumeList(book.getId());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public VolumeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_book, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        ImageView imageView = (ImageView) viewHolder.mView.findViewById(R.id.book_card_cover);
        File imgFile = new  File(Constants.BOOK_DIR + "/" + volumeList.get(i).getBookId() + volumeList.get(i).getCover());
        if(imgFile.exists()){
            Bitmap mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(mBitmap);
        }

        TextView textView = (TextView) viewHolder.mView.findViewById(R.id.book_card_name);
        textView.setText(volumeList.get(i).getHeader());
        textView.append("\t\t\t\t");
        textView.append(volumeList.get(i).getName());

        TextView author = (TextView) viewHolder.mView.findViewById(R.id.book_card_author);
        author.setText(book.getAuthor());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getIntent().putExtra("bookId", volumeList.get(i).getBookId());
                activity.getIntent().putExtra("volumeId", volumeList.get(i).getId());
                Fragment fragment = new ChapterFragment();
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.add(fragment, "ChapterFragment");
                transaction.commit();
                activity.setFragmentChild(new ChapterFragment(), volumeList.get(i).getName());
            }
        });

    }

    @Override
    public long getItemId(int i) {
        return Long.getLong(volumeList.get(i).getBookId());
    }

    @Override
    public int getItemCount() {
        return volumeList.size();
    }
}

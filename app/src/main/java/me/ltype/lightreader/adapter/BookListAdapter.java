package me.ltype.lightreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
import me.ltype.lightreader.fragment.VolumeFragment;
import me.ltype.lightreader.model.Book;
import me.ltype.lightreader.util.FileUtils;

/**
 * Created by ltype on 2015/5/16.
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {
    private static String LOG_TAG = "BookListAdapter";
    private LayoutInflater inflater;
    private MainActivity activity;
    private List<Book> bookList;

    public  BookListAdapter (Activity activity) {
        this.activity = (MainActivity) activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bookList = FileUtils.getBookList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public BookListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_book, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        ImageView imageView = (ImageView) viewHolder.mView.findViewById(R.id.book_card_cover);
        File imgFile = new  File(Environment.getExternalStorageDirectory().getPath() + Constants.BOOK_DIR + "/" + bookList.get(i).getId() + bookList.get(i).getCover());
        if(imgFile.exists()){
            Bitmap mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(mBitmap);
        }

        TextView name = (TextView) viewHolder.mView.findViewById(R.id.book_card_name);
        name.setText(bookList.get(i).getName());

        TextView author = (TextView) viewHolder.mView.findViewById(R.id.book_card_author);
        author.setText(bookList.get(i).getAuthor());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getIntent().putExtra("bookId", bookList.get(i).getId());
                activity.setFragmentChild(new VolumeFragment(), bookList.get(i).getName());
            }
        });

    }
    @Override
    public long getItemId(int i) {
        return Long.getLong(bookList.get(i).getId());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}

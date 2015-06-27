package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.db.LightNiwaDataStore.Books;
import me.ltype.lightniwa.db.LightNiwaDataStore.Volumes;
import me.ltype.lightniwa.db.LightNiwaDataStore.Chapters;
import me.ltype.lightniwa.fragment.VolumeFragment;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.util.FileUtils;
import me.ltype.lightniwa.R;

/**
 * Created by ltype on 2015/5/16.
 */
public class BookListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String LOG_TAG = "BookListAdapter";
    private LayoutInflater inflater;
    private ContentResolver mResolver;
    private MainActivity mActivity;
    private List<Book> bookList = new ArrayList<>();

    public  BookListAdapter (Activity activity) {
        this.mActivity = (MainActivity) activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mResolver = activity.getContentResolver();
        Cursor cursor = mResolver.query(Books.CONTENT_URI, Books.COLUMNS, null, null, null);
        if (cursor != null && cursor.moveToFirst()){
            while(!cursor.isAfterLast()) {
                Book book = new Book();
                book.setId(cursor.getString(cursor.getColumnIndex("book_id")));
                book.setName(cursor.getString(cursor.getColumnIndex("name")));
                book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
                book.setIllustrator(cursor.getString(cursor.getColumnIndex("illustrator")));
                book.setPublisher(cursor.getString(cursor.getColumnIndex("publisher")));
                book.setCover(cursor.getString(cursor.getColumnIndex("cover")));
                book.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                bookList.add(book);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public BookViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        BookViewHolder bookViewHolder = new BookViewHolder(currentView);
        return bookViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BookViewHolder bookViewHolder = (BookViewHolder) viewHolder;
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) bookViewHolder.mView.findViewById(R.id.book_card_cover);
        TextView name = (TextView) bookViewHolder.mView.findViewById(R.id.book_card_name);
        TextView author = (TextView) bookViewHolder.mView.findViewById(R.id.book_card_author);
        TextView illustrator = (TextView) bookViewHolder.mView.findViewById(R.id.book_card_illustrator);
        TextView publisher = (TextView) bookViewHolder.mView.findViewById(R.id.book_card_publisher);

        File imgFile = new  File(Constants.BOOK_DIR + File.separator + bookList.get(position).getId() + bookList.get(position).getCover());
        if(imgFile.exists()){
            simpleDraweeView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
        }

        name.setText(bookList.get(position).getName());
        author.setText(bookList.get(position).getAuthor());
        illustrator.setText(bookList.get(position).getIllustrator());
        publisher.setText(bookList.get(position).getPublisher());

        bookViewHolder.mView.setClickable(true);
        bookViewHolder.mView.setOnClickListener(view -> {
            mActivity.getIntent().putExtra("bookId", bookList.get(position).getId());
            mActivity.setFragmentChild(new VolumeFragment(), bookList.get(position).getName());
        });
        bookViewHolder.mView.setLongClickable(true);
        bookViewHolder.mView.setOnLongClickListener(v -> {
            com.rey.material.app.Dialog.Builder builder = null;
            builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    File bookDir = new File(Constants.BOOK_DIR + File.separator + bookList.get(position).getId());
                    if (bookDir != null && bookDir.isDirectory()) {
                        if (FileUtils.delFolder(bookDir, System.currentTimeMillis())) {
                            mResolver.delete(Books.CONTENT_URI, Books.BOOK_ID + "=?", new String[]{bookList.get(position).getId()});
                            mResolver.delete(Volumes.CONTENT_URI, Volumes.BOOK_ID + "=?", new String[]{bookList.get(position).getId()});
                            mResolver.delete(Chapters.CONTENT_URI, Chapters.BOOK_ID + "=?", new String[]{bookList.get(position).getId()});
                            bookList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mActivity, "操作成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, "未成功删除数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };

            ((SimpleDialog.Builder)builder).message(bookList.get(position).getName())
                    .title("删除")
                    .positiveAction("确定")
                    .negativeAction("取消");

            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(mActivity.getSupportFragmentManager(), null);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}

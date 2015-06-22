package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
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
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {
    private static String LOG_TAG = "BookListAdapter";
    private LayoutInflater inflater;
    private ContentResolver mResolver;
    private MainActivity mActivity;
    private List<Book> bookList = new ArrayList<>();
    private MaterialDialog mMaterialDialog;

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
        File imgFile = new  File(Constants.BOOK_DIR + File.separator + bookList.get(i).getId() + bookList.get(i).getCover());
        if(imgFile.exists()){
            Bitmap mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(mBitmap);
        }

        TextView name = (TextView) viewHolder.mView.findViewById(R.id.book_card_name);
        name.setText(bookList.get(i).getName());

        TextView author = (TextView) viewHolder.mView.findViewById(R.id.book_card_author);
        author.setText(bookList.get(i).getAuthor());

        viewHolder.mView.setClickable(true);
        viewHolder.mView.setOnClickListener(view -> {
            mActivity.getIntent().putExtra("bookId", bookList.get(i).getId());
            mActivity.setFragmentChild(new VolumeFragment(), bookList.get(i).getName());
        });
        viewHolder.mView.setLongClickable(true);
        viewHolder.mView.setOnLongClickListener(v -> {
            mMaterialDialog = new MaterialDialog(mActivity)
                    .setTitle("删除")
                    .setMessage(bookList.get(i).getName())
                    .setPositiveButton("确定", v1 -> {
                        File bookDir = new File(Constants.BOOK_DIR + File.separator + bookList.get(i).getId());
                        if (bookDir != null && bookDir.isDirectory()) {
                            if (FileUtils.delFolder(bookDir, System.currentTimeMillis())) {
                                mResolver.delete(Books.CONTENT_URI, Books.BOOK_ID + "=?", new String[]{bookList.get(i).getId()});
                                mResolver.delete(Volumes.CONTENT_URI, Volumes.BOOK_ID + "=?", new String[]{bookList.get(i).getId()});
                                mResolver.delete(Chapters.CONTENT_URI, Chapters.BOOK_ID + "=?", new String[]{bookList.get(i).getId()});
                                bookList.remove(i);
                                notifyDataSetChanged();
                                Toast.makeText(mActivity, "操作成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mActivity, "未成功删除数据", Toast.LENGTH_SHORT).show();
                            }
                        }
                        mMaterialDialog.dismiss();
                    })
                    .setNegativeButton("取消", v1 -> {
                        mMaterialDialog.dismiss();
                    });
            mMaterialDialog.show();
            return true;
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

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
import android.widget.Toast;

import java.io.File;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
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
    private MainActivity mActivity;
    private List<Book> bookList;
    private MaterialDialog mMaterialDialog;

    public  BookListAdapter (Activity activity) {
        this.mActivity = (MainActivity) activity;
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
        viewHolder.mView.setLongClickable(true);
        viewHolder.mView.setOnLongClickListener(v -> {
            mMaterialDialog = new MaterialDialog(mActivity)
                    .setTitle("删除")
                    .setMessage(bookList.get(i).getName())
                    .setPositiveButton("确定", v1 -> {
                        File bookDir = new File(Constants.BOOK_DIR + File.separator + bookList.get(i).getId());
                        if (bookDir != null && bookDir.isDirectory()) {
                            if (FileUtils.delFolder(bookDir, System.currentTimeMillis())) {
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

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getIntent().putExtra("bookId", bookList.get(i).getId());
                mActivity.setFragmentChild(new VolumeFragment(), bookList.get(i).getName());
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

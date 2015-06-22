package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.R;

/**
 * Created by ltype on 2015/5/16.
 */
public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
    private static String LOG_TAG = "MonthAdapter";
    private MainActivity mActivity;
    private List<Book> bookList = new ArrayList<>();
    private List<String> bookIdList;

    private MaterialDialog mMaterialDialog;

    private RequestQueue mQueue;

    public MonthAdapter(Activity activity, String month) {
        this.mActivity = (MainActivity) activity;
        ContentResolver mResolver = mActivity.getContentResolver();
        mQueue = Volley.newRequestQueue(activity);
        /*Cursor cursor = mResolver.query(LightNiwaDataStore.Bookmarks.CONTENT_URI, new String[]{LightNiwaDataStore.Bookmarks.POSITION},
                LightNiwaDataStore.Bookmarks.CHAPTER_ID + " = " + chapterIdList.get(chapterIndex), null, null);
        if (cursor != null && cursor.moveToFirst()){

        }*/

        StringRequest jsonObjectRequest = new StringRequest(
                Request.Method.GET,
                "http://ltype.me/api/v1/anime/" + month,
                response -> {
                    JSONArray jsonArray = JSON.parseArray(response);
                    if(jsonArray.isEmpty()) return;
                    for (int j = 0; j < jsonArray.size(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        Book book = new Book();
                        book.setId(jsonObject.getString("book_id"));
                        book.setName(jsonObject.getString("book_name"));
                        book.setAuthor(jsonObject.getString("book_author"));
                        book.setCover(jsonObject.getString("book_cover"));
                        bookList.add(book);
                    }
                    notifyDataSetChanged();
                },
                error -> {
                    Log.e(LOG_TAG, error.getMessage(), error);
                    Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                });
        mQueue.add(jsonObjectRequest);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    @Override
    public MonthAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_book, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        ImageLoader imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });

        ImageView imageView = (ImageView) viewHolder.mView.findViewById(R.id.book_card_cover);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.load_default, R.drawable.load_failed);
        imageLoader.get(Constants.SITE + bookList.get(i).getCover(), listener);

        TextView bookName = (TextView) viewHolder.mView.findViewById(R.id.book_card_name);
        bookName.setText(bookList.get(i).getName());

        TextView author = (TextView) viewHolder.mView.findViewById(R.id.book_card_author);
        author.setText(bookList.get(i).getAuthor());
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

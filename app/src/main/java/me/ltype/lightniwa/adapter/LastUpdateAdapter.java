package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
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
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;
import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.model.Volume;
import me.ltype.lightniwa.request.DownloadRequest;
import me.ltype.lightniwa.util.ApiUtil;
import me.ltype.lightniwa.util.Util;
import me.ltype.lightniwa.R;

/**
 * Created by ltype on 2015/5/16.
 */
public class LastUpdateAdapter extends RecyclerView.Adapter<LastUpdateAdapter.ViewHolder> {
    private static String LOG_TAG = "LastUpdateAdapter";
    private LayoutInflater inflater;
    private MainActivity activity;
    private List<Book> bookList = new ArrayList<>();
    private List<Volume> volumeList = new ArrayList<>();
    private MaterialDialog mMaterialDialog;
    private ProgressDialog progress;
    private ProgressDialog progressBar;
    private RequestQueue mQueue;

    private Handler mHandler = new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
            if(msg.what == Constants.PROGRESS_CANCEL){
                if (progress != null && progress.isShowing())
                    progress.dismiss();
                if (progressBar != null && progressBar.isShowing())
                    progressBar.dismiss();
            }
        }
    };

    public LastUpdateAdapter(Activity activity) {
        this.activity = (MainActivity) activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQueue = Volley.newRequestQueue(activity);
        progress = new ProgressDialog(activity);
        progressBar = new ProgressDialog(activity);

        if (Util.isConnect(activity)) {
            progress.setTitle("更新中...");
            progress.setMessage("获取最新数据");
            progress.setCancelable(true);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    ApiUtil.API_PATH + "latestPost",
                    response -> {
                        JSONArray jsonArray = JSON.parseObject(response).getJSONArray("latestPost");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Volume volume = new Volume();
                            volume.setIndex(json.getString("vol_number"));
                            volume.setBookId(json.getString("series_id"));
                            volume.setId(json.getString("id"));
                            volume.setHeader(json.getString("vol_number"));
                            volume.setName(json.getString("vol_title"));
                            volume.setCover(json.getString("vol_cover"));
                            volume.setDescription(json.getString("vol_desc"));
                            volumeList.add(volume);

                            Book book = new Book();
                            book.setAuthor(json.getString("novel_author"));
                            book.setIllustrator(json.getString("novel_illustor"));
                            book.setName(json.getString("novel_title"));
                            bookList.add(book);
                        }
                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(Constants.PROGRESS_CANCEL);
                    },
                    error -> {
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessage(Constants.PROGRESS_CANCEL);
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ApiUtil.getApiHeader();
                }
            };
            mQueue.add(stringRequest);
        } else {
            Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
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
    public LastUpdateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_other_book, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        ImageView imageView = (ImageView) viewHolder.mView.findViewById(R.id.card_other_book_cover);
        TextView bookName = (TextView) viewHolder.mView.findViewById(R.id.card_other_book_name);
        TextView volumeIndex = (TextView) viewHolder.mView.findViewById(R.id.card_other_volume_index);
        TextView volumeName = (TextView) viewHolder.mView.findViewById(R.id.card_other_volume_name);
        TextView author = (TextView) viewHolder.mView.findViewById(R.id.card_other_book_author);

        ImageLoader imageLoader = new ImageLoader(mQueue, new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.load_default, R.drawable.load_failed);
        imageLoader.get(Constants.SITE + volumeList.get(i).getCover(), listener);

        bookName.setText(bookList.get(i).getName());
        volumeIndex.setText("第" + volumeList.get(i).getHeader() + "卷");
        volumeName.setText(volumeList.get(i).getName());
        author.setText(bookList.get(i).getAuthor());

        viewHolder.mView.setOnClickListener(view -> {
            mMaterialDialog = new MaterialDialog(view.getContext())
                    .setTitle("下载")
                    .setMessage("<<" + bookList.get(i).getName() + ">>\n第" + volumeList.get(i).getHeader() + "卷:" + volumeList.get(i).getName())
                    .setPositiveButton("确定", v -> {
                        startDown(volumeList.get(i), bookList.get(i));
                        mMaterialDialog.dismiss();
                    })
                    .setNegativeButton("取消", v -> {
                        mMaterialDialog.dismiss();
                    });
            mMaterialDialog.show();
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

    private void startDown(Volume volume, Book book) {
        progressBar.setTitle("下载中...");
        progressBar.setMessage(book.getName() + "\n" + volume.getName());
        progressBar.setIndeterminate(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);
        progressBar.show();
        new DownloadRequest(activity, mQueue, mHandler).downBook(volume.getId());
    }
}

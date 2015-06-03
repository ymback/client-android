package me.ltype.lightreader.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import me.ltype.lightreader.R;
import me.ltype.lightreader.activity.MainActivity;
import me.ltype.lightreader.constant.Constants;
import me.ltype.lightreader.model.Book;
import me.ltype.lightreader.model.Volume;
import me.ltype.lightreader.task.DownImgTask;
import me.ltype.lightreader.task.DownloadTask;
import me.ltype.lightreader.util.ApiUtil;
import me.ltype.lightreader.util.Util;

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
            if(msg.what == 4662){
                bookList = ApiUtil.getLastBook();
                volumeList = ApiUtil.getLastVolume();
                notifyDataSetChanged();
                progress.cancel();
                progressBar.cancel();
            }
        };
    };

    public LastUpdateAdapter(Activity activity) {
        this.activity = (MainActivity) activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQueue = Volley.newRequestQueue(activity);
        progress = new ProgressDialog(activity);
        progressBar = new ProgressDialog(activity);

        progress.setTitle("更新中...");
        progress.setMessage("获取最新数据");
        progress.setCancelable(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        if (Util.isConnect(activity)) {
            new Thread(){
                public void run() {
                    ApiUtil.initLatestPost();
                    mHandler.sendEmptyMessage(4662);
                }
            }.start();
        } else {
            mMaterialDialog = new MaterialDialog(activity)
                    .setTitle("错误")
                    .setMessage("未连接网络")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
            mHandler.sendEmptyMessage(4662);
            mMaterialDialog.show();
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
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_volume, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final ImageView imageView = (ImageView) viewHolder.mView.findViewById(R.id.book_card_cover);
        TextView bookName = (TextView) viewHolder.mView.findViewById(R.id.book_card_name);
        TextView volName = (TextView) viewHolder.mView.findViewById(R.id.volume_name);

        if (volumeList == null || volumeList.isEmpty()) return;
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


        volName.setText("第" + volumeList.get(i).getHeader() + "卷");
        volName.append("  ");
        volName.append(volumeList.get(i).getName());

        TextView author = (TextView) viewHolder.mView.findViewById(R.id.book_card_author);
        author.setText(bookList.get(i).getAuthor());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog = new MaterialDialog(view.getContext())
                        .setTitle("下载")
                        .setMessage("确定下载" + bookList.get(i).getName() + volumeList.get(i).getHeader() + volumeList.get(i).getName() + "?")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startDown(volumeList.get(i), bookList.get(i));
                                mMaterialDialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
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

    private void startDown(Volume volume, Book book) {
        progressBar.setTitle("下载中...");
        progressBar.setMessage(book.getName() + "\n" + volume.getName());
        progressBar.setIndeterminate(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.show();
        StringBuffer bookJson = new StringBuffer();
        bookJson.append("{")
                .append("\"book_id\":" + volume.getBookId() + ",")
                .append("\"author\":" + "\"" + book.getAuthor() + "\",")
                .append("\"illustor\":" + "\"" + book.getIllustrator() + "\",")
                .append("\"publisher\":" + "\"" + book.getPublisher() + "\",")
                .append("\"name\":" + "\"" + book.getName() + "\",")
                .append("\"cover\":" + "\"" + Util.toCover(volume.getId(), Constants.SITE + volume.getCover()) + "\",")
                .append("\"description\":" + "\"" + volume.getDescription() + "\"")
                .append("}");
        new DownloadTask(mHandler).execute(volume.getId(), bookJson.toString());
    }

    private void asyncLoadImage(ImageView imageView, String path) {
        DownImgTask task = new DownImgTask(imageView);
        task.execute(path);
    }
}

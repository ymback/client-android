package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.ltype.lightniwa.R;
import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.fragment.VolumeFragment;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.model.Volume;
import me.ltype.lightniwa.request.DownloadRequest;
import me.ltype.lightniwa.util.ApiUtil;
import me.ltype.lightniwa.util.Util;

/**
 * Created by ltype on 2015/5/16.
 */
public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
    private static String LOG_TAG = "LastUpdateAdapter";
    private LayoutInflater inflater;
    private MainActivity mActivity;
    private List<Book> bookList = new ArrayList<>();
    private List<Volume> volumeList = new ArrayList<>();
    private ProgressDialog progress;
    private ProgressDialog progressBar;
    private ProgressView pv_circular_inout_colors;
    private RequestQueue mQueue;

    private Handler mHandler = new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
            if(msg.what == Constants.PROGRESS_CANCEL){
                if (progress != null && progress.isShowing())
                    progress.dismiss();
                if (progressBar != null && progressBar.isShowing())
                    progressBar.dismiss();
                if (pv_circular_inout_colors != null && pv_circular_inout_colors.isShown())
                    pv_circular_inout_colors.stop();
            }
        }
    };

    public PopularAdapter(Activity activity, Fragment fragment) {
        this.mActivity = (MainActivity) activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQueue = Volley.newRequestQueue(activity);
        progress = new ProgressDialog(activity);
        progressBar = new ProgressDialog(activity);
        pv_circular_inout_colors = (ProgressView) fragment.getView().findViewById(R.id.progress_pv_circular_inout_colors);
        pv_circular_inout_colors.start();

        if (Util.isConnect(mActivity)) {
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "http://ltype.me/api/v1/popular",
                    response -> {
                        JSONArray jsonArray = JSON.parseArray(response);
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Volume volume = new Volume();
                            volume.setIndex(json.getString("vol_number"));
                            volume.setBookId(json.getString("book_id"));
                            volume.setId(json.getString("volume_id"));
                            volume.setHeader(json.getString("volume_index"));
                            volume.setName(json.getString("volume_name"));
                            volume.setCover(json.getString("volume_cover"));
                            volume.setDescription(json.getString("volume_description"));
                            volumeList.add(volume);

                            Book book = new Book();
                            book.setId(json.getString("book_id"));
                            book.setAuthor(json.getString("author"));
                            book.setIllustrator(json.getString("illustrator"));
                            book.setPublisher(json.getString("publisher"));
                            book.setName(json.getString("book_name"));
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
    public PopularAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) viewHolder.mView.findViewById(R.id.book_card_cover);
        TextView bookName = (TextView) viewHolder.mView.findViewById(R.id.book_card_name);
        TextView author = (TextView) viewHolder.mView.findViewById(R.id.book_card_author);
        TextView illustrator = (TextView) viewHolder.mView.findViewById(R.id.book_card_illustrator);
        TextView publisher = (TextView) viewHolder.mView.findViewById(R.id.book_card_publisher);

        File imgFile = new  File(Constants.BOOK_DIR + File.separator + bookList.get(position).getId() + bookList.get(position).getCover());
        if(imgFile.exists()){
            simpleDraweeView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
        } else {
            simpleDraweeView.setImageURI(Uri.parse(Constants.SITE + volumeList.get(position).getCover()));
        }

        bookName.setText(bookList.get(position).getName());
        author.setText(bookList.get(position).getAuthor());
        illustrator.setText(bookList.get(position).getIllustrator());
        publisher.setText(bookList.get(position).getPublisher());

        viewHolder.mView.setOnClickListener(view -> {
            VolumeFragment volumeFragment = new VolumeFragment();
            mActivity.getIntent().putExtra("bookId", bookList.get(position).getId());
            mActivity.setFragmentChild(volumeFragment, bookList.get(position).getName());
        });

        viewHolder.mView.setLongClickable(true);
        viewHolder.mView.setOnLongClickListener(view -> {
            com.rey.material.app.Dialog.Builder builder = null;
            builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    startDown(volumeList.get(position), bookList.get(position));
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };

            ((SimpleDialog.Builder) builder).message("<<" + bookList.get(position).getName() + ">>\n" + volumeList.get(position).getHeader() + ":" + volumeList.get(position).getName())
                    .title("下载")
                    .positiveAction("确定")
                    .negativeAction("取消");

            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(mActivity.getSupportFragmentManager(), null);
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

    private void startDown(Volume volume, Book book) {
        progressBar.setTitle("下载中...");
        progressBar.setMessage(book.getName() + "\n" + volume.getName());
        progressBar.setIndeterminate(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);
        progressBar.show();
        new DownloadRequest(mActivity, mQueue, mHandler).downBook(volume.getId());
    }
}

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
import android.view.animation.AnimationUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.fragment.VolumeFragment;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.model.Volume;
import me.ltype.lightniwa.request.DownloadRequest;
import me.ltype.lightniwa.util.AnimationUtil;
import me.ltype.lightniwa.util.ApiUtil;
import me.ltype.lightniwa.util.Util;
import me.ltype.lightniwa.R;

/**
 * Created by ltype on 2015/5/16.
 */
public class LastUpdateAdapter extends RecyclerView.Adapter<LastUpdateAdapter.ViewHolder> {
    private static String LOG_TAG = "LastUpdateAdapter";
    private LayoutInflater inflater;
    private MainActivity mActivity;
    private Fragment mFragment;
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
                if (pv_circular_inout_colors != null && pv_circular_inout_colors.isShown()) {
                    pv_circular_inout_colors.stop();
                    RecyclerView mRecyclerView = (RecyclerView) mFragment.getView().findViewById(R.id.list_view_book);
                    mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_list_view));
                    notifyDataSetChanged();
                }
            }
        }
    };

    public LastUpdateAdapter(Activity activity, Fragment fragment) {
        this.mActivity = (MainActivity) activity;
        this.mFragment = fragment;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQueue = Volley.newRequestQueue(activity);
        progress = new ProgressDialog(activity);
        progressBar = new ProgressDialog(activity);
        pv_circular_inout_colors = (ProgressView) fragment.getView().findViewById(R.id.progress_pv_circular_inout_colors);
        pv_circular_inout_colors.start();

        if (Util.isConnect(mActivity)) {
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
                            book.setId(json.getString("series_id"));
                            book.setAuthor(json.getString("novel_author"));
                            book.setIllustrator(json.getString("novel_illustor"));
                            book.setName(json.getString("novel_title"));
                            bookList.add(book);
                        }
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
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_book, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) viewHolder.mView.findViewById(R.id.card_other_book_cover);
        TextView bookName = (TextView) viewHolder.mView.findViewById(R.id.card_other_book_name);
        TextView volumeIndex = (TextView) viewHolder.mView.findViewById(R.id.card_other_volume_index);
        TextView volumeName = (TextView) viewHolder.mView.findViewById(R.id.card_other_volume_name);
        TextView author = (TextView) viewHolder.mView.findViewById(R.id.card_other_book_author);

        simpleDraweeView.setImageURI(Uri.parse(Constants.SITE + volumeList.get(position).getCover()));

        bookName.setText(bookList.get(position).getName());
        volumeIndex.setText("第" + volumeList.get(position).getHeader() + "卷");
        volumeName.setText(volumeList.get(position).getName());
        author.setText(bookList.get(position).getAuthor());

        viewHolder.mView.setOnClickListener(view -> {
            VolumeFragment volumeFragment = new VolumeFragment();
            mActivity.getIntent().putExtra("bookId", bookList.get(position).getId());
            mActivity.setFragmentChild(volumeFragment, bookList.get(position).getName());
        });

        viewHolder.mView.setLongClickable(true);
        viewHolder.mView.setOnLongClickListener(view -> {
            com.rey.material.app.Dialog.Builder builder = null;
            builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
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

            ((SimpleDialog.Builder)builder).message("<<" + bookList.get(position).getName() + ">>\n第" + volumeList.get(position).getHeader() + "卷:" + volumeList.get(position).getName())
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

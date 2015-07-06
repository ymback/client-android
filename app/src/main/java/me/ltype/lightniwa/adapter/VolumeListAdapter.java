package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.db.LightNiwaDataStore;
import me.ltype.lightniwa.db.LightNiwaDataStore.Volumes;
import me.ltype.lightniwa.db.LightNiwaDataStore.Chapters;
import me.ltype.lightniwa.model.Volume;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.fragment.ChapterFragment;
import me.ltype.lightniwa.request.DownloadRequest;
import me.ltype.lightniwa.util.AnimationUtil;
import me.ltype.lightniwa.util.ApiUtil;
import me.ltype.lightniwa.util.FileUtils;
import me.ltype.lightniwa.util.Util;

/**
 * Created by ltype on 2015/5/16.
 */
public class VolumeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String LOG_TAG = "VolumeListAdapter";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ContentResolver mResolver;
    private MainActivity mActivity;
    private Fragment mFragment;
    private ProgressDialog progressBar;
    private ProgressView pv_circular_inout_colors;
    private ItemViewHolder DLItemView = null;

    private List<Volume> volumeList = new ArrayList<>();

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what == Constants.PROGRESS_CANCEL){
                if (progressBar != null && progressBar.isShowing())
                    progressBar.dismiss();
                if (DLItemView != null) {
                    DLItemView.mView.setOnClickListener(view -> {
                        mActivity.getIntent().putExtra("volumeId", volumeList.get(DLItemView.position - 1).getId());
                        Fragment fragment = new ChapterFragment();
                        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
                        transaction.add(fragment, "ChapterFragment");
                        transaction.commit();
                        mActivity.setFragmentChild(new ChapterFragment(), volumeList.get(DLItemView.position).getName());
                    });
                    DLItemView.mView.setAlpha(1);
                }
                if (pv_circular_inout_colors != null && pv_circular_inout_colors.isShown()) {
                    pv_circular_inout_colors.stop();
                    RecyclerView mRecyclerView = (RecyclerView) mFragment.getView().findViewById(R.id.list_view_book);
                    mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_list_view));
                    notifyDataSetChanged();
                }
            }
        }
    };

    public VolumeListAdapter(Activity activity, Fragment fragment) {
        this.mActivity = (MainActivity) activity;
        this.mFragment = fragment;
        mResolver = mActivity.getContentResolver();
        progressBar = new ProgressDialog(mActivity);
        Bundle bundle = activity.getIntent().getExtras();
        String bookId = bundle.getString("bookId");

        if(Util.isConnect(mActivity)) {
            pv_circular_inout_colors = (ProgressView) mFragment.getView().findViewById(R.id.progress_pv_circular_inout_colors);
            pv_circular_inout_colors.start();
            StringRequest jsonObjectRequest = new StringRequest(
                    Request.Method.GET,
                    ApiUtil.API_PATH + "book/" + bookId,
                    response -> {
                        JSONArray jsonArray = JSON.parseObject(response).getJSONArray("volResult");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            Volume volume = ApiUtil.getVolByJsonObj(jsonArray.getJSONObject(i));
                            volumeList.add(volume);
                        }
                        mHandler.sendEmptyMessage(Constants.PROGRESS_CANCEL);
                    },
                    error -> {
                        Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                        loadDataByLocal(bookId);
                        mHandler.sendEmptyMessage(Constants.PROGRESS_CANCEL);
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return ApiUtil.getApiHeader();
                }
            };
            Volley.newRequestQueue(mActivity).add(jsonObjectRequest);
        } else {
            loadDataByLocal(bookId);
            mHandler.sendEmptyMessage(Constants.PROGRESS_CANCEL);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if(viewType == TYPE_HEADER) {
            View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_header, parent, false);
            vh = new HeaderViewHolder(currentView);
        } else {
            View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_volume, parent, false);
            vh = new ItemViewHolder(currentView, viewType);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if(viewHolder instanceof HeaderViewHolder) {
            if(volumeList.size() > 0) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                TextView textView = (TextView) headerViewHolder.mView.findViewById(R.id.list_view_header);
                textView.setText("\u3000\u3000" + volumeList.get(0).getDescription());
            }
            return;
        }
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        int i = itemViewHolder.position - 1;
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) itemViewHolder.mView.findViewById(R.id.card_volume_cover);
        File imgFile = new  File(Constants.BOOK_DIR + File.separator + volumeList.get(i).getBookId() + volumeList.get(i).getCover());
        if(imgFile.exists()){
            simpleDraweeView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
        } else {
            simpleDraweeView.setImageURI(Uri.parse(Constants.SITE + "/" + volumeList.get(i).getCover()));
        }
        TextView indexTV = (TextView) itemViewHolder.mView.findViewById(R.id.card_volume_index);
        indexTV.setText(volumeList.get(i).getIndex());

        TextView nameTV = (TextView) itemViewHolder.mView.findViewById(R.id.card_volume_name);
        nameTV.setText(volumeList.get(i).getName());

        View.OnClickListener onClickListener = null;
        Cursor cursor = mResolver.query(Volumes.CONTENT_URI, Volumes.COLUMNS, Volumes.VOLUME_ID + "=?", new String[]{volumeList.get(i).getId()}, null);
        if (cursor != null && cursor.getCount() > 0){
            onClickListener = view -> {
                mActivity.getIntent().putExtra("volumeId", volumeList.get(i).getId());
                Fragment fragment = new ChapterFragment();
                FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
                transaction.add(fragment, "ChapterFragment");
                transaction.commit();
                mActivity.setFragmentChild(new ChapterFragment(), volumeList.get(i).getName());
            };
        } else {
            itemViewHolder.mView.setAlpha(0.5f);
            onClickListener = view -> {
                com.rey.material.app.Dialog.Builder builder = null;
                builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        startDown(volumeList.get(i));
                        DLItemView = itemViewHolder;
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                ((SimpleDialog.Builder)builder).message(volumeList.get(i).getHeader() + ":" + volumeList.get(i).getName())
                        .title("下载")
                        .positiveAction("确定")
                        .negativeAction("取消");

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(mActivity.getSupportFragmentManager(), null);
            };
        }
        cursor.close();
        itemViewHolder.mView.setClickable(true);
        itemViewHolder.mView.setOnClickListener(onClickListener);

        itemViewHolder.mView.setLongClickable(true);
        itemViewHolder.mView.setOnLongClickListener(v -> {
            com.rey.material.app.Dialog.Builder builder = null;
            builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    File volumeDir = new File(Constants.BOOK_DIR + File.separator + volumeList.get(i).getBookId() + File.separator + volumeList.get(i).getId());
                    if (volumeDir != null && volumeDir.isDirectory()) {
                        Cursor cursor = mResolver.query(Volumes.CONTENT_URI, Volumes.COLUMNS, Volumes.BOOK_ID + "=?", new String[]{volumeList.get(i).getBookId()}, null);
                        if (cursor != null && cursor.getCount() <= 1){
                            Log.i(LOG_TAG, cursor.getCount() + "cursor != null && cursor.getCount() <= 0");
                            FileUtils.delFolder(new File(Constants.BOOK_DIR + File.separator + volumeList.get(i).getBookId()), System.currentTimeMillis());
                            mResolver.delete(LightNiwaDataStore.Books.CONTENT_URI, LightNiwaDataStore.Books.BOOK_ID + "=?", new String[]{volumeList.get(i).getBookId()});
                        } else {
                            FileUtils.delFolder(volumeDir, System.currentTimeMillis());
                        }
                        mResolver.delete(Volumes.CONTENT_URI, Volumes.VOLUME_ID + "=?", new String[]{volumeList.get(i).getId()});
                        mResolver.delete(Chapters.CONTENT_URI, Chapters.VOLUME_ID + "=?", new String[]{volumeList.get(i).getId()});

                        itemViewHolder.mView.setAlpha(0.5f);
//                            volumeList.remove(i);
                        notifyDataSetChanged();
                        Toast.makeText(mActivity, "操作成功", Toast.LENGTH_SHORT).show();
                    }
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };

            ((SimpleDialog.Builder)builder).message(volumeList.get(i).getName())
                    .title("删除")
                    .positiveAction("确定")
                    .negativeAction("取消");

            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(mActivity.getSupportFragmentManager(), null);
            return true;
        });
    }

    @Override
    public long getItemId(int i) {
        return Long.getLong(volumeList.get(i).getBookId());
    }

    @Override
    public int getItemCount() {
        return volumeList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        public HeaderViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private int position;
        public ItemViewHolder(View v, int viewType) {
            super(v);
            mView = v;
            position = viewType;
        }
    }

    private void startDown(Volume volume) {
        progressBar.setTitle(volume.getName());
        progressBar.setMessage("下载中...");
        progressBar.setIndeterminate(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);
        progressBar.show();
        new DownloadRequest(mActivity, Volley.newRequestQueue(mActivity), mHandler).downBook(volume.getId());
    }

    private void loadDataByLocal(String bookId) {
        Cursor cursor = mResolver.query(Volumes.CONTENT_URI, Volumes.COLUMNS, Volumes.BOOK_ID + "=?", new String[]{bookId}, null);
        if (cursor != null && cursor.moveToFirst()){
            while(!cursor.isAfterLast()) {
                Volume volume = new Volume();
                volume.setBookId(cursor.getString(cursor.getColumnIndex("book_id")));
                volume.setId(cursor.getString(cursor.getColumnIndex("volume_id")));
                volume.setIndex(cursor.getString(cursor.getColumnIndex("volume_index")));
                volume.setHeader(cursor.getString(cursor.getColumnIndex("header")));
                volume.setName(cursor.getString(cursor.getColumnIndex("name")));
                volume.setCover(cursor.getString(cursor.getColumnIndex("cover")));
                volume.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                volumeList.add(volume);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }
}

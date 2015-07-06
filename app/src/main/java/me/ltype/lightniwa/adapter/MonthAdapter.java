package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rey.material.widget.ProgressView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.fragment.VolumeFragment;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.util.AnimationUtil;

/**
 * Created by ltype on 2015/5/16.
 */
public class MonthAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String LOG_TAG = "MonthAdapter";
    private MainActivity mActivity;
    private Fragment mFragment;
    private RequestQueue mQueue;
    private ProgressView pv_circular_inout_colors;

    private List<Book> bookList = new ArrayList<>();
    private List<String> bookIdList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what == Constants.PROGRESS_CANCEL){
                if (pv_circular_inout_colors != null && pv_circular_inout_colors.isShown()) {
                    pv_circular_inout_colors.stop();
                    RecyclerView mRecyclerView = (RecyclerView) mFragment.getView().findViewById(R.id.list_view_book);
                    mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_list_view));
                    notifyDataSetChanged();
                }
            }
        }
    };

    public MonthAdapter(Activity activity, Fragment fragment, String month) {
        this.mActivity = (MainActivity) activity;
        this.mFragment = fragment;
        pv_circular_inout_colors = (ProgressView) mFragment.getView().findViewById(R.id.progress_pv_circular_inout_colors);
        pv_circular_inout_colors.start();
        mQueue = Volley.newRequestQueue(mActivity);
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
                        book.setAuthor(jsonObject.getString("author"));
                        book.setIllustrator(jsonObject.getString("illustrator"));
                        book.setPublisher(jsonObject.getString("publisher"));
                        book.setCover(jsonObject.getString("book_cover"));
                        bookList.add(book);
                    }
                    mHandler.sendEmptyMessage(Constants.PROGRESS_CANCEL);
                },
                error -> {
                    Log.e(LOG_TAG, error.getMessage(), error);
                    Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                });
        mQueue.add(jsonObjectRequest);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        Log.i(LOG_TAG, (recyclerView == null) + "" + getItemCount());
//        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        RecyclerView.ViewHolder vh = null;
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        vh = new ItemViewHolder(currentView, viewType);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) itemViewHolder.mView.findViewById(R.id.book_card_cover);
        TextView bookName = (TextView) itemViewHolder.mView.findViewById(R.id.book_card_name);
        TextView author = (TextView) itemViewHolder.mView.findViewById(R.id.book_card_author);
        TextView illustrator = (TextView) itemViewHolder.mView.findViewById(R.id.book_card_illustrator);
        TextView publisher = (TextView) itemViewHolder.mView.findViewById(R.id.book_card_publisher);

        File imgFile = new  File(Constants.BOOK_DIR + File.separator + bookList.get(position).getId() + bookList.get(position).getCover());
        if(imgFile.exists()){
            simpleDraweeView.setImageURI(Uri.parse("file://" + imgFile.getAbsolutePath()));
        } else {
            simpleDraweeView.setImageURI(Uri.parse(Constants.SITE + bookList.get(position).getCover()));
        }
        bookName.setText(bookList.get(position).getName());
        author.setText(bookList.get(position).getAuthor());
        illustrator.setText(bookList.get(position).getIllustrator());
        publisher.setText(bookList.get(position).getPublisher());

        itemViewHolder.mView.setClickable(true);
        itemViewHolder.mView.setOnClickListener(view -> {
            VolumeFragment volumeFragment = new VolumeFragment();
            mActivity.getIntent().putExtra("bookId", bookList.get(position).getId());
            mActivity.setFragmentChild(volumeFragment, bookList.get(position).getName());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
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
}

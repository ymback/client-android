package me.ltype.lightniwa.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
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
import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.db.LightNiwaDataStore.Volumes;
import me.ltype.lightniwa.db.LightNiwaDataStore.Chapters;
import me.ltype.lightniwa.model.Volume;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.fragment.ChapterFragment;
import me.ltype.lightniwa.util.FileUtils;

/**
 * Created by ltype on 2015/5/16.
 */
public class VolumeListAdapter extends RecyclerView.Adapter<VolumeListAdapter.ViewHolder> {
    private static String LOG_TAG = "VolumeListAdapter";

    private ContentResolver mResolver;
    private MainActivity mActivity;
    private MaterialDialog mMaterialDialog;

    private List<Volume> volumeList = new ArrayList<>();

    public VolumeListAdapter(Activity activity) {
        this.mActivity = (MainActivity) activity;
        Bundle bundle = activity.getIntent().getExtras();
        String bookId = bundle.getString("bookId");

        mResolver = activity.getContentResolver();
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
    public VolumeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View currentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_volume, parent, false);
        ViewHolder vh = new ViewHolder(currentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        ImageView imageView = (ImageView) viewHolder.mView.findViewById(R.id.card_volume_cover);
        File imgFile = new  File(Constants.BOOK_DIR + File.separator + volumeList.get(i).getBookId() + volumeList.get(i).getCover());
        if(imgFile.exists()){
            Bitmap mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(mBitmap);
        }

        TextView indexTV = (TextView) viewHolder.mView.findViewById(R.id.card_volume_index);
        indexTV.setText("第" + volumeList.get(i).getIndex() + "卷");


        TextView nameTV = (TextView) viewHolder.mView.findViewById(R.id.card_volume_name);
        nameTV.setText(volumeList.get(i).getName());

        viewHolder.mView.setClickable(true);
        viewHolder.mView.setOnClickListener(view -> {
            mActivity.getIntent().putExtra("bookId", volumeList.get(i).getBookId());
            mActivity.getIntent().putExtra("volumeId", volumeList.get(i).getId());
            Fragment fragment = new ChapterFragment();
            FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
            transaction.add(fragment, "ChapterFragment");
            transaction.commit();
            mActivity.setFragmentChild(new ChapterFragment(), volumeList.get(i).getName());
        });

        viewHolder.mView.setLongClickable(true);
        viewHolder.mView.setOnLongClickListener(v -> {
            mMaterialDialog = new MaterialDialog(mActivity)
                    .setTitle("删除")
                    .setMessage(volumeList.get(i).getName())
                    .setPositiveButton("确定", v1 -> {
                        File volumeDir = new File(Constants.BOOK_DIR + File.separator + volumeList.get(i).getBookId() + File.separator + volumeList.get(i).getId());
                        if (volumeDir != null && volumeDir.isDirectory()) {
                            if (FileUtils.delFolder(volumeDir, System.currentTimeMillis())) {
                                mResolver.delete(Volumes.CONTENT_URI, Volumes.VOLUME_ID + "=?", new String[]{volumeList.get(i).getId()});
                                mResolver.delete(Chapters.CONTENT_URI, Chapters.VOLUME_ID + "=?", new String[]{volumeList.get(i).getId()});
                                volumeList.remove(i);
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
        return Long.getLong(volumeList.get(i).getBookId());
    }

    @Override
    public int getItemCount() {
        return volumeList.size();
    }
}

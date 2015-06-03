package me.ltype.lightreader.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.ltype.lightreader.R;
import me.ltype.lightreader.activity.MainActivity;
import me.ltype.lightreader.adapter.BookListAdapter;
import me.ltype.lightreader.adapter.VolumeListAdapter;
import me.ltype.lightreader.model.Book;
import me.ltype.lightreader.util.FileUtils;

/**
 * Created by ltype on 2015/5/14.
 */
public class VolumeFragment extends Fragment {
    private static String LOG_TAG = "VolumeFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity mainActivity;

    @Override
    public void onAttach(Activity activity) {
        mainActivity = (MainActivity) activity;
        if(Build.VERSION.SDK_INT >= 21) {
            mainActivity.getToolbar().setElevation(12.0F);
            mainActivity.getSupportActionBar().setElevation(12.0F);
            Log.e(LOG_TAG, "setElevation");
        }
        super.onAttach(mainActivity);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = mainActivity.getIntent().getExtras();
        String bookId = bundle.getString("bookId");
        List<Book> bookList = FileUtils.getBookList();
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new VolumeListAdapter(mainActivity));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }
}

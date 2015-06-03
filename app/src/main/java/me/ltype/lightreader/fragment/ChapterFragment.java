package me.ltype.lightreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.ltype.lightreader.R;
import me.ltype.lightreader.adapter.ChapterListAdapter;
import me.ltype.lightreader.adapter.VolumeListAdapter;
import me.ltype.lightreader.model.Book;
import me.ltype.lightreader.util.FileUtils;

/**
 * Created by ltype on 2015/5/14.
 */
public class ChapterFragment extends Fragment {
    private static String LOG_TAG = "VolumeFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String bookId;
    private String volumeId;

    public ChapterFragment() {

    }

    public ChapterFragment(String bookId, String volumeId) {
        this.bookId = bookId;
        this.volumeId = volumeId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            bookId = bundle.getString("bookId");
            volumeId = bundle.getString("volumeId");
        }

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.list_view, container, false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(inflater.getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        if (savedInstanceState != null) {
            bookId = savedInstanceState.getString("bookId");
            volumeId = savedInstanceState.getString("volumeId");
        }
        mRecyclerView.setAdapter(new ChapterListAdapter(getActivity(), bookId, volumeId));

        return mRecyclerView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("bookId", bookId);
        outState.putString("volumeId", volumeId);
    }
}

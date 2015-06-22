package me.ltype.lightniwa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ltype.lightniwa.adapter.ChapterListAdapter;
import me.ltype.lightniwa.R;

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

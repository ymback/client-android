package me.ltype.lightniwa.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.ltype.lightniwa.adapter.BookListAdapter;
import me.ltype.lightniwa.adapter.VolumeListAdapter;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.util.FileUtils;

/**
 * Created by ltype on 2015/5/14.
 */
public class VolumeFragment extends Fragment {
    private static String LOG_TAG = "VolumeFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        mActivity = (MainActivity) getActivity();
        mActivity.enableToolbarElevation();
        super.onAttach(mActivity);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter = new BookListAdapter(getActivity());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new VolumeListAdapter(mActivity));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }
}

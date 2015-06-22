package me.ltype.lightniwa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ltype.lightniwa.R;
import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.adapter.SearchResultListAdapter;

/**
 * Created by ltype on 2015/5/14.
 */
public class SearchResultFragment extends Fragment {
    private static String LOG_TAG = "SearchResultFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String query;
    private MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mActivity.enableToolbarElevation();
        this.query = getActivity().getIntent().getStringExtra("query");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.list_view, container, false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(inflater.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SearchResultListAdapter(getActivity(), query);
        mRecyclerView.setAdapter(mAdapter);
        return mRecyclerView;
    }
}
package me.ltype.lightreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.ltype.lightreader.R;
import me.ltype.lightreader.adapter.SearchResultListAdapter;
import me.ltype.lightreader.model.Volume;

/**
 * Created by ltype on 2015/5/14.
 */
public class SearchResultFragment extends Fragment {
    private static String LOG_TAG = "SearchResultFragment";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String query;
    private SearchView mSearchView;
    private final List<Volume> volumeList = new ArrayList<>();

    public SearchResultFragment(String query) {
        this.query = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.list_view, container, false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(inflater.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new SearchResultListAdapter(getActivity(), query));
        return mRecyclerView;
    }
}

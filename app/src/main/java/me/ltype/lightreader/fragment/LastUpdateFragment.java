package me.ltype.lightreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ltype.lightreader.R;
import me.ltype.lightreader.activity.MainActivity;
import me.ltype.lightreader.adapter.BookListAdapter;
import me.ltype.lightreader.adapter.LastUpdateAdapter;

/**
 * Created by ltype on 2015/5/14.
 */
public class LastUpdateFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.enableToolbarElevation();
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.list_view, container, false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(inflater.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new LastUpdateAdapter(getActivity()));
        return mRecyclerView;
    }
}

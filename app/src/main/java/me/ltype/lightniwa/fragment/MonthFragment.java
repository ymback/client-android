package me.ltype.lightniwa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.ProgressView;

import me.ltype.lightniwa.R;
import me.ltype.lightniwa.adapter.MonthAdapter;
import me.ltype.lightniwa.util.AnimationUtil;

/**
 * Created by ltype on 2015/6/14.
 */
public class MonthFragment extends Fragment {
    private static String LOG_TAG = "MonthFragment";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressView pv_circular_inout_colors;

    private String month = "1";

    @Override
    public void onViewCreated(View parent, Bundle savedInstanceState) {
        super.onViewCreated(parent, savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
            month = bundle.getString("month");
        mRecyclerView = (RecyclerView) parent.findViewById(R.id.list_view_book);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(parent.getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MonthAdapter(getActivity(), this, month);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container, false);
    }
}

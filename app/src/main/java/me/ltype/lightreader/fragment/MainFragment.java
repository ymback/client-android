package me.ltype.lightreader.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayList;
import java.util.List;

import me.ltype.lightreader.R;
import me.ltype.lightreader.activity.MainActivity;
import me.ltype.lightreader.adapter.ViewPagerAdapter;
import me.ltype.lightreader.view.SlidingTabLayout;

/**
 * Created by ltype on 2015/5/19.
 */
public class MainFragment extends Fragment {
    private static String LOG_TAG = "MainFragment";
    private List<Fragment> fragmentList;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private String[] tabsName;

    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabsName = getResources().getStringArray(R.array.tabs_name);

        MainActivity mainActivity = (MainActivity) getActivity();
        if(Build.VERSION.SDK_INT >= 21) {
            mainActivity.getToolbar().setElevation(0F);
            mainActivity.getSupportActionBar().setElevation(0F);
        }
        fragmentList = new ArrayList();
        fragmentList.add(new MyBookFragment());
        fragmentList.add(new PopFragment());

        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList, tabsName);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        tabs.setViewPager(mViewPager);

        /*mLinearLayout = (LinearLayout) inflater.inflate(R.layout.activity_main, container, false);
        MainActivity mActivity = (MainActivity) getActivity();
        mActivity.addMultiPaneSupport();

        String[] tabsName = getResources().getStringArray(R.array.tabs_name);

        fragments = new ArrayList();
        fragments.add(new MyBookFragment());
        fragments.add(new GetMoreFragment());
        fragments.add(new MyBookFragment());

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragments, tabsName);

        viewPager = (ViewPager) mLinearLayout.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(viewPagerAdapter);

        slidingTabLayout = (SlidingTabLayout) mLinearLayout.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);

        return mLinearLayout;*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }
}

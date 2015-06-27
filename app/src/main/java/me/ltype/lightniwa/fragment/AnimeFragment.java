package me.ltype.lightniwa.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import me.ltype.lightniwa.R;
import me.ltype.lightniwa.activity.MainActivity;
import me.ltype.lightniwa.adapter.ViewPagerAdapter;
import me.ltype.lightniwa.view.SlidingTabLayout;

/**
 * Created by ltype on 2015/5/19.
 */
public class AnimeFragment extends Fragment {
    private static String LOG_TAG = "AnimeFragment";
    private List<Fragment> fragmentList;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout slidingTabLayout;
    private LinearLayout mLinearLayout;
    private String[] tabsName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mActivity = (MainActivity) getActivity();
        mActivity.disableToolbarElevation();
        mViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle jan = new Bundle();
        jan.putString("month", "1");
        Fragment winter = new MonthFragment();
        winter.setArguments(jan);

        Bundle apr = new Bundle();
        apr.putString("month", "4");
        Fragment spring = new MonthFragment();
        spring.setArguments(apr);

        Bundle jul = new Bundle();
        jul.putString("month", "7");
        Fragment summer = new MonthFragment();
        summer.setArguments(jul);

        Bundle oct = new Bundle();
        oct.putString("month", "10");
        Fragment autumn = new MonthFragment();
        autumn.setArguments(oct);

        fragmentList = new ArrayList();
        fragmentList.add(winter);
        fragmentList.add(spring);
        fragmentList.add(summer);
        fragmentList.add(autumn);

        tabsName = getResources().getStringArray(R.array.anime_tabs_name);
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList, tabsName);

        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }
}

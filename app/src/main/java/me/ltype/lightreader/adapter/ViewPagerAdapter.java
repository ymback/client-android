package me.ltype.lightreader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.ltype.lightreader.R;

/**
 * Created by ltype on 2015/5/14.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private String[] tabsName;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] tabsName) {
        super(fm);
        this.fragmentList = fragmentList;
        this.tabsName = tabsName;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabsName[position];
    }

    /*@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }*/
}

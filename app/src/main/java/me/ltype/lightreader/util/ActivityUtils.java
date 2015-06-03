package me.ltype.lightreader.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import me.ltype.lightreader.R;

/**
 * Created by ltype on 2015/5/18.
 */
public class ActivityUtils {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    public static void initToolBar(final ActionBarActivity actionBarActivity, final DrawerLayout mDrawerLayout, final ListView mDrawerList, final Toolbar mToolbar) {
        if (mToolbar != null) {
            actionBarActivity.setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_ab_drawer);
        }
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(actionBarActivity, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        String[] values = new String[]{
                "DEFAULT", "RED", "BLUE", "MATERIAL GREY"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBarActivity,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
            int position, long id) {
                /*switch (position) {
                    case 0:
                        mDrawerList.setBackgroundColor(actionBarActivity.getResources().getColor(R.color.material_deep_teal_500));
                        mToolbar.setBackgroundColor(actionBarActivity.getResources().getColor(R.color.material_deep_teal_500));
//                        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case 1:
                        mDrawerList.setBackgroundColor(actionBarActivity.getResources().getColor(R.color.red));
                        mToolbar.setBackgroundColor(actionBarActivity.getResources().getColor(R.color.red));
//                        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.red));
                        mDrawerLayout.closeDrawer(Gravity.START);

                        break;
                    case 2:
                        mDrawerList.setBackgroundColor(actionBarActivity.getResources().getColor(R.color.blue));
                        mToolbar.setBackgroundColor(actionBarActivity.getResources().getColor(R.color.blue));
//                        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.blue));
                        mDrawerLayout.closeDrawer(Gravity.START);

                        break;
                    case 3:
                        mDrawerList.setBackgroundColor(actionBarActivity.getResources().getColor(R.color.material_blue_grey_800));
                        mToolbar.setBackgroundColor(actionBarActivity.getResources().getColor(R.color.material_blue_grey_800));
//                        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
                        mDrawerLayout.closeDrawer(Gravity.START);

                        break;
                }*/

            }
        });
    }
}

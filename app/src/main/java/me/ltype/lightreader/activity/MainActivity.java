package me.ltype.lightreader.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;

import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushRegisterResult;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import me.ltype.lightreader.R;
import me.ltype.lightreader.fragment.AnimeFragment;
import me.ltype.lightreader.fragment.LastUpdateFragment;
import me.ltype.lightreader.fragment.MainFragment;
import me.ltype.lightreader.fragment.SearchResultFragment;

public class MainActivity extends MaterialNavigationDrawer {
    private static String LOG_TAG = "MainActivity";

    private Menu menu;
    private SearchView mSearchView;

    @Override
    public void init(Bundle savedInstanceState) {
        setDrawerHeaderImage(R.drawable.mat2);
        setUsername("ltype");
        setUserEmail("asuka@ltype.me");
        setFirstAccountPhoto(getResources().getDrawable(R.drawable.photo));

        this.addSection(newSection(getResources().getString(R.string.bookshelf), new MainFragment()));
        this.addSection(newSection(getResources().getString(R.string.last_update), new LastUpdateFragment()));
        this.addSection(newSection(getResources().getString(R.string.anime_aera), new AnimeFragment()));

        this.addBottomSection(newSection(getResources().getString(R.string.setting), R.drawable.ic_settings_black_24dp, new Intent(this, SettingActivity.class)));

//        XGPushConfig.enableDebug(this, true);
        XGPushManager.registerPush(getApplicationContext());
//        Log.e(LOG_TAG, XGPushConfig.getToken(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(LOG_TAG + "onQueryTextSubmit", query);
//                    if (mSearchResultFragment == null) mSearchResultFragment = new SearchResultFragment();
                    openChildFragment(new SearchResultFragment(query), "搜索:" + query);
                    mSearchView.onActionViewCollapsed();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    Log.d(LOG_TAG + "onQueryTextChange", query);
                    return true;
                }
            });
            final SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    mSearchView.onActionViewCollapsed();
                    return false;
                }
            };
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void openChildFragment(Fragment childFragment, String titile) {
        this.setFragmentChild(childFragment, titile);
    }
}

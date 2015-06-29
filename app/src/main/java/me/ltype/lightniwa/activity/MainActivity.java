package me.ltype.lightniwa.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import me.ltype.lightniwa.R;
import me.ltype.lightniwa.fragment.AnimeFragment;
import me.ltype.lightniwa.fragment.LastUpdateFragment;
import me.ltype.lightniwa.fragment.MainFragment;
import me.ltype.lightniwa.fragment.SearchResultFragment;
import me.ltype.lightniwa.util.FileUtils;

import static me.ltype.lightniwa.util.Util.checkUpdate;

public class MainActivity extends MaterialNavigationDrawer {
    private static String LOG_TAG = "MainActivity";

    private SearchView mSearchView;
    private SearchResultFragment mSearchResultFragment;

    @Override
    public void init(Bundle savedInstanceState) {
        FileUtils.syncBooks(this);
        checkUpdate(this, false);
        Fresco.initialize(getApplicationContext());

        setDrawerHeaderImage(R.drawable.mat2);
        setUsername("ltype");
        setUserEmail("asuka@ltype.me");
        setFirstAccountPhoto(getResources().getDrawable(R.drawable.photo));

        this.addSection(newSection(getResources().getString(R.string.bookshelf), new MainFragment()));
        this.addSection(newSection(getResources().getString(R.string.last_update), new LastUpdateFragment()));
        this.addSection(newSection(getResources().getString(R.string.anime_aera), new AnimeFragment()));

        this.addBottomSection(newSection(getResources().getString(R.string.setting), R.drawable.ic_settings_black_24dp, new Intent(this, SettingActivity.class)));
        this.addBottomSection(newSection(getResources().getString(R.string.about_us), R.drawable.ic_help_black_24dp, new Intent(this, AboutUsActivity.class)));

//        XGPushConfig.enableDebug(this, true);
        XGPushManager.registerPush(getApplicationContext());
//        Log.e(LOG_TAG, XGPushConfig.getToken(this));
    }

    @Override
    public void onRestart() {
        Log.i(LOG_TAG, "onRestart");
        super.onRestart();
        XGPushManager.startPushService(getApplicationContext());
        XGPushManager.registerPush(getApplicationContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        XGPushManager.unregisterPush(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        this.menu = menu;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            mSearchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    query = query.trim();
                    Bundle bundle = new Bundle();
                    bundle.putString("query", query);
                    if (mSearchResultFragment == null || !mSearchResultFragment.isVisible()) {
                        mSearchResultFragment = new SearchResultFragment();
                        mSearchResultFragment.setArguments(bundle);
                        openChildFragment(mSearchResultFragment, "搜索:" + query);
                    } else {
                        mSearchResultFragment = new SearchResultFragment();
                        mSearchResultFragment.setArguments(bundle);
                        openReplaceFragment(mSearchResultFragment, "搜索:" + query);
                    }
                    mSearchView.onActionViewCollapsed();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    return true;
                }
            });
            final SearchView.OnCloseListener closeListener = () -> {
                mSearchView.onActionViewCollapsed();
                return false;
            };
        }

        return true;
    }

    public void openReplaceFragment(Fragment childFragment, String title) {
        this.onBackPressed();
        this.setFragmentChild(childFragment, title);
    }

    public void openChildFragment(Fragment childFragment, String title) {
        this.setFragmentChild(childFragment, title);
    }
}

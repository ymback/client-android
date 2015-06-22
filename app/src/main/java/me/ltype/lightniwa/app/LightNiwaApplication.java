package me.ltype.lightniwa.app;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.multidex.MultiDexApplication;

import me.ltype.lightniwa.db.LRSQLiteOpenHelper;


/**
 * Created by ltype on 2015/6/12.
 */
public class LightNiwaApplication extends MultiDexApplication {
    String DATABASES_NAME = "LightNiwa";
    int DATABASES_VERSION = 1;

    private SQLiteOpenHelper mSQLiteOpenHelper;

    public static LightNiwaApplication getInstance(final Context context) {
        return (LightNiwaApplication) context.getApplicationContext();
    }

    public SQLiteOpenHelper getSQLiteOpenHelper() {
        if (mSQLiteOpenHelper != null) return mSQLiteOpenHelper;
        return mSQLiteOpenHelper = new LRSQLiteOpenHelper(this);
    }
}

package me.ltype.lightreader.app;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.multidex.MultiDexApplication;

import me.ltype.lightreader.db.LRSQLiteOpenHelper;


/**
 * Created by ltype on 2015/6/12.
 */
public class LightReaderApplication extends MultiDexApplication {
    String DATABASES_NAME = "LightReader";
    int DATABASES_VERSION = 1;

    private SQLiteOpenHelper mSQLiteOpenHelper;

    public static LightReaderApplication getInstance(final Context context) {
        return (LightReaderApplication) context.getApplicationContext();
    }

    public SQLiteOpenHelper getSQLiteOpenHelper() {
        if (mSQLiteOpenHelper != null) return mSQLiteOpenHelper;
        return mSQLiteOpenHelper = new LRSQLiteOpenHelper(this);
    }
}

package me.ltype.lightreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.ltype.lightreader.query.LightReaderDataStore.Bookmarks;
import me.ltype.lightreader.query.NewColumn;
import me.ltype.lightreader.query.SQLCreateTableQuery;
import me.ltype.lightreader.query.SQLQueryBuilder;

/**
 * Created by ltype on 2015/6/9.
 */
public class LRSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LightReader.db";
    private static final int DATABASE_VERSION = 1;

    public LRSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("sql", "LRSQLiteOpenHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("sql", createTable(Bookmarks.TABLE_NAME, Bookmarks.COLUMNS, Bookmarks.TYPES, true));
        db.beginTransaction();

        /* *
         * Create Table if not exits
         */
        db.execSQL(createTable(Bookmarks.TABLE_NAME, Bookmarks.COLUMNS, Bookmarks.TYPES, true));

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("sql", "onUpgrade");
        onCreate(db);
    }

    private static String createTable(final String tableName, final String[] columns, final String[] types,
                                      final boolean createIfNotExists) {
        final SQLCreateTableQuery.Builder qb = SQLQueryBuilder.createTable(createIfNotExists, tableName);
        Log.e("sql", "createTable");
        qb.columns(NewColumn.createNewColumns(columns, types));
        return qb.buildSQL();
    }
}

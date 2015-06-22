package me.ltype.lightniwa.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.ltype.lightniwa.query.NewColumn;
import me.ltype.lightniwa.query.SQLQueryBuilder;
import me.ltype.lightniwa.query.SQLCreateTableQuery;
import me.ltype.lightniwa.db.LightNiwaDataStore.Books;
import me.ltype.lightniwa.db.LightNiwaDataStore.Volumes;
import me.ltype.lightniwa.db.LightNiwaDataStore.Chapters;
import me.ltype.lightniwa.db.LightNiwaDataStore.Bookmarks;
import me.ltype.lightniwa.util.FileUtils;

import static me.ltype.lightniwa.query.SQLQueryBuilder.dropTable;

/**
 * Created by ltype on 2015/6/9.
 */
public class LRSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LightNiwa.db";
    private static final int DATABASE_VERSION = 2;

    private Context mContext;

    public LRSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        /* *
         * Create Table if not exits
         */
        db.execSQL(createTable(Books.TABLE_NAME, Books.COLUMNS, Books.TYPES, true));
        db.execSQL(createTable(Volumes.TABLE_NAME, Volumes.COLUMNS, Volumes.TYPES, true));
        db.execSQL(createTable(Chapters.TABLE_NAME, Chapters.COLUMNS, Chapters.TYPES, true));
        db.execSQL(createTable(Bookmarks.TABLE_NAME, Bookmarks.COLUMNS, Bookmarks.TYPES, true));

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("onUpgrade", oldVersion + "=====" + newVersion);
        if(oldVersion < newVersion) {
            FileUtils.updateBooksInfo(mContext);
            db.beginTransaction();
            db.execSQL(dropTable(true, Books.TABLE_NAME).getSQL());
            db.execSQL(createTable(Books.TABLE_NAME, Books.COLUMNS, Books.TYPES, true));

            db.execSQL(dropTable(true, Volumes.TABLE_NAME).getSQL());
            db.execSQL(createTable(Volumes.TABLE_NAME, Volumes.COLUMNS, Volumes.TYPES, true));

            db.execSQL(dropTable(true, Chapters.TABLE_NAME).getSQL());
            db.execSQL(createTable(Chapters.TABLE_NAME, Chapters.COLUMNS, Chapters.TYPES, true));

            db.execSQL(dropTable(true, Bookmarks.TABLE_NAME).getSQL());
            db.execSQL(createTable(Bookmarks.TABLE_NAME, Bookmarks.COLUMNS, Bookmarks.TYPES, true));
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    private static String createTable(final String tableName, final String[] columns, final String[] types,
                                      final boolean createIfNotExists) {
        final SQLCreateTableQuery.Builder qb = SQLQueryBuilder.createTable(createIfNotExists, tableName);
        Log.e("sql", "createTable");
        qb.columns(NewColumn.createNewColumns(columns, types));
        return qb.buildSQL();
    }

}

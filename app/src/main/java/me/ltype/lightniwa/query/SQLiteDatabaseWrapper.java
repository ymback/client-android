package me.ltype.lightniwa.query;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ltype on 2015/6/12.
 */
public class SQLiteDatabaseWrapper {
    private SQLiteDatabase mDatabase;
    private final SQLiteDatabaseWrapper.LazyLoadCallback mLazyLoadCallback;

    public SQLiteDatabaseWrapper(final LazyLoadCallback callback) {
        mLazyLoadCallback = callback;
    }

    public void beginTransaction() {
        tryCreateDatabase();
        if (mDatabase == null) return;
        mDatabase.beginTransaction();
    }

    public void endTransaction() {
        tryCreateDatabase();
        if (mDatabase == null) return;
        mDatabase.endTransaction();
    }

    public long insert(final String table, final String nullColumnHack, final ContentValues values) {
        tryCreateDatabase();
        if (mDatabase == null) return -1;
        return mDatabase.insert(table, nullColumnHack, values);
    }

    public long insertWithOnConflict(final String table, final String nullColumnHack,
                                     final ContentValues initialValues, final int conflictAlgorithm) {
        tryCreateDatabase();
        if (mDatabase == null) return -1;
        return mDatabase.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
    }

    public int delete(final String table, final String whereClause, final String[] whereArgs) {
        tryCreateDatabase();
        if (mDatabase == null) return 0;
        return mDatabase.delete(table, whereClause, whereArgs);
    }

    public int update(final String table, final ContentValues values, final String whereClause, final String[] whereArgs) {
        tryCreateDatabase();
        if (mDatabase == null) return 0;
        return mDatabase.update(table, values, whereClause, whereArgs);
    }

    public Cursor query(final String table, final String[] columns, final String selection,
                        final String[] selectionArgs, final String groupBy, final String having, final String orderBy) {
        tryCreateDatabase();
        if (mDatabase == null) return null;
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        tryCreateDatabase();
        if (mDatabase == null) return null;
        return mDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        tryCreateDatabase();
        if (mDatabase == null) return null;
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    private void tryCreateDatabase() {
        if (mLazyLoadCallback == null || mDatabase != null) return;
        mDatabase = mLazyLoadCallback.onCreateSQLiteDatabase();
        if (mDatabase == null)
            throw new IllegalStateException("Callback must not return null instance!");
    }

    public interface LazyLoadCallback {
        SQLiteDatabase onCreateSQLiteDatabase();
    }
}

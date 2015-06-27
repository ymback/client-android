package me.ltype.lightniwa.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import me.ltype.lightniwa.app.LightNiwaApplication;
import me.ltype.lightniwa.db.LightNiwaDataStore;
import me.ltype.lightniwa.query.SQLiteDatabaseWrapper;
import me.ltype.lightniwa.db.LightNiwaDataStore.Books;
import me.ltype.lightniwa.db.LightNiwaDataStore.Volumes;
import me.ltype.lightniwa.db.LightNiwaDataStore.Chapters;
import me.ltype.lightniwa.db.LightNiwaDataStore.Bookmarks;

import static me.ltype.lightniwa.query.Utils.getNotificationUri;
import static me.ltype.lightniwa.query.Utils.getTableId;
import static me.ltype.lightniwa.query.Utils.getTableNameById;

/**
 * Created by ltype on 2015/6/12.
 */
public class LightNiwaDataProvider extends ContentProvider implements SQLiteDatabaseWrapper.LazyLoadCallback {
    private ContentResolver mContentResolver;
    private SQLiteDatabaseWrapper mDatabaseWrapper;

    private Handler mHandler;

    @Override
    public SQLiteDatabase onCreateSQLiteDatabase() {
        final LightNiwaApplication app = LightNiwaApplication.getInstance(getContext());
        final SQLiteOpenHelper helper = app.getSQLiteOpenHelper();
        return helper.getWritableDatabase();
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mDatabaseWrapper = new SQLiteDatabaseWrapper(this);
        mHandler = new Handler(Looper.getMainLooper());
        final LightNiwaApplication app = LightNiwaApplication.getInstance(context);
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int tableId = getTableId(uri);
        final String table = getTableNameById(tableId);
        checkWritePermission(tableId, table);
        if (table == null) return null;
        final long rowId;
//        rowId = mDatabaseWrapper.insert(table, null, values);
        /*if (table != null) {
            mDatabaseWrapper.beginTransaction();
            if (tableId == TABLE_ID_CACHED_USERS) {

            }
        }*/
        rowId = mDatabaseWrapper.insertWithOnConflict(table, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        return Uri.withAppendedPath(uri, String.valueOf(rowId));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        try {
            int tableId = getTableId(uri);
            String table = getTableNameById(tableId);
            checkWritePermission(tableId, table);
            if (table == null) return 0;
            int result = mDatabaseWrapper.delete(table, selection, selectionArgs);
            if (result > 0) {
                onDatabaseUpdated(tableId, uri);
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int tableId = getTableId(uri);
        final String table = getTableNameById(tableId);
        checkWritePermission(tableId, table);
        int result = 0;
        if (table != null) {
            /*switch (tableId) {
                case TABLE_ID_DIRECT_MESSAGES_CONVERSATION:
                case TABLE_ID_DIRECT_MESSAGES:
                case TABLE_ID_DIRECT_MESSAGES_CONVERSATIONS_ENTRIES:
                    return 0;
            }*/
            result = mDatabaseWrapper.update(table, values, selection, selectionArgs);
        }
        if (result > 0) {
//            onDatabaseUpdated(tableId, uri);
        }
        return result;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int tableId = getTableId(uri);
        final String table = getTableNameById(tableId);
//        checkReadPermission(tableId, table, projection);
        if (table == null) return null;
        final Cursor c = mDatabaseWrapper.query(table, projection, selection, selectionArgs, null, null, sortOrder);
//        setNotificationUri(c, getNotificationUri(tableId, uri));
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    private ContentResolver getContentResolver() {
        if (mContentResolver != null) return mContentResolver;
        final Context context = getContext();
        return mContentResolver = context.getContentResolver();
    }

    private void setNotificationUri(final Cursor c, final Uri uri) {
        final ContentResolver cr = getContentResolver();
        if (cr == null || c == null || uri == null) return;
        c.setNotificationUri(cr, uri);
    }

    private void checkWritePermission(final int id, final String table) {
        /*switch (id) {
            case TABLE_ID_ACCOUNTS: {
                // Writing to accounts database is not allowed for third-party
                // applications.
                if (!mPermissionsManager.checkSignature(Binder.getCallingUid()))
                    throw new SecurityException(
                            "Writing to accounts database is not allowed for third-party applications");
                break;
            }
            case TABLE_ID_CACHED_HASHTAGS: {
                if (!checkPermission(PERMISSION_WRITE))
                    throw new SecurityException("Access database " + table + " requires level PERMISSION_LEVEL_WRITE");
                break;
            }
        }*/
    }

    private void onDatabaseUpdated(final int tableId, final Uri uri) {
        if (uri == null) return;
        notifyContentObserver(getNotificationUri(tableId, uri));
    }

    private void notifyContentObserver(final Uri uri) {
        mHandler.post((Runnable) () -> {
            final ContentResolver cr = getContentResolver();
            if (uri == null || cr == null) return;
            cr.notifyChange(uri, null);
        });
    }
}

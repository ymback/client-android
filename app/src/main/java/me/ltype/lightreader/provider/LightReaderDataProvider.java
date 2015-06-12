package me.ltype.lightreader.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import me.ltype.lightreader.app.LightReaderApplication;
import me.ltype.lightreader.query.LightReaderDataStore.Bookmarks;
import me.ltype.lightreader.query.SQLiteDatabaseWrapper;
import me.ltype.lightreader.query.SQLiteDatabaseWrapper.LazyLoadCallback;

/**
 * Created by ltype on 2015/6/12.
 */
public class LightReaderDataProvider extends ContentProvider implements LazyLoadCallback {
    private ContentResolver mContentResolver;
    private SQLiteDatabaseWrapper mDatabaseWrapper;
    private static final UriMatcher CONTENT_PROVIDER_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public SQLiteDatabase onCreateSQLiteDatabase() {
        final LightReaderApplication app = LightReaderApplication.getInstance(getContext());
        final SQLiteOpenHelper helper = app.getSQLiteOpenHelper();
        return helper.getWritableDatabase();
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mDatabaseWrapper = new SQLiteDatabaseWrapper(this);
        final LightReaderApplication app = LightReaderApplication.getInstance(context);
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
        rowId = mDatabaseWrapper.insertWithOnConflict(table, "chapter_id", values,
                SQLiteDatabase.CONFLICT_REPLACE);
        return Uri.withAppendedPath(uri, String.valueOf(rowId));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
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

    public static int getTableId(final Uri uri) {
        if (uri == null) return -1;
        return CONTENT_PROVIDER_URI_MATCHER.match(uri);
    }

    public static String getTableNameById(final int id) {
        /*switch (id) {
            case TABLE_ID_ACCOUNTS:
                return Bookmarks.TABLE_NAME;
            default:
                return null;
        }*/

        return Bookmarks.TABLE_NAME;
    }
}

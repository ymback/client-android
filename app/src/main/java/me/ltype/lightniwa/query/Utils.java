package me.ltype.lightniwa.query;

import android.content.UriMatcher;
import android.net.Uri;

import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.db.LightNiwaDataStore;
import me.ltype.lightniwa.db.LightNiwaDataStore.Books;
import me.ltype.lightniwa.db.LightNiwaDataStore.Volumes;
import me.ltype.lightniwa.db.LightNiwaDataStore.Bookmarks;
import me.ltype.lightniwa.db.LightNiwaDataStore.Chapters;

/**
 * Created by ltype on 2015/6/11.
 */
public class Utils implements Constants{
    private static final UriMatcher CONTENT_PROVIDER_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        CONTENT_PROVIDER_URI_MATCHER.addURI(LightNiwaDataStore.AUTHORITY, Books.CONTENT_PATH,
                TABLE_ID_BOOKS);
        CONTENT_PROVIDER_URI_MATCHER.addURI(LightNiwaDataStore.AUTHORITY, Volumes.CONTENT_PATH,
                TABLE_ID_VOLUMES);
        CONTENT_PROVIDER_URI_MATCHER.addURI(LightNiwaDataStore.AUTHORITY, Chapters.CONTENT_PATH,
                TABLE_ID_CHAPTERS);
        CONTENT_PROVIDER_URI_MATCHER.addURI(LightNiwaDataStore.AUTHORITY, Bookmarks.CONTENT_PATH,
                TABLE_ID_BOOKMARKS);

    }

    public static int getTableId(final Uri uri) {
        if (uri == null) return -1;
        return CONTENT_PROVIDER_URI_MATCHER.match(uri);
    }

    public static String getTableNameById(final int id) {
        switch (id) {
            case TABLE_ID_BOOKS:
                return Books.TABLE_NAME;
            case TABLE_ID_VOLUMES:
                return Volumes.TABLE_NAME;
            case TABLE_ID_CHAPTERS:
                return Chapters.TABLE_NAME;
            case TABLE_ID_BOOKMARKS:
                return Bookmarks.TABLE_NAME;
            default:
                return null;
        }
    }

    public static String toString(final Object[] array, final char token, final boolean includeSpace) {
        final StringBuilder builder = new StringBuilder();
        final int length = array.length;
        for (int i = 0; i < length; i++) {
            final String string = objectToString(array[i]);
            if (string != null) {
                if (i > 0) {
                    builder.append(includeSpace ? token + " " : token);
                }
                builder.append(string);
            }
        }
        return builder.toString();
    }

    private static String objectToString(Object o) {
        if (o instanceof SQLLang)
            return ((SQLLang) o).getSQL();
        return o != null ? o.toString() : null;
    }

    public static Uri getNotificationUri(final int tableId, final Uri def) {
        return def;
    }
}

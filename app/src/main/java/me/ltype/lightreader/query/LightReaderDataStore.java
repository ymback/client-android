package me.ltype.lightreader.query;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ltype on 2015/6/11.
 */
public interface LightReaderDataStore {
    String AUTHORITY = "lightreader";

    String TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT";
    String TYPE_INT = "INTEGER";
    String TYPE_INT_UNIQUE = "INTEGER UNIQUE";
    String TYPE_BOOLEAN = "INTEGER(1)";
    String TYPE_BOOLEAN_DEFAULT_TRUE = "INTEGER(1) DEFAULT 1";
    String TYPE_BOOLEAN_DEFAULT_FALSE = "INTEGER(1) DEFAULT 0";
    String TYPE_TEXT = "TEXT";
    String TYPE_TEXT_NOT_NULL = "TEXT NOT NULL";
    String TYPE_TEXT_NOT_NULL_UNIQUE = "TEXT NOT NULL UNIQUE";

    String CONTENT_PATH_NULL = "null_content";

    String CONTENT_PATH_DATABASE_READY = "database_ready";

    Uri BASE_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY).build();

    interface Bookmarks extends BaseColumns {
        String TABLE_NAME = "bookmarks";
        String CONTENT_PATH = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        String BOOK_ID = "book_id";
        String VOLUME_ID = "volume_id";
        String CHAPTER_ID = "chapter_id";
        String POSITION = "position";
        String UPDATE_TIME = "update_time";

        String[] COLUMNS = {_ID, BOOK_ID, VOLUME_ID, CHAPTER_ID, POSITION, UPDATE_TIME};
        String[] TYPES = {TYPE_PRIMARY_KEY, TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL, TYPE_INT_UNIQUE, TYPE_TEXT_NOT_NULL, TYPE_INT};

    }

    /*interface Anime extends BaseColumns {
        String TABLE_NAME = "anime";
        String CONTENT_PATH = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        String BOOK_ID = "book_id";
        String COVER = "cover";
        String AUTHOR = "author";
        String MONTH = "month";

        String[] COLUMNS = {_ID, BOOK_ID, COVER, AUTHOR, MONTH, UPDATE_TIME};
        String[] TYPES = {TYPE_PRIMARY_KEY, TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL, TYPE_INT_UNIQUE, TYPE_TEXT_NOT_NULL, TYPE_INT};

    }*/
}

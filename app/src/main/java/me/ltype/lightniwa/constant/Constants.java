package me.ltype.lightniwa.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by ltype on 2015/5/13.
 */
public interface Constants {
    String BOOK_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator + "iLight" + File.separator + "book";
    String CACHE = "/iLight/cache";
    String SITE = "http://lknovel.lightnovel.cn";
    String API_KEY = "97F2089A6ACCB571694DEA577CFE11DD";
    String API_TIME = "1432577669309";

    int PROGRESS_CANCEL = 0001;

    int TABLE_ID_BOOKS = 1;
    int TABLE_ID_VOLUMES = 2;
    int TABLE_ID_CHAPTERS = 3;
    int TABLE_ID_BOOKMARKS = 11;
}

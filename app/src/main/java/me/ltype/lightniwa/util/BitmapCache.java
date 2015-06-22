package me.ltype.lightniwa.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ltype on 2015/6/20.
 */
public class BitmapCache {
    private static int size = 2 * 1024 * 1024 * 1024;
    private static List<String> list = new ArrayList<>();
    private static Map<String, Bitmap> map = new HashMap<>();

    public static Bitmap getBitmap(String key) {
        return map.get(key);
    }

    public static Bitmap putBitmap(String key, Bitmap bitmap) {
        size = size - bitmap.getByteCount();
        while (size < 0) {
            Bitmap cacheBitmap = map.get(list.remove(0));
            cacheBitmap.recycle();
            size += cacheBitmap.getByteCount();
        }
        list.add(key);
        map.put(key, bitmap);
        return bitmap;
    }
}

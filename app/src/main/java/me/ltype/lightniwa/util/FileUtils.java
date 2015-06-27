package me.ltype.lightniwa.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.db.LightNiwaDataStore;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.model.Chapter;
import me.ltype.lightniwa.model.Volume;

/**
 * Created by ltype on 2015/5/12.
 */
public class FileUtils {
    private static String LOG_TAG = "FileUtils";
    private static List<File> fileList = new ArrayList();
    private final static Map<String, Book> bookMap = new HashMap<>();
    private final static List<Book> bookList = new ArrayList<>();
    private static List<Volume> volumeList = new ArrayList();

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try {
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static String readFile(String path) {
        File file = new File(path);
        FileInputStream fin = null;
        String conent = "";
        try {
            fin = new FileInputStream(file);
            conent = convertStreamToString(fin);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return conent;
    }

    /*public static String readFile(String path) {
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = null;
        BufferedReader buffReader = null;
        try {
            new FileInputStream(path);
            isr = new InputStreamReader(new FileInputStream(new File(path)), "UTF-8");
            buffReader = new BufferedReader(isr);
            String line;
            while ((line = buffReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (buffReader != null) {
                try {
                    buffReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }*/


    public static void updateBooksInfo(Context mContext) {
        File[] books = new File(Constants.BOOK_DIR).listFiles();
        if (books != null) {
            for (int i = 0; i < books.length; i++) {
                File[] volumes = new File(books[i].getPath()).listFiles();
                for (int j = 0; j< volumes.length; j++) {
                    try {
                        String bookPath = volumes[j].getPath() + "/info/book.json";
                        String bookResult = readFile(bookPath).replaceAll("illustor", "illustrator");
                        updateFile(bookResult, bookPath);

                        String volumePath = volumes[j].getPath() + "/info/volume.json";
                        String volumeResult = readFile(volumePath).replaceAll("vol_id", "volume_id");
                        volumeResult = volumeResult.replaceAll("vol_number", "index");
                        volumeResult = volumeResult.replaceAll("vol_title", "name");
                        volumeResult = volumeResult.replaceAll("vol_cover", "cover");
                        volumeResult = volumeResult.replaceAll("vol_desc", "description");
                        updateFile(volumeResult, volumePath);

                        String chapterPath = volumes[j].getPath() + "/info/chapters.json";
                        String chapterResult = readFile(chapterPath).replaceAll("vol_id", "volume_id");
                        chapterResult = chapterResult.replaceAll("chapter_title", "name");
                        updateFile(chapterResult, chapterPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void syncBooks(Context mContext) {
        ContentResolver mResolver = mContext.getContentResolver();
        File[] books = new File(Constants.BOOK_DIR).listFiles();
        if (books != null) {
            for (int i = 0; i < books.length; i++) {
                File[] volumes = new File(books[i].getPath()).listFiles();
                try {
                    JSONObject json = JSON.parseObject(readFile(volumes[0].getPath() + "/info/book.json"));
                    ContentValues values = new ContentValues();
                    values.put(LightNiwaDataStore.Books.BOOK_ID, json.getString("book_id"));
                    values.put(LightNiwaDataStore.Books.AUTHOR, json.getString("author"));
                    values.put(LightNiwaDataStore.Books.ILLUSTRATOR, json.getString("illustrator"));
                    values.put(LightNiwaDataStore.Books.PUBLISHER, json.getString("publisher"));
                    values.put(LightNiwaDataStore.Books.NAME, json.getString("name"));
                    values.put(LightNiwaDataStore.Books.COVER, json.getString("cover"));
                    values.put(LightNiwaDataStore.Books.DESCRIPTION, json.getString("description"));
                    mResolver.insert(LightNiwaDataStore.Books.CONTENT_URI, values);
                    syncVolumes(mContext, volumes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void syncVolumes(Context mContext, File[] volumes) {
        ContentResolver mResolver = mContext.getContentResolver();
        if (volumes != null) {
            for (int i = 0; i < volumes.length; i++) {
                try {
                    JSONObject volJson = JSON.parseObject(readFile(volumes[i].getPath() + "/info/volume.json"));
                    ContentValues values = new ContentValues();
                    values.put(LightNiwaDataStore.Volumes.BOOK_ID, volJson.getString("book_id"));
                    values.put(LightNiwaDataStore.Volumes.VOLUME_ID, volJson.getString("volume_id"));
                    values.put(LightNiwaDataStore.Volumes.VOLUME_INDEX, volJson.getString("index"));
                    values.put(LightNiwaDataStore.Volumes.NAME, volJson.getString("name"));
                    values.put(LightNiwaDataStore.Volumes.COVER, volJson.getString("cover"));
                    values.put(LightNiwaDataStore.Volumes.DESCRIPTION, volJson.getString("description"));
                    mResolver.insert(LightNiwaDataStore.Volumes.CONTENT_URI, values);
                    syncChapters(mContext, volumes[i].getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void syncVolume(Context mContext, String bookId, String volumeId) {
        Log.i(LOG_TAG, bookId + "==" + volumeId);
        ContentResolver mResolver = mContext.getContentResolver();
        try {
            String path = Constants.BOOK_DIR + File.separator + bookId + File.separator + volumeId;

            JSONObject json = JSON.parseObject(readFile(path + File.separator + "info" + File.separator + "book.json"));
            ContentValues bookVal = new ContentValues();
            bookVal.put(LightNiwaDataStore.Books.BOOK_ID, json.getString("book_id"));
            bookVal.put(LightNiwaDataStore.Books.AUTHOR, json.getString("author"));
            bookVal.put(LightNiwaDataStore.Books.ILLUSTRATOR, json.getString("illustrator"));
            bookVal.put(LightNiwaDataStore.Books.PUBLISHER, json.getString("publisher"));
            bookVal.put(LightNiwaDataStore.Books.NAME, json.getString("name"));
            bookVal.put(LightNiwaDataStore.Books.COVER, json.getString("cover"));
            bookVal.put(LightNiwaDataStore.Books.DESCRIPTION, json.getString("description"));
            mResolver.insert(LightNiwaDataStore.Books.CONTENT_URI, bookVal);

            JSONObject volJson = JSON.parseObject(readFile(path + File.separator + "info" + File.separator
                    + "volume.json"));
            ContentValues volVal = new ContentValues();
            volVal.put(LightNiwaDataStore.Volumes.BOOK_ID, volJson.getString("book_id"));
            volVal.put(LightNiwaDataStore.Volumes.VOLUME_ID, volJson.getString("volume_id"));
            volVal.put(LightNiwaDataStore.Volumes.VOLUME_INDEX, volJson.getString("index"));
            volVal.put(LightNiwaDataStore.Volumes.NAME, volJson.getString("name"));
            volVal.put(LightNiwaDataStore.Volumes.COVER, volJson.getString("cover"));
            volVal.put(LightNiwaDataStore.Volumes.DESCRIPTION, volJson.getString("description"));
            mResolver.insert(LightNiwaDataStore.Volumes.CONTENT_URI, volVal);
            syncChapters(mContext, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncChapters(Context mContext, String volPath) {
        ContentResolver mResolver = mContext.getContentResolver();
        try {
            JSONArray chapterJson = JSON.parseArray(readFile(volPath + "/info/chapters.json"));
            for (int i = 0; i < chapterJson.size(); i ++) {
                JSONObject jsonObj = chapterJson.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(LightNiwaDataStore.Chapters.BOOK_ID, jsonObj.getString("book_id"));
                values.put(LightNiwaDataStore.Chapters.VOLUME_ID, jsonObj.getString("volume_id"));
                values.put(LightNiwaDataStore.Chapters.CHAPTER_ID, jsonObj.getString("chapter_id"));
                values.put(LightNiwaDataStore.Chapters.CHAPTER_INDEX, jsonObj.getString("index"));
                values.put(LightNiwaDataStore.Chapters.NAME, jsonObj.getString("name"));
                mResolver.insert(LightNiwaDataStore.Chapters.CONTENT_URI, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initBookList() {
        File[] books = new File(Constants.BOOK_DIR).listFiles();
        if (books != null) {
            for (int i = 0; i < books.length; i++) {
                File[] volumes = new File(books[i].getPath()).listFiles();
                JSONObject json = JSON.parseObject(readFile(volumes[0].getPath() + "/info/book.json"));
                Book book = new Book();
                book.setId(json.getString("book_id"));
                book.setAuthor(json.getString("author"));
                book.setIllustrator(json.getString("illustor"));
                book.setPublisher(json.getString("publisher"));
                book.setName(json.getString("name"));
                book.setCover(json.getString("cover"));
                book.setDescription(json.getString("description"));
                bookMap.put(book.getId(), book);
                bookList.add(book);
            }

        }
    }

    public static Map<String, Book> getBookMap() {
        return bookMap;
    }

    public static List<Book> getBookList() {
        bookList.clear();
        initBookList();
        return bookList;
    }

    public static List<Volume> getVolumeList(String bookId) {
        File[] volumes = new File(Constants.BOOK_DIR + File.separator + bookId).listFiles();
        if (volumes == null) return null;
        List<Volume> list = new ArrayList<>();
        for (int i = 0; i < volumes.length; i++) {
            JSONObject json = JSON.parseObject(readFile(volumes[i].getPath() + "/info/volume.json"));
            Volume volume = new Volume();
            volume.setIndex(json.getString("vol_number"));
            volume.setBookId(json.getString("book_id"));
            volume.setId(json.getString("vol_id"));
            volume.setHeader(json.getString("vol_number"));
            volume.setName(json.getString("vol_title"));
            volume.setCover(json.getString("vol_cover"));
            volume.setDescription(json.getString("vol_desc"));
//                volume.setLastUpdate(json.getString("LastUpdate"));
            list.add(volume);
        }
        return list;
    }

    public static List<Chapter> getChapterList(String bookId, String volumeId) {
        List<Chapter> chapterList = new ArrayList<>();
        String path = Constants.BOOK_DIR + "/" + bookId + "/" + volumeId + "/info/chapters.json";
        JSONArray jsonArray = JSON.parseArray(readFile(path));
        for (int i = 0; i < jsonArray.size(); i ++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Chapter chapter = new Chapter();
            chapter.setIndex(jsonObject.getString("index"));
            chapter.setBookId(jsonObject.getString("book_id"));
            chapter.setVolumeId(jsonObject.getString("vol_id"));
            chapter.setId(jsonObject.getString("chapter_id"));
            chapter.setName(jsonObject.getString("chapter_title"));
            chapterList.add(chapter);
        }
        return chapterList;
    }

    public static List<String> getContentList(String bookId, String volumeId, String chapterId) {
        List<String> contentList = new ArrayList<>();
        String path = Constants.BOOK_DIR + "/" + bookId + "/" + volumeId + "/content/" + chapterId + ".json";
        try {
            JSONArray jsonArray = JSON.parseArray(readFile(path));
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                contentList.add(jsonObject.getString("content"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentList;
    }

    public static void createDir(String path) {
        File content = new File(path +  File.separator  + "content");
        if (!content.exists())
            content.mkdirs();
        File img = new File(path +  File.separator  + "img");
        if (!img.exists())
            img.mkdirs();
        File info = new File(path +  File.separator  + "info");
        if (!info.exists())
            info.mkdirs();
    }

    public static void updateFile(String content, String path) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(path));
            outputStream.write(content.getBytes(Charset.forName("UTF-8")));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void storeInfo(String json, String path, String fileName) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path +  File.separator  + "info" +  File.separator  + fileName + ".json");
            outputStream.write(json.getBytes(Charset.forName("UTF-8")));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void storeContent(String json, String path, String fileName) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path +  File.separator  + "content" +  File.separator  + fileName + ".json");
            outputStream.write(json.getBytes(Charset.forName("UTF-8")));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String storeImgs(String url, String path) {
        byte[] data = HttpUtil.getImg(url);
        String fileName = Util.md5(url);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path + File.separator + "img" +  File.separator + fileName + url.substring(url.lastIndexOf(".")));
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    public static String storeImg(String url, String path, Bitmap bitmap) {
        String fileName = Util.md5(url);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path + File.separator + "img" +  File.separator + fileName + url.substring(url.lastIndexOf(".")));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    public static boolean delFolder(File dir, long curTime) {
        boolean result = false;
        try {
            clearFolder(dir, curTime);
            final File to = new File(dir.getAbsolutePath() + System.currentTimeMillis());
            dir.renameTo(to);
            result = to.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int clearFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        final File to = new File(child.getAbsolutePath() + System.currentTimeMillis());
                        child.renameTo(to);
                        if (to.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }
}

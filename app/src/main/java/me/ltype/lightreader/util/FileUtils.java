package me.ltype.lightreader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;

import org.jsoup.examples.HtmlToPlainText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ltype.lightreader.constant.Constants;
import me.ltype.lightreader.model.Book;
import me.ltype.lightreader.model.Chapter;
import me.ltype.lightreader.model.Volume;

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

    public static String readFile(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(path)), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
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
        bookMap.clear();
        initBookList();
        return bookMap;
    }

    public static  List<Book> getBookList() {
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
            chapter.setTitle(jsonObject.getString("chapter_title"));
            chapterList.add(chapter);
        }
        return chapterList;
    }

    public static List<String> getContentList(String bookId, String volumeId, String chapterId) {
        List<String> contentList = new ArrayList<>();
        String path = Constants.BOOK_DIR + "/" + bookId + "/" + volumeId + "/content/" + chapterId + ".json";
        JSONArray jsonArray = JSON.parseArray(readFile(path));
        for (int i = 0; i < jsonArray.size(); i ++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            contentList.add(jsonObject.getString("content"));
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

    public static void storeInfo(String json, String path, String fileName) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path +  File.separator  + "info" +  File.separator  + fileName + ".json");
            outputStream.write(json.getBytes(Charset.forName("UTF-8")));
            outputStream.flush();
        } catch (IOException e) {
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

    public static int clearFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
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

    public static boolean delFolder(File dir, long curTime) {
        try {
            clearFolder(dir, curTime);
            dir.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}

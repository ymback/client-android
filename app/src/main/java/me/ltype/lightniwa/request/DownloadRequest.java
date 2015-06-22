package me.ltype.lightniwa.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.util.ApiUtil;
import me.ltype.lightniwa.util.FileUtils;
import me.ltype.lightniwa.util.Util;

/**
 * Created by ltype on 2015/6/6.
 */
public class DownloadRequest {
    private static String LOG_TAG = "DownloadRequest";
    private Context mContext;
    private RequestQueue mQueue;
    private RequestQueue.RequestFinishedListener listener;
    private Handler mHandler;
    private List<String> imgList = new ArrayList<>();
    private List<String> contentList = new ArrayList<>();
    private AtomicInteger count = new AtomicInteger(0);
    private String bookId;
    private String volumeId;

    public DownloadRequest(Context context, RequestQueue queue, Handler handler) {
        mContext = context;
        mQueue = queue;
        mHandler = handler;
    }

    public void downBook(String volId) {
        this.volumeId = volId;
        count.incrementAndGet();
        StringRequest volRequest = new StringRequest(
                Request.Method.GET,
                ApiUtil.API_PATH + "vol/" + volId,
                response -> {
                    JSONObject jsonObject = JSON.parseObject(response);
                    JSONObject volDetail = jsonObject.getJSONArray("volDetail").getJSONObject(0);
                    JSONArray chapterResult = jsonObject.getJSONArray("chapterResult");

                    this.bookId = volDetail.getString("series_id");

                    String bookJson = ApiUtil.getBookJson(jsonObject);
                    String volJson = ApiUtil.getVolJson(jsonObject);
                    String chaptersJson = ApiUtil.getChaptersJson(jsonObject);

                    String bookPath = Constants.BOOK_DIR + File.separator + volDetail.getString("series_id") + File.separator + volDetail.getString("id");
                    FileUtils.createDir(bookPath);
                    FileUtils.storeInfo(bookJson.trim(), bookPath, "book");
                    FileUtils.storeInfo(volJson.trim(), bookPath, "volume");
                    FileUtils.storeInfo(chaptersJson.trim(), bookPath, "chapters");

                    downImage(Constants.SITE + volDetail.getString("vol_cover"), bookPath);

                    JSONArray jsonArray = jsonObject.getJSONArray("chapterResult");
                    for (int i = 0; i < jsonArray.size(); i ++) {
                        String chapterId = jsonArray.getJSONObject(i).getString("chapter_id");
                        downContent(chapterId, bookPath);
                    }
                },
                error -> {

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ApiUtil.getApiHeader();
            }
        };
        listener = request -> stop();
        mQueue.addRequestFinishedListener(listener);
        mQueue.add(volRequest);
    }

    private void downContent(final String chapterId, final String bookPath) {
        count.incrementAndGet();
        StringRequest contentRequest = new StringRequest(
                Request.Method.GET,
                ApiUtil.API_PATH + "view/" + chapterId,
                response -> {
                    FileUtils.storeContent(ApiUtil.getContentJson(response), bookPath, chapterId);
                    for (String url : Util.findAll("(http://lknovel.lightnovel.cn/illustration/).*?(\\.jpg)", response)) {
                        downImage(url, bookPath);
                        Log.e(LOG_TAG, url);
                    }
                },
                error -> {
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ApiUtil.getApiHeader();
            }
        };
        mQueue.add(contentRequest);
    }

    private void downImage(final String url, final String path) {
        count.incrementAndGet();
        ImageRequest imageRequest = new ImageRequest(
                url,
                response -> FileUtils.storeImg(url, path, response), 0, 0, Bitmap.Config.ARGB_8888, error -> {

                });
        mQueue.add(imageRequest);
    }

    private void stop() {
        int tmp = count.decrementAndGet();
        Log.i(LOG_TAG, "stop" + tmp);
        if(tmp == 0){
            FileUtils.syncVolume(mContext, bookId,volumeId);
            mHandler.sendEmptyMessage(Constants.PROGRESS_CANCEL);
            mQueue.removeRequestFinishedListener(listener);
        }
    }
}

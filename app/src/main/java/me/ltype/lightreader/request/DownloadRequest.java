package me.ltype.lightreader.request;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.util.Map;

import me.ltype.lightreader.constant.Constants;
import me.ltype.lightreader.util.ApiUtil;
import me.ltype.lightreader.util.FileUtils;
import me.ltype.lightreader.util.Util;

/**
 * Created by ltype on 2015/6/6.
 */
public class DownloadRequest {
    private static String LOG_TAG = "DownloadRequest";
    private RequestQueue mQueue;
    private Handler mHandler;

    public DownloadRequest(RequestQueue queue, Handler handler) {
        mQueue = queue;
        mHandler = handler;
    }

    public void downBook(String volId) {
        StringRequest volRequest = new StringRequest(
                Request.Method.GET,
                ApiUtil.API_PATH + "vol/" + volId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        JSONObject volDetail = jsonObject.getJSONArray("volDetail").getJSONObject(0);
                        JSONArray chapterResult = jsonObject.getJSONArray("chapterResult");

                        String bookJson = ApiUtil.getBookJson(jsonObject);
                        String volJson = ApiUtil.getVolJson(jsonObject);
                        String chaptersJson = ApiUtil.getChaptersJson(jsonObject);

                        String bookPath = Environment.getExternalStorageDirectory().getPath() + Constants.BOOK_DIR + File.separator + volDetail.getString("series_id") + File.separator + volDetail.getString("id");
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }}) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ApiUtil.getApiHeader();
            }
        };
        mQueue.add(volRequest);
    }

    private void downContent(final String chapterId, final String bookPath) {
        StringRequest contentRequest = new StringRequest(
                Request.Method.GET,
                ApiUtil.API_PATH + "view/" + chapterId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FileUtils.storeContent(ApiUtil.getContentJson(response), bookPath, chapterId);
                        for (String url : Util.findAll("(http://lknovel.lightnovel.cn/illustration/).*?(\\.jpg)", response)) {
                            downImage(url, bookPath);
                            Log.e(LOG_TAG, url);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }}) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return ApiUtil.getApiHeader();
            }
        };
        mQueue.add(contentRequest);
    }

    private void downImage(final String url, final String path) {
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        FileUtils.storeImg(url, path, response);
                    }
                }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(imageRequest);
        mHandler.sendEmptyMessage(Constants.PROGRESS_CANCEL);
    }
}

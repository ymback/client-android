package me.ltype.lightreader.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.ltype.lightreader.R;
import me.ltype.lightreader.model.Book;
import me.ltype.lightreader.model.Volume;
import me.ltype.lightreader.util.ApiUtil;

/**
 * Created by ltype on 2015/5/1.
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {
    private String LOG_TAG = "DownloadTask";
    private Handler handler;

    public DownloadTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected String doInBackground(String... urls) {
        ApiUtil.downBook(urls[0], urls[1]);
        return "success";
    }

    @Override
    protected void onPostExecute(String result) {
        handler.sendEmptyMessage(4662);
    }
}

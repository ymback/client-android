package me.ltype.lightreader.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.net.URL;

import me.ltype.lightreader.constant.Constants;
import me.ltype.lightreader.util.ApiUtil;
import me.ltype.lightreader.util.FileUtils;
import me.ltype.lightreader.util.HttpUtil;
import me.ltype.lightreader.util.Util;

/**
 * Created by ltype on 2015/5/1.
 */
public class DownImgTask extends AsyncTask<String, Integer, Uri> {
    private String LOG_TAG = "DownImgTask";
    private ImageView imageViewr;
    private File cache;

    public DownImgTask(ImageView imageViewr) {
        cache = new File(Environment.getExternalStorageDirectory(), Constants.CACHE + File.separator + "img");
        if(!cache.exists()){
            cache.mkdirs();
        }
        this.imageViewr = imageViewr;
    }

    @Override
    protected Uri doInBackground(String... urls) {
        File file = new File(cache.getPath() + File.separator + Util.md5(Constants.SITE + urls[0]) + urls[0].substring(urls[0].lastIndexOf(".")));
        if (!file.exists()) {
            String fileName = FileUtils.storeImgs(Constants.SITE + urls[0], new File(Environment.getExternalStorageDirectory(), Constants.CACHE).getPath());
            file = new File(cache.getPath() + File.separator + fileName + urls[0].substring(urls[0].lastIndexOf(".")));
        }
        return Uri.fromFile(file);
    }

    @Override
    protected void onPostExecute(Uri result) {
        super.onPostExecute(result);
        if (imageViewr != null && result != null) {
            imageViewr.setImageURI(result);
        }
    }
}

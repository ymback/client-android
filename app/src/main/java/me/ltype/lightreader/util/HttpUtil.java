package me.ltype.lightreader.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import me.ltype.lightreader.constant.Constants;

public class HttpUtil {
	public static String doGetByApi(String url) {
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		StringBuffer response = new StringBuffer();
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			Log.e("HttpUtil", url);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setRequestProperty("User-Agent", "LKNovel-Android-0.0.1");
			connection.setRequestProperty("X-Api-Key", Constants.API_KEY);
			connection.setRequestProperty("X-Api-Time", Constants.API_TIME);
			connection.setRequestProperty("Host", "novel.macroth.com");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.connect();
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null){
				response.append(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (connection != null)
				connection.disconnect();
		}
		return response.toString();
	}

	public static byte[] getImg(String url) {
		HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream os = null;
        byte[] data = new byte[0];
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setRequestProperty("Host", "lknovel.lightnovel.cn");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Accept", "image/webp,*/*;q=0.8");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6,ja;q=0.4,en;q=0.2");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
			connection.connect();

            is = connection.getInputStream();
            os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len = is.read(buffer)) != -1){
                os.write(buffer, 0, len);
            }
            data = os.toByteArray();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (os != null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (connection != null)
                connection.disconnect();
		}
		return data;
	}
}

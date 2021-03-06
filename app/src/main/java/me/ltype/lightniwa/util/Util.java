package me.ltype.lightniwa.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ltype.lightniwa.R;

/**
 * Created by ltype on 2015/5/1.
 */
public class Util {
    private static String LOG_TAG = "Util";

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
               context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String getUnicode(String s) {
        StringBuffer sb = new StringBuffer("");
        try {
            byte[] bytes = s.getBytes("unicode");
            for (int i = 0; i < bytes.length - 1; i += 2) {
                sb.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    sb.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                sb.append(str1);
                sb.append(str);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        return string.toString();
    }

    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    public static String htmlToString(String args,boolean replaceNull){
        if(args.equals("")){
            return "";
        }
        args= args.replaceAll("(?is)<(.*?)>","");
        if(replaceNull){
            args = args.replaceAll("\\s*|\t|\r|\n","");
        }
        return args;
    }

    public static String findFirst(String p, String m) {
        String result = "";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(m);
        if (matcher.find())
            result = matcher.group();
        return result;
    }

    public static List<String> findAll(String p, String m) {
        List<String> list = new ArrayList();
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(m);
        while (matcher.find())
            list.add(matcher.group());
        return list;
    }

    public static String md5(String str){
        String reStr = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes){
                int bt = b&0xff;
                if (bt < 16){
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            reStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reStr;
    }

    public static String toCover(String volumeId, String fileName) {
        return File.separator + volumeId + File.separator + "img"  + File.separator + Util.md5(fileName) + ".jpg";
    }

    public static String parseStr(String str) {
        str = StringEscapeUtils.unescapeHtml4(str);
        str = str.replace("\\", "\\\\");
        str = str.replace("\"", "\\\"");
        return str;
    }

    public static boolean isConnect(Activity mActivity) {
        ConnectivityManager connMgr = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isConnect(Context mContext) {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static String encodeUrl(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void checkUpdate(Context mContext, boolean isShowResult) {
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        StringRequest checkReq = new StringRequest(
                Request.Method.GET,
                "http://ltype.me/api/v1/checkUpdate",
                response -> {
                    try {
                        JSONObject jsonObj = JSON.parseObject(response);
                        if (jsonObj.getInteger("versionCode") > mContext.getPackageManager().getPackageInfo("me.ltype.lightniwa", 0).versionCode){
                            com.rey.material.app.Dialog.Builder builder = null;
                            builder = new SimpleDialog.Builder(R.style.SimpleDialogLight){
                                @Override
                                public void onPositiveActionClicked(DialogFragment fragment) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(JSON.parseObject(response).getString("url")));
                                    mContext.startActivity(intent);
                                    super.onPositiveActionClicked(fragment);
                                }
                                @Override
                                public void onNegativeActionClicked(DialogFragment fragment) {
                                    super.onNegativeActionClicked(fragment);
                                }
                            };

                            ((SimpleDialog.Builder)builder).message("有新版本，是否更新？" + "\n" + jsonObj.getString("message"))
                                    .title("检查更新")
                                    .positiveAction("确定")
                                    .negativeAction("取消");

                            DialogFragment fragment = DialogFragment.newInstance(builder);
                            fragment.show(((ActionBarActivity) mContext).getSupportFragmentManager(), null);
                        } else if (isShowResult) {
                            Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(mContext, "服务器连接失败", Toast.LENGTH_SHORT).show());
        mQueue.add(checkReq);
    }
}

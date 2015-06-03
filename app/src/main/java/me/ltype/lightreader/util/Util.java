package me.ltype.lightreader.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.View;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.drakeet.materialdialog.MaterialDialog;
import me.ltype.lightreader.constant.Constants;

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

    /*public static boolean showDialog(View view, String title, String message, String PositiveBut) {
        MaterialDialog mMaterialDialog;
        mMaterialDialog= new MaterialDialog(view.getContext())
                .setTitle("下载")
                .setMessage("确定下载" + bookList.get(i).getName() + volumeList.get(i).getHeader() + volumeList.get(i).getName() + "?")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuffer bookJson = new StringBuffer();
                        bookJson.append("{")
                                .append("\"book_id\":" + bookList.get(i).getId() + ",")
                                .append("\"author\":" + "\"" + bookList.get(i).getAuthor() + "\",")
                                .append("\"illustor\":" + "\"" + bookList.get(i).getIllustrator() + "\",")
                                .append("\"publisher\":" + "\"" + bookList.get(i).getPublisher() + "\",")
                                .append("\"name\":" + "\"" + bookList.get(i).getName() + "\",")
                                .append("\"cover\":" + "\"" + Util.toCover(volumeList.get(i).getId(), Constants.SITE + bookList.get(i).getCover()) + "\",")
                                .append("\"description\":" + "\"" + bookList.get(i).getDescription() + "\"")
                                .append("}");
                        startDown(volumeList.get(i).getId(), bookJson.toString());
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }*/

    public static String encodeUrl(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}

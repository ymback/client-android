package me.ltype.lightniwa.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ltype.lightniwa.constant.Constants;
import me.ltype.lightniwa.model.Book;
import me.ltype.lightniwa.model.Volume;

public class ApiUtil {
    private static String LOG_TAG = "ApiUtil";
	public static String API_PATH = "http://novel.macroth.com/api_node/";

    private static List<String> imgList = new ArrayList<>();
    private static List<Book> lastBook = new ArrayList<>();
    private static List<Volume> lastVolume = new ArrayList<>();
	
	public static void downBook(String volumeId, String bookJson) {
        String url = ApiUtil.API_PATH + "vol/" + volumeId + "/";
        String response = HttpUtil.doGetByApi(url);
        JSONObject jsonObject = JSON.parseObject(response);
        Volume volume = getVolByJsonObj(jsonObject.getJSONArray("volDetail").getJSONObject(0));
        String volJson = getVolJson(jsonObject);
        String chaptersJson = getChaptersJson(jsonObject);

        String bookPath = Constants.BOOK_DIR + File.separator + volume.getBookId() + File.separator + volume.getId();
        FileUtils.createDir(bookPath);
        FileUtils.storeInfo(bookJson.trim(), bookPath, "book");
        FileUtils.storeInfo(volJson.trim(), bookPath, "volume");
        FileUtils.storeInfo(chaptersJson.trim(), bookPath, "chapters");

        JSONArray jsonArray = jsonObject.getJSONArray("chapterResult");

        for (int i = 0; i < jsonArray.size(); i ++) {
            String chapterId = jsonArray.getJSONObject(i).getString("chapter_id");
            String chapterUrl = ApiUtil.API_PATH + "view/" + chapterId + "/";
            String content = HttpUtil.doGetByApi(chapterUrl);
            FileUtils.storeContent(getContentJson(content), bookPath, chapterId);
        }
        if (!imgList.isEmpty()) {
            for (String imgUrl : imgList) {
                FileUtils.storeImgs(imgUrl, bookPath);
            }
        }
	}


	public static Book getBook(String json) {
		JSONObject chapter = JSON.parseObject(json);
		Book book = new Book();
		book.setId(chapter.getString("series_id"));
        book.setAuthor(chapter.getString("novel_author"));
		book.setIllustrator(chapter.getString("novel_illustor"));
		book.setPublisher(chapter.getString("novel_pub"));
        book.setName(chapter.getString("novel_title"));
		book.setCover(chapter.getString("vol_cover"));
		book.setDescription(chapter.getString("vol_desc"));
		return book;
	}

	public static Volume getVolByJsonObj(JSONObject json) {
		Volume volume = new Volume();
		volume.setIndex(json.getString("vol_number"));
		volume.setBookId(json.getString("series_id"));
		volume.setId(json.getString("id") != null ? json.getString("id") : json.getString("vol_id"));
		volume.setHeader(json.getString("vol_number"));
		volume.setName(json.getString("vol_title"));
		volume.setCover(json.getString("vol_cover"));
		volume.setDescription(json.getString("vol_desc"));
		return volume;
	}

    public static String getBookJson(JSONObject jsonObj) {
        JSONObject json = jsonObj.getJSONArray("volDetail").getJSONObject(0);
        StringBuffer bookJson = new StringBuffer();
        bookJson.append("{")
                .append("\"book_id\":" + json.getString("series_id") + ",")
                .append("\"author\":" + "\"" + json.getString("novel_author") + "\",")
                .append("\"illustrator\":" + "\"" + json.getString("novel_illustor") + "\",")
                .append("\"publisher\":" + "\"" + json.getString("novel_pub") + "\",")
                .append("\"name\":" + "\"" + json.getString("novel_title") + "\",")
                .append("\"cover\":" + "\"" + Util.toCover(json.getString("id"), Constants.SITE + json.getString("vol_cover")) + "\",")
                .append("\"description\":" + "\"" + json.getString("vol_desc") + "\"")
                .append("}");
        return bookJson.toString();
    }

    public static String getVolJson(JSONObject jsonObj) {
        JSONObject json = jsonObj.getJSONArray("volDetail").getJSONObject(0);
        String bookId = json.getString("series_id");
        String volumeId = json.getString("id");
        String cover = Constants.SITE + json.getString("vol_cover");
        if (cover != null) {
            imgList.add(cover);
            cover = Util.toCover(volumeId, cover);
        }
        StringBuffer volJson = new StringBuffer();
        volJson.append("{")
                .append("\"index\":" + "\"" + json.getString("vol_number") + "\",")
                .append("\"book_id\":" + bookId + ",")
                .append("\"volume_id\":" + volumeId + ",")
                .append("\"name\":" + "\"" + json.getString("vol_title") + "\",")
                .append("\"cover\":" + "\"" + cover + "\",")
                .append("\"description\":" + "\"" + Util.parseStr(json.getString("vol_desc")) + "\",")
                .append("}");
        return volJson.toString();
    }

    public static String getChaptersJson(JSONObject jsonObj) {
        JSONArray jsonArray = jsonObj.getJSONArray("chapterResult");
        StringBuffer chaptersJson = new StringBuffer("[");
        for (int i = 0; i < jsonArray.size(); i ++) {
            JSONObject json = jsonArray.getJSONObject(i);
            chaptersJson.append("{")
                    .append("\"index\":" + i + ",")
                    .append("\"book_id\":" + json.getString("series_id") + ",")
                    .append("\"volume_id\":" + json.getString("vol_id") + ",")
                    .append("\"chapter_id\":" + json.getString("chapter_id") + ",")
                    .append("\"name\":" + "\"" + json.getString("chapter_title") + "\"")
                    .append("},");
        }
        chaptersJson.replace(chaptersJson.length() - 1, chaptersJson.length(), "");
        chaptersJson.append("]");
        return chaptersJson.toString();
    }

    public static String getContentJson(String str) {
        JSONObject jsonObject = JSON.parseObject(str);
        JSONArray jsonArray = jsonObject.getJSONArray("content");
        String volumeId = jsonObject.getString("vol_id");
        StringBuffer contentJson = new StringBuffer("[");
        for (int i = 0; i < jsonArray.size(); i ++) {
            JSONObject json = jsonArray.getJSONObject(i);
            String content = json.getString("content");
            if (content.equals("<br>")) continue;
            String imgUrl = Util.findFirst("(http://).*?(\\.jpg)", content);
            int isImg = 0;
            if (imgUrl != "") {
                imgList.add(imgUrl);
                content = Util.toCover(volumeId, imgUrl);
                isImg = 1;
            }
            contentJson.append("{")
                    .append("\"index\":" + json.getString("index") + ",")
                    .append("\"is_img\":" + isImg + ",")
                    .append("\"content\":\"" + Util.parseStr(content) + "\"")
                    .append("},");
        }
        contentJson.replace(contentJson.length() - 1, contentJson.length(), "");
        contentJson.append("]");
        return contentJson.toString();
    }

    public static Map<String, String> getApiHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "LKNovel-Android-0.0.1");
        header.put("X-Api-Key", Constants.API_KEY);
        header.put("X-Api-Time", Constants.API_TIME);
        header.put("Host", "novel.macroth.com");
        header.put("Connection", "keep-alive");
        return header;
    }
}

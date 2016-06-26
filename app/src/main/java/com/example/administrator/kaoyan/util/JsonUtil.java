package com.example.administrator.kaoyan.util;

import com.example.administrator.kaoyan.bean.Banner;
import com.example.administrator.kaoyan.bean.ExamSmallItem;
import com.example.administrator.kaoyan.bean.NewsItem;
import com.example.administrator.kaoyan.bean.RealTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/27.
 */
public class JsonUtil {

    public static List<NewsItem> parseJsonNews(String json) {
        List<NewsItem> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonObject1.getJSONArray("contents");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                String title = jsonObject2.getString("title");
                String posttime = jsonObject2.getString("posttime");
                String description = jsonObject2.getString("description");
                String url = jsonObject2.getString("url");
                NewsItem newsItem = new NewsItem();
                newsItem.setTitle(title);
                newsItem.setPosttime(posttime);
                newsItem.setDescription(description);
                newsItem.setUrl(url);
                list.add(newsItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<ExamSmallItem> parseJsonExam(String json) {
        List<ExamSmallItem> list = new ArrayList<>();//有多少科目就有多少个对象,比如医师，药师，护理学
        try {
            JSONObject jsonObject = new JSONObject(json);
            String resultCode = jsonObject.getString("resultCode");
            if (!resultCode.equals("0")){
                return null;
            }else {
                JSONArray result = jsonObject.getJSONArray("result");
                for (int i=0;i<result.length();i++){
                    JSONObject jsonObject1 = result.getJSONObject(i);
                    JSONArray content = jsonObject1.getJSONArray("content");
                    for (int j=0;j<content.length();j++){
                        JSONObject jsonObject2 = content.getJSONObject(j);
                        ExamSmallItem examSmallItem = new ExamSmallItem();
                        String formalDBURL = jsonObject2.getString("formalDBURL");
                        String title = jsonObject2.getString("title");
                        examSmallItem.setTitle(title);
                        examSmallItem.setFormalDBURL(formalDBURL);
                        list.add(examSmallItem);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Banner> parseJsonBanner(String json) {
        List<Banner> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("banners");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String contentURL = jsonObject1.getString("contentURL");
                String imageURL = jsonObject1.getString("imageURL");
                String title = jsonObject1.getString("title");
                Banner banner = new Banner();
                banner.setContentURL(contentURL);
                banner.setImageURL(imageURL);
                banner.setTitle(title);
                list.add(banner);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static List<RealTime> parseJsonReal(String json){
        List<RealTime>list=new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject(json);
            JSONArray jsonArray=jsonObject.getJSONArray("list");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                String title=jsonObject1.getString("title");
                String teacher=jsonObject1.getString("teacher");
                String date=jsonObject1.getString("date");
                String pic=jsonObject1.getString("pic");
                String num=jsonObject1.getString("num");
                String yid=jsonObject1.getString("yid");
                RealTime realTime=new RealTime();
                realTime.setTitle(title);
                realTime.setDate(date);
                realTime.setNum(num);
                realTime.setTeacher(teacher);
                realTime.setPic(pic);
                realTime.setYid(yid);
                list.add(realTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}

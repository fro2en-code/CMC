package com.cdc.cdccmc.common.util;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
  
public class HttpClientUtil {  
  
    public static String doGet(String url, Map<String, String> param) {  
  
        // 创建Httpclient对象  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
  
        String resultString = "";  
        CloseableHttpResponse response = null;  
        try {  
            // 创建uri  
            URIBuilder builder = new URIBuilder(url);  
            if (param != null) {  
                for (String key : param.keySet()) {  
                    builder.addParameter(key, param.get(key));  
                }  
            }  
            URI uri = builder.build();  
  
            // 创建http GET请求  
            HttpGet httpGet = new HttpGet(uri);  
  
            // 执行请求  
            response = httpclient.execute(httpGet);  
            // 判断返回状态是否为200  
            if (response.getStatusLine().getStatusCode() == 200) {  
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (response != null) {  
                    response.close();  
                }  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return resultString;  
    }  
  
    public static String doGet(String url) {  
        return doGet(url, null);  
    }  
  
    public static String doPost(String url, Map<String, String> param) {  
        // 创建Httpclient对象  
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        CloseableHttpResponse response = null;  
        String resultString = "";  
        try {  
            // 创建Http Post请求  
            HttpPost httpPost = new HttpPost(url); 
            // 创建参数列表  
            if (param != null) {  
                List<NameValuePair> paramList = new ArrayList<>();  
                for (String key : param.keySet()) {  
                    paramList.add(new BasicNameValuePair(key, param.get(key)));  
                }  
                // 模拟表单  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,"utf-8");  
                httpPost.setEntity(entity);  
            }  
            
            // 执行http请求  
            response = httpClient.execute(httpPost);  
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                response.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
  
        return resultString;  
    }  
  
    public static String doPost(String url) {  
        return doPost(url, null);  
    }  
      
    public static String doPostJson(String url, String json) {  
        // 创建Httpclient对象  
        CloseableHttpClient httpClient = HttpClients.createDefault();  
        CloseableHttpResponse response = null;  
        String resultString = "";  
        try {  
            // 创建Http Post请求  
            HttpPost httpPost = new HttpPost(url);  
            // 创建请求内容  
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);  
            httpPost.setEntity(entity);  
            // 执行http请求  
            response = httpClient.execute(httpPost);  
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                response.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
  
        return resultString;  
    }  
    
    
    public static void main(String[] args){
    	String url = "http://101.201.55.12:9990/auth/app";
    	
    	String timestamp = System.currentTimeMillis()/1000+"";
    	String app_uuid =  "97258d7606974276ac099829d7cd415f";
    	String corp_uuid = "cd6ea9c56ef841d1ae11a0c284b41815";
    	String app_secret = "6c41568199c24a35a7f8bf038569f5e8";
    	String signature = Md5Util.md5(app_uuid+timestamp+app_secret);
    	
        String format = "corp_uuid=%s&app_uuid=%s&signature=%s&timestamp=%s";
		String params = String.format(format,corp_uuid, app_uuid, signature,timestamp);

        String response = HttpClient.sendPost(url, params);
        Map<String, Object> map = JSON.parseObject(response, new TypeReference<Map<String, Object>>() {
        });
        if ((Integer) map.get("code") != 0) {
            System.out.println((map.get("message").toString()));
        }
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        System.out.println(content.get("accessToken").toString());
    }
}  
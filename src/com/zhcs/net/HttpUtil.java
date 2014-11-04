package com.zhcs.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

public class HttpUtil{
	
	public static final String URL="http://172.18.217.211:8080/SmartCity/";
	/*
	 * @param url 发送请求的url
	 * @param params 请求参数
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String postRequest(String url
			,Map<String,String> rawParams)throws Exception
		{
			HttpClient httpClient=new HttpUtil().createHttpClient();
			//创建HttpPost对象
			HttpPost post=new HttpPost(url);
			//如果参数传递过多，可以对传递的参数进行封装
			List<NameValuePair> params=new ArrayList<NameValuePair>();
			for(String key:rawParams.keySet())
			{
				//封装请求函数
				params.add(new BasicNameValuePair(key,rawParams.get(key)));
			}
			//设置请求参数
			post.setEntity(new UrlEncodedFormEntity(params,"gbk"));
			//发送post请求
			HttpResponse httpResponse=httpClient.execute(post);
			//如果服务器成功的返回响应
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				//获取服务器响应字符串
				String result=EntityUtils.toString(httpResponse.getEntity());
				return result;
			}
			return null;
		}
	
	//创建HttpClient对象
	 private HttpClient createHttpClient(){
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
 
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http",PlainSocketFactory.getSocketFactory(),80));
        schReg.register(new Scheme("https",PlainSocketFactory.getSocketFactory(),433));
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params,schReg);
 
        return new DefaultHttpClient(conMgr,params);
    }
    
    public static JSONObject doGet(String url) {
		  try {
			   String result = null;
			   Log.v("bbbb","ssss");
			   DefaultHttpClient httpClient = new DefaultHttpClient();
			   HttpGet request = new HttpGet(url);
			   HttpResponse response = httpClient.execute(request);
			   result = EntityUtils.toString(response.getEntity());
			   JSONObject object = new JSONObject(result);
			   Log.i("HttpActivity", result);
			   return object;
		               
		  } catch (Exception e) {
		   // TODO: handle exception
		  }
		  return null;
		 }
};

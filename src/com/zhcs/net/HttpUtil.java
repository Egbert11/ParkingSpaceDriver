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
	 * @param url ���������url
	 * @param params �������
	 * @return ��������Ӧ�ַ���
	 * @throws Exception
	 */
	public static String postRequest(String url
			,Map<String,String> rawParams)throws Exception
		{
			HttpClient httpClient=new HttpUtil().createHttpClient();
			//����HttpPost����
			HttpPost post=new HttpPost(url);
			//����������ݹ��࣬���ԶԴ��ݵĲ������з�װ
			List<NameValuePair> params=new ArrayList<NameValuePair>();
			for(String key:rawParams.keySet())
			{
				//��װ������
				params.add(new BasicNameValuePair(key,rawParams.get(key)));
			}
			//�����������
			post.setEntity(new UrlEncodedFormEntity(params,"gbk"));
			//����post����
			HttpResponse httpResponse=httpClient.execute(post);
			//����������ɹ��ķ�����Ӧ
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				//��ȡ��������Ӧ�ַ���
				String result=EntityUtils.toString(httpResponse.getEntity());
				return result;
			}
			return null;
		}
	
	//����HttpClient����
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

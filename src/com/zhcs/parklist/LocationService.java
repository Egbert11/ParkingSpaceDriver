package com.zhcs.parklist;

import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LocationService extends Service {
    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private static int LOCATION_COUTNS = 0;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private static double lat = 0.0;
    private static double lng = 0.0;
     
    @Override
    public void onCreate() {
        super.onCreate();
        
        locationClient = new LocationClient(this);
        Log.e("LocationService","RUN");
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME);    //设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);
         
        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
             
            @Override
            public void onReceiveLocation(BDLocation location) {
                // TODO Auto-generated method stub
                if (location == null) {
                    return;
                }
                lat = location.getLatitude();
                lng = location.getLongitude();
                Log.e("LocationServiceChanged","lat:"+lat+" lng:"+lng);
//                StringBuffer sb = new StringBuffer(256);
//                sb.append("Time : ");
//                sb.append(location.getTime());
//                sb.append("\nError code : ");
//                sb.append(location.getLocType());
//                sb.append("\nLatitude : ");
//                sb.append(location.getLatitude());
//                sb.append("\nLontitude : ");
//                sb.append(location.getLongitude());
//                sb.append("\nRadius : ");
//                sb.append(location.getRadius());
//                if (location.getLocType() == BDLocation.TypeGpsLocation){
//                    sb.append("\nSpeed : ");
//                    sb.append(location.getSpeed());
//                    sb.append("\nSatellite : ");
//                    sb.append(location.getSatelliteNumber());
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//                    sb.append("\nAddress : ");
//                    sb.append(location.getAddrStr());
//                }
//                LOCATION_COUTNS ++;
//                sb.append("\n检查位置更新次数：");
//                sb.append(String.valueOf(LOCATION_COUTNS));
            }
             
            @Override
            public void onReceivePoi(BDLocation location) {
            }  
        });

        startTimer();
    }
    
    public void startLocate() {  
        if (locationClient.isStarted()) {  
            locationClient.requestLocation();  
        } else {  
            locationClient.start();  
        }  
    }  
    
    
    /*
     * 每隔10s定位
     */
    private void startTimer(){
 	   if (timer == null) {  
 		   timer = new Timer();  
 		   Log.e("timer","start");
        } 
 	   
 	   if(timerTask == null){
 		   Log.e("timerTask","start");
 		  timerTask = new TimerTask() {
 			
 			@Override
 			public void run() {
 				startLocate();
 			}
 		};
 	   }
 	   
 	   if(timer != null && timerTask != null){
 		  timer.schedule(timerTask, 0, 10000);
 	   }
    }
    
    public static double getLatitude(){
    	return lat;
    }
    
    public static double getLongitude(){
    	return lng;
    }
     
    @Override
	public void onDestroy() {
        super.onDestroy();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
     
     
}
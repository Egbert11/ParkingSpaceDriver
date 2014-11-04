package com.zhcs.parkingSpaceDao;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.zhcs.driverBean.BookSpaceInfo;
import com.zhcs.driverBean.ParkingSpaceInfo;
import com.zhcs.driverBean.SpaceInfoBean;
import com.zhcs.regAndLogin.Register;

public class MyOverlay extends ItemizedOverlay<OverlayItem> {
    
    private static Activity activity;
    private static ArrayList<SpaceInfoBean> list;
    private static int index;
    private static OverlayItem item;
    //经纬度转换后的地址
    private static String addr = "";
    //显示是否转换成功
    private static boolean flagOfAddr = false;
    private String address = "";

    public MyOverlay(Activity activity, Drawable drawable, MapView mapView) {
        super(drawable, mapView);
        MyOverlay.activity = activity;
    }

    protected boolean onTap(int i) {
        item = this.getItem(i);
        
        //Toast.makeText(activity, item.getTitle()+" hah "+item.getSnippet(), Toast.LENGTH_LONG).show();
        index = Integer.parseInt(item.getSnippet());
        //list为车位信息列表
        list = ParkingSpaceInfo.getList();
        //设置订阅车位信息
        BookSpaceInfo.setSpace(list.get(index));
        getAddress(list.get(index).getCommunityid());
        //将经纬度转换为地址
//        MKSearch search = new MKSearch();
//        search.init(MapForFindParkingSpace.app.mBMapManager, new MyMKSearchListener());
//        search.reverseGeocode(new GeoPoint(list.get(index).getLat(), list.get(index).getLng()));
        return true;
    }
    
    //向服务器发送此车位正处于订阅状态
    private void bookParkingSpace(){
		AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
		Log.d("myoverlay", BookSpaceInfo.getSpace().getSpaceid());
		
		query.whereEqualTo("objectId", BookSpaceInfo.getSpace().getSpaceid());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("查询车位状态成功", "查询到" + avObjects.size() + " 条符合条件的数据");
		            AVObject mod = avObjects.get(0);
		            Log.e("overlay","卡死了");
		            if(mod.getInt("state") != 0) {
		            	Toast.makeText(activity,"此车位正被别人订阅中，请选择其他车位", Toast.LENGTH_SHORT).show();
		            }else {
			          	//修改状态为1
						mod.put("state", 1);
						Log.e("overlay","卡死了1");
						mod.saveInBackground(new SaveCallback() {
							public void done(AVException arg0) {
								if (arg0 == null) {
									Log.e("overlay","卡死了2");
									//Toast.makeText(activity,"预订成功", Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(activity, BookSpace.class);
									activity.startActivity(intent);
						        } else {
						        	Toast.makeText(activity, "预订失败", Toast.LENGTH_SHORT).show();
							}
						}
			        });
		            }
		        }else {
		        	Log.d("失败", "错误信息"+e.getMessage());
		        	Toast.makeText(activity, "预订失败", Toast.LENGTH_SHORT).show();
		        }
			}
		});
    }
    
    /*
     * 根据小区的主键去查询其地址信息
     */
    private void getAddress(String objectid) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Community");
		query.whereEqualTo("objectId", objectid);
		final Message msg = new Message();
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            AVObject obj = avObjects.get(0);
		            address = obj.getString("address");
		            msg.what = 0x00;
		            } else {
		            	msg.what = 0x01;
		            }
		        showDialog.sendMessage(msg);
		        }
			});
		
	}
    
    final Handler showDialog = new Handler(){
    	@Override
		public void handleMessage(Message msg)
		{
			if(msg.what==0x00){
		    	String message = "地址："+address+"\n"+"开放时间："+list.get(index).getStart()
					+":00--"+list.get(index).getEnd()+":00"+"\n"+"你确定订阅该车位？";
		    	address = "";
		    	new AlertDialog.Builder(activity).setTitle("车位信息").setMessage(message)
				.setPositiveButton("订阅", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
				        //向服务器发送订阅消息
				        bookParkingSpace();
					};
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								
							}
						}).show();
			}
			else if(msg.what==0x01){
				Toast.makeText(activity, "获取地址失败", Toast.LENGTH_SHORT).show();
			}
		}
    };
    
    class MyMKSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			
			if (arg0 == null) {
				flagOfAddr = false;
				Log.v("地址1", "");
			}else{
				addr = arg0.strAddr;
				flagOfAddr = true;
				Log.v("地址2", addr);
			}
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
    }
}

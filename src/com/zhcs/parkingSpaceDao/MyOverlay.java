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
    //��γ��ת����ĵ�ַ
    private static String addr = "";
    //��ʾ�Ƿ�ת���ɹ�
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
        //listΪ��λ��Ϣ�б�
        list = ParkingSpaceInfo.getList();
        //���ö��ĳ�λ��Ϣ
        BookSpaceInfo.setSpace(list.get(index));
        getAddress(list.get(index).getCommunityid());
        //����γ��ת��Ϊ��ַ
//        MKSearch search = new MKSearch();
//        search.init(MapForFindParkingSpace.app.mBMapManager, new MyMKSearchListener());
//        search.reverseGeocode(new GeoPoint(list.get(index).getLat(), list.get(index).getLng()));
        return true;
    }
    
    //����������ʹ˳�λ�����ڶ���״̬
    private void bookParkingSpace(){
		AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
		Log.d("myoverlay", BookSpaceInfo.getSpace().getSpaceid());
		
		query.whereEqualTo("objectId", BookSpaceInfo.getSpace().getSpaceid());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("��ѯ��λ״̬�ɹ�", "��ѯ��" + avObjects.size() + " ����������������");
		            AVObject mod = avObjects.get(0);
		            Log.e("overlay","������");
		            if(mod.getInt("state") != 0) {
		            	Toast.makeText(activity,"�˳�λ�������˶����У���ѡ��������λ", Toast.LENGTH_SHORT).show();
		            }else {
			          	//�޸�״̬Ϊ1
						mod.put("state", 1);
						Log.e("overlay","������1");
						mod.saveInBackground(new SaveCallback() {
							public void done(AVException arg0) {
								if (arg0 == null) {
									Log.e("overlay","������2");
									//Toast.makeText(activity,"Ԥ���ɹ�", Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(activity, BookSpace.class);
									activity.startActivity(intent);
						        } else {
						        	Toast.makeText(activity, "Ԥ��ʧ��", Toast.LENGTH_SHORT).show();
							}
						}
			        });
		            }
		        }else {
		        	Log.d("ʧ��", "������Ϣ"+e.getMessage());
		        	Toast.makeText(activity, "Ԥ��ʧ��", Toast.LENGTH_SHORT).show();
		        }
			}
		});
    }
    
    /*
     * ����С��������ȥ��ѯ���ַ��Ϣ
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
		    	String message = "��ַ��"+address+"\n"+"����ʱ�䣺"+list.get(index).getStart()
					+":00--"+list.get(index).getEnd()+":00"+"\n"+"��ȷ�����ĸó�λ��";
		    	address = "";
		    	new AlertDialog.Builder(activity).setTitle("��λ��Ϣ").setMessage(message)
				.setPositiveButton("����", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
				        //����������Ͷ�����Ϣ
				        bookParkingSpace();
					};
						}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								
							}
						}).show();
			}
			else if(msg.what==0x01){
				Toast.makeText(activity, "��ȡ��ַʧ��", Toast.LENGTH_SHORT).show();
			}
		}
    };
    
    class MyMKSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			
			if (arg0 == null) {
				flagOfAddr = false;
				Log.v("��ַ1", "");
			}else{
				addr = arg0.strAddr;
				flagOfAddr = true;
				Log.v("��ַ2", addr);
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

package com.zhcs.parkingSpaceDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.zhcs.driverBean.ParkingSpaceInfo;
import com.zhcs.driverBean.SpaceInfoBean;
import com.zhcs.regAndLogin.R;

/**
 * ��ʾpoi�������� 
 */
public class MapForFindParkingSpace extends Activity {
	
	public static DemoApplication app = null;
	private MapView mMapView = null;
	private MKSearch mSearch = null;   // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private Timer getPublicSpaceTimer = null;
	private TimerTask getPublicSpaceTimerTask = null;
	/**
	 * �����ؼ������봰��
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
    private int load_Index;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 /**
          * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager.
          * BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
          * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
          */
         app = (DemoApplication)this.getApplication();
         if (app.mBMapManager == null) {
             app.mBMapManager = new BMapManager(getApplicationContext());
             /**
              * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
              */
             app.mBMapManager.init(new DemoApplication.MyGeneralListener());
         }
        setContentView(R.layout.activity_mapforfindparkingspace);
        mMapView = (MapView)findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(12);
        //mMapView.getController().animateTo(new GeoPoint(23066803, 113391886));
        mMapView.getController().animateTo(new GeoPoint(23078304, 113402161));
        //����LocationClient��
        mLocationClient = new LocationClient(getApplicationContext());
        //ע���������
        mLocationClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
        option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
        option.setScanSpan(5000);//���÷���λ����ļ��ʱ��Ϊ5000ms
        option.setIsNeedAddress(true);//���صĶ�λ���������ַ��Ϣ
        option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
        mLocationClient.setLocOption(option);
        //��ʼ��λ
        mLocationClient.start();
        if (mLocationClient != null && mLocationClient.isStarted())
        	mLocationClient.requestLocation();
        else 
        	Log.d("LocSDK3", "locClient is null or not started");
        
        //�ӷ�������ȡ���ݲ����³�λ��Ϣ
        startTimer();
        
		// ��ʼ������ģ�飬ע�������¼�����
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){
            //�ڴ˴�������ҳ���
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
                if (error != 0) {
                    Toast.makeText(MapForFindParkingSpace.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MapForFindParkingSpace.this, "�ɹ����鿴����ҳ��", Toast.LENGTH_SHORT).show();
                }
            }
            /**
             * �ڴ˴���poi�������
             */
            public void onGetPoiResult(MKPoiResult res, int type, int error) {
                // ����ſɲο�MKEvent�еĶ���
                if (error != 0 || res == null) {
                    Toast.makeText(MapForFindParkingSpace.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_LONG).show();
                    return;
                }
                // ����ͼ�ƶ�����һ��POI���ĵ�
                if (res.getCurrentNumPois() > 0) {
                    // ��poi�����ʾ����ͼ��
                    MyPoiOverlay poiOverlay = new MyPoiOverlay(MapForFindParkingSpace.this, mMapView, mSearch);
                    poiOverlay.setData(res.getAllPoi());
//                    mMapView.getOverlays().clear();
//                    mMapView.getOverlays().add(poiOverlay);
//                    mMapView.refresh();
                    //��ePoiTypeΪ2��������·����4��������·��ʱ�� poi����Ϊ��
                    for( MKPoiInfo info : res.getAllPoi() ){
                    	if ( info.pt != null ){
                    		mMapView.getController().animateTo(info.pt);
                    		//mMapView.getController().setZoom(16);
                    		break;
                    	}
                    }
                } 
//                else if (res.getCityListNum() > 0) {
//                	//������ؼ����ڱ���û���ҵ����������������ҵ�ʱ�����ذ����ùؼ�����Ϣ�ĳ����б�
//                    String strInfo = "��";
//                    for (int i = 0; i < res.getCityListNum(); i++) {
//                        strInfo += res.getCityListInfo(i).city;
//                        strInfo += ",";
//                    }
//                    strInfo += "�ҵ����";
//                    Toast.makeText(MapForFindParkingSpace.this, strInfo, Toast.LENGTH_LONG).show();
//                }
            }
            public void onGetDrivingRouteResult(MKDrivingRouteResult res,
                    int error) {
            }
            public void onGetTransitRouteResult(MKTransitRouteResult res,
                    int error) {
            }
            public void onGetWalkingRouteResult(MKWalkingRouteResult res,
                    int error) {
            }
            public void onGetAddrResult(MKAddrInfo res, int error) {
            }
            
            public void onGetBusDetailResult(MKBusLineResult result, int iError) {
            }
            /**
             * ���½����б�
             */
            @Override
            public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
            	if ( res == null || res.getAllSuggestions() == null){
            		return ;
            	}
            	sugAdapter.clear();
            	for ( MKSuggestionInfo info : res.getAllSuggestions()){
            		if ( info.key != null)
            		    sugAdapter.add(info.key);
            	}
            	sugAdapter.notifyDataSetChanged();
                
            }
			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub
				
			}
        });
        
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        
        /**
         * ������ؼ��ֱ仯ʱ����̬���½����б�
         */
        keyWorldsView.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				 if ( cs.length() <=0 ){
					 return ;
				 }
				 String city =  ((EditText)findViewById(R.id.city)).getText().toString();
				 /**
				  * ʹ�ý������������ȡ�����б������onSuggestionResult()�и���
				  */
                 mSearch.suggestionSearch(cs.toString(), city);				
			}
        });
        
    }
    
    @Override
    protected void onPause() {
        mMapView.onPause();
        stopTimer();
        Log.e("MapForFindParkingSpace","pause");
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        if(getPublicSpaceTimer == null && getPublicSpaceTimerTask == null)
        	startTimer();
        Log.e("MapForFindParkingSpace","resume");
        super.onResume();
    }
    
    @Override
    protected void onDestroy(){
    	mMapView.destroy();
    	mSearch.destory();
    	stopTimer();
    	Log.e("MapForFindParkingSpace","destroy");
    	super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
    private void initMapView() {
        mMapView.setLongClickable(true);
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
    }
    /**
     * Ӱ��������ť����¼�
     * @param v
     */
    public void searchButtonProcess(View v) {
          EditText editCity = (EditText)findViewById(R.id.city);
          EditText editSearchKey = (EditText)findViewById(R.id.searchkey);
//          mSearch.geocode(editSearchKey.getText().toString(), 
//        		  editCity.getText().toString());
          mSearch.poiSearchInCity(editCity.getText().toString(), 
                  editSearchKey.getText().toString());
    }
   public void goToNextPage(View v) {
        //������һ��poi
        int flag = mSearch.goToPoiPage(++load_Index);
        if (flag != 0) {
            Toast.makeText(MapForFindParkingSpace.this, "��������ʼ��Ȼ����������һ������", Toast.LENGTH_SHORT).show();
        }
    }
   
   class MyLocationListener implements BDLocationListener {
	    @Override
	   public void onReceiveLocation(BDLocation location) {
	      if (location == null)
	          return ;
	      //���õ������
	      NaviDemo.setmLat1(location.getLatitude());
	      NaviDemo.setmLon1(location.getLongitude());
	    }
	public void onReceivePoi(BDLocation poiLocation) {
	    }
	}
   
   /*
    * ��ȡ���ó�λ��Ϣ
    */
   public void getSpaceInfo() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
		query.whereEqualTo("state", 0); //0�ɶ��ģ� 1���Ĵ����У� 2�Ѷ���
		
		query.findInBackground(new FindCallback<AVObject>() {
				@Override
				public void done(List<AVObject> arg0, AVException e) {
					if (e == null) {
			            Log.e("��ȡ��λ����", "��ѯ��" + arg0.size() + " ����������������");
			            /*
			             *�ɹ���ȡ�����ݺ����list���ڵ�ͼ�Ͻ�����ʾ
			             */
			            ParkingSpaceInfo.initializeList(arg0);
			            updatePublicSpace();
			        } else {
			            Log.e("��ȡ��λ����", "��ѯ����: " + e.getMessage());
			        }
			}
		});
	}

   //�ӷ�������ȡ��λ��Ϣ
   private void startTimer(){
	   if (getPublicSpaceTimer == null) {  
		   getPublicSpaceTimer = new Timer();  
		   Log.e("timer","start");
       } 
	   
	   if(getPublicSpaceTimerTask == null){
		   Log.e("timerTask","start");
		   getPublicSpaceTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				//�ӷ�������ȡ��λ��Ϣ
				getSpaceInfo();
			}
		};
	   }
	   
	   if(getPublicSpaceTimer != null && getPublicSpaceTimerTask != null){
		   getPublicSpaceTimer.schedule(getPublicSpaceTimerTask, 0, 10000);
	   }
   }
   
   //ֹͣ��ʱ��
   private void stopTimer() {
	   if (getPublicSpaceTimer != null) {  
		   getPublicSpaceTimer.cancel();
		   getPublicSpaceTimer =null;
		   Log.e("timer","cancel");
       } 
	   
	   if(getPublicSpaceTimerTask != null){
		   getPublicSpaceTimerTask.cancel();
		   getPublicSpaceTimerTask = null;
		   Log.e("timerTask","cancel");
	   }
   }
   
   //�ڵ�ͼ�ϸ��³�λ��Ϣ
   private void updatePublicSpace() {
		if(!ParkingSpaceInfo.getList().isEmpty()){
			ArrayList<SpaceInfoBean> list = ParkingSpaceInfo.getList();
			Drawable drawable = getResources().getDrawable(R.drawable.icon_maker);
			//����drawable
        	MyOverlay overlay = new MyOverlay(MapForFindParkingSpace.this, drawable, mMapView);
        	int size = list.size();
        	for(int i = 0; i < size; i++){
            	OverlayItem overlayItem = new OverlayItem(new GeoPoint(list.get(i).getLat(), list.get(i).getLng()), 
            			"�۸�"+list.get(i).getPrice(), String.valueOf(i));
            	overlayItem.setMarker(drawable);
            	overlay.addItem(overlayItem);
        	}
        	mMapView.getOverlays().clear();
            mMapView.getOverlays().add(overlay);
            mMapView.refresh();
		} else {
			mMapView.getOverlays().clear();
            mMapView.refresh();
		}
	} 
   
}

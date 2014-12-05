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
 * 演示poi搜索功能 
 */
public class MapForFindParkingSpace extends Activity {
	
	public static DemoApplication app = null;
	private MapView mMapView = null;
	private MKSearch mSearch = null;   // 搜索模块，也可去掉地图模块独立使用
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private Timer getPublicSpaceTimer = null;
	private TimerTask getPublicSpaceTimerTask = null;
	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
    private int load_Index;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	 /**
          * 使用地图sdk前需先初始化BMapManager.
          * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
          * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
          */
         app = (DemoApplication)this.getApplication();
         if (app.mBMapManager == null) {
             app.mBMapManager = new BMapManager(getApplicationContext());
             /**
              * 如果BMapManager没有初始化则初始化BMapManager
              */
             app.mBMapManager.init(new DemoApplication.MyGeneralListener());
         }
        setContentView(R.layout.activity_mapforfindparkingspace);
        mMapView = (MapView)findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(12);
        //mMapView.getController().animateTo(new GeoPoint(23066803, 113391886));
        mMapView.getController().animateTo(new GeoPoint(23078304, 113402161));
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        //开始定位
        mLocationClient.start();
        if (mLocationClient != null && mLocationClient.isStarted())
        	mLocationClient.requestLocation();
        else 
        	Log.d("LocSDK3", "locClient is null or not started");
        
        //从服务器获取数据并更新车位信息
        startTimer();
        
		// 初始化搜索模块，注册搜索事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){
            //在此处理详情页结果
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
                if (error != 0) {
                    Toast.makeText(MapForFindParkingSpace.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MapForFindParkingSpace.this, "成功，查看详情页面", Toast.LENGTH_SHORT).show();
                }
            }
            /**
             * 在此处理poi搜索结果
             */
            public void onGetPoiResult(MKPoiResult res, int type, int error) {
                // 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(MapForFindParkingSpace.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                // 将地图移动到第一个POI中心点
                if (res.getCurrentNumPois() > 0) {
                    // 将poi结果显示到地图上
                    MyPoiOverlay poiOverlay = new MyPoiOverlay(MapForFindParkingSpace.this, mMapView, mSearch);
                    poiOverlay.setData(res.getAllPoi());
//                    mMapView.getOverlays().clear();
//                    mMapView.getOverlays().add(poiOverlay);
//                    mMapView.refresh();
                    //当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
                    for( MKPoiInfo info : res.getAllPoi() ){
                    	if ( info.pt != null ){
                    		mMapView.getController().animateTo(info.pt);
                    		//mMapView.getController().setZoom(16);
                    		break;
                    	}
                    }
                } 
//                else if (res.getCityListNum() > 0) {
//                	//当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
//                    String strInfo = "在";
//                    for (int i = 0; i < res.getCityListNum(); i++) {
//                        strInfo += res.getCityListInfo(i).city;
//                        strInfo += ",";
//                    }
//                    strInfo += "找到结果";
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
             * 更新建议列表
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
         * 当输入关键字变化时，动态更新建议列表
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
				  * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
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
     * 影响搜索按钮点击事件
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
        //搜索下一组poi
        int flag = mSearch.goToPoiPage(++load_Index);
        if (flag != 0) {
            Toast.makeText(MapForFindParkingSpace.this, "先搜索开始，然后再搜索下一组数据", Toast.LENGTH_SHORT).show();
        }
    }
   
   class MyLocationListener implements BDLocationListener {
	    @Override
	   public void onReceiveLocation(BDLocation location) {
	      if (location == null)
	          return ;
	      //设置导航起点
	      NaviDemo.setmLat1(location.getLatitude());
	      NaviDemo.setmLon1(location.getLongitude());
	    }
	public void onReceivePoi(BDLocation poiLocation) {
	    }
	}
   
   /*
    * 获取可用车位信息
    */
   public void getSpaceInfo() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
		query.whereEqualTo("state", 0); //0可订阅， 1订阅处理中， 2已订阅
		
		query.findInBackground(new FindCallback<AVObject>() {
				@Override
				public void done(List<AVObject> arg0, AVException e) {
					if (e == null) {
			            Log.e("获取车位数据", "查询到" + arg0.size() + " 条符合条件的数据");
			            /*
			             *成功获取到数据后更新list并在地图上进行显示
			             */
			            ParkingSpaceInfo.initializeList(arg0);
			            updatePublicSpace();
			        } else {
			            Log.e("获取车位数据", "查询错误: " + e.getMessage());
			        }
			}
		});
	}

   //从服务器获取车位信息
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
				//从服务器获取车位信息
				getSpaceInfo();
			}
		};
	   }
	   
	   if(getPublicSpaceTimer != null && getPublicSpaceTimerTask != null){
		   getPublicSpaceTimer.schedule(getPublicSpaceTimerTask, 0, 10000);
	   }
   }
   
   //停止计时器
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
   
   //在地图上更新车位信息
   private void updatePublicSpace() {
		if(!ParkingSpaceInfo.getList().isEmpty()){
			ArrayList<SpaceInfoBean> list = ParkingSpaceInfo.getList();
			Drawable drawable = getResources().getDrawable(R.drawable.icon_maker);
			//监听drawable
        	MyOverlay overlay = new MyOverlay(MapForFindParkingSpace.this, drawable, mMapView);
        	int size = list.size();
        	for(int i = 0; i < size; i++){
            	OverlayItem overlayItem = new OverlayItem(new GeoPoint(list.get(i).getLat(), list.get(i).getLng()), 
            			"价格："+list.get(i).getPrice(), String.valueOf(i));
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

package com.zhcs.parkingSpaceDao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.zhcs.navi.BNavigatorActivity;
import com.zhcs.regAndLogin.R;

public class NaviDemo extends Activity {
	
	//目前所在位置坐标
	private static double mLat1; 
	private static double mLon1; 
   	//需要到达地点坐标
	private static double mLat2;   
	private static double mLon2;
	private boolean mIsEngineInitSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navi_demo);
		BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
                mNaviEngineInitListener, new LBSAuthManagerListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        String str = null;
                        if (0 == status) {
                            str = "key校验成功!";
                        } else {
                            str = "key校验失败, " + msg;
                        }
                        Toast.makeText(NaviDemo.this, str,
                                Toast.LENGTH_LONG).show();
                    }
                });
		//页面初始化
		TextView text = (TextView)findViewById(R.id.navi_info);
		text.setText(String.format("起点:(%f,%f)\n终点:(%f,%f)",mLat1,mLon1,mLat2,mLon2));
	}
	
	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			mIsEngineInitSuccess = true;
		}

		public void engineInitStart() {
		}

		public void engineInitFail() {
		}
	};
   /**
    * 开始导航		
    * @param view
    */
   public void startNavi(View view){		
//	    BNaviPoint mStartPoint = new BNaviPoint(mLat1, mLon1,
//	            "起点", BNaviPoint.CoordinateType.GCJ02);
//	    BNaviPoint mEndPoint = new BNaviPoint(mLat2, mLon2,
//	            "终点", BNaviPoint.CoordinateType.GCJ02);
	    BaiduNaviManager.getInstance().launchNavigator(this,
	    		mLat1, mLon1, "起点",
	    		mLat2, mLon2, "终点",
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, 		 //算路方式
				true, 									   		 //真实导航
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
				new OnStartNavigationListener() {				 //跳转监听
					
					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent = new Intent(NaviDemo.this, BNavigatorActivity.class);
						intent.putExtras(configParams);
				        startActivity(intent);
					}
					
					@Override
					public void onJumpToDownloader() {
					}
				});
		}
   //生成二维码
   public void generateCode(View view) {
	   Intent intent = new Intent(NaviDemo.this, QRCodeView.class);
	   startActivity(intent);
	   
   }

   @Override
  	public void onBackPressed() {
  	    //实现Home键效果，需要添加权限:<uses-permission android:name="android.permission.RESTART_PACKAGES" />
  	    //super.onBackPressed();这句话一定要注掉,不然又去调用默认的back处理方式了
  	    Intent i= new Intent(Intent.ACTION_MAIN);
  	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  	    i.addCategory(Intent.CATEGORY_HOME);
  	    startActivity(i); 
  	}
//	public void startWebNavi(View view) {
//		int lat = (int) (mLat1 * 1E6);
//		int lon = (int) (mLon1 * 1E6);
//		GeoPoint pt1 = new GeoPoint(lat, lon);
//		lat = (int) (mLat2 * 1E6);
//		lon = (int) (mLon2 * 1E6);
//		GeoPoint pt2 = new GeoPoint(lat, lon);
//		// 构建 导航参数
//		NaviPara para = new NaviPara();
//		para.startPoint = pt1;
//		para.endPoint = pt2;
//		BaiduMapNavigation.openWebBaiduMapNavi(para, this);
//	}
	public static double getmLat1() {
		return mLat1;
	}
	public static void setmLat1(double mLat1) {
		NaviDemo.mLat1 = mLat1;
	}
	public static double getmLon1() {
		return mLon1;
	}
	public static void setmLon1(double mLon1) {
		NaviDemo.mLon1 = mLon1;
	}
	public static double getmLat2() {
		return mLat2;
	}
	public static void setmLat2(double mLat2) {
		NaviDemo.mLat2 = mLat2;
	}
	public static double getmLon2() {
		return mLon2;
	}
	public static void setmLon2(double mLon2) {
		NaviDemo.mLon2 = mLon2;
	}
}

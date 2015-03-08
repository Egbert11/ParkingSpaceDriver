package com.zhcs.parkingSpaceDao;

import java.util.List;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.zhcs.driverBean.ParkingSpaceInfo;
import com.zhcs.parklist.CarparkListActivity;
import com.zhcs.parklist.LocationService;
import com.zhcs.regAndLogin.R;

public class ChooseDisplayMode extends SlidingFragmentActivity{
	private Button map;
	private Button list;
	private CanvasTransformer mTransformer;
	private final static String TAG = "ChooseDisplayMode";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ȥ������
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_displaymode);
		setTitle("��λ����");
		
		initAnimation();
		initSlidingMenu();
		// �����Ͻ�ͼ�����߼���һ�����ص�ͼ�� 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		 // �����Ͻ�ͼ�����߼���һ�����ص�ͼ�� 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		map = (Button)findViewById(R.id.map);
		list = (Button)findViewById(R.id.list);
		//������λ��Ϣ
		Intent locationService = new Intent(this, LocationService.class);
		startService(locationService);
	}
	
	public void mapDisplay(View v){
		Intent intent = new Intent(ChooseDisplayMode.this, MapForFindParkingSpace.class);
		startActivity(intent);
	}
	
	public void listDisplay(View v){
		getSpaceInfo();
	}
	
	public void getSpaceInfo() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("SpaceInfo");
		query.whereEqualTo("state", 0); //0�ɶ��ģ� 1���Ĵ����У� 2�Ѷ���
		
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException e) {
				if (e == null) {
		            Log.v("��ȡ��λ����", "��ѯ��" + arg0.size() + " ����������������");
		            ParkingSpaceInfo.initializeList(arg0);
		            Intent intent = new Intent(ChooseDisplayMode.this, CarparkListActivity.class);
		    		startActivity(intent);
		        } else {
		            Log.v("��ȡ��λ����", "��ѯ����: " + e.getMessage());
		        }
			}
		});
	}
	
	/**
	 * ��ʼ�������˵�
	 */
	private void initSlidingMenu(){
		// ������������ͼ
		//getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SampleListFragment()).commit();
				
		// ���û����˵���ͼ
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new SampleListFragment()).commit();

		// ���û����˵�������ֵ
		SlidingMenu sm = getSlidingMenu();		
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.layout.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setBehindWidth(400);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(mTransformer);
		
		setSlidingActionBarEnabled(true);
	}

	/**
	 * ��ʼ������Ч��
	 */
	private void initAnimation(){
		mTransformer = new CanvasTransformer(){
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				canvas.scale(percentOpen, 1, 0, 0);				
			}
			
		};
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}

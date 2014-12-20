package com.zhcs.parkingSpaceDao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zhcs.driverBean.ParkingSpaceInfo;
import com.zhcs.parklist.CarparkListActivity;
import com.zhcs.parklist.LocationService;
import com.zhcs.regAndLogin.R;

public class ChooseDisplayMode extends Activity{
	private Button map;
	private Button list;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_displaymode);
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
		            Log.e("��ȡ��λ����", "��ѯ��" + arg0.size() + " ����������������");
		            ParkingSpaceInfo.initializeList(arg0);
		            Intent intent = new Intent(ChooseDisplayMode.this, CarparkListActivity.class);
		    		startActivity(intent);
		        } else {
		            Log.e("��ȡ��λ����", "��ѯ����: " + e.getMessage());
		        }
			}
		});
	}
}

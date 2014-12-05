package com.zhcs.parkingSpaceDao;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zhcs.regAndLogin.CarparkListActivity;
import com.zhcs.regAndLogin.R;

public class ChooseDisplayMode extends Activity{
	private Button map;
	private Button list;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//È¥³ý±êÌâ
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_displaymode);
		map = (Button)findViewById(R.id.map);
		list = (Button)findViewById(R.id.list);
	}
	
	public void mapDisplay(View v){
		Intent intent = new Intent(ChooseDisplayMode.this, MapForFindParkingSpace.class);
		startActivity(intent);
	}
	
	public void listDisplay(View v){
		Intent intent = new Intent(ChooseDisplayMode.this, CarparkListActivity.class);
		startActivity(intent);
	}
}

package com.zhcs.parkingSpaceDao;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.zhcs.regAndLogin.R;

public class SoftwareInformation extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//È¥³ý±êÌâ
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.softwareinfo);
	}
}

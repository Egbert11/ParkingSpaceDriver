package com.zhcs.parkingSpaceDao;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.widget.TextView;

import com.zhcs.regAndLogin.R;

public class TermOfServices extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//È¥³ý±êÌâ
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.termofservice);
		TextView textView = (TextView)findViewById(R.id.serviceView);   
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
}
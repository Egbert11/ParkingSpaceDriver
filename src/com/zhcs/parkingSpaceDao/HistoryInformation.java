package com.zhcs.parkingSpaceDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.zhcs.driverBean.SpaceHistoryBean;
import com.zhcs.regAndLogin.R;

public class HistoryInformation extends Activity{
	private TextView communityname, number, ownerphone, starttime, endtime, cost, fine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_history_information);
		communityname = (TextView) findViewById(R.id.community_name);
		number = (TextView) findViewById(R.id.space_number);
		ownerphone = (TextView) findViewById(R.id.owner_phone);
		starttime = (TextView) findViewById(R.id.start_time);
		endtime = (TextView) findViewById(R.id.end_time);
		cost = (TextView) findViewById(R.id.cost);
		fine = (TextView) findViewById(R.id.fine);
		
		Bundle data = this.getIntent().getExtras();
		int index = data.getInt("index");
		ArrayList<SpaceHistoryBean> list = GetHistoryInfo.getList();
		final SpaceHistoryBean bean = list.get(index);
		
		communityname.setText(bean.getCommunityname());
		number.setText(String.valueOf(bean.getNumber()));
		ownerphone.setText(String.valueOf(bean.getOwnerphone()));
		SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd ");
		SimpleDateFormat dateformat2=new SimpleDateFormat("HH:mm");
		String day = dateformat1.format(bean.getDate());
		String start=dateformat2.format(bean.getStart());
		starttime.setText(day + start);
		String end=dateformat2.format(bean.getEnd());
		endtime.setText(day + end);
		cost.setText(String.valueOf(bean.getCost())+"元");
		fine.setText(String.valueOf(bean.getFine())+"元");
	}
}

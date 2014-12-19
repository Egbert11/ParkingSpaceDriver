package com.zhcs.parklist;

import com.zhcs.driverBean.DriverInfo;
import com.zhcs.driverBean.ParkingSpaceInfo;
import com.zhcs.driverBean.SpaceInfoBean;
import com.zhcs.regAndLogin.R;
import com.zhcs.regAndLogin.R.id;
import com.zhcs.regAndLogin.R.layout;
import com.zhcs.ui.adapter.CarInfoAdapter;
import com.zhcs.ui.adapter.CarInfoAdapter.Order;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Spinner;

public class CarparkListActivity extends Activity {

	private Spinner mSpinner;
	private ListView mListView;
	private CarInfoAdapter mCarInfoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_showparkinglist);
		initView();
	}

	private void initView() {
		mCarInfoAdapter = new CarInfoAdapter(this, ParkingSpaceInfo.getList());
		mSpinner = (Spinner) findViewById(R.id.order_spinner);
		mListView = (ListView) findViewById(R.id.content_list);
		mListView.setAdapter(mCarInfoAdapter);
		mSpinner.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				if (mSpinner.getSelectedItem().toString().equals("Ê±¼ä"))
				if (position == 0)
					mCarInfoAdapter.switchOrder(Order.Date);
				else if (position == 1)
					mCarInfoAdapter.switchOrder(Order.Price);
				else
					mCarInfoAdapter.switchOrder(Order.Distance);
			}
 		});
	}
}

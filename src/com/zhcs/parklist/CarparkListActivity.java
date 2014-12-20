package com.zhcs.parklist;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zhcs.driverBean.ParkingSpaceInfo;
import com.zhcs.regAndLogin.R;
import com.zhcs.ui.adapter.CarInfoAdapter;
import com.zhcs.ui.adapter.CarInfoAdapter.Order;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
//				if (mSpinner.getSelectedItem().toString().equals("Ê±¼ä"))
				if (position == 0)
					mCarInfoAdapter.switchOrder(Order.Date);
				else if (position == 1)
					mCarInfoAdapter.switchOrder(Order.Price);
				else
					mCarInfoAdapter.switchOrder(Order.Distance);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
 		});
	}
}

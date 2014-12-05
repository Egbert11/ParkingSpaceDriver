package com.zhcs.ui.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zhcs.driverBean.SpaceInfoBean;
import com.zhcs.regAndLogin.R;

public class CarInfoAdapter extends BaseAdapter {

	private List<SpaceInfoBean> mSpaceInfoList;
	private LayoutInflater mInflater;

	public CarInfoAdapter(Context context, List<SpaceInfoBean> spaceInfoBeans) {
		mInflater = LayoutInflater.from(context);
		mSpaceInfoList = spaceInfoBeans;
	}

	private Comparator<SpaceInfoBean> mDistanceComp = new Comparator<SpaceInfoBean>() {
		@Override
		public int compare(SpaceInfoBean lhs, SpaceInfoBean rhs) {
			return 0;
		}
	};

	private Comparator<SpaceInfoBean> mDateComp = new Comparator<SpaceInfoBean>() {
		@Override
		public int compare(SpaceInfoBean lhs, SpaceInfoBean rhs) {
			return lhs.getStart().compareTo(rhs.getStart());
		}
	};

	private Comparator<SpaceInfoBean> mPriceComp = new Comparator<SpaceInfoBean>() {
		@Override
		public int compare(SpaceInfoBean lhs, SpaceInfoBean rhs) {
			return lhs.getPrice() - rhs.getPrice();
		}
	};

	public static enum Order {
		Date, Price, Distance
	}

	public void switchOrder(Order mOrder) {
		Comparator<SpaceInfoBean> comparator = null;
		switch (mOrder) {
		case Date:
			comparator = mDateComp;
			break;
		case Price:
			comparator = mPriceComp;
			break;
		case Distance:
			comparator = mDistanceComp;
			break;
		default:
			break;
		}
		Collections.sort(mSpaceInfoList, comparator);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mSpaceInfoList.size();
	}

	@Override
	public SpaceInfoBean getItem(int position) {
		return mSpaceInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listitem_carinfo, parent,
					false);
		}
		TextView mCarDate = (TextView) convertView.findViewById(R.id.car_date);
		TextView mCarPrice = (TextView) convertView
				.findViewById(R.id.car_price);
		TextView mCarDistance = (TextView) convertView
				.findViewById(R.id.car_distance);
		Button mSubscripe = (Button) convertView.findViewById(R.id.subscribe);

		mCarDate.setText(getDate(position));
		mCarPrice.setText(getItem(position).getPrice());
		mCarDistance.setText(getDistance(position));
		mSubscripe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		return convertView;
	}

	private CharSequence getDate(int position) {
		return null;
	}

	private CharSequence getDistance(int position) {
		return null;
	}

}

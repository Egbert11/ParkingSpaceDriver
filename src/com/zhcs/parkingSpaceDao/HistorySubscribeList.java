package com.zhcs.parkingSpaceDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.zhcs.driverBean.DriverInfo;
import com.zhcs.driverBean.SpaceHistoryBean;
import com.zhcs.regAndLogin.R;

public class HistorySubscribeList extends SlidingFragmentActivity{

	private ListView mListView;
	private CanvasTransformer mTransformer;
	private ProgressDialog proDialog;
	private final static String TAG = "HistorySubscribeList";
	private static ArrayList<SpaceHistoryBean> list = new ArrayList<SpaceHistoryBean>();;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.property_history_list);
		setTitle("历史订单");
		
		initAnimation();
		initSlidingMenu();
		// 给左上角图标的左边加上一个返回的图标 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		 // 给左上角图标的左边加上一个返回的图标 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		proDialog = new ProgressDialog(HistorySubscribeList.this);
		proDialog.setMessage("正在获取数据，请稍后...");
		proDialog.show();
		
		//获取历史订单
		this.getSpaceInfo();
	}
	
	
	private class SpaceListAdapter extends BaseAdapter{
		public SpaceListAdapter(){
			super();
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(HistorySubscribeList.this, R.layout.historyinfo_item, null);
			TextView number = (TextView)convertView.findViewById(R.id.number);
			TextView starttime = (TextView)convertView.findViewById(R.id.start_time_item);
			TextView endtime = (TextView)convertView.findViewById(R.id.end_time_item);
//			Log.e("HistorySubscribeList","日期："+list.get(position).getDate());
			number.setText("车位" + list.get(position).getNumber());
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String start = sdf.format(list.get(position).getStart());
			starttime.setText(start);
			String end = sdf.format(list.get(position).getEnd());
			endtime.setText(end);
			return convertView;
		}
		
	}
	
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x00){
				proDialog.dismiss();
				Log.e("HistorySubscribeList", "成功获取数据");
				GetHistoryInfo.setList(list);
				
				mListView = (ListView)findViewById(R.id.listView1);
				//将返回列表按照车位号进行排序
				Comparator<SpaceHistoryBean> comparator = new Comparator<SpaceHistoryBean>(){
					   public int compare(SpaceHistoryBean s1, SpaceHistoryBean s2) {
						   return s1.getDate().compareTo(s2.getDate());
					   }
				};
				Collections.sort(list,comparator);
				Log.e("size",String.valueOf(list.size()));
				mListView.setAdapter(new SpaceListAdapter());
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(HistorySubscribeList.this, HistoryInformation.class);
						Bundle bun = new Bundle();
						bun.putInt("index", position);
						intent.putExtras(bun);
						startActivity(intent);
					}
				});
			}else{
				proDialog.dismiss();
			}
		}
	};
	
	/**
	 * 获取历史订单
	 */
	public void getSpaceInfo() {
		if(!list.isEmpty())
			list.clear();
		String driverid = DriverInfo.getId();
		Log.e(TAG, driverid);
		AVQuery<AVObject> query = new AVQuery<AVObject>("BookSpaceLog");
		query.whereEqualTo("driverid", driverid);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException e) {
				if (e == null) {
					final int size = arg0.size();
					Log.e(TAG, ""+size);
					for(int i = 0; i < size; i++) {
						final int j = i;
						AVObject obj = arg0.get(i);
						final SpaceHistoryBean bean = new SpaceHistoryBean();
						//设置车位号
						bean.setNumber(obj.getNumber("number").intValue());
						//设置成交价格
						bean.setCost(obj.getNumber("cost").intValue());
						bean.setFine(obj.getNumber("fine").intValue());
						//设置时间
//						Log.e(TAG, "1");
						bean.setDate(obj.getCreatedAt());
						bean.setStart(obj.getDate("start"));
						bean.setEnd(obj.getDate("end"));
						final String communityid = obj.getString("communityid");
						final String spaceid = obj.getString("spaceid");
						final String driverid = obj.getString("driverid");
						AVQuery<AVObject> query01 = new AVQuery<AVObject>("Community");
						query01.whereEqualTo("objectId", communityid);
						query01.findInBackground(new FindCallback<AVObject>() {
							@Override
							public void done(List<AVObject> arg0, AVException e) {
								if (e == null) {
									AVObject obj = arg0.get(0);
//									Log.e(TAG, "2");
									String address = obj.getString("address");
									bean.setCommunityname(address);
									
									AVQuery<AVObject> query02 = new AVQuery<AVObject>("SpaceInfo");
									query02.whereEqualTo("objectId", spaceid);
									query02.findInBackground(new FindCallback<AVObject>() {
										@Override
										public void done(List<AVObject> arg0, AVException e) {
											if (e == null) {
													AVObject space = arg0.get(0);
//													Log.e(TAG, "3");
													final String ownerid = space.getString("ownerid");
													AVQuery<AVObject> query03 = new AVQuery<AVObject>("OwnerInfo");
													query03.whereEqualTo("objectId", ownerid);
													query03.findInBackground(new FindCallback<AVObject>() {
													@Override
													public void done(List<AVObject> arg0, AVException e) {
														if (e == null) {
																AVObject owner = arg0.get(0);
//																Log.e(TAG, "4");
																bean.setOwnerphone(owner.getString("phone"));
																//查询车主的手机号
																AVQuery<AVObject> dealCount = new AVQuery<AVObject>("DriverInfo");
																dealCount.whereEqualTo("objectId", driverid);
																dealCount.findInBackground(new FindCallback<AVObject>() {
																	@Override
																	public void done(List<AVObject> arg0, AVException e) {
																		if (e == null) {
																			AVObject driver = arg0.get(0);
//																			Log.e(TAG, "5");
																			bean.setDriverphone(driver.getString("phone"));
																			list.add(bean);
																			if(list.size() == size) {
																				Message msg = new Message();
																				msg.what = 0x00;
																				handler.sendMessage(msg);
																			}
																			
																		} else {
																			
																		}
																	}
																});
														} else {
															
														}
													}
												});
											}else{
												
											}
										}
									});
								}else {
									
								}
							}
						});
					}
					
				} else {
					
				}
			}
		});
		
	}
	
	
	
	/**
	 * 初始化滑动菜单
	 */
	private void initSlidingMenu(){
		// 设置主界面视图
		//getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SampleListFragment()).commit();
				
		// 设置滑动菜单视图
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new SampleListFragment()).commit();

		// 设置滑动菜单的属性值
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
	 * 初始化动画效果
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




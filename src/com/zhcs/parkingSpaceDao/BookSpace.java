package com.zhcs.parkingSpaceDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.zhcs.driverBean.BookSpaceInfo;
import com.zhcs.driverBean.DriverInfo;
import com.zhcs.regAndLogin.R;

/*
 * 预订车位页面
 */
public class BookSpace extends Activity implements View.OnTouchListener{
	private EditText start;
	private EditText end;
	private TextView illustrate;
	private Button book;
	private Button returnMap;
	private Calendar now  = Calendar.getInstance();
	private Calendar startTime = Calendar.getInstance();
	private Calendar endTime = Calendar.getInstance();
	//车主订阅车位所设定的时间
	private Calendar startDate = Calendar.getInstance();
	private Calendar endDate = Calendar.getInstance();
	private String address = "";
	private Date myDate = new Date();
	private String logObjectId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bookspace);
		start=(EditText)findViewById(R.id.startTime);
		end=(EditText)findViewById(R.id.endTime);
		illustrate=(TextView)findViewById(R.id.illustrate);
		book=(Button)findViewById(R.id.book);
		returnMap=(Button)findViewById(R.id.returnMap);
		
		//设置监听事件
		start.setOnTouchListener(this); 
        end.setOnTouchListener(this); 
		
		startTime.setTime(BookSpaceInfo.getSpace().getStart());
		endTime.setTime(BookSpaceInfo.getSpace().getEnd());
		
		setYearMonthDayMillisecondToZero(now);
		setYearMonthDayMillisecondToZero(startTime);
		setYearMonthDayMillisecondToZero(endTime);
		// 如果当前时间位于车位开始订阅时间之后，则将开始订阅时间设置为当前时间
		if(now.getTime().after(startTime.getTime()))
			startTime = now;
		//获取车位的小区地址并显示
		getAddress(BookSpaceInfo.getSpace().getCommunityid());
		
		//点击支付订单的监听事件
		book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isValid())
					return;
				//计算租金
				int rentMoney = calculateRentMoney();
				new AlertDialog.Builder(BookSpace.this).setTitle("确定支付").setMessage("你确定要订阅该车位吗？点击“支付”后"+
					 rentMoney +"元将从您的账号中先行收取")
					.setPositiveButton("支付", new DialogInterface.OnClickListener() {
						
					@Override
					public void onClick(DialogInterface dialog, int which) {
						bookSpaceDao();
					};
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							
						}
							}).show();
				//Toast.makeText(BookSpace.this, "你需要支付的金钱为:"+BookSpaceInfo.getSpace().getPrice() * (endHour - startHour)+"元", Toast.LENGTH_LONG).show();
			}
		});
		
		returnMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 将车位状态设为0
				resetState();
			}
		});
	}
	
	/**
	 * 将Calendar的年月日秒均设置为0
	 */
	private void setYearMonthDayMillisecondToZero(Calendar c){
		c.set(0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);
	}
	
	/**
	 * 计算租金
	 */
	private int calculateRentMoney() {
		int unitPrice = BookSpaceInfo.getSpace().getPrice();
		int hour = endDate.get(Calendar.HOUR_OF_DAY) - startDate.get(Calendar.HOUR_OF_DAY);
		int minute = endDate.get(Calendar.MINUTE) - startDate.get(Calendar.MINUTE);
		int allMinutes = hour * 60 + minute;
		int hours = allMinutes / 60;
		int minutes = allMinutes % 60;
		float addHour = (minutes >= 30) ? 1.0f : 0.5f;
		Log.e("hours:", String.valueOf(hours));
		Log.e("minutes:", String.valueOf(minutes));
		Log.e("addHour", String.valueOf(addHour));
		Log.e("unitPrice", String.valueOf(unitPrice));
		return (int)((float)(hours + addHour) * unitPrice);
	}
	
	/**
	 * 获取地址信息
	 * @param objectid 小区的id
	 */
	private void getAddress(String objectid) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Community");
		query.whereEqualTo("objectId", objectid);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            AVObject obj = avObjects.get(0);
		            address = obj.getString("address");
		            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
					String start = sdf.format(startTime.getTime());
					String end = sdf.format(endTime.getTime());
					
		            StringBuffer sb = new StringBuffer();
		            sb.append("该车位的地址为：");
		            sb.append(address);
		            sb.append("。\n价格为每小时");
		            sb.append(BookSpaceInfo.getSpace().getPrice());
		            sb.append("元。\n当前可预订的时间段为");
		    		sb.append(start);
		    		sb.append("--");
		    		sb.append(end);
		    		sb.append("。请准确把握你的订阅时间，不要超时哦！");
		    		illustrate.setText(sb.toString());
		            } else {
		            }
		        }
			});
	}
	
	/**
	 * 车位开始时间与结束时间输入框点击监听
	 */
	@Override 
    public boolean onTouch(View v, MotionEvent event) { 
        if (event.getAction() == MotionEvent.ACTION_DOWN) { 
   
            AlertDialog.Builder builder = new AlertDialog.Builder(this); 
            View view = View.inflate(this, R.layout.activity_timedialog, null); 
            final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker); 
            builder.setView(view); 
   
            Calendar cal = Calendar.getInstance(); 
            cal.setTimeInMillis(System.currentTimeMillis());  
   
            timePicker.setIs24HourView(true); 
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)); 
            timePicker.setCurrentMinute(Calendar.MINUTE); 
   
            if (v.getId() == R.id.startTime) { 
                final int inType = start.getInputType(); 
                start.setInputType(InputType.TYPE_NULL); 
                start.onTouchEvent(event); 
                start.setInputType(inType); 
                start.setSelection(start.getText().length()); 
                   
                builder.setTitle("选取开始时间"); 
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() { 
   
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
   
                        StringBuffer sb = new StringBuffer(); 
                        if(timePicker.getCurrentHour() < 10)
                        	sb.append(0);
                        sb.append(timePicker.getCurrentHour()).append(":");
                        if(timePicker.getCurrentMinute() < 10)
                        	sb.append(0);
                        sb.append(timePicker.getCurrentMinute()); 
                        start.setText(sb); 
                        
                        startDate.set(0, 0, 0, timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        end.requestFocus(); 
                           
                        dialog.cancel(); 
                    } 
                }); 
                   
            } else if (v.getId() == R.id.endTime) { 
                int inType = end.getInputType(); 
                end.setInputType(InputType.TYPE_NULL);     
                end.onTouchEvent(event); 
                end.setInputType(inType); 
                end.setSelection(end.getText().length()); 
   
                builder.setTitle("选取结束时间"); 
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() { 
   
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
   
                        StringBuffer sb = new StringBuffer(); 
//                        sb.append(String.format("%d-%02d-%02d",  
//                                datePicker.getYear(),  
//                                datePicker.getMonth() + 1,  
//                                datePicker.getDayOfMonth())); 
//                        sb.append("  "); 
                        if(timePicker.getCurrentHour() < 10)
                        	sb.append(0);
                        sb.append(timePicker.getCurrentHour()).append(":");
                        if(timePicker.getCurrentMinute() < 10)
                        	sb.append(0);
                        sb.append(timePicker.getCurrentMinute()); 
                        end.setText(sb); 
                        
                        endDate.set(0, 0, 0, timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        dialog.cancel(); 
                    } 
                }); 
            } 
               
            Dialog dialog = builder.create(); 
            dialog.show(); 
        } 
   
        return true; 
    } 
	
	//更新车位订阅信息
	private void bookSpaceDao() {
		final int cost = calculateRentMoney();
		
		AVQuery<AVObject> query = new AVQuery<AVObject>("DriverInfo");
		Log.d("objectid", DriverInfo.getId());
		
		query.whereEqualTo("objectId", DriverInfo.getId());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("修改成功", "查询到" + avObjects.size() + " 条符合条件的数据");
		            AVObject mod = avObjects.get(0);
		            if(mod.getInt("money") < cost) {
		            	Toast.makeText(BookSpace.this, "你的余额不够，车位订阅失败", Toast.LENGTH_SHORT).show();
		            } else {
		            //扣除车主的钱
		            mod.increment("money", -cost);
					mod.saveInBackground();
					AVQuery<AVObject> owner = new AVQuery<AVObject>("OwnerInfo");
					owner.whereEqualTo("objectId", BookSpaceInfo.getSpace().getOwnerid());
					//增加车位主的钱
					owner.findInBackground(new FindCallback<AVObject>() {
						@Override
					    public void done(List<AVObject> avObjects, AVException e) {
					        if (e == null) {
					        	AVObject modCost = avObjects.get(0);
					        	modCost.increment("money", cost);
					        	modCost.saveInBackground();
					        } else {
					        	Log.d("OwnerInfo", "添加money失败"+e.getMessage());
					        }
						}
					});
					//将车位状态设为2
					AVQuery<AVObject> space = new AVQuery<AVObject>("SpaceInfo");
					space.whereEqualTo("objectId", BookSpaceInfo.getSpace().getSpaceid());
					space.findInBackground(new FindCallback<AVObject>() {
						@Override
					    public void done(List<AVObject> avObjects, AVException e) {
					        if (e == null) {
					        	AVObject modState = avObjects.get(0);
					        	modState.put("state", 2);
					        	modState.saveInBackground();
					        } else {
					        	Log.d("SpaceInfo", "更改状态失败"+e.getMessage());
					        }
						}
					});
					//添加订阅日志
					
					AVObject reg = new AVObject("BookSpaceLog");
					reg.put("driverid", DriverInfo.getId());
					reg.put("communityid", BookSpaceInfo.getSpace().getCommunityid());
					reg.put("spaceid", BookSpaceInfo.getSpace().getSpaceid());
					reg.put("datetime", myDate);
					reg.put("number", BookSpaceInfo.getSpace().getNum());
					reg.put("start", BookSpaceInfo.getStart());
					reg.put("end", BookSpaceInfo.getEnd());
					reg.put("cost", cost);
					reg.put("fine", 0);
					reg.put("state", 1);// 1已订阅， 2已经进入小区， 3已经离开小区
					reg.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								Toast.makeText(BookSpace.this,"支付成功", Toast.LENGTH_SHORT).show();
								
								//设置终点的经纬度
								NaviDemo.setmLat2((double)BookSpaceInfo.getSpace().getLat()/1000000);
								NaviDemo.setmLon2((double)BookSpaceInfo.getSpace().getLng()/1000000);
								//推送消息给车位所有者
								pushMessage();
								
								
					        } else {
					        	Toast.makeText(BookSpace.this,"支付失败", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }
		        }else {
		        	Log.d("失败", "错误信息"+e.getMessage());
		        }
			}
		});
	}
	
	private void pushMessage() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("OwnerInfo");
		query.whereEqualTo("objectId", BookSpaceInfo.getSpace().getOwnerid());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		        	AVObject obj = avObjects.get(0);
		        	String installationId = obj.getString("installationid");
		        	Log.e("installationid", installationId);
		        	AVPush push = new AVPush();
		            // 设置频道
		            push.setChannel("public");
		            // 设置消息
		            String msg = ""+BookSpaceInfo.getSpace().getNum()+"号车位被订阅，时间为"+
		    				BookSpaceInfo.getStart()+":00--"+BookSpaceInfo.getEnd()+":00";
		            push.setMessage(msg);
		            
		            push.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",installationId));
		            // 推送
		            push.sendInBackground(new SendCallback() {
		              @Override
		              public void done(AVException e) {
		                if (e == null) {
		                  Toast.makeText(BookSpace.this, "Send successfully.", Toast.LENGTH_SHORT).show();
		                  getLogObjectId();
		                } else {
		                  Toast.makeText(BookSpace.this, "Send fails with :" + e.getMessage(), Toast.LENGTH_LONG).show();
		                }
		              }
		            });
		            
		            AVPush end = new AVPush();
		            end.setChannel("public");
		            end.setMessage("你的停车位快到时了哦~~");
		            Date myDate = new Date();
		            Calendar c2 = Calendar.getInstance();
		            c2.set(Calendar.HOUR_OF_DAY, endDate.get(Calendar.HOUR_OF_DAY));
		            c2.set(Calendar.MINUTE, endDate.get(Calendar.MINUTE));
		            //将推送的时间点设置为订单结束时间前20分钟
		            c2.add(Calendar.MINUTE, -20);
		            myDate = c2.getTime();
		            Log.e("myDate", myDate.toString());
		            end.setPushDate(myDate);
		            end.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",AVInstallation.getCurrentInstallation().getInstallationId()));
		            end.sendInBackground();
		        }
		          }
			});
	}
	
	private void getLogObjectId() {
		//获取此次订单的objectId
		AVQuery<AVObject> space = new AVQuery<AVObject>("BookSpaceLog");
		space.whereEqualTo("spaceid", BookSpaceInfo.getSpace().getSpaceid());
		space.whereEqualTo("datetime", myDate);
		space.whereEqualTo("start", BookSpaceInfo.getStart());
		space.whereEqualTo("end", BookSpaceInfo.getEnd());
		space.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		        	AVObject modState = avObjects.get(0);
		        	logObjectId = modState.getObjectId();
		        	BookSpaceInfo.setLogObjectId(logObjectId);
					Intent intent = new Intent(BookSpace.this, NaviDemo.class);
					startActivity(intent);
					finish();
		        } else {
		        	Log.d("SpaceInfo", "更改状态失败"+e.getMessage());
		        }
			}
		});
	}
	//将该车位的state重置为0
	private void resetState() {
		AVQuery<AVObject> space = new AVQuery<AVObject>("SpaceInfo");
		space.whereEqualTo("objectId", BookSpaceInfo.getSpace().getSpaceid());
		space.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		        	AVObject modState = avObjects.get(0);
		        	modState.put("state", 0);
		        	modState.saveInBackground();
		        	Intent intent = new Intent(BookSpace.this, MapForFindParkingSpace.class);
					startActivity(intent);
					finish();
		        } else {
		        	Log.d("SpaceInfo", "更改状态失败"+e.getMessage());
		        }
			}
		});
	}
	
	//判断输入数据是否合法
	private boolean isValid(){
		setYearMonthDayMillisecondToZero(startDate);
		setYearMonthDayMillisecondToZero(endDate);
		setYearMonthDayMillisecondToZero(startTime);
		setYearMonthDayMillisecondToZero(endTime);
		Log.e("start", start.getText().toString());
		Log.e("end", end.getText().toString());
		if(start.getText().toString().trim().equals(""))
		{
			start.setError(Html.fromHtml("<font color=#808183>"
                    + "开始时间不符合要求"+ "</font>"));
			return false;
		}
		else if(end.getText().toString().trim().equals(""))
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "结束时间不符合要求"+ "</font>"));
			return false;
		}
		else if(startDate.getTime().after(endDate.getTime()))
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "结束时间必须大于开始时间"+ "</font>"));
			return false;
		}
		else if(startDate.getTime().before(startTime.getTime()))
		{
			start.setError(Html.fromHtml("<font color=#808183>"
                    + "预订开始时间不能早于该车位的开放时间"+ "</font>"));
			return false;
		}
		else if(endDate.getTime().after(endTime.getTime()))
		{
//			Log.e("endDate:", endDate.getTime().toString());
//			Log.e("endTime:", endTime.getTime().toString());
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "预订结束时间不能超过该车位的结束时间"+ "</font>"));
			return false;
		}
		else
		{
			//设置订阅车位的时间段
			BookSpaceInfo.setStart(startDate.getTime());
			BookSpaceInfo.setEnd(endDate.getTime());
			return true;
		}
	}

}

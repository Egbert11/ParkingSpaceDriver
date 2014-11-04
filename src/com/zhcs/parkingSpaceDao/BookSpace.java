package com.zhcs.parkingSpaceDao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
public class BookSpace extends Activity{
	private EditText start;
	private EditText end;
	private TextView illustrate;
	private Button book;
	private Button returnMap;
	private Calendar now  = Calendar.getInstance();
	//当前小时数
	private int curHour = now.get(Calendar.HOUR_OF_DAY);
	//当前小时数和车位开放开始时间比较
	private int max = curHour;
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
		
		if(max < BookSpaceInfo.getSpace().getStart())
			max = BookSpaceInfo.getSpace().getStart();
		//获取车位的小区地址
		getAddress(BookSpaceInfo.getSpace().getCommunityid());
		illustrate.setText("该车位的地址为："+address+"。价格为每小时"+BookSpaceInfo.getSpace().getPrice()+"元，当前可预订的时间段为"+max+":00--"+
				BookSpaceInfo.getSpace().getEnd()+":00。请准确把握你的订阅时间，不要超时哦！");
		
		//点击支付订单的监听事件
		book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isValid())
					return;
				new AlertDialog.Builder(BookSpace.this).setTitle("确定支付").setMessage("你确定要订阅该车位吗？点击“支付”后"+
					BookSpaceInfo.getSpace().getPrice() * (BookSpaceInfo.getEnd() - BookSpaceInfo.getStart())+"元将从您的账号中先行收取")
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
	
	private void getAddress(String objectid) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Community");
		query.whereEqualTo("objectId", objectid);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            AVObject obj = avObjects.get(0);
		            address = obj.getString("address");
		            illustrate.setText("该车位的地址为："+address+"。价格为每小时"+BookSpaceInfo.getSpace().getPrice()+"元，当前可预订的时间段为"+max+":00--"+
		    				BookSpaceInfo.getSpace().getEnd()+":00。请准确把握你的订阅时间，不要超时哦！");
		            } else {
		            }
		        }
			});
	}
	//更新车位订阅信息
	private void bookSpaceDao() {
		int startHour = BookSpaceInfo.getStart();
		int endHour = BookSpaceInfo.getEnd();
		int last = endHour - startHour;
		final int cost = BookSpaceInfo.getSpace().getPrice() * last;
		
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
//		            JSONObject json = new JSONObject();
//		            try {
//		            	json.put("alert", msg);
//						json.put("msg", msg);
//						json.put("action", "com.zhcs.driver");
//					} catch (JSONException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//		            push.setData(json);
		            // 设置查询条件，只推送给自己，不要打扰别人啦，这是 demo
//		            push.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",
//		                AVInstallation.getCurrentInstallation().getInstallationId()));
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
	                int year = c2.get(Calendar.YEAR);
	                int month = c2.get(Calendar.MONTH) + 1;
	                int date = c2.get(Calendar.DATE);
		            c2.set(year, month - 1, date, BookSpaceInfo.getEnd() - 1, 40);
		            myDate = c2.getTime();
		            Log.e("myDate", myDate.toString());
		            end.setPushDate(myDate);
		            end.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",AVInstallation.getCurrentInstallation().getInstallationId()));
		            end.sendInBackground();
		        }
		          }
		        });
//		        	AVQuery pushQuery = AVInstallation.getQuery();
//		    		pushQuery.whereEqualTo("installationId", installationId);
//		    		String msg = "一位用户订阅了你的"+BookSpaceInfo.getSpace().getNum()+"号车位，订阅时间为"+
//		    				BookSpaceInfo.getSpace().getStart()+":00--"+BookSpaceInfo.getSpace().getEnd()+":00";
//		    		AVPush.sendMessageInBackground(msg, pushQuery, new SendCallback() {
//		    		    @Override
//		    		    public void done(AVException e) {
//		    		    	if(e == null) {
//			    		    	Toast.makeText(BookSpace.this, "消息已发送", Toast.LENGTH_SHORT).show();
//			    		    	//跳转到导航页面
//								getLogObjectId();
//		    		    	} else {
//		    		    		Log.e("推送失败", e.getMessage());
//		    		    	}
//		    		    }
//		    		});
//		        } else {
//		        	Log.d("SpaceInfo", "更改状态失败"+e.getMessage());
//		        }
//			}
//		});
		
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
		if(start.getText().toString().trim().equals("") || Integer.parseInt(start.getText().toString()) >= 24 || Integer.parseInt(start.getText().toString()) < max)
		{
			start.setError(Html.fromHtml("<font color=#808183>"
                    + "开始时间不符合要求"+ "</font>"));
			return false;
		}
		else if(end.getText().toString().trim().equals("") || Integer.parseInt(end.getText().toString()) > 24)
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "结束时间不符合要求"+ "</font>"));
			return false;
		}
		else if(Integer.parseInt(start.getText().toString()) >= Integer.parseInt(end.getText().toString()))
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "结束时间必须大于开始时间"+ "</font>"));
			return false;
		}
		else if(Integer.parseInt(end.getText().toString()) > BookSpaceInfo.getSpace().getEnd())
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "预订结束时间不能超过该车位的开放结束时间"+ "</font>"));
			return false;
		}
		else
		{
			//设置订阅车位的时间段
			BookSpaceInfo.setStart(Integer.parseInt(start.getText().toString()));
			BookSpaceInfo.setEnd(Integer.parseInt(end.getText().toString()));
			return true;
		}
	}

}

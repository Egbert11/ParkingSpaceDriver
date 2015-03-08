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
 * Ԥ����λҳ��
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
	//�������ĳ�λ���趨��ʱ��
	private Calendar startDate = Calendar.getInstance();
	private Calendar endDate = Calendar.getInstance();
	private String address = "";
	private Date myDate = new Date();
	private String logObjectId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bookspace);
		start=(EditText)findViewById(R.id.startTime);
		end=(EditText)findViewById(R.id.endTime);
		illustrate=(TextView)findViewById(R.id.illustrate);
		book=(Button)findViewById(R.id.book);
		returnMap=(Button)findViewById(R.id.returnMap);
		
		//���ü����¼�
		start.setOnTouchListener(this); 
        end.setOnTouchListener(this); 
		
		startTime.setTime(BookSpaceInfo.getSpace().getStart());
		endTime.setTime(BookSpaceInfo.getSpace().getEnd());
		
		setYearMonthDayMillisecondToZero(now);
		setYearMonthDayMillisecondToZero(startTime);
		setYearMonthDayMillisecondToZero(endTime);
		// �����ǰʱ��λ�ڳ�λ��ʼ����ʱ��֮���򽫿�ʼ����ʱ������Ϊ��ǰʱ��
		if(now.getTime().after(startTime.getTime()))
			startTime = now;
		//��ȡ��λ��С����ַ����ʾ
		getAddress(BookSpaceInfo.getSpace().getCommunityid());
		
		//���֧�������ļ����¼�
		book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isValid())
					return;
				//�������
				int rentMoney = calculateRentMoney();
				new AlertDialog.Builder(BookSpace.this).setTitle("ȷ��֧��").setMessage("��ȷ��Ҫ���ĸó�λ�𣿵����֧������"+
					 rentMoney +"Ԫ���������˺���������ȡ")
					.setPositiveButton("֧��", new DialogInterface.OnClickListener() {
						
					@Override
					public void onClick(DialogInterface dialog, int which) {
						bookSpaceDao();
					};
					}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							
						}
							}).show();
				//Toast.makeText(BookSpace.this, "����Ҫ֧���Ľ�ǮΪ:"+BookSpaceInfo.getSpace().getPrice() * (endHour - startHour)+"Ԫ", Toast.LENGTH_LONG).show();
			}
		});
		
		returnMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// ����λ״̬��Ϊ0
				resetState();
			}
		});
	}
	
	/**
	 * ��Calendar���������������Ϊ0
	 */
	private void setYearMonthDayMillisecondToZero(Calendar c){
		c.set(0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);
	}
	
	/**
	 * �������
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
	 * ��ȡ��ַ��Ϣ
	 * @param objectid С����id
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
		            sb.append("�ó�λ�ĵ�ַΪ��");
		            sb.append(address);
		            sb.append("��\n�۸�ΪÿСʱ");
		            sb.append(BookSpaceInfo.getSpace().getPrice());
		            sb.append("Ԫ��\n��ǰ��Ԥ����ʱ���Ϊ");
		    		sb.append(start);
		    		sb.append("--");
		    		sb.append(end);
		    		sb.append("����׼ȷ������Ķ���ʱ�䣬��Ҫ��ʱŶ��");
		    		illustrate.setText(sb.toString());
		            } else {
		            }
		        }
			});
	}
	
	/**
	 * ��λ��ʼʱ�������ʱ�������������
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
                   
                builder.setTitle("ѡȡ��ʼʱ��"); 
                builder.setPositiveButton("ȷ  ��", new DialogInterface.OnClickListener() { 
   
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
   
                builder.setTitle("ѡȡ����ʱ��"); 
                builder.setPositiveButton("ȷ  ��", new DialogInterface.OnClickListener() { 
   
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
	
	//���³�λ������Ϣ
	private void bookSpaceDao() {
		final int cost = calculateRentMoney();
		
		AVQuery<AVObject> query = new AVQuery<AVObject>("DriverInfo");
		Log.d("objectid", DriverInfo.getId());
		
		query.whereEqualTo("objectId", DriverInfo.getId());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("�޸ĳɹ�", "��ѯ��" + avObjects.size() + " ����������������");
		            AVObject mod = avObjects.get(0);
		            if(mod.getInt("money") < cost) {
		            	Toast.makeText(BookSpace.this, "�����������λ����ʧ��", Toast.LENGTH_SHORT).show();
		            } else {
		            //�۳�������Ǯ
		            mod.increment("money", -cost);
					mod.saveInBackground();
					AVQuery<AVObject> owner = new AVQuery<AVObject>("OwnerInfo");
					owner.whereEqualTo("objectId", BookSpaceInfo.getSpace().getOwnerid());
					//���ӳ�λ����Ǯ
					owner.findInBackground(new FindCallback<AVObject>() {
						@Override
					    public void done(List<AVObject> avObjects, AVException e) {
					        if (e == null) {
					        	AVObject modCost = avObjects.get(0);
					        	modCost.increment("money", cost);
					        	modCost.saveInBackground();
					        } else {
					        	Log.d("OwnerInfo", "���moneyʧ��"+e.getMessage());
					        }
						}
					});
					//����λ״̬��Ϊ2
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
					        	Log.d("SpaceInfo", "����״̬ʧ��"+e.getMessage());
					        }
						}
					});
					//��Ӷ�����־
					
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
					reg.put("state", 1);// 1�Ѷ��ģ� 2�Ѿ�����С���� 3�Ѿ��뿪С��
					reg.saveInBackground(new SaveCallback() {
						public void done(AVException arg0) {
							if (arg0 == null) {
								Toast.makeText(BookSpace.this,"֧���ɹ�", Toast.LENGTH_SHORT).show();
								
								//�����յ�ľ�γ��
								NaviDemo.setmLat2((double)BookSpaceInfo.getSpace().getLat()/1000000);
								NaviDemo.setmLon2((double)BookSpaceInfo.getSpace().getLng()/1000000);
								//������Ϣ����λ������
								pushMessage();
								
								
					        } else {
					        	Toast.makeText(BookSpace.this,"֧��ʧ��", Toast.LENGTH_SHORT).show();
						}
					}
		        });
		        }
		        }else {
		        	Log.d("ʧ��", "������Ϣ"+e.getMessage());
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
		            // ����Ƶ��
		            push.setChannel("public");
		            // ������Ϣ
		            String msg = ""+BookSpaceInfo.getSpace().getNum()+"�ų�λ�����ģ�ʱ��Ϊ"+
		    				BookSpaceInfo.getStart()+":00--"+BookSpaceInfo.getEnd()+":00";
		            push.setMessage(msg);
		            
		            push.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",installationId));
		            // ����
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
		            end.setMessage("���ͣ��λ�쵽ʱ��Ŷ~~");
		            Date myDate = new Date();
		            Calendar c2 = Calendar.getInstance();
		            c2.set(Calendar.HOUR_OF_DAY, endDate.get(Calendar.HOUR_OF_DAY));
		            c2.set(Calendar.MINUTE, endDate.get(Calendar.MINUTE));
		            //�����͵�ʱ�������Ϊ��������ʱ��ǰ20����
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
		//��ȡ�˴ζ�����objectId
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
		        	Log.d("SpaceInfo", "����״̬ʧ��"+e.getMessage());
		        }
			}
		});
	}
	//���ó�λ��state����Ϊ0
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
		        	Log.d("SpaceInfo", "����״̬ʧ��"+e.getMessage());
		        }
			}
		});
	}
	
	//�ж����������Ƿ�Ϸ�
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
                    + "��ʼʱ�䲻����Ҫ��"+ "</font>"));
			return false;
		}
		else if(end.getText().toString().trim().equals(""))
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "����ʱ�䲻����Ҫ��"+ "</font>"));
			return false;
		}
		else if(startDate.getTime().after(endDate.getTime()))
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "����ʱ�������ڿ�ʼʱ��"+ "</font>"));
			return false;
		}
		else if(startDate.getTime().before(startTime.getTime()))
		{
			start.setError(Html.fromHtml("<font color=#808183>"
                    + "Ԥ����ʼʱ�䲻�����ڸó�λ�Ŀ���ʱ��"+ "</font>"));
			return false;
		}
		else if(endDate.getTime().after(endTime.getTime()))
		{
//			Log.e("endDate:", endDate.getTime().toString());
//			Log.e("endTime:", endTime.getTime().toString());
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "Ԥ������ʱ�䲻�ܳ����ó�λ�Ľ���ʱ��"+ "</font>"));
			return false;
		}
		else
		{
			//���ö��ĳ�λ��ʱ���
			BookSpaceInfo.setStart(startDate.getTime());
			BookSpaceInfo.setEnd(endDate.getTime());
			return true;
		}
	}

}

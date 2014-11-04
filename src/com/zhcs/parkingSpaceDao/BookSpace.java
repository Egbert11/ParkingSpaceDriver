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
 * Ԥ����λҳ��
 */
public class BookSpace extends Activity{
	private EditText start;
	private EditText end;
	private TextView illustrate;
	private Button book;
	private Button returnMap;
	private Calendar now  = Calendar.getInstance();
	//��ǰСʱ��
	private int curHour = now.get(Calendar.HOUR_OF_DAY);
	//��ǰСʱ���ͳ�λ���ſ�ʼʱ��Ƚ�
	private int max = curHour;
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
		
		if(max < BookSpaceInfo.getSpace().getStart())
			max = BookSpaceInfo.getSpace().getStart();
		//��ȡ��λ��С����ַ
		getAddress(BookSpaceInfo.getSpace().getCommunityid());
		illustrate.setText("�ó�λ�ĵ�ַΪ��"+address+"���۸�ΪÿСʱ"+BookSpaceInfo.getSpace().getPrice()+"Ԫ����ǰ��Ԥ����ʱ���Ϊ"+max+":00--"+
				BookSpaceInfo.getSpace().getEnd()+":00����׼ȷ������Ķ���ʱ�䣬��Ҫ��ʱŶ��");
		
		//���֧�������ļ����¼�
		book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isValid())
					return;
				new AlertDialog.Builder(BookSpace.this).setTitle("ȷ��֧��").setMessage("��ȷ��Ҫ���ĸó�λ�𣿵����֧������"+
					BookSpaceInfo.getSpace().getPrice() * (BookSpaceInfo.getEnd() - BookSpaceInfo.getStart())+"Ԫ���������˺���������ȡ")
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
	
	private void getAddress(String objectid) {
		AVQuery<AVObject> query = new AVQuery<AVObject>("Community");
		query.whereEqualTo("objectId", objectid);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            AVObject obj = avObjects.get(0);
		            address = obj.getString("address");
		            illustrate.setText("�ó�λ�ĵ�ַΪ��"+address+"���۸�ΪÿСʱ"+BookSpaceInfo.getSpace().getPrice()+"Ԫ����ǰ��Ԥ����ʱ���Ϊ"+max+":00--"+
		    				BookSpaceInfo.getSpace().getEnd()+":00����׼ȷ������Ķ���ʱ�䣬��Ҫ��ʱŶ��");
		            } else {
		            }
		        }
			});
	}
	//���³�λ������Ϣ
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
		            // ���ò�ѯ������ֻ���͸��Լ�����Ҫ���ű����������� demo
//		            push.setQuery(AVInstallation.getQuery().whereEqualTo("installationId",
//		                AVInstallation.getCurrentInstallation().getInstallationId()));
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
//		    		String msg = "һλ�û����������"+BookSpaceInfo.getSpace().getNum()+"�ų�λ������ʱ��Ϊ"+
//		    				BookSpaceInfo.getSpace().getStart()+":00--"+BookSpaceInfo.getSpace().getEnd()+":00";
//		    		AVPush.sendMessageInBackground(msg, pushQuery, new SendCallback() {
//		    		    @Override
//		    		    public void done(AVException e) {
//		    		    	if(e == null) {
//			    		    	Toast.makeText(BookSpace.this, "��Ϣ�ѷ���", Toast.LENGTH_SHORT).show();
//			    		    	//��ת������ҳ��
//								getLogObjectId();
//		    		    	} else {
//		    		    		Log.e("����ʧ��", e.getMessage());
//		    		    	}
//		    		    }
//		    		});
//		        } else {
//		        	Log.d("SpaceInfo", "����״̬ʧ��"+e.getMessage());
//		        }
//			}
//		});
		
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
		if(start.getText().toString().trim().equals("") || Integer.parseInt(start.getText().toString()) >= 24 || Integer.parseInt(start.getText().toString()) < max)
		{
			start.setError(Html.fromHtml("<font color=#808183>"
                    + "��ʼʱ�䲻����Ҫ��"+ "</font>"));
			return false;
		}
		else if(end.getText().toString().trim().equals("") || Integer.parseInt(end.getText().toString()) > 24)
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "����ʱ�䲻����Ҫ��"+ "</font>"));
			return false;
		}
		else if(Integer.parseInt(start.getText().toString()) >= Integer.parseInt(end.getText().toString()))
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "����ʱ�������ڿ�ʼʱ��"+ "</font>"));
			return false;
		}
		else if(Integer.parseInt(end.getText().toString()) > BookSpaceInfo.getSpace().getEnd())
		{
			end.setError(Html.fromHtml("<font color=#808183>"
                    + "Ԥ������ʱ�䲻�ܳ����ó�λ�Ŀ��Ž���ʱ��"+ "</font>"));
			return false;
		}
		else
		{
			//���ö��ĳ�λ��ʱ���
			BookSpaceInfo.setStart(Integer.parseInt(start.getText().toString()));
			BookSpaceInfo.setEnd(Integer.parseInt(end.getText().toString()));
			return true;
		}
	}

}

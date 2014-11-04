package com.zhcs.regAndLogin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.zhcs.net.*;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity{
	private EditText phoneNum;
	private EditText userName;
	private EditText password;
	private Button register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ȥ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		phoneNum=(EditText)findViewById(R.id.Phone);
		userName=(EditText)findViewById(R.id.user);
		password=(EditText)findViewById(R.id.password);
		register=(Button)findViewById(R.id.register);
		
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isCellphone(phoneNum.getText().toString()))
				{
//					Intent intent=new Intent(Register.this,Login.class);
//					startActivity(intent);
//					finish();
					phoneNum.setError(Html.fromHtml("<font color=#808183>"
		                    + "�ֻ�����Ч "+ "</font>"));
				}
				else if(!isUser(userName.getText().toString()))
				{
					userName.setError(Html.fromHtml("<font color=#808183>"
		                    + "�û���������Ҫ��"+ "</font>"));
				}				
				else if(!isPassword(password.getText().toString()))
				{
					password.setError(Html.fromHtml("<font color=#808183>"
		                    + "���벻����Ҫ��"+ "</font>"));
				}
				else
				{
					AVQuery<AVObject> query = new AVQuery<AVObject>("DriverInfo");
					query.whereEqualTo("phone", phoneNum.getText().toString());
					query.findInBackground(new FindCallback<AVObject>() {
						@Override
						public void done(List<AVObject> arg0, AVException e) {
							if (e == null) {
					            Log.d("�ɹ�", "��ѯ��" + arg0.size() + " ����������������");
					            if(arg0.size() == 0) {
					            	//ע�����û�
						            AVObject reg = new AVObject("DriverInfo");
									reg.put("phone",phoneNum.getText().toString());
									reg.put("name", userName.getText().toString());
									reg.put("password", password.getText().toString());
									reg.put("money", 1000);
									reg.saveInBackground(new SaveCallback() {
										public void done(AVException arg0) {
											if (arg0 == null) {
												Toast.makeText(Register.this,"ע��ɹ�", Toast.LENGTH_SHORT).show();
									        } else {
									        	Toast.makeText(Register.this,"ע��ʧ��", Toast.LENGTH_SHORT).show();
										}
									}
						        });
					            }
					        } else {
					            Log.d("ʧ��", "����: " + e.getMessage());
					            
								}
							}
					});
				
				}
			}
		});
	}
	
	//�������̣߳������û�ע��
		class reg implements Runnable
		{
			@Override
			public void run()
			{
				Map<String,String> map=new HashMap<String, String>();

				map.put("phone",phoneNum.getText().toString());
				map.put("name", userName.getText().toString());
				map.put("password", password.getText().toString());
				
				//���巢�������URL
				String url=HttpUtil.URL+"DriverRegister";
				String result="";
				//��������
				try {
					result=new JSONObject(HttpUtil.postRequest(url, map)).getString("result");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg=new Message();
				if(result.equals("succeed"))
				{
					//Toast.makeText(Register.this,"ע��ɹ�", Toast.LENGTH_SHORT).show();
					msg.what=0x00;
				}
				else if(result.equals("repeat"))
				{
					//Toast.makeText(Register.this, "�ֻ����Ѿ�ע���", Toast.LENGTH_SHORT).show();
					msg.what=0x01;
				}
				else
				{
					//Toast.makeText(Register.this, "ע��ʧ��", Toast.LENGTH_SHORT).show();
					msg.what=0x02;
				}
				handler.sendMessage(msg);
			}
		}
		
		final Handler handler=new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if(msg.what==0x00){
					Toast.makeText(Register.this,"ע��ɹ�", Toast.LENGTH_SHORT).show();
				}
				else if(msg.what==0x01){
					Toast.makeText(Register.this, "�ֻ����Ѿ�ע���", Toast.LENGTH_SHORT).show();
				}
				else if(msg.what==0x02){
					Toast.makeText(Register.this, "ע��ʧ��", Toast.LENGTH_SHORT).show();
				}
			}
		};
		
	  public static boolean isCellphone(String str)
	  {
		  Pattern pattern = Pattern.compile("1[0-9]{10}");
		  Matcher matcher = pattern.matcher(str); 
		  if (matcher.matches())
		  {
			  return true;
		  }
		  else 
		  {
			  return false;
		  }	  
	 }
	  
	  public  boolean isUser(String str)
	  {
		  if(str.trim().equals(""))
		  {
			return false;
		  }
		  else
		  {
			  return true;
		  }
		  
	  }
	  
	  public  boolean isPassword(String str)
	  {
		  if(str.trim().equals("")||str.trim().length()<6||str.trim().length()>15)
		  {
			return false;
		  }
		  else
		  {
			  return true;
		  }
		  
	  }
	  
}

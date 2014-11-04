package com.zhcs.regAndLogin;
/*
 * 用于车主登陆实现
 */
import java.util.*;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.zhcs.driverBean.DriverInfo;
import com.zhcs.net.*;
import com.zhcs.parkingSpaceDao.MapForFindParkingSpace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity{
	private EditText phoneNum;
	private EditText password;
	private CheckBox rem_pw, auto_login; 
	private Button login;
	private Button reg;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		PushService.setDefaultPushCallback(this, Login.class);
	    // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
	    PushService.subscribe(this, "public", Login.class);
		//获得实例对象
		sp=this.getSharedPreferences("Userinfo", Context.MODE_WORLD_READABLE);
		phoneNum=(EditText)findViewById(R.id.Phone);
		password=(EditText)findViewById(R.id.password);
		rem_pw = (CheckBox) findViewById(R.id.cb_mima);  
        auto_login = (CheckBox) findViewById(R.id.cb_auto);
		login=(Button)findViewById(R.id.log);
		reg=(Button)findViewById(R.id.reg);
		
		//判断记住密码多选框的状态  
        if(sp.getBoolean("ISCHECK", false))  
        {  
          //默认为记住密码
          rem_pw.setChecked(true);
          phoneNum.setText(sp.getString("PHONENUM", ""));  
          password.setText(sp.getString("PASSWORD", ""));  
          //判断自动登陆多选框状态  
          if(sp.getBoolean("AUTO_ISCHECK", false))  
          {  
        	  	// 执行输入校验
				if(validate())
				{
//					login log=new login();
//					new Thread(log,"登陆线程").start();//启动登录线程
					loginToServer();
				}  
			}  
         }  
		
		
		//监听记住密码多选框按钮事件
		rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					sp.edit().putBoolean("ISCHECK", true).commit();
				}
				else{
					sp.edit().putBoolean("ISCHECK", false).commit();
				}
			}
		});
		
		//监听自动登录多选框按钮事件
		auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
				}
				else{
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}
			}
		});
		
		
		//注册按键触发事件
		reg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// 跳转至注册界面
				Intent intent=new Intent(Login.this,Register.class);
				startActivity(intent);
			}
		});
		
		//登陆按键触发事件
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0){
				// 执行输入校验
				if(validate())
				{
//					login log=new login();
//					new Thread(log,"登陆线程").start();//启动登陆线程
					loginToServer();
				}
			}
		});
	}
	
	/*
	 * 登陆到服务器
	 */
	private void loginToServer(){
		final String phone=phoneNum.getText().toString();
		final String pass=password.getText().toString();
		AVQuery<AVObject> query = new AVQuery<AVObject>("DriverInfo");
		query.whereEqualTo("phone", phone);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
		            AVObject obj = avObjects.get(0);
		            if(pass.equals(obj.getString("password"))) {
		            	if(rem_pw.isChecked())  
			            {  
			             //记住用户名、密码
			              Editor editor = sp.edit();  
			              editor.putString("PHONENUM",phone);  
			              editor.putString("PASSWORD",pass);  
			              editor.commit();  
			            }
		            	// 保存 installation 到服务器
		                AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
		                  @Override
		                  public void done(AVException e) {
		                    AVInstallation.getCurrentInstallation().saveInBackground();
		                  }
		                });
						obj.put("installationid",AVInstallation.getCurrentInstallation().getInstallationId());
						obj.saveInBackground();
						//设置车主的id和手机
						DriverInfo.setId(obj.getObjectId());
						DriverInfo.setPhone(phone);	
			            Toast.makeText(Login.this, "登陆成功", Toast.LENGTH_SHORT).show();
						Intent intent=new Intent(Login.this, MapForFindParkingSpace.class);
						startActivity(intent);
						finish();
		            }else {
		            	Toast.makeText(Login.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
		            }
		        } else {
		            Log.d("失败", "登陆失败: " + e.getMessage());
		            Toast.makeText(Login.this, "登陆失败", Toast.LENGTH_SHORT).show();
		        }
		    }
		});
	
	}
	
	//创建新线程，用于用户登录
	class login implements Runnable
	{
		@SuppressWarnings("unchecked")
		@Override
		public void run()
		{
			String phone=phoneNum.getText().toString();
			String pass=password.getText().toString();
			Map<String,String> map=new HashMap<String,String>();
			map.put("phone", phone);
			map.put("pass", pass);
			
			 
			//定义发送请求的URL
			String url=HttpUtil.URL+"DriverLogin";
			JSONObject obj;
			String result = "";
			int id = 0;
			JSONArray jArr = new JSONArray();
			try {
				obj = new JSONObject(HttpUtil.postRequest(url, map));
				result = obj.getString("result");
				id = obj.getInt("driver_id");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Message msg=new Message();
			if(result.equals("succeed"))
			{
				if(rem_pw.isChecked())  
	            {  
	             //记住用户名、密码
	              Editor editor = sp.edit();  
	              editor.putString("PHONENUM",phone);  
	              editor.putString("PASSWORD",pass);  
	              editor.commit();  
	            }
				//设置车主的id和手机
//				DriverInfo.setId(id);
				DriverInfo.setPhone(phone);			
				msg.what=0x01;//登陆成功
			}
			else
			{
				msg.what=0x00;//登录失败
			}
			handler.sendMessage(msg);
		}
	}
	
	final Handler handler=new Handler()
	{
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == 0x01)
			{
				Toast.makeText(Login.this, "登陆成功", Toast.LENGTH_SHORT).show();
				Bundle bundle=new Bundle();
				bundle.putString("phoneNUM", phoneNum.getText().toString());
				Intent intent=new Intent(Login.this, MapForFindParkingSpace.class);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
			else if(msg.what == 0x00){
				Toast.makeText(Login.this, "登陆失败", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	//对用户输入的手机号、密码进行校验是否为空
	private boolean validate()
	{
		String phone=phoneNum.getText().toString().trim();//trim是两边去空格的方法
		String pass=password.getText().toString().trim();//trim是两边去空格的方法
		if(phone.equals("")||pass.equals(""))
		{
			new AlertDialog.Builder(Login.this)
			.setIcon(getResources().getDrawable(R.drawable.login_error_icon))
			.setTitle("登录失败")
			.setMessage("手机号或者密码不能为空，\n请检查后重新输入！")
			.create().show();
			return false;
		}
		return true;
	}

}


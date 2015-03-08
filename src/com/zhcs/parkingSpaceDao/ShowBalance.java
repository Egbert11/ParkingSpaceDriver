package com.zhcs.parkingSpaceDao;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zhcs.driverBean.DriverInfo;
import com.zhcs.regAndLogin.R;

/**
 * 查询余额页面
 * @author gzs3050
 *
 */
public class ShowBalance extends Activity{
	private Button goRecharge = null;
	private TextView moneyText = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_showbalance);
		
		moneyText = (TextView)findViewById(R.id.textView3);
		goRecharge = (Button)findViewById(R.id.goRecharge);
		
		getAccount();
		
	}
	
	/**
	 * 获取个人账户余额并显示
	 */
	private void getAccount(){
		AVQuery<AVObject> query = new AVQuery<AVObject>("DriverInfo");
		query.whereEqualTo("phone", DriverInfo.getPhone());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
		    public void done(List<AVObject> avObjects, AVException e) {
		        if (e == null) {
		            AVObject obj = avObjects.get(0);
		            int money = obj.getNumber("money").intValue();
		            moneyText.setText(String.valueOf(money));
		        }
		        else{
		        	Log.e("显示余额", e.getMessage());
		        }
			}
		});
	}
	
	/**
	 * 充值
	 */
	public void recharge(View view){
		LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.activity_recharge,
				(ViewGroup) findViewById(R.id.dialog));
		new AlertDialog.Builder(this).setTitle("请输入充值金额").setView(layout)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
			   
            @Override 
            public void onClick(DialogInterface dialog, int which) { 

                EditText text = (EditText)layout.findViewById(R.id.money);
                final int money = Integer.parseInt(text.getText().toString());
                if(money >= 0){
                	//增加车位主的钱
                	AVQuery<AVObject> owner = new AVQuery<AVObject>("DriverInfo");
					owner.whereEqualTo("objectId", DriverInfo.getId());
					owner.findInBackground(new FindCallback<AVObject>() {
						@Override
					    public void done(List<AVObject> avObjects, AVException e) {
					        if (e == null) {
					        	AVObject modCost = avObjects.get(0);
					        	modCost.increment("money", money);
					        	modCost.saveInBackground();
					        } else {
					        	Log.d("增加余额", "添加money失败"+e.getMessage());
					        }
						}
					});
					int preMoney = Integer.parseInt(moneyText.getText().toString());
		        	moneyText.setText(String.valueOf(preMoney + money));
                }
            } 
        })
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
			}
		}).show();
	}
}

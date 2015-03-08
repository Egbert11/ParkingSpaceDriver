package com.zhcs.parkingSpaceDao;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.zhcs.driverBean.DriverInfo;
import com.zhcs.driverBean.SpaceHistoryBean;

/**
 * 
 * @author Administrator
 * 获取历史订单列表
 */
public class GetHistoryInfo {
	private static ArrayList<SpaceHistoryBean> list = new ArrayList<SpaceHistoryBean>();
	private static Activity activity = null;
	
	public static ArrayList<SpaceHistoryBean> getList() {
		return list;
	}
	public static void setList(ArrayList<SpaceHistoryBean> list) {
		GetHistoryInfo.list = list;
	}
}

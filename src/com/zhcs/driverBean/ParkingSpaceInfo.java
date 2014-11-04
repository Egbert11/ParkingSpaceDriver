package com.zhcs.driverBean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.avos.avoscloud.AVObject;

/*
 * 用来从服务器下载车位信息与本地车位信息的转化
 */
public class ParkingSpaceInfo {
	private static ArrayList<SpaceInfoBean> list = new ArrayList<SpaceInfoBean>();
	public static ArrayList<SpaceInfoBean> getList() {
		return list;
	}
	public static void setList(ArrayList<SpaceInfoBean> list) {
		ParkingSpaceInfo.list = list;
	}
	public static void initializeList(List<AVObject> obj) {
		// TODO Auto-generated method stub
		if(!ParkingSpaceInfo.list.isEmpty())
			ParkingSpaceInfo.list.clear();
		for(int i = 0; i < obj.size(); i++){
			SpaceInfoBean bean = new SpaceInfoBean();
			bean.setSpaceid(obj.get(i).getObjectId());
			bean.setOwnerid(obj.get(i).getString("ownerid"));
			bean.setCommunityid(obj.get(i).getString("communityid"));
			bean.setLat(obj.get(i).getInt("lat"));
			bean.setLng(obj.get(i).getInt("lng"));
			bean.setNum(obj.get(i).getInt("num"));
			bean.setPrice(obj.get(i).getInt("price"));
			bean.setStart(obj.get(i).getInt("start"));
			bean.setEnd(obj.get(i).getInt("end"));
			bean.setState(obj.get(i).getInt("state"));
			ParkingSpaceInfo.list.add(bean);
		}
	}
}

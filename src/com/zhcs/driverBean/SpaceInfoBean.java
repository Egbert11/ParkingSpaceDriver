package com.zhcs.driverBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author huangjiaming
 * 车位信息
 */
public class SpaceInfoBean {
	/**
	 * 车位id
	 */
	private String spaceid;
	/**
	 * 车位所有者id
	 */
	private String ownerid;
	/**
	 * 车位所处小区id
	 */
	private String communityid;
	/**
	 * 纬度
	 */
	private int lat;
	/**
	 * 经度
	 */
	private int lng;
	/**
	 * 车位号
	 */
	private int num;
	/**
	 * 车位单价
	 */
	private int price;
	/**
	 * 超时罚款单价
	 */
	private int fine;
	/**
	 * 共享开始时间
	 */
	private Date start = new Date();
	/**
	 * 共享结束时间
	 */
	private Date end = new Date();
	/**
	 * 车位状态：0--开放， 1--预订中， 2--已经进入小区， 3--已经离开小区
	 */
	private int state;
	/**
	 * 车位共享日期
	 */
	private List<Calendar> shareTime = new ArrayList<Calendar>();
	public String getSpaceid() {
		return spaceid;
	}
	public void setSpaceid(String spaceid) {
		this.spaceid = spaceid;
	}
	public String getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}
	public String getCommunityid() {
		return communityid;
	}
	public void setCommunityid(String communityid) {
		this.communityid = communityid;
	}
	public int getLat() {
		return lat;
	}
	public void setLat(int lat) {
		this.lat = lat;
	}
	public int getLng() {
		return lng;
	}
	public void setLng(int lng) {
		this.lng = lng;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getFine() {
		return fine;
	}
	public void setFine(int fine) {
		this.fine = fine;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public List<Calendar> getShareTime() {
		return shareTime;
	}
	public void setShareTime(List<Calendar> shareTime) {
		this.shareTime = shareTime;
	}
}

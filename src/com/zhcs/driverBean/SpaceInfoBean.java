package com.zhcs.driverBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author huangjiaming
 * ��λ��Ϣ
 */
public class SpaceInfoBean {
	/**
	 * ��λid
	 */
	private String spaceid;
	/**
	 * ��λ������id
	 */
	private String ownerid;
	/**
	 * ��λ����С��id
	 */
	private String communityid;
	/**
	 * γ��
	 */
	private int lat;
	/**
	 * ����
	 */
	private int lng;
	/**
	 * ��λ��
	 */
	private int num;
	/**
	 * ��λ����
	 */
	private int price;
	/**
	 * ��ʱ�����
	 */
	private int fine;
	/**
	 * ����ʼʱ��
	 */
	private Date start = new Date();
	/**
	 * �������ʱ��
	 */
	private Date end = new Date();
	/**
	 * ��λ״̬��0--���ţ� 1--Ԥ���У� 2--�Ѿ�����С���� 3--�Ѿ��뿪С��
	 */
	private int state;
	/**
	 * ��λ��������
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

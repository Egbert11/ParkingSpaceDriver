package com.zhcs.driverBean;

import java.util.Date;

/*
 * ��ǰ�����ĵĳ�λ��Ϣ
 */
public class BookSpaceInfo {
	private static SpaceInfoBean space;
	//���ĳ�λ�Ŀ�ʼʱ��
	private static Date start;
	//���ĳ�λ�Ľ���ʱ��
	private static Date end;
	//���ĳ�λ����־id
	private static String logObjectId;
	public static SpaceInfoBean getSpace() {
		return space;
	}

	public static void setSpace(SpaceInfoBean space) {
		BookSpaceInfo.space = space;
	}

	public static Date getStart() {
		return start;
	}

	public static void setStart(Date start) {
		BookSpaceInfo.start = start;
	}

	public static Date getEnd() {
		return end;
	}

	public static void setEnd(Date end) {
		BookSpaceInfo.end = end;
	}

	public static String getLogObjectId() {
		return logObjectId;
	}

	public static void setLogObjectId(String logObjectId) {
		BookSpaceInfo.logObjectId = logObjectId;
	}
}

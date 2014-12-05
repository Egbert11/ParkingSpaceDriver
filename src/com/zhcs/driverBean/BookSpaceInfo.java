package com.zhcs.driverBean;

import java.util.Date;

/*
 * 当前所订阅的车位信息
 */
public class BookSpaceInfo {
	private static SpaceInfoBean space;
	//订阅车位的开始时间
	private static Date start;
	//订阅车位的结束时间
	private static Date end;
	//订阅车位的日志id
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

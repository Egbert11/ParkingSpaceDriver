package com.zhcs.driverBean;

/*
 * 当前所订阅的车位信息
 */
public class BookSpaceInfo {
	private static SpaceInfoBean space;
	//订阅车位的开始时间
	private static int start;
	//订阅车位的结束时间
	private static int end;
	//订阅车位的日志id
	private static String logObjectId;
	public static SpaceInfoBean getSpace() {
		return space;
	}

	public static void setSpace(SpaceInfoBean space) {
		BookSpaceInfo.space = space;
	}

	public static int getStart() {
		return start;
	}

	public static void setStart(int start) {
		BookSpaceInfo.start = start;
	}

	public static int getEnd() {
		return end;
	}

	public static void setEnd(int end) {
		BookSpaceInfo.end = end;
	}

	public static String getLogObjectId() {
		return logObjectId;
	}

	public static void setLogObjectId(String logObjectId) {
		BookSpaceInfo.logObjectId = logObjectId;
	}
}

package com.zhcs.driverBean;

/*
 * ��ǰ�����ĵĳ�λ��Ϣ
 */
public class BookSpaceInfo {
	private static SpaceInfoBean space;
	//���ĳ�λ�Ŀ�ʼʱ��
	private static int start;
	//���ĳ�λ�Ľ���ʱ��
	private static int end;
	//���ĳ�λ����־id
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

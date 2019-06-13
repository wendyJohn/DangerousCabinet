package com.sanleng.dangerouscabinet.fid.entity;

public class EPC {
	private int id;
	private String epc;
	private String rssi;
	private int picturePath;
	private int count;
	private String ant;
	private String deviceNo;
	private String direction;

	public EPC() {

	}

	public EPC(int id, String epc, String rssi, int count, String ant,
               String deviceNo, String direction, int picturePath) {
		this.id = id;
		this.epc = epc;
		this.rssi = rssi;
		this.count = count;
		this.ant = ant;
		this.deviceNo = deviceNo;
		this.direction = direction;
		this.picturePath = picturePath;
	}

	public int getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(int picturePath) {
		this.picturePath = picturePath;
	}

	public String getAnt() {
		return ant;
	}

	public void setAnt(String ant) {
		this.ant = ant;
	}

	public String getRssi() {
		return rssi;
	}

	public void setRssi(String rssi) {
		this.rssi = rssi;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}

	/**
	 * @param epc
	 *            the epc to set
	 */
	public void setEpc(String epc) {
		this.epc = epc;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("[id=" + id);
		sb.append(", epc=" + epc);
		sb.append(", rssi=" + rssi);
		sb.append(", count=" + count);
		sb.append(", deviceNo=" + deviceNo);
		sb.append(", direction=" + direction);
		sb.append("]");
		return sb.toString();
	}
}

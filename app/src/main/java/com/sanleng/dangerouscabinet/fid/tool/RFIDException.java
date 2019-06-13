package com.sanleng.dangerouscabinet.fid.tool;

/**
 * 操作标签错误时异常信息
 * @author 
 *
 */
public class RFIDException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public RFIDException(String message){
		super(message);
	}
}

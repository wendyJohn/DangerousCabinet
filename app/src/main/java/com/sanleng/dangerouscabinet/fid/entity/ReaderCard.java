package com.sanleng.dangerouscabinet.fid.entity;


import com.sanleng.dangerouscabinet.fid.dao.Reader;
import com.sanleng.dangerouscabinet.fid.service.CallBack;

public class ReaderCard implements Runnable {
	Reader reader = null;

	CallBack callBack = null;

	public ReaderCard() {
	}

	public ReaderCard(Reader reader, CallBack callBack) {
		this.reader = reader;
		this.callBack = callBack;
	}

	@Override
	public void run() {
		reader.threadFunc(reader, callBack);
	}
}

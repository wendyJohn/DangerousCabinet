package com.sanleng.dangerouscabinet.fid.service;

/**
 * 这是一个回调接口
 *
 */

public interface CallBacks {
    /**
     * 这个是所有锁关闭时要调用的函数告诉安卓，也就是回调函数
     * @param result 是答案 false 全部关闭了
     */
    public void locksStatus(Boolean result);

}

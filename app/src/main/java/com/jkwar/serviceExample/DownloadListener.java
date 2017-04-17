package com.jkwar.serviceExample;

/**
 * Created by jkwar on 2017/4/14.
 * 定义下载接口
 */

public interface DownloadListener {
    //成功
    void onSuccess();

    //失败
    void onFailed();

    //暂停
    void onPaused();

    //取消
    void onCanceled();

    //进度条
    void onProgress(int progress);
}

package com.ly.sample;

import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.LogUtils;

import okhttp3.OkHttpClient;

/**
 * Description: 类的描述
 * Copyright  : Copyright (c) 2019
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019/7/27 on 02:35
 */
public class MyApplication extends ActorApplication {
    @Override
    protected void initOkHttpClient(OkHttpClient.Builder builder) {
        //初始化okhttpclient
    }

    @Override
    protected void onUncaughtException(Thread thread, Throwable e) {
        LogUtils.formatError("onUncaughtException: thread=%s, e=%s", true, thread, e);
    }
}

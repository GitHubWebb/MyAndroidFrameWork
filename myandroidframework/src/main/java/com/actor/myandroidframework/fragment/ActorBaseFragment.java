package com.actor.myandroidframework.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actor.myandroidframework.R;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.MyOkhttpUtils.MyOkHttpUtils;
import com.actor.myandroidframework.utils.TextUtil;
import com.actor.myandroidframework.utils.ToastUtils;
import com.actor.myandroidframework.widget.LoadingDialog;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Description: Fragment基类
 *     onActivityCreated : 这个Fragment所依附的Activity对象被创建成功之后，初始化数据
 *     onViewCreated : 这个Fragment所包装的View对象创建完成之后会进行的回调
 * Copyright  : Copyright (c) 2017
 * Company    : ▓▓▓▓ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
 * Author     : 李大发
 * Date       : 2017/5/27 on 18:22.
 * @version 1.0
 */
public abstract class ActorBaseFragment extends Fragment {

//    private   FrameLayout            flContent;  //主要内容的帧布局
//    private   LinearLayout           llLoading; //加载中的布局
//    protected TextView               tvLoading;  //例:正在加载中,请稍后...
//    private   LinearLayout           llEmpty; //没数据
    protected Activity            activity;
    protected Intent              intent;
    protected Map<String, Object> params = new LinkedHashMap<>();
//    protected ACache              aCache = ActorApplication.instance.aCache;

    //使用newInstance()的方式返回Fragment对象
//    public static ActorBaseFragment newInstance() {
//        ActorBaseFragment fragment = new ActorBaseFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    /**
     * 获取这个Fragment所依附的Activity对象
     * 初始化 & 系统恢复页面数据 & 旋转屏幕 时, 会回调这个方法
     */
    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        Bundle arguments = getArguments();
//        if (arguments != null) {
//            mParam1 = arguments.getString(ARG_PARAM1);
//            mParam2 = arguments.getString(ARG_PARAM2);
//        }
    }

    //生成这个Fragment所包装的View对象
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
//        View view = inflater.inflate(R.layout.xxx, container, false);
//        return view;
    }

    @Override   //这个Fragment所包装的View对象创建完成之后会进行的回调, 初始化View
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        View baseView = getLayoutInflater().inflate(R.layout.activity_base, null);//加载基类布局
//        flContent = baseView.findViewById(R.id.fl_content);
//        llLoading = baseView.findViewById(R.id.ll_loading);
//        tvLoading = baseView.findViewById(R.id.tv_loading);
//        llEmpty = baseView.findViewById(R.id.ll_empty);
//        flContent.addView(view);//将子布局添加到空的帧布局
        super.onViewCreated(/*baseView*/view, savedInstanceState);
    }

    //跳转Activity后返回, 会回调
    @Override
    public void onResume() {
        super.onResume();
        logError(getClass().getName());
    }

    //使用add hide show时会回调这个方法
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        logFormat(getClass().getName().concat(": hidden=%b"), hidden);
    }

    /**
     * 当Fragment可见/不可见的时候
     * 使用ViewPager + Fragment, 当ViewPager切换Fragment时会回调这个方法.
     * 在onCreateView之前调用的, 如果isVisibleToUser=false, "不要使用控件 & 操作UI界面"
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        logFormat(getClass().getName().concat(": isVisibleToUser=%b"), isVisibleToUser);
        if(isVisibleToUser/*getUserVisibleHint()*/) {//当可见的时候
            //do something...
        }
    }

    //是否显示加载中...
//    protected void showLoading(boolean isShow) {
//        llLoading.setVisibility(isShow ? View.VISIBLE : View.GONE);
//    }

    //设置加载中...
//    protected void setLoadingText(CharSequence loading) {
//        tvLoading.setText(loading);
//    }

    //是否显示empty图片
//    protected void showEmpty(boolean isShow) {
//        llEmpty.setVisibility(isShow ? View.VISIBLE : View.GONE);
//    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (activity != null) {
            activity.overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (activity != null) {
            activity.overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logFormat("onActivityResult: requestCode=%d, resultCode=%d, data=%s", requestCode, resultCode, data);
    }


    //返回String区=============================================
    protected String getNoNullString(String text){
        return text == null ? "" : text;
    }

    protected String getNoNullString(String s, String defaultStr) {
        return s == null? defaultStr : s;
    }

    protected String getStringFormat(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    public static String getText(Object obj){
        return TextUtil.getText(obj);
    }


    //判空区=============================================
    protected boolean isEmpty(Object obj) {
        return !isNoEmpty(obj);
    }

    protected boolean isEmpty(Object obj, CharSequence notify) {
        return !isNoEmpty(obj, notify);
    }

    /**
     * @param objs 参数的类型为:
     * <ol>
     *      <li>{@link android.widget.TextView}</li>
     *      <li>{@link android.support.design.widget.TextInputLayout}</li>
     *      <li>{@link TextUtil.GetTextAble}</li>
     *      <li>{@link CharSequence}</li>
     *      <li>{@link java.lang.reflect.Array}</li>
     *      <li>{@link java.util.Collection}</li>
     *      <li>{@link java.util.Map}</li>
     * </ol>
     * @return 都不为空,返回true
     */
    protected boolean isNoEmpty(Object... objs) {
        return TextUtil.isNoEmpty(objs);
    }

    protected boolean isNoEmpty(Object obj, CharSequence notify) {
        return TextUtil.isNoEmpty(obj, notify);
    }


    //打印日志区=============================================
    protected void logError(String msg) {
        LogUtils.Error(msg, false);
    }

    protected void logFormat(String format, Object... args) {
        LogUtils.formatError(format, false, args);
    }

    //toast区=============================================
    protected void toast(CharSequence notify){
        ToastUtils.show(notify);
    }


    //显示加载Diaong=============================================
    private LoadingDialog loadingDialog;
    protected void showLoadingDialog() {
        getLoadingDialog().show();
    }

    protected LoadingDialog getLoadingDialog() {
        if (loadingDialog == null) loadingDialog = new LoadingDialog(activity);
        return loadingDialog;
    }

    //隐藏加载Diaong
    protected void dismissLoadingDialog() {
        if (loadingDialog != null) loadingDialog.dismiss();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissLoadingDialog();
        MyOkHttpUtils.cancelTag(this);
//        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }
}

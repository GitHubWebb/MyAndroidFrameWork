package com.ly.sample.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actor.myandroidframework.widget.SwipeRefreshLayoutCompatViewPager;
import com.ly.sample.R;
import com.ly.sample.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description: 里面层Fragment
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2019-9-6 on 16:41
 */
public class BlankFragment2 extends BaseFragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayoutCompatViewPager swipeRefreshLayout;
    @BindView(R.id.tv_content)
    TextView tvContent;
    Unbinder unbinder;
    private int position;
    private String content;

    public static BlankFragment2 newInstance(int position, String content) {
        BlankFragment2 fragment = new BlankFragment2();
        Bundle args = new Bundle();
        args.putInt(Global.POSITION, position);
        args.putString(Global.CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            position = arguments.getInt(Global.POSITION, -1);
            content = arguments.getString(Global.CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank_fragment2, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvContent.setText(getStringFormat("第%d个Table, content=%s,\n可以下拉刷新哦 ↓", position, content));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {//1秒后消失
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null) {//onDestroyView后, 所有view = null
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1_000);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

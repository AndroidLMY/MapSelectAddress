package com.androidlmy.mapselectaddress;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidlmy.mapselectaddress.map_navigation.NavigationActivity;
import com.androidlmy.mapselectaddress.title_utils.StatusBarUtil;
import com.androidlmy.mapselectaddress.track.TrackActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_adress)
    TextView tvAdress;
    @BindView(R.id.title)
    HeadCustomView title;
    @BindView(R.id.tv_daohang)
    TextView tvDaohang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        title.setTitle("仿京东地址选择");
        title.setTitleTextSize(16);
        title.setBackGone();
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
//        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white));

        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    @OnClick({R.id.tv_daohang, R.id.tv_adress,R.id.tv_huizhi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_daohang:
                NavigationActivity.show(this);
                break;
            case R.id.tv_adress:
                AddAddressActivity.show(this);
                break;
            case R.id.tv_huizhi:
                TrackActivity.show(this);
                break;


        }
    }


}

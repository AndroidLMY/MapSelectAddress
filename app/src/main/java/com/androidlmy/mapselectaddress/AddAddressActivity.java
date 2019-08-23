package com.androidlmy.mapselectaddress;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidlmy.mapselectaddress.title_utils.StatusBarUtil;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class AddAddressActivity extends AppCompatActivity implements ClickCallback, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.title)
    HeadCustomView title;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.rl_province)
    TextView rlProvince;
    @BindView(R.id.tv_province)
    TextView tvProvince;
    @BindView(R.id.iv_weizhi)
    ImageView ivWeizhi;
    @BindView(R.id.tv_adress)
    TextView tvAdress;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.switch_like)
    Switch switchLike;
    @BindView(R.id.bt_submit)
    Button btSubmit;
    private static final int PRC_PHOTO_PREVIEW = 10086;

    //请求码
    @AfterPermissionGranted(PRC_PHOTO_PREVIEW)
    private void methodRequiresTwoPermission() {
        String[] perms = {
                PermissionUtils.LOCATION,
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            LocationMapActivity.show(this);

            //表明已经授权，可以进行用户授予权限的操作
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "地图定位需要以下权限:", PRC_PHOTO_PREVIEW, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions 权限处理请求结果
        Log.i("DDD", "onRequestPermissionsResult:" + requestCode);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //同意授权
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("DDD", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    //拒绝授权
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("DDD", "onPermissionsDenied:" + requestCode + ":" + perms.size());
        new AppSettingsDialog.Builder(this)
                .setTitle("提醒")
                .setRationale("此app需要这些权限才能正常使用")
                .build()
                .show();
    }

    /**
     * 拒绝权限前往设置中开启权限的回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            methodRequiresTwoPermission();

        }
    }


    public static void show(Context context) {
        context.startActivity(new Intent(context, AddAddressActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册Eventbus

        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
//        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.white));

        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
        initTitle();
    }

    public void initTitle() {
        title.setTitle("添加新地址");
        title.setTitleTextSize(16);
        title.setBackImg(R.drawable.ic_back_black);
        title.setClickCallBack(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onRightImgClick() {

    }

    @OnClick({R.id.iv_weizhi, R.id.bt_submit, R.id.tv_province})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_weizhi:
                methodRequiresTwoPermission();

                break;
            case R.id.bt_submit:
                Toast.makeText(this, "保存地址", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_province:
                hideSoftKeyboard(this);
                JDCityPicker cityPicker = new JDCityPicker();
                JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();
                jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
                cityPicker.init(this);
                cityPicker.setConfig(jdCityConfig);
                cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                    @Override
                    public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                        tvProvince.setText(province.getName() + city.getName() + district.getName());
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                cityPicker.showCityPicker();
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MapToAdressEventBus messageEvent) {
        tvProvince.setText(messageEvent.getProvince());
        etAddress.setText(messageEvent.getAddress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    /**
     * 隐藏软键盘(只适用于Activity，不适用于Fragment)
     */
    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

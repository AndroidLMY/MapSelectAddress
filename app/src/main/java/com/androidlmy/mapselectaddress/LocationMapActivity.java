package com.androidlmy.mapselectaddress;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.androidlmy.mapselectaddress.bean.TrackBean;
import com.androidlmy.mapselectaddress.title_utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationMapActivity extends AppCompatActivity implements ClickCallback, PoiSearch.OnPoiSearchListener {
    @BindView(R.id.title)
    HeadCustomView title;
    @BindView(R.id.et_seek)
    EditText etSeek;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.iv_center_location)
    ImageView ivCenterLocation;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.rl_map)
    RelativeLayout rlMap;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private AMap aMap;
    private AMapLocation location;
    private ObjectAnimator mTransAnimator;//地图中心标志动态

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private List<PoiItem> mPoiList = new ArrayList<>();//用于存放pio数据
    private UiSettings mUiSettings;
    private LocationMapAdapter adapter;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private boolean isSearch = false;

    public static void show(Context context) {
        context.startActivity(new Intent(context, LocationMapActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        ButterKnife.bind(this);

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
        //必须要写
        map.onCreate(savedInstanceState);
        initMap();//初始化地图和定位
        initListener();//初始化监听
        initAnimator();//初始化动画
    }


    private void initRecyclerview() {
        if (adapter == null) {
            adapter = new LocationMapAdapter(this, mPoiList);
            recyclerview.setLayoutManager(linearLayoutManager);
            recyclerview.setAdapter(adapter);
            adapter.setOnClick(new LocationMapAdapter.OnClick() {
                @Override
                public void OnClickListener(String province, String address) {

                    if (isSearch) {
                        EventBus.getDefault().post(new MapToAdressEventBus(province,
                                address));
                    } else {
                        EventBus.getDefault().post(new MapToAdressEventBus(location.getProvince() + location.getCity() + location.getDistrict(), address));
                    }
                    finish();
                }
            });
        } else {
            adapter.setData(mPoiList);
        }
    }

    public void initTitle() {
        title.setTitle("定位地址");
        title.setTitleTextSize(16);
        title.setBackImg(R.drawable.ic_back_black);
        title.setClickCallBack(this);
    }

    private void initListener() {
        //设置监测地图画面的移动的监听
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            /**
             * 移动地图结束的回调
             */
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                LogUtil.d("当前选中位置的经度:" + cameraPosition.target.latitude + "||纬度:" + cameraPosition.target.longitude);
                TrackBean trackBean = new TrackBean(cameraPosition.target.latitude, cameraPosition.target.longitude);
                trackBean.save();
                //移动时候光标的缩放
                startTransAnimator();
                getAddressInfoByLatLong(cameraPosition.target.latitude, cameraPosition.target.longitude);
                poiSearch(cameraPosition.target.longitude, cameraPosition.target.latitude, 2000);
            }

            /**
             * 设置监测地图画面的移动的监听
             */
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

        });
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        location = aMapLocation;
                        //定位成功回调信息，设置相关消息
                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        aMapLocation.getLatitude();//获取纬度
                        aMapLocation.getLongitude();//获取经度
                        aMapLocation.getAccuracy();//获取精度信息
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        df.format(date);//定位时间
                        aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        aMapLocation.getCountry();//国家信息
                        aMapLocation.getProvince();//省信息
                        aMapLocation.getCity();//城市信息
                        aMapLocation.getDistrict();//城区信息
                        aMapLocation.getStreet();//街道信息
                        aMapLocation.getStreetNum();//街道门牌号信息
                        aMapLocation.getCityCode();//城市编码
                        aMapLocation.getAdCode();//地区编码
                        aMapLocation.getAoiName();//获取当前定位点的AOI信息
                        // 设置当前地图显示为当前位置
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 19));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                        markerOptions.title("当前位置");
                        markerOptions.visible(true);
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_nowweizhi));
                        markerOptions.icon(bitmapDescriptor);
                        aMap.addMarker(markerOptions);
                        aMapLocation.getProvince();//省信息
                        aMapLocation.getCity();//城市信息
                        aMapLocation.getDistrict();//城区信息
                        LogUtil.d("pcw", "lat : " + aMapLocation.getLatitude() + " lon : " + aMapLocation.getLongitude() + aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict());
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        LogUtil.d("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }

            }
        });

        etSeek.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.e("输入前确认执行该方法", "开始输入");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doSearchQuery(etSeek.getText().toString(), location.getCity());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.e("输入结束执行该方法", "输入结束");
            }
        });

        etSeek.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) {
                    doSearchQuery(etSeek.getText().toString(), location.getCity());
                }
                return false;
            }
        });
        //监听软键盘是否弹出
        etSeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvCancel.setVisibility(View.VISIBLE);
                rlMap.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {
        mTransAnimator = ObjectAnimator.ofFloat(ivCenterLocation, "translationY", 0f, -80f, 0f);
        mTransAnimator.setDuration(800);
    }

    /**
     * 初始化地图和定位
     */
    private void initMap() {
        aMap = map.getMap();
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);//是否显示地图中放大缩小按钮
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        mUiSettings.setScaleControlsEnabled(true);//是否显示缩放级别
        aMap.setMyLocationEnabled(false);// 是否可触发定位并显示定位层
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
//        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    /**
     * 通过经纬度获取当前地址详细信息，逆地址编码
     *
     * @param latitude
     * @param longitude
     */
    private void getAddressInfoByLatLong(double latitude, double longitude) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        /*
        point - 要进行逆地理编码的地理坐标点。
        radius - 查找范围。默认值为1000，取值范围1-3000，单位米。
        latLonType - 输入参数坐标类型。包含GPS坐标和高德坐标。 可以参考RegeocodeQuery.setLatLonType(String)
        */
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 3000, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (i == 1000) {
                    //获取当前地图标记的地点
                    LogUtil.d("移动地图的标记" + regeocodeResult.getRegeocodeAddress().getProvince() +
                            regeocodeResult.getRegeocodeAddress().getCity()
                            + regeocodeResult.getRegeocodeAddress().getDistrict() +
                            regeocodeResult.getRegeocodeAddress().getStreetNumber()
                    );
                    location.setProvince(regeocodeResult.getRegeocodeAddress().getProvince());
                    location.setCity(regeocodeResult.getRegeocodeAddress().getCity());
                    location.setDistrict(regeocodeResult.getRegeocodeAddress().getDistrict());
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            }
        });
    }

    /**
     * 关键词搜索搜索
     */
    protected void doSearchQuery(String keyWord, String city) {
        PoiSearch.Query query = new PoiSearch.Query(keyWord, "", city);
        query.setPageSize(30);
        query.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }


    /**
     * 移动动画
     */
    private void startTransAnimator() {
        if (null != mTransAnimator && !mTransAnimator.isRunning()) {
            mTransAnimator.start();
        }
    }


    /**
     * 周边搜索
     */
    private void poiSearch(double longitude, double latitude, int distances) {
        LatLonPoint point = new LatLonPoint(latitude, longitude);
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(point, distances, geocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
                LogUtil.d(rCode + "");
                if (1000 == rCode) {
                    RegeocodeAddress address = regeocodeResult.getRegeocodeAddress();
                    StringBuffer stringBuffer = new StringBuffer();
                    String area = address.getProvince();//省或直辖市
                    String subLoc = address.getDistrict();//区或县或县级市
                    mPoiList.clear();
                    mPoiList = address.getPois();//获取周围兴趣点
                    LogUtil.d("地区=" + area);
                    LogUtil.d("pois搜索结果" + mPoiList.toString() + "数据长度" + mPoiList.size());
                    isSearch = false;
                    initRecyclerview();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
                LogUtil.d(rCode + "");
                LogUtil.d(geocodeResult.getGeocodeAddressList().toString());
                LogUtil.d(geocodeResult.getGeocodeQuery().toString());
            }
        });
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            mPoiList.clear();
            mPoiList = poiResult.getPois();
            isSearch = true;
            LogUtil.d("关键词pois搜索结果" + mPoiList.toString());
            LogUtil.d("关键词搜索结果省市区" +
                    mPoiList.get(0).getProvinceName() +
                    mPoiList.get(0).getCityName() +
                    mPoiList.get(0).getDirection());
            initRecyclerview();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @OnClick({R.id.iv_location, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_location:
                // 启动定位
                mLocationClient.startLocation();
                break;
            case R.id.tv_cancel:
                rlMap.setVisibility(View.VISIBLE);
                tvCancel.setVisibility(View.GONE);
                etSeek.getText().clear();
                hideSoftKeyboard(this);
                // 启动定位
                mLocationClient.startLocation();
                break;
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
}

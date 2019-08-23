package com.androidlmy.mapselectaddress;

/**
 * author: Liming
 * Date: 2019/8/22 15:42
 * Created by Android Studio.
 */
public class MapToAdressEventBus {
    private String province;
    private String address;

    public MapToAdressEventBus(String province, String address) {
        this.province = province;
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

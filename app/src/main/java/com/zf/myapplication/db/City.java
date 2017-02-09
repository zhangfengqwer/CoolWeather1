package com.zf.myapplication.db;

import org.litepal.crud.DataSupport;

import static android.R.attr.id;

/**
 * Created by Administrator on 2017/2/9.
 */

public class City extends DataSupport {
    private int cityId;
    private String cityName;
    private int provinceId;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}

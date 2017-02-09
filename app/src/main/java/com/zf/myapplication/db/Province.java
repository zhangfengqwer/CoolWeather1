package com.zf.myapplication.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/2/9.
 */

public class Province extends DataSupport{
    private String provinceName;
    private int provinceId;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}

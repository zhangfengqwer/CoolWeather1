package com.zf.myapplication.util;

import android.text.TextUtils;

import com.zf.myapplication.db.City;
import com.zf.myapplication.db.Province;
import com.zf.myapplication.db.County;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/2/9.
 */

public class Utility {
    /**
     * 解析返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty((response))) {
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject jsonObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceId(jsonObject.getInt("id"));
                    province.setProvinceName(jsonObject.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析返回的市级信息
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty((response))) {
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject jsonObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityId(jsonObject.getInt("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析返回的县级数据
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty((response))) {
            try {
                JSONArray allCounty = new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject jsonObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyId(jsonObject.getInt("id"));
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}

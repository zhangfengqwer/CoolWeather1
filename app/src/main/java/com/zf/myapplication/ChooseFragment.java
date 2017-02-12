package com.zf.myapplication;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zf.myapplication.common.CommonURL;
import com.zf.myapplication.db.City;
import com.zf.myapplication.db.County;
import com.zf.myapplication.db.Province;
import com.zf.myapplication.util.HttpUtil;
import com.zf.myapplication.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/9.
 */

public class ChooseFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;


    private TextView title_text;
    private Button backButton;
    private ListView listView;
    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int currentLevel;

    private List<Province> provincesList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        title_text = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_btn);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provincesList.get(position);
                    queryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounty();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCity();
                }

            }
        });
        queryProvince();

    }

    /**
     * 查询全国所有省，先从数据库中找，找不到再从网上找
     */
    private void queryProvince() {
        title_text.setText("中国");
        backButton.setVisibility(View.GONE);
        provincesList = DataSupport.findAll(Province.class);

        if (provincesList.size() > 0) {
            dataList.clear();
            for (Province province : provincesList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = CommonURL.SERVER_URL;
            queryFromServer(address, "province");
        }

    }

    /**
     * 查询省内所有城市，先从数据库中找，找不到再从网上找
     */
    private void queryCity() {
        title_text.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.getProvinceId())).find(City.class);

        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceId = selectedProvince.getProvinceId();
            String address = CommonURL.SERVER_URL +"/"+ provinceId;
            queryFromServer(address, "city");
        }

    }

    /**
     * 查询市内所有县，先从数据库中找，找不到再从网上找
     */
    private void queryCounty() {
        title_text.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?", String.valueOf(selectedCity.getCityId())).find(County.class);

        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceId = selectedProvince.getProvinceId();
            int cityId = selectedCity.getCityId();
            String address = CommonURL.SERVER_URL +"/"+ provinceId +"/"+ cityId;
            queryFromServer(address, "county");
        }

    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();

        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                closeProgressDialog();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                boolean flag = false;
                if ("province".equals(type)) {
                    flag = Utility.handleProvinceResponse(result);
                } else if ("city".equals(type)) {
                    flag = Utility.handleCityResponse(result, selectedProvince.getProvinceId());
                } else if ("county".equals(type)) {
                    flag = Utility.handleCountyResponse(result, selectedCity.getCityId());
                }

                if (flag) {
                    closeProgressDialog();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                queryCity();
                            }else if ("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


}

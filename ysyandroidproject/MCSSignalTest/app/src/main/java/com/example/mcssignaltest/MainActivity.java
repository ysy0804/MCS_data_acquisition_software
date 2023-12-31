package com.example.mcssignaltest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.app.Application;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import com.baidu.location.Address;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {           //AppCompatActivity是 Activity 类的一个子类,提供了对旧版本 Android 平台的兼容性支持
    public LocationClient mLocationClient= null;
    private MyLocationListener myListener = new MyLocationListener();     //创建一个定位监听器类对象
    private MapView mMapView = null;        //百度自定义地图控件
    private BaiduMap baiduMap;              //地图总控制器
    private boolean isFirstLocate = true;     //是否是首次定位
    private TextView positionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //定位监视器
        super.onCreate(savedInstanceState);
        //隐私保护，必须写，不然会报错
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        LocationClient.setAgreePrivacy(true);
        //注册LocationListener监听器
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLocationClient.registerLocationListener(myListener);



        SDKInitializer.initialize(getApplicationContext());//一定要先初始化，再加载布局

        SDKInitializer.setCoordType(CoordType.BD09LL);    //经纬坐标，使用中国国测局的。

        setContentView(R.layout.activity_main);  //加载布局


        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mMapView.getMap();

        positionText = (TextView) findViewById(R.id.position_text_view);
        List<String> permissionList = new ArrayList<>();//权限列表，记录未允许的权限

        baiduMap.setMyLocationEnabled(true);     //开启地图的定位图层


        //判断单个权限是否已经允许
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();      //请求位置信息
        }
    }

    //定位请求方法
    private void requestLocation() {
        mLocationClient.start();       //开始定位，回调定位监听器
        initMyLocation();         //调用本地定位方法
    }


    //权限判断
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    //通过继承抽象类BDAbstractListener并重写其onReceieveLocation方法来获取定位数据，并将其传给MapView。
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            int locType = location.getLocType();
            Log.d("定位结果错误码", String.valueOf(locType));
            //mapView 销毁后不再处理新接收的位置
            if (location == null || mMapView == null) {             //判断 location 和 mMapView 是否为空来确保在地图视图销毁后不再处理新接收的位置
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()       //通过 Builder 模式，设置位置的精度、方向、纬度和经度等属性。
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            baiduMap.setMyLocationData(locData);                //将新的位置信息更新到地图上，以显示当前设备的位置

            if (isFirstLocate) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());      //根据 BDLocation 对象的经纬度创建一个 LatLng 对象
                System.out.println("纬度"+location.getLatitude());
                System.out.println("经度"+location.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);          //将地图的中心点设置为 ll 所表示的位置
                baiduMap.animateMapStatus(update);                          //动画更新中心点
                update = MapStatusUpdateFactory.zoomTo(16f);        //将地图的缩放级别设置为 16
                baiduMap.animateMapStatus(update);              //动画更新缩放级别
                isFirstLocate = false;
            }

        }
    }

    //通过LocationClient发起定位
    private void initMyLocation() {
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        //强制选择高精度定位，三种模式:Hight_Accuracy(高精度）、Battery_Saving(节电模式）、Device_Sensors(传感器模式）
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);           //设置位置更新间隔，1s一更新

        //设置locationClientOption
        mLocationClient.setLocOption(option);

   }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();         //程序销毁时停止定位，防止消耗电量
        baiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    }
}


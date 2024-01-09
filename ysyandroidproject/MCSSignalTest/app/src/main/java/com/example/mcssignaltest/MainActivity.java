package com.example.mcssignaltest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.mcssignaltest.databinding.ActivityMainBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.animation.ObjectAnimator;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {           //AppCompatActivity是 Activity 类的一个子类,提供了对旧版本 Android 平台的兼容性支持
    private ActivityMainBinding binding;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();     //创建一个定位监听器类对象
    private MapView mMapView = null;        //百度自定义地图控件
    private BaiduMap baiduMap;              //地图总控制器
    private boolean isFirstLocate = true;     //是否是首次定位
    private TextView positionText;
    private TextView ShowLatitude;    //打印纬度值
    private TextView ShowLongtitude;    //打印经度值
    private TextView ShowCellSignalStrength;    //打印蜂窝网络信号强度值
    private TextView ShowEquipMessage;    //打印经度值
    private double lastX = 0.0;
    private int laststep;


    // 实例化 MyOrientationListener
    private MyOrientationListener myOrientationListener = new MyOrientationListener();
    //实例化MySTEPListener
//    private MySTEPListener mySTEPListener = new MySTEPListener();
    public float mCurrentDirection;    //此刻的方向
    public float mCurrentAccracy;      //此刻的精度
    public double mCurrentLantitude;  //此刻的纬度值
    public double mCurrentLongtitude;  //此刻的经度值
    private float AverageTime;  //连通性时延


    private MaterialCardView cardView;    //卡片对象，用于引用卡片视图
    private int cardHeight;
    private int screenHeight;

    private float startY;
    private int initialCardTop;
    private boolean isCardVisible = true;
    private DrawerLayout mDrawerLayout;    //滑动菜单布局


    private SignalStrength signalStrength;
    private TelephonyManager telephonyManager;
//    private SensorManager SensorManager;        //传感器对象
//    private Sensor Sensor;
    private static final float THRESHOLD = 10f; // 步行动作的阈值
    private int stepCount = 0; // 步数计数器
    private PhoneStateListener mListener;           //手机状态监听对象
    private final static String LTE_TAG = "LTE_Tag";
    private final static String LTE_SIGNAL_STRENGTH = "getCellSignalStrengths";
    private Handler handler;       //定时器
    private Runnable runnable;
    private final long INTERVAL = 3000; // 每隔3秒获取一次信号强度值



    private static final String PING_COMMAND = "/system/bin/ping";
    private static final int PING_TIMEOUT = 5; // Ping超时时间，单位为秒
    private static final int PING_COUNT = 5;   // Ping次数

    private boolean isTime = true;
    private int rsrpValue;              //网络信号强度
    private Timer timer;
    private Timer timerisStopStep;     //判断是否停驻的计时器
    private TimerTask task;
    private TimerTask taskisSopStep;
    private int collectsignaltimes=0;     //从程序运行开始采集信号的次数
    private int DurCollectSignal = 10000;           //两次采集信号的时间间隔
    private boolean isStopStep = false;


    @RequiresApi(api = Build.VERSION_CODES.O)
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

        //绑定
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainActivity(this);


        //标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //滑动菜单布局
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }


        //浮动按钮返回当前位置
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isFirstLocate = true;     //是否是首次定位
            }
        });

        //获取卡片引用
        cardView = (MaterialCardView) findViewById(R.id.card_view);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        //撤去百度地图logo
        mMapView.removeViewAt(1);

        //获取地图引用
        baiduMap = mMapView.getMap();


        ShowLatitude = findViewById(R.id.latitude);
        ShowLongtitude = findViewById(R.id.longtitude);
        ShowCellSignalStrength = findViewById(R.id.CellSignal);


        ShowEquipMessage = findViewById(R.id.EquipMessList);

        float lineSpacingExtra = 10f;  // 行间距的像素值
        float lineSpacingMultiplier = 1.2f;  // 行间距的倍数
        ShowEquipMessage.setLineSpacing(lineSpacingExtra, lineSpacingMultiplier);
        positionText = (TextView) findViewById(R.id.position_text_view);

        List<String> permissionList = new ArrayList<>();//权限列表，记录未允许的权限


        //设置locationClientOption

        baiduMap.setMyLocationEnabled(true);     //开启地图的定位图层


        // 创建 MyLocationConfiguration 对象并设置相关属性,enableDirection=true则允许显示方向
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, // 定位模式为普通态
                true, // 显示方向信息
                null,
                0xAAec2d7a, // 填充颜色
                0xAA2376b7 // 描边颜色
        );
        baiduMap.setMyLocationConfiguration(config);


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


        //卡片属性动画，滑动隐藏
        cardHeight = cardView.getHeight();
        screenHeight = getResources().getDisplayMetrics().heightPixels;


        cardView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 记录手指按下时的初始坐标
                        startY = event.getRawY();
                        initialCardTop = cardView.getTop();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float deltaYUp = event.getRawY() - startY;
                        float touchSlop = ViewConfiguration.get(getApplicationContext()).getScaledTouchSlop();
                        if (Math.abs(deltaYUp) >= touchSlop) {
                            if (isCardVisible) {
                                if (deltaYUp >= cardHeight / 2) {
                                    hideCard();
                                } else {
                                    showCard();
                                }
                            } else {
                                if (deltaYUp <= -cardHeight / 2) {
                                    showCard();
                                } else {
                                    hideCard();
                                }
                            }
                        }
                        return true;
                }
                return false;
            }
        });


        //启动设备状态监听
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);



        getDeviceInfo();

        //监听蜂窝网络信号强度
        mListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength sStrength) {
                signalStrength = sStrength;
                getLTEsignalStrength();
            }

        };

        //注册监听器
        telephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        handler = new Handler(Looper.getMainLooper());

//        mySTEPListener.registerSensorListener();
        StrengthCollection();


    }

    //获取设备蜂窝网络信号强度值
    private int getLTEsignalStrength() {
        try {
            Method[] methods = android.telephony.SignalStrength.class.getMethods();
            for (Method mthd : methods) {

                if (mthd.getName().equals(LTE_SIGNAL_STRENGTH)) {
                    Object result = mthd.invoke(signalStrength);
                    if (result instanceof List) {
                        List<?> signalStrengthList = (List<?>) result;
                        for (Object obj : signalStrengthList) {
                            if (obj instanceof android.telephony.CellSignalStrengthLte) {
                                android.telephony.CellSignalStrengthLte lteSignalStrength = (android.telephony.CellSignalStrengthLte) obj;

                                Method getRsrpMethod = lteSignalStrength.getClass().getMethod("getRsrp");
                                rsrpValue = (int) getRsrpMethod.invoke(lteSignalStrength);
                                ShowCellSignalStrength.setText(String.valueOf(rsrpValue) + "dBm");


//                                //定义Maker坐标点
//                                LatLng point = new LatLng(mCurrentLantitude, mCurrentLongtitude);
//                                //构建Marker图标
//
//
//                                if (rsrpValue > -80) {
//                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
//                                            .fromResource(R.drawable.location_on_1);
//                                    //构建MarkerOption，用于在地图上添加Marker
//                                    OverlayOptions option = new MarkerOptions()
//                                            .position(point)
//                                            .icon(bitmap);
//                                    //在地图上添加Marker，并显示
//                                    baiduMap.addOverlay(option);
//                                } else if (rsrpValue > -90 && rsrpValue <= -80) {
//                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
//                                            .fromResource(R.drawable.location_on_3);
//                                    //构建MarkerOption，用于在地图上添加Marker
//                                    OverlayOptions option = new MarkerOptions()
//                                            .position(point)
//                                            .icon(bitmap);
//                                    //在地图上添加Marker，并显示
//                                    baiduMap.addOverlay(option);
//                                } else if (rsrpValue > -100 && rsrpValue <= -90) {
//                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
//                                            .fromResource(R.drawable.location_on_2);
//                                    //构建MarkerOption，用于在地图上添加Marker
//                                    OverlayOptions option = new MarkerOptions()
//                                            .position(point)
//                                            .icon(bitmap);
//                                    //在地图上添加Marker，并显示
//                                    baiduMap.addOverlay(option);
//                                } else if (rsrpValue <= -100) {
//                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
//                                            .fromResource(R.drawable.location_on_4);
//                                    //构建MarkerOption，用于在地图上添加Marker
//                                    OverlayOptions option = new MarkerOptions()
//                                            .position(point)
//                                            .icon(bitmap);
//                                    //在地图上添加Marker，并显示
//                                    baiduMap.addOverlay(option);
//                                }
                            }
                        }

                    }
                    return rsrpValue;
                }
            }
        } catch (Exception e) {
            Log.e(LTE_TAG, "Exception: " + e.toString());
        }

        return 0;
    }


    //采样
    private void StrengthCollection(){
        timer = new Timer();
        task = new TimerTask() {
                           @Override
                           public void run() {
                               // 获取网络信号强度
                            int mCurrentSign = getLTEsignalStrength();
                                collectsignaltimes++;
                                System.out.println("采集次数"+collectsignaltimes);
                               updateSignalMarker(mCurrentSign);     //在地图上标记
                                }
                           };
        timer.schedule( task, 0,  10000);   //启动任务，初始时间间隔为1s


        //采样频率自适应调整
        timerisStopStep = new Timer();
        taskisSopStep = new TimerTask() {
            @Override
            public void run() {
                    if (Math.abs(stepCount - laststep) < 20) {      //如果一定时间间隔步数少于20
                        DurCollectSignal *= 3;                    //采样频率降低
                        task.cancel();                          //注销任务
                        task = new TimerTask() {
                            @Override
                            public void run() {
                                // 获取网络信号强度
                                int mCurrentSign = getLTEsignalStrength();
                                collectsignaltimes++;
                                System.out.println("采集次数"+collectsignaltimes);
                                updateSignalMarker(mCurrentSign);
                            }
                        };
                        timer.schedule(task,0,DurCollectSignal);             //重新启动任务，时间间隔调整为新频率
                        System.out.println("时间间隔" + DurCollectSignal);
                    }
                    System.out.println("时间间隔" + DurCollectSignal + "   " + laststep + "   " + stepCount);
                    // 将当前步数记录为上一次的值，用于下一次比较
                    laststep = stepCount;
                    if(Math.abs(stepCount - laststep) >= 20){                               //如果步数大于20步
                        DurCollectSignal /= (1+Math.abs(stepCount - laststep) / 100);     //根据行走速度提高采样频率
                    }
                }

        };

        timerisStopStep.schedule(taskisSopStep,20000,  60000);    //每隔1min调整一次，在启动程序的20s后开始运行，因为要给采集初始步数留时间
    }


    //信号强度标记
    private void updateSignalMarker(int MCurrentSign){
        handler.post(new Runnable() {
            @Override
            public void run() {
                                //定义Maker坐标点
                                LatLng point = new LatLng(mCurrentLantitude, mCurrentLongtitude);
                                //构建Marker图标

                                if (MCurrentSign <= -50 && MCurrentSign >= -70) {
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                                            .fromResource(R.drawable.location_on_1);
                                    //构建MarkerOption，用于在地图上添加Marker
                                    OverlayOptions option = new MarkerOptions()
                                            .position(point)
                                            .icon(bitmap);
                                    //在地图上添加Marker，并显示
                                    baiduMap.addOverlay(option);
                                } else if (MCurrentSign < -70 && MCurrentSign >= -85) {
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                                            .fromResource(R.drawable.location_on_3);
                                    //构建MarkerOption，用于在地图上添加Marker
                                    OverlayOptions option = new MarkerOptions()
                                            .position(point)
                                            .icon(bitmap);
                                    //在地图上添加Marker，并显示
                                    baiduMap.addOverlay(option);
                                } else if (MCurrentSign < -85 && MCurrentSign >= -100) {
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                                            .fromResource(R.drawable.location_on_2);
                                    //构建MarkerOption，用于在地图上添加Marker
                                    OverlayOptions option = new MarkerOptions()
                                            .position(point)
                                            .icon(bitmap);
                                    //在地图上添加Marker，并显示
                                    baiduMap.addOverlay(option);
                                } else if (MCurrentSign < -100) {
                                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                                            .fromResource(R.drawable.location_on_4);
                                    //构建MarkerOption，用于在地图上添加Marker
                                    OverlayOptions option = new MarkerOptions()
                                            .position(point)
                                            .icon(bitmap);
                                    //在地图上添加Marker，并显示
                                    baiduMap.addOverlay(option);
                                }
            }

        });
    }





    //设备信息与网络信息提取
    private void getDeviceInfo() {
        // 设备运营商名称
        String networkOperatorName = telephonyManager.getNetworkOperatorName();
        System.out.println("运营商名称: " + networkOperatorName);

        // 设备品牌
        String deviceBrand = android.os.Build.BRAND;
        System.out.println("设备品牌名称: " + deviceBrand);

        // 设备型号
        String deviceModel = android.os.Build.MODEL;
        System.out.println("设备型号: " + deviceModel);

        // 网络类型
        String networkType = "";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_IDEN) {
            networkType = "2G";
        } else if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPAP) {
            networkType = "3G";
        } else if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
            networkType = "4G";
        }else if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_NR) {
            networkType = "5G";
        }else  {
            System.out.println("未知");
        }
            System.out.println("网络类型: "+ networkType);


        // SIM卡国别
        String simCountry = telephonyManager.getSimCountryIso();
        System.out.println("SIM卡国别: " + simCountry);


        // SIM卡当前状态
        String deviceStatus = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY ? "Ready" : "Not Ready";
        System.out.println("SIM卡当前状态: " + deviceStatus);

        ShowEquipMessage.setText("运营商名称: " + networkOperatorName+'\n'+"设备品牌名称: " + deviceBrand+'\n'+"设备型号: " + deviceModel
        +'\n'+"网络类型: "+ networkType+'\n'+"SIM卡国别: " + simCountry+'\n'+"SIM卡当前状态: " + deviceStatus);
    }

    //标题栏按钮触发事件
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public boolean  onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Ping连通性测试模块
            case R.id.backup :
                PingTask pingTask = new PingTask("www.baidu.com", new PingTask.PingCallback() {
                    @Override
                    public void onPingResult(boolean isSuccessful, float averageTime) {
                        if (isSuccessful) {
                            System.out.println("连接成功，平均时延: " + averageTime + "ms");
                            AverageTime = averageTime;
                        } else {
                            System.out.println("连接失败");
                        }
                    }
                });
                pingTask.execute();
                Toast.makeText(this,"连接成功，平均时延: " + AverageTime + "ms", Toast.LENGTH_SHORT).show();
                break;
                //滑动菜单
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }


    //卡片隐藏动画
    private void hideCard() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(cardView, "translationY", 0, 600);
        animator.setDuration(300);
        animator.start();
        isCardVisible = false;
    }

    //卡片弹出动画
    private void showCard() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(cardView, "translationY", 600, 0);
        animator.setDuration(200);
        animator.start();
        isCardVisible = true;
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
        //开始传感器监听
        myOrientationListener.registerSensorListener();

        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //终止传感器监听
        myOrientationListener.unregisterSensorListener();
        handler.removeCallbacks(runnable);
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }



    //通过继承抽象类BDAbstractListener并重写其onReceieveLocation方法来获取定位数据，并将其传给MapView。
    public class MyLocationListener extends BDAbstractLocationListener  {
        @Override
        public void onReceiveLocation(BDLocation location) {
            int locType = location.getLocType();
            Log.d("定位结果错误码", String.valueOf(locType));
            //mapView 销毁后不再处理新接收的位置
            if (location == null || mMapView == null) {             //判断 location 和 mMapView 是否为空来确保在地图视图销毁后不再处理新接收的位置
                return;
            }
            mCurrentLongtitude = location.getLongitude();
            mCurrentLantitude = location.getLatitude();
            mCurrentAccracy = location.getRadius();

            //在卡片上显示经纬度值
            ShowLatitude.setText(String.format("%.2f",mCurrentLantitude));
            ShowLongtitude.setText(String.format("%.2f",mCurrentLongtitude));

            MyLocationData locData = new MyLocationData.Builder()       //通过 Builder 模式，设置位置的精度、方向、纬度和经度等属性。
                    .accuracy(location.getRadius())
                    // 从传感器拿到方向信息
                    .direction(mCurrentDirection)
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



//    public  class MySTEPListener implements SensorEventListener {
//        private SensorManager StepSensorManager;        //传感器对象
//        //注册传感器监听器方法
//        public void registerSensorListener() {
//            StepSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//            StepSensorManager.registerListener(this, StepSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
//                    StepSensorManager.SENSOR_DELAY_NORMAL);
//        }
//        // 关闭传感器监听器的方法
//        public void unregisterSensorListener(){
//            StepSensorManager.unregisterListener(this);
//        }
//        public void onSensorChanged(SensorEvent Event) {
//            // 步行动作的触发事件发生一次，步数计数器增加一步
//            System.out.println("步数：" +Event.sensor.getType());
//            if (Event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
//                stepCount++;
//                System.out.println("步数：" + stepCount);
//            }
//        }
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            // 传感器精度变化时的回调方法
//        }
//    }




    //自定义传感器监听器类
    public  class MyOrientationListener implements SensorEventListener{
        private SensorManager sensorManager;


        //注册传感器监听器方法
        public void registerSensorListener() {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                    SensorManager.SENSOR_DELAY_FASTEST);

        }


        // 关闭传感器监听器的方法
        public void unregisterSensorListener() {
            sensorManager.unregisterListener(this);
        }

        // 监听传感器变化事件，当传感器数值发生变化时，该方法被调用
        public void onSensorChanged(SensorEvent sensorEvent) {

            if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                // 获取计步器传感器报告的总步数
                System.out.println(sensorEvent.values[0]);
                int totalSteps = (int) sensorEvent.values[0];

                // 更新步数计数器的值
                stepCount = totalSteps;

                // 更新步数显示
                System.out.println("步数：" + stepCount);
            }
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                // 读取传感器数值中的 x 轴方向的值
                double x = sensorEvent.values[SensorManager.DATA_X];
                // 判断当前 x 轴方向的值与上一次记录的值之间的差值是否超过了 1.0，如果超过了 1.0，更新当前方向为新的 x 轴方向的值。
                if (Math.abs(x - lastX) > 1.0) {
                    mCurrentDirection = (float) x;
                    // 构造定位图层数据
                    MyLocationData myLocationData = new MyLocationData.Builder()
                            .accuracy(mCurrentAccracy)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(mCurrentDirection)
                            .latitude(mCurrentLantitude)
                            .longitude(mCurrentLongtitude).build();
                    // 设置定位图层数据
                    baiduMap.setMyLocationData(myLocationData);
                }
                // 将当前 x 轴方向的值记录为上一次的值，用于下一次比较
                lastX = x;
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // 传感器精度变化时的回调方法
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
        option.setScanSpan(4000);           //设置位置更新间隔，1s一更新

        mLocationClient.setLocOption(option);


   }


    //Ping命令测试
    private static class PingTask extends AsyncTask<Void, Void, Boolean> {
        private String host;
        private PingCallback callback;

        public PingTask(String host, PingCallback callback) {
            this.host = host;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        PING_COMMAND,
                        "-c",
                        String.valueOf(PING_COUNT),
                        "-W",
                        String.valueOf(PING_TIMEOUT),
                        host
                );


                // 启动进程执行 ping 命令
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                int receivedCount = 0;
                int lostCount = 0;
                float totalTime = 0;



                // 逐行读取命令输出
                while ((line = reader.readLine()) != null) {
                    if (line.contains("icmp_seq")) {
                        // 解析包含时间信息的行
                        int startIndex = line.indexOf("time=");
                        int endIndex = line.indexOf("ms");
                        if (startIndex != -1 && endIndex != -1) {
                            // 提取时间字符串并转换为浮点数
                            String timeString = line.substring(startIndex + 5, endIndex).trim();
                            float time = Float.parseFloat(timeString);
                            totalTime += time;
                            receivedCount++;
                        } else {
                            lostCount++;
                        }
                    }
                }

                reader.close();
                process.waitFor();

                if (receivedCount > 0) {
                    // 计算平均时间
                    float averageTime = totalTime / receivedCount;
                    // 回调通知 Ping 结果
                    callback.onPingResult(true, averageTime);
                    return true;
                } else {
                    callback.onPingResult(false, 0);
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            callback.onPingResult(false, 0);
            return false;
        }

        public interface PingCallback {
            void onPingResult(boolean isSuccessful, float averageTime);
        }
    }



    @Override
    protected void onDestroy() {
        mLocationClient.stop();         //程序销毁时停止定位，防止消耗电量
        baiduMap.setMyLocationEnabled(false);
//        mySTEPListener.unregisterSensorListener();
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    }
}


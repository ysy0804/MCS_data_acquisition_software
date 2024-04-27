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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.os.PowerManager;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
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
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.mcssignaltest.databinding.ActivityMainBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import android.animation.ObjectAnimator;


import org.json.JSONException;
import org.json.JSONObject;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {           //AppCompatActivity是 Activity 类的一个子类,提供了对旧版本 Android 平台的兼容性支持
    private ActivityMainBinding binding;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();     //创建一个定位监听器类对象
    private WebSocketClient WebClient = new WebSocketClient();
    // private AndroidWebSocketServer WebServer = new AndroidWebSocketServer();
    private MapView mMapView = null;        //百度自定义地图控件
    private BaiduMap baiduMap;              //地图总控制器
    private boolean isFirstLocate = true;     //是否是首次定位
    private TextView positionText;
    private TextView ShowLatitude;    //打印纬度值
    private TextView ShowLongtitude;    //打印经度值
    private TextView ShowCellSignalStrength;    //打印蜂窝网络信号强度值
    private MaterialCardView ShowEquipMessageMenu;    //菜单
    private TextView ShowEquipMessage;    //打印经度值
    private NavigationView ShowEquip;
    private double lastX = 0.0;
    private int laststep;
    private int isStartLocate = 0;
    private PowerManager.WakeLock wakeLock;


    // 实例化 MyOrientationListener
    private MyOrientationListener myOrientationListener = new MyOrientationListener();


    //实例化MySTEPListener
//    private MySTEPListener mySTEPListener = new MySTEPListener();
    public float mCurrentDirection;    //此刻的方向
    public float mCurrentAccracy;      //此刻的精度
    public double mCurrentLantitude;  //此刻的纬度值
    public double mCurrentLongtitude;  //此刻的经度值
    private double previousLatitude;   //上次纬度值
    private double previousLongitude;   //上次经度值
    private double Upload_INTERVAL = Pass_INTERVAL;    //上传服务器时间间隔
    private double last_D;    //上次位移
    private float AverageTime;  //连通性时延
    private String networkOperatorName;
    private String deviceBrand;
    private String deviceModel;
    private String networkType = "";
    private String simCountry;
    private String deviceStatus;
    private String User;   //区分上传数据用户
    private String Time;   //上传数据时间
    private String isSucess; //是否上传成功
    private String dataCache; //是否上传成功


    private MaterialCardView cardView;    //卡片对象，用于引用卡片视图
    private int cardHeight;
    private int screenHeight;

    private float startY;
    private int initialCardTop;
    private int resumeCount = 0;     //第几次获得焦点
    private int NetworkType;
    private int lastIntervalstep = 0;  //上一段步数
    private boolean isCardVisible = true;
    private boolean isStartRun=true;    //算法是否刚启动
    private DrawerLayout mDrawerLayout;    //滑动菜单布局


    private SignalStrength signalStrength;
    private TelephonyManager telephonyManager;
    private SharedPreferences sharedPreferences;
    //    private SensorManager SensorManager;        //传感器对象
//    private Sensor Sensor;
    private static final float THRESHOLD = 10f; // 步行动作的阈值
    private int stepCount = 0; // 步数计数器
    private PhoneStateListener mListener;           //手机状态监听对象
    private final static String LTE_TAG = "LTE_Tag";
    private final static String LTE_SIGNAL_STRENGTH = "getCellSignalStrengths";

    private Handler handler;       //定时器
    private Runnable runnable;
    private Handler upload_handler;
    private Handler locate_handler;
    private Handler Resend_handler;   //定时检查缓存
    private Runnable retryRunnable;  //定时发送缓存数据

    private Runnable sendDataRunnable;
    private final long INTERVAL = 3000; // 每隔3秒获取一次信号强度值
    private static final double Pass_INTERVAL = 5 * 1000; // 上传数据的时间间隔为30秒
    private static final int Pass_DELAY = 20 * 1000; // 启动app20秒后开始上传


    private static final String PING_COMMAND = "/system/bin/ping";
    private static final int PING_TIMEOUT = 5; // Ping超时时间，单位为秒
    private static final int PING_COUNT = 2;   // Ping次数
    private static final double EARTH_RADIUS = 6371.0; // 地球半径（千米）

    private Queue<Integer> signalStrengthQueue;    //异常值检测算法信号强度值滑动窗口
    private double SD;    //滑动窗口标准差,异常值偏移值
    private int WINDOW_Capacity=10;   //滑动窗口容量
    private int initialData = 0; //初始收集的信号强度数据数量
    private double PredictRsrp;   //经过指数平均滑动算法预测的信号强度值
    private double Rsrp_Weight = 0.40;  //指数均值滑动法权重
    private boolean isinitial = true;   //是否是初始收集数据计算基准值
    private int NotNormal=0;   //不是异常值累计

    private boolean isTime = true;
    private boolean isStartSend = false;     //是否开始上传数据
    private boolean isWebsocketOn = false;  //是否连接websocket
    private boolean isServerReceived = true;  //服务器是否收到
    private boolean isrequestdata = true;  //是否是请求数据库数据
    private int rsrpValue;              //网络信号强度
    private int rsrqValue;              //网络信号质量
    private int rssnrValue;              //信噪比
    private int CellCID;          //基站CID
    private int operator;          //运营商代号
    private int stopTime = 1;      //用户停止不动的时间段数
    private Timer timer;
    private Timer timerisStopStep;     //判断是否停驻的计时器
    private TimerTask task;
    private TimerTask taskisSopStep;
    private int collectsignaltimes = 0;     //从程序运行开始采集信号的次数
    private int DurCollectSignal = 10000;           //两次采集信号的时间间隔
    private boolean isStopStep = false;
    private static final String DJANGO_API_URL = "http://39.101.76.7:8000/receive_data/";

    public MainActivity() {
    }


    @SuppressLint("InvalidWakeLockTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //定位监视器
        super.onCreate(savedInstanceState);
        //隐私保护，必须写，不然会报错
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        LocationClient.setAgreePrivacy(true);

        //息屏
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "UploadLock");


        //注册定位监听器
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.registerLocationListener(myListener);

        //百度地图SDK配置，不用动
        SDKInitializer.initialize(getApplicationContext());//一定要先初始化，再加载布局

        SDKInitializer.setCoordType(CoordType.BD09LL);    //经纬坐标，使用中国国测局的。

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);  //加载布局


        //绑定
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainActivity(this);


        //隐藏标题栏，部分型号手机会受原标题栏影响而闪退
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        //标题栏，material design设计
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // 初始化 SharedPreferences，上传数据失败缓存本地，数据持久化
        sharedPreferences = getSharedPreferences("DataCache", Context.MODE_PRIVATE);





        //定义主线程
        handler = new Handler(Looper.getMainLooper());

        //定义上传数据线程
        upload_handler = new Handler();



        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        //获取地图引用
        baiduMap = mMapView.getMap();


        // 定义发送数据的任务
        sendDataRunnable = new Runnable() {
            @Override
            public void run() {
                //定时获取设备信息，因为会出现更换手机卡，更换移动数据类型，以及基站连接切换等状况
                // getDeviceInfo();
//                if(initialData <= 5 && initialData >= 0 ){
//                    signalStrengthQueue.offer(rsrpValue);
//                    System.out.println("现在加入队列的信号强度值是:"+rsrpValue);
//                    initialData++;
//                }
                if (Upload_INTERVAL < 10000.0) {
                    int tempNormal = NotNormal;

                    if (initialData < WINDOW_Capacity || ((double) rsrpValue >= PredictRsrp - SD && (double) rsrpValue <= PredictRsrp + SD)) {
                        NotNormal++;
                        SendDataTask sendDataTask = new SendDataTask();
                        //传三个值，其他要上传的值在别处获取
                        sendDataTask.execute(mCurrentLantitude, mCurrentLongtitude, (double) rsrpValue);
                        if (signalStrengthQueue.size() == WINDOW_Capacity) {
                            signalStrengthQueue.offer(rsrpValue);
                            signalStrengthQueue.poll();
                            System.out.println("现在的啊啊啊队列大小是:" + signalStrengthQueue.size());
                        } else if (signalStrengthQueue.size() != WINDOW_Capacity) {
                            signalStrengthQueue.offer(rsrpValue);
                            initialData++;
                            System.out.println("现在的initialData是:" + initialData);
                            if (initialData == WINDOW_Capacity) {
                                isinitial = false;
                            }
                            System.out.println("现在的队列大小是:" + signalStrengthQueue.size());
                        }
                    }


                    if (initialData > WINDOW_Capacity && (tempNormal != NotNormal)) {
                        PredictRsrp = calcuPredictRsrp(rsrpValue, PredictRsrp);
                        SD = calculateStandardDeviation(signalStrengthQueue);
                        System.out.println("现在的基准值和偏移量是:" + PredictRsrp + "   " + SD);
                        System.out.println("最新的信号强度是:" + rsrpValue);
                    }

                    if (isinitial == false) {
                        isinitial = true;
                        initialData++;
                        PredictRsrp = calculateMean(signalStrengthQueue);
                        SD = calculateStandardDeviation(signalStrengthQueue);
                        System.out.println("现在的基准值和偏移量是:" + PredictRsrp + "   " + SD);
                    }
                }
                    else{
                    SendDataTask sendDataTask = new SendDataTask();
                    //传三个值，其他要上传的值在别处获取
                    sendDataTask.execute(mCurrentLantitude, mCurrentLongtitude, (double) rsrpValue);
                    initialData=0;
                    signalStrengthQueue.clear();
                }
                    //每Pass_INTERVAL时间运行一次
                    handler.postDelayed(this, (long) Upload_INTERVAL);

            }
        };

        //开始上传按钮
        MaterialButton playPauseButton = findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartSend == false) {
                    wakeLock.acquire();
                    //点击按钮后isrequestdata设置为true，表示还未获取到数据库数据
                    isrequestdata = false;
                    isStartRun = true;

                    //建立信号强度值滑动窗口
                    signalStrengthQueue = new LinkedList<>();



                    // 启动定时检测本地缓存是否有缓存的未发送数据
                    startRetryTask();

                    // 调用异步任务发送数据到服务器
                  //  SendDataTask sendDataTask = new SendDataTask();
                  //  sendDataTask.execute(mCurrentLantitude, mCurrentLongtitude, (double) rsrpValue);
                    adjustUploadInterval();
                    System.out.println("开始上传数据");
                    // 设置周期性执行发送数据的任务
                    handler.postDelayed(sendDataRunnable, (long) Pass_INTERVAL);

                    // 设置为播放图标
                    playPauseButton.setIconResource(R.drawable.ic_icons_pause);
                    //改isStartSend为true，下次点击触发关闭连接
                    isStartSend = true;

                } else {
                    //关闭连接
                    //WebClient.closeConnection();
                    //移除数据上传任务
                    handler.removeCallbacks(sendDataRunnable);
                    //移除本地缓存检测任务
                    handler.removeCallbacks(retryRunnable);
                    // 设置为暂停图标
                    playPauseButton.setIconResource(R.drawable.ic__pause);
                    //改isStartSend为false，下次点击触发打开连接
                    isStartSend = false;
                    initialData=0;
                    signalStrengthQueue.clear();
                }
            }
        });


        //用户体验感标记
        MaterialButton SoBadlocate = findViewById(R.id.SoBadlocate);
        SoBadlocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWebsocketOn == true) {
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("latitude", mCurrentLantitude);
                        jsonBody.put("longitude", mCurrentLongtitude);
                        jsonBody.put("rsrpValue", 1);
                        jsonBody.put("rsrqValue", rsrqValue);
                        jsonBody.put("rssnrValue", rssnrValue);
                        jsonBody.put("operator", operator);
                        jsonBody.put("User", User);
                        jsonBody.put("obj", CellCID);
                        jsonBody.put("Time", Time);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //发送体验感不好标记数据
                    WebClient.SendBadtag(jsonBody.toString());
                } else {

                    // 建立 WebSocket 连接
                    WebClient.start();

                    // 延迟2秒后开始发送数据
                    upload_handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(isWebsocketOn);
                            if (isWebsocketOn == true) {
                                System.out.println("启动成功？");
                                JSONObject jsonBody = new JSONObject();
                                try {
                                    jsonBody.put("latitude", mCurrentLantitude);
                                    jsonBody.put("longitude", mCurrentLongtitude);
                                    jsonBody.put("rsrpValue", 1);
                                    jsonBody.put("rsrqValue", rsrqValue);
                                    jsonBody.put("rssnrValue", rssnrValue);
                                    jsonBody.put("operator", operator);
                                    jsonBody.put("User", User);
                                    jsonBody.put("obj", CellCID);
                                    jsonBody.put("Time", Time);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                WebClient.SendBadtag(jsonBody.toString());

                            } else {
                                Toast.makeText(v.getContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            }
                            WebClient.closeConnection();
                        }
                    }, 2000);

                }
            }
        });


        //滑动菜单布局，展示设备信息
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
                //把isFirstLocate设置为true，被定位函数监听到后会触发定位到所在地
                isFirstLocate = true;
            }
        });


        //获取卡片引用
        cardView = (MaterialCardView) findViewById(R.id.card_view);


        //撤去百度地图logo
        mMapView.removeViewAt(1);

        //在卡片上展示经纬度和信号强度数据
        ShowLatitude = findViewById(R.id.latitude);
        ShowLongtitude = findViewById(R.id.longtitude);
        ShowCellSignalStrength = findViewById(R.id.CellSignal);



        //开启地图的定位图层
        baiduMap.setMyLocationEnabled(true);


        //定位图标方向转动实现，创建 MyLocationConfiguration 对象并设置相关属性,enableDirection=true则允许显示方向
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, // 定位模式为普通态
                true, // 显示方向信息
                null,
                0xAAec2d7a, // 填充颜色
                0xAA2376b7 // 描边颜色
        );
        baiduMap.setMyLocationConfiguration(config);

        //菜单
        ShowEquipMessageMenu = findViewById(R.id.EquipMessListshow);

        //展示设备信息
        ShowEquipMessage = findViewById(R.id.EquipMessList);

        float lineSpacingExtra = 10f;  // 行间距的像素值
        float lineSpacingMultiplier = 1.2f;  // 行间距的倍数
        ShowEquipMessage.setLineSpacing(lineSpacingExtra, lineSpacingMultiplier);
        positionText = (TextView) findViewById(R.id.position_text_view);

        //启动设备状态监听
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //权限列表，记录未允许的权限
        List<String> permissionList = new ArrayList<>();


        //判断单个权限是否已经允许
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);

        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }

//        NetworkType = telephonyManager.getNetworkType();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);

        } else {
            requestLocation();      //请求位置信息
        }



        //启动软件获取一次设备信息
        getDeviceInfo();


        //刷新设备信息，展示在菜单栏
        ShowEquipMessage.setText("运营商名称: " + networkOperatorName + '\n' + "设备品牌名称: " + deviceBrand + '\n' + "设备型号: " + deviceModel
                + '\n' + "网络类型: " + networkType + '\n' + "SIM卡国别: " + simCountry + '\n' + "SIM卡当前状态: " + deviceStatus);





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


        //蜂窝网络信号强度监听器定义
        mListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength sStrength) {
                signalStrength = sStrength;
                getLTEsignalStrength();
            }

        };
        //监听器注册
        telephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        //信号强度采集
        StrengthCollection();

        WebClient.start();

    }

    //获取设备蜂窝网络信号强度值
    private int getLTEsignalStrength() {
        try {
            Method[] methods = android.telephony.SignalStrength.class.getMethods();
            for (Method mthd : methods) {
                //4G信号强度获取（主要）
                if (mthd.getName().equals(LTE_SIGNAL_STRENGTH)) {
                    Object result = mthd.invoke(signalStrength);

                    if (result instanceof List) {
                        List<?> signalStrengthList = (List<?>) result;

                        for (Object obj : signalStrengthList) {
                            //如果是4G类型
                            if (obj instanceof android.telephony.CellSignalStrengthLte) {
                                android.telephony.CellSignalStrengthLte lteSignalStrength = (android.telephony.CellSignalStrengthLte) obj;

                                Method getRsrpMethod = lteSignalStrength.getClass().getMethod("getRsrp");
                                Method getRsrqMethod = lteSignalStrength.getClass().getMethod("getRsrq");
                                Method getRssnrMethod = lteSignalStrength.getClass().getMethod("getRssnr");
                                rsrpValue = (int) getRsrpMethod.invoke(lteSignalStrength);
                                //采集rsrq
                                rsrqValue = (int) getRsrqMethod.invoke(lteSignalStrength);
                                //采集到rs-snr
                                rssnrValue = (int) getRssnrMethod.invoke(lteSignalStrength);
                                System.out.println("网络信号质量:" + rsrqValue);
                                System.out.println("网络信噪比:" + rssnrValue);
                                getDeviceInfo();
                                ShowCellSignalStrength.setText(rsrpValue + "dBm");

                            }
                            //2G
                            else if (obj instanceof android.telephony.CellSignalStrengthCdma) {
                                android.telephony.CellSignalStrengthCdma cdmaSignalStrength = (android.telephony.CellSignalStrengthCdma) obj;

                                Method getRsrpMethod = cdmaSignalStrength.getClass().getMethod("getRsrp");
                                rsrpValue = (int) getRsrpMethod.invoke(cdmaSignalStrength);
                                ShowCellSignalStrength.setText(rsrpValue + "dBm");

                            }
                            //3G
                            else if (obj instanceof android.telephony.CellSignalStrengthGsm) {
                                android.telephony.CellSignalStrengthGsm gsmSignalStrength = (android.telephony.CellSignalStrengthGsm) obj;

                                Method getRsrpMethod = gsmSignalStrength.getClass().getMethod("getRsrp");
                                rsrpValue = (int) getRsrpMethod.invoke(gsmSignalStrength);
                                ShowCellSignalStrength.setText(rsrpValue + "dBm");

                            }
                            //5G
                            else if (obj instanceof android.telephony.CellSignalStrengthNr) {
                                android.telephony.CellSignalStrengthNr NrSignalStrength = (android.telephony.CellSignalStrengthNr) obj;

                                Method getRsrpMethod = NrSignalStrength.getClass().getMethod("getDbm");
                                rsrpValue = (int) getRsrpMethod.invoke(NrSignalStrength);
                                ShowCellSignalStrength.setText(rsrpValue + "dBm");

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
    private void StrengthCollection() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // 获取网络信号强度
                int mCurrentSign = getLTEsignalStrength();
                collectsignaltimes++;
                System.out.println("采集次数" + collectsignaltimes);
                updateSignalMarker(mCurrentSign,mCurrentLantitude,mCurrentLongtitude);     //在地图上标记
            }
        };
        timer.schedule(task, 0, 10000);   //启动任务，初始时间间隔为1s


//        //采样频率自适应调整
//        timerisStopStep = new Timer();
//        taskisSopStep = new TimerTask() {
//            @Override
//            public void run() {
//                if (Math.abs(stepCount - laststep) < 20) {      //如果一定时间间隔步数少于20
//                                     //采样频率降低
//                    task.cancel();                          //注销任务
//                    task = new TimerTask() {
//                        @Override
//                        public void run() {
//                            // 获取网络信号强度
//                            int mCurrentSign = getLTEsignalStrength();
//                            collectsignaltimes++;
//                            System.out.println("采集次数" + collectsignaltimes);
//                            updateSignalMarker(mCurrentSign,mCurrentLantitude,mCurrentLongtitude);
//                        }
//                    };
//                 //   timer.schedule(task, 0, DurCollectSignal);             //重新启动任务，时间间隔调整为新频率
//                    System.out.println("时间间隔" + DurCollectSignal);
//                }
//                System.out.println("时间间隔" + DurCollectSignal + "   " + laststep + "   " + stepCount);
//                // 将当前步数记录为上一次的值，用于下一次比较
//                laststep = stepCount;
//
//            }
//
//        };
//
//        timerisStopStep.schedule(taskisSopStep, 20000, 60000);    //每隔1min调整一次，在启动程序的20s后开始运行，因为要给采集初始步数留时间
    }


//    //信号强度标记
//    private void updateSignalMarker(int MCurrentSign,double mCurrentLantitude,double mCurrentLongtitude) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                //定义Maker坐标点
//                LatLng point = new LatLng(mCurrentLantitude, mCurrentLongtitude);
//                //构建Marker图标
//
//                if (MCurrentSign <= 0 && MCurrentSign >= -60) {
//                    BitmapDescriptor bitmap = BitmapDescriptorFactory
//                            .fromResource(R.drawable.location_on_1);
//                    //构建MarkerOption，用于在地图上添加Marker
//                    OverlayOptions option = new MarkerOptions()
//                            .position(point)
//                            .icon(bitmap);
//                    //在地图上添加Marker，并显示
//                    baiduMap.addOverlay(option);
//                } else if (MCurrentSign < -60 && MCurrentSign >= -80) {
//                    BitmapDescriptor bitmap = BitmapDescriptorFactory
//                            .fromResource(R.drawable.location_on_3);
//                    //构建MarkerOption，用于在地图上添加Marker
//                    OverlayOptions option = new MarkerOptions()
//                            .position(point)
//                            .icon(bitmap);
//                    //在地图上添加Marker，并显示
//                    baiduMap.addOverlay(option);
//                } else if (MCurrentSign < -80 && MCurrentSign >= -100) {
//                    BitmapDescriptor bitmap = BitmapDescriptorFactory
//                            .fromResource(R.drawable.location_on_2);
//                    //构建MarkerOption，用于在地图上添加Marker
//                    OverlayOptions option = new MarkerOptions()
//                            .position(point)
//                            .icon(bitmap);
//                    //在地图上添加Marker，并显示
//                    baiduMap.addOverlay(option);
//                } else if (MCurrentSign < -100) {
//                    BitmapDescriptor bitmap = BitmapDescriptorFactory
//                            .fromResource(R.drawable.location_on_4);
//                    //构建MarkerOption，用于在地图上添加Marker
//                    OverlayOptions option = new MarkerOptions()
//                            .position(point)
//                            .icon(bitmap);
//                    //在地图上添加Marker，并显示
//                    baiduMap.addOverlay(option);
//                }
//            }
//
//        });
//    }

    //信号强度标记
    private void updateSignalMarker(int MCurrentSign,double mCurrentLantitude,double mCurrentLongtitude) {
      //  List<OverlayOptions> markerOptionsList = new ArrayList<>();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //定义Maker坐标点
              //  LatLng point = new LatLng(mCurrentLantitude, mCurrentLongtitude);
                //构建Marker图标

                if (MCurrentSign <= 0 && MCurrentSign >= -60) {
                    // 添加第一个圆点标记
                    LatLng point1 = new LatLng(mCurrentLantitude, mCurrentLongtitude);
                    int color1 = 0xAAFF0000; // 红色，半透明
                    float radius1 = 3; // 半径（单位：米）
                    OverlayOptions markerOptions1 = new CircleOptions()
                            .center(point1)
                            .radius((int) radius1)
                            .fillColor(color1)
                            .stroke(new Stroke(2, color1)); // 设置描边宽度和颜色
                    baiduMap.addOverlay(markerOptions1);

                } else if (MCurrentSign < -60 && MCurrentSign >= -80) {
                    LatLng point2 = new LatLng(mCurrentLantitude, mCurrentLongtitude);
                    int color2 = 0xAAFF8000; // 橙色，半透明
                    float radius2 = 3; // 半径（单位：米）
                    OverlayOptions markerOptions2 = new CircleOptions()
                            .center(point2)
                            .radius((int) radius2)
                            .fillColor(color2)
                            .stroke(new Stroke(2, color2)); // 设置描边宽度和颜色
//                    markerOptionsList.add(markerOptions2);
                    baiduMap.addOverlay(markerOptions2);
                } else if (MCurrentSign < -80 && MCurrentSign >= -100) {
                    LatLng point3 = new LatLng(mCurrentLantitude, mCurrentLongtitude);
                    int color3 = 0xAA00FF00; // 绿色，半透明
                    float radius2 = 3; // 半径（单位：米）
                    OverlayOptions markerOptions3 = new CircleOptions()
                            .center(point3)
                            .radius((int) radius2)
                            .fillColor(color3)
                            .stroke(new Stroke(2, color3)); // 设置描边宽度和颜色
//                    markerOptionsList.add(markerOptions2);

                    baiduMap.addOverlay(markerOptions3);
                } else if (MCurrentSign < -100) {
                    LatLng point4 = new LatLng(mCurrentLantitude, mCurrentLongtitude);
                    int color4 = 0xAA0000FF; // 蓝色，半透明
                    float radius4 = 3; // 半径（单位：米）
                    OverlayOptions markerOptions4 = new CircleOptions()
                            .center(point4)
                            .radius((int) radius4)
                            .fillColor(color4)
                            .stroke(new Stroke(2, color4)); // 设置描边宽度和颜色
//                    markerOptionsList.add(markerOptions2);
                    baiduMap.addOverlay(markerOptions4);
                }

            }

        });
    }

    //设备信息与网络信息提取
    private void getDeviceInfo() {

        // 设备运营商名称
        networkOperatorName = telephonyManager.getNetworkOperatorName();

        //存储到数据库中映射为1，2，3
      switch (networkOperatorName) {
          case "中国移动":
          case "CHN-MOBILE":
              operator=1;
              break;
          case "中国电信":
          case "CHN-TELECOM":
              operator=2;
              break;
          case "中国联通":
          case "CHN-UNICOM":
              operator=3;
              break;
      }




        // 设备品牌
        deviceBrand = android.os.Build.BRAND;

        // 设备型号
        deviceModel = android.os.Build.MODEL;

        //设备生产时间
        String Time = Long.toString(Build.TIME);

        //区分上传设备标识，拼接而成
        User = Build.ID+'+'+Time;


        // SIM卡国别
        simCountry = telephonyManager.getSimCountryIso();


        // SIM卡当前状态
        deviceStatus = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY ? "Ready" : "Not Ready";


        // 网络类型
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


        // 获取网络类型
        NetworkType = telephonyManager.getNetworkType();


        switch (NetworkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                networkType = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                networkType = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                networkType = "4G";
                break;
            case TelephonyManager.NETWORK_TYPE_NR:
                networkType = "5G";
            case 0:
                networkType = "5G";
            default:
                networkType = "未知";
                break;
        }



        //采集此时连接的基站CID
      //  if(networkType == "4G"){

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }


        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            if (cellInfoList != null && !cellInfoList.isEmpty()) {
                int[] cidArray = new int[cellInfoList.size()]; // 创建一个与CellInfo数量相同大小的数组

                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte) {
                        CellIdentityLte cellIdentity = ((CellInfoLte) cellInfo).getCellIdentity();
                        boolean isConnected = cellInfo.isRegistered(); // 检查基站是否为当前连接的基站
                        int Cid = cellIdentity.getCi();
                        if (isConnected) {
                            cidArray[0] = Cid; // 将获取到的CID存入数组
//                            index++; // 索引递增
                        }
                    }
                }
//                for (int i = 0; i < cidArray.length; i++) {
//                    System.out.println("CID[" + i + "]: " + cidArray[i]+'\n');
//                }
                CellCID = cidArray[0];
                System.out.println("已连接基站:"+CellCID);
            }

        //}


        //5G类型尚未解决
//
//        else{
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
//            if (cellInfoList != null) {
//                int[] cidArray = new int[cellInfoList.size()]; // 创建一个与CellInfo数量相同大小的数组
//                int index = 0; // 数组索引
//                for (CellInfo cellInfo : cellInfoList) {
//                    if (cellInfo instanceof CellInfoNr) {
//                        CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;
//                        CellIdentity Cid = cellInfoNr.getCellIdentity();
//                        System.out.println(Cid);
////                        if (isConnected) {
////                            cidArray[index] = Cid; // 将获取到的CID存入数组
////                            index++; // 索引递增
////                        }
//                    }
//                }
//                for (int i = 0; i < cidArray.length; i++) {
//                    System.out.println("CID[" + i + "]: " + cidArray[i]+'\n');
//                }
//                CellCID = cidArray[0];
//                System.out.println("已连接基站:"+CellCID);
//            }
//
//        }


//        ShowEquipMessage.setText("运营商名称: " + networkOperatorName+'\n'+"设备品牌名称: " + deviceBrand+'\n'+"设备型号: " + deviceModel
//        +'\n'+"网络类型: "+ networkType+'\n'+"SIM卡国别: " + simCountry+'\n'+"SIM卡当前状态: " + deviceStatus);
    }

    //标题栏按钮触发事件
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }


    //Ping连通性测试模块
    public boolean  onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.backup :
                PingTask pingTask = new PingTask("39.101.76.7", new PingTask.PingCallback() {
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

                //定义连通性测试线程
                Handler show_handler = new Handler();

                //延迟三秒显示卡片
                show_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"连接成功，平均时延: " + AverageTime + "ms", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);

                //重新建立与服务器的连接

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(mMapView, "重新连接服务器", Snackbar.LENGTH_LONG).show();
                    }
                });

                WebClient.start();
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
        initMyLocation();         //调用本地定位方法
        mLocationClient.start();       //开始定位，回调定位监听器

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
        //申请权限重新获得焦点后，再次访问设备信息
        getDeviceInfo();

        //刷新设备信息
        ShowEquipMessage.setText("运营商名称: " + networkOperatorName + '\n' + "设备品牌名称: " + deviceBrand + '\n' + "设备型号: " + deviceModel
                + '\n' + "网络类型: " + networkType + '\n' + "SIM卡国别: " + simCountry + '\n' + "SIM卡当前状态: " + deviceStatus);


        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //终止传感器监听


        mMapView.onPause();
    }



    // 自适应数据传输频率
    private void adjustUploadInterval() {
        // 保存当前位置的经纬度作为上一次的位置
        laststep = stepCount;
        System.out.println("现在时间间隔是："+Upload_INTERVAL);
        timerisStopStep = new Timer();
        taskisSopStep = new TimerTask() {
            @Override
            public void run() {
                // 计算直线距离


                 int  thisStep = stepCount - laststep;
                double calc1;
                // 更新上一次的位置


                if(isStartRun){
                    Upload_INTERVAL = Pass_INTERVAL;
                }


                else{
                    if(thisStep != 0 && Upload_INTERVAL >= 3000.0 && Upload_INTERVAL <= 60000.0) {
                        calc1 = lastIntervalstep / thisStep;
                        if (calc1 >= 0.6 && calc1 < 1 && lastIntervalstep > 10) {
                            Upload_INTERVAL = calc1 * Upload_INTERVAL;
                        } else if (calc1 <= 1.4 && calc1 > 1 && thisStep > 10) {
                            Upload_INTERVAL = calc1 * Upload_INTERVAL;
                        } else if (calc1 >= 0.6 && calc1 < 1 && thisStep < 10) {
                            Upload_INTERVAL = 1.0 * Upload_INTERVAL;
                        } else if (calc1 <= 1.4 && calc1 > 1 && lastIntervalstep < 10) {
                            Upload_INTERVAL = 1.0 * Upload_INTERVAL;
                        }  else if (calc1 < 0.6 && calc1 >= 0 && lastIntervalstep > 10) {
                            Upload_INTERVAL = 0.6 * Upload_INTERVAL;
                        }  else if (calc1 > 1.4  && thisStep > 10) {
                            Upload_INTERVAL = 1.4 * Upload_INTERVAL;
                        }  else if (calc1 < 0.6 && calc1 >= 0 && thisStep < 10) {
                            Upload_INTERVAL = 1.0 * Upload_INTERVAL;
                        } else if (calc1 > 1.4  && lastIntervalstep < 10) {
                            Upload_INTERVAL = 1.0 * Upload_INTERVAL;
                        }
                    }
                    else if(thisStep == 0){
                        Upload_INTERVAL =  1.6 * Upload_INTERVAL;
                    }
                    if(Upload_INTERVAL < 3000.0){
                        Upload_INTERVAL = 3000.0;
                    }
                    if(Upload_INTERVAL > 60000.0){
                        Upload_INTERVAL = 60000.0;
                    }
                }
                System.out.println("上一段步数是"+lastIntervalstep);
                System.out.println("当前位移是"+thisStep);
                lastIntervalstep = thisStep;
                laststep = stepCount;
                isStartRun = false;
                System.out.println("现在时间间隔是"+Upload_INTERVAL);

            }
        };
        timerisStopStep.schedule(taskisSopStep,60000,60000);    //每隔1min调整一次，在启动程序的20s后开始运行，因为要给采集初始步数留时间
    }




//    //用经纬度计算位移
//    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLon = Math.toRadians(lon2 - lon1);
//
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
//
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//
//        return EARTH_RADIUS * c;
//    }




        //标准差计算
        private  double calculateStandardDeviation(Queue<Integer>data) {
            int n = data.size();
            double mean = calculateMean(data);

            double sumOfSquaredDifferences = 0.0;
            for (int value : data) {
                double difference = value - mean;
                sumOfSquaredDifferences += difference * difference;
            }

            double variance = sumOfSquaredDifferences / n;
            return Math.sqrt(variance);
        }

        //平均值计算
        private  double calculateMean(Queue<Integer>data) {
            int sum = 0;
            for (int value : data) {
                sum += value;
            }
            return (double) sum / data.size();
        }


        //指数滑动平均法计算基准值
        private double calcuPredictRsrp(int Current_rsrpValue, double last_value){
            return Rsrp_Weight * Current_rsrpValue + (1 - Rsrp_Weight)*last_value;
        }


    //websocket客户端,与服务器通信
    public class WebSocketClient extends WebSocketListener {
        private WebSocket webSocket;

        public void start() {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("ws:39.101.76.7:8000/android-websocket/").build();

            webSocket = client.newWebSocket(request, this);
        }

        public void sendData(String data) {

                webSocket.send(data);


//            if (!isServerReceived) {
//                // 发送失败，保存到本地缓存
//                String key = Long.toString(System.currentTimeMillis());
//
//                // 将数据存储到 SharedPreferences
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(key, data);
//                editor.apply();
//            }
        }


        public void SendBadtag(String data) {
            webSocket.send(data);
            System.out.println("信号太差");
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            // WebSocket 连接建立
            Log.d("WebSocket", "连接成功");
            isWebsocketOn = true;
//            Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
            // 在这里可以执行后续操作或发送消息
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            // 处理从服务器接收到的消息
            super.onMessage(webSocket, text);
            //如果是服务器对传来的数据反馈
            if(isrequestdata == false){
                //采用比对前后两次服务器发来字符串的差异判断服务器是否收到数据
                if(text != isSucess){
                    //服务器收到了
                    isServerReceived = true;
                }
                else{
                    //服务器没收到
                    isServerReceived = false;
                }
                isSucess = text;
            }
            //如果是请求服务器数据库数据
            else if(isrequestdata == true) {
                System.out.println("怎么回事啊");
                try {
                    // 将每个JSON对象逐个解析
                    while (text.length() > 0) {
                        // 查找下一个JSON对象的索引
                        int endIndex = text.indexOf("}") + 1;

                        // 截取字符串，获取单个JSON对象
                        String jsonStr = text.substring(0, endIndex);

                        // 创建JSONObject并提取字段值
                        JSONObject jsonObject = new JSONObject(jsonStr);

                        double latitude = jsonObject.getDouble("latitude");
                        double longitude = jsonObject.getDouble("longitude");
                        int signalStrength = jsonObject.getInt("signal_strength");
                        int Opt = jsonObject.getInt("Operator");


                        if(operator == Opt){
                            //在手机上显示完整网络信号图谱
                            updateSignalMarker(signalStrength, latitude, longitude);
                        }

                        // 去掉已解析的JSON对象，继续处理剩余部分
                        text = text.substring(endIndex);
                    }
                    //把isrequestdata设置为false，证明已经从数据库请求过数据了
                    isrequestdata = false;
                } catch (JSONException e) {
                    isrequestdata = false;
                    e.printStackTrace();
                }
            }


        }

        public void closeConnection() {
            webSocket.close(1000, "WebSocket断开连接");
            Toast.makeText(MainActivity.this, "连接已经断开", Toast.LENGTH_SHORT).show();
            isWebsocketOn = false;
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            // WebSocket 连接关闭
            // WebSocket 连接关闭
//            WebClient.start();
            Toast.makeText(MainActivity.this, "意外断开连接", Toast.LENGTH_LONG).show();
         //   Log.d("WebSocket", "连接关闭，code: " + code + ", reason: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            isServerReceived =false;
            // 处理连接失败
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(mMapView, "连接意外断开", Snackbar.LENGTH_LONG).show();
                }
            });
        }
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
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);          //将地图的中心点设置为 ll 所表示的位置
                baiduMap.animateMapStatus(update);                          //动画更新中心点
                update = MapStatusUpdateFactory.zoomTo(18f);        //将地图的缩放级别设置为 16
                baiduMap.animateMapStatus(update);              //动画更新缩放级别
                isFirstLocate = false;
            }
        }

    }


    //上传数据到服务器
    private class SendDataTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... params) {

            mCurrentLantitude = params[0];
            mCurrentLongtitude = params[1];
            rsrpValue = params[2].intValue();


            // 获取系统当前时间
            Calendar calendar = Calendar.getInstance();

// 获取年份
            int year = calendar.get(Calendar.YEAR);

// 获取月份（注意：月份从0开始，所以需要加1）
            int month = calendar.get(Calendar.MONTH) + 1;

// 获取日期
            int day = calendar.get(Calendar.DAY_OF_MONTH);

// 获取小时
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

// 获取分钟
            int minute = calendar.get(Calendar.MINUTE);

// 获取秒钟
            int second = calendar.get(Calendar.SECOND);

// 格式化日期和时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d-H-m-s");
            Time = String.format("%04d-%02d-%02d-%02d-%02d-%02d", year, month, day, hour, minute, second);

            //放在try外，为了缓存发送失败数据


            //发送地理坐标、信号强度rsrp，信号质量rsrq, 信噪比rssnr, 运营商operator，用户标识Users，基站CID CellCID,时间戳
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("latitude", mCurrentLantitude);
                jsonBody.put("longitude", mCurrentLongtitude);
                jsonBody.put("rsrpValue", rsrpValue);
                jsonBody.put("rsrqValue", rsrqValue);
                jsonBody.put("rssnrValue", rssnrValue);
                jsonBody.put("operator", operator);
                jsonBody.put("User", User);
                jsonBody.put("obj", CellCID);
                jsonBody.put("Time", Time);
                //缓存下来数据，如果连接断开，存到本地
                dataCache = jsonBody.toString();

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间为10秒
                        .readTimeout(30, TimeUnit.SECONDS) // 读取超时时间为10秒
                        .writeTimeout(30, TimeUnit.SECONDS) // 写入超时时间为10秒
                        .retryOnConnectionFailure(true) // 是否在连接失败时自动重试
                        .build();


                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(mediaType, jsonBody.toString());


                // 发送数据给服务器

                WebClient.sendData(jsonBody.toString());

                Request request = new Request.Builder()
                        .url(DJANGO_API_URL)
                        .post(requestBody)
                        .build();




               Response response = client.newCall(request).execute();

                return response.body().string();


            } catch (Exception e) {
                System.out.println("出错");


//                    // 发送失败，保存到本地缓存
//                    String key = Long.toString(System.currentTimeMillis());
//
//                    // 将数据存储到 SharedPreferences
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString(key, jsonBody.toString());
//                System.out.println(jsonBody.toString());
//                     System.out.println("运行了啊");
//                    editor.apply();

                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                System.out.println("Response from Django: " + result);
                // 在这里处理从 Django 接收到的响应
            } else {
                String key = Long.toString(System.currentTimeMillis());

                // 将数据存储到 SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                System.out.println("这是什么:"+editor);
                editor.putString(key, dataCache);
                System.out.println(dataCache);


                editor.apply();
                System.out.println("未收到回应");
                // 发送失败，保存到本地缓存

            }
        }
    }



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

        // 传感器精度变化时的回调方法
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

    }


    //发起定位
    private void initMyLocation() {
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        //强制选择高精度定位，三种模式:Hight_Accuracy(高精度）、Battery_Saving(节电模式）、Device_Sensors(传感器模式）
        option.setOpenGps(true); // 打开gps
        option.setLocationNotify(true); // 设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setOpenGnss(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);           //设置位置更新间隔，1s一更新

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
                // 创建进程构建器，设置Ping命令及其参数
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


    //检测本地缓存是否还有未发送数据
    private void startRetryTask() {
        retryRunnable = new Runnable() {
            @Override
            public void run() {
                // 获取保存的发送失败的数据
                        // 发送成功，从 SharedPreferences 中删除该数据
                        Map<String,?> allEntries = sharedPreferences.getAll();

                        if (!allEntries.isEmpty()) {
                            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                                String key = entry.getKey();
                                String value = (String) entry.getValue();

                                WebClient.sendData((String)entry.getValue());
                                System.out.println( "发送缓存数据成功");
                                if (isServerReceived) {
                                sharedPreferences.edit().remove(entry.getKey()).apply();
                                System.out.println( "缓存数据已删除");
                            }
                        }
                    } else {
                        // 发送失败，根据具体需求进行处理
                    }


                // 重试任务延迟执行
                handler.postDelayed(this, 1000);
            }
        };

        // 第一次执行定时任务
        handler.post(retryRunnable);
    }


    @Override
    protected void onDestroy() {
        mLocationClient.stop();         //程序销毁时停止定位，防止消耗电量
        baiduMap.setMyLocationEnabled(false);
        myOrientationListener.unregisterSensorListener();
//        mySTEPListener.unregisterSensorListener();
        mMapView.onDestroy();
        mMapView = null;
        // 释放WakeLock
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        //断开websocket连接
        WebClient.closeConnection();
        baiduMap.clear();
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
    }
}




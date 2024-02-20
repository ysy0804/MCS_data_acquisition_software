var map = new BMapGL.Map("allmap");
// map.setMapType(BMAP_SATELLITE_MAP);      // 设置地图类型为地球模式

var Androidsocket = new WebSocket("ws:127.0.0.1:8000/android-websocket/");




// 获取用户当前位置

var geolocation = new BMapGL.Geolocation();
var gc = new BMapGL.Geocoder();//创建地理编码器
  // 开启SDK辅助定位
geolocation.enableSDKLocation();
geolocation.getCurrentPosition(function(result){

		var latitude = result.point.lat; // 获取纬度
		var longitude = result.point.lng; // 获取经度
		map.centerAndZoom(result.point, 19);

});
map.enableScrollWheelZoom(true);
// map.setMapStyleV2({
//   styleId: '6983dcb3181671e1aebd8f830f9574cc'
// });
var cityCtrl = new BMapGL.CityListControl();  // 添加城市列表控件
map.addControl(cityCtrl);
var Locate = new BMapGL.LocationControl();  // 添加城市列表控件
map.addControl(Locate);


// 创建地点搜索对象
var localSearch = new BMapGL.LocalSearch(map, {
  renderOptions: { map: map } // 将搜索结果显示在地图上
});

//引用前端搜索按钮、搜索框输入
var searchButton = document.getElementById("search-button");
var searchInput = document.getElementById("search-input");



//点击按钮检索所有相关地点
searchButton.addEventListener("click",function () {
  var locationName = searchInput.value;
 localSearch.search(locationName);
});


//如果搜索框内没有文字，搜索框具备可伸缩功能
searchButton.addEventListener("click", function() {
    var searchInput = document.getElementById("search-input");
    if(searchInput.value === "") {
        if (searchInput.style.display === "none") {
            // socket.send("hello")
            Androidsocket.send("ekkk")
            searchInput.style.display = "inline-block";
        } else {
            searchInput.style.display = "none";
        }
    }
});


/*以下是地理位置自动联想功能*/


// 创建自动完成对象
var autoComplete = new BMapGL.Autocomplete({
  input: searchInput,
  location: map,

});



var myValue;
autoComplete.addEventListener("onconfirm", function(event) {    //鼠标点击下拉列表后的事件
	var _value = event.item.value;
		myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;

		setPlace();
	});

	function setPlace(){
		map.clearOverlays();    //清除地图上所有覆盖物
		function myFun(){
			var Locate = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
			map.centerAndZoom(Locate, 18);
			map.addOverlay(new BMapGL.Marker(Locate));    //添加标注
		}
		var local = new BMapGL.LocalSearch(map, { //智能搜索
		  onSearchComplete: myFun
		});
		local.search(myValue);
	}



// 标记经纬度和网络信号强度
function markLocation(latitude, longitude, signalStrength) {
    console.log('贫道杯里是隔年的黑枣，怎么赶得上长老的红枣香甜啊');
    //console.log(latitude);
    var point = new BMapGL.Point(longitude, latitude);
    var marker = new BMapGL.Marker(point);
    map.addOverlay(marker);

        // 绑定鼠标悬停事件
    // marker.addEventListener('mouseover', function(e) {
    //   var signalStrength = e.signalStrength;
    //   var infoWindow = new BMapGL.InfoWindow('Signal Strength: ' + signalStrength);
    //   this.openInfoWindow(infoWindow);
    // });
    //
    // map.addOverlay(marker);



     marker.addEventListener('mouseover', function() {
        var infoWindow = new BMapGL.InfoWindow(signalStrength.toString(), { offset: new BMapGL.Size(20, -10) });
        marker.openInfoWindow(infoWindow);
      });

      marker.addEventListener('mouseout', function() {
        map.closeInfoWindow(); // 关闭InfoWindow
      });



    // var label = new BMapGL.Label(signalStrength.toString(), { offset: new BMapGL.Size(20, -10) });
    // marker.setLabel(label);
}


Androidsocket.onopen= function(event) {
    console.log('与android的WebSocket连接已建立');
    Androidsocket.send("ekkk")
    // socket.send(JSON.stringify({
    //     'type': 'join_group',
    //     'group_name': 'your_group_name',
    // }));
};

Androidsocket.onmessage = function(event) {
    // const data = JSON.parse(event.data);
    console.log("chdbscbsdnbc");
    console.log(event.data);
    // markLocation(data.latitude, data.longitude, data.signal_strength);
    //
    //     // 标记为已接收
    // socket.send('received');
}


 var socket = new WebSocket("ws:127.0.0.1:8000/room/hony/");

socket.onopen = function(event) {
    console.log('WebSocket连接已建立');
    socket.send('start_query');
    // socket.send(JSON.stringify({
    //     'type': 'join_group',
    //     'group_name': 'your_group_name',
    // }));
};


socket.onmessage = function(event) {
    // const data = JSON.parse(event.data);
     console.log(event.data);
     var parsedData = JSON.parse(event.data);

     parsedData.locations.forEach(function(location) {
           console.log(location.signalStrength);
           markLocation(location.latitude, location.longitude, location.signalStrength);
    });

    //
    //     // 标记为已接收
    // socket.send('received');
}



// setInterval(function() {
//   // 发送Ajax请求获取最新位置数据
//     console.log('Error哈哈哈哈');
//   $.ajax({
//     url: '/get_latest_locations/',  // 后端处理数据的URL
//     method: 'GET',
//     success: function(response) {
//       // 在成功回调函数中处理接收到的数据
//       updateMap(response.locations);
//     },
//     error: function(error) {
//       console.log('Error:', error);
//     }
//   });
// }, 5000);  // 定时器每隔5秒触发一次
//
// // 在地图上添加新的标记，并绑定鼠标悬停事件
// function updateMap(locations) {
//   // 清除之前的标记
//   //map.clearOverlays();
//
//   // 在地图上添加新的标记
//   for (var i = 0; i < locations.length; i++) {
//     var point = new BMapGL.Point(locations[i].longitude, locations[i].latitude);
//     var marker = new BMapGL.Marker(point);
//
//     // 绑定鼠标悬停事件
//     marker.addEventListener('mouseover', function(e) {
//       var signalStrength = this.signalStrength;
//       var infoWindow = new BMapGL.InfoWindow('Signal Strength: ' + signalStrength);
//       this.openInfoWindow(infoWindow);
//     });
//
//     map.addOverlay(marker);
//   }
// }
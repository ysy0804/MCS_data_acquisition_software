var map = new BMapGL.Map("allmap");
// map.setMapType(BMAP_SATELLITE_MAP);      // 设置地图类型为地球模式


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



// // 实时标记函数
//     function markLocation(latitude, longitude) {
// 		var point = new BMapGL.Point(longitude, latitude);
// 		var marker = new BMapGL.Marker(point);
// 		map.addOverlay(marker);
// 	}
//
// 	   // 使用WebSocket连接Django服务器，接收实时数据
// 	var socket = new WebSocket("ws://your-django-server-url");
// 	socket.onmessage = function(event) {
// 		var data = JSON.parse(event.data);
// 		var latitude = data.latitude;
// 		var longitude = data.longitude;
//
//             // 调用实时标记函数
// 		markLocation(latitude, longitude);
// 	};


 var socket = new WebSocket("ws:127.0.0.1:8000/room/hony/");

 socket.onopen = function () {
     console.log('连接成功');//成功连接上Websocket
 };
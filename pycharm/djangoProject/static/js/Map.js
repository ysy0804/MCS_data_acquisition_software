var map = new BMapGL.Map("allmap");
// map.setMapType(BMAP_SATELLITE_MAP);      // 设置地图类型为地球模式

map.centerAndZoom(new BMapGL.Point(122.7530, 40.8517), 19);
map.enableScrollWheelZoom(true);
// map.setMapStyleV2({
//   styleId: '6983dcb3181671e1aebd8f830f9574cc'
// });
var cityCtrl = new BMapGL.CityListControl();  // 添加城市列表控件
map.addControl(cityCtrl);
var Locate = new BMapGL.LocationControl();  // 添加城市列表控件
map.addControl(Locate);

// // 获取搜索按钮并绑定点击事件
// var searchButton = document.getElementById("search-button");
// searchButton.addEventListener("click", search);

//搜索栏ui代码
// document.getElementById("search-button").addEventListener("click", function() {
//   var input = document.getElementById("search-input");
//   if (input.style.display === "none") {
//     input.style.display = "inline-block";
//      search();
//   } else {
//
//   search();
//   }
// });

  // 设置初始模式为点击模式


// 创建地点搜索对象
var localSearch = new BMapGL.LocalSearch(map, {
  renderOptions: { map: map } // 将搜索结果显示在地图上
});


var searchButton = document.getElementById("search-button");
var searchInput = document.getElementById("search-input");


// 创建自动完成对象
var autoComplete = new BMapGL.Autocomplete({
  input: searchInput,
  location: map,
});



var myValue;
autoComplete.addEventListener("onconfirm", function(event) {    //鼠标点击下拉列表后的事件
	var _value = event.item.value;
		myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
		// document.getElementById("searchResultPanel").innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;

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



searchButton.addEventListener("click",function () {
    var searchInput = document.getElementById("search-input");

  var locationName = searchInput.value;


 localSearch.search(locationName);
});




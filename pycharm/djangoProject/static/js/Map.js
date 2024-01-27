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
  zIndex: 999,
  onSearchComplete: function (results) {
    // 执行地点检索完成后的自定义处理
    // results 是地点检索的结果数组

    // 获取地点检索下拉列表容器
    let suggestionContainer = document.getElementsByClassName('tangram-suggestion')[0];

    // 遍历结果数组，修改样式
    for (let i = 0; i < results.length; i++) {
      let suggestionItem = suggestionContainer.children[i];
      // 对每个下拉列表项进行样式修改
      suggestionItem.style.backgroundColor = '#232425';
      suggestionItem.style.border = '1px solid #ccc';
      suggestionItem.style.borderRadius = '4px';
      suggestionItem.style.boxShadow = '0 2px 4px rgba(0, 0, 0, 0.2)';
      suggestionItem.style.padding = '8px';
      suggestionItem.style.fontSize = '14px';
      suggestionItem.style.color = '#262524';

      // ... 添加更多样式规则 ...
    }
  }
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




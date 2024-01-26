from django.shortcuts import render
from django.http import JsonResponse
import requests
from django.views.decorators.csrf import csrf_exempt
import json
from urllib.parse import quote
import pymysql
# Create your views here.

def map_view(request):
    return render(request, 'Map.html')


# # 与数据库交互
# @csrf_exempt
# def process_request(request):
#     if request.method == 'POST':
#         # 解析请求数据
#         data = request.POST
#
#         # 连接到 MySQL 数据库
#         connection = pymysql.connect(
#             host='ysy',
#             user='ysy',
#             password='210804Ysy!',
#             database='ysydb'
#         )
#
#         # 执行数据库操作
#         cursor = connection.cursor()
#
#         #sql操作语句
#         cursor.execute("INSERT INTO your_table (column1, column2) VALUES (%s, %s)", (data['value1'], data['value2']))
#         connection.commit()
#
#         # 关闭数据库连接
#         cursor.close()
#         connection.close()
#
#         # 返回响应给 Android 客户端
#         return JsonResponse({'status': 'success'})



#与android交互
@csrf_exempt
def receive_data(request):
    if request.method == 'POST':
        data = json.loads(request.body.decode('utf-8'))
        latitude = data.get('latitude')
        longitude = data.get('longitude')
        rsrp_value = data.get('rsrpValue')
        print(request.POST)

        print(latitude)
        print('\n')
        print(longitude)
        print('\n')
        print(rsrp_value)
        # 在这里处理接收到的数据
        # ...

        response_data = {'message': 'Data received successfully'}
        return JsonResponse(response_data, status=200)
    else:
        # 处理非 POST 请求的情况
        response_data = {
            'error': 'Invalid request method'
        }
        return JsonResponse(response_data, status=400)

@csrf_exempt
# 地点检索
def search_location(request):
    if request.method == 'POST':
        # 前端输入的地点名称
        print('在发发发发柏林生活')
        location_name = request.POST.get('location_name')
    #
    #     # 替换为您的百度地图API密钥
    #     api_key = 'UviWxqA0VzgsjjHvtcQCqj65lBnZZBo5'
    #
    #     # 地点检索API请求URL
    #     url = f'http://api.map.baidu.com/place/v2/search?query={location_name}&region=全国&output=json&ak={api_key}'
    #
    #     try:
    #         response = requests.get(url)
    #         data = response.json()
    #
    #         # 解析地点检索结果
    #         if data['status'] == 0 and data['results']:
    #             location = data['results'][0]
    #             latitude = location['location']['lat']
    #             longitude = location['location']['lng']
    #
    #             return render(request, 'Map.html', {'latitude': latitude, 'longitude': longitude})
    #         else:
    #             error_message = '未能找到该地理位置！'
    #             print('在柏林生活')
    #             return render(request, 'Map.html', {'error_message': error_message})
    #
    #     except requests.exceptions.RequestException:
    #         error_message = '地点检索请求失败！'
    #         print('哈哈哈哈')
    #         return render(request, 'Map.html', {'error_message': error_message})
    #
    #
    # return render(request, 'Map.html')
        api_key = 'UviWxqA0VzgsjjHvtcQCqj65lBnZZBo5'
        # 发送请求到百度地图API进行地理坐标转换
        url = "https://api.map.baidu.com/place/v2/search"
        params = {
            "query": location_name,
            'ak': 'UviWxqA0VzgsjjHvtcQCqj65lBnZZBo5',
            'output': 'json',
            'radius': '2000'
        }
        response = requests.get(url=url, params=params)
        data = response.json()
        print(data)

        # 提取经纬度坐标

        if data.get("status") == 0:
            result = data.get("result", {})
            location = result.get("location", {})
            latitude = location.get("lat")
            longitude = location.get("lng")
            return JsonResponse({"latitude": latitude, "longitude": longitude})
        else:
            error_message = data.get("message")
            return JsonResponse({"error_message": error_message}, status=400)

    return JsonResponse({}, status=405)

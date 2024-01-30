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
        Current_Latitude = data.get('latitude')
        Current_Longitude = data.get('longitude')
        rsrp_value = data.get('rsrpValue')
        print(request.POST)

        print(Current_Latitude)
        print('\n')
        print(Current_Longitude)
        print('\n')
        print(rsrp_value)
        # 在这里处理接收到的数据
        # ...

        response_data = {'message': 'Data received successfully'}
        return render(request, 'Map.html', {'latitude': Current_Latitude, 'longitude': Current_Longitude})
        # return JsonResponse(response_data, status=200)
    else:
        # 处理非 POST 请求的情况
        response_data = {
            'error': 'Invalid request method'
        }
        return JsonResponse(response_data, status=400)



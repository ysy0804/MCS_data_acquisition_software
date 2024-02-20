from channels.generic.websocket import AsyncWebsocketConsumer
from channels.generic.websocket import WebsocketConsumer
from channels.layers import get_channel_layer
from .models import LocationData
import asyncio
import json
import time



channel_layer = get_channel_layer()
# class MyConsumer(AsyncWebsocketConsumer):
#     async def connect(self):
#        await self.accept()
#        await self.channel_layer.group_add("web", self.channel_name)
#        print('WebSocket连接已建立')
#
#     async def disconnect(self, close_code):
#         await self.channel_layer.group_discard("web", self.channel_name)
#         print('WebSocket连接已关闭')
#
#     async def receive(self, text_data):
#         # 接收到来自WebSocket的数据
#         print("接收到consumer2" + text_data)
#         if text_data == 'start_query':
#             while True:
#                 locations = LocationData.objects.filter(is_ask=False)  # 获取未被处理的位置数据
#                 data = [
#                     {
#                         'latitude': loc.latitude,
#                         'longitude': loc.longitude,
#                         'signalStrength': loc.signal_strength
#                     }
#                     for loc in locations
#                 ]
#                 locations.update(is_ask=True)
#                 await self.send(text_data=json.dumps({'locations': data}))
#                 time.sleep(5)  # 5秒后再次查询
#
#
#     async def send_to_consumer1(self, event):
#         # 从Consumer2接收到数据并发送给Web客户端的JavaScript文件
#         data = event['data']
#         print("接收到consumer2"+data)
#         await self.send(data)


# 发送数据到WebSocket连接








class MyConsumer(WebsocketConsumer):
    def connect(self):
       self.accept()
       self.channel_layer.group_add('your_group_name', self.channel_name)
       print('WebSocket连接已建立')

    def disconnect(self, close_code):
        self.channel_layer.group_discard('your_group_name', self.channel_name)
        print('WebSocket连接已关闭')

    def receive(self, text_data):
        # 接收到来自WebSocket的数据
        print("接收到consumer2" + text_data)
        if text_data == 'start_query':
            while True:
                locations = LocationData.objects.filter(is_ask=False)  # 获取未被处理的位置数据
                data = [
                    {
                        'latitude': loc.latitude,
                        'longitude': loc.longitude,
                        'signalStrength': loc.signal_strength
                    }
                    for loc in locations
                ]
                locations.update(is_ask=True)
                self.send(text_data=json.dumps({'locations': data}))
                time.sleep(20)  # 5秒后再次查询

    def send_data(self, event):
        print("hjcbjdbsjcbsdbch")
        # 从Consumer2接收到数据并发送给Web客户端的JavaScript文件
        data = event['data']
        print("接收到consumer2" + data)
        self.send(data)

# # 发送数据到WebSocket连接


#
# class AndroidComsumers(AsyncWebsocketConsumer):
#     async def connect(self):
#         await self.accept()
#         await self.channel_layer.group_add("android", self.channel_name)
#
#     async def disconnect(self, close_code):
#         await self.channel_layer.group_discard("android", self.channel_name)
#
#     async def receive(self, text_data):
#         # 接收到来自Android的数据
#         print(text_data)
#         # 将数据发送给Consumer1
#         await self.channel_layer.group_send("web", {
#             'type': 'receive',
#             'data': text_data,
#         })
#         message = json.loads(text_data)
#         Current_Latitude = message.get('latitude')
#         Current_Longitude = message.get('longitude')
#         rsrp_value = message.get('rsrpValue')
#         await Data = LocationData(latitude=Current_Latitude, longitude=Current_Longitude, signal_strength=rsrp_value)
#         await Data.save()

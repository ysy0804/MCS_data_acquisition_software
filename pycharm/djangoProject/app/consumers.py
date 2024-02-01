from channels.generic.websocket import AsyncWebsocketConsumer
from channels.generic.websocket import WebsocketConsumer
from .models import LocationData
import json
# class MyConsumer(AsyncWebsocketConsumer):
#     async def connect(self):
#        await self.accept()
#        await self.channel_layer.group_add('your_group_name', self.channel_name)
#        print('WebSocket连接已建立')
#
#     async def disconnect(self, close_code):
#         await self.channel_layer.group_discard('your_group_name', self.channel_name)
#         print('WebSocket连接已关闭')
#
#     async def receive(self, text_data):
#         print("hahaha")
#         await self.send('哈哈哈哈')
#         # self.send(text_data)
#         # data = json.loads(text_data)
#         # if data.get('type') == 'join_group':
#         #     group_name = data.get('group_name')
#         #     print('hcjnjksncnk')
#         #     await self.channel_layer.group_add(group_name, self.channel_name)
#         #     await self.send(json.dumps({'status': 'joined_group', 'group_name': group_name}))
#         # else:
#         #     # 处理其他消息类型
#         #     print('嘻嘻嘻')
#
#     async def send_data(self, data):
#         print("hjcbjdbsjcbsdbch")
#         await self.send(data)
#         # print('v反对v范德萨v')
#         # # 处理发送给 WebSocket 客户端的数据
#         # latitude = event['latitude']
#         # longitude = event['longitude']
#         # rsrp_value = event['signal_strength']
#         #
#         # # 发送数据给 WebSocket 客户端
#         # self.send(json.dumps({
#         #     'latitude': latitude,
#         #     'longitude': longitude,
#         #     'signal_strength': rsrp_value,
#         # }))
#
# # 发送数据到WebSocket连接

class MyConsumer(WebsocketConsumer):
    def connect(self):
       self.accept()
       self.channel_layer.group_add('your_group_name', self.channel_name)
       print('WebSocket连接已建立')

    def disconnect(self, close_code):
        self.channel_layer.group_discard('your_group_name', self.channel_name)
        print('WebSocket连接已关闭')

    def receive(self, text_data):
        print("hahaha")
        self.send('哈哈哈哈')
        # self.send(text_data)
        # data = json.loads(text_data)
        # if data.get('type') == 'join_group':
        #     group_name = data.get('group_name')
        #     print('hcjnjksncnk')
        #     await self.channel_layer.group_add(group_name, self.channel_name)
        #     await self.send(json.dumps({'status': 'joined_group', 'group_name': group_name}))
        # else:
        #     # 处理其他消息类型
        #     print('嘻嘻嘻')

    def send_data(self, event):
        print("hjcbjdbsjcbsdbch")
        print(event)
        self.send(json.dumps(event))
        # print('v反对v范德萨v')
        # # 处理发送给 WebSocket 客户端的数据
        # latitude = event['latitude']
        # longitude = event['longitude']
        # rsrp_value = event['rsrpValue']
        #
        # # 发送数据给 WebSocket 客户端
        # self.send(json.dumps({
        #     'latitude': latitude,
        #     'longitude': longitude,
        #     'rsrpValue': rsrp_value,
        # }))

# 发送数据到WebSocket连接

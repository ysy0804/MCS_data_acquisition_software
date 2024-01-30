from channels.generic.websocket import AsyncWebsocketConsumer


class MyConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        await self.accept()

    async def disconnect(self, close_code):
        pass

    async def receive(self,text_data):
        await self.send(text_data)

    # async def send_data(self, event):


# 发送数据到WebSocket连接

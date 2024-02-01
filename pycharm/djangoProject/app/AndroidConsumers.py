from channels.generic.websocket import WebsocketConsumer
from app.consumers import MyConsumer
import json
class AndroidComsumers(WebsocketConsumer):
    def connect(self):
        self.accept()
        print('连接已建立')

    def disconnect(self, close_code):
            pass

    def receive(self, text_data):
        # 处理从 Android 客户端接收到的数据
        print("合成和")
        # 将数据推送给 Web 客户端
        print(text_data)
        web_consumer = MyConsumer()  # 创建 Web 客户端消费者的实例
        web_consumer.scope = self.scope  # 设置 Web 客户端消费者的 scope
        # web_consumer.connect()  # 建立 WebSocket 连接
        web_consumer.receive(text_data)  # 将数据推送给 Web 客户端


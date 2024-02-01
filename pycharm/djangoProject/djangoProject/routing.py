from django.urls import re_path
from app import consumers
from app import AndroidConsumers

websocket_urlpatterns = [
    re_path('room/hony', consumers.MyConsumer.as_asgi()),
    re_path('android-websocket', AndroidConsumers.AndroidComsumers.as_asgi()),
]
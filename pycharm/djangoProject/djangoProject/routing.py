from django.urls import re_path
from app import consumers

websocket_urlpatterns = [
    re_path('room/hony', consumers.MyConsumer.as_asgi()),
]
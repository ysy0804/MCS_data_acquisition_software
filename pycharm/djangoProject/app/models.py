from django.db import models

# Create your models here.
# test mysqlconnect
class Register(models.Model):
    username = models.CharField(max_length=32)
    password = models.CharField(max_length=64)


# 把移动感知端传来的数据存入数据库
class LocationData(models.Model):
    latitude = models.FloatField()
    longitude = models.FloatField()
    signal_strength = models.IntegerField()
    is_sent = models.BooleanField(default=False)
    is_ask = models.BooleanField(default=False)

    def __str__(self):
        return f'Latitude: {self.latitude}, Longitude: {self.longitude}'
from django.db import models

# Create your models here.
# test mysqlconnect
class Register(models.Model):
    username = models.CharField(max_length=32)
    password = models.CharField(max_length=64)
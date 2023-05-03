package com.company;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class service {
    private User user;
    private List<User> userList;
    private FileDao fileDao;
    private String filename = "User.txt";
    private List<Log> record;
    private List<Log> history;
    private boolean isLooked;//是否已经查看
    public Service(){
        isLooked = false;
        fileDao = new FileDao(filename);
        userList = fileDao.getList();
        record = new ArrayList<>();
        history = new ArrayList<>();
}
    public Service(String filename){
        isLooked = false;
        this.filename = filename;
        fileDao = new FileDao(this.filename);
        userList = fileDao.getList();
        record = new ArrayList<>();
        history = new ArrayList<>();
    }
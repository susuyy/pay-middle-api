package com.ht.feignapi.gdmap.entity;

public class GdMapGeoFence {

    private String name;
    //    private String center;
//    private Integer radius;        // 0-5000m
    private String points;         //多边形围栏外接圆半径最大为5000米。
    //    private boolean enable;
//    private String valid_time;     //yyyy-MM-dd； 请设置2055年之前的日期
    private String repeat;         //Mon,Tues,Wed,Thur,Fri,Sat,Sun
//    private String fix_date;       //格式"date1;date2;date3"； date格式"yyyy-MM-dd"； 最大个数180
//    private String time;           //拼接字符串格式如："startTime1,endTime1;startTime2,endTime2"； 最大个数24；
//    private String desc;
//    private String alert_condition;// 配置触发围栏所需动作,触发动作分号分割 enter;leave（进入、离开触发

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getCenter() {
//        return center;
//    }
//
//    public void setCenter(String center) {
//        this.center = center;
//    }
//
//    public Integer getRadius() {
//        return radius;
//    }
//
//    public void setRadius(Integer radius) {
//        this.radius = radius;
//    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

//    public boolean isEnable() {
//        return enable;
//    }
//
//    public void setEnable(boolean enable) {
//        this.enable = enable;
//    }
//
//    public String getValid_time() {
//        return valid_time;
//    }
//
//    public void setValid_time(String valid_time) {
//        this.valid_time = valid_time;
//    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

//    public String getFix_date() {
//        return fix_date;
//    }
//
//    public void setFix_date(String fix_date) {
//        this.fix_date = fix_date;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }
//
//    public String getDesc() {
//        return desc;
//    }
//
//    public void setDesc(String desc) {
//        this.desc = desc;
//    }
//
//    public String getAlert_condition() {
//        return alert_condition;
//    }
//
//    public void setAlert_condition(String alert_condition) {
//        this.alert_condition = alert_condition;
//    }


}

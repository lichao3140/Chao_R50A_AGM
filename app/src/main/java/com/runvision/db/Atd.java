package com.runvision.db;

public class Atd {
    /**
     * 主键
     */
    private int id;
   private String name; 	//名称
   private String date;	//比对时间
   private int age;	//年龄
   private String number;	//工号
   private String idcard;	//证件号
   private String sex;	//性别
    private String type;//考勤类型
    private String recordtype;//人证类型

    public String getRecordtype() {
        return recordtype;
    }

    public void setRecordtype(String recordtype) {
        this.recordtype = recordtype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Atd() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public Atd(int id,String name, String time, int age, String wordNo, String cardNo, String sex,String type) {
        this.id = id;
        this.name = name;
        this.date = time;
        this.age = age;
        this.number = wordNo;
        this.idcard = cardNo;
        this.sex=sex;
        this.type=type;
    }

}

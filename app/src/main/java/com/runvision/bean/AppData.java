package com.runvision.bean;

import android.graphics.Bitmap;

import com.runvision.db.User;

/**
 * Created by Administrator on 2018/5/31.
 */

public class AppData {


    private String CardNo;// 证件号
    private String Name;// 姓名
    private String sex;// 性别
    private String Nation;// 名族代码

    private String Birthday;// 生日
    private String Address;// 地址
    private float CompareScore;// 人证比对的比分
    private float OneCompareScore;//1:1比分

    private Bitmap faceBmp;
    private Bitmap cardBmp;
    private Bitmap onefacebmp;
    private Bitmap nfaceBmp=null;

    private String errormsgidnum="";
    private String errormsgname="";
    private String errormsgpicname="";
    private String erroemsg="";

    public String getErroemsg() {
        return erroemsg;
    }

    public void setErroemsg(String erroemsg) {
        this.erroemsg = erroemsg;
    }

    public String getErrormsgidnum() {
        return errormsgidnum;
    }

    public void setErrormsgidnum(String errormsgidnum) {
        this.errormsgidnum = errormsgidnum;
    }

    public String getErrormsgname() {
        return errormsgname;
    }

    public void setErrormsgname(String errormsgname) {
        this.errormsgname = errormsgname;
    }

    public String getErrormsgpicname() {
        return errormsgpicname;
    }

    public void setErrormsgpicname(String errormsgpicname) {
        this.errormsgpicname = errormsgpicname;
    }
    /////////////////////////////////////////////////////////////////////////////////////

    private String facepicName;

    private User user;

    private String updatedeviceip;

    public Bitmap getFrontbmp() {
        return frontbmp;
    }

    public void setFrontbmp(Bitmap frontbmp) {
        this.frontbmp = frontbmp;
    }

    private Bitmap frontbmp;


    public String getAtd_up_startime() {
        return atd_up_startime;
    }

    public void setAtd_up_startime(String atd_up_startime) {
        this.atd_up_startime = atd_up_startime;
    }

    public String getAtd_up_endtime() {
        return atd_up_endtime;
    }

    public void setAtd_up_endtime(String atd_up_endtime) {
        this.atd_up_endtime = atd_up_endtime;
    }

    public String getAtd_down_startime() {
        return atd_down_startime;
    }

    public void setAtd_down_startime(String atd_down_startime) {
        this.atd_down_startime = atd_down_startime;
    }

    public String getAtd_down_endtime() {
        return atd_down_endtime;
    }

    public void setAtd_down_endtime(String atd_down_endtime) {
        this.atd_down_endtime = atd_down_endtime;
    }

    private String atd_up_startime;
    private String atd_up_endtime;
    private String atd_down_startime;
    private String atd_down_endtime;

    public String getSnapImageID() {
        return SnapImageID;
    }

    public void setSnapImageID(String snapImageID) {
        SnapImageID = snapImageID;
    }

    private String SnapImageID;


    public String getAtd_type() {
        return atd_type;
    }

    public void setAtd_type(String atd_type) {
        this.atd_type = atd_type;
    }

    private String atd_type;

///////////////////////////////////////////////////////////////////
    private int flag=0;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private static AppData appData=new AppData();

    public static AppData getAppData(){
        return appData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

///////////////////////////////////////////////////////////////////////////////////////

    public Bitmap getNFaceBmp() {
        return nfaceBmp;
    }

    public void SetNFaceBmp(Bitmap nfaceBmp) {
        this.nfaceBmp=nfaceBmp;
    }

    public Bitmap getOneFaceBmp() {
        return onefacebmp;
    }

    public void setOneFaceBmp(Bitmap onefacebmp) {
        this.onefacebmp = onefacebmp;
    }

///////////////////////////////////////////////////////////////////////////////////////
    public Bitmap getFaceBmp() {
        return faceBmp;
    }

    public void setFaceBmp(Bitmap faceBmp) {
        this.faceBmp = faceBmp;
    }

    public String getCardNo() {
        return CardNo;
    }

    public void setCardNo(String cardNo) {
        CardNo = cardNo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return Nation;
    }

    public void setNation(String nation) {
        Nation = nation;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public float getCompareScore() {
        return CompareScore;
    }

    public void setCompareScore(float compareScore) {
        CompareScore = compareScore;
    }

    public float getoneCompareScore() {
        return OneCompareScore;
    }

    public void setoneCompareScore(float onecompareScore) {
        OneCompareScore = onecompareScore;
    }

    public Bitmap getCardBmp() {
        return cardBmp;
    }

    public void setCardBmp(Bitmap cardBmp) {
        this.cardBmp = cardBmp;
    }

    public void clean() {
        appData = new AppData();
    }

    public String getUpdatedeviceip() {
        return updatedeviceip;
    }

    public void setUpdatedeviceip(String updatedeviceip) {
        this.updatedeviceip = updatedeviceip;
    }


}



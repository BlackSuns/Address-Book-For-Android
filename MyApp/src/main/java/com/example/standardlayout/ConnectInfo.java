package com.example.standardlayout;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.Serializable;

//实现Serializable接口，使得该类的对象可以被序列化在Intent中传输
public class ConnectInfo implements Serializable {
    private String name;
    private String sex;
    private String phone;
    private String phone2;
    //Bitmap实现的是Parcelable，没有实现Serializable，因此不能序列化。使用transient关键字可以避免其被序列化而报错
    //private transient Bitmap avatar;
    private byte[] avatar;
    private String qq;
    private String email;
    private String group_name;

    public ConnectInfo() {

    }

    public ConnectInfo(String name, String phone, String sex) {
        this.setName(name);
        this.setPhone(phone);
        this.setSex(sex);
    }

    public ConnectInfo(String name, String phone) {
        this.setName(name);
        this.setPhone(phone);
    }

    public ConnectInfo(String name, String sex, String phone, String phone2, byte[] avatar, String qq, String email) {
        this.setName(name);
        this.setSex(sex);
        this.setPhone(phone);
        this.setPhone2(phone2);
        this.setAvatar(avatar);
        this.setqq(qq);
        this.setEmail(email);
    }

    public Bitmap getAvatar() {
        if (avatar!=null){
            return Tool.getBitmap(avatar);
        }else{
            return  null;
        }
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.length() == 0) {
            this.email = null;
        } else {
            this.email = email;
        }
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String num) {
        this.phone = num.replaceAll("-", "");
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        if (phone2 == null || phone2.length() == 0) {
            this.phone2 = null;
        } else {
            this.phone2 = phone2.replaceAll("-", "");
        }
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getqq() {
        return qq;
    }

    public void setqq(String qq) {
        if (qq == null || qq.length() == 0) {
            this.qq = null;
        } else {
            this.qq = qq;
        }
    }

    @Override
    public String toString() {
        return "@姓名@" + name + "@号码@" + phone + "@性别@" + sex + "@号码2@" + phone2 + "@QQ@" + qq + "@邮件@" + email;
    }
}

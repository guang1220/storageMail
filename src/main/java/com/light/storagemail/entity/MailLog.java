package com.light.storagemail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

//如果实体类名与表名不一致，可以用该注解映射表名。如果没有该注解，映射名默认为类名
@TableName(value = "mailLog")
public class MailLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String type;
    private String sendTime;
    private String toUser;

    public MailLog(String type, String sendTime, String toUser) {
        this.type = type;
        this.sendTime = sendTime;
        this.toUser = toUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    @Override
    public String toString() {
        return "MailLog{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", toUser='" + toUser + '\'' +
                '}';
    }
}

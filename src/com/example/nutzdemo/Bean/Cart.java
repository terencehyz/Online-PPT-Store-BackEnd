package com.example.nutzdemo.Bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("cart")
public class Cart {
    @Id
    private int id;
    @Column
    private int Pid;
    @Column
    private int Uid;

    public int getPid() {
        return Pid;
    }

    public void setPid(int pid) {
        Pid = pid;
    }

    public void setUid(int uid) {
        Uid = uid;
    }

    public int getUid() {
        return Uid;
    }
}

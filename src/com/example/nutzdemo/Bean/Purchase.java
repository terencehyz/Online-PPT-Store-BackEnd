package com.example.nutzdemo.Bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("purchase")
public class Purchase {
    @Id
    private int id;
    @Column
    private int Uid;
    @Column
    private int Pid;

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

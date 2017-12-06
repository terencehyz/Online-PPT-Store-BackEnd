package com.example.nutzdemo.Bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("verification_code")
public class VerificationCode {
    @Id
    private int id;
    @Column
    private String code;
    @Column
    private String email;
    @Column
    private int vaild;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getVaild() {
        return vaild;
    }

    public void setVaild(int vaild) {
        this.vaild = vaild;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

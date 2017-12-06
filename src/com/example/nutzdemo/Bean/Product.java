package com.example.nutzdemo.Bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("product")
public class Product {
    @Id
    private int id;
    @Column
    private String Title;
    @Column
    private String Description;
    @Column
    private String Thumbnail;
    @Column
    private String Download;
    @Column
    private int FavoriteCount;
    @Column
    private int Type;
    @Column
    private int Way;
    @Column
    private double value;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getFavoriteCount() {
        return FavoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        FavoriteCount = favoriteCount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getDownload() {
        return Download;
    }

    public void setDownload(String download) {
        Download = download;
    }

    public void setWay(int way) {
        Way = way;
    }

    public int getWay() {
        return Way;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}

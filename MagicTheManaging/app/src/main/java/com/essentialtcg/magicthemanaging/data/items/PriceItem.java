package com.essentialtcg.magicthemanaging.data.items;

/**
 * Created by Shawn on 5/10/2016.
 */
public class PriceItem {

    private int id;
    private float highPrice;
    private float averagePrice;
    private float lowPrice;
    private float foilPrice;
    private String link;

    public PriceItem() {

    }

    public PriceItem(int id, float highPrice, float averagePrice, float lowPrice,
                     float foilPrice, String link) {
        this.highPrice = highPrice;
        this.averagePrice = averagePrice;
        this.lowPrice = lowPrice;
        this.foilPrice = foilPrice;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(float highPrice) {
        this.highPrice = highPrice;
    }

    public float getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(float averagePrice) {
        this.averagePrice = averagePrice;
    }

    public float getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(float lowPrice) {
        this.lowPrice = lowPrice;
    }

    public float getFoilPrice() {
        return foilPrice;
    }

    public void setFoilPrice(float foilPrice) {
        this.foilPrice = foilPrice;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

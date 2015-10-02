package br.com.vsgdev.leilaodojo.models;

import java.util.Calendar;

/**
 * Created by ascaloners on 10/1/15.
 */
public class Auction {
    private int id;
    private Product product;
    private Calendar createdAt;
    private Calendar endAt;
    //quem esta leiloando
    private User owner;
    //oferta atualmente vencedora
    private double lastBid;
    //quem esta ganhando o leilao
    private User bidOwner;

    public Auction() {
    }

    public Auction(int id, Product product, Calendar createdAt, Calendar endAt, User owner, double lastBid, User bidOwner) {
        this.id = id;
        this.product = product;
        this.createdAt = createdAt;
        this.endAt = endAt;
        this.owner = owner;
        this.lastBid = lastBid;
        this.bidOwner = bidOwner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public Calendar getEndAt() {
        return endAt;
    }

    public void setEndAt(Calendar endAt) {
        this.endAt = endAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public double getLastBid() {
        return lastBid;
    }

    public void setLastBid(double lastBid) {
        this.lastBid = lastBid;
    }

    public User getBidOwner() {
        return bidOwner;
    }

    public void setBidOwner(User bidOwner) {
        this.bidOwner = bidOwner;
    }
}

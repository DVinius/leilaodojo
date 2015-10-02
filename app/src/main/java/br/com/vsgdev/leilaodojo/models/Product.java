package br.com.vsgdev.leilaodojo.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Classe que representa os produtos do leiloados.
 */
public class Product {
    private int id;
    public Bitmap imgProduto;
    public String nomeProduto;
    public String descProduto;
    public double valorEstimado;
    //ultimo lance ofertado neste produto
    public double lastBid;
    private int tempo;

    public Product() {
        super();
    }

    public Product(int id) {
        this.id = id;
    }

    public Product(Bitmap imgProduto, String nomeProduto, String descProduto, float valorEstimado, int tempo) {
        this.imgProduto = imgProduto;
        this.nomeProduto = nomeProduto;
        this.descProduto = descProduto;
        this.valorEstimado = valorEstimado;
        this.tempo = tempo;
    }

    public double getLastBid() {
        return lastBid;
    }

    public void setLastBid(double lastBid) {
        this.lastBid = lastBid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImgProduto() {
        return imgProduto;
    }

    public void setImgProduto(Bitmap imgProduto) {
        this.imgProduto = imgProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getDescProduto() {
        return descProduto;
    }

    public void setDescProduto(String descProduto) {
        this.descProduto = descProduto;
    }

    public double getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(double valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Product){
            return getId() == ((Product) o).getId();
        }
        return super.equals(o);
    }
}

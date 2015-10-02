package br.com.vsgdev.leilaodojo.activities;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.models.Product;

public class Adapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> list;
    LayoutInflater inflater;

    public Adapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final Product product){
        this.list.add(product);
    }

    public Product getLast(){
        if (this.list != null){
            return getItem(this.list.size()-1);
        }
        return null;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Product getItem(int position) {
        if (!this.list.isEmpty()){
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Holder productHolder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_protuct, parent, false);
            productHolder = new Holder();
            productHolder.holderDescricao = (TextView) view.findViewById(R.id.tv_descricao);
            productHolder.holderNome = (TextView) view.findViewById(R.id.tv_nomeProduto);
            productHolder.lastBid = (TextView) view.findViewById(R.id.tv_ultimoLance);
            productHolder.holderImagem = (ImageView) view.findViewById(R.id.iv_imgProduto);

            view.setTag(productHolder);
        } else {
            view = convertView;
            productHolder = (Holder) view.getTag();
        }

        //coleta o item da posicao e adiciona os valores aos campos da lista
        final Product auctionProduct = getItem(position);
        productHolder.holderNome.setText(auctionProduct.getNomeProduto());
        productHolder.holderDescricao.setText(auctionProduct.getDescProduto());
        //formatar para R$ N.NNN,NN
        DecimalFormat last = new DecimalFormat("####.##");
        productHolder.lastBid.setText("R$ "+last.format(auctionProduct.getLastBid()));
        //a exibicao da imagem será tratada em evento posterior. No momento está exibindo o icone do launcher
        if (auctionProduct.getImgProduto() == null){
            productHolder.holderImagem.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        } else {
            productHolder.holderImagem.setImageBitmap(auctionProduct.getImgProduto());
        }

        return view;
    }

    public class Holder {
        TextView holderNome;
        TextView holderDescricao;
        TextView lastBid;
        ImageView holderImagem;
    }
}


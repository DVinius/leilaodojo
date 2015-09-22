package br.com.vsgdev.leilaodojo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.models.Product;

public class NewProduct extends AppCompatActivity {
    public EditText nome;
    public EditText descricao;
    public EditText valor;
    private EditText tempoLeilao;
    public Button cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        nome = (EditText) findViewById(R.id.edt_nome_produto);
        descricao = (EditText) findViewById(R.id.edt_desc_produto);
        valor = (EditText)findViewById(R.id.edt_valor_produto);
        tempoLeilao = (EditText) findViewById(R.id.et_tempo_new_product);
        cadastrar = (Button)findViewById(R.id.bt_ok);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = nome.getText().toString();
                String d = descricao.getText().toString();
                float va = Float.parseFloat(valor.getText().toString());
                int tempo = Integer.valueOf(tempoLeilao.getText().toString());
                Product product = new Product(null, n, d, va, tempo);
            }
        });
    }


}

package br.com.vsgdev.leilaodojo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.models.Product;

public class ViewActivity extends AppCompatActivity {

    private Button btn;
    private ImageView img;
    private TextView txt_nome;
    private TextView txt_desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent intent = getIntent();
        txt_desc = (TextView)findViewById(R.id.view_text_desc);
        txt_nome = (TextView)findViewById(R.id.view_txt_campo_descricao_item);
        if(intent.hasExtra("nomeProduto")){
            txt_nome.setText(intent.getStringExtra("nomeProduto"));
            txt_desc.setText(intent.getStringExtra("descProduto"));
        }

    }

    public void camera(View view){

    }

    public void open(){



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

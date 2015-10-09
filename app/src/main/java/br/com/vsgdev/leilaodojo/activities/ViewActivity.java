package br.com.vsgdev.leilaodojo.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.models.User;
import br.com.vsgdev.leilaodojo.utils.DateTimeUtils;
import br.com.vsgdev.leilaodojo.utils.JSONConverter;
import br.com.vsgdev.leilaodojo.utils.WebServiceUtils;

public class ViewActivity extends AppCompatActivity {

    private Button btn;
    private ImageView img;
    private TextView txt_nome;
    private TextView txt_desc;
    private TextView tvEndAt;
    private EditText etMakeBid;
    private Button btnMakeBid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        img = (ImageView) findViewById(R.id.imv_produto);
        txt_desc = (TextView) findViewById(R.id.view_text_desc);
        txt_nome = (TextView) findViewById(R.id.view_txt_campo_descricao_item);
        tvEndAt = (TextView) findViewById(R.id.tv_end_time);
        etMakeBid = (EditText) findViewById(R.id.lance);
        btnMakeBid = (Button) findViewById(R.id.btn_make_a_bid);

        final Activity activity = this;

        btnMakeBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bid = etMakeBid.getText().toString();{
                    if (!bid.isEmpty()){
                        final Double doubleBid = Double.parseDouble(bid);
                        if (MainActivity.auction.getBidOwner() == null || MainActivity.auction.getLastBid() <= doubleBid){
                            //make a bid
                            MainActivity.auction.setBidOwner(MainActivity.currentUser);
                            MainActivity.auction.setLastBid(doubleBid);
                            WebServiceUtils.makeABid(MainActivity.auction, activity);
                        }
                    }
                }
            }
        });

        if (MainActivity.auction != null) {
            img.setImageBitmap(MainActivity.auction.getProduct().getImgProduto());
            txt_nome.setText(MainActivity.auction.getProduct().getNomeProduto());
            txt_desc.setText(MainActivity.auction.getProduct().getDescProduto());
            final String endDate = DateTimeUtils.getEndAtRemainingTime(MainActivity.auction);
            tvEndAt.setText(getString(R.string.end_at_hours, endDate));

            final User currentUser = MainActivity.currentUser;

            if (currentUser != null && MainActivity.auction.getOwner() != null && MainActivity.currentUser.getEmail().equals(MainActivity.auction.getOwner().getEmail())){
                etMakeBid.setHint(getString(R.string.you_are_this_auction_owner));
                etMakeBid.setEnabled(false);
                btnMakeBid.setEnabled(false);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        MainActivity.auction = null;
        super.onBackPressed();
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

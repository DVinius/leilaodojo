package br.com.vsgdev.leilaodojo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.ArrayList;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.models.Auction;
import br.com.vsgdev.leilaodojo.models.Product;
import br.com.vsgdev.leilaodojo.models.User;
import br.com.vsgdev.leilaodojo.services.RegistrationIntentService;
import br.com.vsgdev.leilaodojo.utils.JSONConverter;
import br.com.vsgdev.leilaodojo.utils.QuickstartPreferences;
import br.com.vsgdev.leilaodojo.utils.WebServiceUtils;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1;
    private ListView produtos;
    private Adapter productAdapter;
    public static Auction auction;
    public static ProgressBar pbLoadItens;
    private GoogleApiClient mGoogleApiClient;
    public static User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_main);

        if (pbLoadItens == null) {
            pbLoadItens = (ProgressBar) findViewById(R.id.pb_load_auctions);
        }

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (sentToken) {
                    // mInformationTextView.setText(getString(R.string.gcm_send_message));
                    Log.d("MainActivity", "sever already has the gcm token");
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                    Log.e("MainActivity", "sever does not have the token");
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        produtos = (ListView) findViewById(R.id.lv_Produtos);
        final ArrayList<Auction> listProducts = new ArrayList<>();
        Product produto = new Product(null, "moto", "Honda", 7000, 48);
        produto.setId(0);
        //listProducts.add(produto);
        productAdapter = new Adapter(this, listProducts);
        produtos.setAdapter(productAdapter);
        produtos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                auction = listProducts.get(position);
                startActivity(intent);
            }
        });
    }

    protected void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(new Scope(Scopes.PROFILE))
                    .addScope(new Scope(Scopes.PLUS_ME))
                    .addScope(new Scope(Scopes.EMAIL))
                    .build();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        pbLoadItens.setVisibility(View.VISIBLE);
        updateAuctionList();
    }

    private void updateAuctionList() {
        WebServiceUtils.getNextAuction(productAdapter, this);
    }

    @Override
    protected void onPause() {
        mGoogleApiClient.disconnect();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("tag", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_new_product) {
            Intent i = new Intent(this, NewProduct.class);
            startActivity(i);
        }
        if (id == R.id.action_nearest_auctions) {
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            final SharedPreferences prefs = getSharedPreferences(QuickstartPreferences.PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(QuickstartPreferences.IS_SIGNED_IN_WITH_PLUS, true).apply();

            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            final User user = new User();
            user.setEmail(email);
            user.setDeviceId(JSONConverter.deviceId);
            WebServiceUtils.registerUser(user, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        final SharedPreferences prefs = getSharedPreferences(QuickstartPreferences.PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(QuickstartPreferences.IS_SIGNED_IN_WITH_PLUS, false).apply();
    }
}

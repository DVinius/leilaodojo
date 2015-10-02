package br.com.vsgdev.leilaodojo.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.utils.JSONConverter;

public class LoginActivity
        extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    public Button bt_login_registre;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //constroi
        buildGoogleApiClient();

        bt_login_registre = (Button) findViewById(R.id.button_login_activity_register);
        bt_login_registre.setOnClickListener(this);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        JSONConverter.deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    protected void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(new Scope(Scopes.PROFILE))
                    .addScope(new Scope(Scopes.PLUS_ME))
                    .build();
        }
    }

    @Override
    public void onClick(View v) {
        if (bt_login_registre.isPressed()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        if (v.getId() == findViewById(R.id.sign_in_button).getId()){
            mGoogleApiClient.connect();
        }
    }

    /**
     * Metodo chamado quando a conexao com o
     * ApiClient é estabelecida com sucesso
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {

        //Verifica se existe usuario logado, e exibe infos deste
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            Log.e("","success");

            //Cria tela para compartilhamento de conteudo no Plus (postagem)
            Intent shareIntent = new PlusShare.Builder(this)
                    .setType("text/plain")
                    .setText("Welcome to the Google+ platform.")
                    .setContentUrl(Uri.parse("https://developers.google.com/+/"))
                    .getIntent();

            startActivityForResult(shareIntent, 0);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //caindo neste caso
        if (result.hasResolution()) {
            try {
                //mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                //mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
}
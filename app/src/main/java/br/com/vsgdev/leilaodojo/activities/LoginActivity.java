package br.com.vsgdev.leilaodojo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.utils.JSONConverter;
import br.com.vsgdev.leilaodojo.utils.QuickstartPreferences;

public class LoginActivity
        extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    public Button bt_login_registre;
    private GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        final AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null){
            Log.d("AccessTkn",token.toString());
            //startActivity(new Intent(this, MainActivity.class));
            //sendFBIDtoServer();
        }

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("AccessTkn",loginResult.getAccessToken().toString());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("AccessTknExc", exception.getMessage());
            }

        });



        final SharedPreferences sharedPreferences = getSharedPreferences(QuickstartPreferences.PREFS_NAME, Context.MODE_PRIVATE);
        final boolean isPlusSignedId = sharedPreferences.getBoolean(QuickstartPreferences.IS_SIGNED_IN_WITH_PLUS, false);
        if (isPlusSignedId) {
            //startActivity(new Intent(this, MainActivity.class));
            //finish();
        }

        setContentView(R.layout.activity_login);

        //constroi
        buildGoogleApiClient();

        bt_login_registre = (Button) findViewById(R.id.button_login_activity_register);
        bt_login_registre.setOnClickListener(this);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        JSONConverter.deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
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
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode == RESULT_OK) {
                final SharedPreferences prefs = getSharedPreferences(QuickstartPreferences.PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putBoolean(QuickstartPreferences.IS_SIGNED_IN_WITH_PLUS, true).apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (bt_login_registre.isPressed()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        if (v.getId() == findViewById(R.id.sign_in_button).getId()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Metodo chamado quando a conexao com o
     * ApiClient é estabelecida com sucesso
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {

        //Verifica se existe usuario logado, e exibe infos deste
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            final SharedPreferences prefs = getSharedPreferences(QuickstartPreferences.PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(QuickstartPreferences.IS_SIGNED_IN_WITH_PLUS, true).apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();

            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            Log.e("", "success");


            //Cria tela para compartilhamento de conteudo no Plus (postagem)
            /*
            Intent shareIntent = new PlusShare.Builder(this)
                    .setType("text/plain")
                    .setText("Welcome to the Google+ platform.")
                    .setContentUrl(Uri.parse("https://developers.google.com/+/"))
                    .getIntent();

            startActivityForResult(shareIntent, 0);
            */
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
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
package br.com.vsgdev.leilaodojo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.vsgdev.leilaodojo.R;
import br.com.vsgdev.leilaodojo.utils.JSONConverter;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public Button bt_login_registre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bt_login_registre = (Button) findViewById(R.id.button_login_activity_register);
        bt_login_registre.setOnClickListener(this);

        JSONConverter.deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onClick(View v) {
        if (bt_login_registre.isPressed()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
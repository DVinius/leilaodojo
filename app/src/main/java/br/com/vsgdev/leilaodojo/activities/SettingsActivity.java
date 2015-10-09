package br.com.vsgdev.leilaodojo.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import br.com.vsgdev.leilaodojo.R;

/**
 * Esta é a activity responsável por exibir a tela de configurações.
 *
 * Created by ascaloners on 9/8/15.
 */
public class SettingsActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_settings);
    }
}

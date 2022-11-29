package com.example.elchinoadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class CajaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);
    }


    /*Personalizacion de la navegacion hacia atras!!
    #################################################################################################*/
    @Override
    public void onBackPressed(){
        boton_atras();
    }

    private void boton_atras() {
        Intent Main = new Intent(this, MainActivity.class);
        Main.putExtra("retorno", "logedIn");
        startActivity(Main);
        finish();
        System.exit(0);
    }
    //#################################################################################################
}
package com.example.elchinoadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HoyActivity extends AppCompatActivity {

    public TextView et_monto_prestado;
    public TextView et_balance_general;
    public TextView et_monto_mora;
    public TextView et_monto_recuperado;
    private TextView tv_saludo;
    public TextView tv_monto_prestado;
    public TextView tv_balance_general;
    public TextView tv_monto_mora;
    public TextView tv_monto_recuperado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);

        et_monto_prestado = (TextView) findViewById(R.id.et_monto_prestado);
        et_balance_general = (TextView) findViewById(R.id.et_balance_general);
        et_monto_mora = (TextView) findViewById(R.id.et_monto_mora);
        et_monto_recuperado = (TextView) findViewById(R.id.et_monto_recuperado);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("Cierre de hoy");
        tv_monto_prestado = (TextView) findViewById(R.id.tv_monto_prestado);
        tv_balance_general = (TextView) findViewById(R.id.tv_balance_general);
        tv_monto_mora = (TextView) findViewById(R.id.tv_monto_mora);
        tv_monto_recuperado = (TextView) findViewById(R.id.tv_monto_recuperado);







    }

    /*Personalizacion de la navegacion hacia atras!!
    #################################################################################################*/
    @Override
    public void onBackPressed(){
        boton_atras();
    }

    private void boton_atras() {
        Intent Main = new Intent(this, MainActivity.class);
        startActivity(Main);
        finish();
        System.exit(0);
    }
    //#################################################################################################

}
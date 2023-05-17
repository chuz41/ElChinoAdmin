package com.example.elchinoadmin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.elchinoadmin.Util.DateUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Agregar_cobrador extends AppCompatActivity {

    private Map<String, Integer> meses = new HashMap<String, Integer>();
    private String dia;
    private String mes;
    private String anio;
    private String fecha;
    private String hora;
    private String minuto;
    private String nombre_dia;
    private String spreadsheet_cobradores = "1y5wRGgrkH48EWgd2OWwon_Um42mxN94CdmJSi_XCwvM";
    private String readRowURL = "https://script.google.com/macros/s/AKfycbxJNCrEPYSw8CceTwPliCscUtggtQ2l_otieFmE/exec?spreadsheetId=";
    private String addRowURL = "https://script.google.com/macros/s/AKfycbweyYb-DHVgyEdCWpKoTmvOxDGXleawjAN8Uw9AeJYbZ24t9arB/exec";
    private String sheet_cobradores = "cobradores";
    private String sheet_creditos = "creditos";
    private String sheet_abonos = "abonos";
    private TextView tvSaludo;
    private TextView tvNombre;
    private EditText etNombre;
    private TextView tvApodo;
    private EditText etApodo;
    private TextView tvTelefono;
    private EditText etTelefono;
    private TextView tvUsuario;
    private EditText etUsuario;
    private TextView tvPassword;
    private EditText etPassword;
    private Button btConfirmar;
    private Button btCancelar;
    private Date fecha_hoy = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cobrador);

        tvSaludo = (TextView) findViewById(R.id.tvSaludo);
        tvNombre = (TextView) findViewById(R.id.tvNombre);
        etNombre = (EditText) findViewById(R.id.etNombre);
        tvApodo = (TextView) findViewById(R.id.tvApodo);
        etApodo = (EditText) findViewById(R.id.etApodo);
        tvTelefono = (TextView) findViewById(R.id.tvTelefono);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        tvUsuario = (TextView) findViewById(R.id.tvUsuario);
        etUsuario = (EditText) findViewById(R.id.etUsuario);
        tvPassword = (TextView) findViewById(R.id.tvPassword);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btCancelar = (Button) findViewById(R.id.btCancelar);
        btConfirmar = (Button) findViewById(R.id.btConfirmar);

        Date fecha_hoy_D = Calendar.getInstance().getTime();
        String fecha_hoy_S = DateUtilities.dateToString(fecha_hoy_D);
        String[] split_fecha_hoy = fecha_hoy_S.split("-");
        fecha_hoy_S = split_fecha_hoy[2] + "/" + split_fecha_hoy[1] + "/" + split_fecha_hoy[0];
        String[] split_fecha_hoy_s = fecha_hoy_S.split("/");
        fecha_hoy_S = split_fecha_hoy_s[2] + "-" + split_fecha_hoy_s[1] + "-" + split_fecha_hoy_s[0];
        try {
            fecha_hoy = DateUtilities.stringToDate(fecha_hoy_S);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        separar_fechaYhora();
    }

    public void cancelar (View veiw) {
        //abrir un cuadro de dialogo con dos opciones, si y no. Si se selecciona si entonces se llama al metodo boton_atras(), si se selecciona no entonces se cierra el cuadro de dialogo
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Volver")
                .setMessage("¿Estas seguro de que quieres salir sin guardar?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boton_atras();
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    public void confirmar (View view) {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_input_add)
                .setTitle("Guardar")
                .setMessage("¿Estas seguro de que deseas guardar al nuevo cobrador " + etNombre.getText().toString() + "?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        guardarCobrador();
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    private void guardarCobrador () {

    }


    private void separar_fechaYhora () {
        llenar_mapa_meses();
        Date now = fecha_hoy;
        String ahora = now.toString();
        String[] split = ahora.split(" ");
        nombre_dia = split[0];
        dia = split[2];
        mes = String.valueOf(meses.get(split[1]));
        anio = split[5];
        String hora_completa = split[3];
        fecha = split[2];
        split = hora_completa.split(":");
        minuto = split[1];
        hora = split[0];
    }

    private void llenar_mapa_meses() {
        meses.put("Jan", 1);
        meses.put("Feb", 2);
        meses.put("Mar", 3);
        meses.put("Apr", 4);
        meses.put("May", 5);
        meses.put("Jun", 6);
        meses.put("Jul", 7);
        meses.put("Aug", 8);
        meses.put("Sep", 9);
        meses.put("Oct", 10);
        meses.put("Nov", 11);
        meses.put("Dec", 12);
        meses.put("1", 1);
        meses.put("2", 2);
        meses.put("3", 3);
        meses.put("4", 4);
        meses.put("5", 5);
        meses.put("6", 6);
        meses.put("7", 7);
        meses.put("8", 8);
        meses.put("9", 9);
        meses.put("10", 10);
        meses.put("11", 11);
        meses.put("12", 12);
    }

    /*Personalizacion de la navegacion hacia atras!!
    #################################################################################################*/
    @Override
    public void onBackPressed() {
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

    private boolean verificar_internet() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(this, "Debe estar conectado a una red WiFi o datos mobiles.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            //Si esta conectado a internet.
            //Toast.makeText(this, "Conectado a internet!", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private boolean archivo_existe(String[] archivos, String file_name) {
        for (int i = 0; i < archivos.length; i++) {
            if (file_name.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

}

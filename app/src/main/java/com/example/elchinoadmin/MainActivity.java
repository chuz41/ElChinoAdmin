package com.example.elchinoadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView tv_saludo;
    private TextView tv_fecha;
    private TextView tv_esperar;
    private EditText et_ID;
    private Button bt_aprob_recha;
    private Button bt_cierre_rango;
    private Button bt_finanzas;
    private Button bt_caja;
    private Button bt_hoy;
    private Button bt_agregar_cobrador;
    private Map<String, Integer> meses = new HashMap<String, Integer>();
    private String dia;
    private String mes;
    private String anio;
    private String fecha;
    private String hora;
    private String minuto;
    private String nombre_dia;
    private String autent = "file_autent.txt";
    private String spreadsheet_cobradores = "1y5wRGgrkH48EWgd2OWwon_Um42mxN94CdmJSi_XCwvM";
    private String readRowURL = "https://script.google.com/macros/s/AKfycbxJNCrEPYSw8CceTwPliCscUtggtQ2l_otieFmE/exec?spreadsheetId=";
    private String addRowURL = "https://script.google.com/macros/s/AKfycbweyYb-DHVgyEdCWpKoTmvOxDGXleawjAN8Uw9AeJYbZ24t9arB/exec";
    private boolean flag_salir = false;
    private String user;
    private String password;
    private boolean flag_pedir_cambio = false;
    private String password1 = "";
    private String password2 = "";
    private String retorno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        retorno = getIntent().getStringExtra("retorno");
        tv_saludo.setText("*** MENU PRINCIPAL ***");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        tv_esperar.setVisibility(View.INVISIBLE);
        et_ID = (EditText) findViewById(R.id.et_ID);
        et_ID.setVisibility(View.INVISIBLE);
        bt_aprob_recha = (Button) findViewById(R.id.bt_aprob_recha);
        bt_cierre_rango = (Button) findViewById(R.id.bt_cierre_rango);
        bt_cierre_rango.setVisibility(View.INVISIBLE);
        bt_finanzas = (Button) findViewById(R.id.bt_finanzas);
        bt_caja = (Button) findViewById(R.id.bt_caja);
        bt_hoy = (Button) findViewById(R.id.bt_hoy);
        bt_hoy.setVisibility(View.INVISIBLE);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        bt_agregar_cobrador = (Button) findViewById(R.id.bt_agregar_cobrador);
        separar_fechaYhora();
        String fecha_hoy = fecha + "/" + mes + "/" + anio;
        tv_fecha.setText(fecha_hoy);
        flag_salir = false;
        crear_archivos_config();
        if (retorno != null) {
            if (retorno.equals("logedIn")) {
                mostrar_todo();
            } else {
                revisar_archivos_autent();
            }
        } else {
            revisar_archivos_autent();
        }
    }

    private void crear_archivos_config() {
        String[] archivos = fileList();
        if (archivo_existe(archivos, "creditos.txt")) {
            crear_archivo("creditos.txt");
        }
        if (!archivo_existe(archivos, "abonos.txt")) {
            crear_archivo("abonos.txt");crear_archivo("abonos.txt");
        }
        if (!archivo_existe(archivos, "solicitudes.txt")) {
            crear_archivo("solicitudes.txt");
        }
    }

    private String obtener_dia_espaniol (String dia_ingles) {
        //Sun, Mon, Tue, Wed, Thu, Fri, Sat.
        String flag = "";
        if (dia_ingles.equals("Sun")) {
            flag = "Domingo";
        } else if (dia_ingles.equals("Mon")) {
            flag = "Lunes";
        } else if (dia_ingles.equals("Tue")) {
            flag = "Martes";
        } else if (dia_ingles.equals("Wed")) {
            flag = "Miercoles";
        } else if (dia_ingles.equals("Thu")) {
            flag = "Jueves";
        } else if (dia_ingles.equals("Fri")) {
            flag = "Viernes";
        } else if (dia_ingles.equals("Sat")) {
            flag = "Sabado";
        }
        return flag;
    }

    private String obtener_dia_ingles (String dia_espaniol) {
        //Sun, Mon, Tue, Wed, Thu, Fri, Sat.
        String flag = "";
        if (dia_espaniol.equals("Domingo")) {
            flag = "Sun";
        } else if (dia_espaniol.equals("Lunes")) {
            flag = "Mon";
        } else if (dia_espaniol.equals("Martes")) {
            flag = "Tue";
        } else if (dia_espaniol.equals("Miercoles")) {
            flag = "Wed";
        } else if (dia_espaniol.equals("Jueves")) {
            flag = "Thu";
        } else if (dia_espaniol.equals("Viernes")) {
            flag = "Fri";
        } else if (dia_espaniol.equals("Sabado")) {
            flag = "Sat";
        }
        return flag;
    }

    private void ocultar_todo () {
        tv_saludo.setText("Login...");
        tv_esperar.setVisibility(View.VISIBLE);
        et_ID.setVisibility(View.VISIBLE);
        bt_agregar_cobrador.setText("CAMBIAR PASSWORD");
        bt_aprob_recha.setVisibility(View.INVISIBLE);
        bt_agregar_cobrador.setVisibility(View.INVISIBLE);
        bt_cierre_rango.setVisibility(View.INVISIBLE);
        bt_finanzas.setText("CONFIRMAR");
        bt_caja.setVisibility(View.INVISIBLE);
        bt_hoy.setVisibility(View.INVISIBLE);
    }

    private void mostrar_todo () {
        tv_saludo.setText("*** MENU PRINCIPAL ***");
        tv_esperar.setVisibility(View.INVISIBLE);
        et_ID.setVisibility(View.INVISIBLE);
        bt_finanzas.setEnabled(true);
        bt_finanzas.setClickable(true);
        bt_finanzas.setVisibility(View.VISIBLE);
        bt_aprob_recha.setVisibility(View.VISIBLE);
        bt_agregar_cobrador.setEnabled(true);
        bt_agregar_cobrador.setText("AGREGAR COBRADOR");
        bt_agregar_cobrador.setClickable(true);
        bt_agregar_cobrador.setVisibility(View.INVISIBLE);
        bt_cierre_rango.setVisibility(View.INVISIBLE);
        bt_finanzas.setText("VER VENDEDOR");
        bt_caja.setVisibility(View.VISIBLE);
        bt_hoy.setVisibility(View.INVISIBLE);
    }

    private void revisar_archivos_autent () {
        ocultar_todo();
        String archivos[] = fileList();
        String contenido = "";
        if (archivo_existe(archivos, autent)) {//Archivo nombre_archivo es el archivo que vamos a imprimir
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(autent));//Se abre archivo
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();//Se lee archivo
                password = "";
                user = "";
                while (linea != null) {
                    Log.v("revisar_archivos0", "Main. Linea: " + linea);
                    String[] split = linea.split(" ");
                    if (split[0].equals("user")) {
                        user = split[1];
                    } else if (split[0].equals("password")) {
                        password = split[1];
                    }
                    contenido = contenido + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            flag_pedir_cambio = true;
        }
        autenticar();
    }

    private void autenticar () {
        if (flag_pedir_cambio) {
            tv_esperar.setText("Debe crear su usuario y password!");
            et_ID.setText("");
            et_ID.setHint("Usuario...");
            et_ID.requestFocus();
        } else {
            tv_esperar.setText("Digite su usuario");
            et_ID.setText("");
            et_ID.setHint("Usuario...");
            et_ID.requestFocus();
        }
    }

    public void hoy (View view) {
        Intent hoy_ac = new Intent(this, HoyActivity.class);
        startActivity(hoy_ac);
        finish();
        System.exit(0);
    }

    public void caja (View view) {
        Intent caja_ac = new Intent(this, CajaActivity.class);
        startActivity(caja_ac);
        finish();
        System.exit(0);
    }

    public void estado_financiero (View view) throws IOException {
        bt_finanzas.setEnabled(false);
        bt_finanzas.setClickable(false);
        et_ID.setEnabled(false);
        if (tv_saludo.getText().toString().equals("Login...")) {
            Log.v("Estado_financiero", "Main.\n\nflag_pedir_cambio: " + flag_pedir_cambio + "\n\n.");
            if (flag_pedir_cambio) {
                if (tv_esperar.getText().toString().equals("Debe crear su usuario y password!") | tv_esperar.getText().toString().equals("Ingrese su nuevo usuario")) {
                    if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                        msg("Debe ingresar un usuario!!!");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.requestFocus();
                        bt_finanzas.setEnabled(true);
                        bt_finanzas.setClickable(true);
                        tv_esperar.setText("Ingrese su nuevo usuario");
                    } else {
                        user = et_ID.getText().toString();
                        tv_esperar.setText("Ingrese su nuevo password");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.setHint("Password...");
                        et_ID.requestFocus();
                        bt_finanzas.setEnabled(true);
                        bt_finanzas.setClickable(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Ingrese su nuevo password")) {
                    if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                        msg("Debe ingresar un password!!!");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.requestFocus();
                        bt_finanzas.setEnabled(true);
                        bt_finanzas.setClickable(true);
                    } else {
                        password1 = et_ID.getText().toString();
                        tv_esperar.setText("Confirme su nuevo password");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.setHint("Password...");
                        et_ID.requestFocus();
                        bt_finanzas.setEnabled(true);
                        bt_finanzas.setClickable(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Confirme su nuevo password")) {
                    if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                        msg("Debe ingresar un password!!!");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.requestFocus();
                        bt_finanzas.setEnabled(true);
                        bt_finanzas.setClickable(true);
                    } else {
                        password2 = et_ID.getText().toString();
                        if (password1.equals(password2)) {
                            password = password1;
                            msg("Ususario y password creados con exito!");
                            guardar_usuario();
                        } else {
                            msg("Passwords no coinciden, trate de nuevo!");
                            tv_esperar.setText("Ingrese su nuevo usuario");
                            et_ID.setEnabled(true);
                            et_ID.setText("");
                            et_ID.setHint("Usuario...");
                            et_ID.requestFocus();
                            bt_finanzas.setEnabled(true);
                            bt_finanzas.setClickable(true);
                        }
                    }
                }
            } else {
                //autenticar normal...
                bt_agregar_cobrador.setVisibility(View.INVISIBLE);
                if (tv_esperar.getText().toString().equals("Digite su usuario")) {
                    if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                        msg("Debe ingresar un usuario!!!");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.requestFocus();
                        bt_finanzas.setEnabled(true);
                        bt_finanzas.setClickable(true);
                    } else {
                        user = et_ID.getText().toString();
                        tv_esperar.setText("Digite su password");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.setHint("Password...");
                        et_ID.requestFocus();
                        bt_finanzas.setEnabled(true);
                        bt_finanzas.setClickable(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite su password")) {
                    if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                        msg("Debe ingresar un password!!!");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.requestFocus();
                        bt_finanzas.setEnabled(true);
                        bt_finanzas.setClickable(true);
                    } else {
                        password = et_ID.getText().toString();
                        tv_esperar.setText("Digite su password");
                        verificar_usuario();
                    }
                }
            }
        } else if (tv_saludo.getText().toString().equals("*** MENU PRINCIPAL ***")) {
            Intent finanzas_ac = new Intent(this, FinanzasActivity.class);
            startActivity(finanzas_ac);
            finish();
            System.exit(0);
        }
    }

    public void agregar_cobrador (View view) throws IOException {
        bt_agregar_cobrador.setClickable(false);
        bt_agregar_cobrador.setEnabled(false);
        bt_finanzas.setClickable(false);
        bt_finanzas.setVisibility(View.INVISIBLE);
        bt_finanzas.setEnabled(false);
        et_ID.setEnabled(false);
        if (tv_saludo.getText().toString().equals("Login...")) {
            Log.v("cierre_general", "Main.\n\nflag_pedir_cambio: " + flag_pedir_cambio + "\n\n.");
            bt_agregar_cobrador.setText("CONFIRMAR");
            //autenticar normal...
            if (tv_esperar.getText().toString().equals("Ingrese su usuario")) {
                if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                    msg("Debe ingresar un usuario!!!");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                } else {
                    user = et_ID.getText().toString();
                    tv_esperar.setText("Ingrese su password");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.setHint("Password...");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                }
            } else if (tv_esperar.getText().toString().equals("Ingrese su password")) {
                if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                    msg("Debe ingresar un password!!!");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                } else {
                    password = et_ID.getText().toString();
                    verificar_usuario2();
                }
            } else if (tv_esperar.getText().toString().equals("Ingrese su nuevo usuario")) {
                if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                    msg("Debe ingresar un usuario!!!");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                } else {
                    user = et_ID.getText().toString();
                    tv_esperar.setText("Digite su nuevo password");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.setHint("Password...");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                }
            } else if (tv_esperar.getText().toString().equals("Digite su nuevo password")) {
                if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                    msg("Debe ingresar un password!!!");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                } else {
                    password1 = et_ID.getText().toString();
                    tv_esperar.setText("Confirme su nuevo password");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.setHint("Password...");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                }
            } else if (tv_esperar.getText().toString().equals("Confirme su nuevo password")) {
                if (et_ID.getText().toString().equals("") | et_ID.getText().toString().isEmpty()) {
                    msg("Debe ingresar un password!!!");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                } else {
                    password2 = et_ID.getText().toString();
                    if (password1.equals(password2)) {
                        password = password1;
                        msg("Ususario y password cambiados con exito!");
                        guardar_usuario2();
                    } else {
                        msg("Passwords no coinciden, trate de nuevo!");
                        tv_esperar.setText("Ingrese su nuevo usuario");
                        et_ID.setEnabled(true);
                        et_ID.setText("");
                        et_ID.setHint("Usuario...");
                        et_ID.requestFocus();
                        bt_agregar_cobrador.setEnabled(true);
                        bt_agregar_cobrador.setClickable(true);
                    }
                    tv_esperar.setText("Confirme su nuevo password");
                    et_ID.setEnabled(true);
                    et_ID.setText("");
                    et_ID.setHint("Password...");
                    et_ID.requestFocus();
                    bt_agregar_cobrador.setEnabled(true);
                    bt_agregar_cobrador.setClickable(true);
                }
            } else if (tv_esperar.getText().toString().equals("Digite su password")) {
                msg("Cambio de usuario y/o password!!!");
                et_ID.setEnabled(true);
                et_ID.setText("");
                et_ID.requestFocus();
                tv_esperar.setText("Ingrese su nuevo usuario");
                bt_agregar_cobrador.setEnabled(true);
                bt_agregar_cobrador.setClickable(true);
            }
        } else if (tv_saludo.getText().toString().equals("*** MENU PRINCIPAL ***")) {
            Intent agregar_cobrador = new Intent(this, Agregar_cobrador.class);
            startActivity(agregar_cobrador);
            finish();
            System.exit(0);
        }
    }

    public void cierre_rang (View view) {
        Intent cierre_ac = new Intent(this, Cierre_rangoActivity.class);
        startActivity(cierre_ac);
        finish();
        System.exit(0);
    }

    public void aprob_rech (View view) {
        Intent aprob_rech_ac = new Intent(this, Aprob_rechActivity.class);
        startActivity(aprob_rech_ac);
        finish();
        System.exit(0);
    }

    private void separar_fechaYhora (){
        llenar_mapa_meses();
        Date now = Calendar.getInstance().getTime();
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
        meses.put("Jan",1);
        meses.put("Feb",2);
        meses.put("Mar",3);
        meses.put("Apr",4);
        meses.put("May",5);
        meses.put("Jun",6);
        meses.put("Jul",7);
        meses.put("Aug",8);
        meses.put("Sep",9);
        meses.put("Oct",10);
        meses.put("Nov",11);
        meses.put("Dec",12);
        meses.put("1",1);
        meses.put("2",2);
        meses.put("3",3);
        meses.put("4",4);
        meses.put("5",5);
        meses.put("6",6);
        meses.put("7",7);
        meses.put("8",8);
        meses.put("9",9);
        meses.put("10",10);
        meses.put("11",11);
        meses.put("12",12);
    }

    public  void agregar_linea_archivo (String new_line, String file_name) {
        String archivos[] = fileList();
        String ArchivoCompleto = "";//Aqui se lee el contenido del archivo guardado.
        if (archivo_existe(archivos, file_name)) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null) {
                    ArchivoCompleto = ArchivoCompleto + linea + "\n";
                    linea = br.readLine();
                }
                ArchivoCompleto = ArchivoCompleto + new_line + "\n";
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            crear_archivo(file_name);
            agregar_linea_archivo(file_name, new_line);
            return;
        }
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(file_name, Activity.MODE_PRIVATE));
            archivo.write(ArchivoCompleto);
            archivo.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean archivo_existe (String[] archivos, String file_name){
        for (int i = 0; i < archivos.length; i++) {
            if (file_name.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

    private void crear_archivo (String nombre_archivo) {
        try{
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(nombre_archivo, Activity.MODE_PRIVATE));
            archivo.flush();
            archivo.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed (){
        msg("Presione atras nuevamente para salir...");
        boton_atras();
    }

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void boton_atras () {
        if (flag_salir) {
            finish();
            System.exit(0);
        } else {
            flag_salir = true;
        }
    }

    private void verificar_usuario () {
        boolean flag_password = false;
        boolean flag_user = false;
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(autent));//Se abre archivo
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();//Se lee archivo

            while (linea != null) {
                Log.v("verificar_usuario0", "Main. Linea: " + linea);
                String[] split = linea.split(" ");
                if (split[0].equals("user")) {
                    if (user.equals(split[1])) {
                        flag_user = true;
                    }
                } else if (split[0].equals("password")) {
                    if (password.equals(split[1])) {
                        flag_password = true;
                    }

                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (flag_password & flag_user) {
            msg("Password aceptado!");
            mostrar_todo();
        } else {
            msg("Usuario y/o password incorrectos!");
            tv_esperar.setText("Digite su usuario");
            et_ID.setEnabled(true);
            et_ID.setText("");
            et_ID.setHint("Usuario...");
            et_ID.requestFocus();
            bt_finanzas.setEnabled(true);
            bt_finanzas.setClickable(true);
        }

    }

    private void verificar_usuario2 () {
        boolean flag_password = false;
        boolean flag_user = false;
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(autent));//Se abre archivo
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();//Se lee archivo
            while (linea != null) {
                Log.v("verificar_usuario20", "Main. Linea: " + linea);
                String[] split = linea.split(" ");
                if (split[0].equals("user")) {
                    if (user.equals(split[1])) {
                        flag_user = true;
                    }
                } else if (split[0].equals("password")) {
                    if (password.equals(split[1])) {
                        flag_password = true;
                    }
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (flag_password & flag_user) {
            msg("Password aceptado!");
            cambiar_password();
        } else {
            msg("Usuario y/o password incorrectos!");
            tv_esperar.setText("Ingrese su usuario");
            et_ID.setEnabled(true);
            et_ID.setText("");
            et_ID.setHint("Usuario...");
            et_ID.requestFocus();
            bt_agregar_cobrador.setEnabled(true);
            bt_agregar_cobrador.setClickable(true);
        }
    }

    private void cambiar_password () {
        tv_esperar.setText("Digite su nuevo usuario");
        msg("Ingrese sus nuevas credenciales...");
        et_ID.setEnabled(true);
        et_ID.setText("");
        et_ID.setHint("Usuario...");
        et_ID.requestFocus();
    }

    private void guardar_usuario () throws IOException {
        crear_archivo(autent);
        guardar("user " + user + "\npassword " + password, autent);
        flag_pedir_cambio = false;
        mostrar_todo();
    }

    private void guardar_usuario2 () throws IOException {
        borrar_archivo(autent);
        crear_archivo(autent);
        guardar("user " + user + "\npassword " + password, autent);
        mostrar_todo();
    }

    public  void borrar_archivo (String file) throws IOException {
        File archivo = new File(file);
        String empty_string = "";
        guardar(empty_string, file);
        archivo.delete();
    }

    public  void guardar (String contenido, String file_name) throws IOException {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(file_name, Activity.MODE_PRIVATE));
            archivo.write(contenido);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
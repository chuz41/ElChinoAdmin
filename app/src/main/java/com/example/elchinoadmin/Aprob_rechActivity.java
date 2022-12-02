package com.example.elchinoadmin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
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

public class Aprob_rechActivity extends AppCompatActivity {

    private Map<String, Integer> meses = new HashMap<String, Integer>();private String dia;
    private String mes;
    private String anio;
    private String fecha;
    private String hora;
    private String minuto;
    private String nombre_dia;
    private String linea = "";
    private Spinner spinner;
    public TextView et_monto_prestado;
    public TextView tv_fecha;
    public TextView et_balance_general;
    public TextView et_monto_mora;
    public TextView et_monto_recuperado;
    private TextView tv_saludo;
    public TextView tv_monto_prestado;
    public TextView tv_balance_general;
    public TextView tv_monto_mora;
    public TextView tv_monto_recuperado;
    private String spreadsheet_cobradores = "1y5wRGgrkH48EWgd2OWwon_Um42mxN94CdmJSi_XCwvM";
    private String readRowURL = "https://script.google.com/macros/s/AKfycbxJNCrEPYSw8CceTwPliCscUtggtQ2l_otieFmE/exec?spreadsheetId=";
    private String addRowURL = "https://script.google.com/macros/s/AKfycbweyYb-DHVgyEdCWpKoTmvOxDGXleawjAN8Uw9AeJYbZ24t9arB/exec";
    private String sheet_cobradores = "cobradores";
    private String sheet_creditos = "creditos";
    private String sheet_abonos = "abonos";
    private String sheet_solicitudes = "solicitudes";
    private Map<String, String> cobradores = new HashMap<String, String>();
    private Map<String, String> cobradores2 = new HashMap<String, String>();
    private Map<String, String> lineas = new HashMap<String, String>();
    private Map<String, String> solicitudes = new HashMap<String, String>();
    private Map<String, String> solicitudes2 = new HashMap<String, String>();
    private Map<Integer, String> lineas2 = new HashMap<Integer, String>();
    private Integer monto_prestado_total = 0;
    private Integer monto_recuperado_total = 0;
    private Integer monto_en_mora_a_hoy = 0;
    private Integer balance_general = 0;
    private Button bt_cambiar_fecha;
    private Button bt_volver;
    private int mes_I = 0;
    private int anio_I = 0;
    private int fecha_I = 0;;
    private Date fecha_hoy = new Date();
    private TextView tv_auxiliar;
    private TextView tv_multiline;
    private String solicitudes_file = "solicitudes.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);

        et_monto_prestado = (TextView) findViewById(R.id.et_monto_prestado);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        et_balance_general = (TextView) findViewById(R.id.et_balance_general);
        et_monto_mora = (TextView) findViewById(R.id.et_monto_mora);
        et_monto_recuperado = (TextView) findViewById(R.id.et_monto_recuperado);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("Cierre de hoy");
        tv_monto_prestado = (TextView) findViewById(R.id.tv_monto_prestado);
        tv_balance_general = (TextView) findViewById(R.id.tv_balance_general);
        tv_monto_mora = (TextView) findViewById(R.id.tv_monto_mora);
        tv_monto_recuperado = (TextView) findViewById(R.id.tv_monto_recuperado);
        bt_cambiar_fecha = (Button) findViewById(R.id.bt_cambiar_fecha);
        spinner = (Spinner) findViewById(R.id.spinner);
        tv_auxiliar = (TextView) findViewById(R.id.tv_auxiliar);
        tv_multiline = (TextView) findViewById(R.id.tv_multiline);
        bt_volver = (Button) findViewById(R.id.bt_volver);
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
        tv_fecha.setText(dia + "/" + mes + "/" + anio);

        revisar_cobradores();

    }

    private void revisar_cobradores () {

        ocultar_todo("Conectando,\npor favor espere...");

        if (verificar_internet()) {
            RequestQueue requestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            BasicNetwork network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);

            // Start the queue
            requestQueue.start();

            String url = readRowURL + spreadsheet_cobradores + "&sheet=" + sheet_cobradores;

            Log.v("verificar_usuario()", ".\nurl: " + url + "\n.");

            // Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(String response) {
                            // Do something with the response
                            //Log.v("rev_ventas_hoy0", ".\nResponse:\n" + response);
                            if (response != null) {
                                String[] split = response.split("estado");
                                if (split.length > 1) {
                                    for (int i = 0; i < split.length; i++) {
                                        String[] split2 = split[i].split("\"");
                                        Log.v("rev_ventas_hoy1", "HoyActivity.\n\nResponse: " + response + "\n\n.");
                                        if (split2.length > 1) {
                                            String key = split2[22];//cobrador_ID
                                            String value = split2[14] + "_separador_" + split2[18] + "_separador_" + split2[34] + "_separador_";//SpreadSheet_creditos_separador_SpreadSheet_clientes_separador_apodo_coberador_separador_
                                            cobradores.put(key, value);
                                            cobradores2.put(key, value);
                                        } else {
                                            //Do nothing.
                                        }
                                    }
                                } else {
                                    //Do nothing.
                                }
                                mostrar_todo();
                                lineas2.clear();
                                revisar_solicitudes_de_hoy();
                            } else {
                                msg("No fue posible conectar con el servidor!!!\nIntente de nuevo...");
                                mostrar_todo();
                            }

/*
Response: [{"estado":"TRUE","nombre":"Cobrador1","password":"Laja1982","Screditos":"1IFw5Ln69-ASPdi55cMgZgPbEqXmrh2te3WcZk2kvev0","Sclientes":"1L_qx9L-yeYi_bf3h-m5L_yeupxixeNwtSY8DzpKryow","ID_cobrador":"C0001010001","usuario":"Cobron1","telefono":"61649116","apodo":"Maykol"},{"estado":"TRUE","nombre":"Cobrador2","password":"Cejon123","Screditos":"13MM3ckBBvyZwRkXiLlFLZTw5pl7xKtEO8X2qdj3w2BI","Sclientes":"127tqKKngNeElVCTaCGII8Qd1jx3qYH5YotpBwwdxJkI","ID_cobrador":"C2001010033","usuario":"Cobron2","telefono":"63259725","apodo":"Cejon"},{"estado":"TRUE","nombre":"Cobrador3","password":"letmein","Screditos":"1Tl5XBFH6Pe4jd2ODdRj0nyEbIQMD2O7jc4JqHU9fjoE","Sclientes":"1mZ6hvVIW1_EHcaP8KbJJfRv6yhb03WALG00SXL2MX74","ID_cobrador":"C3580300005","usuario":"Cobron3","telefono":"85258108","apodo":"Chuz"}]
                                                                                                    .
split2[0]:
split2[1]: :
split2[2]: TRUE
split2[3]: ,
split2[4]: nombre
split2[5]: :
split2[6]: Cobrador3
split2[7]: ,
split2[8]: password
split2[9]: :
split2[10]: letmein
split2[11]: ,
split2[12]: Screditos
split2[13]: :
split2[14]: 1Tl5XBFH6Pe4jd2ODdRj0nyEbIQMD2O7jc4JqHU9fjoE
split2[15]: ,
split2[16]: Sclientes
split2[17]: :
split2[18]: 1mZ6hvVIW1_EHcaP8KbJJfRv6yhb03WALG00SXL2MX74
split2[19]: ,
split2[20]: ID_cobrador
split2[21]: :
split2[22]: C3580300005
split2[23]: ,
split2[24]: usuario
split2[25]: :
split2[26]: Cobron3
split2[27]: ,
split2[28]: telefono
split2[29]: :
split2[30]: 85258108
split2[31]: ,
split2[32]: apodo
split2[33]: :
split2[34]: Chuz
split2[35]: }]
*/

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        } else {
            msg("Debe estar conectado a una red WiFi o datos celular!");
        }
    }

    private void revisar_solicitudes_de_hoy () {
        ocultar_todo("Obteniendo solicitudes...");
        if (cobradores.isEmpty()) {
            comprobar_aprobadas();
        } else {
            for (String key : cobradores.keySet()) {
                String value = cobradores.get(key);
                Log.v("revisr_solici_de_hoy0", "Aprob_rech.\n\nKey: " + key + "\nValue: " + value);
                leer_solicitudes_nube(key, value);
                break;
            }
        }
    }

    private void comprobar_aprobadas () {

        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(solicitudes_file));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                loop_comprobar(linea);
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
        }
        solicitudes2.clear();
        solicitudes2.putAll(solicitudes);
        llenar_spinner();
    }

    private void loop_comprobar (String linea) {
        for (String key : solicitudes2.keySet()) {
            String value = solicitudes2.get(key);
            if (linea.equals(value)) {
                solicitudes.remove(key);
            } else {
                //Do nothing. La linea de abajo va en la funcion guardar o aprobar.
                //agregar_linea_archivo(solicitudes_file, value);
            }
            Log.v("loop_aprobar0", "Aprob_rech.\n\nKey: " + key + "\nValue: " + value);
        }
    }

    private void llenar_spinner () {

        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        ocultar_todo("Anctualizando solicitudes...");
        String solicitudes_S = "Ver solicitudes...___";

        for (String key : solicitudes.keySet()) {

            String value = solicitudes.get(key);
            String[] split = value.split("_sepa_linea_");
            solicitudes_S = solicitudes_S + split[4] + " " + split[1] +  "___";

        }

        Log.v("llenando_spinner-1", "Caja.\n\nSolicitudes:\n\n" + solicitudes_S + "\n\n.");
        String[] split_spinner = solicitudes_S.split("___");
        spinner.setEnabled(true);
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, split_spinner);
        spinner.setAdapter(adapter);

        spinner_listener();

    }

    private void spinner_listener () {
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada

                        String seleccion = spinner.getSelectedItem().toString();
                        if (seleccion.equals("Ver solicitudes...")) {
                            //Do nothing!
                        } else {

                            mostrar_solicitud(seleccion);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void mostrar_solicitud (String seleccion) {

        String[] splits = seleccion.split(" ");
        String solicitud_ID = splits[1];
        String nombre_vendedor = splits[0];
        String value = solicitudes.get(solicitud_ID);
        String[] split = value.split("_sepa_linea_");
        String monto_perdonado = split[5];
        String mensaje = "\n\n\n\nMonto perdonado: " + monto_perdonado + " colones.\n\n\nMensaje:\n\n" + split[3];
        mostrar_todo();
        tv_monto_recuperado.setVisibility(View.INVISIBLE);
        tv_auxiliar.setText("Solicitud #" + solicitud_ID + " " + nombre_vendedor);
        tv_multiline.setText(mensaje);
        onClickListener(value);
        onClickListener2();

    }

    private void leer_solicitudes_nube (String key, String value) {

        /*
split2[0]:
split2[1]: :
split2[2]: 200000
split2[3]: ,
split2[4]: plazo_credito
split2[5]: :
split2[6]: 6_semanas_(20%)
split2[7]: ,
split2[8]: monto_cuota
split2[9]: :
split2[10]: 40000
split2[11]: ,
split2[12]: fecha_credito
split2[13]: :
split2[14]: 24/09/2022
split2[15]: ,
split2[16]: fecha_proxima_cuota
split2[17]: :
split2[18]: 01/10/2022
split2[19]: ,
split2[20]: saldo_mas_intereses
split2[21]: :
split2[22]: 240000
split2[23]: ,
split2[24]: tasa
split2[25]: :
split2[26]: 20
split2[27]: ,
split2[28]: cuotas
split2[29]: :
split2[30]: 6
split2[31]: ,
split2[32]: ID_credito
split2[33]: :
split2[34]: B83056053_P_1_P_
split2[35]: ,
split2[36]: moroso
split2[37]: :
split2[38]: D
split2[39]: ,
split2[40]: cuadratura
split2[41]: :
split2[42]: semana_1_40000_01/10/2022__semana_2_40000_08/10/2022__semana_3_40000_15/10/2022__semana_4_40000_22/10/2022__semana_5_40000_29/10/2022__semana_6_40000_05/11/2022__
split2[43]: ,
split2[44]: intereses_moratorios
split2[45]: :
split2[46]: 0
split2[47]: },{
                                             */

        String[] split = value.split("_separador_");
        String spreadsheet_creditos = split[0];
        String spreadsheet_clientes = split[1];
        String apodo_cobrador = split[2];
        linea = "";
        //sheet_creditos = "creditos";
        //sheet_abonos = "abonos";
        ocultar_todo("Solicitudes de " + apodo_cobrador + ",\npor favor espere...");

        if (verificar_internet()) {
            RequestQueue requestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            BasicNetwork network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);

            // Start the queue
            requestQueue.start();

            String url = readRowURL + spreadsheet_creditos + "&sheet=" + sheet_solicitudes;

            Log.v("leer_solic_nube0", "Aprob_rech.\n\nurl: " + url + "\n\n.");

            // Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(String response) {
                            // Do something with the response
                            //Log.v("rev_ventas_hoy0", ".\nResponse:\n" + response);
                            if (response != null) {
                                cobradores.remove(key);
                                String[] split = response.split("solicitud_ID");
                                if (split.length > 0) {
                                    for (int i = 0; i < split.length; i++) {
                                        String[] split2 = split[i].split("\"");
                                        Log.v("leer_solic_nube0", "Aprob_rech.\n\nsplit.length: " + split.length + "\n\nsplit[" + i + "]: " + split[i] + "\n\n.");
                                        if (split2.length > 1) {//solicitud ID [0]           valor ID [1]                 mensaje [2]              valor mensaje [3]                  apodo [4]                  monto perdonado [5]
                                            String linea_hash = split2[0] + "_sepa_linea_" + split2[2] + "_sepa_linea_" + split2[4] + "_sepa_linea_" + split2[6] + "_sepa_linea_" + apodo_cobrador + "_sepa_linea_" + split2[10];
                                            Log.v("leer_solic_nube1", "Caja.\n\nlinea_hash: " + linea_hash + "\n\n.");
                                            if (lineas.containsValue(linea_hash)) {
                                                //Do nothing. Aqui se eliminan los archivos repetidos.
                                            } else {
                                                Log.v("leer_solic_nube2", "Aprob_rech.\n\nLinea hash: " + "\n\n" + linea_hash + "\n\n.");
                                                lineas.put(String.valueOf(i), linea_hash);
                                                solicitudes.put(split2[2], linea_hash);
                                                solicitudes2.put(split2[2], linea_hash);
                                            }
                                        } else {
                                            //Do nothing. Continue...
                                        }
                                    }
                                } else {
                                    //Do nothing.
                                }
                                mostrar_todo();
                                revisar_solicitudes_de_hoy();
                            } else {
                                msg("No fue posible conectar con el servidor!!!\nIntente de nuevo...");
                                mostrar_todo();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        } else {
            msg("Debe estar conectado a una red WiFi o datos celular!");
        }

    }

    private void mostrar_todo () {

        bt_cambiar_fecha.setVisibility(View.VISIBLE);
        bt_cambiar_fecha.setText("APROBAR");
        tv_auxiliar.setVisibility(View.VISIBLE);
        tv_multiline.setVisibility(View.VISIBLE);
        bt_volver.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.INVISIBLE);

    }

    private void ocultar_todo (String mensaje) {
        et_monto_mora.setVisibility(View.INVISIBLE);
        et_monto_prestado.setVisibility(View.INVISIBLE);
        et_balance_general.setVisibility(View.INVISIBLE);
        tv_monto_recuperado.setText(mensaje);
        tv_monto_prestado.setVisibility(View.INVISIBLE);
        tv_balance_general.setVisibility(View.INVISIBLE);
        tv_monto_mora.setVisibility(View.INVISIBLE);
        et_monto_recuperado.setVisibility(View.VISIBLE);
        bt_cambiar_fecha.setVisibility(View.INVISIBLE);
        tv_auxiliar.setVisibility(View.INVISIBLE);
        tv_multiline.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        bt_volver.setVisibility(View.INVISIBLE);
    }

    private void onClickListener (String linea) {
        bt_cambiar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                archivar_solicitud(linea);

            }
        });
    }

    private void onClickListener2 () {
        bt_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comprobar_aprobadas();

            }
        });
    }

    private void archivar_solicitud (String linea) {

        agregar_linea_archivo(solicitudes_file, linea);
        msg("Solicitud archivada!!!");
        //cobradores.clear();
        //cobradores.putAll(cobradores2);
        mostrar_todo();
        comprobar_aprobadas();
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
        }
    }

    private void separar_fechaYhora (){
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

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
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
        }
    }

    private boolean verificar_internet() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
package com.example.elchinoadmin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

public class Cierre_rangoActivity extends AppCompatActivity {

    private Map<String, Integer> meses = new HashMap<String, Integer>();
    private String dia;
    private Integer contador_puntos = 0;
    private String mes;
    private String anio;
    private String fecha;
    private String hora;
    private String minuto;
    private String nombre_dia;
    private String linea = "";
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
    private Map<String, String> cobradores = new HashMap<String, String>();
    private Map<String, String> cobradores2 = new HashMap<String, String>();
    private Map<String, String> lineas = new HashMap<String, String>();
    private Map<Integer, String> lineas2 = new HashMap<Integer, String>();
    private Integer monto_prestado_total = 0;
    private Integer monto_recuperado_total = 0;
    private Integer monto_en_mora_a_hoy = 0;
    private Integer balance_general = 0;
    private Button bt_fecha_inicio;
    private Button bt_fecha_fin;
    private String fecha_mostrar_inicio = "";
    private String fecha_mostrar_fin = "";
    private int mes_inicio = 0;
    private int anio_inicio = 0;
    private int fecha_inicio = 0;
    private boolean flag_inicio = false;
    private boolean flag_fin = false;
    private int mes_fin = 0;
    private int anio_fin = 0;
    private int fecha_fin = 0;
    private Button bt_confirmar;
    private Date fecha_param = new Date();


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
        tv_saludo.setText("Rango de fechas");
        tv_monto_prestado = (TextView) findViewById(R.id.tv_monto_prestado);
        tv_balance_general = (TextView) findViewById(R.id.tv_balance_general);
        tv_monto_mora = (TextView) findViewById(R.id.tv_monto_mora);
        tv_monto_recuperado = (TextView) findViewById(R.id.tv_monto_recuperado);
        bt_fecha_inicio = (Button) findViewById(R.id.bt_fecha_inicio);
        bt_fecha_inicio.setVisibility(View.VISIBLE);
        bt_fecha_fin = (Button) findViewById(R.id.bt_fecha_fin);
        bt_fecha_fin.setVisibility(View.VISIBLE);
        bt_confirmar = (Button) findViewById(R.id.bt_confirmar);
        bt_confirmar.setVisibility(View.VISIBLE);
        bt_confirmar.setClickable(false);
        bt_confirmar.setEnabled(false);


        separar_fechaYhora();
        tv_fecha.setText(dia + "/" + mes + "/" + anio);


        ocultar_todo("buscando...");

        revisar_cobradores();

        //revisar_cobradores();

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

    private void obtener_ventas_rango () {
        bt_fecha_inicio.setVisibility(View.VISIBLE);
        bt_fecha_fin.setVisibility(View.VISIBLE);
        bt_confirmar.setVisibility(View.VISIBLE);
        //tv_monto_recuperado.setVisibility(View.INVISIBLE);
        onClickListener();
        onClickListener2();
        Log.v("obtener_ventas_rango0", "Cierre_rango.\n\nobtener rangos con un loop for.\n\n.");

    }

    private void onClickListener2 () {
        bt_fecha_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(bt_fecha_inicio)) {
                    final Calendar c = Calendar.getInstance();
                    //final boolean[] edad_permitida = {true};
                    mes_inicio = (c.get(Calendar.MONTH));
                    //Toast.makeText(bt_fecha_inicio.getContext(), "mes: " + mes_inicio, Toast.LENGTH_SHORT).show();
                    anio_inicio = c.get(Calendar.YEAR);
                    fecha_inicio = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(bt_fecha_inicio.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                            //edad_cliente.autofill(AutofillValue.forText(String.valueOf(i2) + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i)));
                            mes_inicio = i1+1;
                            anio_inicio = i;
                            fecha_inicio = i2;
                            //flag_fecha = true;
                            String i_s = String.valueOf(anio_inicio);
                            String i1_s = String.valueOf(mes_inicio);
                            String i2_s = String.valueOf(fecha_inicio);
                            if (i1_s.length() == 1) {
                                i1_s = "0" + i1_s;
                            }
                            if (i2_s.length() == 1) {
                                i2_s = "0" + i2_s;
                            }
                            String mes_inicioS = i1_s;
                            String anio_inicioS = i_s;
                            String fecha_inicioS = i2_s;
                            //fecha_mostrar_inicio = (i2_s + "/" + i1_s + "/" + i_s);
                            Log.v("inicial_fecha0", "Cierre_rango.\n\nFecha antes de sumarle uno al mes:\n\n" + i2_s + "/" + i1_s + "/" + i_s + "\n\n.");
                            Log.v("inicial_fecha1", "Cierre_rango.\n\nFecha despues de sumarle uno al mes:\n\n" + String.valueOf(fecha_inicio) + "/" + String.valueOf(mes_inicio) + "/" + String.valueOf(anio_inicio));
                            fecha_mostrar_inicio = fecha_inicioS + "/" + mes_inicioS + "/" + anio_inicioS;
                            bt_fecha_inicio.setText(fecha_mostrar_inicio);
                            flag_inicio = true;
                            if (flag_fin & flag_inicio) {
                                bt_confirmar.setEnabled(true);
                                bt_confirmar.setClickable(true);
                                onClickListener3();
                            }
                        }
                    },anio_inicio,mes_inicio,fecha_inicio);
                    datePickerDialog.show();
                    onClickListener();
                    onClickListener2();
                    if (flag_fin & flag_inicio) {
                        bt_confirmar.setEnabled(true);
                        bt_confirmar.setClickable(true);
                        onClickListener3();
                    }
                } else {
                    //Do nothing.
                }

                if (v.equals(bt_fecha_fin)) {
                    final Calendar c = Calendar.getInstance();
                    //final boolean[] edad_permitida = {true};
                    mes_fin = (c.get(Calendar.MONTH));
                    //Toast.makeText(bt_fecha_fin.getContext(), "mes : " + mes_fin, Toast.LENGTH_SHORT).show();
                    anio_fin = c.get(Calendar.YEAR);
                    fecha_fin = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(bt_fecha_fin.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            mes_fin = i1+1;
                            anio_fin = i;
                            fecha_fin = i2;
                            //flag_fecha = true;
                            String i_s = String.valueOf(anio_fin);
                            String i1_s = String.valueOf(mes_fin);
                            String i2_s = String.valueOf(fecha_fin);
                            if (i1_s.length() == 1) {
                                i1_s = "0" + i1_s;
                            }
                            if (i2_s.length() == 1) {
                                i2_s = "0" + i2_s;
                            }
                            String mes_finS = i1_s;
                            String anio_finS = i_s;
                            String fecha_finS = i2_s;
                            //fecha_mostrar_fin = (i2_s + "/" + i1_s + "/" + i_s);
                            Log.v("inicial_fecha0", "Cierre_rango.\n\nFecha antes de sumarle uno al mes:\n\n" + i2_s + "/" + i1_s + "/" + i_s + "\n\n.");
                            Log.v("inicial_fecha1", "Cierre_rango.\n\nFecha despues de sumarle uno al mes:\n\n" + String.valueOf(fecha_fin) + "/" + String.valueOf(mes_fin) + "/" + String.valueOf(anio_fin));
                            fecha_mostrar_fin = fecha_finS + "/" + mes_finS + "/" + anio_finS;
                            bt_fecha_fin.setText(fecha_mostrar_fin);
                            flag_fin = true;
                            if (flag_fin & flag_inicio) {
                                bt_confirmar.setEnabled(true);
                                bt_confirmar.setClickable(true);
                                onClickListener3();
                            }
                        }
                    },anio_fin,mes_fin,fecha_fin);
                    datePickerDialog.show();
                    if (flag_fin & flag_inicio) {
                        bt_confirmar.setEnabled(true);
                        bt_confirmar.setClickable(true);
                        onClickListener3();
                    }
                    onClickListener();
                    onClickListener2();
                } else {
                    //Do nothing.
                }
            }
        });
    }

    private void onClickListener () {
        bt_fecha_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(bt_fecha_inicio)) {
                    final Calendar c = Calendar.getInstance();
                    //final boolean[] edad_permitida = {true};
                    mes_inicio = (c.get(Calendar.MONTH));
                    //Toast.makeText(bt_fecha_inicio.getContext(), "mes: " + mes_inicio, Toast.LENGTH_SHORT).show();
                    anio_inicio = c.get(Calendar.YEAR);
                    fecha_inicio = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(bt_fecha_inicio.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                            //edad_cliente.autofill(AutofillValue.forText(String.valueOf(i2) + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i)));
                            mes_inicio = i1+1;
                            anio_inicio = i;
                            fecha_inicio = i2;
                            //flag_fecha = true;
                            String i_s = String.valueOf(anio_inicio);
                            String i1_s = String.valueOf(mes_inicio);
                            String i2_s = String.valueOf(fecha_inicio);
                            if (i1_s.length() == 1) {
                                i1_s = "0" + i1_s;
                            }
                            if (i2_s.length() == 1) {
                                i2_s = "0" + i2_s;
                            }
                            String mes_inicioS = i1_s;
                            String anio_inicioS = i_s;
                            String fecha_inicioS = i2_s;
                            //fecha_mostrar_inicio = (i2_s + "/" + i1_s + "/" + i_s);
                            Log.v("inicial_fecha0", "Cierre_rango.\n\nFecha antes de sumarle uno al mes:\n\n" + i2_s + "/" + i1_s + "/" + i_s + "\n\n.");
                            Log.v("inicial_fecha1", "Cierre_rango.\n\nFecha despues de sumarle uno al mes:\n\n" + String.valueOf(fecha_inicio) + "/" + String.valueOf(mes_inicio) + "/" + String.valueOf(anio_inicio));
                            fecha_mostrar_inicio = fecha_inicioS + "/" + mes_inicioS + "/" + anio_inicioS;
                            bt_fecha_inicio.setText(fecha_mostrar_inicio);
                            flag_inicio = true;
                            if (flag_fin & flag_inicio) {
                                bt_confirmar.setEnabled(true);
                                bt_confirmar.setClickable(true);
                                onClickListener3();
                            }
                        }
                    },anio_inicio,mes_inicio,fecha_inicio);
                    datePickerDialog.show();
                    onClickListener();
                    onClickListener2();
                    if (flag_fin & flag_inicio) {
                        bt_confirmar.setEnabled(true);
                        bt_confirmar.setClickable(true);
                        onClickListener3();
                    }
                } else {
                    //Do nothing.
                }

                if (v.equals(bt_fecha_fin)) {
                    final Calendar c = Calendar.getInstance();
                    //final boolean[] edad_permitida = {true};
                    mes_fin = (c.get(Calendar.MONTH));
                    //Toast.makeText(bt_fecha_fin.getContext(), "mes : " + mes_fin, Toast.LENGTH_SHORT).show();
                    anio_fin = c.get(Calendar.YEAR);
                    fecha_fin = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(bt_fecha_fin.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            mes_fin = i1+1;
                            anio_fin = i;
                            fecha_fin = i2;
                            //flag_fecha = true;
                            String i_s = String.valueOf(anio_fin);
                            String i1_s = String.valueOf(mes_fin);
                            String i2_s = String.valueOf(fecha_fin);
                            if (i1_s.length() == 1) {
                                i1_s = "0" + i1_s;
                            }
                            if (i2_s.length() == 1) {
                                i2_s = "0" + i2_s;
                            }
                            String mes_finS = i1_s;
                            String anio_finS = i_s;
                            String fecha_finS = i2_s;
                            //fecha_mostrar_fin = (i2_s + "/" + i1_s + "/" + i_s);
                            Log.v("inicial_fecha0", "Cierre_rango.\n\nFecha antes de sumarle uno al mes:\n\n" + i2_s + "/" + i1_s + "/" + i_s + "\n\n.");
                            Log.v("inicial_fecha1", "Cierre_rango.\n\nFecha despues de sumarle uno al mes:\n\n" + String.valueOf(fecha_fin) + "/" + String.valueOf(mes_fin) + "/" + String.valueOf(anio_fin));
                            fecha_mostrar_fin = fecha_finS + "/" + mes_finS + "/" + anio_finS;
                            bt_fecha_fin.setText(fecha_mostrar_fin);
                            flag_fin = true;
                            //onClickListener();
                            if (flag_fin & flag_inicio) {
                                bt_confirmar.setEnabled(true);
                                bt_confirmar.setClickable(true);
                                onClickListener3();
                            }
                        }
                    },anio_fin,mes_fin,fecha_fin);
                    datePickerDialog.show();
                    onClickListener();
                    onClickListener2();
                    if (flag_fin & flag_inicio) {
                        bt_confirmar.setEnabled(true);
                        bt_confirmar.setClickable(true);
                        onClickListener3();
                    }
                } else {
                    //Do nothing.
                }
            }
        });
    }

    private void funcion_loop () throws ParseException {
        for (int i = 0; i >= 0; i++) {
            //Este es un loop infinito que se rompe con un break.
            bt_confirmar.setClickable(false);
            bt_confirmar.setEnabled(false);
            bt_fecha_inicio.setClickable(false);
            bt_fecha_inicio.setEnabled(false);
            bt_fecha_fin.setClickable(false);
            bt_fecha_fin.setEnabled(false);
            Log.v("funcion_loop", "\n\nCierre_rango.\n\n" + "loop infinito i = " + i + "\n\n.");

            fecha_param = DateUtilities.addDays(fecha_param, 1);
            String fecha_siguiente = DateUtilities.dateToString(fecha_param);
            String[] split_fecha_sig = fecha_siguiente.split("-");
            fecha_siguiente = split_fecha_sig[2] + "/" + split_fecha_sig[1] + "/" + split_fecha_sig[0];
            et_monto_recuperado.setText(fecha_siguiente);
            String[] split_sig = fecha_siguiente.split("/");
            String comp_sig = split_sig[2] + split_sig[1] + split_sig[0];
            String fecha_sig_S = split_sig[2] + "-" + split_sig[1] + "-" + split_sig[0];
            int comp_sig_I = Integer.parseInt(comp_sig);
            String[] split_fin = bt_fecha_fin.getText().toString().split("/");
            String comp_fin = split_fin[2] + split_fin[1] + split_fin[0];
            String fecha_fin_S = split_fin[2] + "-" + split_fin[1] + "-" + split_fin[0];
            int comp_fin_I = Integer.parseInt(comp_fin);

            if (comp_sig_I > comp_fin_I) {
                msg("Lectura completa!");
                balance_general = monto_recuperado_total - monto_prestado_total;
                mostrar_todo();
                break;
            } else {
                Date fecha_sig_D = DateUtilities.stringToDate(fecha_sig_S);
                Log.v("OnClickListener32", "Cierre_rango.\n\nDate inicio:\n\n" + fecha_sig_D.toString() + "\n\n.");
                String[] split_sig_D = fecha_sig_D.toString().split(" ");
                String sheet_fechada = split_sig_D[0] + "-" + split_sig_D[1] + "-" + split_sig_D[2] + "-" + split_sig_D[5] + "-creditos";
                HashMap<String, String> cobradores_helper = new HashMap<String, String>();
                HashMap<String, String> cobradores_helper2 = new HashMap<String, String>();
                cobradores_helper.putAll(cobradores);
                cobradores_helper2.putAll(cobradores2);
                revisar_ventas_de_hoy(cobradores_helper, cobradores_helper2, sheet_fechada);
                break;
            }

        }
    }

    private void onClickListener3 () {
        bt_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_confirmar.setClickable(false);
                bt_confirmar.setEnabled(false);
                bt_fecha_inicio.setClickable(false);
                bt_fecha_inicio.setEnabled(false);
                bt_fecha_fin.setClickable(false);
                bt_fecha_fin.setEnabled(false);
                Log.v("onClickListener30", "\n\nCierre_rango.\n\n.");

                String[] split_inicio = bt_fecha_inicio.getText().toString().split("/");
                et_monto_recuperado.setVisibility(View.VISIBLE);
                et_monto_recuperado.setText(bt_fecha_inicio.getText().toString());
                String comp_inicio = split_inicio[2] + split_inicio[1] + split_inicio[0];
                String fecha_inicio_S = split_inicio[2] + "-" + split_inicio[1] + "-" + split_inicio[0];
                int comp_inicio_I = Integer.parseInt(comp_inicio);
                String[] split_fin = bt_fecha_fin.getText().toString().split("/");
                String comp_fin = split_fin[2] + split_fin[1] + split_fin[0];
                String fecha_fin_S = split_fin[2] + "-" + split_fin[1] + "-" + split_fin[0];
                int comp_fin_I = Integer.parseInt(comp_fin);
                //lineas.clear();

                if (comp_inicio_I >= comp_fin_I) {
                    msg("La fecha inicial debe ser menor a la fecha final!");
                    bt_fecha_inicio.setEnabled(true);
                    bt_fecha_inicio.setClickable(true);
                    bt_fecha_fin.setEnabled(true);
                    bt_fecha_fin.setClickable(true);
                    ocultar_todo("buscando...");
                    //lineas.clear();
                    revisar_cobradores();
                } else {
                    Date fecha_inicio_D = null;
                    try {
                        fecha_inicio_D = DateUtilities.stringToDate(fecha_inicio_S);
                        fecha_param = fecha_inicio_D;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.v("OnClickListener31", "Cierre_rango.\n\nDate inicio:\n\n" + fecha_inicio_D.toString() + "\n\n.");
                    String[] split_inicio_D = fecha_inicio_D.toString().split(" ");
                    String sheet_fechada = split_inicio_D[0] + "-" + split_inicio_D[1] + "-" + split_inicio_D[2] + "-" + split_inicio_D[5]  + "-creditos";
                    HashMap<String, String> cobradores_helper = new HashMap<String, String>();
                    HashMap<String, String> cobradores_helper2 = new HashMap<String, String>();
                    cobradores_helper.putAll(cobradores);
                    cobradores_helper2.putAll(cobradores2);
                    try {
                        revisar_ventas_de_hoy(cobradores_helper, cobradores_helper2, sheet_fechada);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    public void final_fecha (View view) {

        final Calendar c = Calendar.getInstance();
        //final boolean[] edad_permitida = {true};
        mes_fin = (c.get(Calendar.MONTH));
        Toast.makeText(this, "mes selected: " + mes_fin, Toast.LENGTH_LONG).show();
        anio_fin = c.get(Calendar.YEAR);
        fecha_fin = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String i_s = String.valueOf(i);
                String i1_s = String.valueOf(i1 + 1);
                String i2_s = String.valueOf(i2);
                if (i_s.length() == 1) {
                    i_s = "0" + i_s;
                }
                if (i1_s.length() == 1) {
                    i1_s = "0" + i1_s;
                }
                if (i2_s.length() == 1) {
                    i2_s = "0" + i2_s;
                }
                //fecha_mostrar_inicio = (i2_s + "/" + i1_s + "/" + i_s);
                Log.v("final_fecha0", "Cierre_rango.\n\nFecha antes de sumarle uno al mes:\n\n" + i2_s + "/" + i1_s + "/" + i_s + "\n\n.");
                //edad_cliente.autofill(AutofillValue.forText(String.valueOf(i2) + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i)));
                mes_fin = i1+1;
                anio_fin = i;
                fecha_fin = i2;
                //flag_fecha = true;
                Log.v("final_fecha1", "Cierre_rango.\n\nFecha despues de sumarle uno al mes:\n\n" + String.valueOf(fecha_fin) + "/" + String.valueOf(mes_fin) + "/" + String.valueOf(anio_fin));
                fecha_mostrar_fin = String.valueOf(fecha_fin) + "/" + String.valueOf(mes_fin) + "/" + String.valueOf(anio_fin);
                bt_fecha_fin.setText(fecha_mostrar_fin);
                flag_fin = true;
            }
        },anio_fin,mes_fin,fecha_fin);
        datePickerDialog.show();
    }

    private void mostrar_todo () {
        /*
        monto_prestado_total = 0;
        monto_recuperado_total = 0;
        monto_en_mora_a_hoy = 0;
        balance_general = 0;
         */

        et_monto_mora.setVisibility(View.VISIBLE);
        et_monto_mora.setText(String.valueOf(monto_en_mora_a_hoy) + " colones");
        et_monto_prestado.setVisibility(View.VISIBLE);
        et_monto_prestado.setText(String.valueOf(monto_prestado_total) + " colones");
        et_balance_general.setVisibility(View.VISIBLE);
        et_balance_general.setText(String.valueOf(balance_general) + " colones");
        et_monto_recuperado.setVisibility(View.VISIBLE);
        et_monto_recuperado.setText(String.valueOf(monto_recuperado_total) + " colones");
        tv_monto_recuperado.setText("Monto recuperado total:");
        tv_monto_recuperado.setVisibility(View.VISIBLE);
        tv_monto_prestado.setVisibility(View.VISIBLE);
        tv_balance_general.setVisibility(View.VISIBLE);
        tv_monto_mora.setVisibility(View.VISIBLE);
        bt_confirmar.setVisibility(View.INVISIBLE);
        bt_fecha_fin.setVisibility(View.VISIBLE);
        bt_fecha_inicio.setVisibility(View.VISIBLE);
    }

    private void ocultar_todo (String mensaje) {
        et_monto_mora.setVisibility(View.INVISIBLE);
        et_monto_prestado.setVisibility(View.INVISIBLE);
        et_balance_general.setVisibility(View.INVISIBLE);
        tv_monto_recuperado.setText(mensaje);
        tv_monto_prestado.setVisibility(View.INVISIBLE);
        tv_balance_general.setVisibility(View.INVISIBLE);
        tv_monto_mora.setVisibility(View.INVISIBLE);
        //et_monto_recuperado.setVisibility(View.INVISIBLE);
        bt_confirmar.setVisibility(View.INVISIBLE);
        bt_fecha_inicio.setVisibility(View.INVISIBLE);
        bt_fecha_fin.setVisibility(View.INVISIBLE);
    }

    private void revisar_ventas_de_hoy (HashMap<String, String> cobradores_helper1, HashMap<String, String> cobradores_helper2, String sheet) throws ParseException {
        ocultar_todo("Obteniendo ventas de hoy...");

        if (cobradores_helper1.isEmpty()) {
            //Do nothing.
            //mostrar_todo();
            sheet = sheet.replace("creditos", "abonos");
            leer_cobros_de_hoy(cobradores_helper2, sheet);
        } else {

            for (String key : cobradores_helper1.keySet()) {
                String value = cobradores_helper1.get(key);
                Log.v("rev_ventas_hoy0", "Hoy.\n\nKey: " + key + "\nValue: " + value);
                leer_ventas_nube(key, value, cobradores_helper1, cobradores_helper2, sheet);
                break;
            }

        }

    }

    private void leer_cobros_de_hoy (HashMap<String, String> cobradores_helper2, String sheet) throws ParseException {

        ocultar_todo("Obteniendo cobros de hoy...");

        if (cobradores_helper2.isEmpty()) {
            //balance_general = monto_recuperado_total - monto_prestado_total;
            //mostrar_todo();
            funcion_loop();
        } else {

            for (String key : cobradores_helper2.keySet()) {
                String value = cobradores_helper2.get(key);
                Log.v("rev_ventas_hoy0", "Hoy.\n\nKey: " + key + "\nValue: " + value);
                leer_cobros_nube(key, value, cobradores_helper2, sheet);
                break;
            }

        }

    }

    private boolean linea_existe (String linea) {
        boolean flag = true;



        return flag;
    }

    private void leer_ventas_nube (String key, String value, HashMap<String, String> cobradores_helper1, HashMap<String, String> cobradores_helper2, String sheet) {

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

        String mensaje_ocultar = "Leyendo datos\ndel servidor";
        for(int i = 0; i < contador_puntos; i++) {
            mensaje_ocultar = mensaje_ocultar + ".";
        }
        contador_puntos++;
        if (contador_puntos > 5) {
            contador_puntos = 0;
        }
        ocultar_todo(mensaje_ocultar);

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

            String url = readRowURL + spreadsheet_creditos + "&sheet=" + sheet;

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
                                cobradores_helper1.remove(key);
                                String[] split = response.split("monto_credito");
                                if (split.length > 1) {

                                    for (int i = 0; i < split.length; i++) {
                                        String[] split2 = split[i].split("\"");
                                        Log.v("leer_ventas_nube0", "HoyActivity.\n\nSplit.length: " + split.length + "\n\nsplit[" + i + "]: " + split[i] + "\n\n.");
                                        if (split2.length > 1) {
                                            String linea_hash = split2[42] + "_sepa_linea_" + split2[38] + "_sepa_linea_" + split2[22] + "_sepa_linea_" +
                                                    split2[6] + "_sepa_linea_" + split2[14] + "_sepa_linea_" + split2[2] + "_sepa_linea_" + split2[46] + "_sepa_linea_" + apodo_cobrador;


                                            if (lineas.containsValue(linea_hash)) {
                                                //Do nothing. Aqui se eliminan los archivos repetidos.
                                            } else {
                                                if (lineas.containsKey(split2[34])) {
                                                    //Do nothing. Parece que no es necesario, pero no afecta tampoco.
                                                } else {
                                                    Log.v("leer_ventas_nube1", "Hoy.\n\nLinea hash: " + "\n\n" + linea_hash + "\n\n.");
                                                    lineas.put(split2[34], linea_hash);
                                                    monto_prestado_total = monto_prestado_total + Integer.parseInt(split2[2]);
                                                    //monto_en_mora_a_hoy = monto_en_mora_a_hoy + Integer.parseInt(split2[46]);
                                                }
                                            }
                                        } else {
                                            //Do nothing. Continue...
                                        }

                                    }
                                } else {
                                    //Do nothing.
                                }
                                //mostrar_todo();
                                try {
                                    revisar_ventas_de_hoy(cobradores_helper1, cobradores_helper2, sheet);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
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

    private void leer_cobros_nube (String key, String value, HashMap<String, String> cobradores_helper2, String sheet) {

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
        String mensaje_ocultar = "Leyendo datos\ndel servidor";
        for(int i = 0; i < contador_puntos; i++) {
            mensaje_ocultar = mensaje_ocultar + ".";
        }
        contador_puntos++;
        if (contador_puntos > 5) {
            contador_puntos = 0;
        }
        ocultar_todo(mensaje_ocultar);

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

            String url = readRowURL + spreadsheet_creditos + "&sheet=" + sheet;

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
                                cobradores_helper2.remove(key);
                                String[] split = response.split("monto_credito");
                                if (split.length > 1) {

                                    for (int i = 0; i < split.length; i++) {
                                        String[] split2 = split[i].split("\"");
                                        Log.v("leer_cobros_nube0", "HoyActivity.\n\nSplit.length: " + split.length + "\n\nsplit[" + i + "]: " + split[i] + "\n\n.");
                                        if (split2.length > 1) {
                                            String linea_hash = split2[42] + "_sepa_linea_" + split2[38] + "_sepa_linea_" + split2[22] + "_sepa_linea_" +
                                                    split2[6] + "_sepa_linea_" + split2[14] + "_sepa_linea_" + split2[2] + "_sepa_linea_" + split2[46] + "_sepa_linea_" +
                                                    apodo_cobrador + "_sepa_linea_" + split2[50];


                                            if (lineas2.containsValue(linea_hash)) {
                                                //Do nothing. Aqui se eliminan los archivos repetidos.
                                            } else {
                                                Log.v("leer_cobros_nube1", "Hoy.\n\nLinea hash: " + "\n\n" + linea_hash + "\n\n.");
                                                lineas2.put(i, linea_hash);
                                                monto_recuperado_total = monto_recuperado_total + Integer.parseInt(split2[50]);
                                                monto_en_mora_a_hoy = monto_en_mora_a_hoy + Integer.parseInt(split2[46]);
                                            }
                                        } else {
                                            //Do nothing. Continue...
                                        }

                                    }
                                } else {
                                    //Do nothing.
                                }
                                //mostrar_todo();
                                try {
                                    leer_cobros_de_hoy(cobradores_helper2, sheet);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
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

    private void revisar_cobradores () {

        ocultar_todo("Leyendo datos del servidor,\npor favor espere.");

        cobradores.clear();
        cobradores2.clear();
        lineas.clear();
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
                                //mostrar_todo();
                                lineas2.clear();
                                obtener_ventas_rango();
                                //revisar_ventas_de_hoy();
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

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
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
}
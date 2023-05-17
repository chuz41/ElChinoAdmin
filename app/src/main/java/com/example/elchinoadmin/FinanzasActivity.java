package com.example.elchinoadmin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.elchinoadmin.Clases_comunes.Cliente;
import com.example.elchinoadmin.Util.BluetoothUtil;
import com.example.elchinoadmin.Util.DateUtilities;
import com.example.elchinoadmin.Util.GuardarArchivo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FinanzasActivity extends AppCompatActivity {

    private Map<String, Integer> meses = new HashMap<>();private String dia;
    private Spinner spinner;
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
    private String spreadsheet_clientes;
    private String spreadsheet_creditos;
    private String readRowURL = "https://script.google.com/macros/s/AKfycbxJNCrEPYSw8CceTwPliCscUtggtQ2l_otieFmE/exec?spreadsheetId=";
    private String addRowURL = "https://script.google.com/macros/s/AKfycbweyYb-DHVgyEdCWpKoTmvOxDGXleawjAN8Uw9AeJYbZ24t9arB/exec";
    private String sheet_cobradores = "cobradores";
    private String sheet_creditos = "creditos";
    private String sheet_abonos = "abonos";
    private String sheet_cierre = "cierre";
    private Map<String, String> cobradores = new HashMap<>();
    private Map<String, String> cobradores2 = new HashMap<>();
    private Map<String, String> lineas = new HashMap<>();
    private Map<Integer, String> lineas2 = new HashMap<>();
    private Integer monto_prestado_total = 0;
    private Integer monto_recuperado_total = 0;
    private Integer monto_en_mora_a_hoy = 0;
    private Integer balance_general = 0;
    private Button bt_cambiar_fecha;
    private int mes_I = 0;
    private int anio_I = 0;
    private int fecha_I = 0;;
    private Date fecha_hoy = new Date();
    private TextView tv_auxiliar;
    private EditText et_multiLine;
    private String contenidoCierre;
    private String nombreCobrador;
    private Map<Integer, String> abonos = new HashMap<Integer, String>();
    private Map<Integer, String> creditos = new HashMap<Integer, String>();
    private Map<Integer, String> bancas = new HashMap<Integer, String>();
    private Integer balance_general_abonos = 0;
    private Integer balance_general_creditos = 0;
    private Integer balance_general_banca_entrega = 0;
    private Integer balance_general_banca_recibe = 0;
    private String mensaje_imprimir = "";
    private HashMap<String, Cliente> clientesEncontrados = new HashMap<>();//Contendra a todos los clientes leidos y guardados.
    private Button bt_imprimir;
    private Integer contLeer = 0;

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
        et_multiLine = (EditText) findViewById(R.id.et_multiLine);
        et_multiLine.setVisibility(View.INVISIBLE);
        tv_monto_prestado = (TextView) findViewById(R.id.tv_monto_prestado);
        tv_balance_general = (TextView) findViewById(R.id.tv_balance_general);
        tv_monto_mora = (TextView) findViewById(R.id.tv_monto_mora);
        tv_monto_recuperado = (TextView) findViewById(R.id.tv_monto_recuperado);
        bt_cambiar_fecha = (Button) findViewById(R.id.bt_cambiar_fecha);
        spinner = (Spinner) findViewById(R.id.spinner);
        tv_auxiliar = (TextView) findViewById(R.id.tv_auxiliar);
        bt_imprimir = (Button) findViewById(R.id.bt_imprimir);
        bt_imprimir.setVisibility(View.INVISIBLE);
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

    private void obtenerClientes () {

        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String sheetClientes = "clientes";
        String url = readRowURL + spreadsheet_clientes + "&sheet=" + sheetClientes;
        //Log.v("obtenerClientes_0", "Main.\n\nurl: " + url + "\n\n.");
        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    if (response != null) {
                        Log.v("obtenerClientes_1", "Finanzas.\n\nresponse:\n\n" + response + "\n\n.");
                        if (response.contains("DOCTYPE")) {
                            obtenerClientes();
                        } else {
                            String[] split = response.split("ID_cliente");
                            try {
                                guardarClientes(split);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                },
                error -> {
                    obtenerClientes();
                });
        requestQueue.add(stringRequest);// Add the request to the RequestQueue.
    }

    private void guardarClientes (String[] clientesArrary) throws IOException {//Depende del formato Json para funcionar.
        ocultar_todo("Guardando clientes...");
        int cont = 0;
        for (String infoToFile : clientesArrary) {
            Log.v("guardarClientes_0", "info: " + infoToFile + "Finanzas.\n\nclientesArray.length(): " + clientesArrary.length + "\n\n.");
            String[] split = infoToFile.split("\",\"");
            String u0,u1,u2,u3,u4,u6,u9,u10,u11,u12,u13,newFile;//u0 = clienteID
            u0=u1=u2=u3=u4=u12=u13=u9=u10=u6=u11=newFile=null;
            boolean flagControl = false;
            for (String clienteInfo : split) {
                if (split.length > 10) {
                    flagControl = true;
                    //Log.v("guardarClientes_*", "split.length: " + split.length + ", InfoCliente: " + clienteInfo + ".");
                    String[] splitInfo = clienteInfo.split("\":\"");
                    if (splitInfo[0].isEmpty()) {
                        //Log.v("guardarCliente_#", "Main.\nID_cliente: " + splitInfo[1] + "\n.");
                        u0 = splitInfo[1];
                        newFile = splitInfo[1] + "_C_.txt";
                    } else if (splitInfo[0].equals("nombre_cliente")) {
                        u1 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("apellido1_cliente")) {
                        u2 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("apellido2_cliente")) {
                        u3 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("apodo_cliente")) {
                        u4 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("telefono1_cliente")) {
                        u12 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("telefono2_cliente")) {
                        u13 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("notas_cliente")) {
                        u9 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("direccion_cliente")) {
                        u10 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("puntuacion_cliente")) {
                        u11 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("monto_disponible")) {
                        u6 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    }
                    cont++;
                } else {
                    flagControl = false;
                }
            }
            if (flagControl) {
                //u0 ID_cliente, u1 nombre_cliente, u2 apellido1_cliente, u3 apellido2_cliente, u4 apodo_cliente, u12 telefono1_cliente, u13 telefono2_cliente, u9 notas_cliente, u10 direccion_cliente, u11 puntuacion_cliente, u6 monto_disponible, "arriba" estado_archivo) {
                //if (u6 == null) throw new AssertionError();//
                Cliente cliente = new Cliente(u0, u1, u2, u3, u4, u12, u13, u9, u10, Integer.parseInt(u6), u11, "arriba");
                String idEncontrado = "ninguno";
                if (!clientesEncontrados.containsKey(u0)) {
                    boolean flagEncontrado = false;
                    for (String key : clientesEncontrados.keySet()) {
                        if (clientesEncontrados.get(key).getId().contains(u0)) {
                            flagEncontrado = true;
                            idEncontrado = clientesEncontrados.get(key).getId();
                        }
                        if (u0.contains(clientesEncontrados.get(key).getId())) {
                            flagEncontrado = true;
                            idEncontrado = clientesEncontrados.get(key).getId();
                        }
                    }
                    if (!flagEncontrado) {
                        clientesEncontrados.put(u0, cliente);
                        new GuardarArchivo(cliente, newFile, "arriba", getApplicationContext()).guardarCliente();
                    } else {
                        Log.v("guardarClientes_1", "Main.\n\nCliente ya se ha ingresado:\n\n" + clientesEncontrados.get(idEncontrado) + "\n\n.");
                    }
                }
            }
        }
        revisar_ventas_de_hoy();
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

    private void mostrar_todoCierre () throws IOException {
        et_monto_mora.setVisibility(View.INVISIBLE);
        et_monto_prestado.setVisibility(View.INVISIBLE);
        et_balance_general.setVisibility(View.INVISIBLE);
        et_monto_recuperado.setVisibility(View.INVISIBLE);
        tv_monto_prestado.setVisibility(View.INVISIBLE);
        tv_balance_general.setVisibility(View.INVISIBLE);
        tv_monto_mora.setVisibility(View.INVISIBLE);
        tv_monto_recuperado.setVisibility(View.INVISIBLE);
        bt_cambiar_fecha.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        tv_auxiliar.setVisibility(View.INVISIBLE);
        et_multiLine.setVisibility(View.VISIBLE);
        if (!archivo_existe(fileList(), "cierre.txt")) {
            crear_archivo("cierre.txt");
        }
        borrar_archivo("cierre.txt");
        guardar(contenidoCierre, "cierre.txt");
        generar_cierre();
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

    public  void borrar_archivo (String file) throws IOException {
        File archivo = new File(file);
        String empty_string = "";
        guardar(empty_string, file);
        archivo.delete();
    }

    private void generar_cierre () {
        boolean flag_null = false;
        bt_imprimir.setVisibility(View.VISIBLE);
        String contenido_cierre = "*#*#*#*#*#* CIERRE *#*#*#*#*#*\n\nFecha: " + fecha + "/" + mes + "/" + anio
                + "\nCobrador: " + nombreCobrador + "\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("cierre.txt"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            int cont_abonar = 0;
            int cont_creditos = 0;
            int cont_bancas = 0;
            while (linea != null) {
                if (linea.equals("sin movimientos el dia") || linea.equals("de hoy!")) {
                    Log.v("generar_cierre_-1", "Finanzas.\n\nCierre vacio\n\n.");
                } else {
                    String[] split = linea.split(" ");
                    int lalrgo = split.length;
                    Log.v("generar_cierre0", "Finanzas.\n\nlinea:\n\n" + linea + "\n\ncont: " + cont_abonar + "\n\nlargo_split: " + lalrgo + "\n\n.");
                    String persona = new String();
                    String personaRuta = new String();
                    if (lalrgo == 3) {
                        persona = "cobrador";
                    } else if (lalrgo == 4) {
                        persona = split[3];
                        personaRuta = persona;
                        if (persona.contains("_P_")) {
                            String splitPersona[] = persona.split("_P_");
                            String nombreCliente = getNombreCliente(splitPersona[0]);
                            persona = nombreCliente;
                        }
                    }
                    String tipo = split[0];
                    String monto = split[1];
                    String caja = split[2];
                    String frase = tipo + " " + monto + " " + caja + " " + persona;
                    if (tipo.equals("abono")) {
                        Log.v("generar_cierre1", "Cierre.\n\ncont: " + cont_abonar + "\n\nvalue: " + frase + "\n\n.");
                        abonos.put(cont_abonar, frase);
                        cont_abonar++;
                    } else if (tipo.equals("credito")) {
                        Log.v("generar_cierre2", "Cierre.\n\ncont: " + cont_creditos + "\n\nvalue: " + frase + "\n\n.");
                        creditos.put(cont_creditos, frase);
                        cont_creditos++;
                    } else if (tipo.equals("banca")) {
                        String[] splitFrase = frase.split(" ");
                        int largoSplitFrase = splitFrase.length;
                        if (largoSplitFrase == 3) {
                            frase = frase + " " + personaRuta;
                        } else {
                            frase = frase.replace(persona, personaRuta);
                        }
                        Log.v("generar_cierre3", "Cierre.\n\ncont: " + cont_bancas + "\n\nvalue: " + frase + "\n\n.");
                        bancas.put(cont_bancas, frase);
                        cont_bancas++;
                    }
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        if (abonos.isEmpty() && creditos.isEmpty() && bancas.isEmpty()) {
            msg("Sin movimientos el dia de hoy!!!");
            contenido_cierre = contenido_cierre +
                    "Sin movimientos el dia de hoy!!!\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n\n\n";
            et_multiLine.setText(contenido_cierre);
            mensaje_imprimir = contenido_cierre;
            bt_imprimir.setVisibility(View.VISIBLE);
            onClickListener2 ();
            //boton_atras2("Sin movimientos el dia de hoy!!!");
        } else {
            if (!abonos.isEmpty()) {
                contenido_cierre = contenido_cierre + "#*#*#* ABONOS RECIBIDOS *#*#*#\n\n";
                for (Integer key : abonos.keySet()) {
                    String value = abonos.get(key);
                    String[] split_value = value.split(" ");
                    int monto_tempo = Integer.parseInt(split_value[1]);
                    balance_general_abonos = balance_general_abonos + monto_tempo;
                    Log.v("generar_cierre4", "Cierre.\n\nsplit_value.lenght: " + split_value.length + "\nsplit_value[" + 0 + "]: " + split_value[0] + "\nsplit_value[" + 1 + "]: " + split_value[1] + "\n\n.");
                    String nameCliente = "";
                    if (split_value.length == 4) {
                        nameCliente = split_value[3];
                    } else if (split_value.length >= 5) {
                        nameCliente = split_value[3] + " " + split_value[4];
                    }
                    //Log.v("generar_cierre5", "Cierre.\n\nnameCliente: " + nameCliente + "\n\n.");
                    contenido_cierre = contenido_cierre + "Abono de\n" + nameCliente + ":\nMonto: " +
                            split_value[1] + " colones.\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
                }
            }
            if (!creditos.isEmpty()) {
                contenido_cierre = contenido_cierre + "*#*#* CREDITOS APROBADOS *#*#*\n\n";
                for (Integer key : creditos.keySet()) {
                    String value = creditos.get(key);
                    String[] split_value = value.split(" ");
                    int monto_tempo = Integer.parseInt(split_value[1]);
                    balance_general_creditos = balance_general_creditos + monto_tempo;
                    //Log.v("generar_cierre4", "Cierre.\n\nsplit_value.lenght: " + split_value.length + "\nsplit_value[" + 3 + "]: " + split_value[3] + "\nsplit_value[" + 4 + "]: " + split_value[4] + "\n\n.");
                    String nameCliente = "";
                    if (split_value.length == 4) {
                        nameCliente = split_value[3];
                    } else if (split_value.length >= 5) {
                        nameCliente = split_value[3] + " " + split_value[4];
                    }
                    contenido_cierre = contenido_cierre + "Credito aprobado a:\n" + nameCliente + ":\nMonto: " +
                            split_value[1] + " colones.\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
                }
            }
            if (!bancas.isEmpty()) {
                contenido_cierre = contenido_cierre + "*#*#* MOVIMIENTOS BANCA *#*#*\n\n";
                for (Integer key : bancas.keySet()) {
                    String value = bancas.get(key);
                    String[] split_value = value.split(" ");
                    int valor_monto = Integer.parseInt(split_value[1]);
                    String persona = split_value[3];
                    String pre_mensaje = "";
                    if (persona.equals("banca")) {
                        if (valor_monto < 0) {
                            pre_mensaje = "Se entrega a banca:";
                            valor_monto = valor_monto * -1;
                            balance_general_banca_recibe = balance_general_banca_recibe + valor_monto;
                        } else {
                            pre_mensaje = "Se recibe de banca:";
                            balance_general_banca_entrega = balance_general_banca_entrega + valor_monto;
                        }
                        contenido_cierre = contenido_cierre + pre_mensaje + ":\nMonto: " +
                                String.valueOf(valor_monto) + " colones.\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
                    } else {
                        persona = persona.replace("_", " ");
                        pre_mensaje = "Se utilizan " + (-1 * valor_monto) + " colones\nde los fondos de caja para\ncubrir gastos de ruta.\n\nNotas:\n"
                                + persona + "\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
                        contenido_cierre = contenido_cierre + pre_mensaje;
                        balance_general_banca_entrega = balance_general_banca_entrega + valor_monto;
                    }
                }
            }
            contenido_cierre = contenido_cierre + "\nFirma cobrador:\n\n__________________\nNombre: " + nombreCobrador + ".\n\n" +
                    "T. cobrado: " + balance_general_abonos + " colones.\n\nT. prestado: " + balance_general_creditos +
                    " colones.\n\nAbonos banca: " + balance_general_banca_recibe + " colones.\n\nB. entrega: " +
                    balance_general_banca_entrega + " colones.\n\nBalance: " + String.valueOf(balance_general_abonos -
                    balance_general_creditos + balance_general_banca_entrega - balance_general_banca_recibe) +
                    " colones.\n\n#*#*#*#* ULTIMA LINEA *#*#*#*#\n\n\n\n";
            et_multiLine.setText(contenido_cierre);
            mensaje_imprimir = contenido_cierre;
            bt_imprimir.setVisibility(View.VISIBLE);
            onClickListener2 ();
        }
    }

    private void onClickListener2 () {
        bt_imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printIt();
            }
        });
    }

    private String getNombreCliente (String ID_buscado) {
        String nombreCliente = "";
        String archivos[] = fileList();
        for (int i = 0; i < archivos.length; i++) {
            Pattern pattern = Pattern.compile(ID_buscado + "_C_.txt", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    while (linea != null) {
                        //Log.v("getNombreCliente0", "Cierre.\n\nlinea:\n\n" + linea + "\n\n.");
                        String[] split = linea.split("_separador_");
                        if (split[0].equals("nombre_cliente")) {
                            if (nombreCliente.equals("")) {
                                nombreCliente = split[1];
                            } else {
                                nombreCliente = nombreCliente + " " + split[1];
                            }
                        }
                        if (split[0].equals("apellido1_cliente")) {
                            if (nombreCliente.equals("")) {
                                nombreCliente = split[1];
                            } else {
                                nombreCliente = nombreCliente + " " + split[1];
                            }
                        }
                        linea = br.readLine();
                    }
                    Log.v("getNombreCliente1", "Cierre.\n\nnombreCliente: " + nombreCliente + "\n\n.");
                    br.close();
                    archivo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return nombreCliente;
    }

    public void printIt () {
        BluetoothSocket socket;
        socket = null;
        byte[] data = mensaje_imprimir.getBytes();
        //Get BluetoothAdapter
        BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "BlueTooth abierto!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get sunmi InnerPrinter BluetoothDevice\
        String impresora = get_impresora();
        BluetoothDevice device = BluetoothUtil.getDevice(btAdapter, impresora);
        if (device == null) {
            Toast.makeText(getBaseContext(), "Asegurese de tener conectada una impresora!!!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            socket = BluetoothUtil.getSocket(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert socket != null;
            BluetoothUtil.sendData(data, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String get_impresora () {
        String impresora = "00:11:22:33:44:55";
        return impresora;
    }

    private void mostrar_todo () {

        et_monto_mora.setVisibility(View.INVISIBLE);
        et_monto_mora.setText(String.valueOf(monto_en_mora_a_hoy) + " colones");
        et_monto_prestado.setVisibility(View.INVISIBLE);
        et_monto_prestado.setText(String.valueOf(monto_prestado_total) + " colones");
        et_balance_general.setVisibility(View.INVISIBLE);
        et_balance_general.setText(String.valueOf(balance_general) + " colones");
        et_monto_recuperado.setVisibility(View.INVISIBLE);
        et_monto_recuperado.setText(String.valueOf(monto_recuperado_total) + " colones");
        tv_monto_recuperado.setText("Monto recuperado total:");
        tv_monto_prestado.setVisibility(View.INVISIBLE);
        tv_balance_general.setVisibility(View.INVISIBLE);
        tv_monto_mora.setVisibility(View.INVISIBLE);
        bt_cambiar_fecha.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        tv_auxiliar.setVisibility(View.INVISIBLE);
        et_multiLine.setVisibility(View.INVISIBLE);
    }

    private void ocultar_todo (String mensaje) {
        et_monto_mora.setVisibility(View.INVISIBLE);
        et_monto_prestado.setVisibility(View.INVISIBLE);
        et_balance_general.setVisibility(View.INVISIBLE);
        tv_monto_recuperado.setVisibility(View.VISIBLE);
        tv_monto_recuperado.setText(mensaje);
        tv_monto_prestado.setVisibility(View.INVISIBLE);
        tv_balance_general.setVisibility(View.INVISIBLE);
        tv_monto_mora.setVisibility(View.INVISIBLE);
        et_monto_recuperado.setVisibility(View.INVISIBLE);
        bt_cambiar_fecha.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        tv_auxiliar.setVisibility(View.INVISIBLE);
        et_multiLine.setVisibility(View.INVISIBLE);
    }

    private void revisar_ventas_de_hoy () throws IOException {
        ocultar_todo("Obteniendo ventas de hoy...");
        if (cobradores.isEmpty()) {
            mostrar_todoCierre();
            onClickListener();
        } else {
            for (String key : cobradores.keySet()) {
                String value = cobradores.get(key);
                Log.v("rev_ventas_hoy0", "Hoy.\n\nKey: " + key + "\nValue: " + value + "\n\n.");
                leer_ventas_nube(key, value);
                break;
            }
        }
    }

    private void leer_cobros_de_hoy () {
        ocultar_todo("Obteniendo cobros de hoy...");
        if (cobradores2.isEmpty()) {
            balance_general = monto_recuperado_total - monto_prestado_total;
            mostrar_todo();
            onClickListener();
        } else {
            for (String key : cobradores2.keySet()) {
                String value = cobradores2.get(key);
                Log.v("rev_ventas_hoy0", "Hoy.\n\nKey: " + key + "\nValue: " + value);
                leer_cobros_nube(key, value);
                break;
            }
        }
    }

    private void onClickListener () {
        bt_cambiar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrar_todo();
                cambiar_fecha();
            }
        });
    }

    private void cambiar_fecha () {
        bt_imprimir.setVisibility(View.INVISIBLE);
        final Calendar c = Calendar.getInstance();
        mes_I = (c.get(Calendar.MONTH));
        anio_I = c.get(Calendar.YEAR);
        fecha_I = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mes_I = i1+1;
                anio_I = i;
                fecha_I = i2;
                String i_s = String.valueOf(anio_I);
                String i1_s = String.valueOf(mes_I);
                String i2_s = String.valueOf(fecha_I);
                if (i1_s.length() == 1) {
                    i1_s = "0" + i1_s;
                }
                if (i2_s.length() == 1) {
                    i2_s = "0" + i2_s;
                }
                String mes_S = i1_s;
                String anio_S = i_s;
                String fecha_S = i2_s;
                mes = mes_S;
                anio = anio_S;
                fecha = fecha_S;
                String fecha_hoy_S = anio + "-" + mes + "-" + fecha;
                try {
                    fecha_hoy = DateUtilities.stringToDate(fecha_hoy_S);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                separar_fechaYhora();
                tv_fecha.setText(dia + "/" + mes + "/" + anio);
                Log.v("cambiar_fecha0", "Hoy.\n\nDate inicio:\n\n" + fecha_hoy.toString() + "\n\n.");
                String[] split_hoy_D = fecha_hoy.toString().split(" ");
                sheet_creditos = split_hoy_D[0] + "-" + split_hoy_D[1] + "-" + split_hoy_D[2] + "-" + split_hoy_D[5] + "-creditos";
                sheet_abonos = split_hoy_D[0] + "-" + split_hoy_D[1] + "-" + split_hoy_D[2] + "-" + split_hoy_D[5] + "-abonos";
                sheet_cierre = split_hoy_D[0] + "-" + split_hoy_D[1] + "-" + split_hoy_D[2] + "-" + split_hoy_D[5] + "-cierre";
                tv_saludo.setText("Cierre del dia: ");
                monto_en_mora_a_hoy = 0;
                monto_prestado_total = 0;
                monto_recuperado_total = 0;
                cobradores.clear();
                cobradores2.clear();
                lineas.clear();
                lineas2.clear();
                revisar_cobradores();
                Log.v("cambiar_fecha1", "Hoy.\n\nFecha antes de sumarle uno al mes:\n\n" + i2_s + "/" + i1_s + "/" + i_s + "\n\n.");
                Log.v("cambiar_fecha2", "Hoy.\n\nFecha despues de sumarle uno al mes:\n\n" + String.valueOf(fecha_S) + "/" + String.valueOf(mes_S) + "/" + String.valueOf(anio_S));
            }
        },anio_I,mes_I,fecha_I);
        datePickerDialog.show();
    }

    private void leer_ventas_nube (String key, String value) {

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
        Log.v("leer_ventas_nube_0", "Finanzas.\n\nkey:\n\n" + key + "\n\nvalue:\n\n" + value + "\n\n.");
        String[] split = value.split("_separador_");
        String spreadsheet_creditos2 = split[0];
        String apodo_cobrador = split[2];
        Log.v("leer_ventas_nube_1", "Finanzas.\n\nsprdSheet creditos: " + spreadsheet_creditos2 + "\n\nnombre cobrador: " + nombreCobrador + "\n\n.");
        linea = "";
        ocultar_todo("Ventas de " + apodo_cobrador + ",\npor favor espere...");

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

            String url = readRowURL + spreadsheet_creditos2 + "&sheet=" + sheet_cierre;

            Log.v("verificar_usuario()", ".\nurl: " + url + "\n.");

            // Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(String response) {
                            Log.v("responseDebug", "\n\n.response:\n\n" + response + "\n\n.");
                            if (response != null) {
                                if (response.contains("DOCTYPE")) {
                                    contLeer++;
                                    Log.v("contLeer", "\n\n.ContLeer: " + contLeer + "\n\n.");
                                    if (contLeer >= 16) {
                                        String responsE = "[{\"tipo\":\"[]";
                                        try {
                                            llenarMapas(responsE, "fin");
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        //boton_atras2("Error al bajar archivos,\nIntente mas tarde!!!");rrr
                                    } else {
                                        leer_ventas_nube(key, value);
                                    }
                                } else {
                                    cobradores.remove(key);
                                    try {
                                        llenarMapas(response);
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            } else {
                                contLeer++;
                                Log.v("contLeer", "\n\n.ContLeer: " + contLeer + "\n\n.");
                                if (contLeer >= 6) {
                                    boton_atras2("Error al bajar archivos,\nIntente mas tarde!!!");
                                } else {
                                    leer_ventas_nube(key, value);
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            leer_ventas_nube(key, value);
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        } else {
            msg("Debe estar conectado a una red WiFi o datos celular!");
        }
    }

    private void llenarMapas (Object response) throws IOException {
        String responsE = (String) response.toString();
        String contenidoCier;
        Log.v("llenarMapas_0", "Finanzas.\n\nresponse:\n\n" + response + "\n\nresponsE:\n\n" + responsE + "\n\n.");
        contenidoCier = "";
        ocultar_todo("Leyendo archivo " + "cierre.txt" + "...");
        String[] splitCierre = responsE.split("\"tipo\":\"");
        String tipo,monto,caja,cliente;
        tipo = monto = caja = cliente = "";
        for (String datoCierre : splitCierre) {
            if (!datoCierre.equals("[{")) {
                if (datoCierre.equals("[]")) {
                    contenidoCier = contenidoCier + "Sin movimientos el dia\nde hoy!\n";
                } else {
                    Log.v("llenarMapas_3", "Finanzas.\n\nDatoCierre: " + datoCierre + "\nfecha: " + fecha + "\n\n.");
                    String[] splitDatos = datoCierre.split("\",\"");
                    tipo = splitDatos[0];
                    String[] splitMonto = splitDatos[1].split("\":\"");
                    monto = splitMonto[1];
                    String[] splitCaja = splitDatos[2].split("\":\"");
                    caja = splitCaja[1];
                    String[] splitCliente = splitDatos[3].split("\":\"");
                    cliente = splitCliente[1];
                    cliente = cliente.replace("\"", "");
                    cliente = cliente.replace("}", "");
                    cliente = cliente.replace("]", "");
                    cliente = cliente.replace(",", "");
                    cliente = cliente.replace("{", "");
                    if (!contenidoCier.contains(tipo + " " + monto + " " + caja + " " + cliente)) {
                        contenidoCier = contenidoCier + tipo + " " + monto + " " + caja + " " + cliente + "\n";
                    }
                }
                Log.v("llenarMapas_4", "Finanzas.\n\nDatoCierre: " + datoCierre + "\nfecha: " + fecha + "\n\n.");
            }
        }
        contenidoCierre = contenidoCier;
        Log.v("llenarMapas_5", "Finanzas.\n\ntipo: " + tipo + "\nmonto: " + monto + "\ncaja: " + caja + "\ncliente: " + cliente + "\n\n.");
        revisar_ventas_de_hoy();
    }

    private void llenarMapas (Object response, String s) throws IOException {
        String responsE = (String) response.toString();
        String contenidoCier;
        Log.v("llenarMapas_0", "Finanzas.\n\nresponse:\n\n" + response + "\n\nresponsE:\n\n" + responsE + "\n\n.");
        contenidoCier = "";
        ocultar_todo("Leyendo archivo " + "cierre.txt" + "...");
        String[] splitCierre = responsE.split("\"tipo\":\"");
        String tipo,monto,caja,cliente;
        tipo = monto = caja = cliente = "";
        for (String datoCierre : splitCierre) {
            if (!datoCierre.equals("[{")) {
                if (datoCierre.equals("[]")) {
                    contenidoCier = contenidoCier + "Sin movimientos el dia\nde hoy!\n";
                } else {
                    Log.v("llenarMapas_3", "Finanzas.\n\nDatoCierre: " + datoCierre + "\nfecha: " + fecha + "\n\n.");
                    String[] splitDatos = datoCierre.split("\",\"");
                    tipo = splitDatos[0];
                    String[] splitMonto = splitDatos[1].split("\":\"");
                    monto = splitMonto[1];
                    String[] splitCaja = splitDatos[2].split("\":\"");
                    caja = splitCaja[1];
                    String[] splitCliente = splitDatos[3].split("\":\"");
                    cliente = splitCliente[1];
                    cliente = cliente.replace("\"", "");
                    cliente = cliente.replace("}", "");
                    cliente = cliente.replace("]", "");
                    cliente = cliente.replace(",", "");
                    cliente = cliente.replace("{", "");
                    if (!contenidoCier.contains(tipo + " " + monto + " " + caja + " " + cliente)) {
                        contenidoCier = contenidoCier + tipo + " " + monto + " " + caja + " " + cliente + "\n";
                    }
                }
                Log.v("llenarMapas_4", "Finanzas.\n\nDatoCierre: " + datoCierre + "\nfecha: " + fecha + "\n\n.");
            }
        }
        contenidoCierre = contenidoCier;
        Log.v("llenarMapas_5", "Finanzas.\n\ntipo: " + tipo + "\nmonto: " + monto + "\ncaja: " + caja + "\ncliente: " + cliente + "\n\n.");
        mostrar_todoCierre();
    }

    private void leer_cobros_nube (String key, String value) {

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
        spreadsheet_clientes = split[1];
        String apodo_cobrador = split[2];
        linea = "";
        //sheet_creditos = "creditos";
        //sheet_abonos = "abonos";
        ocultar_todo("Cobros de " + apodo_cobrador + ",\npor favor espere...");

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

            String url = readRowURL + spreadsheet_creditos + "&sheet=" + sheet_abonos;

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
                                cobradores2.remove(key);
                                String[] split = response.split("monto_credito");
                                if (split.length > 1) {
                                    for (int i = 0; i < split.length; i++) {
                                        String[] split2 = split[i].split("\"");
                                        Log.v("leer_cobros_nube0", "HoyActivity.\n\nSplit.length: " + split.length + "\n\nsplit[" + i + "]: " + split[i] + "\n\n.");
                                        if (split2.length > 1) {
                                            String linea_hash = split2[42] + "_sepa_linea_" + split2[38] + "_sepa_linea_" + split2[22] + "_sepa_linea_" +
                                                    split2[6] + "_sepa_linea_" + split2[14] + "_sepa_linea_" + split2[2] + "_sepa_linea_" + split2[46] + "_sepa_linea_" +
                                                    apodo_cobrador + "_sepa_linea_" + split2[50];
                                            if (!lineas2.containsValue(linea_hash)) {
                                                Log.v("leer_cobros_nube1", "Hoy.\n\nLinea hash: " + "\n\n" + linea_hash + "\n\n.");
                                                lineas2.put(i, linea_hash);
                                                monto_recuperado_total = monto_recuperado_total + Integer.parseInt(split2[50]);
                                                monto_en_mora_a_hoy = monto_en_mora_a_hoy + Integer.parseInt(split2[46]);
                                            }
                                        }
                                    }
                                }
                                mostrar_todo();
                                leer_cobros_de_hoy();
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
        contLeer = 0;
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
                            Log.v("rev_ventas_hoy0", ".\nResponse:\n" + response);
                            if (response != null) {
                                if (response.contains("DOCTYPE")){
                                    revisar_cobradores();
                                } else {
                                    String[] split = response.split("estado");
                                    if (split.length > 1) {
                                        for (int i = 0; i < split.length; i++) {
                                            String[] split2 = split[i].split("\"");
                                            Log.v("rev_ventas_hoy1", "HoyActivity.\n\nResponse: " + response + "\n\n.");
                                            if (split2.length > 1) {
                                                String key = split2[22];//cobrador_ID
                                                String value = split2[14] + "_separador_" + split2[18] + "_separador_" + split2[34] + "_separador_";//SpreadSheet_creditos_separador_SpreadSheet_clientes_separador_apodo_cobrador_separador_
                                                if (split2[2].equals("TRUE")) {
                                                    cobradores.put(key, value);
                                                }
                                            }
                                        }
                                    }
                                    mostrar_todo();
                                    lineas2.clear();
                                    llenar_spinner();
                                }
                            } else {
                                revisar_cobradores();
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
                            revisar_cobradores();
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        } else {
            msg("Debe estar conectado a una red WiFi o datos celular!");
        }
    }

    private void llenar_spinner () {
        ocultar_todo("Anctualizando cobradores...");
        String cobradores_S = "Escoja el cobrador...___";
        for (String key : cobradores.keySet()) {
            //String value = split2[14] + "_separador_" + split2[18] + "_separador_" + split2[34] + "_separador_";//SpreadSheet_creditos_separador_SpreadSheet_clientes_separador_apodo_cobrador_separador_
            String[] split = cobradores.get(key).split("_separador_");
            String apodo_cobrador = split[2];
            if (!apodo_cobrador.equals("apodo")) {
                cobradores_S = cobradores_S + apodo_cobrador + "___";
            }
        }
        Log.v("llenar_spinner_0", "Finanzas.\n\nCobradores:\n\n" + cobradores_S + "\n\n.");
        String[] split_spinner = cobradores_S.split("___");
        spinner.setEnabled(true);
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, split_spinner);
        spinner.setAdapter(adapter);
        spinner_listener();
        bt_cambiar_fecha.setVisibility(View.VISIBLE);
        onClickListener();
    }

    private void spinner_listener () {

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = spinner.getSelectedItem().toString();
                        if (!seleccion.equals("Escoja el cobrador...")) {
                            String newKey = "";
                            String newValue = "";
                            for (String key : cobradores.keySet()) {
                                String value = cobradores.get(key);
                                //String value = split2[14] + "_separador_" + split2[18] + "_separador_" + split2[34] + "_separador_";//SpreadSheet_creditos_separador_SpreadSheet_clientes_separador_apodo_cobrador_separador_
                                String[] split = value.split("_separador_");
                                String apodo_cobrador = split[2];
                                if (apodo_cobrador.equals(seleccion)) {
                                    spreadsheet_clientes = split[1];
                                    spreadsheet_creditos = split[0];
                                    nombreCobrador = apodo_cobrador;
                                    newKey = key;
                                    newValue = value;
                                    tv_auxiliar.setText("Cobrador: " + apodo_cobrador);
                                }
                            }
                            cobradores.clear();
                            cobradores.put(newKey, newValue);
                            ocultar_todo("Leyendo informacion de clientes...");
                            obtenerClientes();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
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

    private void boton_atras2(String s) {
        Toast.makeText(this.getApplicationContext(), s, Toast.LENGTH_LONG).show();
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

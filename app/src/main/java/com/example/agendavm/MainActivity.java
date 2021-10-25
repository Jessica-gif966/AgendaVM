package com.example.agendavm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
Button btn1,btn;
EditText et1,et2;
TextInputLayout ti1,ti2;
ProgressDialog progressDialog;
RequestQueue requestQueue;
String e,p1;
String HttpURI="http://192.168.0.103:8080/agendavm/usuario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //enlazamos objetos con controladores
        ti1=(TextInputLayout)findViewById(R.id.ti1);
        ti2=(TextInputLayout)findViewById(R.id.ti2);
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        btn1=(Button)findViewById(R.id.btn1);
        btn=(Button)findViewById(R.id.btn);
        //inicializar requestqueue
        requestQueue= Volley.newRequestQueue(MainActivity.this);
        //inicializar el progressdialog
        progressDialog=new ProgressDialog(MainActivity.this);

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ti1.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ti2.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        //oyente
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String u=et1.getText().toString();
        String p=et2.getText().toString();

        boolean a=esNombreValido(u);
        boolean b=esPasswordValido(p);

            if(a&&b){

            //mostramos el progressdialog
            progressDialog.setMessage("Procesando...");
            progressDialog.show();

            //creacion de la cadena a ejecutar en el webservices mediante volley
            StringRequest stringRequest=new StringRequest(Request.Method.POST, HttpURI, new Response.Listener<String>() {
                @Override
                public void onResponse(String serverResponse) {
                    //ocultamos el progress dialog
                    progressDialog.dismiss();
                    //realizar try y catch para el manejo de errores
                    try{
                        JSONObject object=new JSONObject(serverResponse);
                        Boolean error=object.getBoolean("error");
                        String mensaje=object.getString("mensaje");
                        if(error==true){
                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Acceso Correcto", Toast.LENGTH_SHORT).show();

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //ocultamos el progress dialog
                    progressDialog.dismiss();
                    //colocar un toast, colocar el volley error
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            }){
                protected Map<String,String>getParams(){
                    Map<String,String>parametros=new HashMap<String,String>();
                    parametros.put("nombre",u);
                    parametros.put("password",p);
                    return parametros;
                }
            };
            requestQueue.add(stringRequest);}
            Intent intent1=new Intent(getApplicationContext(),Agenda.class);
            startActivity(intent1);
    }

    private boolean esPasswordValido(String p) {

        if(p.length()>8||p.length()<1){
            ti2.setError("Password Invalida");
            return false;
        }else{
            ti2.setError(null);
        }return true;
    }

    private boolean esNombreValido(String u) {
        Pattern patron=Pattern.compile("^[a-zA-Z]+$");
        if(!patron.matcher(u).matches()||u.length()>12){
            ti1.setError("Nombre Invalido");
            return false;
        }else{
            ti1.setError(null);
        }
        return true;

    }
}
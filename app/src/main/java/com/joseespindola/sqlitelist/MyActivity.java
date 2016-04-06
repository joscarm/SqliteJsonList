package com.joseespindola.sqlitelist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

public class MyActivity extends AppCompatActivity {

    Dialog customDialog = null;
    ListView lista;
    SQLControlador dbconnection;
    TextView tv_miemID, tv_miemNombre, tv_miemNombre2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        dbconnection = new SQLControlador(this);
       try {
           dbconnection.openDB();
       } catch (SQLException e) {
           e.printStackTrace();
        }

        lista = (ListView) findViewById(R.id.listViewMiembros);

        showDataList();//invoca clase para cargar Listview con informacion.

        // acción cuando hacemos click en item para poder modificarlo o eliminarlo
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                tv_miemID = (TextView) view.findViewById(R.id.miembro_id);
                tv_miemNombre = (TextView) view.findViewById(R.id.miembro_nombre);
                tv_miemNombre2 = (TextView) view.findViewById(R.id.miembro_nombre2);

                String aux_miembroId = tv_miemID.getText().toString();
                String aux_miembroNombre = tv_miemNombre.getText().toString();
                String aux_miembroNombre2 = tv_miemNombre2.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(), UpdateDelete.class);
                modify_intent.putExtra("miembroId", aux_miembroId);
                modify_intent.putExtra("miembroNombre", aux_miembroNombre);
                modify_intent.putExtra("miembroNombre2", aux_miembroNombre2);
                startActivity(modify_intent);
            }
        });
    }  //termina el onCreate

    public void addMore()//venta de dialogo para añadir miembro
    {
        // con este tema personalizado evitamos los bordes por defecto
        customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
        //deshabilitamos el título por defecto
        //customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setTitle("Agregar nuevo...");
        //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setCancelable(false);
        //establecemos el contenido de nuestro dialog
        customDialog.setContentView(R.layout.add);

        final TextView et = (TextView)customDialog.findViewById(R.id.et_miembro_id);
        final TextView et2 = (TextView)customDialog.findViewById(R.id.et_miembro_id2);

        ((Button) customDialog.findViewById(R.id.btnAgregarId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et.getText().toString();
                String name2 = et2.getText().toString();
                dbconnection.insertData(name, name2);//añadir datos a BD
                showDataList();//muestra informacion.
                customDialog.dismiss();//cerrar ventana de dialogo
            }
        });
        ((Button) customDialog.findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }//termina clase addMore

    public void showDataList()//clase que carga informacion en ListView desde Bd.
    {
        Cursor cursor = dbconnection.loadData();
        String[] from = new String[] {
                DBhelper.MIEMBRO_ID,
                DBhelper.MIEMBRO_NOMBRE,
                DBhelper.MIEMBRO_APELLIDO
        };
        int[] to = new int[] {
                R.id.miembro_id,
                R.id.miembro_nombre,
                R.id.miembro_nombre2
        };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(MyActivity.this, R.layout.row_format, cursor, from, to);
//        View header = (View)getLayoutInflater().inflate(R.layout.header,null);
        adapter.notifyDataSetChanged();
//        lista.addHeaderView(header);
        lista.setAdapter(adapter);
    }//termina clase showDataList

    class DataFetcherTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONObject jsonObject = new JSONObject(finalJson);
                JSONArray jsonArray = jsonObject.getJSONArray("movies");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectMovies = jsonArray.getJSONObject(i);
                    String movieName = jsonObjectMovies.getString("movie");
                    String movieDirector = jsonObjectMovies.getString("director");
                    dbconnection.insertData(movieName, movieDirector);//añadir datos a BD
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showDataList();
        }
    }//termina DataFetchertask

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update){
            //se checa conexion a la red, si tiene conexion ejecuta
            if (NetworkConecction.isOnline(MyActivity.this)){
                new DataFetcherTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesData.txt");
            }
            else
            {
                Toast.makeText(MyActivity.this, "Revise su conexion a la red", Toast.LENGTH_LONG).show();
            }
        }
        else if(id == R.id.action_add){
            addMore();

        }
        else if(id == R.id.action_clear){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
            alertDialogBuilder.setTitle("Limpiar tabla");
            alertDialogBuilder.setMessage("¿Esta seguro que deseas eliminar todos los registros de la tabla?");

            alertDialogBuilder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbconnection.clearTable();
                    showDataList();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //nothing
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(id == R.id.action_exit){
            finish();
        }
        //showDataList();
        return super.onOptionsItemSelected(item);
    }//termina onOptionsItemSelect
} //termina clase
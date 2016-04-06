package com.joseespindola.sqlitelist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

public class UpdateDelete extends AppCompatActivity implements View.OnClickListener {
    EditText et,et2;
    Button btnActualizar, btnEliminar;

    long member_id;

    SQLControlador dbcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_delete);

        dbcon = new SQLControlador(this);
        try {
            dbcon.openDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        et = (EditText) findViewById(R.id.et_miembro_id);
        et2 = (EditText) findViewById(R.id.et_miembro_id2);

        btnActualizar = (Button) findViewById(R.id.btnActualizar);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);

        Intent i = getIntent();
        String memberID = i.getStringExtra("miembroId");
        String memberName = i.getStringExtra("miembroNombre");
        String mLast_name = i.getStringExtra("miembroNombre2");

        member_id = Long.parseLong(memberID);

        et.setText(memberName);
        et2.setText(mLast_name);

        btnActualizar.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnActualizar:
                String memName_upd = et.getText().toString();
                String memLast_upd = et2.getText().toString();
                dbcon.updateData(member_id, memName_upd, memLast_upd);
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                this.returnHome();
                break;

            case R.id.btnEliminar:
                delete();
               /* dbcon.deleteData(member_id);
                this.returnHome();*/
                break;
        }
    }

    //clase para regresas a la pagina principal.
    public void returnHome() {

        Intent home_intent = new Intent(getApplicationContext(),
                MyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(home_intent);
    }
    public void delete(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder.setTitle("ELIMINAR!!!");
        alertDialogBuilder.setMessage("¿Estas seguro que desea eliminar este registro?");

        alertDialogBuilder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dbcon.deleteData(member_id);//elimina registro de Bd
                returnHome();//regresa a ventana principal
            }
        });
        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish(); //vuelve a ventana principal
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

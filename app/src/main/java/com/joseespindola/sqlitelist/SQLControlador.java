package com.joseespindola.sqlitelist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by joseespindola on 09/03/16.
 */
public class SQLControlador {
    private DBhelper dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLControlador(Context c) {
        ourcontext = c;
    }

    public SQLControlador openDB() throws SQLException {
        dbhelper = new DBhelper(ourcontext);
        database = dbhelper.getWritableDatabase();
        return this;
    }

    public void cerrar() {
        dbhelper.close();
    }

    public void insertData(String name, String name2) {
        ContentValues cv = new ContentValues();
        cv.put(DBhelper.MIEMBRO_NOMBRE, name);
        cv.put(DBhelper.MIEMBRO_APELLIDO, name2);
        database.insert(DBhelper.TABLE_MEMBER, null, cv);
    }

    public Cursor loadData() {
        String[] todasLasColumnas = new String[] {
                DBhelper.MIEMBRO_ID,
                DBhelper.MIEMBRO_NOMBRE,
                DBhelper.MIEMBRO_APELLIDO};
        Cursor c = database.query(DBhelper.TABLE_MEMBER, todasLasColumnas, null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public int updateData(long memberID, String memberName, String memberName2) {
        ContentValues cvActualizar = new ContentValues();
        cvActualizar.put(DBhelper.MIEMBRO_NOMBRE, memberName);
        cvActualizar.put(DBhelper.MIEMBRO_APELLIDO, memberName2);
        int i = database.update(DBhelper.TABLE_MEMBER, cvActualizar,
                DBhelper.MIEMBRO_ID + " = " + memberID, null);
        return i;
    }

    public void deleteData(long memberID) {
        database.delete(DBhelper.TABLE_MEMBER, DBhelper.MIEMBRO_ID + "="
                + memberID, null);
    }
    public void clearTable(){
        database.execSQL("delete from " + DBhelper.TABLE_MEMBER);
    }
}
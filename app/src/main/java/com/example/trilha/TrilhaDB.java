package com.example.trilha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TrilhaDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "trilha_database";
    private static final int VERSION = 1;

    public TrilhaDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE waypoints (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "latitude NUMERIC NOT NULL, longitude NUMERIC NOT NULL, altitude NUMERIC NOT NULL);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS waypoints";
        db.execSQL(dropTable);
        onCreate(db);
    }

    // Método para salvar waypoint
    public void registrarWaypoint(Waypoint waypoint) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", waypoint.getLatitude());
        values.put("longitude", waypoint.getLongitude());
        values.put("altitude", waypoint.getAltitude());
        db.insert("waypoints", null, values);
    }

    // Método para recuperar waypoints
    public ArrayList<Waypoint> recuperarWaypoints() {
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("waypoints", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Índices das colunas
            int indexLatitude = cursor.getColumnIndex("latitude");
            int indexLongitude = cursor.getColumnIndex("longitude");
            int indexAltitude = cursor.getColumnIndex("altitude");

            // Verificando se os índices são válidos
            if (indexLatitude >= 0 && indexLongitude >= 0 && indexAltitude >= 0) {
                do {
                    double latitude = cursor.getDouble(indexLatitude);
                    double longitude = cursor.getDouble(indexLongitude);
                    double altitude = cursor.getDouble(indexAltitude);

                    Waypoint waypoint = new Waypoint(latitude, longitude, altitude);
                    waypoints.add(waypoint);
                } while (cursor.moveToNext());
            } else {
                System.out.println("Erro: uma ou mais colunas não foram encontradas.");
            }
            cursor.close();
        } else {
            System.out.println("Nenhum waypoint encontrado.");
        }

        return waypoints;
    }
    // Método para apagar todos os registros de waypoints
    public void apagarTrilha() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("waypoints", null, null);  // Apaga todos os registros da tabela
    }
}

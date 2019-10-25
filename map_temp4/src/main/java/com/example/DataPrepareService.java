package com.example;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class DataPrepareService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private final String dbName = "mDatabase.db";

    public DataPrepareService() {
        super("data_prepare_service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(!doseDatabaseExist(this, dbName)){
            HashMap<String, String> sa2_code = new HashMap<>();
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbName, null);
            try {
                InputStream is = getAssets().open("sa2_code.csv");
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr);

                reader.readLine();
                String line;
                String[] temp;
                while((line = reader.readLine())!=null){

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private boolean doseDatabaseExist(Context context, String dbName){
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}

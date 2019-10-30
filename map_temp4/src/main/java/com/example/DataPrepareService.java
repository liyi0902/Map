//package com.example;
//
//import android.app.IntentService;
//import android.content.Context;
//import android.content.Intent;
//
//import androidx.annotation.Nullable;
//
//import java.io.File;
//
//public class DataPrepareService extends IntentService{
//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    private final String dbName = "Project_db.db";
//    public DatabaseAccess access;
//
//
//
//    public DataPrepareService() {
//        super("data_prepare_service");
//    }
//
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//
//        //access = DatabaseAccess.getInstance(getApplicationContext());
////        access.open();
//
//
//
//
////            HashMap<String, String> sa2_code = new HashMap<>();
////            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbName, null);
////            try {
////                InputStream is = getAssets().open("sa2_code.csv");
////                InputStreamReader isr = new InputStreamReader(is);
////                BufferedReader reader = new BufferedReader(isr);
////
////                reader.readLine();
////                String line;
////                String[] temp;
////                while((line = reader.readLine())!=null){
////
////                }
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
//
//
//    }
//
//    private boolean doseDatabaseExist(Context context, String dbName){
//        File dbFile = context.getDatabasePath(dbName);
//        return dbFile.exists();
//    }
//}

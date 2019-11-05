package com.example;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/***For database management, with parsing input and return filter search result.
 * Methods filterRequest, polygonRequest and centerRequest are used by main activity。
 ***/
public class DBActivity {
    public DatabaseAccess access;
    private static int NUMBER_OF_SEARCH = 3;
//    protected void onCreate(Bundle savedInstanceState) {
//        getAccess();
//    }
    public DBActivity(){
        getAccess();
    }

    public void getAccess(){
        access = DatabaseAccess.getInstance(MapActivity.mContext);
        access.open();
    }
    /*Parse HashMap, transform input data into int[]*/
    private int[] parseInput(HashMap input){
        String[] result = new String[16];
        result[2] = input.get("housePrice").toString();
        result[3] = input.get("houseRent").toString();
        result[4] = input.get("unitPrice").toString();
        result[5] = input.get("unitRent").toString();
        result[6] = input.get("traffic").toString();
        result[7] = input.get("income").toString();
        result[8] = input.get("education").toString();
        result[9] = input.get("immigrant").toString();
        result[10] ="0";
        result[11] ="0";
        result[12] ="0";
        result[13] ="0";
        result[14] ="0";
        result[15] ="0";
        if(notNull(input, "religion")) {
            String m = input.get("religion").toString();
            if ("christian".equals(m)) {
                result[10] = "5";
            }else if ("buddhism".equals(m)) {
                result[11] = "5";
            }else if ("judasim".equals(m)) {
                result[13] = "5";
            }else if ("hinduism".equals(m)) {
                result[12] = "5";
            }else if ("islam".equals(m)) {
                result[14] = "5";
            }else if ("others".equals(m)) {
                result[15] = "5";
            }
        }
        for(int x=0;x<result.length;x++){
            if(result[x] != null){
                Log.i("int",result[x].toString());}
        }
        return toInt(result);
    }
    /*For identifying if the key does have a value*/
    private boolean notNull(HashMap input, String m){
        if (input.get(m) != null) return true;
        else return false;
    }
    /*Transform String[] into int[]*/
    private int[] toInt(String[] columns){
        int[] result = new int[16];
        for (int i = 2;i<16;i++) {
            result[i] = Integer.parseInt(columns[i]);
        }
        return result;
    }

    /*Generate a list where all culumns contained*/
    private String[] getSa2() {
        String[] result = new String[16];
        result[0] = "sa2_main16";
        result[1] = "sa2_name16";
        result[2] = "h_sale_price";
        result[3] ="h_rent_price";
        result[4] = "u_sale_price";
        result[5] = "u_rent_price";
        result[6] = "vehicle_num";
        result[7] ="median_income";
        result[8] ="children_education";
        result[9] ="immi_not_citizen";
        result[10] ="christian_pr100";
        result[11] ="buddhism_pr100";
        result[12] ="hinduism_pr100";
        result[13] = "judaism_pr100 ";
        result[14] ="islam_pr100";
        result[15] ="other_religion_pr100";
        return result;
    }

    /*Get which column is required bu the request*/
    private String[] genColumn(int[] input){
        String[] columns = getSa2();
        int i; int num = 2;
        for(i=2; i<input.length; i++){
            if (input[i] != 0){
                num++;
            }
        }
        String[] result = new String[num];
        result[0] = "sa2_main16";
        result[1] = "sa2_name16";
        int j = 2;
        for(i=2;i<input.length;i++){
            if (input[i] != 0){
                result[j] = columns[i];
                j++;
            }
        }
        for(int x=0;x<result.length;x++){
            Log.i("column",result[x].toString());
        }
        return result;
    }
    /*Generate the sentence following "ORDER BY" in SQL
    Finalweight = (Weight(i)*Attribute(i))*/
    private String genWeight(int[] input){
        String[] columns = getSa2();
        String result = "";
        for(int i =2; i<16;i++) {
            if (input[i] != 0) {
                if(i == 7){
                    int j = input[6];
                    result = result + " - " + j + "*" + columns[i];
                }
                else{
                    int j = input[i];
                    if (result == "") result = j + "*" + columns[i];
                    else result = result + " + " + j + "*" + columns[i];}
            }
        }
        result = result + " DESC";
        return result;
    }

    /*Get sa2_main16 attribute from the cursor
    return with a String[] which contains all codes in the cursor*/
    public String[] getMain(Cursor cursor){
        String[] result = new String[NUMBER_OF_SEARCH];
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            int i = 0;
            do {
                result[i] = cursor.getString(cursor.getColumnIndex("sa2_main16"));
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        else{cursor.close();result = null;}
        for(int x=0;x<result.length;x++){
            Log.i("main",result[x].toString());
        }
        return result;
    }

    /*Using main code and column to start another query, 
    get the original values
    ArrayList<HashMap<String,String>>*/
    public ArrayList<HashMap<String,String>> returnResult(String[] main, String[] column){
        ArrayList<HashMap<String, String>> result = new ArrayList();
        String[] keys = getSa2();
        String where = new String();
        where = "sa2_main16 = " + main[0];
        int length = main.length;
        if(length >1) {
            for (int q = 1; q < length; q++) {
                where = where + " OR sa2_main16 = " + main[q];
            }
        }
        Cursor cursor = access.query("SA2_data_1", null, where,null);
        String[] output = new String[cursor.getColumnCount()];
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                HashMap<String, String> maps = new HashMap();
                for (int j = 0; j <= (cursor.getColumnCount() - 1); j++) {
                    maps.put(keys[j],cursor.getString(j));
                }
                result.add(maps);
            } while (cursor.moveToNext());
            cursor.close();
        }
        else {cursor.close();result = null;}


        return result;

    }

    /*Used by filter query, invoked by main activity.
     * Return a ArrayList<HashMap<String, String>>
     * (sa2_main16和sa2_name16 are added)
     * Only non-zero input will be returned easy for illustration。
     */
    public ArrayList<HashMap<String, String>> filterRequest(HashMap map){
        int[] input = parseInput(map);
        String[] columns = genColumn(input);
        String weight = genWeight(input);
        Cursor cursor = access.query("SA2_data_normal", columns, null, weight);
        if(cursor.getCount() != 0){
            String[] main = getMain(cursor);
            cursor.close();
            return returnResult(main,columns);
        }
        else {cursor.close();return null;}
    }


    /*For getting coordinates of a polygon with inputing the sa2_main16
    return with an ArrayList which contains many float[2]
    each list is a coordinate of a point*/
    public ArrayList polygonRequest(String main){
        Cursor cursor = access.queryCoordinate("Greater_MEL","sa2_main16 = "+main);
        ArrayList result = new ArrayList();
        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            do{
                float[] coordinate = new float[2];
                coordinate[0] = cursor.getFloat(cursor.getColumnIndex("POINT_X"));
                coordinate[1] = cursor.getFloat(cursor.getColumnIndex("POINT_Y"));
                result.add(coordinate);
            }while(cursor.moveToNext());
            cursor.close();}
        else {cursor.close();result = null;}
        return result;
    }
     /*For getting coordinates of the center with inputing the sa2_main16
    return float[2] where each list is a coordinate of a point*/
    public float[] centerRequest(String main){
        float[] result = new float[2];
        Cursor cursor = access.queryCoordinate("Center","sa2_main16 = "+main);
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            result[0] = cursor.getFloat(cursor.getColumnIndex("POINT_X"));
            result[1] = cursor.getFloat(cursor.getColumnIndex("POINT_Y"));
            cursor.close();
        }
        else {cursor.close();result = null;}
        return result;
    }

}

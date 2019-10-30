package com.example;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/***用于与DB有关的IO。
 * 最重要的三个方法在类的最下面，分别是
 * filterRequest, polygonRequest和centerRequest。
 * 关于DatabaseAccess实例的访问，这边可能需要你吧getApplicationContext()弄成static或者写一个getInstance()***/
public class DBActivity {
    public DatabaseAccess access;
    private static int NUMBER_OF_SEARCH = 1;
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
    /*用于将HashMap转成int[].
     * 值得一提的是你那边judaism拼成了judasim，我按照你的写了，最后数据展示出来不要写错就好*/
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
    private boolean notNull(HashMap input, String m){
        if (input.get(m) != null) return true;
        else return false;
    }
    private int[] toInt(String[] columns){
        int[] result = new int[16];
        for (int i = 2;i<16;i++) {
            result[i] = Integer.parseInt(columns[i]);
        }
        return result;
    }

    /*用于生成一个包含所有表字段的数组*/
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
    /*用于获取权重在SQL中order by 的表达式。
     * 值得一提的是只有vehicle_num这个字段是负值，
     * 原因是车辆数量越多其traffic权值应该越低。*/
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


    public HashMap returnResult(String[] main, String[] column){
        HashMap<String, String> result = new HashMap();
        String[] keys = getSa2();
        String where = new String();
        int length = main.length;
        if(length >1) {
            for (int q = 1; q < length; q++) {
                where = where + "OR sa2_main16 = " + main[q];
            }
        }else{where = "sa2_main16 = " + main[0];}
        Cursor cursor = access.query("SA2_data_1", null, where,null);
        String[] output = new String[cursor.getColumnCount()];
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                for (int j = 0; j <= (cursor.getColumnCount() - 1); j++) {
                    result.put(keys[j],cursor.getString(j));
                }

            } while (cursor.moveToNext());
            cursor.close();
        }
        else {cursor.close();result = null;}


        return result;

    }

    /*用于获取每一次filter检索的内容，输入的格式是一个HashMap。
     * 返回的格式是一个ArrayList，其中每个元素是一个长度为输入检索key数量+2的float[]
     * （多了sa2_main16和sa2_name16两个字段）
     * 每个float[]中按照上面getSa2()方法中的顺序给出了结果
     * 注意：返回的结果只包含了输入value不为0的字段，为0的字段会被跳过。这也便于展示数据。
     * 目前最多返回一个区域的结果。
     * 若检索结果为空，将返回null。*/
    public HashMap filterRequest(HashMap map){
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


    /*用于获取polygon坐标。输入的是sa2_main16，即该polygon的编号。
    返回格式是一个存储了float[2]的ArrayList。每一个数组是一对坐标。
    若检索结果为空，将返回null。*/
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
    /*用于获取polygon中心点的坐标。输入的是sa2_main16，即该polygon的编号。
    返回格式是一个float[2]。每一个数组是一对坐标。
    若检索结果为空，将返回null。*/
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

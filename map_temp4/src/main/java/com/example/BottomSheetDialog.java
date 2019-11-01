package com.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    private Button btn_confirm;
    private Button btn_cancel;

    private SeekBar sb_house_price;
    private SeekBar sb_unit_price;
    private SeekBar sb_traffic;
    private SeekBar sb_house_rent;
    private SeekBar sb_unit_rent;
    private SeekBar sb_income;
    private SeekBar sb_education;
    private SeekBar sb_immigrants;

    private Button btn_christian;
    private Button btn_buddhism;
    private Button btn_hinduism;
    private Button btn_judaism;
    private Button btn_islam;
    private Button btn_others;

    private TextView text_save;

    private ArrayList<HashMap<String, String>> savedOptions;
    private HashMap<String, String> selectedItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        btn_confirm = v.findViewById(R.id.btn_confirm);

        btn_christian = v.findViewById(R.id.choose_christrian);
        btn_buddhism = v.findViewById(R.id.choose_buddhism);
        btn_hinduism = v.findViewById(R.id.choose_hinduism);
        btn_judaism = v.findViewById(R.id.choose_judaism);
        btn_islam = v.findViewById(R.id.choose_islam);
        btn_others = v.findViewById(R.id.choose_others);

        sb_house_price = v.findViewById(R.id.seekBar_house_price);
        sb_unit_price = v.findViewById(R.id.seekBar_unit_price);
        sb_traffic = v.findViewById(R.id.seekBar_traffic);
        sb_house_rent = v.findViewById(R.id.seekBar_house_rent);
        sb_unit_rent = v.findViewById(R.id.seekBar_unit_rent);
        sb_income = v.findViewById(R.id.seekBar_income);
        sb_education = v.findViewById(R.id.seekBar_education);
        sb_immigrants = v.findViewById(R.id.seekBar_immigrant);

        text_save = v.findViewById(R.id.text_save);

        Bundle bundle = getArguments();
        if(bundle != null){
            if(bundle.getSerializable("savedOptions") != null){
                savedOptions = (ArrayList<HashMap<String, String>>) bundle.getSerializable("savedOptions");
            }
            if(bundle.getSerializable("selectedItem") != null){
                selectedItem = (HashMap<String, String>) bundle.getSerializable("selectedItem");
                setFilter();
            }
        }




        btn_christian.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                ColorDrawable btnColor = (ColorDrawable)btn_christian.getBackground();
                if(btnColor.getColor() == getResources().getColor(R.color.notSelected)){
                    btn_christian.setBackgroundResource(R.color.selected);
                    btn_buddhism.setBackgroundResource(R.color.notSelected);
                    btn_hinduism.setBackgroundResource(R.color.notSelected);
                    btn_judaism.setBackgroundResource(R.color.notSelected);
                    btn_islam.setBackgroundResource(R.color.notSelected);
                    btn_others.setBackgroundResource(R.color.notSelected);
                }else if(btnColor.getColor() == getResources().getColor(R.color.selected)){
                    btn_christian.setBackgroundResource(R.color.notSelected);
                }
            }
        });

        btn_buddhism.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                ColorDrawable btnColor = (ColorDrawable)btn_buddhism.getBackground();
                if(btnColor.getColor() == getResources().getColor(R.color.notSelected)){
                    btn_christian.setBackgroundResource(R.color.notSelected);
                    btn_buddhism.setBackgroundResource(R.color.selected);
                    btn_hinduism.setBackgroundResource(R.color.notSelected);
                    btn_judaism.setBackgroundResource(R.color.notSelected);
                    btn_islam.setBackgroundResource(R.color.notSelected);
                    btn_others.setBackgroundResource(R.color.notSelected);
                }else if(btnColor.getColor() == getResources().getColor(R.color.selected)){
                    btn_buddhism.setBackgroundResource(R.color.notSelected);
                }
            }
        });

        btn_hinduism.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                ColorDrawable btnColor = (ColorDrawable)btn_hinduism.getBackground();
                if(btnColor.getColor() == getResources().getColor(R.color.notSelected)){
                    btn_christian.setBackgroundResource(R.color.notSelected);
                    btn_buddhism.setBackgroundResource(R.color.notSelected);
                    btn_hinduism.setBackgroundResource(R.color.selected);
                    btn_judaism.setBackgroundResource(R.color.notSelected);
                    btn_islam.setBackgroundResource(R.color.notSelected);
                    btn_others.setBackgroundResource(R.color.notSelected);
                }else if(btnColor.getColor() == getResources().getColor(R.color.selected)){
                    btn_hinduism.setBackgroundResource(R.color.notSelected);
                }
            }
        });

        btn_judaism.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                ColorDrawable btnColor = (ColorDrawable)btn_judaism.getBackground();
                if(btnColor.getColor() == getResources().getColor(R.color.notSelected)){
                    btn_christian.setBackgroundResource(R.color.notSelected);
                    btn_buddhism.setBackgroundResource(R.color.notSelected);
                    btn_hinduism.setBackgroundResource(R.color.notSelected);
                    btn_judaism.setBackgroundResource(R.color.selected);
                    btn_islam.setBackgroundResource(R.color.notSelected);
                    btn_others.setBackgroundResource(R.color.notSelected);
                }else if(btnColor.getColor() == getResources().getColor(R.color.selected)){
                    btn_judaism.setBackgroundResource(R.color.notSelected);
                }
            }
        });


        btn_islam.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                ColorDrawable btnColor = (ColorDrawable)btn_islam.getBackground();
                if(btnColor.getColor() == getResources().getColor(R.color.notSelected)){
                    btn_christian.setBackgroundResource(R.color.notSelected);
                    btn_buddhism.setBackgroundResource(R.color.notSelected);
                    btn_hinduism.setBackgroundResource(R.color.notSelected);
                    btn_judaism.setBackgroundResource(R.color.notSelected);
                    btn_islam.setBackgroundResource(R.color.selected);
                    btn_others.setBackgroundResource(R.color.notSelected);
                }else if(btnColor.getColor() == getResources().getColor(R.color.selected)){
                    btn_islam.setBackgroundResource(R.color.notSelected);
                }
            }
        });


        btn_others.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                ColorDrawable btnColor = (ColorDrawable)btn_others.getBackground();
                if(btnColor.getColor() == getResources().getColor(R.color.notSelected)){
                    btn_christian.setBackgroundResource(R.color.notSelected);
                    btn_buddhism.setBackgroundResource(R.color.notSelected);
                    btn_hinduism.setBackgroundResource(R.color.notSelected);
                    btn_judaism.setBackgroundResource(R.color.notSelected);
                    btn_islam.setBackgroundResource(R.color.notSelected);
                    btn_others.setBackgroundResource(R.color.selected);
                }else if(btnColor.getColor() == getResources().getColor(R.color.selected)){
                    btn_others.setBackgroundResource(R.color.notSelected);
                }
            }
        });


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSetFilter()){
                    HashMap<String, String> result = new HashMap<>();
                    String housePrice = String.valueOf(sb_house_price.getProgress());
                    String unitPrice = String.valueOf(sb_unit_price.getProgress());
                    String traffic = String.valueOf(sb_traffic.getProgress());
                    String houseRent = String.valueOf(sb_house_rent.getProgress());
                    String unitRent = String.valueOf(sb_unit_rent.getProgress());
                    String income = String.valueOf(sb_income.getProgress());
                    String education = String.valueOf(sb_education.getProgress());
                    String immigrant = String.valueOf(sb_immigrants.getProgress());
                    String religion = religionSelected();
                    result.put("housePrice", housePrice);
                    result.put("unitPrice", unitPrice);
                    result.put("traffic", traffic);
                    result.put("houseRent", houseRent);
                    result.put("unitRent", unitRent);
                    result.put("income", income);
                    result.put("education", education);
                    result.put("immigrant", immigrant);
                    result.put("religion", religion);
                    mListener.onButtomClicked(result, "confirm");
                    dismiss();
                }else{
                    Toast.makeText(MapActivity.mContext, "Please set the filter", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_cancel = v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dismiss();
            }
        });

        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSetFilter()){
                    if(isExistInMenu()){
                        Toast.makeText(getActivity(), "The same setting is already exist", Toast.LENGTH_SHORT).show();
                    }else{
                        HashMap<String, String> saved = new HashMap<>();
                        String housePrice = String.valueOf(sb_house_price.getProgress());
                        String unitPrice = String.valueOf(sb_unit_price.getProgress());
                        String traffic = String.valueOf(sb_traffic.getProgress());
                        String houseRent = String.valueOf(sb_house_rent.getProgress());
                        String unitRent = String.valueOf(sb_unit_rent.getProgress());
                        String income = String.valueOf(sb_income.getProgress());
                        String education = String.valueOf(sb_education.getProgress());
                        String immigrant = String.valueOf(sb_immigrants.getProgress());
                        String religion = religionSelected();
                        saved.put("housePrice", housePrice);
                        saved.put("unitPrice", unitPrice);
                        saved.put("traffic", traffic);
                        saved.put("houseRent", houseRent);
                        saved.put("unitRent", unitRent);
                        saved.put("income", income);
                        saved.put("education", education);
                        saved.put("immigrant", immigrant);
                        saved.put("religion", religion);
                        mListener.onButtomClicked(saved, "save");
                    }
                }else{
                    Toast.makeText(getContext(), "Please set the filter first", Toast.LENGTH_SHORT).show();
                }

            }
        });



        return v;
    }

    public interface BottomSheetListener{
        void onButtomClicked(HashMap<String, String> result, String command);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener.");
        }

    }

    private String religionSelected(){
        String religion;

        ColorDrawable btn_christian_color = (ColorDrawable) btn_christian.getBackground();
        ColorDrawable btn_buddhism_color = (ColorDrawable) btn_buddhism.getBackground();
        ColorDrawable btn_hinduism_color = (ColorDrawable) btn_hinduism.getBackground();
        ColorDrawable btn_judaism_color = (ColorDrawable) btn_judaism.getBackground();
        ColorDrawable btn_islam_color = (ColorDrawable) btn_islam.getBackground();
        ColorDrawable btn_others_color = (ColorDrawable) btn_others.getBackground();

        if(btn_christian_color.getColor() == getResources().getColor(R.color.selected)){
            religion = "christian";
        }else if(btn_buddhism_color.getColor() == getResources().getColor(R.color.selected)){
            religion = "buddhism";
        }else if(btn_hinduism_color.getColor() == getResources().getColor(R.color.selected)){
            religion = "hinduism";
        }else if(btn_judaism_color.getColor() == getResources().getColor(R.color.selected)){
            religion = "judasim";
        }else if(btn_islam_color.getColor() == getResources().getColor(R.color.selected)){
            religion = "islam";
        }else if(btn_others_color.getColor() == getResources().getColor(R.color.selected)){
            religion = "others";
        }else{
            religion = null;
        }


        return religion;
    }

    public boolean isExistInMenu(){
        HashMap<String, String> temp;
        if(savedOptions == null){
            return false;
        }
        for(int i=0; i<savedOptions.size(); i++){
            temp = savedOptions.get(i);
            if(isSameSetting(temp)){
                return true;
            }
        }
        return false;
    }

    public boolean isSameSetting(HashMap<String, String> h1){
        if(!h1.get("housePrice").equals(String.valueOf(sb_house_price.getProgress()))){
            return false;
        }else if(!h1.get("unitPrice").equals(String.valueOf(sb_unit_price.getProgress()))){
            return false;
        }else if(!h1.get("traffic").equals(String.valueOf(sb_traffic.getProgress()))){
            return false;
        }else if(!h1.get("houseRent").equals(String.valueOf(sb_house_rent.getProgress()))){
            return false;
        }else if(!h1.get("unitRent").equals(String.valueOf(sb_unit_rent.getProgress()))){
            return false;
        }else if(!h1.get("income").equals(String.valueOf(sb_income.getProgress()))){
            return false;
        }else if(!h1.get("education").equals(String.valueOf(sb_education.getProgress()))){
            return false;
        }else if(!h1.get("immigrant").equals(String.valueOf(sb_immigrants.getProgress()))){
            return false;
        }else if(h1.get("religion") != null && religionSelected() == null){
            return false;
        }else if(h1.get("religion") == null && religionSelected() != null){
            return false;
        }else if(h1.get("religion") != null && religionSelected() != null){
            if(!h1.get("religion").equals(religionSelected())){
                return false;
            }
        }
        return true;
    }

    private void setFilter(){
        if(!selectedItem.get("housePrice").equals("0")){
            //SeekBar sb_house_price = findViewById(R.id.seekBar_house_price);
            sb_house_price.setProgress(Integer.parseInt(selectedItem.get("housePrice")));
        }
        if(!selectedItem.get("unitPrice").equals("0")){
            //SeekBar sb_unit_price = findViewById(R.id.seekBar_unit_price);
            sb_unit_price.setProgress(Integer.parseInt(selectedItem.get("unitPrice")));
        }
        if(!selectedItem.get("traffic").equals("0")){
            //SeekBar sb_traffic = findViewById(R.id.seekBar_traffic);
            sb_traffic.setProgress(Integer.parseInt(selectedItem.get("traffic")));
        }
        if(!selectedItem.get("houseRent").equals("0")){
            //SeekBar sb_house_rent = findViewById(R.id.seekBar_house_rent);
            sb_house_rent.setProgress(Integer.parseInt(selectedItem.get("houseRent")));
        }
        if(!selectedItem.get("unitRent").equals("0")){
            //SeekBar sb_unit_rent = findViewById(R.id.seekBar_unit_rent);
            sb_unit_rent.setProgress(Integer.parseInt(selectedItem.get("unitRent")));
        }
        if(!selectedItem.get("income").equals("0")){
           // SeekBar sb_income = findViewById(R.id.seekBar_income);
            sb_income.setProgress(Integer.parseInt(selectedItem.get("income")));
        }
        if(!selectedItem.get("education").equals("0")){
            //SeekBar sb_education = findViewById(R.id.seekBar_education);
            sb_education.setProgress(Integer.parseInt(selectedItem.get("education")));
        }
        if(!selectedItem.get("immigrant").equals("0")){
            //SeekBar sb_immigrants = findViewById(R.id.seekBar_immigrant);
            sb_immigrants.setProgress(Integer.parseInt(selectedItem.get("immigrant")));
        }
        if(selectedItem.get("religion") != null){
            switch (selectedItem.get("religion")){
                case "christian":
                   // Button btn_christian = findViewById(R.id.choose_christrian);
                    btn_christian.setBackgroundResource(R.color.selected);
                    break;
                case "buddhism":
                    //Button btn_buddhism = findViewById(R.id.choose_buddhism);
                    btn_buddhism.setBackgroundResource(R.color.selected);
                    break;
                case "hinduism":
                    //Button btn_hinduism = findViewById(R.id.choose_hinduism);
                    btn_hinduism.setBackgroundResource(R.color.selected);
                    break;
                case "judasim":
                   // Button btn_judaism = findViewById(R.id.choose_judaism);
                    btn_judaism.setBackgroundResource(R.color.selected);
                    break;
                case "islam":
                    //Button btn_islam = findViewById(R.id.choose_islam);
                    btn_islam.setBackgroundResource(R.color.selected);
                    break;
                case "others":
                   // Button btn_others = findViewById(R.id.choose_others);
                    btn_others.setBackgroundResource(R.color.selected);
                    break;
            }
        }
    }

    private boolean isSetFilter(){
        if(sb_house_price.getProgress() != 0){
            return true;
        }
        if(sb_unit_price.getProgress() != 0){
            return true;
        }
        if(sb_traffic.getProgress() != 0){
            return true;
        }
        if(sb_house_rent.getProgress() != 0){
            return true;
        }
        if(sb_unit_rent.getProgress() != 0){
            return true;
        }
        if(sb_income.getProgress() != 0){
            return true;
        }
        if(sb_education.getProgress() != 0){
            return true;
        }
        if(sb_immigrants.getProgress() != 0){
            return true;
        }
        if(religionSelected() != null){
            return true;
        }
        return false;

    }

}

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    private Button btn_confirm;
    private Button btn_cancel;

    private SeekBar sb_building_price;
    private SeekBar sb_traffic;
    private SeekBar sb_rent;
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

        sb_building_price = v.findViewById(R.id.seekBar_price);
        sb_traffic = v.findViewById(R.id.seekBar_traffic);
        sb_rent = v.findViewById(R.id.seekBar_rent);
        sb_income = v.findViewById(R.id.seekBar_income);
        sb_education = v.findViewById(R.id.seekBar_education);
        sb_immigrants = v.findViewById(R.id.seekBar_immigrant);


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
                HashMap<String, String> result = new HashMap<>();
                String buildingPrice = String.valueOf(sb_building_price.getProgress());
                String traffic = String.valueOf(sb_traffic.getProgress());
                String rent = String.valueOf(sb_rent.getProgress());
                String income = String.valueOf(sb_income.getProgress());
                String education = String.valueOf(sb_education.getProgress());
                String immigrant = String.valueOf(sb_immigrants.getProgress());
                String religion = religionSelected();
                result.put("price", buildingPrice);
                result.put("traffic", traffic);
                result.put("rent", rent);
                result.put("income", income);
                result.put("education", education);
                result.put("immigrant", immigrant);
                result.put("religion", religion);
                mListener.onButtomClicked(result);
                dismiss();
            }
        });

        btn_cancel = v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dismiss();
            }
        });



        return v;
    }

    public interface BottomSheetListener{
        void onButtomClicked(HashMap<String, String> result);
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



}

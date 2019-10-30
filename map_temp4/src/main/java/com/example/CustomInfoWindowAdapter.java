package com.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
    }

    private void redoWindowText(Marker marker, View view){
        TextView infoTitle = view.findViewById(R.id.info_window_tite);
        TextView infoContent = view.findViewById(R.id.info_window_content);
        String title = marker.getTitle();
        String content = marker.getSnippet();
        infoTitle.setText(title);
        infoContent.setText(content);


    }

    @Override
    public View getInfoWindow(Marker marker) {
        redoWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        redoWindowText(marker, mWindow);
        return mWindow;
    }
}

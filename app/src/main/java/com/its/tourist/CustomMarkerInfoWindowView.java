package com.its.tourist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    CustomMarkerInfoWindowView(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        @SuppressLint("InflateParams") View markerItemView = context.getLayoutInflater().inflate(R.layout.layout_markers, null);
        TextView titoloCard = markerItemView.findViewById(R.id.titoloCard);
        TextView infoCard = markerItemView.findViewById(R.id.infoCard);
        ImageView imageCard = markerItemView.findViewById(R.id.imageCard);
        Bitmap bitmap = (Bitmap) marker.getTag();

        titoloCard.setText(marker.getTitle());
        infoCard.setText(marker.getSnippet());

        if(bitmap != null) {
            imageCard.setImageBitmap(bitmap);
        } else {
            imageCard.setVisibility(View.GONE);
        }

        return markerItemView;
    }
}

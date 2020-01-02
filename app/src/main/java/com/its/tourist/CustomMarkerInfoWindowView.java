package com.its.tourist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


/**
 *  CustomMarkerInfoWindowView
 *  Classe utilizzata per modificare all'interno della mappa le finestre che appaiono quando viene premuto un Marker
 */
public class CustomMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {

    private View markerItemView;


    /**
     *  Metodo costruttore
     *  Viene richiesta un'Activity, un contesto, alla quale collegarsi per modificare il suo interno
     *  @param context L'Activity collegata
     */
    @SuppressLint("InflateParams")
    CustomMarkerInfoWindowView(Activity context) {
        markerItemView = context.getLayoutInflater().inflate(R.layout.layout_markers, null);
    }


    /**
     *  Metodo per la gestione delle finestre di ogni marker
     *  Prende in considerazione ogni singolo marker e lo scandisce, ricavandone le informazioni,
     *  per poi inserirle all'interno di una View modificata con uno specifico layout
     *  @param marker Il marker preso in carico
     *  @return View
     */
    @Override
    public View getInfoWindow(Marker marker) {
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


    /**
     *  Metodo per la gestione dei contenuti della finestra del marker
     *  Prende in considerazione ogni singolo marker e lo scandisce, ricavandone le informazioni,
     *  per poi inserirle all'interno della finestra di default.
     *  Se ritorna null, passa di default a getInfoWindow.
     *  @param marker Il marker preso in carico
     *  @return View
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

package com.its.tourist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.libraries.places.api.model.Place.Type.POINT_OF_INTEREST;
import static com.google.android.libraries.places.api.model.Place.Type.MUSEUM;
import static com.google.android.libraries.places.api.model.Place.Type.MOVIE_THEATER;
import static com.google.android.libraries.places.api.model.Place.Type.RESTAURANT;
import static com.google.android.libraries.places.api.model.Place.Field.ID;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ToolbarArcBackground mToolbarArcBackground;
    private AppBarLayout mAppBarLayout;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private Location mLastLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private GlobalVariable global;
    private List<LatLng> polyline;
    private List<Place.Field> placeFetchFields;
    private FindCurrentPlaceRequest findPlaceRequest;

    private final float DEFAULT_ZOOM = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        global = GlobalVariable.getInstance();
        global.setBackPeople(true);
        global.setHandlerPeople(true);

        mToolbarArcBackground = findViewById(R.id.toolbarArcBackground);
        mAppBarLayout = findViewById(R.id.appbar);

        Places.initialize(this, getResources().getString(R.string.google_maps_key));

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        placesClient = Places.createClient(this);

        placeFetchFields = Arrays.asList(ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.PHOTO_METADATAS,
                Place.Field.PRICE_LEVEL, Place.Field.OPENING_HOURS, Place.Field.TYPES);

        visualizzaMappa();
        treeObserve();
        toolbar();
        getWindow().getDecorView().post(() -> mToolbarArcBackground.startAnimate());
        getCurrentWeather();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(mapStyle());
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMinZoomPreference(11);
        mMap.setInfoWindowAdapter(new CustomMarkerInfoWindowView(MapActivity.this));
        mMap.setPadding(0, mToolbarArcBackground.getHeight(), 0, findViewById(R.id.buttonContainer).getHeight());
        findPlaceRequest = FindCurrentPlaceRequest.newInstance(Collections.singletonList(ID));

        circoscrizioneTorino();

        setPositionBtnGeo();
        checkGPS();
        places(findPlaceRequest, POINT_OF_INTEREST);

        filtriMarker();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        makeAlertDialog("Chiudi","Sei sicuro di voler uscire?",true);
    }


    /**
     * Metodo per visualizzare la mappa
     * Viene visualizzata la mappa all'interno di un fragment
     */
    private void visualizzaMappa () {
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        assert fm != null;
        fm.getMapAsync(this);
        mapView = fm.getView();
    }


    /**
     * Metodo per la gestione dei stili della mappa
     * Viene letto un json con i stili della mappa, lo stile della mappa cambierà in base all'ora del telefono
     * Se l'ora è inferiore alle 6 del mattino o 18 del pomeriggio allora la mappa sarà in uno stile più notturno
     * Altrimenti avrà uno stile giornaliero
     * @return MapStyleOptions Lo stile della mappa
     */
    private MapStyleOptions mapStyle() {
        MapStyleOptions style;
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour < 6 || hour > 18){
            style = MapStyleOptions.loadRawResourceStyle(
                    this,R.raw.map_style_night);
        } else {
            style = MapStyleOptions.loadRawResourceStyle(
                    this,R.raw.map_style_day);
        }
        return style;
    }


    /**
     * Metodo per la lettura delle coordinate torinesi
     * Vengono lette le coordinate da un file txt chiamato turinCoordinates.txt che si trova all'interno della cartella assets
     * @return String contiene le coordinate
     */
    public String metodoLetturaCoordinate () {
        try {
            InputStream is = getAssets().open("turinCoordinates.txt");
            int size = is.available();
            byte[] buffer = new byte[size];

            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Metodo per la circoscrizione di Torino
     * Vengono splittate le coordinate prese dal metodo sovrastante
     * Successivamente viene disegnato il confine torinese tramite l'uso di PolyLine
     */
    private void circoscrizioneTorino () {
        String[] coordinate = metodoLetturaCoordinate().split(";");
        polyline = new ArrayList<>();
        for (String s : coordinate) {
            String[] LatLng = s.split(",");
            polyline.add(new LatLng(Double.parseDouble(LatLng[1]), Double.parseDouble(LatLng[0])));
        }
        PolylineOptions rectOptions = new PolylineOptions().addAll(polyline);
        rectOptions.color(Color.parseColor("#1c88e2"));
        rectOptions.width(8);
        mMap.addPolyline(rectOptions);
    }


    /**
     * Metodo che serve per posizionare il pulsante della geolocalizzazione in basso a destro
     */
    private void setPositionBtnGeo() {
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }
    }


    /**
     * Metodo per verificare se il gps è attivo
     * Se è confermato avvia la procedura per la geolocalizzazione dell'utente
     * */
    private void checkGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> getDeviceLocation());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(MapActivity.this, 51);
                } catch (IntentSender.SendIntentException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    /**
     * Metodo per trovare la posizione del device
     */
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        providerDeviceLocation(task);
                    } else {
                        Toast.makeText(MapActivity.this,
                                "Non è possibile trovare l'ultima posizione nota",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Metodo per verificare se il device è nell'area di Torino oppure no
     * Se il device non si trova nell'area di Torino viene visualizzato un alert
     * Ma si potrà comunque utilizzare l'app
     * @param task per verificare se il task va a buon fine oppure no
     */
    private void providerDeviceLocation(Task<Location> task) {
        mLastLocation = task.getResult();
        if (mLastLocation != null) {
            LatLng mLastLocationLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocationLatLng, DEFAULT_ZOOM));
            if(!PolyUtil.containsLocation(mLastLocationLatLng, polyline, true)){
                makeAlertDialog(
                        "Attenzione",
                        "Attualmente non ti trovi nella città di Torino.\nVerranno comunque visulizzati i luoghi di interesse intorno a te \uD83D\uDE09",
                        false
                );
            }
        } else {
            final LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult == null) {
                        return;
                    }
                    mLastLocation = locationResult.getLastLocation();
                    LatLng mLastLocationLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocationLatLng, DEFAULT_ZOOM));
                    if(!PolyUtil.containsLocation(mLastLocationLatLng, polyline, true)){
                        makeAlertDialog(
                                "Attenzione",
                                "Attualmente non ti trovi nella città di Torino.\nVerranno comunque visulizzati i luoghi di interesse intorno a te! \uD83D\uDE09",
                                false
                        );
                    }
                    mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                }
            };
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }


    /**
     * Metodo per trovare tutti i luoghi che si trovano attorno al device
     * @param placeType Il tipo al quale siamo interessati eseguire la richiesta al server (Tipi come musei/cinema/ristoranti ecc)
     * @param request Una richiesta effettuata per potever visualizzare i luoghi che si trovano attorno al device
     * */
    private void places(FindCurrentPlaceRequest request, Place.Type placeType) {
        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindCurrentPlaceResponse response = task.getResult();
                assert response != null;
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    FetchPlaceRequest requestFetch = FetchPlaceRequest
                            .newInstance(Objects.requireNonNull(placeLikelihood.getPlace().getId()), placeFetchFields);
                    fetchPlace(requestFetch, placeType);
                }
            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    Log.e("Find not found", "Place not found: " + exception.getMessage());
                }
            }
        });

    }


    /**
     * Metodo per prendere i dati del singolo posto e stampare i marker
     * Vengono verificate tutte le informazioni dei primi 3 fragment e in base a questi dati
     * si creano nuovi marker e vengono stampati all'interno della mappa
     * @param request Una richiesta che io faccio per poter avere tutti i dati del singolo places
     * @param placeType Il tipo al quale siamo interessati eseguire la richiesta al server (Tipi come musei/cinema/ristoranti ecc)
     */
    private void fetchPlace(FetchPlaceRequest request, Place.Type placeType) {
        placesClient.fetchPlace(request).addOnSuccessListener((responseFetch) -> {
            Place place = responseFetch.getPlace();
            MarkerOptions markerOptions = new MarkerOptions();
            List<Place.Type> types = place.getTypes();
            assert types!= null;

            //Dato che tourist_attraction non c'è questo if serve per mettere le cose che secondo me sono per "turisti"
            if(types.contains(placeType)){
                markerOptions.title(place.getName());
                markerOptions.position(Objects.requireNonNull(place.getLatLng()));

                if(place.getOpeningHours() != null) {
                    List<Period> periods = Objects.requireNonNull(place.getOpeningHours()).getPeriods();

                    for(Period p : periods) {

                        // verifico che il posto sia aperto nel giorno inserito dall'utente
                        if (((p.getOpen().getDay()).toString()).equals(gestioneDatiCalendario())) {

                            // separo la stringa dell'orario inserito dall'utente
                            String[] userTimeStart = (global.getTimeStart()).split(":");
                            String hourStart = userTimeStart[0];
                            String minuteStart = userTimeStart[1];

                            String[] userTimeEnd = (global.getTimeEnd()).split(":");
                            String hourEnd = userTimeEnd[0];
                            String minuteEnd = userTimeEnd[1];

                            // converto le ore da String a int per fare il confronto
                            // ORE
                            int hourStartInt = Integer.parseInt(hourStart);
                            int hourEndInt = Integer.parseInt(hourEnd);

                            // controllo se il locale è aperto nell'itervallo di ORE inserito dall'utente
                            if(((p.getOpen().getTime().getHours()) >= hourStartInt) && (hourEndInt < (p.getClose().getTime().getHours())))
                            {
                                Log.i("locale", "aperto" );
                                markerOptions.snippet("aperto");

                                // se il locale è CHIUSO non mostro il marker
                            } else {
                                Log.i("locale", "chiuso");
                                markerOptions.visible(false);
                            }
                        }
                    }

                    // Se OpeningHours == null
                } else {
                    if (place.getPriceLevel() == null) {
                        markerOptions.snippet("Indirizzo: " + place.getAddress() + "\nI prezzi possono variare" + "\nGli orari possono variare");
                    } else if (place.getPriceLevel() <= gestioneDatiPrezzo()) {
                        if (place.getRating() != null) {
                            markerOptions.snippet("Indirizzo: " + place.getAddress() + "\nRating: " + place.getRating() + "\nGli orari possono variare");
                        } else {
                            markerOptions.snippet("Indirizzo: " + place.getAddress() + "\nGli orari possono variare");
                        }
                    }
                }

                if (place.getPriceLevel() == null || place.getPriceLevel() <= gestioneDatiPrezzo()) {
                    if (place.getPhotoMetadatas() != null) {
                        PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
                        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) ->
                                mMap.addMarker(markerOptions).setTag(fetchPhotoResponse.getBitmap())
                        ).addOnFailureListener((exception) -> {
                            if (exception instanceof ApiException) {
                                Log.e("PlaceNotFoundPhoto", "Place not found: " + exception.getMessage());
                            }
                        });
                    } else {
                        mMap.addMarker(markerOptions).setTag(null);
                    }
                }
            }

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e("Fetch not found", "Place not found: " + exception.getMessage());
            }
        });
    }


    /**
     * Metodo che filtra i marker in base al tipo
     * Abbiamo 3 pulsanti per la stampa dei marker in base al tipo: Musei, Cinema, Ristoranti
     */
    private void filtriMarker () {
        Button btnMusei = findViewById(R.id.btnMusei);
        Button btnCinema = findViewById(R.id.btnCinema);
        Button btnRisto = findViewById(R.id.btnRistoranti);

        btnMusei.setOnClickListener(view -> {
            mMap.clear();
            circoscrizioneTorino();
            Toast.makeText(MapActivity.this,
                    "Caricamento dei Musei attorno a te",
                    Toast.LENGTH_SHORT).show();
            findPlaceRequest = FindCurrentPlaceRequest.newInstance(Collections.singletonList(ID));
            places(findPlaceRequest, MUSEUM);
        });

        btnCinema.setOnClickListener(view -> {
            mMap.clear();
            circoscrizioneTorino();
            Toast.makeText(MapActivity.this,
                    "Caricamento dei Cinema attorno a te",
                    Toast.LENGTH_SHORT).show();
            findPlaceRequest = FindCurrentPlaceRequest.newInstance(Collections.singletonList(ID));
            places(findPlaceRequest, MOVIE_THEATER);
        });

        btnRisto.setOnClickListener(view -> {
            mMap.clear();
            circoscrizioneTorino();
            Toast.makeText(MapActivity.this,
                    "Caricamento dei Ristoranti attorno a te",
                    Toast.LENGTH_SHORT).show();
            findPlaceRequest = FindCurrentPlaceRequest.newInstance(Collections.singletonList(ID));
            places(findPlaceRequest, RESTAURANT);
        });

    }


    /**
     * Metodo per settare l'altezza della toolbar
     */
    private void treeObserve () {
        ViewTreeObserver vto = mAppBarLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAppBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = mAppBarLayout.getMeasuredHeight();
                mToolbarArcBackground.setHeight(height);
            }
        });
    }


    /**
     * Metodo per settare la toolbar dell'app
     */
    private void toolbar () {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                float scale = (float) Math.abs(verticalOffset) / scrollRange;
                mToolbarArcBackground.setScale(1 - scale);

            }
        });
    }


    /**
     * Metodo per la gestione del Meteo
     * Tramite l'OpenWeatherMap prendiamo i dati meteo di Torino e vengono stampati nella toolbar
     */
    private void getCurrentWeather() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData("45.070935",
                "7.685048", "c96e8bd7dcf26eab873b1b5417951ba7");
        TextView txtMeteo = findViewById(R.id.txtMeteo);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {

                if (response.code() == 200) {
                    assert response.body() != null;
                    WeatherResponse weatherResponse = response.body();
                    @SuppressLint("DefaultLocale") String stringBuilder =
                            String.format("%.0f", kelvinToCelsius(weatherResponse.main.temp)) + "°";
                    txtMeteo.setText(stringBuilder);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                txtMeteo.setText("E°");
            }

        });

    }


    /**
     * Metodo per la conversione da Kelvin a Celsius
     * @return double il risultato della conversione
     */
    private double kelvinToCelsius (double grades){
        return grades - 273.15;
    }


    /**
     * Metodo per la gestione del prezzo/persone (i primi 2 fragment)
     * Vengono confrontati i dati inseriti dall'utente con i dati che noi abbiamo scelto
     * @return int 0,1,2,3 o 4 che rappresentano 0="gratis", 1="economico", 2="medio", 3="medio-alto", 4="alto"
     */
    private int gestioneDatiPrezzo () {
        int priceS = global.getBudgetStart();
        int priceE = global.getBudgetEnd();
        String personT = global.getTypePerson();

        if (priceE == 0) {
            return 0;
        } if ((priceS >= 0 && priceE <= 20) && personT.equals("singolo")) {
            return 1;
        } else if ((priceS >= 0 && priceE <= 50) && personT.equals("singolo")) {
            return 2;
        } else if ((priceS >= 0 && priceE <= 100) && personT.equals("singolo")) {
            return 3;
        } else if ((priceS >= 0 && priceE <= 200) && personT.equals("singolo")) {
            return 4;
        } else if ((priceS >= 0 && priceE <= 30) && personT.equals("coppia")) {
            return 1;
        } else if ((priceS >= 0 && priceE <= 70) && personT.equals("coppia")) {
            return 2;
        } else if ((priceS >= 0 && priceE <= 120) && personT.equals("coppia")) {
            return 3;
        } else if ((priceS >= 0 && priceE <= 200) && personT.equals("coppia")) {
            return 4;
        } else if ((priceS >= 0 && priceE <= 50) && personT.equals("gruppo")) {
            return 1;
        } else if ((priceS >= 0 && priceE <= 100) && personT.equals("gruppo")) {
            return 2;
        } else if ((priceS >= 0 && priceE <= 150) && personT.equals("gruppo")) {
            return 3;
        } else if ((priceS >= 0 && priceE <= 200) && personT.equals("gruppo")) {
            return 4;
        }

        return 0;
    }


    /**
     * Metodo per la gestione del calendario (3 fragment)
     * Viene prelevato il giorno della settimana in base alla data scelta dall'utente
     * @return String il giorno della settimana
     */
    public String gestioneDatiCalendario() {
        int timeD = global.getCalendarDay();

        if(timeD == 1) {
            return "SUNDAY";
        } else if(timeD == 2) {
            return "MONDAY";
        } else if(timeD == 3) {
            return "TUESDAY";
        } else if (timeD == 4) {
            return "WEDNESDAY";
        } else if(timeD == 5) {
            return  "THURSDAY";
        } else if(timeD == 6) {
            return "FRIDAY";
        } else if(timeD == 7) {
            return "SATURDAY";
        }

        return "";
    }


    /**
     * Metodo per la gestione degli alert dialog
     * @param title il titolo dell'alert
     * @param text il testo dell'alert
     * @param exit se l'allert è di uscita dall'applicazione o generico
     */
    private void makeAlertDialog(String title, String text, boolean exit) {
        if (exit) {
            new AlertDialog.Builder(this).setTitle(title).setMessage(text)
                    .setPositiveButton("ESCI", (dialogInterface, i) -> {
                        Intent intent = new Intent(MapActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit", true);
                        startActivity(intent);
                        finish();
                    }).setNegativeButton("ANNULLA", null)
                    .show();
        } else {
            new AlertDialog.Builder(this).setTitle(title).setMessage(text)
                    .setPositiveButton("OK", null).show();
        }

    }

}

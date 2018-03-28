package fr.wildcodeschool.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMS_CALL_ID=1234;
    private LocationManager lm;

    //Pour recup la carte utiliser ses 2 attributs
    //1er name ds layout=instance
    private MapFragment mapFragment;
    //2eme: objet a linterieur pour pouvoir modif les données
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recuperer mapFragment avec FragmentManager
        android.app.FragmentManager fragmentManager =  getFragmentManager();
        //lui indiquer ou se positionner
        mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);

        //recuperation de la carto = av methode loadMap

    }

    //Pour aquerir le fournisseur et sy abonner
    // A linterieur apl de la methode pour demander Permisions
    @Override
    protected void onResume() {
        super.onResume();

        //on lui demande la permission au cours de lutilisation si jamais il les deactive ses autorisations
        checkPermissions();

    }

    private void checkPermissions () {
        //Demander de recuperer le service proposé par android
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        //demande permission lorsque utilisateur active desactive les donnes == popup au demarrage
        //on lui demande les permissions au demarrage (pour demander au cours de utilisation on l'invoque dans onResume)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },PERMS_CALL_ID);
            return;
        }

        //sur ce locationManager (lm) si il existe un gps ou passive provider ou network de données on s'y abonne
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // ()= type de fournisseur+frequence pour retrouver localisation+ecouteur
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        }
        if(lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            // ()= type de fournisseur+frequence pour retrouver localisation+ecouteur
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);

        }
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            // ()= type de fournisseur+frequence pour retrouver localisation+ecouteur
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);

        }
        // on charge la carte
        loadMap();
    }

    //redemander de lactiver jusqu'a avoir les droits
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == PERMS_CALL_ID){
            checkPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //si jamais le locationManager a été initialisé alors retire lecouteur
        if (lm != null){
            lm.removeUpdates( this);
        }
    }

    // on vient deja de demander les droits dc pas besoin de recommencer
  @SuppressWarnings("MissingPermission")
    // mettre la carto sur fragment
    private void loadMap () {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                MainActivity.this.googleMap= googleMap;

                checkPermissions();
                // pouvoir se positionner sur la carte
                googleMap.setMyLocationEnabled(true);

                //taille marker
                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.sorcier);
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

               //googleMap.moveCamera(CameraUpdateFactory.zoomBy(30 ));

               // Rajout de marker
                googleMap.addMarker(new MarkerOptions().position(new LatLng(43.7, 1.4167))
                .title("Claire").snippet("maison").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(43.6014536,1.4421452000000272)).title("WCS").snippet("formation").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

            }
        });
    }

    //rajout 4 methodes de LocationListener, sinon erreur
    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    //ici une localisation a été capuré si invoqué
    @Override
    public void onLocationChanged(Location location) {
        //on stocke les valeurs souhaités dans des variable
        double longitude=location.getLongitude();
        double latitude=location.getLatitude();


        // on peut demander a afficher un toast par ex
        Toast.makeText(this, "Vous êtes "+longitude+" "+latitude, Toast.LENGTH_LONG).show();
        //attendre que la carte est chargé pour faire action
        if (googleMap != null){
          //recup ses données
           LatLng googleLocation = new LatLng( latitude, longitude);
           //repositionne la carto
           // googleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
        }
    }
}

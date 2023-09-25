package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.Manifest;

public class UploadActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    ImageView uploadImage;
    Button saveButton;
    EditText uploadTopic, uploadDesc, uploadLang;
    String imageURL;
    Uri uri;
    private SwitchMaterial getLocation;
    private LocationManager locationManager;
    private String CarLongitude, CarLatitude, address;
    TextView carLocation;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().setTitle("Add Recipe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uploadImage = findViewById(R.id.uploadImage);
        uploadDesc = findViewById(R.id.uploadDesc);
        uploadTopic = findViewById(R.id.uploadTopic);
        uploadLang = findViewById(R.id.uploadLang);
        saveButton = findViewById(R.id.saveButton);
        getLocation = findViewById(R.id.GetLocation);
        carLocation = findViewById(R.id.CarLocation);

        getLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    CheckedLocationPermission();
                } else {
                    if (locationManager != null) {
                        locationManager.removeUpdates(locationListener);
                    }
                    carLocation.setText("");
                }
            }
        });


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    private void CheckedLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Ovde ažurirajte TextView sa novom lokacijom
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    CarLatitude = String.valueOf(latitude);
                    CarLongitude = String.valueOf(longitude);

                    String Location = CarLatitude + CarLongitude;
                    carLocation.setText(Location);

                    getAddressFromLatLong(UploadActivity.this, latitude, longitude);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            if (ActivityCompat.checkSelfPermission(
                    UploadActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            UploadActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                // Zatražite ažuriranje lokacije
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }


    private void getCurrentLocation() {
        if (getLocation.isChecked()) { // Provera da li je prekidač uključen
            if (ActivityCompat.checkSelfPermission(
                    UploadActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    UploadActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                Location GpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (GpsLocation != null) {
                    double latitude = GpsLocation.getLatitude();
                    double longitude = GpsLocation.getLongitude();

                    CarLatitude = String.valueOf(latitude);
                    CarLongitude = String.valueOf(longitude);

                    String Location = CarLatitude + CarLongitude;
                    carLocation.setText(Location);

                    getAddressFromLatLong(UploadActivity.this, latitude, longitude);
                }
            }
        } else {
            // Ako je prekidač isključen, obrišite prikazanu lokaciju
            carLocation.setText("");
        }
    }


    public void getAddressFromLatLong(Context context, double LATITUDE, double LONGITUDE){
        try{
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            List<Address> addresses = geocoder.getFromLocation(LATITUDE,LONGITUDE,1);
            if(addresses != null && addresses.size()>0){
                address = addresses.get(0).getAddressLine(0);
            }
            carLocation.setText(address);
            carLocation.setSelected(true);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setMessage("Enable GPS").setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveData(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData() {
        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String lang = uploadLang.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid(); // Dobijanje ID-a trenutnog korisnika
            String currentUserName = currentUser.getDisplayName(); // Dobijanje imena trenutnog korisnika

            // Kreirajte objekat DataClass sa svim podacima
            DataClass dataClass = new DataClass(title, desc, lang, imageURL, CarLongitude, CarLatitude);

            // Postavite ID korisnika za ovaj recept
            dataClass.setUserId(currentUserId);

            // Postavite ime korisnika za ovaj recept
            dataClass.setUserName(currentUserName);

            // Koristite trenutni datum i vreme kao ključ za recept
            String currentDateTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

            // Sačuvajte podatke recepta u bazi podataka
            DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("Android Tutorials");
            recipesRef.child(currentDateTime).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        addPointToUser(currentUserId);
                        
                        Toast.makeText(UploadActivity.this, "Recipe saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, "Error saving recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }




//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String currentUserId = currentUser.getUid();
//            // Kreirajte objekat DataClass sa svim podacima
//            DataClass dataClass = new DataClass(title, desc, lang, imageURL, CarLongitude, CarLatitude);
//            // Postavite UID korisnika za ovaj recept
//            dataClass.setUserId(currentUserId);
//
//            // Koristite trenutni datum i vreme kao ključ za recept
//            String currentDateTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//
//            // Sačuvajte podatke recepta u bazi podataka
//            DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("Android Tutorials");
//            recipesRef.child(currentDateTime).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(UploadActivity.this, "Recipe saved", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(UploadActivity.this, "Error saving recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }

    private void addPointToUser(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");

        // Povećajte broj poena korisnika za 1
        usersRef.child(userId).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long currentPoints = dataSnapshot.getValue(Long.class);
                    currentPoints++; // Povećaj broj poena za 1
                    usersRef.child(userId).child("points").setValue(currentPoints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obrada grešaka
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


//    public void uploadData() {
//        String title = uploadTopic.getText().toString();
//        String desc = uploadDesc.getText().toString();
//        String lang = uploadLang.getText().toString();
//
//        // Kreirajte objekat dataClass sa svim podacima
//        DataClass dataClass = new DataClass(title, desc, lang, imageURL, CarLongitude, CarLatitude);
//
//        // Postavite i lokaciju u objektu dataClass
//        dataClass.setLongi(CarLongitude);
//        dataClass.setLati(CarLatitude);
//
//        // We are changing the child from title to currentDate,
//        // because we will be updating title as well and it may affect child value.
//        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//        FirebaseDatabase.getInstance().getReference("Android Tutorials").child(currentDate)
//                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(UploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

}
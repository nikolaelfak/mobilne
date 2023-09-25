package com.example.app1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailLang, ratingScale, authorName;
    ImageView detailImage;
    String key = "";
    String imageUrl = "";
    RatingBar ratingBar;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FloatingActionButton deleteButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setTitle("Recipe Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailLang = findViewById(R.id.detailLang);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);

        ratingBar = findViewById(R.id.ratingBar);
        ratingScale = findViewById(R.id.textView);
        authorName = findViewById(R.id.authorName);

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials");

        firebaseAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));

            key = bundle.getString("Key");
            loadAuthorName(key);

            // Dohvatimo ID korisnika koji je postavio recept
            String authorId = bundle.getString("userId");

            // Ako imamo ID korisnika, dohvatimo njegovo ime i prikažemo ga
            if (authorId != null) {
                loadAuthorName(authorId);
            }

            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);

            // Prikaži ocenu ako postoji
            showRating();

            // Dobijamo referencu na recept u bazi podataka prema ključu
            DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Android Tutorials").child(key);

// Dohvatamo vrednost dataLang iz baze podataka
            recipeRef.child("dataLang").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String dataLang = dataSnapshot.getValue(String.class);

                        // Postavljamo vrednost dataLang u TextView
                        if (dataLang != null) {
                            detailLang.setText("Lokacija: " + dataLang);
                        } else {
                            detailLang.setText("Lokacija nije dostupna");
                        }
                    } else {
                        // Ako ključ ne postoji u bazi podataka, prikažemo odgovarajuću poruku
                        detailLang.setText("Lokacija nije dostupna");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Obrada greške
                }
            });

        }

        // Postavi slušača za dugme "Submit Rating"
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Čuvajte korisničku ocenu u bazi podataka samo kada korisnik pritisne dugme "Submit Rating"
                float userRating = ratingBar.getRating();
                saveRatingToDatabase(userRating);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Android Tutorials").child(key);
            recipeRef.child("userId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String authorId = dataSnapshot.getValue(String.class);

                        if (currentUserId.equals(authorId)) {
                            // Autor recepta - prikaži dugmad za meni
                            findViewById(R.id.editButton).setVisibility(View.VISIBLE);
                            findViewById(R.id.deleteButton).setVisibility(View.VISIBLE);
                        } else {
                            // Nije autor recepta - sakrij dugmad za meni
                            findViewById(R.id.editButton).setVisibility(View.GONE);
                            findViewById(R.id.deleteButton).setVisibility(View.GONE);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Obrada greške
                }
            });
        } else {
            // Korisnik nije prijavljen - sakrij dugmad za meni
            findViewById(R.id.editButton).setVisibility(View.GONE);
            findViewById(R.id.deleteButton).setVisibility(View.GONE);
        }

        // Dodajte OnClickListener za deleteButton
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), ReceptiActivity.class));
                                finish();
                            }
                        });
                    }
                });
            }
        });

        // Dodajte OnClickListener za editButton
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("Language", detailLang.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });
    }

    private void loadAuthorName(String recipeKey) {
        DatabaseReference recipeRef = databaseReference.child(recipeKey);
        recipeRef.child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    authorName.setText("Autor: " + userName);
                } else {
                    Toast.makeText(DetailActivity.this, "Ime korisnika nije pronađeno", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obrada greške
            }
        });
    }

    private void saveRatingToDatabase(float userRating) {
        // Čuvajte korisničku ocenu u bazi podataka
        databaseReference.child(key).child("ratings").push().setValue(userRating);

        // Osvježite srednju vrednost ocene za prikaz
        updateAverageRating();
    }

    private void updateAverageRating() {
        // Izračunajte srednju vrednost ocena iz baze podataka za taj recept
        databaseReference.child(key).child("ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalRating = 0;
                int numRatings = 0;

                // Prolazak kroz sve ocene i izračunavanje ukupne ocene i broja ocena
                for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                    float rating = ratingSnapshot.getValue(Float.class);
                    totalRating += rating;
                    numRatings++;
                }

                if (numRatings > 0) {
                    float averageRating = totalRating / numRatings;

                    // Ažurirajte srednju vrednost ocene u bazi podataka
                    databaseReference.child(key).child("averageRating").setValue(averageRating);

                    // Ažurirajte prikaz na ekranu
                    ratingScale.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });
    }

    private void showRating() {
        // Prikazi trenutnu ocenu ako postoji
        databaseReference.child(key).child("averageRating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Float averageRating = snapshot.getValue(Float.class);
                    if (averageRating != null) {
                        ratingScale.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
                        ratingBar.setRating(averageRating);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, ReceptiActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

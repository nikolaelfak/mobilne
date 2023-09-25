package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RangLista extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rang_lista);
        getSupportActionBar().setTitle("Rang Lista");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Inicijalizuj RecyclerView i adapter
        userRecyclerView = findViewById(R.id.user_recyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        userRecyclerView.setAdapter(userAdapter);

        // Dohvati referencu ka "Registered Users" tabeli u Firebase bazi podataka
        usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");

        // Dohvati sve korisnike i sortiraj ih prema broju poena
        Query query = usersRef.orderByChild("points").limitToLast(10); // Dohvati top 10 korisnika
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }

                // Sortiraj korisnike prema broju poena (opadajuće)
                Collections.sort(userList, new Comparator<User>() {
                    @Override
                    public int compare(User user1, User user2) {
                        return Integer.compare(user2.getPoints(), user1.getPoints());
                    }
                });

                // Obavesti adapter o promenama u podacima
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obrada greške pri dohvatanju podataka
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
}
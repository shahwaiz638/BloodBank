package com.muhammaddaniyal.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.UserAdapter;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class GuestActivity extends AppCompatActivity {
    Button signUpButton;
    private RecyclerView rv;

    private Toolbar toolbar;
    private DatabaseReference ref;
    private ProgressBar progressBar;
    private List<User> userArray;
    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        progressBar=findViewById(R.id.progressBar);
        rv=findViewById(R.id.rv);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Guest Mode");

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rv.setLayoutManager(layoutManager);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestActivity.this, SelectRegistrationActivity.class);
                startActivity(intent);
            }
        });

        userArray=new ArrayList<User>();
        userAdapter=new UserAdapter(GuestActivity.this,userArray,true);
        rv.setAdapter(userAdapter);

//        DatabaseReference ref1= FirebaseDatabase.getInstance().getReference().child("users").child(
//                FirebaseAuth.getInstance().getCurrentUser().getUid()
//        );
//
//        ref1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child("type").getValue()!=null)
//                {
//                    if(snapshot.child("type").getValue().toString().equals("donor"))
//                    {
//                        readRecipients();
//                    }
//                    else
//                    {
//                        readDonors();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        readDonors();

    }



    public void readDonors() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users");
        Query query =ref.orderByChild("type");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArray.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    if(dataSnapshot.exists())
                    {
                        //String url=snapshot.getValue(User.class).getProfilePictureUrl();
                        User user=dataSnapshot.getValue(User.class);
                        userArray.add(user);
                    }

                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(userArray.isEmpty())
                {
                    Toast.makeText(GuestActivity.this, "No Recipients", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readRecipients() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users");
        Query query =ref.orderByChild("type").equalTo("recipient");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArray.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {

                    User user=dataSnapshot.getValue(User.class);
                    userArray.add(user);

                }

                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(userArray.isEmpty())
                {
                    Toast.makeText(GuestActivity.this, "No Recipients", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

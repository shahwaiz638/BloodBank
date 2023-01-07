package com.muhammaddaniyal.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class CategorySelectedActivity extends AppCompatActivity {

    private RecyclerView rv;
    private Toolbar toolbar;
    private List<User> userArray;
    private UserAdapter userAdapter;
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selected);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        rv=findViewById(R.id.rv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rv.setLayoutManager(layoutManager);

        userArray=new ArrayList<User>();
        userAdapter=new UserAdapter(CategorySelectedActivity.this,userArray);
        rv.setAdapter(userAdapter);

        if(getIntent().getExtras()!=null)
        {
            title=getIntent().getStringExtra("group");
            getSupportActionBar().setTitle("Blood Group "+title);

            if(title.equals("compatible")){
                getCompatibleUsers();
                getSupportActionBar().setTitle("Compatible with me");
            }else{
                readUsers();
            }


        }



    }

    private void getCompatibleUsers() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result=null;
                String type=snapshot.child("type").getValue().toString();
                if(type.equals("donor"))
                {
                    result="recipient";

                }
                else
                {
                    result="donor";
                }

                String bloodGroup = snapshot.child("bloodgroup").getValue().toString();
                String s=result+bloodGroup;
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users");
                Query query=ref.orderByChild("search").equalTo(s);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userArray.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                        {
                            if(dataSnapshot.exists())
                            {
                                User user=dataSnapshot.getValue(User.class);
                                userArray.add(user);
                            }
                            userAdapter.notifyDataSetChanged();

                            if(userArray.isEmpty())
                            {
                                Toast.makeText(CategorySelectedActivity.this, "No Donors/Recipients", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result=null;
                String type=snapshot.child("type").getValue().toString();
                if(type.equals("donor"))
                {
                    result="recipient";

                }
                else
                {
                    result="donor";
                }

                String s=result+title;
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users");
                Query query=ref.orderByChild("search").equalTo(s);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userArray.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren())
                        {
                            if(dataSnapshot.exists())
                            {
                                User user=dataSnapshot.getValue(User.class);
                                userArray.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                            
                            if(userArray.isEmpty())
                            {
                                Toast.makeText(CategorySelectedActivity.this, "No Donors/Recipients", Toast.LENGTH_SHORT).show();
                            }
                        }
                        
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(CategorySelectedActivity.this, MainActivity.class);
                finish();
                startActivity(intent);

                return true;

            default:
                finish();
                return super.onOptionsItemSelected(item);
        }

    }
}

package com.muhammaddaniyal.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rv;
    private NavigationView nv;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView nav_name,nav_email,nav_userType,nav_group;
    private CircleImageView nav_img;
    private DatabaseReference ref;
    private ProgressBar progressBar;
    private List<User> userArray;
    private UserAdapter userAdapter;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv=findViewById(R.id.rv);
        nv=findViewById(R.id.nav);
        drawerLayout=findViewById(R.id.drawerLayout);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Bank");
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        startService(new Intent(getApplication(),MyService.class));

        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(MainActivity.this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.syncState();

        nv.setNavigationItemSelectedListener(this);

        nav_img=nv.getHeaderView(0).findViewById(R.id.user_image_header);
        nav_name=nv.getHeaderView(0).findViewById(R.id.name);
        nav_email=nv.getHeaderView(0).findViewById(R.id.email);
        nav_group=nv.getHeaderView(0).findViewById(R.id.bloodGroup);
        nav_userType=nv.getHeaderView(0).findViewById(R.id.usertype);
        progressBar=findViewById(R.id.progressBar);
        rv=findViewById(R.id.rv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rv.setLayoutManager(layoutManager);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String name=snapshot.child("name").getValue().toString();
                    nav_name.setText(name);

                    String email=snapshot.child("email").getValue().toString();
                    nav_email.setText(email);

                    String type=snapshot.child("type").getValue().toString();
                    nav_userType.setText(type);

                    String blood=snapshot.child("bloodgroup").getValue().toString();
                    nav_group.setText(blood);

                    if(snapshot.child("profilepictureurl").getValue()!=null)
                    {
                        String img_url= snapshot.child("profilepictureurl").getValue().toString();
                        Glide.with(getApplicationContext()).load(img_url).into(nav_img);
                    }else {
                        nav_img.setImageResource(R.drawable.profile);
                    }

                    Menu nav_menu = nv.getMenu();

                    if(type.equals("donor")){
                        nav_menu.findItem(R.id.sentEmail).setTitle("Received Emails");

                        nav_menu.findItem(R.id.notifications).setVisible(true);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userArray=new ArrayList<User>();
        userAdapter=new UserAdapter(MainActivity.this,userArray);
        rv.setAdapter(userAdapter);

        DatabaseReference ref1= FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("type").getValue()!=null)
                {
                    if(snapshot.child("type").getValue().toString().equals("donor"))
                    {
                        readRecipients();
                    }
                    else
                    {
                        readDonors();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    WifiStateReceiver wifiStateReceiver = new WifiStateReceiver();

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);
    }


    public void readDonors() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users");
        Query query =ref.orderByChild("type").equalTo("donor");
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
                    Toast.makeText(MainActivity.this, "No Recipients", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MainActivity.this, "No Recipients", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.profile:
                Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.Logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent2);
                finish();
                return true;

            case R.id.a_plus:
                Intent intent3=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent3.putExtra("group","A+");
                startActivity(intent3);
                finish();
                return true;

            case R.id.a_neg:
                Intent intent4=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent4.putExtra("group","A-");
                startActivity(intent4);
                finish();
                return true;
            case R.id.b_plus:
                Intent intent5=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent5.putExtra("group","B+");
                startActivity(intent5);
                finish();
                return true;
            case R.id.b_neg:
                Intent intent6=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent6.putExtra("group","B-");
                startActivity(intent6);
                finish();
                return true;

            case R.id.ab_plus:
                Intent intent7=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent7.putExtra("group","AB+");
                startActivity(intent7);
                finish();
                return true;

            case R.id.ab_minus:
                Intent intent8=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent8.putExtra("group","AB-");
                startActivity(intent8);
                finish();
                return true;

            case R.id.o_plus:
                Intent intent9=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent9.putExtra("group","O+");
                startActivity(intent9);
                finish();
                return true;

            case R.id.o_neg:
                Intent intent10=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent10.putExtra("group","O-");
                startActivity(intent10);
                finish();
                return true;

            case R.id.compatible:
                Intent intent11=new Intent(MainActivity.this,CategorySelectedActivity.class);
                intent11.putExtra("group","compatible");
                startActivity(intent11);
                finish();
                return true;

            case R.id.notifications:
                Intent intent13=new Intent(MainActivity.this,NotificationActivity.class);
                startActivity(intent13);
                finish();
                return true;

            case R.id.sentEmail:
                Intent intent12=new Intent(MainActivity.this,SentEmailActivity.class);
                startActivity(intent12);
                finish();
                return true;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(getApplication(),MyService.class));


    }
}

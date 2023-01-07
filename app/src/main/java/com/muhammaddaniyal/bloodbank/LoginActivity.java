package com.muhammaddaniyal.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView backButton,forgotPassword;
    private EditText loginEmail,loginPassword;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loader;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        loader=new ProgressDialog(this);
        backButton = findViewById(R.id.backButton);
        loginButton=findViewById(R.id.loginButton);
        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        forgotPassword=findViewById(R.id.forgotPassword);
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);

                    startActivity(intent);
                    finish();

                }
            }
        };

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SelectRegistrationActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {






                String email=loginEmail.getText().toString().trim();
                String password=loginPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    loginEmail.setError("Email can not be Empty");
                }
                if(TextUtils.isEmpty(password))
                {
                    loginPassword.setError("Password can not be Empty");
                }
                else
                {
                    loader.setMessage("Login in Progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {
                               NotificationManagerCompat notificationManagerCompat ;
                               Notification notification;

                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                   NotificationChannel channel = new NotificationChannel("myCh", "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
                                   NotificationManager manager = getSystemService(NotificationManager.class);
                                   manager.createNotificationChannel(channel);

                               }
                               NotificationCompat.Builder builder = new NotificationCompat.Builder(LoginActivity.this, "myCh")
                                       .setSmallIcon(R.drawable.blood_bank_logo)
                                       .setContentTitle("Welcome Back!")
                                       .setContentText("Glad to have you back "+email);

                               notification = builder.build();
                               notificationManagerCompat = NotificationManagerCompat.from(LoginActivity.this);

                               notificationManagerCompat.notify(1,notification);
                               Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                               startActivity(intent);
                               finish();
                           }
                           else
                           {
                               Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                    loader.dismiss();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}

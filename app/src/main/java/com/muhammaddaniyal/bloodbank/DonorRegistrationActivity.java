package com.muhammaddaniyal.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorRegistrationActivity extends AppCompatActivity {

    private TextView backButton;
    private Spinner bloodGroupsSpinner;
    private TextInputEditText registerIdNumber,registerPassword,
            registerPhoneNumber,registerFullName,registerEmail ;
    private Button registerButton;
    private CircleImageView profile_image;
    private ProgressDialog loader;
    private Uri resultUri;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);


        backButton= findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonorRegistrationActivity.this,LoginActivity.class );
                startActivity(intent);
            }
        });


        bloodGroupsSpinner = findViewById(R.id.bloodGroupsSpinner);
        registerIdNumber = findViewById(R.id.registerIdNumber);
        registerPassword = findViewById(R.id.registerPassword);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        registerFullName = findViewById(R.id.registerFullName);
        registerEmail = findViewById(R.id.registerEmail);
        profile_image = findViewById(R.id.profile_image);
        registerButton = findViewById(R.id.registerButton);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = registerEmail.getText().toString().trim();
                final String password = registerPassword.getText().toString().trim();
                final String fullName = registerFullName.getText().toString().trim();
                final String idNumber = registerIdNumber.getText().toString().trim();
                final String phoneNumber = registerPhoneNumber.getText().toString().trim();
                final String bloodGroup = bloodGroupsSpinner.getSelectedItem().toString();




                if (TextUtils.isEmpty(fullName)){
                    registerFullName.setError("Full Name Required!");
                    return;
                }
                if (TextUtils.isEmpty(idNumber)){
                    registerIdNumber.setError("CNIC Required!");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)){
                    registerPhoneNumber.setError("Phone Number Required!");
                    return;
                }
                if (bloodGroup.equals("Select your Blood Group")){
                    Toast.makeText(DonorRegistrationActivity.this, "Blood Group Required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    registerEmail.setError("Email Required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    registerPassword.setError("Password Required!");
                    return;
                }

                else {
                    loader.setMessage("Getting you Signed Up...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(DonorRegistrationActivity.this, "Error: "+ error, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                NotificationManagerCompat notificationManagerCompat ;
                                Notification notification;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    NotificationChannel channel = new NotificationChannel("myCh", "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
                                    NotificationManager manager = getSystemService(NotificationManager.class);
                                    manager.createNotificationChannel(channel);

                                }
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(DonorRegistrationActivity.this, "myCh")
                                        .setSmallIcon(R.drawable.blood_bank_logo)
                                        .setContentTitle("Welcome Onboard!")
                                        .setContentText(fullName+" , Glad to have you in Blood Bank Family ");

                                notification = builder.build();
                                notificationManagerCompat = NotificationManagerCompat.from(DonorRegistrationActivity.this);

                                notificationManagerCompat.notify(1,notification);



                                String currentUserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(currentUserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("id",currentUserId);
                                userInfo.put("name",fullName);
                                userInfo.put("email",email);
                                userInfo.put("idnumber",idNumber);
                                userInfo.put("phonenumber",phoneNumber);
                                userInfo.put("bloodgroup",bloodGroup);
                                userInfo.put("type","donor");
                                userInfo.put("search","donor"+bloodGroup);

                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(DonorRegistrationActivity.this, "Data set Successful", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(DonorRegistrationActivity.this, "Error: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        finish();
                                    }
                                });

                                if (resultUri!=null){
                                    final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                                            .child("profile images").child(currentUserId);
                                    Bitmap bitmap = null;

                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                                    byte[] data = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filePath.putBytes(data);

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DonorRegistrationActivity.this, "Failed to Upload Image!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata().getReference()!=null && taskSnapshot.getMetadata()!=null){
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl = uri.toString();
                                                        Map newImageMap = new HashMap<>();
                                                        newImageMap.put("profilepictureurl", imageUrl);

                                                        userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(DonorRegistrationActivity.this, "Image Url added to Database successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else{
                                                                    Toast.makeText(DonorRegistrationActivity.this, "Error: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                        finish();
                                                    }
                                                });

                                            }
                                        }
                                    });

                                    Intent intent = new Intent(DonorRegistrationActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }



                            }
                        }
                    });






                }






            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null  &&  resultCode == RESULT_OK && requestCode == 1 ){
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);

        }

    }
}

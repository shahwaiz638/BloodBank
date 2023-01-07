package Adapter;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhammaddaniyal.bloodbank.MainActivity;
import com.muhammaddaniyal.bloodbank.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Email.JavaMailApi;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userArray;
    private boolean guest;

    public UserAdapter(Context context, List<User> userArray) {
        this.context = context;
        this.userArray = userArray;
    }

    public UserAdapter(Context context, List<User> userArray, boolean guest) {
        this.context = context;
        this.userArray = userArray;
        this.guest = guest;
    }

    @NonNull
    @Override


    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_displayed_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userArray.get(position);


        if (user.getType() != null) {
            if (user.getType().equals("recipient")) {
                holder.emailNow.setVisibility(View.GONE);
            }
            if (guest) {
                holder.emailNow.setVisibility(View.GONE);
            }
            holder.type.setText(user.getType());
            holder.name.setText(user.getName());
            holder.email.setText(user.getEmail());
            holder.phone.setText(user.getphonenumber());
            holder.group.setText(user.getbloodgroup());

            if (user.getprofilepictureurl() != null) {
                Glide.with(context).load(user.getprofilepictureurl()).into(holder.dp);
            }
        }

        final String nameOfTheReceiver = user.getName();
        final String idOfTheReceiver = user.getId();

        holder.emailNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("SEND EMAIL")
                        .setMessage("Send email to " + user.getName() + "?")
                        .setCancelable(false)
                        .setPositiveButton(
                                "Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String nameOfSender = snapshot.child("name").getValue().toString();
                                                String email = snapshot.child("email").getValue().toString();
                                                String phone = snapshot.child("phonenumber").getValue().toString();
                                                String blood = snapshot.child("bloodgroup").getValue().toString();

                                                String mEmail = user.getEmail();
                                                String mSubject = "BLOOD DONATION";
                                                String mMessage = "Hello " + nameOfTheReceiver + ", " + nameOfSender +
                                                        " would like blood donation from you. Here's his/her details : \n" +
                                                        "Name: " + nameOfSender + "\n" +
                                                        " Phone Number:" + phone + "\n" +
                                                        "Email: " + email + "\n" +
                                                        "Blood Group: " + blood + "\n" +
                                                        "Kindly Reach out to him/her. Thank you! \n" +
                                                        "BLOOD DONATION APP DONATE BLOOD, SAVE LIVES!";





                                                String[] addresses = new String[1];
                                                addresses[0] = mEmail;
                                                Intent intent = new Intent(Intent.ACTION_SEND);
                                                intent.setType("*/*");
                                                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                                                intent.putExtra(Intent.EXTRA_SUBJECT, mSubject);
                                                intent.putExtra(Intent.EXTRA_TEXT, mMessage);
                                                    context.startActivity(intent);



                                               // JavaMailApi javaMailApi = new JavaMailApi(context, mEmail, mMessage, mSubject);
                                               // javaMailApi.execute();

                                                DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("email").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                senderRef.child(idOfTheReceiver).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("email").child(idOfTheReceiver);
                                                            receiverRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                                            addNotification(idOfTheReceiver,FirebaseAuth.getInstance().getCurrentUser().getUid());

                                                        }
                                                    }
                                                });


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                }

                        )
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView dp;
        private TextView name, email, phone, group, type;
        private Button emailNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            group = itemView.findViewById(R.id.group);
            type = itemView.findViewById(R.id.type);
            emailNow = itemView.findViewById(R.id.emailNow);
            dp = itemView.findViewById(R.id.userProfileImage);

        }
    }

    private void addNotification(String receiverId, String senderId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("notifications").child(receiverId);
        String date = DateFormat.getDateInstance().format(new Date());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("receiverId", receiverId);
        hashMap.put("senderId", senderId);
        hashMap.put("text", "Sent you an email, kindly check it out!");
        hashMap.put("date", date);

        reference.push().setValue(hashMap);
    }

}

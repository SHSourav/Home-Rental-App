package com.example.rentahome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactUs extends AppCompatActivity {

    LinearLayout l1,l2;
    private static final int REQUEST_CALL = 1;
    private String number1,email;
    private TextView adminMAil,adminPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        l1=findViewById(R.id.userMail);
        l2=findViewById(R.id.userCall);
        adminMAil=(TextView) findViewById(R.id.adminMail);
        adminPhone=(TextView) findViewById(R.id.adminPhone);

        DatabaseReference OtherRef= FirebaseDatabase.getInstance().getReference().child("Admins").child("Contact");
        OtherRef.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists()) {

                number1=dataSnapshot.child("mobile").getValue().toString();
                email=dataSnapshot.child("email").getValue().toString();
                adminPhone.setText(dataSnapshot.child("mobile").getValue().toString());
                adminMAil.setText(dataSnapshot.child("email").getValue().toString());

            }
         }

                    @Override
             public void onCancelled(DatabaseError databaseError) {

                         }
                       }
        );




        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }

        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call();
            }
        });
    }

    private void Call() {
        String number =number1;
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(dial));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }

        } else {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Call();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void sendMail() {
        String[] to = {email};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }
}

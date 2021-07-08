package com.example.rentahome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterMobileActivity extends AppCompatActivity {


    private EditText editText;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mobile);


        loadingbar=new ProgressDialog(this);
        editText = findViewById(R.id.editTextPhone);

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = editText.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }

                final String phonenumber = "+880" + number;

                final DatabaseReference RootRef;
                RootRef= FirebaseDatabase.getInstance().getReference();

                RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!(dataSnapshot.child("Users").child(phonenumber).exists())){
                            Intent intent = new Intent(RegisterMobileActivity.this, OTPActivity.class);
                            intent.putExtra("phonenumber", phonenumber);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"This"+phonenumber+"Already Exist", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Toast.makeText(getApplicationContext(),"Please try whith another number", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }*/
    }
}

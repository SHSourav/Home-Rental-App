package com.example.rentahome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentahome.Model.Users;
import com.example.rentahome.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class AfterSplash extends AppCompatActivity {

    private int progressValue;
    private String ParentDBName;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Paper.init(this);
        ParentDBName = Paper.book().read(Prevalent.ParentDB);


        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        loadingbar = new ProgressDialog(this);

        if (UserPhoneKey != "" && UserPasswordKey != "") {

            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {

                AllowAccess(UserPhoneKey, UserPasswordKey);


            }

        }
        Intent intent = new Intent(AfterSplash.this, LoginActivity.class);
        startActivity(intent);

    }

    private void AllowAccess(final String phone,final String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("Users").child(phone).exists())){

                    Users usersData=dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone)){

                        if(usersData.getPassword().equals(password)){

                            try {
                                if (ParentDBName.equals("Users")) {

                                    Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();

                                    Prevalent.currentOnlineUser = usersData;
                                    Intent intent=new Intent(AfterSplash.this,HomeActivity.class);
                                    startActivity(intent);
                                }
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Auto Login of User Problem", Toast.LENGTH_SHORT).show();
                            }


                        }

                    }

                }
                else if((dataSnapshot.child("Admins").child(phone).exists())){

                    Users usersData=dataSnapshot.child("Admins").child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone)){

                        if(usersData.getPassword().equals(password)){
                            try {
                                if(ParentDBName.equals("Admins")){

                                    Toast.makeText(getApplicationContext(),"Admin Logged in successfully",Toast.LENGTH_SHORT).show();

                                    Prevalent.currentOnlineUser=usersData;
                                    Intent intent=new Intent(AfterSplash.this,AdminActivity.class);
                                    startActivity(intent);

                                }}catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Auto Login of Admin Problem", Toast.LENGTH_SHORT).show();
                            }


                        }

                    }

                }


                else {
                    Toast.makeText(getApplicationContext(),"This"+phone+"do not Exist",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(getApplicationContext(),ParentDBName,Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
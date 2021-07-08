package com.example.rentahome;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentahome.Prevalent.Prevalent;
import com.example.rentahome.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private TextView txtSignIn,Forgot;
    public Button signInbtn;
    private EditText SignInPhone,SignInPassword;
    private ProgressDialog loadingbar;
    private String ParentDBName="Users";
    private CheckBox checkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Forgot=(TextView)findViewById(R.id.Forgot);
        txtSignIn=(TextView)  findViewById(R.id.Register);
        signInbtn=(Button) findViewById(R.id.btnLogin);
        SignInPassword=(EditText) findViewById(R.id.SignInPass);
        SignInPhone=(EditText)findViewById(R.id.SignInPhone);

        loadingbar =new ProgressDialog(this);


        checkBoxRememberMe=(CheckBox)findViewById(R.id.checkbox);
        Paper.init(this);


        signInbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });


        Forgot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  Intent intent=new Intent(LoginActivity.this,ForgotPassword.class);
               // startActivity(intent);
            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterMobileActivity.class);
                startActivity(intent);
            }
        });







    }

    private void LoginUser() {

        String phone="+88"+SignInPhone.getText().toString();
        String password=SignInPassword.getText().toString();

        DatabaseReference OtherRef= FirebaseDatabase.getInstance().getReference().child("Admins")
                .child("+88"+SignInPhone.getText().toString());
        OtherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    ParentDBName="Admins";

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if(TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(),"Please Insert Phone Number",Toast.LENGTH_SHORT);
        }

        else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please Insert Password",Toast.LENGTH_SHORT);
        }

        else{
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please Wait..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            AllowAccessToAccount(phone,password);


        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {




        Paper.book().write(Prevalent.UserPhoneKey,phone);
        Paper.book().write(Prevalent.UserPasswordKey,password);




        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.child(ParentDBName).child(phone).exists())){
                    Paper.book().write(Prevalent.ParentDB,ParentDBName);


                    Users usersData=dataSnapshot.child(ParentDBName).child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone)){

                        if(usersData.getPassword().equals(password)){

                            if(ParentDBName.equals("Admins"))
                            {
                                Toast.makeText(getApplicationContext(),"Welcome Admin,you are Logged in successfully",Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent intent=new Intent(LoginActivity.this,AdminActivity.class);
                                Prevalent.currentOnlineUser=usersData;
                                startActivity(intent);

                            }
                            else if(ParentDBName.equals("Users"))
                            {
                                Toast.makeText(getApplicationContext(),"Logged in successfully",Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser=usersData;
                                startActivity(intent);

                            }
                        }

                        else {
                            Toast.makeText(getApplicationContext(),"Incorrect Password",Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Toast.makeText(getApplicationContext(),"Please try with correct Password",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Incorrect phone number",Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                        Toast.makeText(getApplicationContext(),"Please try with correct number",Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(),"This "+phone+" do not Exist",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(getApplicationContext(),"Please try with correct number",Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}

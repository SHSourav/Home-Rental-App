package com.example.rentahome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private TextView txtSignUp,signUpPhone;
    private Button signUpbtn;
    private EditText SignUpName,SignUpEmail,SignUpPassword;
    private ProgressBar progressBar;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog loadingbar;
    private String SignUpPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        SignUpPhone=getIntent().getStringExtra("p_number");


        progressBar=(ProgressBar)findViewById(R.id.progressId);
        txtSignUp=(TextView)  findViewById(R.id.AlreadyRegister);
        signUpbtn=findViewById(R.id.SignUpButton);
        SignUpName=(EditText)findViewById(R.id.SignUpName);
        SignUpEmail=(EditText) findViewById(R.id.SignUpEmail);
        signUpPhone=(TextView)findViewById(R.id.SignUpPhone);
        signUpPhone.setText(SignUpPhone);

        SignUpPassword=(EditText) findViewById(R.id.SignUpPass);

        loadingbar=new ProgressDialog(this);


        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();


            }
        });
    }

    private void CreateAccount() {

        String name=SignUpName.getText().toString();
        String email=SignUpEmail.getText().toString();
        String phone=signUpPhone.getText().toString();
        String password=SignUpPassword.getText().toString();



        if(TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(),"Please Insert Name",Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please Insert Email",Toast.LENGTH_SHORT);
        }

        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(),"Please Insert Phone Number",Toast.LENGTH_SHORT);
        }

        else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please Insert Password",Toast.LENGTH_SHORT);
        }


        else if(!email.matches(emailPattern))
        {
            Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please Wait..");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidatePhoneNumber(name,phone,email,password);
        }
    }

    private void ValidatePhoneNumber(final String name, final String phone, final String email, final String password) {



        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!(dataSnapshot.child("Users").child(phone).exists())){
                    HashMap<String,Object> userdataMap= new HashMap<>();
                    userdataMap.put("name",name);
                    userdataMap.put("phone",phone);
                    userdataMap.put("email",email);
                    userdataMap.put("password",password);


                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"Congratulations..",Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                        Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                                        startActivity(intent);

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"Check Your Internet Connection",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(),"This"+phone+"Already Exist",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(getApplicationContext(),"Please try whith another number",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
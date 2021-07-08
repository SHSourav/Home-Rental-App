package com.example.rentahome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentahome.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MyAccount extends AppCompatActivity {

    EditText MAname,MAemail,MApassword,MAaddress,MAsq;
    TextView MAphone;
    private String oldPhone,oldPass;
    private Button MAbtnUpdate;
    private CircleImageView UpdateProfileImage;
    private Uri imageUri;
    private String myUrl = "";
    private String checker = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        Paper.init(this);

        MAname=findViewById(R.id.myAccountName);
        MAphone=findViewById(R.id.myAccountPhone);
        MAemail=findViewById(R.id.myAccountEmail);
        MApassword=findViewById(R.id.myAccountPass);
        MAbtnUpdate=findViewById(R.id.myAccountUpdateBtn);
        UpdateProfileImage=(CircleImageView)findViewById(R.id.updateProfileimage);
        DisplayOthersInfo(UpdateProfileImage,MAname,MAphone,MAemail,MApassword);
        UpdateProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(MyAccount.this);
            }
        });


        MAbtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }
                else{

                updateOthersinfo();

            }}
        });

    }


    private void updateOthersinfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", MAname.getText().toString());
        userMap. put("phone", MAphone.getText().toString());
        userMap. put("email", MAemail.getText().toString());
        userMap. put("password", MApassword.getText().toString());
        ref.updateChildren(userMap);


        if(oldPass!=MApassword.getText().toString()){
            Paper.book().write(Prevalent.UserPasswordKey,MApassword.getText().toString());

        }


        Toast.makeText(MyAccount.this, "Information Updated Successfully.", Toast.LENGTH_SHORT).show();
        Intent intent2=new Intent(MyAccount.this,MyAccount.class);
        startActivity(intent2);
        finish();
    }





    //////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            UpdateProfileImage.setImageURI(imageUri);
        }


        else{
            Toast.makeText(this,"Error,Try again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MyAccount.this, MyAccount.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(MAname.getText().toString()))
        {
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(MAemail.getText().toString()))
        {
            Toast.makeText(this, "Email is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(MAphone.getText().toString()))
        {
            Toast.makeText(this, "Phone Number is mandatory.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(MApassword.getText().toString()))
        {
            Toast.makeText(this, "Password Number is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }


    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap. put("name", MAname.getText().toString());
                        userMap. put("email", MAemail.getText().toString());
                        userMap. put("phone", MAphone.getText().toString());
                        userMap. put("password", MApassword.getText().toString());
                        userMap. put("image", myUrl);
                        if(oldPass!=MApassword.getText().toString()){
                            Paper.book().write(Prevalent.UserPasswordKey,MApassword.getText().toString());

                        }
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(MyAccount.this, MyAccount.class));
                        Toast.makeText(MyAccount.this, "Profile Info updated successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(MyAccount.this, "Error in Image saving", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }







    ////////////////////////////////////////
    private void DisplayOthersInfo(final CircleImageView updateProfileImage,final EditText MAname, final TextView MAphone, final EditText MAemail,
                                    final EditText MApassword) {

        DatabaseReference OtherRef= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());
        OtherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){


                        String MAname1 = dataSnapshot.child("name").getValue().toString();
                        String MAphone1 = dataSnapshot.child("phone").getValue().toString();
                        String MAemail1 = dataSnapshot.child("email").getValue().toString();
                        String MApassword1 = dataSnapshot.child("password").getValue().toString();

                        oldPhone = MAphone1;
                        oldPass = MApassword1;


                        MAname.setText(MAname1);
                        MAphone.setText(MAphone1);
                        MAemail.setText(MAemail1);
                        MApassword.setText(MApassword1);
                    if(dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(updateProfileImage);
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void onBackPressed() {

        Intent intent2=new Intent(MyAccount.this,HomeActivity.class);
        startActivity(intent2);
    }


}

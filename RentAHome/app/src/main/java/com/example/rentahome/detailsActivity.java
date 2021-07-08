package com.example.rentahome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class detailsActivity extends AppCompatActivity {
    private String userkey;
    DatabaseReference Rdb;
    private static final int REQUEST_CALL = 1;
    private TextView Daddress,Drprice,Dinfo,Dextrainfo,Ddescription;
    Button call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Daddress=(TextView)findViewById(R.id.details_address);
        Drprice=(TextView)findViewById(R.id.details_price);
        Dinfo=(TextView)findViewById(R.id.details_info);
        Dextrainfo=(TextView)findViewById(R.id.details_extra_info);
        Ddescription=(TextView)findViewById(R.id.details_description);
        call=(Button)findViewById(R.id.details_call_btn);


        userkey=getIntent().getStringExtra("userkey");
        Rdb= FirebaseDatabase.getInstance().getReference("FlatInfo").child(userkey);

        Rdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String rfloor = dataSnapshot.child("floor").getValue().toString();
                    String rhouse = dataSnapshot.child("house").getValue().toString();
                    String rroad = dataSnapshot.child("road").getValue().toString();
                    String rarea = dataSnapshot.child("area").getValue().toString();
                    String rprice = dataSnapshot.child("price").getValue().toString();
                    String rbed = dataSnapshot.child("bed").getValue().toString();
                    final String rphone = dataSnapshot.child("number").getValue().toString();
                    String rrbed="";
                    if(rbed.equals("0"))
                    {
                        rrbed="";
                    }
                    else {
                        rrbed= rbed+" Bed Room,";
                    }
                    String rwash = dataSnapshot.child("wash").getValue().toString();
                    String rrwash="";
                    if(rwash.equals("0"))
                    {
                        rrwash="";
                    }
                    else {
                        rrwash= rwash+" Washroom,";
                    }

                    String rkitchen = dataSnapshot.child("kitchen").getValue().toString();
                    String rrkitchen="";
                    if(rkitchen.equals("0"))
                    {
                        rrkitchen="";
                    }
                    else {
                        rrkitchen=rkitchen+" Kitchen,";
                    }


                    String rstore = dataSnapshot.child("store").getValue().toString();
                    String rrstore="";
                    if(rstore.equals("0"))
                    {
                        rrstore="";
                    }
                    else {
                        rrstore=rstore+" Store Room.";
                    }
                    String rveranda = dataSnapshot.child("veranda").getValue().toString();
                    String rrveranda="";
                    if(rveranda.equals("0"))
                    {
                        rrveranda="";
                    }
                    else{
                        rrveranda=rveranda+" Veranda,";
                    }
                    String rdrawing = dataSnapshot.child("drawing").getValue().toString();
                    if(rdrawing.equals("Yes")){
                        rdrawing="Drawing Space,";
                    }
                    else {
                        rdrawing="";
                    }
                    String rdining = dataSnapshot.child("dining").getValue().toString();
                    if(rdining.equals("Yes")){
                        rdining=" Dining Spcae,";
                    }
                    else {
                        rdining="";
                    }

                    String rlift = dataSnapshot.child("lift").getValue().toString();
                    if(rlift.equals("Yes")){
                        rlift=" Lift Facility";
                    }
                    else {
                        rlift="";
                    }
                    String rbachelor = dataSnapshot.child("bachelor").getValue().toString();
                    if(rbachelor.equals("Yes")){
                        rbachelor=" Bachelor Allowed.";
                    }
                    else {
                        rbachelor=" Bachelor Not Allowed.";
                    }
                    String rcaretaker = dataSnapshot.child("caretaker").getValue().toString();
                    if(rcaretaker.equals("Yes")){
                        rcaretaker=" Caretaker Service,";
                    }
                    else{
                        rcaretaker="";
                    }
                    String rdescription = dataSnapshot.child("description").getValue().toString();

                    ImageSlider imageSlider=findViewById(R.id.slider);

                    List<SlideModel> slideModels=new ArrayList<>();
                    slideModels.add(new SlideModel(dataSnapshot.child("image1").getValue().toString()));
                    slideModels.add(new SlideModel(dataSnapshot.child("image2").getValue().toString()));
                    slideModels.add(new SlideModel(dataSnapshot.child("image3").getValue().toString()));
                    slideModels.add(new SlideModel(dataSnapshot.child("image4").getValue().toString()));
                    imageSlider.setImageList(slideModels,false);

                   Daddress.setText("Flat:"+rfloor+", House:"+rhouse+", Road:"+rroad+", Area:"+rarea);
                   Drprice.setText("Rental Price: ৳"+rprice);
                   Dinfo.setText(rrbed+rrwash+rrkitchen+rrveranda+rrstore);
                   Dextrainfo.setText(rdrawing+rdining+rlift+rcaretaker+rbachelor);
                   Ddescription.setText(rdescription);

                   call.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {

                                String number =rphone;


                                if (ContextCompat.checkSelfPermission(detailsActivity.this,
                                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(detailsActivity.this,
                                                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                                            } else {
                                                              String dial = "tel:" + number;
                                                              Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                              callIntent.setData(Uri.parse(dial));
                                                              callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                              startActivity(callIntent);
                                                          }


                       }
                   });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
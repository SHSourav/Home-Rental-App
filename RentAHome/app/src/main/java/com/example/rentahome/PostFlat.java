package com.example.rentahome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentahome.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostFlat extends AppCompatActivity {

    private ImageView r1,r2,r3,r4;
    private String s,slatitude,slongitude,snumber,sfloor,shouse,sroad,sarea,sbed,sstore,swash,skitchen,sveranda,sdrawing="No",sdining="No",
                   slift="No",sbachelor="No",scaretaker="No",sprice,sdescription,suserid;
    private int num=0,count=1;
    private Button setlocbtn,postbrn;
    private EditText mobile,floor,house,road,area,price,description;
    private TextView location;
    private Spinner bed,store,wash,kitchen,veranda;
    private CheckBox drawing,dining,lift,bachelor,caretaker;
    private ProgressDialog loadingbar;
    private StorageReference ImagesRef;
    private DatabaseReference dRef;
    private String checker = "",saveCurrentTime,saveCurrentDate;
    private String productRandomKey,downloadImageUrl,dI1,dI2,dI3,dI4;
    private Uri imageUri,imageUri1,imageUri2,imageUri3,imageUri4;
    LocationManager locationManager;
    private static  final int REQUEST_LOCATION=1;
    String latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_flat);

        ImagesRef= FirebaseStorage.getInstance().getReference().child("Flat Images");
        dRef= FirebaseDatabase.getInstance().getReference().child("FlatInfo");

        r1=(ImageView)findViewById(R.id.room1);
        r2=(ImageView)findViewById(R.id.room2);
        r3=(ImageView)findViewById(R.id.room3);
        r4=(ImageView)findViewById(R.id.room4);
        location=(TextView)findViewById(R.id.postloaction);
        mobile=(EditText)findViewById(R.id.postNumber);
        floor=(EditText)findViewById(R.id.postFloorNo);
        house=(EditText)findViewById(R.id.postHouseNo);
        road=(EditText)findViewById(R.id.postRoadNo);
        area=(EditText)findViewById(R.id.postAreaName);
        price=(EditText)findViewById(R.id.postrentingPrice);
        description=(EditText)findViewById(R.id.postDescription);
        bed=(Spinner)findViewById(R.id.spinnerbed);
        store=(Spinner)findViewById(R.id.spinnerstore);
        wash=(Spinner)findViewById(R.id.spinnerwash);
        kitchen=(Spinner)findViewById(R.id.spinnerkitchen);
        veranda=(Spinner)findViewById(R.id.spinnerveranda);
        drawing=(CheckBox)findViewById(R.id.checkdrawing);
        dining=(CheckBox)findViewById(R.id.checkdining);
        lift=(CheckBox)findViewById(R.id.checklift);
        bachelor=(CheckBox)findViewById(R.id.checkbachelor);
        caretaker=(CheckBox)findViewById(R.id.checkcaretaker);
        setlocbtn=(Button)findViewById(R.id.postlocationbtn);
        postbrn=(Button)findViewById(R.id.postMyFlat);
        loadingbar =new ProgressDialog(this);

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                num=1;
                CropImage.activity(imageUri)
                        .start(PostFlat.this);
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                num=2;
                CropImage.activity(imageUri)
                        .start(PostFlat.this);
            }
        });
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                num=3;
                CropImage.activity(imageUri)
                        .start(PostFlat.this);
            }
        });
        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                num=4;
                CropImage.activity(imageUri)
                        .start(PostFlat.this);
            }
        });
        setlocbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //Check gps is enable or not

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    //Write Function To enable gps

                    OnGPS();
                }
                else
                {
                    //GPS is already On then

                    getLocation();
                }
            }
        });

        //post flat
         postbrn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlatData();
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

                if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
                {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    imageUri = result.getUri();
                    if(num==1){
                        r1.setImageURI(imageUri);
                        num=0;
                        imageUri1=imageUri;
                        imageUri=null;
                    }
                    if(num==2){
                        r2.setImageURI(imageUri);
                        num=0;
                        imageUri2=imageUri;
                        imageUri=null;
                    }if(num==3){
                        r3.setImageURI(imageUri);
                        num=0;
                        imageUri3=imageUri;
                        imageUri=null;
                    }if(num==4){
                        r4.setImageURI(imageUri);
                        num=0;
                       imageUri4=imageUri;
                    }

                }
                else
                {
                    Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(PostFlat.this, PostFlat.class));
                    finish();
                }
    }

    private void getLocation() {

        //Check Permissions again

                if (ActivityCompat.checkSelfPermission(PostFlat.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PostFlat.this,

                        Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
                else
                {
                    Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    if (LocationGps !=null)
                    {
                        double lat=LocationGps.getLatitude();
                        double longi=LocationGps.getLongitude();

                        latitude=String.valueOf(lat);
                        longitude=String.valueOf(longi);

                        location.setText(latitude+","+longitude);
                    }
                    else if (LocationNetwork !=null)
                    {
                        double lat=LocationNetwork.getLatitude();
                        double longi=LocationNetwork.getLongitude();

                        latitude=String.valueOf(lat);
                        longitude=String.valueOf(longi);

                        location.setText(latitude+","+longitude);
                    }
                    else if (LocationPassive !=null)
                    {
                        double lat=LocationPassive.getLatitude();
                        double longi=LocationPassive.getLongitude();

                        latitude=String.valueOf(lat);
                        longitude=String.valueOf(longi);

                        location.setText(latitude+","+longitude);
                    }
                    else
                    {
                        Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
                    }

                    //Thats All Run Your App
                }

    }
    private void OnGPS() {

                final AlertDialog.Builder builder= new AlertDialog.Builder(this);

                builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                final AlertDialog alertDialog=builder.create();
                alertDialog.show();
    }

    private void FlatData() {

                slatitude=latitude;
                slongitude=longitude;
                snumber=mobile.getText().toString();
                sfloor=floor.getText().toString();
                shouse=house.getText().toString();
                sroad=road.getText().toString();
                sarea=area.getText().toString();
                suserid= Prevalent.currentOnlineUser.getPhone();
                sbed=bed.getSelectedItem().toString();
                sstore=store.getSelectedItem().toString();
                swash=wash.getSelectedItem().toString();
                skitchen=kitchen.getSelectedItem().toString();
                sveranda=veranda.getSelectedItem().toString();
                if(drawing.isChecked()){
                     sdrawing="Yes";
                }if(dining.isChecked()){
                     sdining="Yes";
                }if(lift.isChecked()){
                     slift="Yes";
                }if(bachelor.isChecked()){
                     sbachelor="Yes";
                }if(caretaker.isChecked()){
                     scaretaker="Yes";
                }
                sprice=price.getText().toString();
                sdescription=description.getText().toString();

                if(imageUri==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Add four Images",Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(snumber))
                {
                    Toast.makeText(getApplicationContext(),"Mobile Number is Mandatory..",Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(sfloor))
                {
                    Toast.makeText(getApplicationContext()," Floor Number is Mandatory..",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(shouse))
                {
                    Toast.makeText(getApplicationContext()," House Number is Mandatory..",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(sroad))
                {
                    Toast.makeText(getApplicationContext()," Road Number is Mandatory..",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(sarea))
                {
                    Toast.makeText(getApplicationContext()," Area is Mandatory..",Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(sprice))
                {
                    Toast.makeText(getApplicationContext()," Renting Price is Mandatory..",Toast.LENGTH_SHORT).show();
                }

                else{

                    StoreProductInformation();
                }
    }

    //For Date and Time

    private void StoreProductInformation() {

        loadingbar.setTitle("Posting Your Flat");
        loadingbar.setMessage("Please Wait..");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();



        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime=currentTime.format(calendar.getTime());


        productRandomKey=saveCurrentDate+saveCurrentTime+sroad+sarea+sfloor;


            if(count==1){
                imageUri=imageUri1;
            }if(count==2){
                imageUri=imageUri2;
            }if(count==3){
                imageUri=imageUri3;
            }if(count==4){
                imageUri=imageUri4;
            }
        final StorageReference filePath=ImagesRef.child(suserid).child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");


        final UploadTask uploadTask=filePath.putFile(imageUri);



        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(getApplicationContext(),"Error.."+message,Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(),"Image "+count+" Uploaded Succesfully",Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){

                            throw task.getException();


                        }

                        downloadImageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {

                            downloadImageUrl=task.getResult().toString();
                            imageLink();

                        }
                    }
                });
            }
        });

    }

    private void imageLink(){

        if (count==1){
            dI1=downloadImageUrl;
            count++;
            StoreProductInformation();
        }else if (count==2){
            dI2=downloadImageUrl;
            count++;
            StoreProductInformation();
        }else if (count==3){
            dI3=downloadImageUrl;
            count++;
            StoreProductInformation();
        }else if (count==4){
            dI4=downloadImageUrl;
            SaveProductInfoToDatabase();
        }

    }

    private void SaveProductInfoToDatabase() {

        HashMap<String,Object> productMap=new HashMap<>();

        productMap.put("userid",suserid);
        productMap.put("userkey",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("image1",dI1);
        productMap.put("image2",dI2);
        productMap.put("image3",dI3);
        productMap.put("image4",dI4);
        productMap.put("latitude",slatitude);
        productMap.put("longitude",slongitude);
        productMap.put("number",snumber);
        productMap.put("floor",sfloor);
        productMap.put("house",shouse);
        productMap.put("road",sroad);
        productMap.put("area",sarea);
        productMap.put("bed",sbed);
        productMap.put("store",sstore);
        productMap.put("wash",swash);
        productMap.put("kitchen",skitchen);
        productMap.put("veranda",sveranda);
        productMap.put("drawing",sdrawing);
        productMap.put("dining",sdining);
        productMap.put("lift",slift);
        productMap.put("bachelor",sbachelor);
        productMap.put("caretaker",scaretaker);
        productMap.put("price",sprice);
        productMap.put("description",sdescription);

        dRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent intent=new Intent(PostFlat.this,HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Your Flat is Added Successfully..",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
                else {
                    loadingbar.dismiss();
                    String message=task.getException().toString();

                    Toast.makeText(getApplicationContext(),"Error"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
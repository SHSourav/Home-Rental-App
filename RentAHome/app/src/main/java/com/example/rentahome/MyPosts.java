package com.example.rentahome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rentahome.Model.Flats;
import com.example.rentahome.Prevalent.Prevalent;
import com.example.rentahome.ViewHolder.HistoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyPosts extends AppCompatActivity {
    private RecyclerView recyclerView2;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        recyclerView2=findViewById(R.id.history_list);
        recyclerView2.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart(){
        super.onStart();

        final DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("FlatInfo");

        FirebaseRecyclerOptions<Flats> options=new FirebaseRecyclerOptions.Builder<Flats>()
                .setQuery(historyRef.orderByChild("userid").equalTo(Prevalent.currentOnlineUser.getPhone()),Flats.class).build();

        FirebaseRecyclerAdapter<Flats, HistoryViewHolder> adapter=
                new FirebaseRecyclerAdapter<Flats, HistoryViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position, @NonNull final Flats model) {



                        holder.historyaddress.setText("Flat:"+model.getFloor()+", House:"+model.getHouse()+", Road:"+model.getRoad()+", Area:"+model.getArea());

                        holder.historyRprice.setText(model.getPrice());
                        holder.historynumber.setText(model.getNumber());
                        holder.deletePost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String uID=getRef(position).getKey();
                                historyRef.child(uID).removeValue();
                                StorageReference storageReference1 = FirebaseStorage.getInstance().
                                        getReferenceFromUrl(model.getImage1());
                                storageReference1.delete();
                                StorageReference storageReference2 = FirebaseStorage.getInstance().
                                        getReferenceFromUrl(model.getImage2());
                                storageReference2.delete();
                                StorageReference storageReference3 = FirebaseStorage.getInstance().
                                        getReferenceFromUrl(model.getImage3());
                                storageReference3.delete();
                                StorageReference storageReference4= FirebaseStorage.getInstance().
                                        getReferenceFromUrl(model.getImage4());
                                storageReference4.delete();
                            }
                        });


                    }


                    @NonNull
                    @Override
                    public HistoryViewHolder onCreateViewHolder(@NonNull  ViewGroup viewGroup, int position) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                                .post_layout, viewGroup, false);


                        HistoryViewHolder holder1=new HistoryViewHolder(view);
                        return holder1;
                    }
                };



        recyclerView2.setAdapter(adapter);
        adapter.startListening();



    }
}
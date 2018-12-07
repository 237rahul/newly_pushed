package com.example.pavani.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity
{

    //Initializing fields
    private TextView userName;
    private CircleImageView userProfileImage;

    //Database references to retrieve the data
    private DatabaseReference dbr;
    private FirebaseAuth mAuth;
    private StorageReference sr;
    // for current user ID
    private String curr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        curr = mAuth.getCurrentUser().getUid();
        dbr = FirebaseDatabase.getInstance().getReference().child("students").child(curr);

        //Fields
        userName = (TextView) findViewById(R.id.profile_username);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_profile_pic);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String Fullname = dataSnapshot.child("Fullname").getValue().toString();
                    String prof_image = dataSnapshot.child("prof_image").getValue().toString();
                    Picasso.get().load(prof_image).placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText(Fullname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

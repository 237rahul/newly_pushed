package com.example.pavani.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private ProgressDialog pd;
    String du;
//    final static int Gallerypick = 1;

    //Fields Initialization
//    private EditText userName,userProfileName, userStatus, userCountry, userGender, userRelation, userDateofBirth;
    private Button updateAccountSettings;
    private EditText userName;
    private CircleImageView userProfileImage;


    //Database references to retrieve the data
    private DatabaseReference dbr;
    private FirebaseAuth mAuth;
    private StorageReference sr;

    //Getting ID
    private String curr;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        pd=new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        // Reference to the user's node
        curr = mAuth.getCurrentUser().getUid();
        dbr = FirebaseDatabase.getInstance().getReference().child("students").child(curr);
        sr= FirebaseStorage.getInstance().getReference().child("image_prof");



        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        //Back button towards main_activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Fields

//        userName = (EditText) findViewById(R.id.settings_username);
        userName = (EditText) findViewById(R.id.settings_username);
//        userStatus = (EditText) findViewById(R.id.settings_status);
//        userCountry = (EditText) findViewById(R.id.settings_country);
//        userGender = (EditText) findViewById(R.id.settings_gender);
//        userRelation = (EditText) findViewById(R.id.settings_relationship_status);
//        userDateofBirth = (EditText) findViewById(R.id.settings_dob);
        updateAccountSettings = (Button) findViewById(R.id.update_AccountSettings_button);
        userProfileImage = (CircleImageView) findViewById(R.id.settings_profile_image);

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Toast.makeText(SettingsActivity.this,dataSnapshot.child("Fullname").getValue().toString(),Toast.LENGTH_LONG).show();
                //Retrieve data from firebase
                if(dataSnapshot.exists())
                {
                    String Fullname = dataSnapshot.child("Fullname").getValue().toString();
                    String prof_image = dataSnapshot.child("prof_image").getValue().toString();
//                    Toast.makeText(SettingsActivity.this,Fullname,Toast.LENGTH_LONG).show();
                    Picasso.get().load(prof_image).placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText(Fullname);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //to change values in the data base
        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUserInfo();
            }
        });

        //change profile picture
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
//                Intent galleryIntent=new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent,Gallerypick);

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);

            }
        });
    }


    @Override
    protected void onActivityResult(int rc,int resc, Intent data)
    {
        super.onActivityResult(rc,resc,data);
        if (rc==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult r = CropImage.getActivityResult(data);

            if (resc == RESULT_OK) {
                pd.setTitle("uploading image");
                pd.setMessage("please wait");
                pd.show();
                pd.setCanceledOnTouchOutside(true);

                Uri ru = r.getUri();
                final StorageReference fp = sr.child(curr + ".jpg");
                fp.putFile(ru).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fp.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                du = uri.toString();
                                pd.dismiss();
                                dbr.child("prof_image").setValue(du)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Toast.makeText(SettingsActivity.this,"success!!!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                    pd.dismiss();
                                                }
                                                else
                                                {
                                                    Toast.makeText(SettingsActivity.this,"error"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                    pd.dismiss();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
            }

        }
    }






    private void validateUserInfo()
    {
        String usnam = userName.getText().toString();

        if(TextUtils.isEmpty(usnam))
        {
            Toast.makeText(this,"Please enter user name..",Toast.LENGTH_LONG).show();
        }
        else
        {
            updateAccountInfo(usnam);
        }
    }

    private void updateAccountInfo(String usnam)
    {
        HashMap userInfo = new HashMap();
        userInfo.put("Fullname",usnam);

        //saving data inside our database
        dbr.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    //sends user to main activity
                    mainact();
                    Toast.makeText(SettingsActivity.this,"User Information Updated Successfully..",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this,"Error occured while updating User Information",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void mainact() {
        Intent mi=new Intent(SettingsActivity.this,MainActivity.class);
        mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mi);
        finish();
    }
}

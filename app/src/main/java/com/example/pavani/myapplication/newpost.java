package com.example.pavani.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.Console;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

public class newpost extends AppCompatActivity
{
    private Toolbar utoolbar;
    private ImageButton post_image;
    private Button post_button;
    private EditText description;
    private static final int Gallery_pic=1;
    private Uri Image1;
    private String discipt;
    private StorageReference postref;
    private String Currdatesaver,Currtimesaver,randname,url_download,curruser_id;
    private DatabaseReference refuser,refpost;
    private FirebaseAuth uauth;
    private ProgressDialog Loadbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        uauth=FirebaseAuth.getInstance();
        curruser_id=uauth.getCurrentUser().getUid();
        postref= FirebaseStorage.getInstance().getReference();

        post_image=(ImageButton)findViewById(R.id.post_image);
        post_button=(Button)findViewById(R.id.post_button);
        description=(EditText)findViewById(R.id.description_of_Post);
        refuser= FirebaseDatabase.getInstance().getReference().child("students");
        refpost= FirebaseDatabase.getInstance().getReference().child("posts12");
        utoolbar=(Toolbar)findViewById( R.id.update_post_toolbar);
        Loadbar=new ProgressDialog(this);
        setSupportActionBar(utoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("post update");


        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(newpost.this);
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//            Toast.makeText(newpost.this,"Posted sucessfully...",Toast.LENGTH_LONG).show();
                valildatepost();
            }
        });

    }

    private void valildatepost()
    {
        String discipt=description.getText().toString();

//        if(Image1==null)
//        {
//            Toast.makeText(this, "please select post image...", Toast.LENGTH_SHORT).show();
//
//        }
        if(discipt.length()==0)
        {
            Toast.makeText(this, "please write a message about post...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Loadbar.setTitle("Add new Post");

            Loadbar.setMessage("please wait, while Updating... ");
            Loadbar.show();

            Loadbar.setCanceledOnTouchOutside(true);


            push_post_to_Database();
        }
    }

    private void push_post_to_Database()
    {

        Calendar time_date=Calendar.getInstance();
        SimpleDateFormat currDate=new SimpleDateFormat("dd-MMMM-yyyy");
        Currdatesaver=currDate.format(time_date.getTime());

        Calendar time_date1=Calendar.getInstance();
        SimpleDateFormat currTime=new SimpleDateFormat("HH:mm");
        Currtimesaver=currTime.format(time_date1.getTime());
        randname=Currdatesaver+Currtimesaver;

        final StorageReference postpath=postref.child("Post78pictures").child(Image1.getEncodedPath()+randname+".jpg");
        postpath.putFile(Image1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                postpath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url_download = uri.toString();
                        savePostinfo();
                    }
                });

            }
        });
//
    }

    private void savePostinfo()
    {

//        Log.e("===>>>", "savePostinfo: " );
        refuser.child(curruser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
//
                if(dataSnapshot.exists())
                {

//                    Toast.makeText(newpost.this, "savePostinfo...", Toast.LENGTH_SHORT).show();

                    String Ufullname=dataSnapshot.child("Fullname").getValue().toString();
                    String Uprofileimage;
                    if (dataSnapshot.child("prof_image").getValue()==null)
                    {
                        Uprofileimage="null";
                    }
                    else {
                        Uprofileimage = dataSnapshot.child("prof_image").getValue().toString();
                    }
//                    Toast.makeText(newpost.this, "savePostinfo..."+Ufullname+" "+Uprofileimage, Toast.LENGTH_SHORT).show();
                    HashMap<String, String> Mapposts =new HashMap<>();
                    Mapposts.put("uid",curruser_id);
//                    Toast.makeText(newpost.this, "savePostinfo..."+Mapposts.get("uid"), Toast.LENGTH_SHORT).show();
                    Mapposts.put("date",Currdatesaver);
                    Mapposts.put("time",Currtimesaver);
                    Mapposts.put("description",description.getText().toString());
                    Mapposts.put("postimage",url_download);
                    Mapposts.put("profileimage",Uprofileimage);
                    Mapposts.put("Fullname",Ufullname);
//                    Log.e("===>>>", "Mapposts: 1"+Mapposts.toString() );
////                    Toast.makeText(newpost.this,refpost.toString(),Toast.LENGTH_LONG).show();
//                    refpost.child(curruser_id+randname).setValue(Mapposts);
//                    Toast.makeText(newpost.this, "savePostinfo..."+Mapposts.get("Fullname")+" "+Mapposts.get("description"), Toast.LENGTH_SHORT).show();
//                    Log.e("===>>>", "Mapposts: 2"+Mapposts.toString() );


                    refpost.child(curruser_id+randname).setValue(Mapposts).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                BackToMainActivity();
                                Toast.makeText(newpost.this,"Post successfully updated..!",Toast.LENGTH_LONG).show();
                                Loadbar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(newpost.this,"Error occured in updating post",Toast.LENGTH_LONG).show();
                                Loadbar.dismiss();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


//    private void Opening_Gallery()
//    {
//        Intent galleryIntent=new Intent();
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent,Gallery_pic);
////        Log.e("===>>>", "onActivityResult: 3" );
////        Toast.makeText(newpost.this,"onActivityResult: 3",Toast.LENGTH_LONG).show();
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.e("===>>>", "onActivityResult: 4" );
        Toast.makeText(newpost.this,"onActivityResult: 4",Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult r = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Image1 = r.getUri();
                post_image.setImageURI(Image1);
            }
            }
            }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            BackToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void BackToMainActivity()
    {
        Intent main=new Intent(newpost.this,MainActivity.class);
        startActivity(main);
    }

}

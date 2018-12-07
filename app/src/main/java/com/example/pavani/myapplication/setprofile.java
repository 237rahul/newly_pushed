package com.example.pavani.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class setprofile extends AppCompatActivity {
    private EditText fn;
    private CircleImageView pi;
    private Button u;
    private FirebaseAuth fa;
    private DatabaseReference dbr;
    String curr;
    private ProgressDialog pd;
    final static int gp=1;
    private StorageReference sr;
    String du;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setprofile);

        fa=FirebaseAuth.getInstance();
        curr=fa.getCurrentUser().getUid();
        dbr= FirebaseDatabase.getInstance().getReference().child("students").child(curr);
        sr= FirebaseStorage.getInstance().getReference().child("image_prof");

        fn=(EditText)findViewById(R.id.fullname);
        pi=(CircleImageView)findViewById(R.id.profileimage);
        u=(Button)findViewById(R.id.upload);
        pd=new ProgressDialog(this);

        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });

        pi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(setprofile.this);
            }
        });

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("prof_image"))
                    {
                        String im=dataSnapshot.child("prof_image").getValue().toString();
                        Picasso.get().load(im).placeholder(R.drawable.profile).into(pi);
                    }
                    else
                    {
                        Toast.makeText(setprofile.this,"please select profile image",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                                                pd.dismiss();
                                            }
                                            else
                                            {
                                                Toast.makeText(setprofile.this,"error"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
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

    private void uploadimage() {
        String fullname=fn.getText().toString();

        if (TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this,"please enter your name",Toast.LENGTH_SHORT).show();

        }
        else
        {
            pd.setTitle("saving details");
            pd.setMessage("please wait");
            pd.show();
            pd.setCanceledOnTouchOutside(true);

            HashMap um=new HashMap();
            um.put("Fullname",fullname);
            dbr.updateChildren(um).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        mainact();
                        pd.dismiss();
                    }
                    else
                    {
                        pd.dismiss();
                    }
                }
            });
        }
    }

    private void mainact() {
        Intent mi=new Intent(setprofile.this,MainActivity.class);
        mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mi);
        finish();
    }
}

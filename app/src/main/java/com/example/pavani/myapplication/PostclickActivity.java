package com.example.pavani.myapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostclickActivity extends AppCompatActivity
{
    private ImageView postimage;
    private TextView post_desscription;
    private Button delete_post,edit_post;
    private DatabaseReference clickposref;
    private FirebaseAuth uauth;

    private String poskey,curruser_id,dbuser_id,descript,img;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postclick);

        uauth=FirebaseAuth.getInstance();
        curruser_id=uauth.getCurrentUser().getUid();

        poskey=getIntent().getExtras().get("poskey").toString();
        clickposref=FirebaseDatabase.getInstance().getReference().child("posts12").child(poskey);

        postimage=(ImageView)findViewById(R.id.clickpostimage);
        post_desscription=(TextView)findViewById(R.id.clickpostdescription);
        delete_post=(Button)findViewById(R.id.clickdelete_post);
        edit_post=(Button)findViewById(R.id.clickedit_post);

        delete_post.setVisibility(View.INVISIBLE);
        edit_post.setVisibility(View.INVISIBLE);

        clickposref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
               if(dataSnapshot.exists())
               {
                   descript=dataSnapshot.child("description").getValue().toString();
                   img=dataSnapshot.child("postimage").getValue().toString();
                   dbuser_id=dataSnapshot.child("uid").getValue().toString();

                   post_desscription.setText(descript);
                   Picasso.get().load(img).into(postimage);
                   if(curruser_id.equals(dbuser_id))
                   {
                       delete_post.setVisibility(View.VISIBLE);
                       edit_post.setVisibility(View.VISIBLE);
                   }
                   edit_post.setOnClickListener(new View.OnClickListener()
                   {
                       @Override
                       public void onClick(View view)
                       {
                           Editcurrpost();
                       }
                   });
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        delete_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Delcurrentpost();
            }
        });

    }

    private void Editcurrpost()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(PostclickActivity.this);
        builder.setTitle("Edit Post: ");

        final EditText inpFeild=new EditText(PostclickActivity.this);
        inpFeild.setText(descript);
        builder.setView(inpFeild);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                clickposref.child("description").setValue(inpFeild.getText().toString());
                Toast.makeText(PostclickActivity.this, "Post has been Updated Sucessfully", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        Dialog dialog=builder.create();
        dialog.show();

    }

    private void Delcurrentpost()
    {
        clickposref.removeValue();
        mainact();
        Toast.makeText(this, "Post has been Deleted", Toast.LENGTH_SHORT).show();

    }

    private void mainact() {
        Intent mi=new Intent(PostclickActivity.this,MainActivity.class);
        mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mi);
        finish();
    }

}

package com.example.pavani.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private NavigationView navview;
    private DrawerLayout dralay;
    private RecyclerView poslis;
    private Toolbar tb;
    private ActionBarDrawerToggle abt;
    private FirebaseAuth fba;
    private DatabaseReference ur,pref;
    private CircleImageView npi;
    private TextView npn;
    String curr;
    private ImageButton anp;
    private  RecyclerView newpostrecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newpostrecycle=(RecyclerView)findViewById(R.id.mainrecycleview);
        newpostrecycle.setLayoutManager(new LinearLayoutManager(this));

        fba=FirebaseAuth.getInstance();
        curr=fba.getCurrentUser().getUid();
        ur= FirebaseDatabase.getInstance().getReference().child("students");
        pref=FirebaseDatabase.getInstance().getReference().child("posts12");

        tb=(Toolbar)findViewById(R.id.maintoolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("IuThink");

        anp=(ImageButton)findViewById(R.id.newpost);

        dralay=(DrawerLayout)findViewById(R.id.maindrawerlayout);
        abt=new ActionBarDrawerToggle(MainActivity.this,dralay,R.string.ON,R.string.OFF);
        dralay.addDrawerListener(abt);
        abt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navview=(NavigationView)findViewById(R.id.mainnavigationview);

        poslis=(RecyclerView)findViewById(R.id.mainrecycleview);
        poslis.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        poslis.setLayoutManager(linearLayoutManager);




        View navi=navview.inflateHeaderView(R.layout.navheader);
        npi=(CircleImageView)navi.findViewById(R.id.headerimage);
        npn=(TextView)navi.findViewById(R.id.headertextview);

        ur.child(curr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("Fullname"))
                    {
                        String fn=dataSnapshot.child("Fullname").getValue().toString();
                        npn.setText(fn);
                    }
                    if (dataSnapshot.hasChild("prof_image"))
                    {
                        String im=dataSnapshot.child("prof_image").getValue().toString();
                        Picasso.get().load(im).placeholder(R.drawable.profile).into(npi);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"profile image or username doesn't exist",Toast.LENGTH_SHORT).show();
                    }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectmenu(item);
                return false;
            }
        });

        anp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPost();
            }
        });
        DisplayallPosts();
        
    }

    private void DisplayallPosts()
    {
        FirebaseRecyclerOptions<Posts> options=
                new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(pref,Posts.class).build();
        FirebaseRecyclerAdapter<Posts,pvh> adapter=
                new FirebaseRecyclerAdapter<Posts, pvh>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull pvh holder, int position, @NonNull Posts model) {
                       final String poskey=getRef(position).getKey();
                      holder.un.setText(model.getFullusername());
                      holder.postdes.setText(model.getDescription());
                      holder.pd.setText(model.getDate());
                      holder.pt.setText(model.getTime());
                      Picasso.get().load(model.getProfileimage()).placeholder(R.drawable.profile).into(holder.image_prof);
//                      Log.e("--------------------",pref.toString());
                      Picasso.get().load(model.getPostimage()).into(holder.postim);
                      holder.postim.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              Intent postclickIntent=new Intent(MainActivity.this,PostclickActivity.class);
                              postclickIntent.putExtra("poskey",poskey);
                              startActivity(postclickIntent);
                          }
                      });


                    }

                    @NonNull
                    @Override
                    public pvh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.viewposts,parent,false);
                        pvh viewholder=new pvh(view);
                        return viewholder;
                    }
                };
        newpostrecycle.setAdapter(adapter);
        adapter.startListening();
    }

    public static class pvh extends RecyclerView.ViewHolder
    {
        TextView un,pt,pd,postdes;
        CircleImageView image_prof;
        ImageView postim;
        public pvh(@NonNull View mv)
        {
            super(mv);
            un=(TextView)mv.findViewById(R.id.postpage_username);
            image_prof=(CircleImageView)mv.findViewById(R.id.postpage_profileimg);
            pt=(TextView)mv.findViewById(R.id.post_time);
            pd=(TextView)mv.findViewById(R.id.post_date);
            postdes=(TextView)mv.findViewById(R.id.post_description);
            postim=(ImageView)mv.findViewById(R.id.post_image);
//            Log.e("---------------->",postim);
        }
    }

    private void NewPost() {
//        Log.e("===>>>", "onActivityResult: 8989" );
        Toast.makeText(this,"New post clicked hefre",Toast.LENGTH_LONG).show();
        Intent anpi=new Intent(MainActivity.this,newpost.class);
        startActivity(anpi);
    }

    private void selectmenu(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.newpost:
                NewPost();
                break;
            case R.id.profile:
                Profileactivity();
                break;
            case R.id.home:
                break;
            case R.id.following:
                break;
            case R.id.findfriends:
                FindPeople();
                break;
            case R.id.findcollege:
                break;
            case R.id.settings:
                Settingsactivity();
                break;
            case R.id.logout:
                fba.signOut();
                Login();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (abt.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected  void onStart()
    {
        super.onStart();
        FirebaseUser cu=fba.getCurrentUser();
        if(cu==null)
        {
            Login();
        }
        else
        {
            existence();
        }
    }

    private void existence() {
        final String cid=fba.getCurrentUser().getUid();

        ur.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(cid))
                {
                    setup();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setup() {
        Intent li=new Intent(MainActivity.this,setprofile.class);
        li.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(li);
        finish();
    }

    private void Login()
    {
        Intent li=new Intent(MainActivity.this,login.class);
        li.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(li);
        finish();
    }


    private void Settingsactivity()
    {
        Intent se=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(se);
    }

    private void Profileactivity()
    {
        Intent se=new Intent(MainActivity.this,Profile.class);
        startActivity(se);
    }

    private void FindPeople()
    {
        Intent se=new Intent(MainActivity.this,FindPeople.class);
        startActivity(se);
    }

}

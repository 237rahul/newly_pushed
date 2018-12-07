package com.example.pavani.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FindPeople extends AppCompatActivity
{

    private Toolbar tb;

    private ImageButton searchButton;
    private EditText searchText;
    private RecyclerView searchList;

    private DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        tb = (Toolbar) findViewById(R.id.findfriends_bar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find People");
        dbr = FirebaseDatabase.getInstance().getReference().child("students");
        searchList = (RecyclerView) findViewById(R.id.search_results);
        searchList.setHasFixedSize(true);
        searchList.setLayoutManager(new LinearLayoutManager(this));

        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchText = (EditText) findViewById(R.id.searchbox);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String searchthing = searchText.getText().toString();

                searching(searchthing);
            }
        });

    }

    private void searching(String searchthing)
    {
//        FirebaseRecyclerAdapter<searchpeople,FindpeopleViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<searchpeople, FindpeopleViewHolder>(searchpeople.class,R.layout.display_allusers,FindpeopleViewHolder.class,dbr)
//        {
//            @Override
//            protected void onBindViewHolder(@NonNull FindpeopleViewHolder holder, int position, @NonNull searchpeople model)
//            {
//
//            }
//
//            @NonNull
//            @Override
//            public FindpeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//            {
//                return null;
//            }
//        };


        FirebaseRecyclerOptions<searchpeople> options = new FirebaseRecyclerOptions.Builder<searchpeople>().setQuery(dbr, searchpeople.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<searchpeople, FindpeopleViewHolder>(options) {
            @Override
            public FindpeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.display_allusers, parent, false);

                return new FindpeopleViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(FindpeopleViewHolder holder, int position, searchpeople model) {
                // Bind the Chat object to the ChatHolder
                // ...
            }
        };




    }

    public static class FindpeopleViewHolder extends RecyclerView.ViewHolder
    {
        View mv;

        public FindpeopleViewHolder(View itemView)
        {
            super(itemView);
            mv = itemView;
        }
    }
}

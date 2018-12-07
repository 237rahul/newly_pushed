package com.example.pavani.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    private Button lb;
    private EditText ue,up;
    private TextView co;
    private FirebaseAuth fba;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        fba=FirebaseAuth.getInstance();

        co=(TextView)findViewById(R.id.createone);
        ue=(EditText)findViewById(R.id.email);
        up=(EditText)findViewById(R.id.password);
        lb=(Button)findViewById(R.id.loginbutton);
        pd=new ProgressDialog(this);

        co.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        lb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ul();
            }
        });
    }
    


    private void ul() {
        String uemail=ue.getText().toString();
        String upassword=up.getText().toString();

        if(TextUtils.isEmpty(uemail))
        {
            Toast.makeText(this,"please enter email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(upassword))
        {
            Toast.makeText(this,"please enter password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            pd.setTitle("logging");
            pd.setMessage("please wait");
            pd.show();
            pd.setCanceledOnTouchOutside(true);

            fba.signInWithEmailAndPassword(uemail,upassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                mainact();
                              Toast.makeText(login.this,"successful",Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            else
                            {
                                String m=task.getException().getMessage();
                                Toast.makeText(login.this,"Error"+m,Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        }
                    });
        }
    }

    private void mainact() {
        Intent mi=new Intent(login.this,MainActivity.class);
        mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mi);
        finish();
    }

    private void signup() {
        Intent ri=new Intent(login.this,Register.class);
        startActivity(ri);
        finish();
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = fba.getCurrentUser();

        if(currentUser != null)
        {
            mainact();
        }
    }
}

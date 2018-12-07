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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {
    private EditText ue,up,ucp;
    private Button fsu;
    private FirebaseAuth fa;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        fa=FirebaseAuth.getInstance();

        ue=(EditText)findViewById(R.id.email);
        up=(EditText)findViewById(R.id.password);
        ucp=(EditText)findViewById(R.id.confirmpassword);
        fsu=(Button)findViewById(R.id.finishsignup);
        pd=new ProgressDialog(this);

        fsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cna();
            }
        });
    }

    private void cna() {
        String nemail=ue.getText().toString();
        String npassword=up.getText().toString();
        String nconfirm=ucp.getText().toString();

        if(TextUtils.isEmpty(nemail))
        {
            Toast.makeText(this,"please enter email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(npassword))
        {
            Toast.makeText(this,"please enter password",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(nconfirm))
        {
            Toast.makeText(this,"please enter confirm password",Toast.LENGTH_SHORT).show();
        }
        else if(!npassword.equals(nconfirm))
        {
            Toast.makeText(this,"password and confirm password deoesn't match",Toast.LENGTH_SHORT).show();
        }
        else {
            pd.setTitle("creating account");
            pd.setMessage("please wait");
            pd.show();
            pd.setCanceledOnTouchOutside(true);

            fa.createUserWithEmailAndPassword(nemail,npassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                setact();
                                Toast.makeText(Register.this,"successful",Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            else
                            {
                                String m=task.getException().getMessage();
                                Toast.makeText(Register.this,"Error"+m,Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                            }
                        }
                    });
        }
    }


    private void setact() {
        Intent mi=new Intent(Register.this,setprofile.class);
        mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mi);
        finish();

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = fa.getCurrentUser();

        if(currentUser != null)
        {
           mainact();
        }
    }
    private void mainact() {
        Intent mi=new Intent(Register.this,MainActivity.class);
        mi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mi);
        finish();
    }

}

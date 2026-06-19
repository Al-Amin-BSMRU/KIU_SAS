//Login.java
package com.eid.codeprobro.kiu_sas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener{
    ProgressBar pb;
    EditText Password,email;
    TextView loginlink,forgetPassword;
    Button login;
    FirebaseAuth mAuth;boolean pass=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        Password=findViewById(R.id.Password);
        email=findViewById(R.id.email);
        mAuth=FirebaseAuth.getInstance();
        pb=findViewById(R.id.progressBar2);
        forgetPassword=findViewById(R.id.forgetPassword);
//        mailvar=findViewById(R.id.mailvar);
//        mailvar.setOnClickListener(this);

//        varbtn=findViewById(R.id.varbtn);
//        varbtn.setOnClickListener(this);
        loginlink=findViewById(R.id.loginlink);
        login=findViewById(R.id.login);
        login.setOnClickListener(this);
        loginlink.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        FirebaseUser fa=mAuth.getCurrentUser();

        if(mAuth.getCurrentUser()!=null&& (mAuth.getCurrentUser().isEmailVerified())){
            startActivity(new Intent(Login.this, showinfo.class));
            finish();
        }


    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.forgetPassword) {
            EditText ed=new EditText(v.getContext());
            AlertDialog.Builder ab=new AlertDialog.Builder(v.getContext());
            ab.setTitle("Reset Password?");
            ab.setMessage("Enter your Email address.to reset password");
            ab.setView(ed);//settignt the edit text on the alert box
            ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String mail=ed.getText().toString();

                    mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Login.this,"message sent successfully",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this,"fsiled to send email "+e.getMessage()+"occers",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            ab.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ab.create().show();
        }
        if(v.getId()==R.id.login){
            String ema,pas;
            ema=email.getText().toString();
            pas=Password.getText().toString();
            if(checkvalid(ema,pas)==true){
                pb.setVisibility(View.VISIBLE);
                firebase_call(ema,pas);
            }
        }
        if(v.getId()==R.id.loginlink){
            Intent in=new Intent(Login.this, showinfo.class);
            startActivity(in);
        }
    }
    public void firebase_call(String email,String password){
        String a=email;
        String b=password;
        mAuth.signInWithEmailAndPassword(a,b).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pb.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    FirebaseUser fb=mAuth.getCurrentUser();
                    if(fb.isEmailVerified()&&fb!=null) {
                        Toast.makeText(Login.this, "successfully Logged in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, showinfo.class));
                        pb.setVisibility(View.GONE);
                        finish();
                    }
                }
                else{
                    Toast.makeText(Login.this, "sorry " + task.getException().getMessage() + "error has occer:", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
            }
        });

    }

    public boolean checkvalid(String emil,String password){
        boolean boo=false;
        String em=emil;
        String pas=password;
        if(TextUtils.isEmpty(em)){
            Toast.makeText(this, "no any email have intered", Toast.LENGTH_SHORT).show();
            return boo;
        }
        if(!em.contains("@")){
            Toast.makeText(this, "no any @ email have intered", Toast.LENGTH_SHORT).show();
            return boo;
        }

        if(pas.isEmpty()){
            Toast.makeText(this,"no passwords entered",Toast.LENGTH_SHORT).show();
            return boo;
        }
        if(pas.length()<6){
            Toast.makeText(this, "password must be in 6 length", Toast.LENGTH_SHORT).show();
            return boo;
        }
        else{
            return true;
        }

    }
}
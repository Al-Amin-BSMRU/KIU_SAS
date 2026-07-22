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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener{
    ProgressBar pb;
    EditText Password,email,punchcard_no,Department;
    String dept;
    TextView loginlink,forgetPassword;
    Button login;
    FirebaseAuth mAuth;
    boolean boo=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        Password=findViewById(R.id.Password);
        email=findViewById(R.id.email);
        punchcard_no=findViewById(R.id.punchcard_no);
        Department=findViewById(R.id.Department);
        mAuth=FirebaseAuth.getInstance();
        pb=findViewById(R.id.progressBar2);
        Department=findViewById(R.id.Department);
        punchcard_no=findViewById(R.id.punchcard_no);
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
                            Toast.makeText(Login.this,"mail sent successfully",Toast.LENGTH_SHORT).show();
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
            String eml,pas,dep,pun;
            eml=email.getText().toString();
            pun=punchcard_no.getText().toString();
            dep=Department.getText().toString();
            pas=Password.getText().toString();

            if (vlaidation_check(eml, pas, pun, dep)) {
                upload_fb_auth(eml, pas, dep, pun);
            }
        }
        if(v.getId()==R.id.loginlink){
            Intent in=new Intent(Login.this, showinfo.class);
            startActivity(in);
        }
    }
    public void upload_fb_auth(String eml, String pass, final String dep, final String pun) {

        pb.setVisibility(View.VISIBLE);
        login.setEnabled(false);

        mAuth.signInWithEmailAndPassword(eml, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                pb.setVisibility(View.GONE);
                login.setEnabled(true);

                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("OX").child("UID_Mappings").child(uid);
                    dbRef.child("department").setValue(dep);
                    dbRef.child("punch").setValue(pun);
                    //update the emails passwords
                    DatabaseReference updatepass=FirebaseDatabase.getInstance().getReference("OX");
                    updatepass.child(dep).child("student").child(pun).child("password").setValue(pass);
                    Toast.makeText(Login.this, "successfully Loged in ", Toast.LENGTH_SHORT).show();

                    Intent in = new Intent(Login.this, showinfo.class);
                    in.putExtra("deprt", dep);
                    in.putExtra("punch", pun);
                    startActivity(in);
                    finish();
                } else {
//                    failed_custom_toast();
                    Toast.makeText(Login.this, "Failed to login: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean vlaidation_check(String em, String pas, String pun, String dpt) {
        if (em.isEmpty() || dpt.isEmpty() || pun.isEmpty() || pas.isEmpty()) {
            return boo;
        }
        if (!em.contains("@")) {
            Toast.makeText(this, "Wrong email! email must contains @", Toast.LENGTH_LONG).show();
            return boo;
        }
        if (dpt.contains(".") || dpt.contains("#") || dpt.contains("$") ||
                pun.contains(".") || pun.contains("#") || pun.contains("$")) {
            Toast.makeText(this, "Special characters (., #, $) are not allowed!", Toast.LENGTH_LONG).show();
            return boo;
        } else {
            return true;
        }
    }
//    public void firebase_call(String email,String password){
//        String a=email;
//        String b=password;
//        mAuth.signInWithEmailAndPassword(a,b).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                pb.setVisibility(View.GONE);
//
//                if (task.isSuccessful()) {
//                    FirebaseUser fb=mAuth.getCurrentUser();
//                    if(fb.isEmailVerified()&&fb!=null) {
//                        Toast.makeText(Login.this, "successfully Logged in", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(Login.this, showinfo.class));
//                        pb.setVisibility(View.GONE);
//                        finish();
//                    }
//                }
//                else{
//                    Toast.makeText(Login.this, "sorry " + task.getException().getMessage() + "error has occer:", Toast.LENGTH_SHORT).show();
//                    pb.setVisibility(View.GONE);
//                }
//            }
//        });
//
//    }
//
//    public boolean checkvalid(String emil,String password){
//        boolean boo=false;
//        String em=emil;
//        String pas=password;
//        if(TextUtils.isEmpty(em)){
//            Toast.makeText(this, "no any email have intered", Toast.LENGTH_SHORT).show();
//            return boo;
//        }
//        if(!em.contains("@")){
//            Toast.makeText(this, "no any @ email have intered", Toast.LENGTH_SHORT).show();
//            return boo;
//        }
//
//        if(pas.isEmpty()){
//            Toast.makeText(this,"no passwords entered",Toast.LENGTH_SHORT).show();
//            return boo;
//        }
//        if(pas.length()<6){
//            Toast.makeText(this, "password must be in 6 length", Toast.LENGTH_SHORT).show();
//            return boo;
//        }
//        else{
//            return true;
//        }
//
//    }
}
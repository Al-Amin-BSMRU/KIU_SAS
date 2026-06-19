package com.eid.codeprobro.kiu_sas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class showinfo extends AppCompatActivity implements View.OnClickListener {

    TextView University_Name, Name, id, Email, Password, Depertment, session, punchcard_no;
    Button register, schadule, logoutBtn;

    // ডাটা হোল্ড করার জন্য গ্লোবাল ভেরিয়েবল
    String uni_name, name, idi, email, password, depertment, recsession, apunchid;
    DatabaseReference db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.showinfo);

        mAuth = FirebaseAuth.getInstance();

        // হার্ডকোডেড ইউনিভার্সিটি নেম ব্যাকআপ হিসেবে (দরকার হলে ডাইনামিক হবে)
        uni_name = "OX";

        // XML ভিউ আইডি কানেক্ট করা
        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        session = findViewById(R.id.session); // 🎯 স্টুডেন্টের সেশন ফিল্ড
        punchcard_no = findViewById(R.id.punchcard_no);

        register = findViewById(R.id.register);
        logoutBtn = findViewById(R.id.logoutBtn); // যদি এক্সএমএল-এ লগআউট বাটন থাকে

        register.setOnClickListener(this);
        if (logoutBtn != null) logoutBtn.setOnClickListener(this);

        Intent in = getIntent();

        // ১. রেজিস্ট্রেশন পেজ থেকে সরাসরি সব ডাটা ইনটেন্টে চলে আসলে
        if (in != null && in.getStringExtra("name") != null) {
            name = in.getStringExtra("name");
            idi = in.getStringExtra("id");
            email = in.getStringExtra("email");
            password = in.getStringExtra("password");
            depertment = in.getStringExtra("depertment");
            recsession = in.getStringExtra("session");
            apunchid = in.getStringExtra("punch");
            if(in.getStringExtra("uni_name") != null) {
                uni_name = in.getStringExtra("uni_name");
            }

            displayUiData();
        }
        // ২. ম্যানুয়াল লগইন পেজ থেকে শুধুমাত্র ডিপার্টমেন্ট এবং পাঞ্চ আইডি আসলে
        else if (in != null && in.getStringExtra("dept") != null) {
            String dept = in.getStringExtra("dept");
            apunchid = in.getStringExtra("pun");
            fetchStudentData(dept, apunchid);
        }
        // ৩. অ্যাপে অলরেডি ইউজার লগইন থাকা অবস্থায় প্রোফাইল ওপেন করলে (UID_Mappings থেকে ডাটা রিড)
        else if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference(uni_name).child("UID_Mappings").child(uid);

            uidRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String dept = snapshot.child("department").getValue(String.class);
                        String punch = snapshot.child("punch").getValue(String.class);

                        if (dept != null && punch != null) {
                            fetchStudentData(dept, punch);
                        }
                    } else {
                        Toast.makeText(showinfo.this, "Session Expired! Please login again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    // 🎯 ডাটাবেস থেকে স্টুডেন্টের রিয়েল-টাইম ডাটা তুলে এনে ম্যাপ করার মেথড
    private void fetchStudentData(String dept, String punch) {
        db = FirebaseDatabase.getInstance().getReference(uni_name);

        // 🎯 পাথ পরিবর্তন করে "student" করা হয়েছে এবং Login_Model ম্যাপ করা হয়েছে
        db.child(dept).child("student").child(punch).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Login_Model student_info = snapshot.getValue(Login_Model.class);
                    if (student_info != null) {
                        // আপনার মডেল ক্লাসের ভেরিয়েবল অনুসারে ডাটা অ্যাসাইন (যেমন: nam_ses_rol)
                        name = student_info.nam_ses_rol;
                        idi = student_info.id;
                        email = student_info.email;
                        password = student_info.password;
                        depertment = dept; // প্যারামিটার থেকে প্রাপ্ত ডিপার্টমেন্ট
                        recsession = student_info.session; // 🎯 মডেল থেকে সেশন রিড
                        apunchid = student_info.punch;

                        displayUiData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // 🎯 স্ক্রিনে ডাটা সেট করার মেথড (সেশন সহ)
    private void displayUiData() {
        University_Name.setText(uni_name);
        punchcard_no.setText(apunchid);
        Name.setText(name);
        id.setText(idi);
        Email.setText(email);
        Password.setText(password);
        Depertment.setText(depertment);
        session.setText(recsession); // 🎯 টেক্সটভিউতে সেশন শো করা হলো
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            showLogoutConfirmationDialog();
        }
        if (logoutBtn != null && v.getId() == R.id.logoutBtn) {
            showLogoutConfirmationDialog();
        }
    }

    // 🎯 লগআউট এবং অ্যাকাউন্ট সুইচের পপআপ ডায়ালগ মেথড
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(showinfo.this);
        builder.setTitle("Action Required");
        builder.setMessage("You are currently logged in. What would you like to do?");
        builder.setCancelable(true);

        builder.setPositiveButton("Logout & Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                Intent in = new Intent(showinfo.this, Register_page.class);
                startActivity(in);
                Toast.makeText(showinfo.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNeutralButton("Switch to Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                Intent in = new Intent(showinfo.this, Login.class); // আপনার প্রজেক্টের লগইন ক্লাস নেম দিন
                startActivity(in);
                Toast.makeText(showinfo.this, "Logged out! Please login again", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
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

    String uni_name, name, idi, email, password, depertment, recsession, apunchid;
    DatabaseReference db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.showinfo);

        mAuth = FirebaseAuth.getInstance();

        uni_name = "OX";

        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        session = findViewById(R.id.session);
        punchcard_no = findViewById(R.id.punchcard_no);

        register = findViewById(R.id.register);
        logoutBtn = findViewById(R.id.logoutBtn);

        register.setOnClickListener(this);
        if (logoutBtn != null) logoutBtn.setOnClickListener(this);

        Intent in = getIntent();

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
        else if (in != null && in.getStringExtra("dept") != null) {
            String dept = in.getStringExtra("dept");
            apunchid = in.getStringExtra("pun");
            fetchStudentData(dept, apunchid);
        }
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

    // datasnapsot
    private void fetchStudentData(String dept, String punch) {
        db = FirebaseDatabase.getInstance().getReference(uni_name);

        // show info students
        db.child(dept).child("student").child(punch).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Login_Model student_info = snapshot.getValue(Login_Model.class);
                    if (student_info != null) {
                        // details fetch
                        name = student_info.name;
                        idi = student_info.id;
                        email = student_info.email;
                        password = student_info.password;
                        depertment = dept;
                        recsession = student_info.session;
                        apunchid = student_info.punch;

                        displayUiData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // shows datasssss
    private void displayUiData() {
        University_Name.setText(uni_name);
        punchcard_no.setText(apunchid);
        Name.setText(name);
        id.setText(idi);
        Email.setText(email);
        Password.setText(password);
        Depertment.setText(depertment);
        session.setText(recsession);
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

    //pop up dialogs
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
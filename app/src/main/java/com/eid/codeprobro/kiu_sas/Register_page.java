package com.eid.codeprobro.kiu_sas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register_page extends AppCompatActivity implements View.OnClickListener {

    EditText University_Name, Name, id, Email, Password, Depertment, session, punchcard_no;
    Button Create;
    TextView registerlink;
    DatabaseReference db;
    public FirebaseAuth mAuth;

    private boolean isWaitingForVerification = false;
    private String backupUniName, backupName, backupId, backupEmail, backupPassword, backupDept, backupSession, backupPunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_page);

        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        session = findViewById(R.id.session);
        punchcard_no = findViewById(R.id.punchcard_no);
        Create = findViewById(R.id.Create);
        registerlink = findViewById(R.id.registerlink);

        Create.setOnClickListener(this);
        registerlink.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            Intent in = new Intent(Register_page.this, showinfo.class);
            startActivity(in);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isWaitingForVerification) {
            checkVerifiedAutomatic();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Create) {
            String uni_name = this.University_Name.getText().toString().trim().toUpperCase().replace(" ", "_");
            String rawname = this.Name.getText().toString().trim();
            String rawidd = this.id.getText().toString().trim();
            String email = this.Email.getText().toString().trim();
            String password = this.Password.getText().toString().trim();
            String depertment = this.Depertment.getText().toString().trim().toUpperCase().replace(" ", "_");
            String sess = this.session.getText().toString().trim();
            String punch = this.punchcard_no.getText().toString().trim().toUpperCase();

            if (uni_name.isEmpty() || rawname.isEmpty() || rawidd.isEmpty() || punch.isEmpty() || password.isEmpty() || depertment.isEmpty() || sess.isEmpty()) {
                fillinfo();
                return;
            }
            if (rawidd.length() != 12) {
                Toast.makeText(this, "Wrong ID! ID must be exactly 12 digits long", Toast.LENGTH_LONG).show();
                return;
            }
            if (uni_name.contains(".") || uni_name.contains("#") || uni_name.contains("$") ||
                    depertment.contains(".") || depertment.contains("#") || depertment.contains("$") ||
                    punch.contains(".") || punch.contains("#") || punch.contains("$")) {
                Toast.makeText(this, "Special characters (., #, $) are not allowed!", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                String standardizedSession = sess.trim();
                if (standardizedSession.contains("-")) {
                    String[] parts = standardizedSession.split("-");
                    String firstPart = parts[0].trim();
                    if (firstPart.length() >= 4) {
                        standardizedSession = firstPart.substring(firstPart.length() - 2);
                    } else {
                        standardizedSession = firstPart;
                    }
                } else if (standardizedSession.length() >= 4) {
                    standardizedSession = standardizedSession.substring(standardizedSession.length() - 2);
                }
                final String finalSession = standardizedSession;

                backupUniName = uni_name;
                backupName = rawname;
                backupId = rawidd;
                backupEmail = email;
                backupPassword = password;
                backupDept = depertment;
                backupSession = finalSession;
                backupPunch = punch;

                String name = rawname.substring(0, 1).toUpperCase() + rawname.substring(1).toLowerCase();
                String idd = rawidd.substring(0, 6);
                String roll = rawidd.substring(rawidd.length() - 2);
                String nam_ses_rol = name + "_" + idd + "__" + roll;

                Login_Model ln = new Login_Model(nam_ses_rol, punch, rawidd, rawname, uni_name, depertment, finalSession, email, password);

                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser fa = mAuth.getCurrentUser();
                        if (fa != null) {
                            db = FirebaseDatabase.getInstance().getReference(uni_name);
                            String uid = fa.getUid();
                            db.child("UID_Mappings").child(uid).child("department").setValue(depertment);
                            db.child("UID_Mappings").child(uid).child("punch").setValue(punch);

                            db.child(depertment).child("student").child(punch).setValue(ln).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    db.child("Card_Mappings").child(punch).setValue(depertment);
                                    fa.sendEmailVerification().addOnSuccessListener(unused -> {
                                        Toast.makeText(Register_page.this, "Email sent! Please check inbox.", Toast.LENGTH_LONG).show();
                                        Create.setVisibility(View.GONE);
                                        isWaitingForVerification = true;
                                    });
                                }
                            });
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(Register_page.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (v.getId() == R.id.registerlink) {
            Intent in = new Intent(Register_page.this, Login.class);
            startActivity(in);
            finish();
        }
    }

    public void checkVerifiedAutomatic() {
        FirebaseUser fa = mAuth.getCurrentUser();
        if (fa != null) {
            fa.reload().addOnSuccessListener(unused -> {
                if (fa.isEmailVerified()) {
                    isWaitingForVerification = false;
                    Intent in = new Intent(Register_page.this, showinfo.class);
                    in.putExtra("name", backupName);
                    in.putExtra("id", backupId);
                    in.putExtra("email", backupEmail);
                    in.putExtra("password", backupPassword);
                    in.putExtra("depertment", backupDept);
                    in.putExtra("session", backupSession);
                    in.putExtra("punch", backupPunch);
                    in.putExtra("uni_name", backupUniName);
                    startActivity(in);
                    finish();
                }
            });
        }
    }

    public void success_custom_toast() {
        LayoutInflater lf = getLayoutInflater();
        View v = lf.inflate(R.layout.success_custom_toast, (ViewGroup) findViewById(R.id.cstm_toast));
        Toast tt = new Toast(getApplicationContext());
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setView(v);
        tt.show();
    }

    public void fillinfo() {
        LayoutInflater li = getLayoutInflater();
        View v = li.inflate(R.layout.fill_info, (ViewGroup) findViewById(R.id.fill_cust));
        Toast tt = new Toast(getApplicationContext());
        tt.setDuration(Toast.LENGTH_SHORT);
        tt.setView(v);
        tt.show();
    }

    public void failed_custom_toast() {
        LayoutInflater lf = getLayoutInflater();
        View v = lf.inflate(R.layout.failed_custom_toast, (ViewGroup) findViewById(R.id.failed_cstm_toast));
        Toast tt = new Toast(getApplicationContext());
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setView(v);
        tt.show();
    }
}

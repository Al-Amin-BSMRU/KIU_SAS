//package com.eid.codeprobro.kiu_tas;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.eid.codeprobro.kiu_sas.Login; // 🎯 ফিক্স: লগইন পেজে রিডাইরেক্টের জন্য সঠিক ইমপোর্ট
//import com.eid.codeprobro.kiu_sas.Login_Model;
//import com.eid.codeprobro.kiu_sas.R;
//import com.eid.codeprobro.kiu_sas.showinfo;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class Register extends AppCompatActivity implements View.OnClickListener {
//
//    EditText University_Name, Name, id, Email, Password, Depertment, session, punchcard_no;
//    Button Create;
//    DatabaseReference db;
//    FirebaseAuth mAuth;
//    TextView registerlink;
//    ProgressBar regProgressBar, progressBar5; // 🎯 ফিক্স: ভেরিয়েবল নাম সঠিকভাবে ডিক্লেয়ার করা হলো
//    private boolean isWaitingForVerification = false;
//
//    private String intentName, intentId, intentEmail, intentPassword, intentDept, intentSession, intentPunch, intentUni;
//
//    public void success_custom_toast() {
//        LayoutInflater lf = getLayoutInflater();
//        View v = lf.inflate(R.layout.success_custom_toast, (ViewGroup) findViewById(R.id.cstm_toast));
//        Toast tt = new Toast(getApplicationContext());
//        tt.setDuration(Toast.LENGTH_LONG);
//        tt.setGravity(Gravity.BOTTOM, 0, 0);
//        tt.setView(v);
//        tt.show();
//    }
//
//    public void fillinfo() {
//        LayoutInflater li = getLayoutInflater();
//        View v = li.inflate(R.layout.fill_info, (ViewGroup) findViewById(R.id.fill_cust));
//        Toast tt = new Toast(getApplicationContext());
//        tt.setDuration(Toast.LENGTH_SHORT);
//        tt.setGravity(Gravity.BOTTOM, 0, 0);
//        tt.setView(v);
//        tt.show();
//    }
//
//    public void failed_custom_toast() {
//        LayoutInflater lf = getLayoutInflater();
//        View v = lf.inflate(R.layout.failed_custom_toast, (ViewGroup) findViewById(R.id.failed_cstm_toast));
//        Toast tt = new Toast(getApplicationContext());
//        tt.setDuration(Toast.LENGTH_LONG);
//        tt.setGravity(Gravity.BOTTOM, 0, 0);
//        tt.setView(v);
//        tt.show();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.register_page);
//
//        mAuth = FirebaseAuth.getInstance();
//        University_Name = findViewById(R.id.University_Name);
//        Name = findViewById(R.id.Name);
//        id = findViewById(R.id.id);
//        Email = findViewById(R.id.Email);
//        Password = findViewById(R.id.Password);
//        Depertment = findViewById(R.id.Depertment);
//        session = findViewById(R.id.session);
//        punchcard_no = findViewById(R.id.punchcard_no);
//        Create = findViewById(R.id.Create);
//        registerlink = findViewById(R.id.registerlink);
//
//        // 🎯 ফিক্স: XML আইডির সাথে গ্লোবাল ভেরিয়েবল ম্যাচ করা হলো
//        regProgressBar = findViewById(R.id.progressBar);
//        progressBar5 = findViewById(R.id.progressBar5);
//        if (progressBar5 != null) {
//            progressBar5.setVisibility(View.GONE); // এক্সএমএল এর বাড়তি প্রগ্রেসবার হাইড করে রাখা হলো
//        }
//
//        registerlink.setOnClickListener(this);
//        Create.setOnClickListener(this);
//
//        // লগইন স্ট্যাটাস চেক
//        if (mAuth.getCurrentUser() != null) {
//            if (mAuth.getCurrentUser().isEmailVerified()) {
//                Intent in = new Intent(Register.this, showinfo.class);
//                Toast.makeText(Register.this, "Welcome back", Toast.LENGTH_SHORT).show();
//                startActivity(in);
//                finish();
//            } else {
//                isWaitingForVerification = true;
//                Create.setVisibility(View.GONE);
//                Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isWaitingForVerification) {
//            checkVerifiedAutomatic();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.registerlink) {
//            // 🎯 ফিক্স: প্রোফাইল পেজের বদলে অলরেডি অ্যাকাউন্ট থাকলে 'Login' পেজে যাবে
//            Intent in = new Intent(Register.this, Login.class);
//            startActivity(in);
//            finish();
//        }
//        if (v.getId() == R.id.Create) {
//            String uni_name = this.University_Name.getText().toString().trim().toUpperCase().replace(" ", "_");
//            String rawname = this.Name.getText().toString().trim();
//            String rawidd = this.id.getText().toString().trim();
//            String email = this.Email.getText().toString().trim();
//            String password = this.Password.getText().toString().trim();
//            String depertment = this.Depertment.getText().toString().trim().toUpperCase().replace(" ", "_");
//            String sess = this.session.getText().toString().trim();
//            String punch = this.punchcard_no.getText().toString().trim().toUpperCase();
//
//            if (vlaidation_check(uni_name, rawname, rawidd, email, password, depertment, sess, punch)) {
//                startRegistrationProcess(uni_name, rawname, rawidd, email, password, depertment, sess, punch);
//            }
//        }
//    }
//
//    public void checkVerifiedAutomatic() {
//        FirebaseUser fu = mAuth.getCurrentUser();
//        if (fu != null) {
//            regProgressBar.setVisibility(View.VISIBLE);
//            fu.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void unused) {
//                    FirebaseUser updatedUser = mAuth.getCurrentUser();
//
//                    if (updatedUser != null && updatedUser.isEmailVerified()) {
//                        regProgressBar.setVisibility(View.GONE);
//                        Toast.makeText(Register.this, "Verified Successfully!", Toast.LENGTH_SHORT).show();
//                        isWaitingForVerification = false;
//
//                        Intent in = new Intent(Register.this, showinfo.class);
//                        in.putExtra("name", intentName);
//                        in.putExtra("id", intentId);
//                        in.putExtra("email", intentEmail);
//                        in.putExtra("password", intentPassword);
//                        in.putExtra("depertment", intentDept);
//                        in.putExtra("session", intentSession);
//                        in.putExtra("punch", intentPunch);
//                        in.putExtra("uni_name", intentUni);
//
//                        startActivity(in);
//                        finish();
//                    } else {
//                        regProgressBar.setVisibility(View.GONE);
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    regProgressBar.setVisibility(View.GONE);
//                    Toast.makeText(Register.this, "Status Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    public boolean vlaidation_check(String uni, String rwnam, String rwid, String eml, String pas, String dpt, String ses, String pun) {
//        if (uni.isEmpty() || rwnam.isEmpty() || rwid.isEmpty() || eml.isEmpty() || pas.isEmpty() || dpt.isEmpty() || ses.isEmpty() || pun.isEmpty()) {
//            fillinfo(); // কাস্টম ফিল টোস্ট শো করা হলো
//            return false;
//        }
//        if (rwid.length() != 12) {
//            Toast.makeText(this, "Wrong ID! ID must be exactly 12 digits long", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        if (pas.length() < 6) {
//            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (uni.contains(".") || uni.contains("#") || uni.contains("$") ||
//                dpt.contains(".") || dpt.contains("#") || dpt.contains("$") ||
//                pun.contains(".") || pun.contains("#") || pun.contains("$")) {
//            Toast.makeText(this, "Special characters (., #, $) are not allowed!", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        return true;
//    }
//
//    public void startRegistrationProcess(String uni_name, String rawname, String rawidd, String email, String password, String depertment, String sess, String punch) {
//
//        regProgressBar.setVisibility(View.VISIBLE);
//        Create.setEnabled(false);
//
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
//            if (authTask.isSuccessful()) {
//                try {
//                    // সেশন স্ট্যান্ডার্ডাইজেশন (যেমন: 2021 -> 21)
//                    String standardizedSession = sess.trim();
//                    if (standardizedSession.contains("-")) {
//                        String[] parts = standardizedSession.split("-");
//                        String firstPart = parts[0].trim();
//                        if (firstPart.length() >= 4) {
//                            standardizedSession = firstPart.substring(firstPart.length() - 2);
//                        } else {
//                            standardizedSession = firstPart;
//                        }
//                    } else if (standardizedSession.length() >= 4) {
//                        standardizedSession = standardizedSession.substring(standardizedSession.length() - 2);
//                    }
//                    final String finalSession = standardizedSession;
//
//                    String name = rawname.substring(0, 1).toUpperCase() + rawname.substring(1).toLowerCase();
//                    String idd = rawidd.substring(0, 6);
//                    String roll = rawidd.substring(rawidd.length() - 2);
//                    String nam_ses_rol = name + "_" + idd + "__" + roll;
//
//                    intentName = name;
//                    intentId = rawidd;
//                    intentEmail = email;
//                    intentPassword = password;
//                    intentDept = depertment;
//                    intentSession = sess; // সম্পূর্ণ অরিজিনাল সেশনটিই রাখছি মডেল ক্লাসের জন্য
//                    intentPunch = punch;
//                    intentUni = uni_name;
//
//                    // 🎯 মডেল ক্লাসের সাথে ভ্যালু ম্যাপিং (Login_Model এর ৯টি প্যারামিটার অনুযায়ী)
//                    Login_Model ln = new Login_Model(nam_ses_rol, punch, rawidd, name, uni_name, depertment, sess, email, password);
//                    db = FirebaseDatabase.getInstance().getReference(uni_name);
//
//                    String uid = mAuth.getCurrentUser().getUid();
//                    db.child("UID_Mappings").child(uid).child("department").setValue(depertment);
//                    db.child("UID_Mappings").child(uid).child("punch").setValue(punch);
//
//                    // 🎯 স্টুডেন্ট নোডে ডাটা রাইট করা হচ্ছে
//                    db.child(depertment).child("student").child(punch).setValue(ln).addOnCompleteListener(dbTask -> {
//                        if (dbTask.isSuccessful()) {
//                            db.child("Card_Mappings").child(punch).setValue(depertment);
//                            success_custom_toast(); // সফলতার কাস্টম টোস্ট
//
//                            FirebaseUser fu = mAuth.getCurrentUser();
//                            if (fu != null) {
//                                fu.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        regProgressBar.setVisibility(View.GONE);
//                                        Toast.makeText(Register.this, "Verification email sent! Check inbox.", Toast.LENGTH_LONG).show();
//                                        Create.setVisibility(View.GONE);
//                                        isWaitingForVerification = true;
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        regProgressBar.setVisibility(View.GONE);
//                                        Create.setEnabled(true);
//                                        Toast.makeText(Register.this, "Mail failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        } else {
//                            regProgressBar.setVisibility(View.GONE);
//                            Create.setEnabled(true);
//                            Toast.makeText(Register.this, "Failed to save data to Realtime DB", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                } catch (Exception e) {
//                    regProgressBar.setVisibility(View.GONE);
//                    Create.setEnabled(true);
//                    Toast.makeText(Register.this, "Data parsing error!", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            } else {
//                regProgressBar.setVisibility(View.GONE);
//                Create.setEnabled(true);
//                failed_custom_toast();
//                Toast.makeText(Register.this, "Auth Failed: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//}
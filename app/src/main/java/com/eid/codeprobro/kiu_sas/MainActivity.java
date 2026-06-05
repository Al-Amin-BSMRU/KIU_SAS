package com.eid.codeprobro.kiu_sas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText University_Name, Name, id, Email, Password, Depertment, session, punchcard_no;
    Button Create;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        session = findViewById(R.id.session);
        punchcard_no = findViewById(R.id.punchcard_no);
        Create = findViewById(R.id.Create);

        Create.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, imeInsets.bottom);
            return windowInsets;
        });
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
                Toast.makeText(this, "Please fill all info!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rawidd.length() < 12) {
                Toast.makeText(this, "Wrong ID! ID must be 12 digits long", Toast.LENGTH_LONG).show();
                return;
            }

            if (uni_name.contains(".") || uni_name.contains("#") || uni_name.contains("$") ||
                    depertment.contains(".") || depertment.contains("#") || depertment.contains("$") ||
                    punch.contains(".") || punch.contains("#") || punch.contains("$")) {
                Toast.makeText(this, "Special characters (., #, $) are not allowed in system IDs!", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                String standardizedSession = sess.trim();

                try {
                    // ১. যদি সেশনে হাইফেন থাকে (যেমন: "2021-22" বা "21-22")
                    if (standardizedSession.contains("-")) {
                        String[] parts = standardizedSession.split("-");
                        String firstPart = parts[0].trim();

                        if (firstPart.length() >= 4) {
                            // "2021" থেকে শেষের ২ ডিজিট "21" কেটে নেওয়া হচ্ছে
                            standardizedSession = firstPart.substring(firstPart.length() - 2);
                        } else {
                            standardizedSession = firstPart; // যদি অলরেডি "21" থাকে
                        }
                    }
                    // ২. যদি সেশন পুরো ৪ ডিজিটের বছর হয় (যেমন: "2021")
                    else if (standardizedSession.length() >= 4) {
                        standardizedSession = standardizedSession.substring(standardizedSession.length() - 2); // "21"
                    }
                    // ৩. যদি সেশন অলরেডি ২ ডিজিটের হয় (যেমন: "21"), তবে সেটি যা আছে তাই থাকবে
                } catch (Exception e) {
                    standardizedSession = sess; // কোনো এরর হলে অরিজিনালটাই ব্যাকআপ থাকবে
                }

                // 🛠️ ল্যাম্বডা লিসেনারের জন্য ফাইনাল ভেরিয়েবল তৈরি (এখন এটি সবসময় "21" বা "22" ফরম্যাটে থাকবে)
                final String finalSession = standardizedSession;

                // 🛠️ আইডি থেকে সাবস্ট্রিং লজিক
                String idd = rawidd.substring(0, 6);
                String roll = rawidd.substring(rawidd.length() - 2);

                // নাম কম্বিনেশন তৈরি
                String nam_ses_rol = rawname + "_" + idd + "__" + roll;

                // Login অবজেক্ট তৈরি
                Login ln = new Login(nam_ses_rol, punch, email, password, finalSession, rawidd);

                db = FirebaseDatabase.getInstance().getReference(uni_name);

                db.child(depertment).child("student").child(punch).setValue(ln).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.child("Card_Mappings").child(punch).setValue(depertment);
                        Toast.makeText(MainActivity.this, "Registration Success!", Toast.LENGTH_SHORT).show();

                        Intent in = new Intent(MainActivity.this, showinfo.class);
                        in.putExtra("name", rawname);
                        in.putExtra("id", rawidd);
                        in.putExtra("email", email);
                        in.putExtra("password", password);
                        in.putExtra("depertment", depertment);
                        in.putExtra("session", finalSession); // "21" বা "22" পাস হবে
                        in.putExtra("punch", punch);
                        in.putExtra("uni_name", uni_name);

                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(this, "An error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
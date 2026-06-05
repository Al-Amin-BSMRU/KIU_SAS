package com.eid.codeprobro.kiu_sas;

// ফিক্স ১: ভুল স্ট্যাটিক ইমপোর্টটি এখান থেকে সরিয়ে দেওয়া হয়েছে।
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class showinfo extends AppCompatActivity implements View.OnClickListener {
    TextView University_Name, Name, id, Email, Password, Depertment, session, punchcard_no;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.showinfo);

        // ভিউ আইডিগুলো কানেক্ট করা
        University_Name = findViewById(R.id.University_Name);
        Name = findViewById(R.id.Name);
        id = findViewById(R.id.id);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Depertment = findViewById(R.id.Depertment);
        session = findViewById(R.id.session);
        punchcard_no = findViewById(R.id.punchcard_no);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        // ফিক্স ২: এখন এই getIntent() একদম পারফেক্টলি মেইন অ্যাক্টিভিটির ডাটা রিসিভ করবে
        Intent in = getIntent();
        if (in != null) {
            String recname = in.getStringExtra("name");
            String recid = in.getStringExtra("id");
            String recemail = in.getStringExtra("email");
            String recpassword = in.getStringExtra("password");
            String recdepertment = in.getStringExtra("depertment");
            String recsession = in.getStringExtra("session");
            String recpunch = in.getStringExtra("punch");
            String reuni = in.getStringExtra("uni_name");

            // ভিউতে ডাটা সেট করা
            University_Name.setText(reuni);
            Name.setText(recname); // (নোট: এখানে আপনি recid দিয়েছিলেন, নামের জায়গায় recname দেওয়া হলো)
            id.setText(recid);
            Email.setText(recemail);
            Password.setText(recpassword);
            Depertment.setText(recdepertment);
            session.setText(recsession);
            punchcard_no.setText(recpunch);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            Intent in = new Intent(showinfo.this, MainActivity.class);
            startActivity(in);
            finish(); // এই পেজটি বন্ধ করে দেওয়া ভালো প্র্যাকটিস
        }
    }
}
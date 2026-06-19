package com.eid.codeprobro.kiu_sas;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Login_Model {
    public String nam_ses_rol;
    public String punch;
    public String id;
    public String name;
    public String university;
    public String dept;
    public String session;
    public String email;
    public String password;

    // Required empty constructor for Firebase
    public Login_Model() {
    }

    public Login_Model(String nam_ses_rol, String punch, String id, String name, String university, String dept, String session, String email, String password) {
        this.nam_ses_rol = nam_ses_rol;
        this.punch = punch;
        this.id = id;
        this.name = name;
        this.university = university;
        this.dept = dept;
        this.session = session;
        this.email = email;
        this.password = password;
    }

    // Getters for showinfo.java and others
    public String getName() { return name; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDept() { return dept; }
    public String getSession() { return session; }
    public String getPuid() { return punch; }
    public String getUniversity() { return university; }
}
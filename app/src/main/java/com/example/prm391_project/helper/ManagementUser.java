package com.example.prm391_project.helper;


import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Toast;

import com.example.prm391_project.view.LoginActivity;
import com.example.prm391_project.view.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ManagementUser {
    private final Context context;
    private final TinyDB tinyDB;


    public ManagementUser(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void saveUser(String email, String password) {
        this.tinyDB.putString("email", email);
        this.tinyDB.putString("password", password);
    }

    public String getEmail() {
        return this.tinyDB.getString("email");
    }

    public String getPassword() {
        return this.tinyDB.getString("password");
    }

    public boolean userSaved() {
        return !this.tinyDB.getString("password").isEmpty();
    }
}

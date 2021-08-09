package com.example.clinicapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clinicapplication.info.calendier;
import com.example.clinicapplication.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;
public class splash extends AppCompatActivity {
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        timer = new Timer();
        timer.schedule (new TimerTask (){
            @Override
            public void run() {

                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent Intent = new Intent(splash.this, MainActivity.class);
                    Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(Intent);
                    finish();
                }else {

                    updateUI(FirebaseAuth.getInstance().getCurrentUser());

                }
            }
        }, 3000);
    }
    private void updateUI(final FirebaseUser currentUser) {
        if(currentUser!=null){
            try {
                FirebaseFirestore.getInstance().collection("User").document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user=documentSnapshot.toObject(User.class);
                            if(user.getType().equals("Patient")){
                                //////////////////////patient
                                Intent k = new Intent(splash.this, HomeActivity.class);
                                k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                calendier.name_chat = user.getName();
                                finish();
                                startActivity(k);
                            }else{
                                Intent k = new Intent(splash.this, DoctorHomeActivity.class);
                                k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                calendier.name_chat = user.getName();

                                finish();
                                startActivity(k);
                                //Snackbar.make(findViewById(R.id.main_layout), "Doctor interface entraint de realisation", Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        else {
                            Intent k = new Intent(splash.this, signinactivity.class);
                            startActivity(k);
                        }
                    }
                });
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    }

}

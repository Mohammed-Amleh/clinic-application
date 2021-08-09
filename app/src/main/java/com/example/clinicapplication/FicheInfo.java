package com.example.clinicapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinicapplication.info.calendier;
import com.example.clinicapplication.model.Fiche;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class FicheInfo extends AppCompatActivity {
    TextView disease_sheet_1 ,fiche_description_1, fiche_traitement_1;
    Spinner fiche_type_spinner_1;
    Button button_add_edit;
    DocumentReference reference;
    String txt_diesase;
    String txt_fiche_description_1;
    String txt_fiche_traitement_1;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_info);

        disease_sheet_1=findViewById(R.id.disease_sheet_1);
        fiche_description_1=findViewById(R.id.fiche_description_1);
        fiche_traitement_1=findViewById(R.id.fiche_traitement_1);
        fiche_type_spinner_1 = findViewById(R.id.fiche_type_spinner_1);
        button_add_edit = findViewById(R.id.button_add_edit);

        String patient_id = calendier.patient_id;
        String id = getIntent().getStringExtra("id");


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reference = db.collection("Patient").document(patient_id).collection("MyMedicalFolder").document(id);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Tretment_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fiche_type_spinner_1.setAdapter(adapter);


        getFicheBransh();

        disableButtons();










        button_add_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (button_add_edit.getText().toString().equals("Edit")) {
                    enableButtons();
                    txt_diesase = disease_sheet_1.getText().toString().trim();
                    txt_fiche_description_1 = fiche_description_1.getText().toString().trim();
                    txt_fiche_traitement_1 = fiche_traitement_1.getText().toString().trim();


                    button_add_edit.setText("update");
                }else {

                    //put data into strings

                    updateFicheBransh(txt_diesase,txt_fiche_description_1,txt_fiche_traitement_1);
                    disableButtons();
                    button_add_edit.setText("Edit");


                }

            }
        });










    }

    private void updateFicheBransh(String diesase, String description, String traitement) {

        HashMap<String,Object> map = new HashMap<>();
        if (!disease_sheet_1.getText().toString().trim().equals(diesase)){

            map.put("disease",disease_sheet_1.getText().toString().trim());

        }
        if (!fiche_description_1.getText().toString().trim().equals(description)){

            map.put("description",fiche_description_1.getText().toString().trim());

        }
        if (!fiche_traitement_1.getText().toString().trim().equals(traitement)){

            map.put("traitement",fiche_traitement_1.getText().toString().trim());

        }

        String item = (String) fiche_type_spinner_1.getSelectedItem();
        map.put("type",item);



        reference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Log.d("hhhhhhhhhhh","okkkkkkk");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Log.d("dsdsdsd",e.getMessage());
            }
        });



    }

    private void getFicheBransh() {

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()){
                    Fiche fiche = documentSnapshot.toObject(Fiche.class);
                    disease_sheet_1.setText(fiche.getDisease());
                    fiche_description_1.setText(fiche.getDescription());
                    fiche_traitement_1.setText(fiche.getTraitement());

                    if (fiche.getType().equals("Consultation")){

                        fiche_type_spinner_1.setSelection(0);

                    }else {
                        fiche_type_spinner_1.setSelection(1);
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void enableButtons(){
        disease_sheet_1.setEnabled(true);
        fiche_description_1.setEnabled(true);
        fiche_traitement_1.setEnabled(true);
        fiche_type_spinner_1.setEnabled(true);
    }

    private void disableButtons(){
        disease_sheet_1.setEnabled(false);
        fiche_description_1.setEnabled(false);
        fiche_traitement_1.setEnabled(false);
        fiche_type_spinner_1.setEnabled(false);

    }


}

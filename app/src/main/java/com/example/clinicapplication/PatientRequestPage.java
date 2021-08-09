package com.example.clinicapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.example.clinicapplication.adapter.PatRequestAdapter;
import com.example.clinicapplication.info.calendier;
import com.example.clinicapplication.model.Request;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PatientRequestPage extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference addRef = db.collection("Request");

    private PatRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_request_page);

        setUpRecyclerView();
    }


    private void setUpRecyclerView() {
        final String idDoc = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
//        Query query = addRef.whereEqualTo("id_doctor",idDoc+"").orderBy("id_patient", Query.Direction.DESCENDING);
        Query query = addRef.whereEqualTo("id_patient", calendier.CurrentUserid);
        query = addRef.whereEqualTo("id_doctor",idDoc);

        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();

        adapter = new PatRequestAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.RequestDocRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                new AlertDialog.Builder(PatientRequestPage.this).setMessage("Are you sure for delete the request").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.deleteItem(viewHolder.getBindingAdapterPosition());

                        adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());



                    }

                }).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}

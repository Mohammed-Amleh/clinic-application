package com.example.clinicapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.example.clinicapplication.info.calendier;
import com.example.clinicapplication.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorAdapterFiltred extends RecyclerView.Adapter<DoctorAdapterFiltred.DoctoreHolder2> implements Filterable {
    public static boolean specialiteSearch = false;
    private static final String TAG = "DoctorAdapterFiltred";
    static String doc;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference addRequest = db.collection("Request");
    private List<Doctor> mTubeList;
    private List<Doctor> mTubeListFiltered;
    StorageReference pathReference;
    Context context;
    String pattern = "";
    List<Doctor> filteredList;


    public DoctorAdapterFiltred(List<Doctor> tubeList) {
        mTubeList = tubeList;
        mTubeListFiltered = tubeList;

    }


    @NonNull
    @Override
    public DoctoreHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item, parent, false);
        context = parent.getContext();

        return new DoctoreHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctoreHolder2 doctoreHolder, int i) {
        final Doctor doctor = mTubeListFiltered.get(i);
        final TextView t = doctoreHolder.title;
        doctoreHolder.title.setText(doctor.getName());
        /// ajouter l'image


        isFriend(doctor.getEmail(), doctoreHolder.addDoc);


//        String imageId = doctor.getEmail() + ".jpg";
//        pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/" + imageId);
//        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.with(doctoreHolder.image.getContext())
//                        .load(uri)
//                        .placeholder(R.mipmap.ic_launcher)
//                        .fit()
//                        .centerCrop()
//                        .into(doctoreHolder.image);//Image location
//
//                // profileImage.setImageURI(uri);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
        doctoreHolder.specialite.setText("Specialite : " + doctor.getSpecialite());
        final String idPat = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        final String idDoc = doctor.getEmail();
        // doctoreHolder.image.setImageURI(Uri.parse("drawable-v24/ic_launcher_foreground.xml"));
        doctoreHolder.addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (doctoreHolder.addDoc.getText().toString().equals(context.getString(R.string.add_doctor))) {
                    Log.d(TAG, "onClick: " + doctoreHolder.addDoc.getText().toString());
                    Map<String, Object> note = new HashMap<>();
                    note.put("id_patient", idPat);
                    note.put("id_doctor", idDoc);
                    addRequest.document().set(note)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(
                                            context, "Request sent", Toast.LENGTH_SHORT).show();
                                    doctoreHolder.addDoc.setText(R.string.requested);

                                }
                            });

//                doctoreHolder.addDoc.setVisibility(View.INVISIBLE);
                } else {


                    Query query = addRequest.whereEqualTo("id_patient", calendier.CurrentUserid);
                    query = query.whereEqualTo("id_doctor", doctor.getEmail());

                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                                if (documentSnapshot.exists()) {
                                    documentSnapshot.getReference().delete();


                                    Toast.makeText(context, "Request deleted", Toast.LENGTH_SHORT).show();
                                    doctoreHolder.addDoc.setText(doctoreHolder.addDoc.getContext().getString(R.string.add_doctor));


                                }
                            }


                        }
                    });
//                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                            if (!value.isEmpty()) {
//                                DocumentSnapshot documentSnapshot = value.getDocuments().get(0);
//
//                                if (documentSnapshot.exists()) {
//                                    documentSnapshot.getReference().delete();
//                                    Snackbar.make(t, "\n" +
//                                            "Request deleted", Snackbar.LENGTH_SHORT).show();
//
//                                    addDoc.setText("AddDoctor");
//                                }
//                            }
//                        }
//                    });


                }
            }
        });

        doctoreHolder.appointemenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doc = doctor.getEmail();
                calendier.CurreentDoctor = doctor.getEmail();
                calendier.CurrentDoctorName = doctor.getName();
                calendier.CurrentPhone = doctor.getTel();
                calendier.CurrentUserType = "patient";
                openPage(v.getContext());

            }
        });

    }

    private void isRequested(String email, Button addDoc) {


        String user = calendier.CurrentUserid;
        Log.d("hello", user);


        Query query = addRequest.whereEqualTo("id_patient", calendier.CurrentUserid).whereEqualTo("id_doctor", email);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (!task.getResult().isEmpty()) {

                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    if (document.exists() && pattern.isEmpty()) {
                        String id_doctor = (String) document.get("id_doctor");
                        if (addDoc.getText().toString().equals(addDoc.getContext().getString(R.string.requested))) {
                            if (id_doctor.equals(email)) {
                                addDoc.setText(addDoc.getContext().getString(R.string.add_doctor));

//                            addDoc.setBackgroundColor(R.color.purple_700);


                            }


                        } else {
                            if (id_doctor.equals(email)) {
                                addDoc.setText(addDoc.getContext().getString(R.string.requested));


                            }

                        }
                    }
                }


            }
        });


    }


    private void isFriend(String email, Button addDoc) {

        DocumentReference reference = db.collection("Patient").document(calendier.CurrentUserid).collection("MyDoctors").document(email);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    addDoc.setEnabled(false);
                    addDoc.setText(addDoc.getContext().getString(R.string.added));


                } else {
                    if (filteredList == null) {
                        isRequested(email, addDoc);
                    }


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mTubeListFiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredList = new ArrayList<>();

                pattern = constraint.toString().toLowerCase();
                if (pattern.isEmpty() || pattern == null) {
                    mTubeListFiltered = mTubeList;
                } else {
                    for (Doctor tube : mTubeList) {
                        if (specialiteSearch == false) {
                            if (tube.getName().toLowerCase().contains(pattern)) {
                                filteredList.add(tube);
                            }
                        } else {
                            if (tube.getSpecialite().toLowerCase().contains(pattern)) {
                                filteredList.add(tube);
                            }
                        }
                    }
                    mTubeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mTubeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTubeListFiltered = (ArrayList<Doctor>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class DoctoreHolder2 extends RecyclerView.ViewHolder {

        Button appointemenBtn;
        TextView title;
        TextView specialite;
        ImageView image;
        Button addDoc;
        Button load;

        public DoctoreHolder2(@NonNull View itemView) {
            super(itemView);
            addDoc = itemView.findViewById(R.id.addDocBtn);
            title = itemView.findViewById(R.id.doctor_view_title);
            specialite = itemView.findViewById(R.id.text_view_description);
            image = itemView.findViewById(R.id.doctor_item_image);
            appointemenBtn = itemView.findViewById(R.id.appointemenBtn);
        }
    }

    private void openPage(Context wf) {
        Intent i = new Intent(wf, TestActivity.class);
        wf.startActivity(i);
    }
}

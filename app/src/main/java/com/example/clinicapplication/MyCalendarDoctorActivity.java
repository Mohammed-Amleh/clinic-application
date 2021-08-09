package com.example.clinicapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicapplication.Interface.ITimeSlotLoadListener;
import com.example.clinicapplication.adapter.MyTimeSlotAdapter;
import com.example.clinicapplication.info.calendier;
import com.example.clinicapplication.model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class MyCalendarDoctorActivity extends AppCompatActivity implements ITimeSlotLoadListener {

    DocumentReference doctorDoc;

    ITimeSlotLoadListener iTimeSlotLoadListener;
    android.app.AlertDialog alertDialog;
    @BindView(R.id.recycle_time_slot2)
    RecyclerView recycler_time_slot;
    @BindView(R.id.calendarView2)
    HorizontalCalendarView calendarView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calendar_doctor);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        iTimeSlotLoadListener = this;
        alertDialog = new SpotsDialog.Builder().setCancelable(false).setContext(this)
                .build();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE,0);
        loadAvailabelTimeSlotOfDoctor(calendier.CurreentDoctor,simpleDateFormat.format(date.getTime()));
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);


        Calendar startDate = Calendar.getInstance();  startDate.add(Calendar.DATE,0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,7);
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this,R.id.calendarView2)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(calendier.currentDate.getTimeInMillis() != date.getTimeInMillis()){
                    calendier.currentDate = date;
                    loadAvailabelTimeSlotOfDoctor(calendier.CurreentDoctor,simpleDateFormat.format(date.getTime()));

                }

            }
        });

    }

    private void loadAvailabelTimeSlotOfDoctor(String curreentDoctor, String bookDate) {
        alertDialog.show();

//        doctorDoc = FirebaseFirestore.getInstance()
//                .collection("Doctor")
//                .document(calendier.CurreentDoctor);
//        doctorDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful())
//                {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if(documentSnapshot.exists()){

        Log.d("sssssssss",bookDate);

        bookDate = replaceArabicNumbers( bookDate);

        Log.d("sssssssss",bookDate);

                        CollectionReference date =FirebaseFirestore.getInstance()
                                .collection("Doctor")
                                .document(calendier.CurreentDoctor)
                                .collection(bookDate);

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty())
                                    {
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    }else {
                                        List<TimeSlot> timeSlots = new ArrayList<>();
                                        for (QueryDocumentSnapshot document:task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot.class));

                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);

                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                            }
                        });
//                    }
                }

    private String  replaceArabicNumbers(String bookDate) {

            if (bookDate != null) {
                return bookDate
                        .replaceAll("٠","0")
                        .replaceAll("١", "1")
                        .replaceAll("٢", "2")
                        .replaceAll("٣", "3")
                        .replaceAll("٤","4")
                        .replaceAll("٥","5")
                        .replaceAll("٦","6")
                        .replaceAll("٧","7")
                        .replaceAll("٨","8")
                        .replaceAll("٩","9");



            }

            return null;
        }


//            }
//        });




    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(this,timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(this);
        recycler_time_slot.setAdapter(adapter);
        alertDialog.dismiss();
    }
}

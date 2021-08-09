package com.example.clinicapplication.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.clinicapplication.Interface.IRecyclerItemSelectedListener;
import com.example.clinicapplication.R;
import com.example.clinicapplication.info.calendier;
import com.example.clinicapplication.model.ApointementInformation;
import com.example.clinicapplication.model.TimeSlot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {


    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;
    SimpleDateFormat simpleDateFormat;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot,parent,false);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String book_time = calendier.convertTimeSlotToString(position);

        holder.txt_time_slot.setText(book_time);
        if(timeSlotList.isEmpty()){
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

            holder.txt_time_slot_description.setText("Available");
            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));

        }
        else{
            for (TimeSlot slotValue:timeSlotList){
                int slot = Integer.parseInt(slotValue.getSlot().toString());

                if(slot == position){
                    holder.card_time_slot.setTag(calendier.DISABLE_TAG);
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));

                    holder.txt_time_slot_description.setText("Full");

//                    R.string.checked;

                    if(slotValue.getType().equals("Checked"))
                        holder.txt_time_slot_description.setText("Choosen");

                    holder.txt_time_slot_description.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                    holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                }
            }

        }if (!cardViewList.contains(holder.card_time_slot))
            cardViewList.add(holder.card_time_slot);


        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                for(CardView cardView:cardViewList) {
                    if (cardView.getTag() == null)
                        cardView.setCardBackgroundColor(context.getResources()
                                .getColor(android.R.color.white));
                }



                if (!holder.txt_time_slot_description.getText().equals("Full")){

                    holder.card_time_slot.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.holo_orange_dark));


                }


                Intent intent = new Intent(calendier.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(calendier.KEY_TIME_SLOT,position);
                calendier.currentTimeSlot = position ;
                intent.putExtra(calendier.KEY_STEP,2);
//                Log.e("pos ", "onItemSelectedListener: "+position );
//                localBroadcastManager.sendBroadcast(intent);
                if(calendier.CurrentUserType.equals("doctor") && holder.txt_time_slot_description.getText().equals("Available")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(holder.card_time_slot.getContext());
                    alert.setTitle("Block");
                    alert.setMessage(R.string.are_you_sure);
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {



                            ApointementInformation apointementInformation = new ApointementInformation();
                            apointementInformation.setApointementType(calendier.Currentaappointementatype);
                            apointementInformation.setDoctorId(calendier.CurreentDoctor);
                            apointementInformation.setDoctorName(calendier.CurrentDoctorName);
                            apointementInformation.setChemin("Doctor/"+calendier.CurreentDoctor+"/"+calendier.replaceArabicNumbers(calendier.simpleFormat.format(calendier.currentDate.getTime()))+"/"+String.valueOf(calendier.currentTimeSlot));
                            apointementInformation.setType("full");
                            apointementInformation.setTime(new StringBuilder(calendier.convertTimeSlotToString(calendier.currentTimeSlot))
                                    .append("at")
                                    .append(calendier.replaceArabicNumbers(calendier.simpleFormat.format(calendier.currentDate.getTime()))).toString());
                            apointementInformation.setSlot(Long.valueOf(calendier.currentTimeSlot));

                            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
                            holder.txt_time_slot_description.setText("Full");

                            holder.txt_time_slot_description.setTextColor(context.getResources()
                                    .getColor(android.R.color.white));
                            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));

                            holder.card_time_slot.setTag(calendier.DISABLE_TAG);

                            DocumentReference bookingDate = FirebaseFirestore.getInstance()
                                    .collection("Doctor")
                                    .document(calendier.CurreentDoctor)
                                    .collection(calendier.replaceArabicNumbers(calendier.simpleFormat.format(calendier.currentDate.getTime())))
                                    .document(String.valueOf(calendier.currentTimeSlot));

                            bookingDate.set(apointementInformation);


                        }
                    });

                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    alert.show();

                } if (calendier.CurrentUserType.equals("doctor") && holder.txt_time_slot_description.getText().equals("Full")){


                    AlertDialog.Builder alert = new AlertDialog.Builder(holder.card_time_slot.getContext());
                    alert.setTitle(R.string.un_block);
                    alert.setMessage(R.string.are_you_sure_you_want_to_unblock);
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DocumentReference bookingDate = FirebaseFirestore.getInstance()
                                    .collection("Doctor")
                                    .document(calendier.CurreentDoctor)
                                    .collection(calendier.replaceArabicNumbers(calendier.simpleFormat.format(calendier.currentDate.getTime())))
                                    .document(String.valueOf(calendier.currentTimeSlot));


                            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                            holder.txt_time_slot_description.setText("Available");
                            holder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
                            holder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));
                            bookingDate.delete();


                        }
                    });

                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            holder.card_time_slot.setCardBackgroundColor(context.getResources()
                                    .getColor(android.R.color.white));

                        }
                    });

                    alert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = (CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = (TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = (TextView)itemView.findViewById(R.id.txt_time_slot_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}


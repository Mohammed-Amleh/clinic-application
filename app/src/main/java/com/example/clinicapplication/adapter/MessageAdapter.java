package com.example.clinicapplication.adapter;

import android.content.Context;
import android.net.Uri;
import android.service.autofill.Transformation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.clinicapplication.ProfilePatientActivity;
import com.example.clinicapplication.R;
import com.example.clinicapplication.info.calendier;
import com.example.clinicapplication.model.Message;
import com.example.clinicapplication.model.Utils;
import com.firebase.ui.common.BaseObservableSnapshotArray;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class MessageAdapter extends FirestoreRecyclerAdapter {
    StorageReference pathReference;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Map<String, String> ServerTimestamp;
    private Context mContext;
//    final String doctorID = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    DocumentReference docRef = db.collection("Patient").document("" + doctorID + "");


    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i, @NonNull Object o) {

        Message message = (Message) o;

//        if(model.getUserSender().equals(getCurrentUser().getEmail()+"") ){
//            //holder.text.setTextSize(20);
//            //holder.text.setBackgroundColor(0xC0C0C0);
//            // CoordinatorLayout.LayoutParams  lllp= (CoordinatorLayout.LayoutParams) holder.text.getLayoutParams();
//            // lllp.gravity= Gravity.LEFT;
//            //holder.text.setLayoutParams(lllp);
//            //holder.text.setBackground(holder.text.getContext().getResources().getDrawable(R.drawable.rounded_message2));
//            holder.text2.setText(model.getMessage());
//            holder.text2.setPadding(35,35,35,35);
//        }
//        else {
//            holder.text.setText(model.getMessage());
//            holder.text.setPadding(35,35,35,35);
//        }

        switch (viewHolder.getItemViewType()) {

            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);


        }

    }

    @Override
    public int getItemViewType(int position) {
//        Log.d("dssssssssssssssss", getSnapshots().getSnapshot(position).ge);

        QueryDocumentSnapshot x = (QueryDocumentSnapshot) getSnapshots().getSnapshot(position);

        String userSender = x.get("userSender").toString();

        if (userSender.equals(getCurrentUser().getEmail() + "")) {

            return VIEW_TYPE_MESSAGE_SENT;


        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_chat_item,parent,false);
//        return new MessageHolder(v);

        View view;

        mContext = parent.getContext();
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_itme_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_time_2, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;

    }


    class MessageHolder extends RecyclerView.ViewHolder {

        TextView text;
        TextView text2;

        public MessageHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_item_text);
            text2 = itemView.findViewById(R.id.message_item_text2);
        }
    }

    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;

        SentMessageHolder(View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.text_gchat_date_me);
            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());

//            String dd = calendier.simpleFormat.format(message.getDateCreated()).replaceAll("_", "/");

//            dateText.setText(dd);
            // Format the stored timestamp into a readable String using method.
//            timeText.setText(Utils.formatTime(message.getDateCreated().getTime()));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, tv_date;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            tv_date = itemView.findViewById(R.id.tv_date);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(Utils.formatTime(message.getDateCreated().getTime()));
//            String dd = calendier.simpleFormat.format(message.getDateCreated()).replaceAll("_", "/");
//            tv_date.setText(dd);


            nameText.setText(message.getUsername());

            // Insert the profile image from the URL into the ImageView.

            String imageId = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
            pathReference = FirebaseStorage.getInstance().getReference().child("DoctorProfile/" + imageId + ".jpg");

            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(mContext)
                            .load(uri)
                            .placeholder(R.mipmap.ic_launcher)
                            .fit()
                            .centerCrop()
                            .into(profileImage);//hna fin kayn Image view
                    // profileImage.setImageURI(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    // Handle any errors
                }
            });

        }
    }

}


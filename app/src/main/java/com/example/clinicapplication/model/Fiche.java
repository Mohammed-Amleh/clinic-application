package com.example.clinicapplication.model;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Fiche {
    private String disease;
    private String description;
    private String traitement;
    private String type;
    private Date dateCreated;
    private String doctor;
    private String id;

    public Fiche(){

    }

    public Fiche(String Disease, String description, String traitement, String type, String doctor,String id) {
        this.disease = Disease;
        this.description = description;
        this.traitement = traitement;
        this.type = type;
        this.doctor = doctor;
        this.id = id;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String Disease) {
        this.disease = Disease;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
}


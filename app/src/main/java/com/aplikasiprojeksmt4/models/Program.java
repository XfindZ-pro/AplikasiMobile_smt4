package com.aplikasiprojeksmt4.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Program {
    private String id;
    private String nama;
    private String organisasi;
    private String wilayah;
    private String tipe;
    private String target;
    private String deskripsi;
    private String imageUrl;
    private String status;
    private long terkumpul;
    @ServerTimestamp
    private Date created_at;

    public Program() {
        // Required for Firebase
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public String getOrganisasi() { return organisasi; }
    public String getWilayah() { return wilayah; }
    public String getTipe() { return tipe; }
    public String getTarget() { return target; }
    public String getDeskripsi() { return deskripsi; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }
    public long getTerkumpul() { return terkumpul; }
    public Date getCreated_at() { return created_at; }
    
    // Helper to get target as number if possible (for progress)
    public long getTargetValue() {
        if (target == null) return 0;
        String clean = target.replaceAll("[^0-9]", "");
        try {
            return Long.parseLong(clean);
        } catch (Exception e) {
            return 0;
        }
    }
}

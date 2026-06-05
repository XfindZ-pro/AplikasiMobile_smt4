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
    private String dibuat_oleh = null; // Menunjuk ke UID di tabel users
    @ServerTimestamp
    private Date created_at;

    public Program() {
        // Required for Firebase
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getOrganisasi() { return organisasi; }
    public void setOrganisasi(String organisasi) { this.organisasi = organisasi; }

    public String getWilayah() { return wilayah; }
    public void setWilayah(String wilayah) { this.wilayah = wilayah; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTerkumpul() { return terkumpul; }
    public void setTerkumpul(long terkumpul) { this.terkumpul = terkumpul; }

    public String getDibuat_oleh() { return dibuat_oleh; }
    public void setDibuat_oleh(String dibuat_oleh) { this.dibuat_oleh = dibuat_oleh; }

    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }
    
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

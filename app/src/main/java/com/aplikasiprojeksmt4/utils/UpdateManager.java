package com.aplikasiprojeksmt4.utils;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.aplikasiprojeksmt4.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class UpdateManager {

    private static final String TAG = "UpdateManager";
    private final Context context;
    private final FirebaseFirestore db;

    public UpdateManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    public void checkForUpdates() {
        db.collection("app_settings").document("update_info")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String latestVersion = documentSnapshot.getString("latest_version");
                        String downloadUrl = documentSnapshot.getString("download_url");
                        String currentVersion = BuildConfig.VERSION_NAME;

                        Log.d(TAG, "Current Version: " + currentVersion);
                        Log.d(TAG, "Latest Version: " + latestVersion);

                        if (latestVersion != null && isVersionNewer(currentVersion, latestVersion)) {
                            showUpdateDialog(latestVersion, downloadUrl);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking for updates", e));
    }

    private boolean isVersionNewer(String current, String latest) {
        return latest.compareTo(current) > 0;
    }

    private void showUpdateDialog(String version, String url) {
        new AlertDialog.Builder(context)
                .setTitle("Update Tersedia")
                .setMessage("Versi baru (" + version + ") telah tersedia. Silakan update untuk mendapatkan fitur terbaru.")
                .setPositiveButton("Update Sekarang", (dialog, which) -> startDownload(url))
                .setNegativeButton("Nanti", null)
                .setCancelable(false)
                .show();
    }

    private void startDownload(String url) {
        Toast.makeText(context, "Mengunduh update...", Toast.LENGTH_SHORT).show();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("App Update");
        request.setDescription("Mengunduh versi terbaru...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        
        String fileName = "app-update.apk";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    installApk(fileName);
                    context.unregisterReceiver(this);
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    private void installApk(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "File APK tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }
}

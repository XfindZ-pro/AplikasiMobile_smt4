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
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.aplikasiprojeksmt4.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class UpdateManager {

    private static final String TAG = "UpdateManager";
    private static final String APK_NAME = "update_donasiku.apk";
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
        // Simple comparison for version format like "1.0.YYYYMMDD-HHmm"
        return latest.compareTo(current) > 0;
    }

    private void showUpdateDialog(String version, String url) {
        new AlertDialog.Builder(context)
                .setTitle("Pembaruan DonasiKu Tersedia")
                .setMessage("Versi baru (" + version + ") telah tersedia.\n\nUnduh sekarang untuk mendapatkan fitur terbaru dan perbaikan sistem.")
                .setPositiveButton("Download & Install", (dialog, which) -> {
                    if (canInstallPackages()) {
                        startDownload(url);
                    } else {
                        requestInstallPermission();
                    }
                })
                .setNegativeButton("Nanti", null)
                .setCancelable(false)
                .show();
    }

    private boolean canInstallPackages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    private void requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(context, "Aktifkan 'Izinkan dari sumber ini' untuk mengupdate aplikasi", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    .setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    private void startDownload(String url) {
        // Delete old APK if exists to avoid conflict
        File oldFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), APK_NAME);
        if (oldFile.exists()) {
            oldFile.delete();
        }

        Toast.makeText(context, "Mengunduh pembaruan di latar belakang...", Toast.LENGTH_LONG).show();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("DonasiKu Update");
        request.setDescription("Versi " + BuildConfig.VERSION_NAME + " -> Terbaru");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_NAME);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    installApk();
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

    private void installApk() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), APK_NAME);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            context.startActivity(intent);
        } else {
            Log.e(TAG, "File APK tidak ditemukan di folder Downloads");
        }
    }
}

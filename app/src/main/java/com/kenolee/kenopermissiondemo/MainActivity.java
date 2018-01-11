package com.kenolee.kenopermissiondemo;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.kenolee.kenopermission.KenoPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String[] permission = new String[0];
        try {
            permission = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        KenoPermission kenoPermission = new KenoPermission(this, permission, 1);
        if (!kenoPermission.isGranted(permission)) {
            kenoPermission.callback(new KenoPermission.Callback() {
                @Override
                public void onResult(List<String> grantedPermission, List<String> deniedPermission, List<String> deniedPermissionForever, List<String> deniedPermissionJust) {
                    Toast.makeText(MainActivity.this, "Granted:" + grantedPermission.toArray() + "\nDenied:" + deniedPermission.toString()
                            + "\nDeniedForever:" + deniedPermissionForever.toString() + "\nDeinedJust" + deniedPermissionJust.toString(), Toast.LENGTH_LONG)
                            .show();

                }
            }).requestPermission();
        }
        else {
            Toast.makeText(MainActivity.this, "已經具有權限了", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        KenoPermission.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

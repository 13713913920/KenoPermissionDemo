package com.kenolee.kenopermission;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v13.app.ActivityCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2018/1/10.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class KenoPermission {

    private WeakReference<Activity> mWRActivity;
    private Callback mCallback;
    private  int code=666;
    private static HashMap<Integer,KenoPermission>  hashMap=new HashMap<>();
    private  String[] permissions;

    public static   KenoPermission get(int requestCode)
    {
        KenoPermission kenoPermission=null;
        if(KenoPermission.hashMap.containsKey(requestCode))
            kenoPermission=KenoPermission.hashMap.get(requestCode);
        return  kenoPermission;
    }
    public KenoPermission(Activity activity, String[] permissions, int requestCode)
    {
        this.permissions=permissions;
        code=requestCode;
        mWRActivity=new WeakReference<>(activity);
        hashMap.put(requestCode,this);
    }

    public  KenoPermission callback( @NonNull Callback mCallback) {
        this.mCallback=mCallback;
        return this;
    }
    public KenoPermission.Callback getCallback()
    {
        return  mCallback;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(mWRActivity.get(),getNotGranted(permissions),code);
    }
    public  boolean isGranted(String[] permissions)
    {
        for(String permission :permissions)
        {
            if(PackageManager.PERMISSION_GRANTED!=mWRActivity.get().checkSelfPermission(permission)) {
                return false;
            }
        }
        return true;
    }
    public void openPermissionSetting()
    {
        try{
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + mWRActivity.get().getPackageName()));
        mWRActivity.get().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public   String[] getNotGranted(String[] permissions)
    {
        List<String> list=new ArrayList<>();
        for(String permission :permissions)
        {
            if(PackageManager.PERMISSION_GRANTED!=mWRActivity.get().checkSelfPermission(permission)) {
                list.add(permission);
            }
        }
        return  list.toArray(new String[0]);
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults)
    {
        KenoPermission kenoPermission=KenoPermission.get(requestCode);
        if(kenoPermission==null)  return;
        List<String> lstAllow=new ArrayList<>();
        List<String> lstDeny=new ArrayList<>();
        List<String> lstDenyForever=new ArrayList<>();
        List<String> lstDenyJust=new ArrayList<>();
        for(int i=0;i<grantResults.length;i++)
        {
            if(grantResults[i]== PackageManager.PERMISSION_GRANTED)
                lstAllow.add(permissions[i]);
            else if(grantResults[i]==PackageManager.PERMISSION_DENIED)
            {
                lstDeny.add(permissions[i]);
                if(!kenoPermission.mWRActivity.get().shouldShowRequestPermissionRationale(permissions[i]))
                    lstDenyForever.add(permissions[i]);
                else
                    lstDenyJust.add(permissions[i]);
            }
        }
        if(kenoPermission.getCallback()!=null)
            kenoPermission.getCallback().onResult(lstAllow,lstDeny,lstDenyForever,lstDenyJust);
    }
   public interface Callback {
        void onResult(List<String> grantedPermission, List<String> deniedPermission, List<String>
                deniedPermissionForever, List<String> deniedPermissionJust);
    }
}

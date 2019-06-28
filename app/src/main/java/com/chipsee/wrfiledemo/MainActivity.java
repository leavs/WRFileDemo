package com.chipsee.wrfiledemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WRFileDemo";
    String PRIVATE_FILE_PATH = "";
    String PRIVATE_FILE_NAME = "private_file.txt";
    String EXT_STORAGE_FILE_PATH = "";
    String EXT_STORAGE_FILE_NAME = "ext_storage_file.txt";
    String PRIVATE_FILE_CONTENTS = "Private File Contents";
    String EXT_STORAGE_FILE_CONTENTS = "EXT Storage file contents";

    private TextView mTextView;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: getUid is " + getUid(this));
        Log.d(TAG, "onCreate: getPid is " + android.os.Process.myPid());

        /*--1--Assets File*/
        String assetsStr = FileUtils.readAssetsFile(this, "txt" + File.separator + "p.txt");
        Log.d(TAG, "onCreate: assetsStr is " + assetsStr);

        /*--2--raw file*/
        String rawStr = FileUtils.readRawFile(this, R.raw.d);
        Log.d(TAG, "onCreate: rawStr is " + rawStr);

        /*--3--private file*/
        PRIVATE_FILE_PATH = getFilesDir().getPath() + File.separator + "self" + File.separator;
        /*write content to data-user-0-com.chipsee.wrfiledemo-files-self-private_file.txt*/
        FileUtils.writeToFile(PRIVATE_FILE_PATH + PRIVATE_FILE_NAME, PRIVATE_FILE_CONTENTS);
        /*readout file from private_file.txt*/
        String pf = FileUtils.readFile(PRIVATE_FILE_PATH + PRIVATE_FILE_NAME);
        Log.d(TAG, "onCreate: Private_file contents is " + "[" + pf + "]");

        /*--4-- External storage*/
        /*When app share uid with system, don't need to verify storage permissions dynamic*/
        //verifyStoragePermissions(this);

        mTextView = (TextView) findViewById(R.id.tw);
        mTextView.setText("Follow is the EXT Stroge:\n\n\n");
        ArrayList paths = (ArrayList) getExtSDCardPath(this);
        for(int i = 0;i < paths.size(); i ++){
            Log.d(TAG, "onCreate: path"+i+"="+paths.get(i));
            EXT_STORAGE_FILE_PATH = (String) paths.get(i);
            FileUtils.writeToFile(EXT_STORAGE_FILE_PATH + File.separator + EXT_STORAGE_FILE_NAME, EXT_STORAGE_FILE_CONTENTS);
            String isf = FileUtils.readFile(EXT_STORAGE_FILE_PATH + File.separator + EXT_STORAGE_FILE_NAME);
            Log.d(TAG, "onCreate: EXT Storage file is " + EXT_STORAGE_FILE_PATH + File.separator + EXT_STORAGE_FILE_NAME);
            Log.d(TAG, "onCreate: EXT Storage file contents is " + "[" + isf + "]");
            if(isf == ""){
                mTextView.append("EXT Storage File ["+i+"] " + EXT_STORAGE_FILE_PATH + File.separator +"   Error (Permission denied)"+"\n");
                continue;
            }
            mTextView.append("EXT Storage File ["+i+"] " + EXT_STORAGE_FILE_PATH + File.separator + EXT_STORAGE_FILE_NAME +"\n");
            mTextView.append("Contents:" + isf +"\n\n");
        }
    }   /*on create*/

    /**
     * Application will get root permission, the device must be rooted first
     *
     * @return Application rooted or not
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity){
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                switch (permissions[0]) {
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:   // permission 1  WRITE_EXTERNAL_STORAGE
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // permission was granted
                            // do something now?
                        } else {
                            // permission denied
                            Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Manifest.permission.READ_EXTERNAL_STORAGE: //permission 2
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // permission was granted
                            // do something now?
                        } else {
                            // permission denied
                            Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                }
                break;
            default:
        }
    }   /*onRequestPermissionsResult*/

    public static List getExtSDCardPath(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context
                .STORAGE_SERVICE);
        ArrayList paths = new ArrayList();
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            paths = new ArrayList(Arrays.asList((String[]) invoke));
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (!paths.contains(rootPath))
            paths.add(0, rootPath);

        return paths;
    }

    public static String getUid(Context context) {
        String uid = "";
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo("com.chipsee.wrfiledemo", PackageManager.GET_META_DATA);
            uid = String.valueOf(ai.uid);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return uid;
    }

}

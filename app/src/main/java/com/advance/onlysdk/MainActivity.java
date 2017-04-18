package com.advance.onlysdk;

import android.Manifest.permission;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.advance.library.core.AdvanceSDK;
import com.advance.library.permission.EasyPermissions;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

  TextView textView;
  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textView = (TextView) findViewById(R.id.tv_about_location);

    String[] permissions = {permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION,
        permission.READ_CONTACTS, permission.READ_EXTERNAL_STORAGE, permission.READ_CALL_LOG,
        permission.READ_SMS, permission.READ_PHONE_STATE};

    if (EasyPermissions.hasPermissions(this, permissions)) {
//      Toast.makeText(this, "TODO://OK", Toast.LENGTH_LONG).show();
    } else {
      EasyPermissions.requestPermissions(this, getString(R.string.rationale_all), 100, permissions);
    }
    AdvanceSDK.init(getApplicationContext());
  }

  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {
    Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
  }

  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {
    Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
    EasyPermissions
        .checkDeniedPermissionsNeverAskAgain(this, getString(R.string.rationale_ask_again),
            R.string.setting, R.string.cancel, perms);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }
}

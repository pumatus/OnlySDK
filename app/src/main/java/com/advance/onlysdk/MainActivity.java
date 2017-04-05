package com.advance.onlysdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.advance.library.core.AdvanceSDK;

public class MainActivity extends AppCompatActivity {
TextView textView;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textView = (TextView) findViewById(R.id.tv_about_location);
    AdvanceSDK.init(getApplicationContext());
  }
}

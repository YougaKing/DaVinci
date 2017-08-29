package com.youga.imageselector;

import android.app.Activity;
import android.os.Bundle;

public class VinciActivity extends Activity {

    public static final String EXTRA_PREFIX = BuildConfig.APPLICATION_ID;
    public static final String EXTRA_CONFIGURATION = EXTRA_PREFIX + ".Configuration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinci);
    }
}

package com.haoweilai.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.haoweilai.demo.view.SCurveView;

public class MainActivity extends AppCompatActivity {
    private SCurveView id_scurve_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_scurve_view = findViewById(R.id.id_scurve_view);
    }

    public void start(View view) {
        id_scurve_view.startAnim();
    }

}

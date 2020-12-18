package com.example.proj2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    Drawer d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.d = (Drawer)findViewById(R.id.drawView);
    }

    public void clearScreen(View view){
        d.clearPoints();
    }

    public void doClustering(View view){
        EditText et = (EditText)findViewById(R.id.cInput);
        int clusterNumber = Integer.parseInt(et.getText().toString());
        Clusters clusters = new Clusters();
        clusters.load(d.getPoints());
        d.loadClustering(clusters.performClustering(clusterNumber,0.1,0.1,0.8,1,2.,1., 0.01));
    }
}
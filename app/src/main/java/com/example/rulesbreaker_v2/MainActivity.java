package com.example.rulesbreaker_v2;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.spark.submitbutton.SubmitButton;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter adapter;
    // Variable used to allocate the pictures that we intend to use from external source
    int images[] = {R.drawable.cuba_libre2, R.drawable.gin_apa_tonica3, R.drawable.long_island,
                    R.drawable.screw_driver, R.drawable.vodka_coke, R.drawable.vodka_tonic};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View of the layout |activity_main.xml|
        setContentView(R.layout.activity_main);
        configureBeginButton();
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(MainActivity.this,images);
        viewPager.setAdapter(adapter);
    }
    // Function defined | called when *Pair and Order* is pressed
    private void configureBeginButton() {
        // Find the button by its own ID declared in the layout |activity_main.xml|
        final SubmitButton begin1 =  findViewById(R.id.begin1);
        BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
        begin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //timer for button
                //2sec cooldown so the animation can finish, then execute the following Handler
                Handler myhandler = new Handler();
                myhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, pairing.class));
                    }
                },2000);
            }
        });
    }
}

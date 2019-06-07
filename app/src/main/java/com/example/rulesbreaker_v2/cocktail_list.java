package com.example.rulesbreaker_v2;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spark.submitbutton.SubmitButton;

import java.io.IOException;
import java.util.UUID;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class cocktail_list extends AppCompatActivity {
    /*Declared widgets*/
    Button initial_pos;
    static String address = null;
    private static ProgressDialog progress;
    static BluetoothSocket btSocket;
    static BluetoothAdapter myBluetooth;
    private static boolean isBtConnected;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // receive the address from the BT device
        Intent newint = getIntent();
        address = newint.getStringExtra(pairing.EXTRA_ADDRESS);
        // View of the layout |activity_cocktail_list.xml|
        setContentView(R.layout.activity_cocktail_list);
        // widget call for initial_pos button (safety button)
        initial_pos = (Button) findViewById(R.id.initial_pos);
        // this class below is to start the Connection with the BT device
        new ConnectBT().execute();


        // Using 'final', so the variables won't get changed
        // In the layout we configured a ProSwipeButton that has a custom animation
        // It gets find using an ID from layout | the distance for swipe action is customizable
        // In order to make the button work, there is being used a method named .setOnSwipeListener
        // and the role of it is to recognise our gestures (e.g. Left, right, pressing for a period of time ...)
        final ProSwipeButton proSwipeBtn = findViewById(R.id.order_cuba);
        proSwipeBtn.setSwipeDistance(0.5f);
        proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // after the swipe gesture is getting done, the handler gonna set
                // a short delay in order to see the animation
                // user has swiped the button| Perform your async operation now |
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtn.showResultIcon(true, true);
                    }
                }, 1500);
                // the string number that is attached for 1st_cocktail(cuba_libre)
                // |'sendSignal' is a function that is defined as a 'public void' | and finally called here
                sendSignal("1");
            }
        });

        final ProSwipeButton proSwipeBtn2 = findViewById(R.id.gin_apa_tonica);
        proSwipeBtn2.setSwipeDistance(0.5f);
        proSwipeBtn2.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtn2.showResultIcon(true, true);
                    }
                }, 1500);
                sendSignal("2");
            }
        });


        final ProSwipeButton proSwipeBtn3 = findViewById(R.id.long_island);
        proSwipeBtn3.setSwipeDistance(0.5f);
        proSwipeBtn3.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtn3.showResultIcon(true, true);
                    }
                }, 1500);
                sendSignal("3");
            }
        });

        final ProSwipeButton proSwipeBtn4 = findViewById(R.id.screw_driver);
        proSwipeBtn4.setSwipeDistance(0.5f);
        proSwipeBtn4.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtn4.showResultIcon(true, true);
                    }
                }, 1500);
                sendSignal("4");
            }
        });

        final ProSwipeButton proSwipeBtn5 = findViewById(R.id.vodka_cake);
        proSwipeBtn5.setSwipeDistance(0.5f);
        proSwipeBtn5.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtn5.showResultIcon(true, true);
                    }
                }, 1500);
                sendSignal("5");
            }
        });

        final ProSwipeButton proSwipeBtn6 = findViewById(R.id.vodka_tonic);
        proSwipeBtn6.setSwipeDistance(0.5f);
        proSwipeBtn6.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proSwipeBtn6.showResultIcon(true, true);
                    }
                }, 1500);
                sendSignal("6");
            }
        });

        initial_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("7");
            }
        });

        configureBackButton();
    }

    // OutputStream is used for writing data to a destination, in our case the BT_Module
    // every cocktail has attached a string number to it, from 1 to 6
    public void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    class ConnectBT extends AsyncTask<Void, Void, Void> //UI thread
     {
        private boolean ConnectSuccess = true; //if is here, is almost connected

        @Override
        public  void onPreExecute () {
            //this dialog is used as a feedback, a progress dialog
            progress = ProgressDialog.show(cocktail_list.this,
                    "CONNECTING . . .",
                    "PLEASE, BE PATIENT o_^");
        }

        @Override
        protected Void doInBackground (Void... devices) //while the dialog is shown, the connection is done in BACKGROUND
        {
            try {
                if ( btSocket==null || isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();                    // bluetooth device is getting here
                    BluetoothDevice mydevice = myBluetooth.getRemoteDevice(address);       // connecting to the device and verify if it is available
                    btSocket = mydevice.createInsecureRfcommSocketToServiceRecord(myUUID); // create a RFCOM (SPP) connection between them
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();                                                    // start connection
                }
            } catch (IOException e) {       // This class is the general class of exceptions produced by failed or interrupted I/O operations.
                ConnectSuccess = false;     // Constructs an IOException with null as its error detail message.
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) // after 2nd task-doInBackground, this verify if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("");

            } else {
                msg("Connected");               // if the device was successfully connected, there will be a PopUp with a message
                isBtConnected = true;
            }

           progress.dismiss();
        }
    }
    // this function is used to call the clickable button (back) that is find by his ID in layout
    // for the button itself, it was used custom one with an animation
    // the delay of 1500(1.5sec) is for the animation to get finished, being done by a Handler
    private void configureBackButton() {
        SubmitButton back = (SubmitButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //timer
                // 1.5 before *backtomainactivity*
                Handler myhandler = new Handler();
                myhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BACK();
                    }
                }, 1500);
            }
        });

    }
    // this function is created if we want to go back for the Main_activity
    // the connection with bluetooth is closed and we can review the cocktails, once again
    private void BACK () {
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }
        // starting the Main_Activity
        startActivity(new Intent(cocktail_list.this, MainActivity.class));
    }
}

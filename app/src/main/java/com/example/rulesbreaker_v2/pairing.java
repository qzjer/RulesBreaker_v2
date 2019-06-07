package com.example.rulesbreaker_v2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class pairing extends AppCompatActivity {
    /*Declared widgets variables*/
    Button btnPaired;
    ListView devicelist;
    // Initialise variables to control the bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View of the layout |activity_pairing.xml|
        setContentView(R.layout.activity_pairing);
        // Find each button by its own ID declared in the layout | activity_pairing.xml
        btnPaired = (Button) findViewById(R.id.button);
        devicelist = (ListView) findViewById(R.id.listView);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        // Checking if the device has a Bluetooth or no |
        if ( myBluetooth==null ) {
            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
            finish();
            // Calling the action_request | Pop-up for turning on the bluetooth on device | ALLOW||DENY |
        } else if ( !myBluetooth.isEnabled() ) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }
        // If the button is pressed, a list with paired devices will be shown
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });
    }
    // Initialise the list of devices | You can be able to see JUST devices
    // that have been paired before
    private void pairedDevicesList () {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();
        if ( pairedDevices.size() > 0 ) {
            for ( BluetoothDevice bt : pairedDevices ) {
                // get the devices names and the addresses (unique addresses)
                list.add(bt.getName().toString() + "\n" + bt.getAddress().toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        // Method called when the list is clicked by the USER
        devicelist.setOnItemClickListener(myListClickListener);
    }

    // This method allows the USER to click the ITEMS via List
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length()-17);
            // Making an 'Intent' so we can start a NEW activity | cocktail_list activity |
            Intent i = new Intent(pairing.this, cocktail_list.class);
            // This will be received in the |cocktail_list class| where we can order any drinks
            i.putExtra(EXTRA_ADDRESS, address);
            startActivity(i);
        }
    };
}


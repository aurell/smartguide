package com.justbeatit.smartguide;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.justbeatit.smartguide.text.Messenger;
import com.justbeatit.smartguide.text.MessengerImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    Place currentPlace;
    Messenger messenger;
    Boolean navigationMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        messenger = new MessengerImpl(getApplicationContext(), this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Speaking ...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                speak(fab.getRootView());
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupLocations();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messenger.sendMessage(getString(R.string.info_start));
                currentPlace.startDefaultPath();
                discoverDevices();

            }
        }, 2000);
    }

    private void setupLocations() {
        currentPlace = new Place(
                "Dworzec Gdański Teatr Szekspirowski",
                "",
                "Bilety promocyjne można zakupić tylko w kasie biletowej Teatru. Bilety ulgowe przysługują uczniom, studentom, nauczycielom, emerytom, rencistom oraz osobom niepełnosprawnym. Bilety ulgowe bez udokumentowania prawa do ulgi nie uprawniają do wejścia na widownię. Kupujący winien udać się do kasy biletowej Teatru i uiścić dopłatę.",
                "12:00 Dziennik przebudzenia. 20:00 Mój ulubiony Młynarski.",
                new ArrayList<>(Arrays.asList(
                        new Beacon("Wejście",
                                "Witamy w teatrze szekspirowskim.",
                                getString(R.string.entry_guide_to_toilets),
                                "98:E7:F5:83:D3:A4"
                        ),
                        new Beacon("Schody - góra",
                                "Skręć w lewio i schodami na sam dół.",
                                getString(R.string.upper_stairs_guide_to_toilet),
                                "28:ED:6A:40:B9:39"
                        ),
                        new Beacon("Schody - dół",
                                "Skręć w lewo, za drzwiami w prawo.",
                                getString(R.string.lower_stairs_guide_to_toilet),
                                "50:55:27:24:AF:26"
                        ),
                        new Beacon("Toaleta",
                                "Skręć w lewo, toaleta jest za drzwiami po prawej stronie.",
                                getString(R.string.toilet_guide_to_toilet),
                                "28:ED:6A:40:B9:39"
                        )
                )));

    }

    private void discoverDevices() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                //Finding devices
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceId = device.getAddress();

                    if (navigationMode) {
                        if (!currentPlace.isBeaconOnActivePath(deviceId)) {
                            return;
                        }
                        if (currentPlace.isSeenBeenBefore(deviceId)) {
                            return;
                        }
                        Beacon currentBeacon = currentPlace.getCurrentBeaconOnActivePath();
                        if (null != currentBeacon && deviceId.equalsIgnoreCase(currentBeacon.getDeviceId())) {
                            // we still receive signal from current beacon
                            showToastMessage("Still the same beacon...");
                            // messenger.sendMessage(currentBeacon.getInfo());
                            return;
                        }
                        currentBeacon = getNextBeacon();
                        if (null == currentBeacon) {
                            return;
                        }
                        if (deviceId.equalsIgnoreCase(currentBeacon.getDeviceId())) {
                            // we caught signal from next beacon on the path
                            showToastMessage("Next beacon: " + deviceId);
                            messenger.sendMessage(currentBeacon.getPathTips());
                            currentPlace.setCurrentBeaconOnActivePath(currentBeacon);
                            return;
                        } else {
                            currentBeacon = currentPlace.getPreviousBeaconOnActivePath();
                            if (!deviceId.equalsIgnoreCase(currentBeacon.getDeviceId())) {
                                currentBeacon = currentPlace.getPreviousBeaconOnActivePath();
                            }
                            currentPlace.setCurrentBeaconOnActivePath(currentBeacon);
                        }
                    } else {

                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    mBluetoothAdapter.startDiscovery();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    @Nullable
    private Beacon getNextBeacon() {
        Beacon currentBeacon = currentPlace.getNextBeaconOnActivePath();
        if (null == currentBeacon) {
            // path is finished
            showToastMessage("Path finished!");
        }
        return currentBeacon;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
            if (resultCode == RESULT_OK) {
                List<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (!textMatchList.isEmpty()) {
                    String recognizedText = textMatchList.get(0);
                    showToastMessage(recognizedText);
                    runVoiceCommand(recognizedText);
                }
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void runVoiceCommand(String text) {
        if (text.toLowerCase().contains("pomoc")) {
            CallForHelp();
        } else if (text.toLowerCase().contains("instrukcja")) {
            messenger.sendMessage(getString(R.string.command_instructions));
        } else if (text.toLowerCase().contains("lista obiektów")) {
            messenger.sendMessage(currentPlace.getName());
        } else if (text.toLowerCase().contains("ulgi")) {
            messenger.sendMessage(currentPlace.getDiscounts());
        }  else if (text.toLowerCase().contains("gdzie jestem")) {
            if (currentPlace.getCurrentBeaconOnActivePath() == null) {
                messenger.sendMessage("Nie można określić lokalizacji.");
            } else {
                messenger.sendMessage(currentPlace.getCurrentBeaconOnActivePath().getName());
            }
        } else if (text.toLowerCase().contains("rozkład jazdy")) {
            messenger.sendMessage(currentPlace.getTimetable());
        } else if (text.toLowerCase().contains("lista komend")) {
            messenger.sendMessage("Instrukcja, pomoc, lista obiektów, ulgi, rozkład jazdy, gdzie jestem");
        }
    }

    private void CallForHelp() {
        //TODO: send sms message or application notification
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messenger.sendMessage("Wezwano pomoc.");

            }
        }, 2000);
    }

    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

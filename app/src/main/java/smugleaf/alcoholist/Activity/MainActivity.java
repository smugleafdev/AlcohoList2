package smugleaf.alcoholist.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import smugleaf.alcoholist.Varvet.BarcodeReaderSample.Barcode.BarcodeCaptureActivity;
import smugleaf.alcoholist.R;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ListView listView;
    String[] listItem;
    FloatingActionButton fab, fabPaste, fabQr, fabNfc;
    boolean isFabOpen;
    NfcAdapter nfcAdapter;
    TextView pasteResult, qrResult, nfcResult;
    Switch firstSwitch, secondSwitch;

    PendingIntent pendingIntent;
    IntentFilter[] writeTagFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        pasteResult = findViewById(R.id.paste_result);
        qrResult = findViewById(R.id.qr_result);
        nfcResult = findViewById(R.id.nfc_result);

        setupListView();
        setupDrawerLayout();
        setupSwitches();
        setupFloatingActionButtons();
        setupNfcAdapter();

        if (getIntent() != null) {
            handleIntent(getIntent());
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        // TODO: This isn't being used regardless of CLEAR_TOP or SINGLE_TOP
//        toast("onNewIntent");
//        handleIntent(intent);
//    }

//    NdefRecord uriRecord = new NdefRecord(
//            NdefRecord.TNF_ABSOLUTE_URI,
//            "http://developer.android.com/index.html".getBytes(Charset.forName("US-ASCII")),
//            new byte[0], new byte[0]);

    private void setupNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            toast("This device doesn't support NFC.");
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter discovery = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            writeTagFilters = new IntentFilter[]{discovery};
        }
    }

    private void handleIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            toast("NDEF discovered");
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];

//                toast("NFC data received. " + messages.toString());

                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
//                    toast("Message " + i + ": " + rawMessages[i].toString());
                }

                for (NdefRecord r : messages[0].getRecords()) {
                    if (r.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                        if (Arrays.equals(r.getType(), NdefRecord.RTD_TEXT)) {
                            byte[] payload = r.getPayload();
                            boolean isUtf8 = (payload[0] & 0x080) == 0;
                            int languageLength = payload[0] & 0x03F;
                            int textLength = payload.length - 1 - languageLength;
                            try {
                                String languageCode = new String(payload, 1, languageLength, "US-ASCII");
                                String payloadText = new String(payload, 1 + languageLength, textLength, isUtf8 ? "UTF-8" : "UTF-16");

                                nfcResult.setText(payloadText);
                            } catch (UnsupportedEncodingException e) {
                                throw new AssertionError("UTF-8 is unknown");
                            }
                        }
                    }
                }

//                NdefMessage msg = messages[0];
//
//                try {
//                    String nfcText = new String(msg.getRecords()[0].getPayload(), "UTF-8");
//                    nfcResult.setText(nfcText);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    private void setupSwitches() {
        firstSwitch = findViewById(R.id.toggle_first);
        firstSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toast("First switch on: " + isChecked);
            }
        });

        secondSwitch = findViewById(R.id.toggle_second);
        secondSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toast("Second switch on: " + isChecked);
            }
        });
    }

    private void setupFloatingActionButtons() {
        fab = findViewById(R.id.fab);
        fabPaste = findViewById(R.id.fabPaste);
        fabQr = findViewById(R.id.fabQr);
        fabNfc = findViewById(R.id.fabNfc);

        fab.setOnClickListener(fabClickListener);
        fabPaste.setOnClickListener(fabClickListener);
        fabQr.setOnClickListener(fabClickListener);
        fabNfc.setOnClickListener(fabClickListener);

        fabPaste.addOnHideAnimationListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                animation.setStartDelay(100);
            }
        });
        fabQr.addOnHideAnimationListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                animation.setStartDelay(50);
            }
        });

        fabQr.addOnShowAnimationListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                animation.setStartDelay(50);
            }
        });
        fabNfc.addOnShowAnimationListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                animation.setStartDelay(100);
            }
        });
    }

    private void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(drawerListener);
    }

    private void setupListView() {
        listView = findViewById(R.id.listView);
        listItem = getResources().getStringArray(R.array.array_technology);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                toast(adapter.getItem(position));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            closeFabMenu();
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

            switch (v.getId()) {
                case R.id.fab:
                    toggleFab();
                    break;
                case R.id.fabPaste:
                    pasteFromClipboard();
                    break;
                case R.id.fabQr:
                    launchQrReader();
                    break;
                case R.id.fabNfc:
                    broadcastNfc();
                    break;
                default:
                    toast("[FloatingActionButton ERROR]");
                    break;
            }
        }
    };

    private void toggleFab() {
        if (!isFabOpen) {
            showFabMenu();
        } else {
            closeFabMenu();
        }

        // TODO: Implement material design response
        // I'm thinking maybe start as a plus sign and rotate into the download icon while menu is open?
    }

    private void showFabMenu() {
        isFabOpen = true;

        fabPaste.show();
        fabQr.show();
        fabNfc.show();
    }

    private void closeFabMenu() {
        isFabOpen = false;

        fabPaste.hide();
        fabQr.hide();
        fabNfc.hide();
    }

    private void pasteFromClipboard() {
        closeFabMenu();

        // TODO: Grab clipboard
        toast("Paste clipboard");
        pasteResult.setText("Nothing pasted");
    }

    private void launchQrReader() {
        toast("QR");
        closeFabMenu();

        // TODO: Check camera is available first. Currently it crashes on the emu.
        Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
        startActivityForResult(intent, 1);
    }

    private void broadcastNfc() {
        closeFabMenu();

        // TODO: Grab clipboard
        toast("NFC");
        nfcResult.setText("No NFC detected");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode b = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = b.cornerPoints;
                    qrResult.setText(b.displayValue);
                } else {
                    qrResult.setText("NO BARCODE CAPTURED");
                }
            } else {
                toast("ERROR");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.SimpleDrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            // Respond when the drawer's position changes
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            // Respond when the drawer is opened
//                        Toast.makeText(MainActivity.this, "onDrawerOpened", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            // Respond when the drawer is closed
//                        Toast.makeText(MainActivity.this, "onDrawerCloser", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            // Respond when the drawer motion state changes
        }
    };

    private void toast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
        Log.d("toast", string);
    }
}
package smugleaf.alcoholist.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

import java.util.ArrayList;

import smugleaf.alcoholist.List.CustomListAdapter;
import smugleaf.alcoholist.List.ListItem;
import smugleaf.alcoholist.Nfc.NfcHandler;
import smugleaf.alcoholist.R;
import smugleaf.alcoholist.Varvet.BarcodeReaderSample.Barcode.BarcodeCaptureActivity;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    DrawerLayout drawerLayout;
    ListView listView;
    //    String[] listItem;
    // TODO: Convert ot RecyclerView?
    CustomListAdapter listAdapter;
    FloatingActionButton fab, fabPaste, fabQr, fabNfc;
    boolean isFabOpen;
    String updatePaste, updateQr, updateNfc;
    NfcAdapter nfcAdapter;
    NfcHandler nfcHandler;
//    TextView pasteResult, qrResult, nfcResult;

    Switch firstSwitch, secondSwitch;

    PendingIntent pendingIntent;
    IntentFilter[] writeTagFilters;

    ArrayList<ListItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        setTitle("Local Relic");

        // TODO: Remove these eventually
//        pasteResult = findViewById(R.id.paste_result);
//        qrResult = findViewById(R.id.qr_result);
//        nfcResult = findViewById(R.id.nfc_result);

        itemList = new ArrayList<>();
        itemList.add(new ListItem("beer_tulip", "Bee Pollen Saison", "Mixed culture saison brewed with bee pollen", "7.1%, $6"));
        itemList.add(new ListItem("beer_tulip", "Boysenberry Saison", "Mixed culture saison brewed with boysenberries", "7.0%, $6"));
        itemList.add(new ListItem("beer_ipa", "Dry Hopped Lager", "Lager double dry hopped with monroe hops", "6.8%, $6"));
        itemList.add(new ListItem("beer_weizen", "Rye Pale Ale", "Pale ale brewed with rye", "8.5%, $6"));
        itemList.add(new ListItem("beer_ipa", "Brut IPA", "Triple dry hopped india pale ale", "7.3%, $6"));
        itemList.add(new ListItem("beer_ipa", "Hazy Pacific Gem IPA", "Hazy but west coast style IPA brewed with one percent New Zealand grown pacific gem hops", "6.8%, $6"));
        itemList.add(new ListItem("beer_tulip", "Sour Saison", "Ale brewed with brettanomyces and lactobacillus", "6.3%, $6"));
        itemList.add(new ListItem("wine_port", "Calypso Brett", "India pale ale brewed with one hundred percent calypso hops and fermented with brettanomyces", "8.2%, $7"));
        itemList.add(new ListItem("beer_pilsner_footed", "Saint Arnold's Braggot", "Beer and mead hybrid brewed with colorado honey for Feast of St. Arnold", "9.7%, $7"));
        itemList.add(new ListItem("beer_snifter", "Pecan Coffee Stout", "Ale brewed with pecans and coffee", "8.4%, $6"));
        itemList.add(new ListItem("beer_snifter", "BBA Oatmeal Stout", "Imperial stout brewed with flaked oats and aged in a whiskey barrel", "8.7%, $7"));

        setupListView();
        setupDrawerLayout();
        setupSwitches();
        setupFloatingActionButtons();
        setupNfcAdapter();

        if (getIntent() != null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        toast("onNewIntent");
        handleIntent(intent);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return nfcHandler.createNdefMessage();
    }

    private void setupNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            toast("This device doesn't support NFC.");
        } else if (!nfcAdapter.isEnabled()) {
            toast("NFC is disabled.");
        } else {
            nfcHandler = new NfcHandler(this);
            nfcAdapter.setNdefPushMessageCallback(this, this);

            pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter discovery = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            writeTagFilters = new IntentFilter[]{discovery};
        }
    }

    private void handleIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            // TODO: This is a lot of garbage testing code. Refactor eventually.
            if (intent.getType().equals("text/plain")) {
//                nfcResult.setText(nfcHandler.readNfc(intent));
                updateNfcResult(nfcHandler.readNfc(intent));
//                nfcResult.setText(nfcHandler.writeToNfc(intent));
            } else if (intent.getType().equals("application/smugleaf.alcoholist")) {
                toast("Holy shit it has my app info!");
//                nfcResult.setText(nfcHandler.readNfc(intent));
                updateNfcResult(nfcHandler.readNfc(intent));
            } else {
//                nfcResult.setText(nfcHandler.writeToNfc(intent));
                updateNfcResult(nfcHandler.writeToNfc(intent));
            }
//        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
//            writeToNfc(intent);
        }
    }

    private void setupSwitches() {
        firstSwitch = findViewById(R.id.toggle_first);
        firstSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toast("First switch on: " + isChecked);
//                itemList.set(1, new ListItem("copper_mug", "Mule", "Mixed culture saison brewed with boysenberries", "7.0%, $6"));
//                for (ListItem i : itemList) {
//
//                }
                listAdapter.setDisplayIcons(isChecked);
                listAdapter.notifyDataSetChanged();
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
        listAdapter = new CustomListAdapter(this, itemList);
        listView = findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

//        ArrayList<ListItem> listItems = new ArrayList<ListItem>();

//        listItem = getResources().getStringArray(R.array.array_technology);
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                toast(adapter.getItem(position));
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            closeFabMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
            case R.id.action_write_nfc:
                // TODO: Write to NFC now
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
//                    writeToNfc();
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
        // I'm thinking maybe start as a download sign and turn into the X cancel button?
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

        toast("Paste clipboard");

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = clipboardManager.getPrimaryClip();
//        pasteResult.setText(clip.getItemAt(0).getText().toString());
        updatePasteResult(clip.getItemAt(0).getText().toString());
    }

    private void updatePasteResult(String string) {
        updatePaste = string;
        invalidateOptionsMenu();
    }

    private void updateQrResult(String string) {
        updateQr = string;
        invalidateOptionsMenu();
    }

    private void updateNfcResult(String string) {
        updateNfc = string;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.filler_paste_result).setTitle("Paste result: " + updatePaste);
        menu.findItem(R.id.filler_qr_result).setTitle("QR result: " + updateQr);
        menu.findItem(R.id.filler_nfc_result).setTitle("NFC result: " + updateNfc);

        return super.onPrepareOptionsMenu(menu);
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

//        toast("NFC shit about to go down");
        toast("You haven't implemented anything yet.");
//        nfcResult.setText("No NFC detected");
        NfcHandler nfcHandler = new NfcHandler(this);
        // TODO: implement foreground dispatch shit here
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode b = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = b.cornerPoints;
//                    qrResult.setText(b.displayValue);
                    updateQrResult(b.displayValue);
                } else {
//                    qrResult.setText("NO BARCODE CAPTURED");
                    updateQrResult("NO BARCODE CAPTURED");
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
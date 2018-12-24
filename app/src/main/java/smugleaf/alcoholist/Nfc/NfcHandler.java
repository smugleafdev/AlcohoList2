package smugleaf.alcoholist.Nfc;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class NfcHandler {

    private Context context;

    public NfcHandler(Context context) {
        this.context = context;
    }

    public String readNfc(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];

            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }

            for (NdefRecord r : messages[0].getRecords()) {
                if (r.getTnf() == NdefRecord.TNF_MIME_MEDIA) {
                    byte[] payload = r.getPayload();
                    try {
                        String payloadText = new String(payload, 0, payload.length, "UTF-8");
                        return payloadText;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return "Read error";
                    }
                }
            }
        }

        return "Read failed, nothing happened";
    }

    public String writeToNfc(Intent intent, String sheetUrl) {
        NdefMessage message = createNdefMessage(sheetUrl);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null) {
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(message);
                        nForm.close();
                        return "Tag written under if!";
                    } else {
                        return "Error, tag not written: nForm == null";
                    }
                } else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(message);
                    ndefTag.close();
                    return "Tag written under else!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("nfc bitches", "broked");
                return "Error, tag not written";
            }
        } else {
            return "Error, tag not written for reasons";
        }
    }

    public NdefMessage createNdefMessage(String url) {
        String text = "https://docs.google.com/spreadsheets/d/1W4AaC49V-h49RvnCT5DkpEC7274Q0tPjcPKmKLvk19U/edit?usp=sharing";
        // TODO: Fix this so it trims the above URL into just the keyID, hopefully cleanly with REGEX!
//        if (url.contains("docs.google.com")) {
//            url.
//            url = url.replaceAll("/spreadsheets/d/([a-zA-Z0-9-_]+)", "");
//        }
//        String text = "1W4AaC49V-h49RvnCT5DkpEC7274Q0tPjcPKmKLvk19U";

        // Record launches Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());
        // Record with data URL, hopefully
//        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, new String("application/" + context.getPackageName()).getBytes(Charset.forName("US-ASCII")), null, text.getBytes());

//        NdefRecord relayRecord = NdefRecord.createExternal(context.getPackageName(), "appurl", text.getBytes());
        NdefRecord relayRecord = NdefRecord.createUri(text);

        return new NdefMessage(new NdefRecord[] {relayRecord, appRecord});
    }
}
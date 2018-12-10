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
                // This is for reading boring plain text records but I don't need that crap
//                if (r.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
//                    if (Arrays.equals(r.getType(), NdefRecord.RTD_TEXT)) {
//                        byte[] payload = r.getPayload();
//                        boolean isUtf8 = (payload[0] & 0x080) == 0;
//                        int languageLength = payload[0] & 0x03F;
//                        int textLength = payload.length - 1 - languageLength;
//                        try {
//                            String payloadText = new String(payload, 1 + languageLength, textLength, isUtf8 ? "UTF-8" : "UTF-16");
////                            nfcResult.setText(payloadText);
//                            return "TNF_WELL_KNOWN: " + payloadText;
//                        } catch (UnsupportedEncodingException e) {
////                            throw new AssertionError("UTF-8 is unknown");
//                            e.printStackTrace();
//                            return "UTF-8 is unknown";
//                        }
//                    }
//                }
                if (r.getTnf() == NdefRecord.TNF_MIME_MEDIA) {
                    byte[] payload = r.getPayload();
                    try {
                        String payloadText = new String(payload, 0, payload.length, "UTF-8");
                        return "TNF_MIME_MEDIA: " + payloadText;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return "Read error";
                    }
                }
            }
        }

        return "Read failed, nothing happened";
    }

    public String writeToNfc(Intent intent) {
//        NdefRecord mimeRecord = NdefRecord.createMime("application/smugleaf.alcoholist", "Beam me up, Android".getBytes(Charset.forName("US-ASCII")));
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//        NdefMessage message = new NdefMessage(new NdefRecord[] { mimeRecord });
//
//        Ndef ndef = Ndef.get(tag);
//
//        try {
//            ndef.connect();
//            ndef.writeNdefMessage(message);
//        } catch (FormatException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String text = ("Beam me up, Android!");

//        NdefRecord mimeRecord = NdefRecord.createMime("application/smugleaf.alcoholist");
//        NdefMessage message = new NdefMessage(new NdefRecord[]{NdefRecord.createMime("application/smugleaf.alcoholist", text.getBytes());

        // Record launches Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // Record with data URL, hopefully
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                new String("application/" + context.getPackageName()).getBytes(Charset.forName("US-ASCII")),
                null, text.getBytes());

        NdefMessage message = new NdefMessage(new NdefRecord[] {relayRecord, appRecord});

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null) {
                    // Let's try to format the Tag in NDEF
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
                return "Error, tag not written";
            }
        } else {
            return "Error, tag not written for reasons";
        }
    }
}
package smugleaf.alcoholist.Varvet.BarcodeReaderSample.Barcode;

import android.content.Context;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

class BarcodeTracker extends Tracker<Barcode> {

    private BarcodeGraphicTrackerCallback listener;

    public interface BarcodeGraphicTrackerCallback {
        void onDetectedQrCode(Barcode barcode);
    }

    BarcodeTracker(Context listener) {
        this.listener = (BarcodeGraphicTrackerCallback) listener;
    }

    @Override
    public void onNewItem(int id, Barcode item) {
        if (item.displayValue != null) {
            listener.onDetectedQrCode(item);
        }
    }
}
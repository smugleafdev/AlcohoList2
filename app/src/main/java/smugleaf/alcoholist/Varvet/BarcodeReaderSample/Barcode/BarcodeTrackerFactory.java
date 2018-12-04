package smugleaf.alcoholist.Varvet.BarcodeReaderSample.Barcode;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private Context context;

    BarcodeTrackerFactory(Context context) {
        this.context = context;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new BarcodeTracker(context);
    }
}
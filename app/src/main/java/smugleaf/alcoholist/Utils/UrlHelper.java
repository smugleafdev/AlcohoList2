package smugleaf.alcoholist.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlHelper {

    Context context;

    public UrlHelper(Context context) {
        this.context = context;
    }

    public String parseUrl(String link) {

        // TODO: Adding this if messes with a lot of the logic below. Take another look at it.
        if (link.contains("docs.google.com")) {
            try {
                URL url = new URL(link);

                if (url.toString().isEmpty()) {
                    toast("Empty URL");
                    return "";
                } else if (url.getAuthority().contains("docs.google.com")) {
                    link = url.getPath();
                    link = link.replace("/spreadsheets/d/", "");
                    link = link.replace("/edit", "");
                } else {
                    toast("Invalid URL");
                    return "";
                }
            } catch (MalformedURLException e) {
                toast("MalformedURLException");
                e.printStackTrace();
                return "";
            }
        }

        return "https://spreadsheets.google.com/tq?key=" + link;
    }

    private void toast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        Log.d("toast", string);
    }
}
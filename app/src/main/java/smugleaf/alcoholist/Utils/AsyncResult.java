package smugleaf.alcoholist.Utils;

import org.json.JSONObject;

public interface AsyncResult {
    void onResult(JSONObject object);
}
package PushNotifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class CarBCFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "REG_TOKEN";
    @Override
    public void onTokenRefresh() {
        String instance_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,instance_token);
    }
}

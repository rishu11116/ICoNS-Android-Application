package app.racdeveloper.com.ICoNS.pushNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Rachit on 10/15/2016.
 */
public class StartServiceOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //start pending intent service and alarm manager
//        if (PreferenceManager.getDefaultSharedPreferences(context).getString("regId",null)!=null)
//            FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
    }
}

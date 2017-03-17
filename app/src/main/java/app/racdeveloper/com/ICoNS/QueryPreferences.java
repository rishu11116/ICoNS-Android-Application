package app.racdeveloper.com.ICoNS;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Rachit on 10/10/2016.
 */
public class QueryPreferences {
    private static final String PREF_TOKEN_KEY= "token";
    private static final String PREF_ROLLNO_KEY= "rollno";
    private static final String NOTIF_GENERATION_ID= "notifGenerationID";
    private static final String INTRO_NEEDED= "introNeeded";

    // Token saved in shared Preference
    public static boolean isTokenSet(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_TOKEN_KEY, null)!=null;
    }

    public static String getToken(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_TOKEN_KEY, null);
    }

    public static void setToken(Context context, String token){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_TOKEN_KEY, token)
                .apply();
    }
    // Token_ends

    // Logged in User Roll No.
    public static String getRollNo(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_ROLLNO_KEY, null);
    }

    public static void setRollNo(Context context, String rollno){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_ROLLNO_KEY, rollno)
                .apply();
    }
    // Roll No ends

    //Unique ID for notification generation
    public static int getNotificationID(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(NOTIF_GENERATION_ID, 0);
    }

    public static void incrementNotificationID(Context context, int id){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(NOTIF_GENERATION_ID, (id+1) % 1000)
                .apply();
    }
    // end of UniqueID for notification generation

    //App start for 1st time
    public static boolean isIntroNeeded(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(INTRO_NEEDED, true);
    }

    public static void setIntroNeed(Context context, boolean isIntroNeeded){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(INTRO_NEEDED, isIntroNeeded)
                .apply();
    }
    // end of Intro need

}

/**
 * 
 */
package ch.cri.pingmonitor;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * Handles alarms set to turn off Wifi with a delay 
 * @author sven
 *
 */
public class AlarmReceiver extends BroadcastReceiver
{		 
	private static String TAG = "AlarmReceiver";
	
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d(TAG, "Alarm received");
	}
}


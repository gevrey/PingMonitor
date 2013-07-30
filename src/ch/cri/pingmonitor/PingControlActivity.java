package ch.cri.pingmonitor;

import java.io.IOException;
import java.util.Calendar;

import ch.cri.pingmonitor.util.SystemUiHider;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.AsyncTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */


public class PingControlActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

	// UI View Elements
	TextView txtStatus;				// TextView that displays the current ping status
	ToggleButton b1;
	Button m_ButtonAlarm;
	EditText m_EditText; 
	
	Boolean hostActive;
	String host;
	
	public class MyAlarmReceiver extends BroadcastReceiver { 
	     @Override
	     public void onReceive(Context context, Intent intent) {
	         Toast.makeText(context, "Alarm went off", Toast.LENGTH_LONG).show();
	     }
	}	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pingcontrol);
	
		// Set UI View Elements
		txtStatus = (TextView)findViewById(R.id.txtStatus);
	    b1 = (ToggleButton) findViewById(R.id.toggleButtonOnOff);
	    m_ButtonAlarm = (Button) findViewById(R.id.buttonAlarm);
	    m_EditText = (EditText) findViewById(R.id.target);

	    View.OnClickListener myhandler1 = new View.OnClickListener() {
		    public void onClick(View v) {
			    host = m_EditText.getText().toString();
		        String myString;
		        myString = "Unreachable";
		        int myColor;
		        myColor = Color.WHITE;
		        txtStatus.setText("");
		        
		        if (b1.isChecked()) {
		        	m_EditText.setEnabled(false);
		        } else {
		        	m_EditText.setEnabled(true);
		        }
				new pingHostTask().execute();
		        
		        Toast.makeText(PingControlActivity.this, "Host is " + myString, Toast.LENGTH_SHORT).show();
		    	
		    	View mlayout= findViewById(R.id.relativeLayout);
		    	// set the color 
		    	mlayout.setBackgroundColor(myColor);
		    	// you can use setBackgroundResource() and pass appropriate ID
		    	// if you want a drawable bundled as resource in the background
		    	// mlayout.setBackgroundResource(R.drawable.background_img);		    		    	
		    	
		    };
	    };
	    b1.setOnClickListener(myhandler1);

	    View.OnClickListener handlerButtonAlarm = new View.OnClickListener() {
		    public void onClick(View v) {
		        Toast.makeText(PingControlActivity.this, "Alarm set", Toast.LENGTH_SHORT).show();
		        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		        Intent intent = new Intent(PingControlActivity.this, MyAlarmReceiver.class);
		        PendingIntent pendingIntent = PendingIntent.getBroadcast(PingControlActivity.this, 0, intent, 0);
		        Calendar time = Calendar.getInstance();
		        time.setTimeInMillis(System.currentTimeMillis());
		        time.add(Calendar.SECOND, 5);
		        alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);		        
		    };
	    };
	    m_ButtonAlarm.setOnClickListener(handlerButtonAlarm);		
	}	
	
	
	////
	// AsyncTask class to ping a remote host
	////
	private class pingHostTask extends AsyncTask<Void, Void, Boolean> {
		
		// This method is what performs the tasks you wish to perform in the background. In this case, it is the ping request.
		@Override
		protected Boolean doInBackground(Void... params) {
			
			// before pinging, publish the progress.
			// In this case, there is no progress, but this allows for the class to update the status textView
			publishProgress();
			// Return the boolean value of pingHost
			return pingHost();
		}
		
		// This method is called automatically after the 'doinBackground' tasks have finished
		@Override
		protected void onPostExecute(Boolean result) {			
			hostActive = result;
			// Update the status TextView depending on the result of the ping
			if(result) {
				txtStatus.setText("host " +host+ " found");
			} else { 
				txtStatus.setText("host " +host+ " not found");
			}
		}
		
		// This method is automatically called when the 'publishProgress' method is called within the 'doInBackground'
		@Override
		protected void onProgressUpdate(Void... values) {
			// Simply update the status TextView, to inform the user that the app is currently performing the ping
			txtStatus.setText("Checking for host " + host);
		}
		
		// This method is what actually pings the host
		private boolean pingHost() {
			
			Process p1 = null;		// Create a process object, which will be used to perform the ping
			int returnVal = 0;		// Set ping return status to 0, which automatically declares it as fail

			try {
				// Since Android is Unix base, we can perform a unix ping command. This will return 0 if the ping was unsuccessful, or 1 if the ping returned true
				p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 " + host);
			} catch (IOException e) {
				Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
			}

			try {
				returnVal = p1.waitFor();
			} catch (InterruptedException e) {
				Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
			}
			
			// return true or false, depending on the status of ping
			if (returnVal == 0) {
				return true;

			} else {
				return false;
			}
		}
	}
	
}

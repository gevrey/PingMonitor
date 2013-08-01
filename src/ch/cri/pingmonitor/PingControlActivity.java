package ch.cri.pingmonitor;

import java.io.IOException;

import ch.cri.pingmonitor.util.SystemUiHider;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
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
	Button m_ButtonAlarm;
	EditText m_EditText; 
	ToggleButton m_ButtonOnOff;
	Boolean hostActive;
	String host;
	RadioGroup m_RadioGroup;
	CheckBox m_BeepOnAlert;
	
	private PendingIntent pendingIntent;
	MyAlarmReceiver alarm = new MyAlarmReceiver();
	
	public class MyAlarmReceiver extends BroadcastReceiver { 
	     @Override
	     public void onReceive(Context context, Intent intent) {
	         //Toast.makeText(context, "Ping", Toast.LENGTH_LONG).show();
	    	 txtStatus.setText("");
	    	View mlayout= findViewById(R.id.relativeLayout);
	    	mlayout.setBackgroundColor(Color.WHITE);
	         new pingHostTask().execute();
	     }
	}	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pingcontrol);
	
		// Set UI View Elements
		txtStatus = (TextView)findViewById(R.id.txtStatus);
		m_ButtonOnOff = (ToggleButton) findViewById(R.id.toggleButtonOnOff);
	    m_ButtonAlarm = (Button) findViewById(R.id.buttonAlarm);
	    m_EditText = (EditText) findViewById(R.id.target);
	    m_RadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
	    m_BeepOnAlert = (CheckBox) findViewById(R.id.checkBox2);

	    View.OnClickListener myhandler1 = new View.OnClickListener() {
		    public void onClick(View v) {
			    host = m_EditText.getText().toString();
		        //String myString;
		        //myString = "Unreachable";
		        int myColor;
		        myColor = Color.WHITE;
		        txtStatus.setText("");
		        
		        if (m_ButtonOnOff.isChecked()) {
		        	m_EditText.setEnabled(false);

					registerReceiver(alarm, new IntentFilter("ch.cri.pingmonitor") );
			        Intent intent = new Intent("ch.cri.pingmonitor");
			        pendingIntent = PendingIntent.getBroadcast(PingControlActivity.this, 0, intent, 0);
			    	AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 2, pendingIntent); // Millisec * Second * Minute
		        
		        } else {
		        	m_EditText.setEnabled(true);
					txtStatus.setText("");
		        	AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		        	alarmMgr.cancel(pendingIntent);
		    		unregisterReceiver(alarm);
		        }

		        //new pingHostTask().execute();
		        
		        //Toast.makeText(PingControlActivity.this, "Host is " + myString, Toast.LENGTH_SHORT).show();
		    	
		    	View mlayout= findViewById(R.id.relativeLayout);
		    	// set the color 
		    	mlayout.setBackgroundColor(myColor);
		    	// you can use setBackgroundResource() and pass appropriate ID
		    	// if you want a drawable bundled as resource in the background
		    	// mlayout.setBackgroundResource(R.drawable.background_img);		    		    	
		    	
		    };
	    };
	    m_ButtonOnOff.setOnClickListener(myhandler1);

	    
	    View.OnClickListener handlerButtonAlarm = new View.OnClickListener() {
		    public void onClick(View v) {
		    	int radioButtonID = m_RadioGroup.getCheckedRadioButtonId();
		    	View radioButton = m_RadioGroup.findViewById(radioButtonID);
		    	int idx = m_RadioGroup.indexOfChild(radioButton);
		    	Toast.makeText(PingControlActivity.this, "RadioButton: " + idx, Toast.LENGTH_SHORT).show();
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
	    	int radioButtonID = m_RadioGroup.getCheckedRadioButtonId();
	    	View radioButton = m_RadioGroup.findViewById(radioButtonID);
	    	int idx = m_RadioGroup.indexOfChild(radioButton);
	    	// idx = 0 => Alert if inactive
	    	// idx = 1 => Alert if active
	    	if(result) {
				if (idx == 1) {
					txtStatus.setText(host + " is active");
			    	View mlayout= findViewById(R.id.relativeLayout);
			    	mlayout.setBackgroundColor(Color.GREEN);
			    	if (m_BeepOnAlert.isChecked()) {
				        try {
				            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				            r.play();
				        } catch (Exception e) {}				
			    	}
	    		}
			} else {
				if (idx == 0) {
					txtStatus.setText(host + " is inactive");
			    	View mlayout= findViewById(R.id.relativeLayout);
			    	mlayout.setBackgroundColor(Color.RED);
			    	if (m_BeepOnAlert.isChecked()) {
				        try {
				            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				            r.play();
				        } catch (Exception e) {}				
			    	}
				}
			}
		}
		
		// This method is automatically called when the 'publishProgress' method is called within the 'doInBackground'
		@Override
		protected void onProgressUpdate(Void... values) {
			// Simply update the status TextView, to inform the user that the app is currently performing the ping
			txtStatus.setText("Checking " + host);
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

package ch.cri.pingmonitor;

import java.io.IOException;
import ch.cri.pingmonitor.util.SystemUiHider;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
public class FullscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

	// UI View Elements
	TextView txtStatus;				// TextView that displays the current ping status
	ToggleButton b1;
	
	Boolean hostActive;
	String host = "192.168.1.201";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
	
		// Set UI View Elements
		txtStatus = (TextView)findViewById(R.id.txtStatus);
	    b1 = (ToggleButton) findViewById(R.id.toggleButtonOnOff);

	    View.OnClickListener myhandler1 = new View.OnClickListener() {
		    public void onClick(View v) {
		        String myString;
		        myString = "Unreachable";
		        int myColor;
		        myColor = Color.WHITE;
		        
				new pingHostTask().execute();
		        
		        Toast.makeText(FullscreenActivity.this, "Host is " + myString, Toast.LENGTH_SHORT).show();
		    	
		    	View mlayout= findViewById(R.id.relativeLayout);
		    	// set the color 
		    	mlayout.setBackgroundColor(myColor);
		    	// you can use setBackgroundResource() and pass appropriate ID
		    	// if you want a drawable bundled as resource in the background
		    	// mlayout.setBackgroundResource(R.drawable.background_img);		    		    	
		    	
		    };
	    };
	    b1.setOnClickListener(myhandler1);
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
			txtStatus.setText("");
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
			txtStatus.setText("Checking for host");
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

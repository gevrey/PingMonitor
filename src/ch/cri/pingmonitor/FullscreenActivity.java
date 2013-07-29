package ch.cri.pingmonitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ch.cri.pingmonitor.util.SystemUiHider;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
	ToggleButton b1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
	
	    b1 = (ToggleButton) findViewById(R.id.toggleButtonOnOff);
	    View.OnClickListener myhandler1 = new View.OnClickListener() {
		    public void onClick(View v) {
		        String myString;
		        myString = "Unreachable";
		        boolean redexterna = false;
		        int myColor;
		        myColor = Color.WHITE;
//		        if (b1.isChecked()) {
//		        	myString = "ON";
//		        	myColor = Color.WHITE;
//		        }
		    	
		        try {
					redexterna = InetAddress.getByAddress("8.8.8.8".getBytes()).isReachable(1000);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        if (redexterna) {
		        	myString = "Reachable";
		        }
		        
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
	
//	 ////
//	 // AsyncTask class to ping a remote host
//	 ////
//	 private class pingHostTask extends AsyncTask<void boolean="boolean" void="void"> {
//	   
//	  // This method is what performs the tasks you wish to perform in the background. In this case, it is the ping request.
//	  @Override
//	  protected Boolean doInBackground(Void... params) {
//	    
//	   // before pinging, publish the progress.
//	   // In this case, there is no progress, but this allows for the class to update the status textView
//	   publishProgress();
//	   // Return the boolean value of pingHost
//	   return pingHost();
//	  }
//	   
//	  // This method is called automatically after the 'doinBackground' tasks have finished
//	  @Override
//	  protected void onPostExecute(Boolean result) {
//	    
//	   // Update the status TextView depending on the result of the ping
//	   if(result) {
//	    txtStatus.setText("host found");
//	   } else { 
//	    txtStatus.setText("no host found");
//	   }
//	  }
}

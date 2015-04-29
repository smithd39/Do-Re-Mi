package com.relldesigns.do_re_mi;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;


public class MainActivity extends Activity {

    int myIndex=0;
	int sampleRate = 8000;
	int numSamples = 3200;  //.4 of a second * 8000
	double sample[] = new double[numSamples];
	byte generatedSnd[] = new byte[2 * numSamples];
	 
	Handler handler = new Handler();
	
	String [] Notes = {"G","G#","A","A#","B","C","C#","D","D#","E","F", "F#", "G"};
	int [] Values = { -2,-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TableLayout layout = (TableLayout) findViewById(R.id.tableForButtons);
		
	    for(int i=0;i< Notes.length;i++){
	    	Button myButton = new Button(this);
			myButton.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.WRAP_CONTENT,
					TableLayout.LayoutParams.WRAP_CONTENT,
					1.0f
					));
		
			//String Notes = Notes.getString("Notes");
			myButton.setText(Notes[i]);
    		//String Values = Values.getString("Values");
			myButton.setTag(Values[i]);
			
			myButton.setPadding(0, 0, 0, 0);
			layout.addView(myButton);
			
			myButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Button me = (Button) v;
					Toast.makeText(getBaseContext(), me.getTag().toString(), Toast.LENGTH_SHORT).show();	
					
					myIndex = Integer.parseInt(me.getTag().toString());
					
				    final Thread thread = new Thread(new Runnable() {
			            public void run() {
			            	double myFreq = 440*Math.pow(2,myIndex/12.0);
			            	// 440 --> 0 --> note A
			            	//466 --> 1 --> A#
			            	//880 --> 12 --> Note A
			            	// 0 corresponds to 440 Hz --> note A
			            	// 1 corresponds to A# 
			            	// 2 corresponds to B 
			            	// etc.
			                genTone(myFreq);    // -----  makes the wave
			                handler.post(new Runnable() {

			                   public void run() {
			                        playSound();    //------  plays the wave
			                    } //run
			                }); //Runnable == post
			            }//run
			        }); //Runnable --Thread
				    thread.start();     
					
				} //onClick
				}); //end setOnClickListener
			
	    }//end for
	    
	} //end onCreate
		

	void genTone(double freqOfTone){
        // fill out the array	   -- make a sine wave
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }
	
    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}


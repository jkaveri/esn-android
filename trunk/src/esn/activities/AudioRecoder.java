/*
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 */


package esn.activities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioRecoder {
	public static final int IS_STARTING = 1;
	public static final int IS_PAUSED = 2;
	public static final int IS_STOP = 0;

	private static final int FREQUENCY = 8000;
	private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
	private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static int REC_BUFFER_SIZE;
	private static final int CALLBACK_PERIOD = 50;
	
	private int status = 0;
	private AudioRecord audioRecord;
	private Thread th;
	private ByteArrayOutputStream bufferStream;
	private DataOutputStream dos;
	private Runnable run;

	public AudioRecoder() {
		REC_BUFFER_SIZE = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL_CONFIG, AUDIO_ENCODING);
		audioRecord = new AudioRecord(AudioSource.MIC, FREQUENCY, CHANNEL_CONFIG, AUDIO_ENCODING, REC_BUFFER_SIZE);
		
		bufferStream = new ByteArrayOutputStream();
		dos = new DataOutputStream(bufferStream);
		
		run = new Runnable() {

			@Override
			public void run() {
				short[] buffer = new short[REC_BUFFER_SIZE]; 
				audioRecord.startRecording();				
				
				while (status == IS_STARTING) {
					int count = audioRecord.read(buffer, 0, REC_BUFFER_SIZE);
					Log.i("AudioRecoder", "Writing new data to buffer");
					for(int i = 0; i < count; i++){
						try {
							dos.writeShort(buffer[i]);
						} catch (IOException e) {
							Log.e("AudioRecoder", "IOException writeShort() at output stream");
						}
					}
					try {
						Thread.sleep(CALLBACK_PERIOD);
					} catch (InterruptedException e) {
						Log.i("AudioRecoder", "Thread on destroy");
					}
				}
			}
		};
	}

	public void startRecording() {
		if (status != IS_STARTING) {
			if (status == IS_STOP) {
				bufferStream.reset();
			}
			status = IS_STARTING;
			th = new Thread(run);
			th.start();
		}
	}
	
	public void pauseRecording() {
		if(status == IS_STARTING){
			status = IS_PAUSED;
			try {
				dos.flush();
			} catch (IOException e) {
				Log.e("AudioRecoder", "IOException writeShort() at output stream");
			}
			audioRecord.stop();
			th.interrupt();
			th = null;
		}
	}

	public void stopRecording() {
		if (status != IS_STOP) {
			status = IS_STOP;
			try {
				dos.flush();
				dos.close();
			} catch (IOException e) {
				Log.e("AudioRecorder", "IOException close output stream");
			}
			audioRecord.stop();
			if(th != null){
				th.interrupt();
				th = null;
			}
		}
	}

	public byte[] getBufferRecod() {
		return bufferStream.toByteArray();
	}

	public int getStatus() {
		return status;
	}
}
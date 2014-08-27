package com.cerigo.foleyMachine;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;


public class VidRec extends Activity {
	
	final int NUMBEROFSAMPLES = 12;
	
	private Camera mCamera;
	private CameraPreview mPreview;
	private MediaRecorder mMediaRecorder;
	ImageView image;
	private int[] goingInt;
	private SoundPool soundPool;
	private int presetNum = 0;
	private int highestPreset;
	private float volume = 1;
	private HashMap<Integer, Integer> idsAndPlaying;
	private AudioManager  mAudioManager;
	private ImageButton[] buttons;
	private Bitmap[] bitMaps;
	
	public static Camera.Parameters param;
	
	
	
	public static final int MEDIA_TYPE_VIDEO = 2;

	private boolean isRecording = false;
	
	private static final String TAG = "Camera_Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    
		highestPreset = NUMBEROFSAMPLES/4;
		
		Bundle bundle = getIntent().getExtras();
		
		goingInt = bundle.getIntArray("goingInts");
		String name = bundle.getString("name");
		//Log.d("name", name);
		
		for(int i = 0; i<NUMBEROFSAMPLES; i++)
		{
			
			String strValue = getApplicationContext().getResources().getResourceEntryName(goingInt[i]);
			
			//Log.d("Id of Seceted Bank", strValue);
		}
		
		
		makeMedia(this);
		
		
		
		
		
		
		
		
		mCamera = getCameraInstance();
		setContentView(R.layout.activity_vid_rec);
		
		makeButtonList(this);
		makeBitMaps(name, this);
		makeThingsGreen(this);
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview, 0);
		
    	
		
	
   
        //Listener TO the Capture Button
		Button captureButton = (Button) findViewById(R.id.button_capture);
		
		captureButton.setOnClickListener(
		    new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            if (isRecording) {
		                // stop recording and release camera
		                mMediaRecorder.stop();  // stop the recording
		                releaseMediaRecorder(); // release the MediaRecorder object
		                mCamera.lock(); 
		                mCamera.startPreview();// take camera access back from MediaRecorder

		                // inform the user that recording has stopped
		                setCaptureButtonText("Capture");
		                isRecording = false;
		            } else {
		                // initialize video camera
		                if (prepareVideoRecorder(mCamera, mPreview)) {
		                    // Camera is available and unlocked, MediaRecorder is prepared,
		                    // now you can start recording
		                    mMediaRecorder.start();

		                    // inform the user that recording has started
		                    setCaptureButtonText("Stop");
		                    isRecording = true;
		                } else {
		                    // prepare didn't work, release the camera
		                    releaseMediaRecorder();
		                    // inform user
		                }
		            }
		        }
		    }
		);
		
		
	}
	
	private void makeBitMaps(String name, Context context) {
		
		bitMaps = new Bitmap[3];
		
		
		String nametest = new String(name);
		nametest = name.concat(String.valueOf((1)));
		int in = context.getResources().getIdentifier(nametest, "drawable", context.getPackageName());
		if(in == 0){
			name = "action";
			
		}
			
		
		for(int i=0; i<3; i++){
			String nameNew = new String(name);
			
			nameNew = name.concat(String.valueOf((i+1)));
			Log.d("resIdName", nameNew);
			
			Integer resId = context.getResources().getIdentifier(nameNew, "drawable", context.getPackageName());
			Log.d("resId", resId.toString());
			
				bitMaps[i] = decodeSampledBitmapFromResource(getResources(), resId, 300, 300);
		}
		
		
	}
   

	private void makeThingsGreen(Context context){
		for(int i=0; i<4; i++){
			buttons[i].setImageBitmap(bitMaps[0]);
		}
		Button preset = (Button)findViewById(R.id.preset);
		preset.setBackgroundColor(Color.GREEN);
	}
	
	   public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}
	    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	            int reqWidth, int reqHeight) {

	        // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeResource(res, resId, options);

	        // Calculate inSampleSize
	        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	        return BitmapFactory.decodeResource(res, resId, options);
	    }
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vid_rec, menu);
		return true;
	}
	
	//Creattion of the Mesia in the soundPool
	
	private void makeMedia(Context context){
		
		//Log.d("errorAfeter", "here");
		
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

		mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		
		idsAndPlaying = new HashMap();
		
		
		
		for(int i = 0; i<NUMBEROFSAMPLES; i++)
		{
			goingInt[i] = soundPool.load(this, goingInt[i], 1);
			idsAndPlaying.put(goingInt[i], 0);
			
		}
		
		Integer curLength = new Integer(goingInt.length);
		
		
		Log.d("goingInt", goingInt.toString());
		Log.d("goingInt", curLength.toString());
		
		
	}
	
	private void makeButtonList(Context context){
		buttons = new ImageButton[4];
		
		buttons[0] = (ImageButton)findViewById(R.id.button1);
		buttons[1] = (ImageButton)findViewById(R.id.button2);
		buttons[2] = (ImageButton)findViewById(R.id.button3);
		buttons[3] = (ImageButton)findViewById(R.id.button4);
		
		
		
		
	}
	
	//Function that Each button uses to play the sounds
	
	public void playSound(View v){
		int toAdd = presetNum*4;
		
		Object toPlay = v.getTag();
		
		int curInt = Integer.valueOf(toPlay.toString());
		Log.d("PresetNUme", String.valueOf(presetNum));
		
		curInt = curInt + toAdd;
		
		int curId = goingInt[curInt];
		
		int idOfPlaying = idsAndPlaying.get(curId);
		soundPool.stop(idOfPlaying);
		idOfPlaying = soundPool.play(curId, volume, volume, 1, 0, 1f);
		idsAndPlaying.put(curId, idOfPlaying);
			
			
	
		

		
		
		}
	
	//This is to control which preset is being shown and played

	public void presetChange(View v){
			
			Log.d("Preset", "1");
			Log.d("HighestPreset", String.valueOf(highestPreset));
			
			int colorPr = Color.GREEN;
			
			
			
			
			if(presetNum == (highestPreset-1)){
				presetNum = 0;
				
			} else {
				presetNum = presetNum +1;
			}
			
			switch(presetNum)
			{
			case 0:
			
				colorPr = Color.GREEN;
				break;
				
			case 1:
				colorPr = Color.RED;
				break;
				
		case 2:
				colorPr = Color.BLUE;
				break;
			}
			
			v.setBackgroundColor(colorPr);
			for(int i=0; i<4; i++){
				buttons[i].setImageBitmap(bitMaps[presetNum]);
			}
				
				
				
			
			
		}
	
	//Capture Button text change function
	
	 private void setCaptureButtonText(String string){
	    	Button captureButton = (Button) findViewById(R.id.button_capture);
	    	
	    	captureButton.setText(string);
	    	
	    }
		
		
	
 
	
	
	
	
	
	
	////This Gets the Camera Instance
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		
		
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        Log.d("CameraAvaleable", "Camera is not available (in use or does not exist");
	    }
	    
	    
	    
	    
	    



		param = c.getParameters();
		//List<String> effects = param.getSupportedColorEffects();
		
		
		
		//Log.d("effects", effects.toString());
		
		//param.set( "cam_mode", 1 );
		param.setColorEffect(Parameters.EFFECT_MONO);
		//param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		c.setParameters( param );
		
	    return c; // returns null if camera is unavailable
	}
	
	
	
	
	
	
	
	
	
	
	///This Creates the Camera Preveiw
	
	/** A basic Camera preview class */
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		
		
	    private SurfaceHolder mHolder;
	    private Camera mCamera;

	    @SuppressWarnings("deprecation")
		public CameraPreview(Context context, Camera camera) {
	    	
	    	
	        super(context);
	        mCamera = camera;
	        
	        

	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();
	        
	        
	        mHolder.addCallback(this);
	        // deprecated setting, but required on Android versions prior to 3.0
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }
	    
	    
	    
	    

	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, now tell the camera where to draw the preview.
	        try {
	            mCamera.setPreviewDisplay(holder);
	            mCamera.startPreview();
	            mCamera.setParameters(param);
	        } catch (IOException e) {
	            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	        }
	    }
	    
	    

	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // empty. Take care of releasing the Camera preview in your activity.
	    	
	    	releaseCamera();
	    }

	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.

	        if (mHolder.getSurface() == null){
	          // preview surface does not exist
	          return;
	        }

	        // stop preview before making changes
	        try {
	            mCamera.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	        }

	        // set preview size and make any resize, rotate or
	        // reformatting changes here

	        // start preview with new settings
	        try {
	            mCamera.setPreviewDisplay(mHolder);
	            mCamera.startPreview();

	        } catch (Exception e){
	            Log.d("This One", "Error starting camera preview: " + e.getMessage());
	        }
	    }
	}
	
	
	
	
	
	//This is the section which Records that Camera and makes the File
	
	private boolean prepareVideoRecorder(Camera camera, CameraPreview preview){
		
		

	    mCamera = camera;
	    mMediaRecorder = new MediaRecorder();
	    
	    mCamera.stopPreview();
	    
	    

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);
	    
	    

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
	    
	    
	    
	    


	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	   //if(CamcorderProfile.hasProfile(mCamera, CamcorderProfile.QUALITY_HIGH)){
	    	
	    	
	    //mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
	    //Log.d("whichProfile", "High");
	    //} else  {
	    	mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    
	 		mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	 		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	 		//Log.d("whichProfile", "None");
	    	
	    //}
	    	
	    	
	    //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    
 		//mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
 		//mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    // Step 4: Set output file
	    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
	   
	   

	    // Step 5: Set the preview output
	   mMediaRecorder.setPreviewDisplay(preview.getHolder().getSurface());
	    

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		File mediaFile = null;
		File mediaStorageDir = null;
		if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

			// ok, you can use SD card...

		//String string = Environment.getExternalStorageState();
		
		
		

	    mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_DCIM), "FoleyApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
	    //Log.d("StorageDir", mediaStorageDir.toString());

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    
	    if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }
	   
		}
		Log.d("the File", mediaFile.toString());
		return mediaFile;
		//Log.d("the Path", mediaStorageDir.toString());
		//return mediaStorageDir;

	    
		
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        Log.d("pause", "1");
        if(isRecording){
        	mMediaRecorder.stop();
        	isRecording = false;
        }
        releaseMediaRecorder();
        releaseCamera();  
        releaseSoundPool(); // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            if (mCamera != null){
            mCamera.lock();
            Log.d("EnPause", "1");
            }// lock camera for later use
        }
    }

    private void releaseCamera(){
    	Log.d("releaseesCameraMethod", "1");
        if (mCamera != null){
        	if (mMediaRecorder != null) {
                mMediaRecorder.reset();   // clear recorder configuration
                mMediaRecorder.release(); // release the recorder object
                mMediaRecorder = null;
        	}
        	mCamera.stopPreview();//Need to sort out the relase of the camera when ther eis no Media. 
            mCamera.release();        // release the camera for other applications
            mCamera = null;
            Log.d("cameraRealease", "1");
        	
  
    }
        
       
    
    }
    private void releaseSoundPool(){
    	if(soundPool != null){
    	 for(int i=0; i<NUMBEROFSAMPLES; i++){
    	        int idOfPlaying = idsAndPlaying.get(goingInt[i]);
    			soundPool.stop(idOfPlaying);
    	        }
    	        soundPool.release();
    	        soundPool = null;
    	        Log.d("soundPoolReleased", "Sound released");
    	}
    }
    
   
    
    
    
   
  
}


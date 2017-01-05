/* Copyright 2017 AlaskaLinuxUser

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package com.example.alaskalinuxuser.s4camera;

    import android.os.Bundle;
    import java.io.*;
    import java.lang.*;
    import java.util.Locale;
    import android.app.Activity;
    import android.hardware.Camera;
    import android.hardware.Camera.PictureCallback;
    import android.hardware.Camera.ShutterCallback;
    import android.os.Handler;
    import android.util.Log;
    import android.view.*;
    import android.widget.*;
    import android.content.pm.*;

    public class MainActivity extends Activity implements SurfaceHolder.Callback {
        public TextView testView;
        public Camera camera;
        public SurfaceView surfaceView;
        public SurfaceHolder surfaceHolder;
        public PictureCallback rawCallback;
        public ShutterCallback shutterCallback;
        public PictureCallback jpegCallback;
        public boolean isRecording;
        public boolean threeMinutes;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isRecording = false;
            threeMinutes = false;
            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            surfaceHolder = surfaceView.getHolder();
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            surfaceHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            String[] getSu = {"su", "-c", "ls"};
            try {
                Runtime.getRuntime().exec(getSu);
            } catch (IOException e) {
                e.printStackTrace();
            }

            jpegCallback = new PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(String.format(Locale.ENGLISH, "/sdcard/Pictures/%d.jpg", System.currentTimeMillis()));
                        outStream.write(data);
                        outStream.close();
                        Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                    }

                    Toast picToast = Toast.makeText(getApplicationContext(),"Picture saved.", Toast.LENGTH_SHORT);
                    picToast.setGravity(Gravity.RIGHT|Gravity.CENTER, 0, 0);
                    picToast.show();

                    refreshCamera();
                }
            };
        }
        public void captureImage(View v) throws IOException {
            //take the picture
            camera.takePicture(null, null, jpegCallback);
        }
        public void refreshCamera() {
            if (surfaceHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            // stop preview before making changes
            try {
                camera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            // set preview size and make any resize, rotate or
            // reformatting changes here
            // start preview with new settings
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
            }
        }
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            refreshCamera();
        }
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                // open the camera
                camera = Camera.open();
            } catch (RuntimeException e) {
                // check for exceptions
                System.err.println(e);
                return;
            }
            Camera.Parameters param;
            param = camera.getParameters();
            // modify parameter
            param.setPreviewSize(640, 480);
            camera.setParameters(param);
            try {
                // The Surface has been created, now tell the camera where to draw
                // the preview.
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
                // check for exceptions
                System.err.println(e);
                return;
            }
        }
        public void surfaceDestroyed(SurfaceHolder holder) {
            // stop preview and release camera
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        public void stopRecord (View stopView) throws IOException {

            String[] stopNow = {"su", "-c", "pkill -2 screenrecord"};
            Runtime.getRuntime().exec(stopNow);

            ImageView stopTime = (ImageView)findViewById(R.id.stop);
            stopTime.setVisibility(View.INVISIBLE);

            //Rename file.
            File from      = new File("/sdcard/Pictures/video.mp4");
            File to        = new File(String.format(Locale.ENGLISH, "/sdcard/Pictures/%d.mp4", System.currentTimeMillis()));
            from.renameTo(to);
            //Set boolean.

            isRecording=false;

            //define icons
            ImageView nowRecord = (ImageView)findViewById(R.id.record);
            ImageView nowCapture = (ImageView)findViewById(R.id.capture);
            ImageView nowTime = (ImageView)findViewById(R.id.theTime);
            //Show icons.
            nowCapture.setVisibility(View.VISIBLE);
            nowRecord.setVisibility(View.VISIBLE);
            nowTime.setVisibility(View.VISIBLE);


        }

        public void setTime (View timeView) throws IOException {

            //ImageView nowRecord = (ImageView)findViewById(R.id.record);
            //ImageView nowCapture = (ImageView)findViewById(R.id.capture);
            ImageView nowTime = (ImageView)findViewById(R.id.theTime);

            if (threeMinutes) {
                threeMinutes=false;
                nowTime.setImageResource(R.drawable.m1);
            } else {
                threeMinutes=true;
                nowTime.setImageResource(R.drawable.m3);
            }

        }

        public void captureRecord (View myView) throws IOException {

            if (threeMinutes) {

                //make a recording of the surfaceview.
                String[] recordNow = {"su", "-c", "screenrecord --time-limit 180 --size 720x1280 /sdcard/Pictures/video.mp4"};
                Runtime.getRuntime().exec(recordNow);

                isRecording=true;

                //define icons
                ImageView nowRecord = (ImageView)findViewById(R.id.record);
                ImageView nowCapture = (ImageView)findViewById(R.id.capture);
                ImageView nowTime = (ImageView)findViewById(R.id.theTime);

                //Hide icons.
                nowCapture.setVisibility(View.INVISIBLE);
                nowRecord.setVisibility(View.INVISIBLE);
                nowTime.setVisibility(View.INVISIBLE);

                ImageView stopTime = (ImageView)findViewById(R.id.stop);
                stopTime.setVisibility(View.VISIBLE);

                //make it run later....
                Runnable r = new Runnable() {
                    @Override
                    public void run(){

                        //Rename file.
                        File from      = new File("/sdcard/Pictures/video.mp4");
                        File to        = new File(String.format(Locale.ENGLISH, "/sdcard/Pictures/%d.mp4", System.currentTimeMillis()));
                        from.renameTo(to);
                        //Set boolean.

                        isRecording=false;

                        //define icons
                        ImageView nowRecord = (ImageView)findViewById(R.id.record);
                        ImageView nowCapture = (ImageView)findViewById(R.id.capture);
                        ImageView nowTime = (ImageView)findViewById(R.id.theTime);
                        //Show icons.
                        nowCapture.setVisibility(View.VISIBLE);
                        nowRecord.setVisibility(View.VISIBLE);
                        nowTime.setVisibility(View.VISIBLE);
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 181000); // <-- delay time in miliseconds.

            } else {

                //make a recording of the surfaceview.
                String[] recordIng = {"su", "-c", "screenrecord --time-limit 60 --size 720x1280 /sdcard/Pictures/video.mp4"};
                Runtime.getRuntime().exec(recordIng);

                isRecording=true;

                //define icons
                ImageView nowRecord = (ImageView)findViewById(R.id.record);
                ImageView nowCapture = (ImageView)findViewById(R.id.capture);
                ImageView nowTime = (ImageView)findViewById(R.id.theTime);

                //Hide icons.
                nowCapture.setVisibility(View.INVISIBLE);
                nowRecord.setVisibility(View.INVISIBLE);
                nowTime.setVisibility(View.INVISIBLE);

                ImageView stopTime = (ImageView)findViewById(R.id.stop);
                stopTime.setVisibility(View.VISIBLE);

                //make it run later....
                Runnable s = new Runnable() {
                    @Override
                    public void run(){

                        //Rename file.
                        File from      = new File("/sdcard/Pictures/video.mp4");
                        File to        = new File(String.format(Locale.ENGLISH, "/sdcard/Pictures/%d.mp4", System.currentTimeMillis()));
                        from.renameTo(to);
                        //Set boolean.

                        isRecording=false;

                        //define icons
                        ImageView nowRecord = (ImageView)findViewById(R.id.record);
                        ImageView nowCapture = (ImageView)findViewById(R.id.capture);
                        ImageView nowTime = (ImageView)findViewById(R.id.theTime);
                        //Show icons.
                        nowCapture.setVisibility(View.VISIBLE);
                        nowRecord.setVisibility(View.VISIBLE);
                        nowTime.setVisibility(View.VISIBLE);
                    }
                };

                Handler z = new Handler();
                z.postDelayed(s, 61000); // <-- delay time in miliseconds.
            }

        }

    }

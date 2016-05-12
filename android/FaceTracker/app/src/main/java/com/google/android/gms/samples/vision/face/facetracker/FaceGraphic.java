/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.facetracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    MediaPlayer mp = new MediaPlayer();
    boolean setupDone = false;

    String happyValueJ;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    private boolean musicOn;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
        audioPlayer("celebrate.mp3");
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("SLEEP EXCEPTION: " + e.getMessage());
        }

        String happiness = String.format("%.2f", face.getIsSmilingProbability());


        new RetrieveFeedTask().execute(happiness);


        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;
        String happyValueJ;

        protected String doInBackground(String... vals) {
            int count = vals.length;
            return postToAPI(vals);
        }

        public String postToAPI(String[] happiness) {
            try {
                String url = "http://moodanalytics.herokuapp.com/moodInput";
                //URL object = new URL(url);

                //try {
                //    Thread.sleep(1000);
                //} catch (Exception ex) {
                //    System.out.println("EXCEPTION: " + ex.getMessage());
                //}
                //String url = "https://api.particle.io/v1/devices/270018000d47343432313031/led?access_token=cf0bb3a3b303a068ac415a12d232b98fc5afe03b";
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");


                String happyValue = happiness[0];
                //float happyFloat = Float.parseFloat(happyValue) * 265.0f;
                float happyFloat = Float.parseFloat(happyValue);
                happyValue = String.valueOf(happyFloat);
                if (happyFloat < 0)
                    happyValue = "0";

                JSONObject data = new JSONObject();
                data.put("timestamp:", String.valueOf(System.currentTimeMillis()));
                data.put("id", "1234");
                data.put("happiness", happyValue);
                data.put("sadness", ".1");
                data.put("anger", ".1");
                data.put("surpise", ".1");

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                String params = "params=" + happyValue;
                System.out.println("HERE STUFF!: " + data.toString());
                wr.write(data.toString());
                //wr.write(params);
                wr.flush();
                if (happyFloat < .1 ) {
                    stopMusic();
                } else {
                    if (!musicOn) {
                        startMusic();
                    }
                }

//display what returns the POST request

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(con.getResponseMessage());
                }
            } catch (Exception ex) {
                System.out.println("EX!!:" + ex.getMessage());
            }
            return "success";

        }

        protected void onPostExecute(String result) {
            System.out.print("RESULT: " + result);
        }
    }

    public void startMusic(){
        musicOn = true;
        mp.start();
    }

    public void stopMusic() {
        musicOn = false;
        mp.stop();
    }
    public void audioPlayer(String fileName){
        //set up MediaPlayer
        if (!setupDone) {


            try {
                File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + fileName);

                File[] downloadDirs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();

                //File exdir = Environment.getExternalStorageDirectory();
                //System.out.println("EXDIR!!!!!!!!!: " + exdir);


                for (File tmpf : downloadDirs) {
                    System.out.println("FILE: " + tmpf.getAbsolutePath());
                }


                File file = new File("/storage/emulated/0/downloads" + File.separator + fileName);
                FileInputStream inputStream = new FileInputStream(downloadDir);
                mp.setDataSource(inputStream.getFD());
                mp.prepare();
                setupDone = true;

            } catch (Exception e) {
                System.out.println("ERROR READING MUSIC: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

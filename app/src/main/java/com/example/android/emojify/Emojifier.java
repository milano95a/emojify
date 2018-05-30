/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import timber.log.Timber;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();
    private static final float EMOJI_SCALE_FACTOR = .9f;
    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap picture) {

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces
//        Log.d(LOG_TAG, "detectFacesAndOverlayEmoji: number of faces = " + faces.size());

        Timber.d("detectFaces: number of faces = " + faces.size());

        Bitmap resultBitmap = picture;
        // If there are no faces detected, show a Toast message
        if(faces.size() == 0){
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < faces.size(); ++i) {
                Bitmap emojiBitmap = null;
                Face face = faces.valueAt(i);
                switch (whichEmoji(face)){
                    case smiling:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.smile);
                        break;
                    case frowning:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.frown);
                        break;
                    case left_wink_smile:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.leftwink);
                        break;
                    case right_wink_smile:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.rightwink);
                        break;
                    case close_eye_frowning:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.closed_frown);
                        break;
                    case closed_eye_smiling:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.closed_smile);
                        break;
                    case left_wink_frowning:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.leftwinkfrown);
                        break;
                    case right_wink_frowning:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.rightwinkfrown);
                        break;
                    default:
                        break;
                }

                if(emojiBitmap != null){
                    resultBitmap = addBitmapToFace(resultBitmap,emojiBitmap,face);
                }
            }
        }


        // Release the detector
        detector.release();
        return resultBitmap;
    }


    private static float SMILE_THRESHOLD = 0.1f;
    private static float EYE_OPEN_THRESHOLD = 0.5f;

    /**
     * Method for logging the classification probabilities.
     *
     * @param face The face to get the classification probabilities.
     */
    private static Emoji whichEmoji(Face face){
        // done (2): Change the name of the getClassifications() method to whichEmoji() (also change the log statements)
        // Log all the probabilities
        Timber.d("whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Timber.d("whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Timber.d("whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());

        // done (3): Create threshold constants for a person smiling, and and eye being open by taking pictures of yourself and your friends and noting the logs.
        boolean isSmiling = face.getIsSmilingProbability() > SMILE_THRESHOLD;
        boolean isLeftEyeOpen = face.getIsLeftEyeOpenProbability() > EYE_OPEN_THRESHOLD;
        boolean isRightEyeOpen = face.getIsRightEyeOpenProbability() > EYE_OPEN_THRESHOLD;

        // done (4): Create 3 boolean variables to track the state of the facial expression based on the thresholds you set in the previous step: smiling, left eye closed, right eye closed.
        // done (5): Create an if/else system that selects the appropriate emoji based on the above booleans and log the result.
        Emoji emoji;
        if(isSmiling){
            if(isLeftEyeOpen){
                if(isRightEyeOpen){
                    emoji = Emoji.smiling;
                }else{
                    emoji = Emoji.right_wink_smile;
                }
            }else{
                if(isRightEyeOpen){
                    emoji = Emoji.left_wink_smile;
                }else{
                    emoji = Emoji.closed_eye_smiling;
                }
            }
        }else{
            if(isLeftEyeOpen){
                if(isRightEyeOpen){
                    emoji = Emoji.frowning;
                }else{
                    emoji = Emoji.left_wink_frowning;
                }
            }else{
                if(isRightEyeOpen){
                    emoji = Emoji.right_wink_frowning;
                }else{
                    emoji = Emoji.close_eye_frowning;
                }
            }
        }

        Log.wtf(LOG_TAG, emoji.name());

        return emoji;
    }

    enum Emoji{
     smiling,
     frowning,
     left_wink_smile,
     right_wink_smile,
     left_wink_frowning,
     right_wink_frowning,
     closed_eye_smiling,
     close_eye_frowning
    }

    // done (1): Create an enum class called Emoji that contains all the possible
    // emoji you can make (
    // smiling,
    // frowning,
    // left wink,
    // right wink,
    // left wink frowning,
    // right wink frowning,
    // closed eye smiling,
    // close eye frowning
    // ).

    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }

}

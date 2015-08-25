package de.markus_unterkofler.yotatextcover;

/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Markus Unterkofler
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

/**
 * Collection of useful stuff
 */
public class Utilities extends Application {
    public static int defaultTextSize = 200;
    public static String prefPrefixText = "bigText_";
    public static String prefPrefixRotation = "rotation_";
    public static String prefPrefixCenterText = "centerText_";

    private static Context context;
    private static String sharedPrefsTag = "yotatextcover"; //@FIXME: change to package name (workaround for current users needed)

    /**
     * Get app context
     *
     * @return App context
     */
    public static Context getAppContext() {
        return Utilities.context;
    }

    /**
     * Get SharedPreferences object
     *
     * @return SharedPreferences
     */
    private static SharedPreferences getPrefs() {
        return getAppContext().getSharedPreferences(sharedPrefsTag, Context.MODE_PRIVATE);
    }

    /**
     * Get editor for SharedPreferences
     *
     * @return SharedPreferences editor
     */
    private static SharedPreferences.Editor getPrefsEditor() {
        SharedPreferences sharedPrefs = getPrefs();
        return sharedPrefs.edit();
    }

    /**
     * Get an integer preference
     *
     * @param prefName     Preference name
     * @param defaultValue Default value
     *
     * @return Preference value
     */
    public static int getPrefsInt(String prefName, int defaultValue) {
        SharedPreferences sharedPrefs = getPrefs();
        return sharedPrefs.getInt(prefName, defaultValue);
    }

    /**
     * Get a string preference
     *
     * @param prefName     Preference name
     * @param defaultValue Default value
     *
     * @return Preference value
     */
    public static String getPrefsString(String prefName, String defaultValue) {
        SharedPreferences sharedPrefs = getPrefs();
        return sharedPrefs.getString(prefName, defaultValue);
    }

    /**
     * Get a boolean preference
     *
     * @param prefName     Preference name
     * @param defaultValue Default value
     *
     * @return Preference value
     */
    public static boolean getPrefsBoolean(String prefName, boolean defaultValue) {
        SharedPreferences sharedPrefs = getPrefs();
        return sharedPrefs.getBoolean(prefName, defaultValue);
    }

    /**
     * Save an integer preference
     *
     * @param prefName  Preference name
     * @param prefValue Preference value
     */
    public static void putPrefsInt(String prefName, int prefValue) {
        SharedPreferences.Editor editor = getPrefsEditor();
        editor.putInt(prefName, prefValue);
        editor.commit();
    }

    /**
     * Save a string preference
     *
     * @param prefName  Preference name
     * @param prefValue Preference value
     */
    public static void putPrefsString(String prefName, String prefValue) {
        SharedPreferences.Editor editor = getPrefsEditor();
        editor.putString(prefName, prefValue);
        editor.commit();
    }

    /**
     * Save a boolean preference
     *
     * @param prefName  Preference name
     * @param prefValue Preference value
     */
    public static void putPrefsBoolean(String prefName, boolean prefValue) {
        SharedPreferences.Editor editor = getPrefsEditor();
        editor.putBoolean(prefName, prefValue);
        editor.commit();
    }

    /**
     * Remove a preference
     *
     * @param prefName Preference name
     */
    public static void removePref(String prefName) {
        SharedPreferences.Editor editor = getPrefsEditor();
        editor.remove(prefName);
        editor.commit();
    }

    /**
     * Get the rotation angle by spinner position
     *
     * @param position Position
     *
     * @return Rotation angle
     */
    public static int getRotationAngleByPosition(int position) {
        return position * 45;
    }

    /**
     * Create an image/bitmap from a text
     *
     * @param text Text
     * @param textSizePixels Text size
     * @param rotationAngle Rotation angle
     * @param centerText Center text?
     *
     * @return Bitmap
     */
    public static Bitmap createTextBitmap(final String text, final float textSizePixels, final int rotationAngle, boolean centerText) {
        //set up TextPaint
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        textPaint.setTextSize(textSizePixels);
        textPaint.setARGB(255, 255, 255, 255);

        //optional stuff (could be useful)
        //textPaint.setAntiAlias(true);
        //textPaint.setFilterBitmap(true);
        //textPaint.setTextAlign(Paint.Align.LEFT);
        //textPaint.setColor(textColour);
        //textPaint.setFakeBoldText(true);
        //textPaint.setDither(true);
        //textPaint.setLinearText(true);
        //textPaint.setSubpixelText(true);
        //textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));

        //split text in lines
        String[] textLines = text.split("\n");
        int lineSpace = 20;

        //get width of longest text line
        float textWidth = 0;
        for (String textLine : textLines) {
            if (textPaint.measureText(textLine) > textWidth) {
                textWidth = textPaint.measureText(textLine);
            }
        }

        //calculate bitmap height
        float bitmapHeight = (textSizePixels * textLines.length) + (lineSpace * (textLines.length - 1)) + lineSpace; //add another lineSpace because uhm.. to be centered

        //create empty bitmap
        Bitmap myBitmap = Bitmap.createBitmap((int) textWidth, (int) bitmapHeight, Bitmap.Config.ARGB_8888);

        //copy bitmap into canvas
        Canvas myCanvas = new Canvas(myBitmap);

        //debugging stuff (colored background)
        //myCanvas.drawColor(android.graphics.Color.GREEN);
        //myCanvas.drawBitmap(myBitmap, 0, 0, null);

        //Rect for getting dimensions
        Rect bounds = new Rect();

        int x = 0;
        int y = 170; //no idea where to get this value from (it's not the text size, not the bitmap height etc.) - but it works

        //try to calculate y value (does not work yet)
        //int y = Math.round((textSizePixels + lineSpace) * textLines.length * 0.80f);
        //int y = (int) bitmapHeight * 0.25;

        //iterate over text lines
        for (String line : textLines) {
            //user wants centered text?
            if (centerText) {
                textPaint.getTextBounds(line, 0, line.length(), bounds);
                x = (myCanvas.getWidth() / 2) - (bounds.width() / 2);
            }

            //draw text to canvas and calculate next y value
            myCanvas.drawText(line, x, y, textPaint);
            y += textSizePixels + lineSpace;
        }

        //matrix is needed for rotation
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);

        myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myCanvas.getWidth(), myCanvas.getHeight(), matrix, true);

        return myBitmap;
    }

    /**
     * Set application context for later use
     */
    public void onCreate() {
        super.onCreate();
        Utilities.context = getApplicationContext();
    }
}

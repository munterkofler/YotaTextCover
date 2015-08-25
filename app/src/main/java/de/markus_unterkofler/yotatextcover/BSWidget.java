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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import com.yotadevices.sdk.Drawer;
import com.yotadevices.sdk.utils.EinkUtils;

/**
 * Actual back screen (cover) widget
 */
public class BSWidget extends AppWidgetProvider {
    private static final String TAG = BSWidget.class.getSimpleName();

    private static Context context = null;

    /**
     * When widget is loaded or updated
     *
     * @param context          Context
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetIds     Widget IDs
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Store a context
        if (BSWidget.context == null) {
            BSWidget.context = context;
        }

        RemoteViews views;

        //update all widgets
        for (int widgetId : appWidgetIds) {
            //create remote view for widget
            views = new RemoteViews(context.getPackageName(), R.layout.bs_widget);

            //set waveform and dithering - maybe useless?
            EinkUtils.setRemoteViewsWaveformAndDithering(views, R.layout.bs_widget, Drawer.Waveform.WAVEFORM_GC_FULL, Drawer.Dithering.DITHER_DEFAULT);

            //get preferences
            String bigText = Utilities.getPrefsString(Utilities.prefPrefixText + widgetId, context.getString(R.string.text_default));
            int rotationAngle = Utilities.getRotationAngleByPosition(Utilities.getPrefsInt(Utilities.prefPrefixRotation + widgetId, 0));
            boolean centerText = Utilities.getPrefsBoolean(Utilities.prefPrefixCenterText, true);

            try {
                //generate and set text as image/bitmap
                Bitmap textBitmap = Utilities.createTextBitmap(bigText, Utilities.defaultTextSize, rotationAngle, centerText);
                views.setImageViewBitmap(R.id.bigText, textBitmap);

                //update UI for current widget
                appWidgetManager.updateAppWidget(widgetId, views);

                Log.d(TAG, "Widget " + widgetId + " updated.");
            } catch (Exception e) {
                //bitmap update error? just ignore it
            }
        }
    }

    /**
     * When a widget is deleted
     *
     * @param context       Context
     * @param appWidgetIds  Widget IDs
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        //remove widget prefs when widget is deleted
        for (int widgetId : appWidgetIds) {
            Utilities.removePref(Utilities.prefPrefixText + widgetId);
            Utilities.removePref(Utilities.prefPrefixRotation + widgetId);
            Utilities.removePref(Utilities.prefPrefixCenterText + widgetId);
        }
    }
}
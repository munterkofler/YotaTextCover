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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

import com.yotadevices.sdk.Drawer;
import com.yotadevices.sdk.utils.EinkUtils;

/**
 * Widget settings (YotaHub)
 */
public class SettingsActivity extends Activity {
    private Intent intent = null;

    /** Identifier of Widget that is on a front screen (into YotaHub) */
    private int frWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    /** Identifier of Widget that is on a back screen */
    private int bsWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    /**
     * Activity onCreate method
     *
     * @param savedInstanceState Saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getActionBar().setElevation(0); //no shadow
        } catch (NullPointerException e) {
            //ignore
        }
        setContentView(R.layout.activity_settings);

        //get widget IDs
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            int ids[] = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if (ids != null) {
                frWidgetId = ids[0]; // front screen widget ID (YotaHub)
                bsWidgetId = ids[1]; // back screen widget ID
            }
        }

        //get view objects
        TextView editText = (TextView) this.findViewById(R.id.editText);
        Spinner spinner = (Spinner) this.findViewById(R.id.rotationAngle);
        CheckBox centerTextBox = (CheckBox) this.findViewById(R.id.centerText);

        //set text
        editText.setText(Utilities.getPrefsString(Utilities.prefPrefixText + bsWidgetId, getString(R.string.text_default)));

        //set rotation angle spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.settings_rotationAngles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(Utilities.getPrefsInt(Utilities.prefPrefixRotation + bsWidgetId, 0));

        //set center checkbox
        centerTextBox.setChecked(Utilities.getPrefsBoolean(Utilities.prefPrefixCenterText, true));

        // Setting up a default result for this activity
        intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, frWidgetId);
        setResult(RESULT_CANCELED, intent);

        //cancel button events
        Button btnCancel = (Button) findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        //accept button events
        Button btnAccept = (Button) findViewById(R.id.accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get view objects
                TextView editText = (TextView) findViewById(R.id.editText);
                Spinner spinner = (Spinner) findViewById(R.id.rotationAngle);
                CheckBox centerTextCheckbox = (CheckBox) findViewById(R.id.centerText);

                //get view objects values
                String bigText = editText.getText().toString();
                int spinnerPos = spinner.getSelectedItemPosition();
                boolean centerText = centerTextCheckbox.isChecked();
                TextView errorMessage = (TextView) findViewById(R.id.errorMessage);

                RemoteViews v = new RemoteViews(getPackageName(), R.layout.bs_widget);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

                //set waveform and dithering - useless?
                EinkUtils.setRemoteViewsWaveformAndDithering(v, 0, Drawer.Waveform.WAVEFORM_GC_FULL, Drawer.Dithering.DITHER_DEFAULT);

                try {
                    //reset error message
                    errorMessage.setText("");

                    //get rotation angle and generate text image/bitmap
                    int rotationAngle = Utilities.getRotationAngleByPosition(spinnerPos);
                    Bitmap textBitmap = Utilities.createTextBitmap(bigText, Utilities.defaultTextSize, rotationAngle, centerText);

                    //update view bitmap
                    v.setImageViewBitmap(R.id.bigText, textBitmap);

                    //update widgets
                    appWidgetManager.updateAppWidget(frWidgetId, v);
                    appWidgetManager.updateAppWidget(bsWidgetId, v);
                } catch (Exception e) {
                    //display error message
                    errorMessage.setText(R.string.settings_save_errorMessage);
                    return;
                }

// TODO: 22.08.15 Maybe save settings to DB?

                //save text
                Utilities.putPrefsString(Utilities.prefPrefixText + frWidgetId, bigText);
                Utilities.putPrefsString(Utilities.prefPrefixText + bsWidgetId, bigText);

                //save rotation
                Utilities.putPrefsInt(Utilities.prefPrefixRotation + frWidgetId, spinnerPos);
                Utilities.putPrefsInt(Utilities.prefPrefixRotation + bsWidgetId, spinnerPos);

                //save center checkbox
                Utilities.putPrefsBoolean(Utilities.prefPrefixCenterText + frWidgetId, centerText);
                Utilities.putPrefsBoolean(Utilities.prefPrefixCenterText + bsWidgetId, centerText);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}

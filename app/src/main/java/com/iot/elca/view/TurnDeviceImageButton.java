package com.iot.elca.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.iot.elca.model.MainDevice;

/**
 * Created by Marcos on 02/04/2017.
 */

@SuppressLint("AppCompatCustomView")
public class TurnDeviceImageButton extends ImageButton {

    private MainDevice device;

    public TurnDeviceImageButton(Context context, MainDevice device) {
        super(context);
        this.device = device;
    }

    public TurnDeviceImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TurnDeviceImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TurnDeviceImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    public MainDevice getDevice() {
        return device;
    }

    public void setDevice(MainDevice device) {
        this.device = device;
    }

}

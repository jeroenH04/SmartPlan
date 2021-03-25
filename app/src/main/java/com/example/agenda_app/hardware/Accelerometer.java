package com.example.agenda_app.hardware;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Accelerometer {

    /*
     * Implementation of a design pattern "the observer" to decouple the sensor
     * notification from the main activity.
     */
    public interface Listener {
        void onTranslation(float tx, float ty, float tz);
    }

    private Listener listener;

    public void setListener(final Listener l) {
        listener = l;
    }

    private final SensorManager sensorManager;
    private final Sensor sensor;
    private final SensorEventListener sensorEventListener;

    public Accelerometer(Context context) {
        sensorManager = (SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);
        sensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(final SensorEvent sensorEvent) {
                if (listener != null) {
                    listener.onTranslation(sensorEvent.values[0],
                            sensorEvent.values[1], sensorEvent.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(final Sensor sensor,
                                          final int accuracy) {
            }
        };
    }

    /**
     * Method to register sensor notifications.
     */
    public void register() {
        sensorManager.registerListener(sensorEventListener, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Method to unregister from sensor notifications.
     */
    public void unregister() {
        sensorManager.unregisterListener(sensorEventListener);
    }
}

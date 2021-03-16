package com.example.agenda_app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Accelerometer {

    /*
     * Implementation of a design pattern "the observer" to decouple the sensor notification from
     * the main activity.
     */
    public interface Listener {
        void onTranslation(float tx, float ty, float tz);
    }

    private Listener listener;

    public void setListener(Listener l) {
        listener = l;
    }

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    Accelerometer(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (listener != null) {
                    listener.onTranslation(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }


    /*
     * Method To register sensor notifications
     */
    public void register() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * Method to unregister from sensor notifications
     */
    public void unregister() {
        sensorManager.unregisterListener(sensorEventListener);
    }


}

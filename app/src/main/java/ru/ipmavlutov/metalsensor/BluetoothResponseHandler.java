package ru.ipmavlutov.metalsensor;


import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.Arrays;

import ru.ipmavlutov.metalsensor.Activities.Work;

public class BluetoothResponseHandler extends Handler {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 2;
    public static final int MESSAGE_TOAST = 3;
    public static final int MESSAGE_DATA = 4;
    private String MSG_CONNECTING;
    private WeakReference<Work> mActivity;
    private String MSG_CONNECTED;
    private String MSG_NOT_CONNECTED;
    private double Z;
    private boolean first_start;

    public BluetoothResponseHandler(Work activity) {

        mActivity = new WeakReference<>(activity);
        MSG_CONNECTED = activity.MSG_CONNECTED;
        MSG_NOT_CONNECTED = activity.MSG_NOT_CONNECTED;
        MSG_CONNECTING = activity.MSG_CONNECTING;
        Z = Work.Z;
        first_start = true;

    }

    public void setTarget(Work target) {
        mActivity.clear();
        mActivity = new WeakReference<>(target);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Work activity = mActivity.get();
        if (activity != null) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    final ActionBar bar = activity.getSupportActionBar();
                    switch (msg.arg1) {
                        case DeviceConnector.STATE_CONNECTED:
                            assert bar != null;
                            bar.setSubtitle(MSG_CONNECTED);
                        case DeviceConnector.STATE_CONNECTING:
                            assert bar != null;
                            bar.setSubtitle(MSG_CONNECTING);
                            break;
                        case DeviceConnector.STATE_NONE:
                            assert bar != null;
                            bar.setSubtitle(MSG_NOT_CONNECTED);
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    activity.setDeviceName((String) msg.obj);
                    break;
                case MESSAGE_TOAST:
                    Intent action_disconnected = new Intent(activity, WidgetInfo.class);
                    action_disconnected.setAction(WidgetInfo.ACTION_STATE_NONE);
                    action_disconnected.putExtra("disconnected", true);
                    activity.sendBroadcast(action_disconnected);

                    Toast.makeText(activity.getBaseContext(), "Устройство " + msg.obj + " отключено", Toast.LENGTH_LONG).show();
                    activity.myTimer_off();
                    activity.finish();
                    /*Intent start = new Intent(activity.getBaseContext(), Welcome.class);
                    activity.startActivityForResult(start, 0);*/
                    break;
                case MESSAGE_DATA:
                    byte[] a = (byte[]) msg.obj;
                    //activity.UpDateResult(a);
                    if (a.length == 26) {//26
                        try {
                            byte[] temperature_1 = Arrays.copyOfRange(a, 0, 2);//[0,1]
                            byte[] signal_1 = Arrays.copyOfRange(a, 2, 7);//[2,6]
                            byte[] temperature_2 = Arrays.copyOfRange(a, 7, 9);//[7,8]
                            byte[] signal_2 = Arrays.copyOfRange(a, 9, 14);//[9,13]
                            byte[] temperature_3 = Arrays.copyOfRange(a, 14, 16);
                            byte[] signal_3 = Arrays.copyOfRange(a, 16, 21);


                            //byte[] super_signal = Arrays.copyOfRange(a, 4, 6);//[4,5]
                            //FF=[6,7],/r/n=[8,9]

                            //температура датчика #1
                            int temperature_result_1 = Integer.parseInt(new String(temperature_1,"UTF-8"));
                            TextView temperature_value_1 = (TextView) activity.findViewById(R.id.temperature_value_1);
                            temperature_value_1.setText(String.format("%s  °C", String.valueOf(temperature_result_1)));

                            //значение сигнала #1
                            TextView signal_value_1 = (TextView) activity.findViewById(R.id.signal_value_1);
                            String str_signal_1 = new String(signal_1, Charset.forName("UTF-8"));
                            signal_value_1.setText(String.format("%s мг", str_signal_1));

                            //для БД
                            double signal_result_1 = Double.parseDouble(str_signal_1.replace(",", "."));

                            //температура датчика #2
                            int temperature_result_2 = Integer.parseInt(new String(temperature_2,"UTF-8"));
                            TextView temperature_value_2 = (TextView) activity.findViewById(R.id.temperature_value_2);
                            temperature_value_2.setText(String.format("%s  °C", String.valueOf(temperature_result_2)));

                            //значение сигнала #2
                            //double signal_result_2 = FindRudeSignal(Correction(GetSignal(signal_2), temperature_result_2), Z);
                            TextView signal_value_2 = (TextView) activity.findViewById(R.id.signal_value_2);
                            String str_signal_2 = new String(signal_2, Charset.forName("UTF-8"));
                            signal_value_2.setText(String.format("%s мг", str_signal_2));

                            //температура датчика #3
                            int temperature_result_3 = Integer.parseInt(new String(temperature_3,"UTF-8"));
                            TextView temperature_value_3 = (TextView) activity.findViewById(R.id.temperature_value_3);
                            temperature_value_3.setText(String.format("%s  °C", String.valueOf(temperature_result_3)));

                            //значение сигнала #3
                            TextView signal_value_3 = (TextView) activity.findViewById(R.id.signal_value_3);
                            String str_signal_3 = new String(signal_3, Charset.forName("UTF-8"));
                            signal_value_3.setText(String.format("%s мг", str_signal_3));


                            //значение супер сигнала
                        /*double super_signal_result = FindSuperSignal(Correction(GetSuperSignal(super_signal), temperature_result_1), Z);
                        TextView super_signal_value = (TextView) activity.findViewById(R.id.super_signal_value);
                        super_signal_value.setText(String.format("%s мг", Double.toString(super_signal_result)));*/

                            //на время super_signal_result = 0
                            double super_signal_result = 0;
                            activity.UpDateBD(temperature_result_1, signal_result_1, super_signal_result);

                            activity.myTimer(first_start);
                            first_start = false;

                            Intent action = new Intent(activity, WidgetInfo.class);
                            action.setAction(WidgetInfo.ACTION_WIDGET_RECEIVER);
                            action.putExtra("signal_value1", signal_result_1);
                            //PendingIntent actionPendingIntent = PendingIntent.getBroadcast(activity, 0, action, 0);
                            activity.sendBroadcast(action);
                        } catch (Exception ex) {
                            appendLog(ex.toString());
                        }


                    }

            }
        }
    }

    public void appendLog(String text) {
        File logFile = new File(Environment.getExternalStorageDirectory(), "log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}



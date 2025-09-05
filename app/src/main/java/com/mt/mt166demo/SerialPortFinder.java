package com.mt.mt166demo;

import android.util.Log;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

/* loaded from: classes2.dex */
public class SerialPortFinder {
    private static final String TAG = "SerialPort";
    private Vector<Driver> mDrivers = null;

    public static class Driver {
        private String mDeviceRoot;
        Vector<File> mDevices = null;
        private String mDriverName;

        public Driver(String name, String root) {
            Log.i("chw", "SerialPortFinder ---> Driver");
            this.mDriverName = name;
            this.mDeviceRoot = root;
        }

        public Vector<File> getDevices() {
            Log.i("chw", "SerialPortFinder ---> getDevices");
            if (this.mDevices == null) {
                this.mDevices = new Vector<>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].getAbsolutePath().startsWith(this.mDeviceRoot)) {
                            Log.d(SerialPortFinder.TAG, "Found new device: " + files[i]);
                            this.mDevices.add(files[i]);
                        }
                    }
                }
            }
            return this.mDevices;
        }

        public String getName() {
            Log.i("chw", "SerialPortFinder ---> getName");
            return this.mDriverName;
        }
    }

    Vector<Driver> getDrivers() throws IOException {
        Log.i("chw", "SerialPortFinder ---> getDrivers");
        if (this.mDrivers == null) {
            this.mDrivers = new Vector<>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            while (true) {
                String l = r.readLine();
                if (l == null) {
                    break;
                }
                String[] w = l.split(" +");
                if (w.length == 5 && w[4].equals("serial")) {
                    Log.d(TAG, "Found new driver: " + w[1]);
                    this.mDrivers.add(new Driver(w[0], w[1]));
                }
            }
            r.close();
        }
        return this.mDrivers;
    }

    public Vector<SerialPortItem> getAllPorts() {
        Vector<SerialPortItem> ports = new Vector<>();
        try {
            Iterator<Driver> itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while (itdev.hasNext()) {
                    File file = itdev.next();
                    String name = String.format("%s (%s)", file.getName(), driver.getName());
                    String fullPath = file.getAbsolutePath();
                    ports.add(new SerialPortItem(name, fullPath));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ports;
    }
}

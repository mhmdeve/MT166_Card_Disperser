package com.mt.mt166demo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import java.util.Vector;

/* loaded from: classes2.dex */
public class ConnDialog extends Dialog {
    OnCloseListener mCloseListener;
    Context mContext;
    Spinner mSpinBaudRates;
    Spinner mSpinPorts;

    public interface OnCloseListener {
        void onClose(boolean z);
    }

    public ConnDialog(Context context) {
        super(context);
        this.mCloseListener = null;
        this.mContext = context;
    }

    public void setCloseListener(OnCloseListener listener) {
        this.mCloseListener = listener;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conn);
        setTitle(MainActivity.instance.getString(R.string.deviceconnect));
        setCancelable(false);
        this.mSpinPorts = (Spinner) findViewById(R.id.spinPorts);
        this.mSpinBaudRates = (Spinner) findViewById(R.id.spinBaudRates);
        SerialPortFinder finder = new SerialPortFinder();
        Vector<SerialPortItem> ports = finder.getAllPorts();
        if (ports.size() != 0) {
            ArrayAdapter<SerialPortItem> adapter1 = new ArrayAdapter<>(this.mContext, R.layout.support_simple_spinner_dropdown_item, ports);
            this.mSpinPorts.setAdapter((SpinnerAdapter) adapter1);
        } else {
            Vector<SerialPortItem> ports2 = new Vector<>();
            ports2.add(new SerialPortItem("COM0", "/dev/ttyS0"));
            ports2.add(new SerialPortItem("COM1", "/dev/ttyS1"));
            ports2.add(new SerialPortItem("COM2", "/dev/ttyS2"));
            ports2.add(new SerialPortItem("COM3", "/dev/ttyS3"));
            ports2.add(new SerialPortItem("COM4", "/dev/ttyS4"));
            ArrayAdapter<SerialPortItem> adapter12 = new ArrayAdapter<>(this.mContext, R.layout.support_simple_spinner_dropdown_item, ports2);
            this.mSpinPorts.setAdapter((SpinnerAdapter) adapter12);
        }
        Vector<String> baudRates = new Vector<>();
        baudRates.add("9600");
        baudRates.add("19200");
        baudRates.add("38400");
        baudRates.add("57600");
        baudRates.add("115200");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this.mContext, R.layout.support_simple_spinner_dropdown_item, baudRates);
        this.mSpinBaudRates.setAdapter((SpinnerAdapter) adapter2);
        SharedPreferences sp = this.mContext.getSharedPreferences("conn_pref", 0);
        String lastPortName = sp.getString("PortName", "");
        int i = 0;
        while (true) {
            if (i >= this.mSpinPorts.getCount()) {
                break;
            }
            if (!((SerialPortItem) this.mSpinPorts.getItemAtPosition(i)).getName().equalsIgnoreCase(lastPortName)) {
                i++;
            } else {
                this.mSpinPorts.setSelection(i);
                break;
            }
        }
        int i2 = 0;
        while (true) {
            if (i2 >= this.mSpinBaudRates.getCount()) {
                break;
            }
            if (Integer.parseInt((String) this.mSpinBaudRates.getItemAtPosition(i2)) != 9600) {
                i2++;
            } else {
                this.mSpinBaudRates.setSelection(i2);
                break;
            }
        }
        Button OkButton = (Button) findViewById(R.id.btnOK);
        OkButton.setOnClickListener(new View.OnClickListener() { // from class: com.mt.mt166demo.ConnDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    SerialPortItem port = (SerialPortItem) ConnDialog.this.mSpinPorts.getSelectedItem();
                    String baudRate = (String) ConnDialog.this.mSpinBaudRates.getSelectedItem();
                    Application.cr.MtConnect(port.getFullPath(), Integer.parseInt(baudRate), 0);
                    ConnDialog.this.dismiss();
                    SharedPreferences sp2 = ConnDialog.this.mContext.getSharedPreferences("conn_pref", 0);
                    SharedPreferences.Editor editor = sp2.edit();
                    editor.putString("PortName", port.getName());
                    editor.putInt("BaudRate", Integer.parseInt(baudRate));
                    editor.apply();
                    if (ConnDialog.this.mCloseListener != null) {
                        ConnDialog.this.mCloseListener.onClose(true);
                    }
                } catch (Exception e) {
                    Application.showError(e.getMessage(), ConnDialog.this.mContext);
                }
            }
        });
        Button CancelButton = (Button) findViewById(R.id.btnCancel);
        CancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.mt.mt166demo.ConnDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    ConnDialog.this.dismiss();
                    if (ConnDialog.this.mCloseListener != null) {
                        ConnDialog.this.mCloseListener.onClose(false);
                    }
                } catch (Exception e) {
                    Application.showError(e.getMessage(), ConnDialog.this.mContext);
                }
            }
        });
    }
}

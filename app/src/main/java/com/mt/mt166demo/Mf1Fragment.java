package com.mt.mt166demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mt.mtapi.mtDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/* Migrated from AppCompatActivity to Fragment */
public class Mf1Fragment extends Fragment implements View.OnClickListener {

    private ArrayAdapter<String> adapterBlock;
    private ArrayAdapter<String> adapterSector;
    private EditText mEdtBlkData;
    private EditText mEdtKeyData;
    private EditText mEdtValue;
    private Spinner mSpinBlock;
    private Spinner mSpinKeyTypes;
    private Spinner mSpinSector;

    private final List<String> listSector = new ArrayList<>();
    private final List<String> listBlock = new ArrayList<>();

    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_m1, container, false);

        mSpinSector = mView.findViewById(R.id.spinSector);
        mSpinBlock = mView.findViewById(R.id.spinblock);
        mSpinKeyTypes = mView.findViewById(R.id.spinMf1KeyTypes);
        mEdtKeyData = mView.findViewById(R.id.edtMf1KeyData);
        mEdtBlkData = mView.findViewById(R.id.edtMf1RawData);
        mEdtValue = mView.findViewById(R.id.edtMf1Value);

        mEdtKeyData.setText("FF FF FF FF FF FF");
        mEdtValue.setText("1");

        // Setup key type spinner
        Vector<Mf1KeyItem> vec = new Vector<>();
        vec.add(new Mf1KeyItem("Key A", true));
        vec.add(new Mf1KeyItem("Key B", false));
        ArrayAdapter<Mf1KeyItem> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, vec);
        mSpinKeyTypes.setAdapter(adapter);

        initSpinner();

        return mView;
    }

    private void initSpinner() {
        listSector.clear();
        for (int i = 0; i < 39; i++) {
            listSector.add(String.valueOf(i));
        }
        adapterSector = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, listSector);
        adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinSector.setAdapter(adapterSector);

        listBlock.clear();
        listBlock.add("0");
        listBlock.add("1");
        listBlock.add("2");
        listBlock.add("3");

        adapterBlock = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, listBlock);
        adapterBlock.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinBlock.setAdapter(adapterBlock);

        // Register click listeners
        mView.findViewById(R.id.btnMf1Select).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1ReadUID).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1Auth).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1Update).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1Read).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1Write).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1IniValue).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1ReadValue).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1Increment).setOnClickListener(this);
        mView.findViewById(R.id.btnMf1Decrement).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        try {
            String strMsg = getString(R.string.strSucceed);
            Button btn = mView.findViewById(view.getId());
            String btnText = btn.getText().toString();
            byte bSector = (byte) mSpinSector.getSelectedItemPosition();
            byte bBlock = (byte) mSpinBlock.getSelectedItemPosition();
            byte[] keyData = getKeyData();

            int viewId = view.getId();

            if (viewId == R.id.btnMf1Auth) {
                boolean isKeyA = ((Mf1KeyItem) mSpinKeyTypes.getSelectedItem()).isKeyA();
                byte bKey = isKeyA ? mtDevice.MT166_M1_KEY_A : mtDevice.MT166_M1_KEY_B;
                Application.cr.MT166_M1PasswordVerify(bKey, bSector, keyData);
                Application.showInfo(strMsg, btnText, requireContext());
            } else if (viewId == R.id.btnMf1Decrement) {
                int values = getValue();
                Application.cr.MT166_M1Decrement(bSector, bBlock, values);
                Application.showInfo(strMsg, btnText, requireContext());
            } else if (viewId == R.id.btnMf1Increment) {
                int values = getValue();
                Application.cr.MT166_M1Increase(bSector, bBlock, values);
                Application.showInfo(strMsg, btnText, requireContext());
            } else if (viewId == R.id.btnMf1IniValue) {
                int values = getValue();
                Application.cr.MT166_M1InitValue(bSector, bBlock, values);
                Application.showInfo(strMsg, btnText, requireContext());
            } else if (viewId == R.id.btnMf1Read) {
                byte[] bBlockData = Application.cr.MT166_M1ReadBlock(bSector, bBlock);
                String strInfo = Utils.bytesToHexString(bBlockData);
                mEdtBlkData.setText(strInfo);
            } else if (viewId == R.id.btnMf1ReadUID) {
                byte[] data = Application.cr.MT166_M1GetSerial();
                String strInfo = Utils.bytesToHexString(data);
                Application.showInfo(strInfo, btnText, requireContext());
            } else if (viewId == R.id.btnMf1ReadValue) {
                int values = Application.cr.MT166_M1ReadValue(bSector, bBlock);
                String strInfo = String.valueOf(values);
                Log.d("Mf1Fragment", strInfo);
                mEdtValue.setText(strInfo);
            } else if (viewId == R.id.btnMf1Select) {
                Application.cr.MT166_M1FindCard();
                Application.showInfo(strMsg, btnText, requireContext());
            } else if (viewId == R.id.btnMf1Update) {
                Application.cr.MT166_M1UpdatePassword(bSector, keyData);
                Application.showInfo(strMsg, btnText, requireContext());
            } else if (viewId == R.id.btnMf1Write) {
                byte[] bWriteData = getRawData();
                Application.cr.MT166_M1WriteBlock(bSector, bBlock, bWriteData);
                Application.showInfo(strMsg, btnText, requireContext());
            }
        } catch (Exception e) {
            Application.showError(e.getMessage(), requireContext());
        }
    }

    private byte[] getKeyData() throws Exception {
        String text = mEdtKeyData.getText().toString();
        byte[] data = Utils.hexStringToBytes(text);
        if (data == null) throw new Exception(getString(R.string.strNoInputKey));
        if (data.length != 6) throw new Exception(getString(R.string.strKeyerr));
        return data;
    }

    private byte[] getRawData() throws Exception {
        String text = mEdtBlkData.getText().toString();
        byte[] data = Utils.hexStringToBytes(text);
        if (data == null) throw new Exception(getString(R.string.strNoData));
        if (data.length != 16) throw new Exception(getString(R.string.strBlockDataErr));
        return data;
    }

    private int getValue() throws Exception {
        String text = mEdtValue.getText().toString().replaceAll(" ", "");
        if (text.isEmpty()) throw new Exception(getString(R.string.strNoData));
        return Integer.parseInt(text);
    }
}

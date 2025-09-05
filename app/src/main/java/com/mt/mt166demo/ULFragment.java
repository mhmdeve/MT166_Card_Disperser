package com.mt.mt166demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/* Migrated from AppCompatActivity to Fragment */
public class ULFragment extends Fragment implements View.OnClickListener {

    private ArrayAdapter<String> adapterBlock;
    private final List<String> listBlock = new ArrayList<>();

    private EditText mEdtData;
    private Spinner mSpinBlock;
    private TextView mTvResp;

    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_ul, container, false);

        mEdtData = mView.findViewById(R.id.edtULData);
        mEdtData.setText("FF FF FF FF");

        mSpinBlock = mView.findViewById(R.id.spinBlockNo);
        mTvResp = mView.findViewById(R.id.txtApduResp);

        initSpinner();

        mView.findViewById(R.id.btnUlFind).setOnClickListener(this);
        mView.findViewById(R.id.btnRead).setOnClickListener(this);
        mView.findViewById(R.id.btnWrite).setOnClickListener(this);

        return mView;
    }

    private void initSpinner() {
        listBlock.clear();
        for (int i = 0; i < 16; i++) {
            listBlock.add(String.valueOf(i));
        }
        adapterBlock = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listBlock
        );
        adapterBlock.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinBlock.setAdapter(adapterBlock);
    }

    @Override
    public void onClick(View view) {
        try {
            byte bBlockNo = (byte) mSpinBlock.getSelectedItemPosition();
            int viewId = view.getId();

            if (viewId == R.id.btnRead) {
                byte[] data = Application.cr.MT166_ULRead(bBlockNo);
                String strInfo = Utils.bytesToHexString(data);
                mTvResp.setText(strInfo);

            } else if (viewId == R.id.btnUlFind) {
                byte[] atr = Application.cr.MT166_ULFind();
                String strInfo = Utils.bytesToHexString(atr);
                mTvResp.setText(strInfo);

            } else if (viewId == R.id.btnWrite) {
                byte[] bData = getRawData();
                Application.cr.MT166_ULWrite(bBlockNo, bData);
                mTvResp.setText(getString(R.string.strSucceed));
            }

        } catch (Exception e) {
            Application.showError(e.getMessage(), requireContext());
        }
    }

    private byte[] getRawData() throws Exception {
        String text = mEdtData.getText().toString();
        byte[] data = Utils.hexStringToBytes(text);
        if (data == null) {
            throw new Exception(getString(R.string.strNoData));
        }
        if (data.length != 4) {
            throw new Exception(getString(R.string.strBlockDataErr));
        }
        return data;
    }
}

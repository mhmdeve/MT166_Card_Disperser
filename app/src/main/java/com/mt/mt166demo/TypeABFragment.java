package com.mt.mt166demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/* Migrated from AppCompatActivity to Fragment */
public class TypeABFragment extends Fragment implements View.OnClickListener {

    private ArrayAdapter<String> typeAdapter;
    private final List<String> typeList = new ArrayList<>();

    private Spinner mSpinCmds;
    private Spinner mSpinTypeAB;
    private TextView mTvATR;
    private TextView mTvResp;

    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_typeab, container, false);

        mSpinCmds = mView.findViewById(R.id.spinApduCmds);
        mSpinTypeAB = mView.findViewById(R.id.spinType);
        mTvATR = mView.findViewById(R.id.txtATR);
        mTvResp = mView.findViewById(R.id.txtApduResp);

        mTvATR.setText("");

        // Load APDU command list
        Vector<APDUCmdItem> vec = Application.getPredCmdItems();
        ArrayAdapter<APDUCmdItem> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                vec
        );
        mSpinCmds.setAdapter(adapter);

        initSpinner();

        // Click listeners
        mView.findViewById(R.id.btnActive).setOnClickListener(this);
        mView.findViewById(R.id.btnDeselect).setOnClickListener(this);
        mView.findViewById(R.id.btnTransmit).setOnClickListener(this);

        return mView;
    }

    private void initSpinner() {
        typeList.clear();
        typeList.add("TypeA");
        typeList.add("TypeB");

        typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                typeList
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinTypeAB.setAdapter(typeAdapter);
    }

    @Override
    public void onClick(View view) {
        try {
            Button btn = mView.findViewById(view.getId());
            String btnText = btn.getText().toString();

            byte bType = (byte) (mSpinTypeAB.getSelectedItemPosition() + 65); // 65 = 'A'

            int id = view.getId();
            if (id == R.id.btnActive) {
                byte[] atr = Application.cr.MT166_TypeCpuActive(bType);
                String strInfo = Utils.bytesToHexString(atr);
                mTvATR.setText(strInfo);
            } else if (id == R.id.btnDeselect) {
                Application.cr.MT166_TypeCpuDeselect();
                mTvResp.setText(getString(R.string.strCpuDeselect));
            } else if (id == R.id.btnTransmit) {
                APDUCmdItem cmd = (APDUCmdItem) mSpinCmds.getSelectedItem();
                byte[] rdata = Application.cr.MT166_TypeCpuTransmit(bType, cmd.getCmdData());
                String strInfo = Utils.bytesToHexString(rdata);
                mTvResp.setText(strInfo);
            }

        } catch (Exception e) {
            Application.showError(e.getMessage(), requireContext());
        }
    }
}

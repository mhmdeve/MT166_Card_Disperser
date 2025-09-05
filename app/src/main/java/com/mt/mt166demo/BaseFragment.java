package com.mt.mt166demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mt.mtapi.mtDevice;

public class BaseFragment extends Fragment implements View.OnClickListener {
    private View mView;

    public BaseFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout instead of setContentView
        mView = inflater.inflate(R.layout.fragment_base, container, false);

        // Set click listeners
        mView.findViewById(R.id.btnGetVersion).setOnClickListener(this);
        mView.findViewById(R.id.btnState).setOnClickListener(this);
        mView.findViewById(R.id.btnDisReader).setOnClickListener(this);
        mView.findViewById(R.id.btnDisOutlet).setOnClickListener(this);
        mView.findViewById(R.id.btnDisEject).setOnClickListener(this);
        mView.findViewById(R.id.btnRecycle).setOnClickListener(this);
        mView.findViewById(R.id.btnEnableEntry).setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View view) {
        try {
            String resultInfo = getString(R.string.strSucceed);
            Button btn = mView.findViewById(view.getId());
            String btnText = btn.getText().toString();
            int id = view.getId();

            if (id == R.id.btnDisEject) {
                Application.cr.MT166_Dispense(mtDevice.MT166_M1_KEY_A);
            } else if (id == R.id.btnDisOutlet) {
                Application.cr.MT166_Dispense((byte) 49);
            } else if (id == R.id.btnDisReader) {
                Application.cr.MT166_Dispense((byte) 48);
            } else if (id == R.id.btnEnableEntry) {
                Application.cr.MT166_EnableEntry();
            } else if (id == R.id.btnGetVersion) {
                resultInfo = Application.cr.MT166_GetVersion();
            } else if (id == R.id.btnRecycle) {
                Application.cr.MT166_CardRecycle();
            } else if (id == R.id.btnState) {
                resultInfo = processDeviceStatus();
            }

            // Pass context from fragment
            Application.showInfo(resultInfo, btnText, requireContext());
        } catch (Exception e) {
            Application.showError(e.getMessage(), requireContext());
        }
    }

    private String processDeviceStatus() throws Exception {
        byte[] status = Application.cr.MT166_GetState();
        StringBuilder statusBuilder = new StringBuilder();

        processStatusByte0(status[0], statusBuilder);
        processStatusByte1(status[1], statusBuilder);
        processStatusByte2(status[2], statusBuilder);
        processStatusByte3(status[3], statusBuilder);
        processStatusByte4(status[4], statusBuilder);
        processStatusByte5(status[5], statusBuilder);
        processStatusByte6(status[6], statusBuilder);
        processStatusByte7(status[7], statusBuilder);

        return statusBuilder.toString();
    }

    private void processStatusByte0(byte status, StringBuilder builder) {
        if (status == 48) {
            builder.append(getString(R.string.strNoCardRecycle));
        } else if (status == 49) {
            builder.append(getString(R.string.strTimeOutRecycle));
        }
        builder.append("\n");
    }

    private void processStatusByte1(byte status, StringBuilder builder) {
        if (status == 48) {
            // Device OK (no dispense issue)
        } else if (status == 49) {
            builder.append(getString(R.string.strDispenseErr));
        }
        builder.append("\n");
    }

    private void processStatusByte2(byte status, StringBuilder builder) {
        if (status == 48) {
            builder.append(getString(R.string.strDeviceOK));
        } else if (status == 49) {
            builder.append(getString(R.string.strRecycling));
        }
        builder.append("\n");
    }

    private void processStatusByte3(byte status, StringBuilder builder) {
        if (status == 48) {
            builder.append(getString(R.string.strDeviceOK));
        } else if (status == 49) {
            builder.append(getString(R.string.strDispenseing));
        }
        builder.append("\n");
    }

    private void processStatusByte4(byte status, StringBuilder builder) {
        if (status == 48) {
            builder.append(getString(R.string.strBoxOK));
        } else if (status == 49) {
            builder.append(getString(R.string.strPreEmpty));
        }
        builder.append("\n");
    }

    private void processStatusByte5(byte status, StringBuilder builder) {
        if (status == 48) {
            builder.append(getString(R.string.strNotInReaderPos));
        } else if (status == 49) {
            builder.append(getString(R.string.strInReaderPos));
        }
        builder.append("\n");
    }

    private void processStatusByte6(byte status, StringBuilder builder) {
        if (status == 48) {
            builder.append(getString(R.string.strNotInExitGate));
        } else if (status == 49) {
            builder.append(getString(R.string.strInExitGate));
        }
        builder.append("\n");
    }

    private void processStatusByte7(byte status, StringBuilder builder) {
        if (status == 48) {
            builder.append(getString(R.string.strBoxNotEmpty));
        } else if (status == 49) {
            builder.append(getString(R.string.strBoxEmpty));
        }
        builder.append("\n");
    }
}

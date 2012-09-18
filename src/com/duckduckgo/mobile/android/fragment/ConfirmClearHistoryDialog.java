package com.duckduckgo.mobile.android.fragment;
import com.duckduckgo.mobile.android.R;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

@TargetApi(11)
public class ConfirmClearHistoryDialog extends DialogFragment {

    private Button mOK, mCancel;
    private OnClickListener mOkListener;

    public ConfirmClearHistoryDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clear_history, container);
        mOK = (Button) view.findViewById(R.id.btn_clearhistory_ok);
        mCancel = (Button) view.findViewById(R.id.btn_clearhistory_cancel);
        getDialog().setTitle(getResources().getString(R.string.Confirm));
        
        mOK.setOnClickListener(mOkListener);
        mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();				
			}
		});

        return view;
    }
    
    public void setOKListener(OnClickListener listener){
    	if(listener != null){
    		mOkListener = listener;
    	}
    }
}
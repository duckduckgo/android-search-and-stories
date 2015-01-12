package com.duckduckgo.mobile.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.RequestOpenWebPageEvent;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;

public class HelpFeedbackFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "help_feedback_fragment";

    private Button helpButton;
    private Button feedbackButton;
    private Button rateButton;

    private View fragmentView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_help, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        helpButton = (Button) fragmentView.findViewById(R.id.help_button);
        helpButton.setOnClickListener(this);

        feedbackButton = (Button) fragmentView.findViewById(R.id.feedback_button);
        feedbackButton.setOnClickListener(this);

        rateButton = (Button) fragmentView.findViewById(R.id.rate_button);
        rateButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.help_button:
                BusProvider.getInstance().post(new RequestOpenWebPageEvent(
                        getActivity().getResources().getString(R.string.help_link), SESSIONTYPE.SESSION_BROWSE));
                break;
            case R.id.feedback_button:
                Intent feedbackIntent = DDGUtils.newEmailIntent(getActivity().getResources().getString(R.string.FeedbackTo),
                        getActivity().getResources().getString(R.string.FeedbackSubject), DDGUtils.getBuildInfo(getActivity()), "");
                startActivity(Intent.createChooser(
                        feedbackIntent, getActivity().getResources().getString(R.string.select_application)
                ));//"Select application to send"
                break;
            case R.id.rate_button:
                Intent rateIntent = new Intent(Intent.ACTION_VIEW);
                rateIntent.setData(Uri.parse(getString(R.string.LinkToApp_Google)));
                startActivity(rateIntent);
                break;
            default:
                break;
        }
    }

}

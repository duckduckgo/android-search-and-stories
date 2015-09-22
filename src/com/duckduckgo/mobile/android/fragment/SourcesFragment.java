package com.duckduckgo.mobile.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.SourcesAdapter;
import com.duckduckgo.mobile.android.container.SourcePreferencesContainer;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.tasks.SourcesTask;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;

import java.util.List;

public class SourcesFragment extends Fragment implements SourcesTask.SourcesListener {

    public SourcesTask sourcesTask = null;
    public SourcesAdapter sourcesAdapter = null;

    private ListView sourcesView = null;
    private Button defaultButton = null;
    private Button suggestSourceButton = null;

    public static final String TAG = "sources_fragment";

    private View fragmentView;

    @Override
    public void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_sources, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DDGControlVar.changedSources = true;

        SourcePreferencesContainer sourcePrefContainer = null;
        sourcesAdapter = new SourcesAdapter(getActivity());

        sourcesView = (ListView) fragmentView.findViewById(R.id.sourceItems);
        sourcesView.addHeaderView(createHeaderView());

        sourcesView.setAdapter(sourcesAdapter);

        defaultButton = (Button) fragmentView.findViewById(R.id.sourceDefaultButton);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), R.string.DefaultsSetToast, Toast.LENGTH_SHORT).show();

                DDGUtils.deleteSet(DDGApplication.getSharedPreferences(), "sourceset");
                DDGControlVar.hasUpdatedFeed = false;

                // copy defaults
                DDGControlVar.userAllowedSources.clear();
                DDGControlVar.userDisallowedSources.clear();

                // reset source set of underlying list adapter
                sourcesAdapter.notifyDataSetChanged();
                sourcesView.invalidateViews();
            }
        });

        suggestSourceButton = (Button) fragmentView.findViewById(R.id.suggestSourceButton);
        suggestSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = DDGUtils.newEmailIntent(getActivity().getResources().getString(R.string.FeedbackTo),
                        getActivity().getResources().getString(R.string.FeedbackSubject), getString(R.string.SuggestedSources), "");
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.SelectAppToSend)));
            }
        });
    }

    private View createHeaderView() {
        View headerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.sources_header, null, false);
        TextView title = (TextView) headerView.findViewById(R.id.list_item_section_text);
        title.setText(getActivity().getResources().getString(R.string.options));
        return headerView;
    }

    public void onSourcesRetrieved(List<SourcesObject> feed) {
        sourcesAdapter.setList(feed);
        sourcesAdapter.notifyDataSetChanged();
    }

    public void onSourcesRetrievalFailed() {
        //If the sourcesTask is null, we are currently paused
        //Otherwise, we can try again
        if (sourcesTask != null) {
            sourcesTask = new SourcesTask(getActivity(), this);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                sourcesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                sourcesTask.execute();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sourcesTask = new SourcesTask(getActivity(), SourcesFragment.this);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    sourcesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    sourcesTask.execute();
                }
            }
        }, 200);
    }
}

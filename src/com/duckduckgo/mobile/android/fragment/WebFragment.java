package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.OpenInExternalDialogBuilder;
import com.duckduckgo.mobile.android.download.ContentDownloader;
import com.duckduckgo.mobile.android.events.TestEvent;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.webview.DDGWebChromeClient;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;
import com.duckduckgo.mobile.android.views.webview.DDGWebViewClient;
import com.squareup.otto.Subscribe;

public class WebFragment extends Fragment {

	public static final String TAG = "web_fragment";
	public static final String URL = "url";

	private String mWebViewDefaultUA = null;
	private DDGWebView mainWebView = null;
	private ContentDownloader contentDownloader;

	private boolean savedState = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("aaa", "web fragment on create");
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("aaa", "web fragment on destroy");
		BusProvider.getInstance().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e("aaa", "web fragment on create view");

		if(savedInstanceState!=null) {
			savedState = true;
		}
		return inflater.inflate(R.layout.fragment_web, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("aaa", "web fragment on activity created");
		init();

		/*
		if(getArguments()!=null) {
			mainWebView.loadUrl(getArguments().getString(URL));
		} else {
			mainWebView.loadUrl("www.google.com	");
		}*/
	}
/*
	public void init() {
		init(null);
	}
*/
	public void init() {
		mainWebView = (DDGWebView) getView().findViewById(R.id.fragmentMainWebView);//web fragment
		mainWebView.setParentActivity(getActivity());
		mainWebView.getSettings().setJavaScriptEnabled(true);
		DDGWebView.recordCookies(PreferencesManager.getRecordCookies());
		DDGNetworkConstants.setWebView(mainWebView);

		// get default User-Agent string for reuse later
		mWebViewDefaultUA = mainWebView.getSettings().getUserAgentString();

		PreferencesManager.setWebViewFontDefault(mainWebView.getSettings().getDefaultFontSize());
		DDGControlVar.webViewTextSize = PreferencesManager.getWebviewFontSize();

		mainWebView.setWebViewClient(new DDGWebViewClient(getActivity(), true));
		//mainWebView.setWebChromeClient(new DDGWebChromeClient(getActivity(), null));

		mainWebView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				WebView.HitTestResult hitTestResult = ((DDGWebView) v).getHitTestResult();
				if(hitTestResult != null && hitTestResult.getExtra() != null) {
					Log.i(TAG, "LONG getExtra = " + hitTestResult.getExtra() + "\t\t Type=" + hitTestResult.getType());
					final String touchedUrl = hitTestResult.getExtra();

					new OpenInExternalDialogBuilder(((DuckDuckGo)getActivity()), touchedUrl).show();
				}

				return false;
			}
		});

		contentDownloader = new ContentDownloader((DuckDuckGo)getActivity());//to change

		mainWebView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
										String contentDisposition, String mimetype,
										long contentLength) {

				contentDownloader.downloadContent(url, mimetype);
			}
		});
/*
		if(url!=null) {
			mainWebView.loadUrl(url);
		}*/
	}

	@Subscribe
	public void onTestEvent(TestEvent event) {
		if(mainWebView!=null && !event.url.equals("")) {
			mainWebView.loadUrl(event.url);
		}
	}
}

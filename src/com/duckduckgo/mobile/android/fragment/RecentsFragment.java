package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.view.Menu;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.squareup.otto.Subscribe;

public class RecentsFragment extends TabFragment {

    public static final String TAG = "recents_fragment";

    private Menu recentMenu = null;
    private DDGOverflowMenu overflowMenu = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected TabItem getFirstTabItem() {
        return new TabItem(R.string.recent_stories, R.string.recent_stories_narrow, new RecentFeedTabFragment());
    }

    @Override
    protected TabItem getSecondTabItem() {
        return new TabItem(R.string.recent_searches, R.string.recent_searches_narrow, new RecentResultTabFragment());
    }

    @Override
    protected void setMenu(Menu menu) {
        recentMenu = menu;
    }

    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(getTag()) && recentMenu!=null) {
            recentMenu.findItem(R.id.action_recents).setEnabled(false);
            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            overflowMenu = new DDGOverflowMenu(getActivity());
            overflowMenu.setMenu(recentMenu);
            overflowMenu.show(event.anchor);
        }
    }

}

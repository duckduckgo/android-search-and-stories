package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.view.Menu;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.squareup.otto.Subscribe;

public class FavoriteFragment extends TabFragment {

	public static final String TAG = "saved_fragment";

    private Menu favoriteMenu = null;
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
        return new TabItem(R.string.favorite_stories, R.string.favorite_stories_narrow, new FavoriteFeedTabFragment());
    }

    @Override
    protected TabItem getSecondTabItem() {
        return new TabItem(R.string.favorite_search, R.string.favorite_search_narrow, new FavoriteResultTabFragment());
    }

    @Override
    protected void setMenu(Menu menu) {
        favoriteMenu = menu;
    }

    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(getTag()) && favoriteMenu!=null) {
            favoriteMenu.findItem(R.id.action_favorites).setEnabled(false);
            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            overflowMenu = new DDGOverflowMenu(getActivity());
            overflowMenu.setMenu(favoriteMenu);
            overflowMenu.show(event.anchor);
        }
    }

}
package com.duckduckgo.mobile.android.util;

import android.content.Intent;
import android.net.Uri;

import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Created by mahes on 5/26/16.
 */
public class AppLaunchUtils {

    private static final String GOOGLE_MAP_BANG = "!m";
    private static final String GOOGLE_MAP_BANG_1 = "!gm";

    private static final String YOUTUBE_BANG = "!yt";

    private static final String TWITTER_BANG = "!twitter";

    private static final String GOOGLE_PLAY_BANG = "!gplay";

    private static final String GOOGLE_MAP_INTENT = "com.google.android.apps.maps";
    private static final String YOUTUBE_INTENT = "com.google.android.youtube";

    private static final String GOOGLE_MAP_QUERY= "geo:0,0?q=";

    public static final String BANG_START = "!";

    public static final String[]BANG_LIST = {
            GOOGLE_MAP_BANG, GOOGLE_MAP_BANG_1,
            YOUTUBE_BANG,
            TWITTER_BANG,
            GOOGLE_PLAY_BANG
    };

    public static Intent getBangLaunchIntent(String url) {

        if(url != null && url.length() > 0) {

            int bangStartLocation = url.indexOf(BANG_START);
            String bangName;

            if (bangStartLocation >= 0) {

                if (url.indexOf(' ', bangStartLocation) > bangStartLocation) {
                    bangName = url.substring(bangStartLocation, url.indexOf(' ', bangStartLocation));
                } else {
                    bangName = url.substring(bangStartLocation);
                }

                if (bangName != null && bangName.length() > 0) {

                    url = url.replace(bangName, "");

                    if (Arrays.asList(BANG_LIST).contains(bangName)) {

                        if (bangName.equalsIgnoreCase(GOOGLE_MAP_BANG) || bangName.equalsIgnoreCase(GOOGLE_MAP_BANG_1)) {

                            Uri gmmIntentUri = Uri.parse(GOOGLE_MAP_QUERY + URLEncoder.encode(url));
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage(GOOGLE_MAP_INTENT);

                            return mapIntent;
                        } else if (bangName.equalsIgnoreCase(YOUTUBE_BANG)) {

                            Intent intent = new Intent(Intent.ACTION_SEARCH);
                            intent.setPackage(YOUTUBE_INTENT);
                            intent.putExtra("query", url);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            return intent;
                        } else if (bangName.equalsIgnoreCase(TWITTER_BANG)) {

                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("twitter://search?query=" + url));

                            return intent;
                        } else if (bangName.equalsIgnoreCase(GOOGLE_PLAY_BANG)) {

                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://search?q=" + url + "&c=apps"));

                            return intent;
                        }
                    }
                }
            }
        }

        return null;
    }
}

package com.duckduckgo.mobile.android;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.text.TextUtils;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import junit.framework.Assert;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.duckduckgo.mobile.android.activity.DuckDuckGoTest \
 * com.duckduckgo.mobile.android.tests/android.test.InstrumentationTestRunner
 */
public class DuckDuckGoTest extends ActivityInstrumentationTestCase2<DuckDuckGo> {

    public DuckDuckGoTest() {
        super("com.duckduckgo.mobile.android", DuckDuckGo.class);
    }

    public void test_onCreate_searchField_shouldBe_empty() throws Exception {
        Assert.assertTrue(TextUtils.isEmpty(getActivity().getSearchField().getText()));
    }
}

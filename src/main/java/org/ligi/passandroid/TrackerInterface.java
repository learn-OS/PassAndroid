package org.ligi.passandroid;


import android.support.annotation.Nullable;

public interface TrackerInterface {
    void trackException(String s, Exception e, boolean fatal);

    void trackException(String s, boolean fatal);

    void trackEvent(@Nullable String category, @Nullable String action, @Nullable String label, @Nullable Long val);
}

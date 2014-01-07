package roboguice.android.content;

import roboguice.android.DroidGuice;

import android.content.ContentProvider;

/**
 * Provides injection to content providers.
 * 
 * If you're subclassing this class and need to override #onCreate,
 * make sure to call super.onCreate() first.
 */
public abstract class RoboContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        DroidGuice.instance().getInjector(getContext()).injectMembers(this);
        return true;
    }
}

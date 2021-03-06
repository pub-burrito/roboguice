package roboguice.android.inject;

import roboguice.base.inject.ContextSingleton;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

@ContextSingleton
public class FragmentManagerProvider implements Provider<FragmentManager> {
    @Inject protected Activity activity;

    @Override
    public FragmentManager get() {
        // BUG only supports compat library at the moment.  Does not support honeycomb directly yet
        return ((FragmentActivity)activity).getSupportFragmentManager();
    }
}

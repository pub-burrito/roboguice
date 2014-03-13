package roboguice.android.content;

import roboguice.android.DroidGuice;
import roboguice.base.RoboGuice;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Provides injection to async task loaders.
 * @author cherrydev
 */
public abstract class RoboAsyncTaskLoader<D> extends AsyncTaskLoader<D> {

    public RoboAsyncTaskLoader(Context context) {
        super(context);
        RoboGuice.<DroidGuice>instance().injectMembers(context, this);
    }
    
}

package roboguice.android.util;

import java.util.concurrent.Executor;

import roboguice.android.DroidGuice;
import roboguice.base.RoboGuice;

import android.content.Context;
import android.os.Handler;

public abstract class RoboAsyncTask<ResultT> extends SafeAsyncTask<ResultT> {
    protected Context context;

    protected RoboAsyncTask(Context context) {
        this.context = context;
        RoboGuice.<DroidGuice>instance().getInjector(context).injectMembers(this);
    }

    protected RoboAsyncTask(Context context, Handler handler) {
        super(handler);
        this.context = context;
        RoboGuice.<DroidGuice>instance().getInjector(context).injectMembers(this);
    }

    protected RoboAsyncTask(Context context, Handler handler, Executor executor) {
        super(handler, executor);
        this.context = context;
        RoboGuice.<DroidGuice>instance().getInjector(context).injectMembers(this);
    }

    protected RoboAsyncTask(Context context, Executor executor) {
        super(executor);
        this.context = context;
        RoboGuice.<DroidGuice>instance().getInjector(context).injectMembers(this);
    }

    public Context getContext() {
        return context;
    }
}

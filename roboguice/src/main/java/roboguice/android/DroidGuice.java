package roboguice.android;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import roboguice.android.config.AndroidDefaultRoboModule;
import roboguice.android.inject.AndroidResourceListener;
import roboguice.android.inject.ContextScope;
import roboguice.android.inject.ContextScopedRoboInjector;
import roboguice.android.inject.RoboInjector;
import roboguice.android.inject.ViewListener;
import roboguice.base.RoboGuice;

import com.google.inject.Module;

import android.app.Application;
import android.content.Context;

/**
 *
 * Manages injectors for RoboGuice applications.
 *
 * There are two types of injectors:
 *
 * 1. The base application injector, which is not typically used directly by the user.
 * 2. The ContextScopedInjector, which is obtained by calling {@link #getInjector(android.content.Context)}, and knows about
 *    your current context, whether it's an activity, service, or something else.
 * 
 * BUG hashmap should also key off of stage and modules list
 */

public final class DroidGuice extends RoboGuice<Integer, Application, Context, AndroidDefaultRoboModule, AndroidResourceListener> {
    
    protected static WeakHashMap<Application,ViewListener> viewListeners = new WeakHashMap<Application, ViewListener>();

    private DroidGuice() {
    }

    @Override
    protected List<Module> baseModules(Application application) {
        
        int id = modulesResourceId;
        if (id == 0)
            id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());

        final String[] moduleNames = id>0 ? application.getResources().getStringArray(id) : new String[]{};
        final ArrayList<Module> modules = new ArrayList<Module>();
        final AndroidDefaultRoboModule defaultRoboModule = newDefaultRoboModule(application);

        modules.add(defaultRoboModule);

        try {
            for (String name : moduleNames) {
                final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);

                try {
                    modules.add(clazz.getDeclaredConstructor(Context.class).newInstance(application));
                } catch( NoSuchMethodException ignored ) {
                    modules.add( clazz.newInstance() );
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return modules;
    }

    @Override
    public RoboInjector getInjector( Context context ) {
        final Application application = (Application)context.getApplicationContext();
        return new ContextScopedRoboInjector(context, getScopedInjector(application), getViewListener(application));
    }
    
    @Override
    protected AndroidResourceListener getResourceListener(Application application) {
        AndroidResourceListener resourceListener = resourceListeners.get(application);
        if( resourceListener==null ) {
            synchronized (RoboGuice.class) {
                if( resourceListener==null ) {
                    resourceListener = new AndroidResourceListener( application );
                    resourceListeners.put(application,resourceListener);
                }
            }
        }
        return resourceListener;
    }

    @Override
    public AndroidDefaultRoboModule newDefaultRoboModule( Application application ) {
        return new AndroidDefaultRoboModule(application, new ContextScope(application), getViewListener(application), getResourceListener(application));
    }

    protected ViewListener getViewListener( final Application application ) {
        ViewListener viewListener = viewListeners.get(application);
        if( viewListener==null ) {
            synchronized (DroidGuice.class) {
                if( viewListener==null ) {
                    viewListener = new ViewListener();
                    viewListeners.put(application,viewListener);
                }
            }
        }
        return viewListener;
    }
}

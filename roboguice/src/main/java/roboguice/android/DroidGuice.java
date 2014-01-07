package roboguice.android;

import java.util.ArrayList;
import java.util.WeakHashMap;

import roboguice.android.config.DefaultRoboModule;
import roboguice.android.event.EventManager;
import roboguice.android.inject.ContextScope;
import roboguice.android.inject.ContextScopedRoboInjector;
import roboguice.android.inject.RoboInjector;
import roboguice.android.inject.ViewListener;
import roboguice.base.RoboGuice;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

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

public class DroidGuice extends RoboGuice<Application> {
    
    protected static WeakHashMap<Application,ViewListener> viewListeners = new WeakHashMap<Application, ViewListener>();
    protected static int modulesResourceId = 0;
    
    private static DroidGuice guice = null;
    
    public static DroidGuice instance()
    {
        if ( guice == null )
        {
            guice = new DroidGuice();
        }
        
        return guice;
    }
    
    private DroidGuice() {
    }

    /**
     * Allows the user to override the "roboguice_modules" resource name with some other identifier.
     * This is a static value.
     */
    public void setModulesResourceId(int modulesResourceId) {
        DroidGuice.modulesResourceId = modulesResourceId;
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public Injector setBaseApplicationInjector(Application application, Stage stage) {

        synchronized (DroidGuice.class) {
            int id = modulesResourceId;
            if (id == 0)
                id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());

            final String[] moduleNames = id>0 ? application.getResources().getStringArray(id) : new String[]{};
            final ArrayList<Module> modules = new ArrayList<Module>();
            final DefaultRoboModule defaultRoboModule = newDefaultRoboModule(application);

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

            final Injector rtrn = setScopedInjector(application, stage, modules.toArray(new Module[modules.size()]));
            injectors.put(application,rtrn);
            return rtrn;
        }

    }


    public RoboInjector getInjector(Context context) {
        final Application application = (Application)context.getApplicationContext();
        return new ContextScopedRoboInjector(context, getScopedInjector(application), getViewListener(application));
    }

    /**
     * A shortcut for RoboGuice.getInjector(context).injectMembers(o);
     */
    public <T> T injectMembers( Context context, T t ) {
        getInjector(context).injectMembers(t);
        return t;
    }

    public DefaultRoboModule newDefaultRoboModule(final Application application) {
        return new DefaultRoboModule(application, new ContextScope(application), getViewListener(application), getResourceListener(application));
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
    
    public static class util {
        private util() {}

        /**
         * This method is provided to reset RoboGuice in testcases.
         * It should not be called in a real application.
         */
        public static void reset() {
            guice.injectors.clear();
            guice.resourceListeners.clear();
            viewListeners.clear();
        }
    }
}

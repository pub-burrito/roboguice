package roboguice.java;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import roboguice.android.util.RoboContext;
import roboguice.base.RoboGuice;
import roboguice.base.util.PropertyLoader;
import roboguice.base.util.logging.Ln;
import roboguice.java.config.JavaDefaultRoboModule;
import roboguice.java.inject.JavaResourceListener;
import roboguice.java.inject.RoboApplication;

import com.google.inject.Injector;
import com.google.inject.Module;

public final class JavaGuice extends RoboGuice<String, RoboApplication, RoboContext, JavaDefaultRoboModule, JavaResourceListener> {

    private JavaGuice() {
        modulesResourceId = "";
    }
    
    @Override
    protected List<Module> baseModules(RoboApplication scopedObject) 
    {
        List<URL> configMatches = PropertyLoader.urlsFor(scopedObject, null);
        Ln.v("Configuration files: %s", configMatches);
        
        Properties property = PropertyLoader.loadProperty(scopedObject, new Properties(), null);
        
        String custom_modules = property != null ? (String) property.get(modulesResourceId) : null;
        final String[] moduleNames = custom_modules != null ? custom_modules.split( "," ) : new String[]{};
        
        final ArrayList<Module> modules = new ArrayList<Module>();
        final JavaDefaultRoboModule defaultRoboModule = newDefaultRoboModule( scopedObject );

        modules.add(defaultRoboModule);

        try {
            for (String name : moduleNames) {
                final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                
                modules.add( clazz.newInstance() );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return modules;
    }

    @Override
    public Injector getInjector(RoboContext scopedObject) {
        return getScopedInjector( new RoboApplication(scopedObject) );
    }
    
    @Override
    public JavaDefaultRoboModule newDefaultRoboModule( RoboApplication scopedObject ) {
        return new JavaDefaultRoboModule( getResourceListener(scopedObject) );
    }

    public JavaGuice addResourcePath( RoboApplication scopedObject, String... paths )
    {
        getResourceListener( scopedObject ).addResourcePath(paths);
        return this;
    }
    
    public JavaGuice addResourceComparator( RoboApplication scopedObject, Comparator<URL> comparator )
    {
        getResourceListener(scopedObject).addResourceComparator(comparator);
        return this;
    }
    
    @Override
    protected JavaResourceListener getResourceListener(RoboApplication scopedObject) {
        
        JavaResourceListener resourceListener = resourceListeners.get(scopedObject);
        if( resourceListener==null ) {
            synchronized (RoboGuice.class) {
                if( resourceListener==null ) {
                    resourceListener = new JavaResourceListener();
                    resourceListeners.put(scopedObject,resourceListener);
                }
            }
        }
        
        return resourceListener;
    }


}
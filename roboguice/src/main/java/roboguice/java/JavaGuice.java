package roboguice.java;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import roboguice.base.RoboGuice;
import roboguice.base.util.logging.Ln;
import roboguice.java.config.JavaDefaultRoboModule;
import roboguice.java.inject.JavaResourceListener;

import com.google.inject.Injector;
import com.google.inject.Module;

public final class JavaGuice extends RoboGuice<String, String, String, JavaDefaultRoboModule, JavaResourceListener> {

    private JavaGuice() {
    }
    
    @Override
    protected List<Module> baseModules(String scopedObject) 
    {
        Properties property = new Properties();
        
        //scoped object should the directory to property file
        InputStream in = JavaResourceListener.class.getClassLoader().getResourceAsStream( scopedObject );
        try
        {//and load the property file 
            if ( in != null )
            {
                property.load( in );
            } 
            else
            {
                Ln.w( "Could not find [%s] resource - can not inject any resources in specified file.", scopedObject );
            }
        }
        catch ( Exception e )
        {
            Ln.e( e, "Error loading property file [%s]", scopedObject );
        }
        finally
        {
            try
            {
                in.close();
            }
            catch ( Exception ex )
            {
                // ignore
            }
        }
        
        String custom_modules = (String) property.get(modulesResourceId);
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
    public Injector getInjector(String scopedObject) {
        return getScopedInjector( scopedObject );
    }
    
    @Override
    public JavaDefaultRoboModule newDefaultRoboModule( String scopedObject ) {
        return new JavaDefaultRoboModule( getResourceListener(scopedObject) );
    }

    public JavaGuice addResourcePath( String scopedObject, String... paths )
    {
        getResourceListener( scopedObject ).addResourcePath(paths);
        return this;
    }
    
    @Override
    protected JavaResourceListener getResourceListener(String scopedObject) {
        
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
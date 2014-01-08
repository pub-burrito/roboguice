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

    private static JavaGuice guice = null;
    
    public static JavaGuice instance()
    {
        if ( guice == null )
        {
            guice = new JavaGuice();
        }
        
        return guice;
    }
    
    private JavaGuice() {
    }
    
    @Override
    protected List<Module> baseModules(String scopeObject) 
    {
        Properties property = new Properties();
        
        InputStream in = JavaResourceListener.class.getClassLoader().getResourceAsStream( scopeObject );
        try
        {//and load the property file 
            if ( in != null )
            {
                property.load( in );
            } 
            else
            {
                Ln.w( "Could not find [%s] resource - can not inject any resources in specified file.", scopeObject );
            }
        }
        catch ( Exception e )
        {
            Ln.e( e, "Error loading property file [%s]", scopeObject );
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
        
        final String[] moduleNames = property.get(modulesResourceId) != null ? (String[]) property.get(modulesResourceId) : new String[]{};
        final ArrayList<Module> modules = new ArrayList<Module>();
        final JavaDefaultRoboModule defaultRoboModule = newDefaultRoboModule( scopeObject );

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
    public Injector getInjector(String context) {
        return getScopedInjector( context );
    }
    
    @Override
    public JavaDefaultRoboModule newDefaultRoboModule( String scopeObject ) {
        return new JavaDefaultRoboModule( getResourceListener(scopeObject) );
    }

    @Override
    protected JavaResourceListener getResourceListener(String scopeObject) {
        
        JavaResourceListener resourceListener = resourceListeners.get(scopeObject);
        if( resourceListener==null ) {
            synchronized (RoboGuice.class) {
                if( resourceListener==null ) {
                    resourceListener = new JavaResourceListener( );
                    resourceListeners.put(scopeObject,resourceListener);
                }
            }
        }
        
        return resourceListener;
    }


}
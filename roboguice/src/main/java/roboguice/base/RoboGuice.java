package roboguice.base;

import java.util.WeakHashMap;

import roboguice.android.inject.ResourceListener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.StaticInjectionRequest;

/**
 * 
 * <!--
 * RoboGuice.java
 * -->
 * TODO Description
 * 
 * @param <T> Injection scope
 */
public class RoboGuice<T>{
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;
    
    protected WeakHashMap<T,Injector> injectors = new WeakHashMap<T,Injector>();
    protected WeakHashMap<T,ResourceListener> resourceListeners = new WeakHashMap<T, ResourceListener>();
    
    public WeakHashMap<T, Injector> injectors()
    {
        return injectors;
    }
    
    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public Injector getScopedInjector(T scopedObject ) {
        Injector rtrn = injectors.get(scopedObject);
        if( rtrn!=null )
            return rtrn;

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(scopedObject);
            if( rtrn!=null )
                return rtrn;
            
            return setScopedInjector(scopedObject, DEFAULT_STAGE);
        }
    }
    
    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     * If specifying your own modules, you must include a DefaultRoboModule for most things to work properly.
     * Do something like the following:
     *
     * RoboGuice.setAppliationInjector( app, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.newDefaultRoboModule(app)).with(new MyModule() );
     *
     * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
     * @see roboguice.android.DroidGuice#setScopedInjector(android.app.Application, com.google.inject.Stage, com.google.inject.Module...)
     * @see roboguice.android.DroidGuice#newDefaultRoboModule(android.app.Application)
     * @see roboguice.android.DroidGuice#DEFAULT_STAGE
     *
     * If using this method with test cases, be sure to call {@link roboguice.android.DroidGuice.util#reset()} in your test teardown methods
     * to avoid polluting our other tests with your custom injector.  Don't do this in your real application though.
     *
     */
    public Injector setScopedInjector(final T scopedObject, Stage stage, Module... modules) {

        // Do a little rewriting on the modules first to
        // add static resource injection
        for(Element element : Elements.getElements(modules)) {
            element.acceptVisitor(new DefaultElementVisitor<Void>() {
                @Override
                public Void visit(StaticInjectionRequest element) {
                    getResourceListener(scopedObject).requestStaticInjection(element.getType());
                    return null;
                }
            });
        }

        synchronized (RoboGuice.class) {
            final Injector rtrn = Guice.createInjector(stage, modules);
            injectors.put(scopedObject,rtrn);
            return rtrn;
        }
    }
    
    protected ResourceListener getResourceListener( T scopedObject ) {
        ResourceListener resourceListener = resourceListeners.get(scopedObject);
        if( resourceListener==null ) {
            synchronized (RoboGuice.class) {
                if( resourceListener==null ) {
                    //FIXME ResourceListener
//                    resourceListener = new ResourceListener( application );
                    resourceListeners.put(scopedObject,resourceListener);
                }
            }
        }
        return resourceListener;
    }
}

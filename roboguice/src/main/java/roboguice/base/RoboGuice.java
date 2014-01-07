package roboguice.base;

import java.util.List;
import java.util.WeakHashMap;

import roboguice.android.DroidGuice;
import roboguice.android.config.DefaultRoboModule;
import roboguice.android.event.EventManager;
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
 * @param <S> Object which a main {@link Injector} is scoped to
 * @param <O> Object which multiple {@link Injector}s are scoped bye
 * @param <R> Specific {@link DefaultRoboModule} impl
 */
public abstract class RoboGuice<S, O, R extends DefaultRoboModule>{
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;
    
    protected WeakHashMap<S,Injector> injectors = new WeakHashMap<S,Injector>();
    protected WeakHashMap<S,ResourceListener> resourceListeners = new WeakHashMap<S, ResourceListener>();
    
    public WeakHashMap<S, Injector> injectors()
    {
        return injectors;
    }
    
    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public Injector getScopedInjector(S scopedObject ) {
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
    public Injector setScopedInjector(final S scopedObject, Stage stage, Module... modules) {

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
    
    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public Injector setScopedInjector(S application, Stage stage) {

        synchronized (DroidGuice.class) {
            
            List<Module> modules = baseModules(application);
            final Injector rtrn = setScopedInjector(application, stage, modules.toArray(new Module[modules.size()]));
            injectors.put(application,rtrn);
            return rtrn;
        }
    }
    
    /**
     * A shortcut for RoboGuice.getInjector(context).injectMembers(o);
     */
    public <Z> Z injectMembers( O context, Z s ) {
        getInjector(context).injectMembers(s);
        return s;
    }
    
    protected abstract List<Module> baseModules( S application );
    
    public void destroyInjector(O context) {
        final Injector injector = getInjector(context);
        injector.getInstance(EventManager.class).destroy();
        injectors.remove(context);
    }
    
    public Injector getInjector( O context )
    {
        return Guice.createInjector(DEFAULT_STAGE);
    }
    
    public abstract DefaultRoboModule newDefaultRoboModule( S app );
    
    protected ResourceListener getResourceListener( S scopedObject ) {
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

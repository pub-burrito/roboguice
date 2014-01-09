package roboguice.base;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.WeakHashMap;

import roboguice.base.config.DefaultRoboModule;
import roboguice.base.event.EventManager;
import roboguice.base.inject.ResourceListener;

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
 * @param <I> Id to identify the Custom Modules
 * @param <S> Object which a main {@link Injector} is scoped to
 * @param <O> Object which multiple {@link Injector}s are scoped bye
 * @param <R> Specific {@link DefaultRoboModule} impl
 * @param <L> Specific {@link ResourceListener} impl
 */
public abstract class RoboGuice<I, S, O, R extends DefaultRoboModule<L>, L extends ResourceListener>{
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;
    
    protected I modulesResourceId;
    
    protected WeakHashMap<S,Injector> injectors = new WeakHashMap<S,Injector>();
    protected WeakHashMap<S,L> resourceListeners = new WeakHashMap<S,L>();
    
    @SuppressWarnings("rawtypes")
    private static RoboGuice instance;
    public static RoboGuiceType type;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T extends RoboGuice> T instance()
    {
        if ( instance == null )
        {
            try
            {
                if ( type == RoboGuiceType.JAVA )
                {
                    instance = (RoboGuice) Class.forName("roboguice.java.JavaGuice").newInstance();
                }
                else if ( type == RoboGuiceType.ANDROID )
                {
                    instance = (RoboGuice) Class.forName("roboguice.android.AndroidGuice").newInstance(); 
                }
                else
                {
                    throw new InvalidParameterException( "RoboGuice.type was not set, make sure it is not null before requesting an instance of RoboGuice");
                }
            }
            catch ( Exception e )
            {
               new RuntimeException(e);
            }
        }
        
        return (T) instance;
    }
    
    public static enum RoboGuiceType
    {
        JAVA,
        ANDROID
        ;
    }
    
    public WeakHashMap<S, Injector> injectors()
    {
        return injectors;
    }
    
    /**
     * Allows the user to override the "roboguice_modules" resource name with some other identifier.
     * This is a static value.
     */
    public void setModulesResourceId(I modulesResourceId) {
        this.modulesResourceId = modulesResourceId;
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
    public Injector setScopedInjector(S scopedObject, Stage stage) {

        synchronized (RoboGuice.class) {
            
            List<Module> modules = baseModules(scopedObject);
            final Injector rtrn = setScopedInjector(scopedObject, stage, modules.toArray(new Module[modules.size()]));
            injectors.put(scopedObject,rtrn);
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
    
    protected abstract List<Module> baseModules( S scopeObject );
    
    public void destroyInjector(O context) {
        final Injector injector = getInjector(context);
        injector.getInstance(EventManager.class).destroy();
        injectors.remove(context);
    }
        
    public Injector getInjector( O context )
    {
        return Guice.createInjector(DEFAULT_STAGE);
    }
    
    public abstract R newDefaultRoboModule( S scopeObject );
    
    protected abstract L getResourceListener( S scopedObject );
    
    public static class util {
        private util() {}

        /**
         * This method is provided to reset RoboGuice in testcases.
         * It should not be called in a real application.
         */
        @SuppressWarnings("rawtypes")
        public static void reset() {
            RoboGuice.instance().injectors.clear();
            RoboGuice.instance().resourceListeners.clear();
            
            try
            {
                //doing through reflection to not have any dependencies on child class on RoboGuice
                Field f = RoboGuice.instance().getClass().getField("viewListeners");
                if ( RoboGuice.instance().getClass().getField("viewListeners") != null )
                {
                    f.setAccessible(true);
                    ( (WeakHashMap) f.get( RoboGuice.instance() ) ).clear();
                }
            }
            catch ( Exception e )
            {
                //ignore
            }
        }
    }
}

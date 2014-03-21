package roboguice.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

import roboguice.base.config.DefaultRoboModule;
import roboguice.base.event.EventManager;
import roboguice.base.inject.ResourceListener;
import roboguice.base.inject.ResourceListener.RequestStaticResourceInjection;
import roboguice.base.util.logging.Ln;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

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
        Ln.v( "Current: %s, Type: %s", instance, type );
        
        if ( instance == null )
        {
            try
            {
                if ( type == RoboGuiceType.JAVA )
                {
                    instance = juice("roboguice.java.JavaGuice");
                }
                else if ( type == RoboGuiceType.ANDROID )
                {
                    instance = juice("roboguice.android.DroidGuice");
                }
                else
                {
                    throw new InvalidParameterException( "RoboGuice.type was not set, make sure it is not null before requesting an instance of RoboGuice");
                }
            }
            catch ( Exception e )
            {
               throw new RuntimeException(e);
            }
        }
        
        return (T) instance;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends RoboGuice> T juice( String className ) throws ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        Class<?> clazz = Class.forName(className);
        
        Constructor constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        
        return (T) constructor.newInstance();
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
        Ln.v( "Getting injector for [%s] - Current: %s", scopedObject, injectors.keySet() );
        
        Injector rtrn = injectors.get(scopedObject);
        if( rtrn!=null )
            return rtrn;

        Ln.v( new Throwable() );
        
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
        
        Ln.v("Modules: %s", modules.length);
        Ln.v(" - %s", Arrays.asList( modules ));
        Ln.v("Stage: %s", stage );
        
        if ( stage == Stage.DEVELOPMENT )
        {
            GuiceDebug.enable();
        }
        
        requestStaticResourceInjection(scopedObject, modules);
        
        synchronized (RoboGuice.class) {
            final Injector rtrn = Guice.createInjector(stage, modules);
            injectors.put(scopedObject,rtrn);
            return rtrn;
        }
    }

    /**
     * Checking if module requests static resource injection for particular types
     */
    @SuppressWarnings("rawtypes")
    protected void requestStaticResourceInjection(final S scopedObject, Module... modules) {
        
        for (Module module : modules) 
        {
            Class<? extends Module> moduleClass = module.getClass();
            
            if ( isAnnotationPresent( moduleClass, RequestStaticResourceInjection.class) )
            {
                RequestStaticResourceInjection requestStaticResourceInjection = getAnnotation(moduleClass, RequestStaticResourceInjection.class);
                
                Class[] classesToStaticallyInjectResources = requestStaticResourceInjection.value();
                
                Ln.v( "Static injection of resources for: " + Arrays.asList(classesToStaticallyInjectResources) );
                
                getResourceListener( scopedObject ).requestStaticInjection( classesToStaticallyInjectResources );
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean isAnnotationPresent(Class<? extends Module> moduleClass, Class<? extends Annotation> annotationClass) {
        return 
               moduleClass.isAnnotationPresent( annotationClass ) || 
               isModule( moduleClass ) &&
               isAnnotationPresent( (Class<? extends Module>) moduleClass.getSuperclass(), annotationClass);
    }

    protected boolean isModule(Class<? extends Module> moduleClass) {
        return moduleClass.getSuperclass() != null &&
           Module.class.isAssignableFrom( moduleClass.getSuperclass() );
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends Annotation> T getAnnotation(Class<? extends Module> moduleClass, Class<T> annotationClass) {
        return moduleClass.getAnnotation(annotationClass) != null ?
                moduleClass.getAnnotation(annotationClass) :
                  isModule( moduleClass ) ?
                     getAnnotation( (Class<? extends Module>) moduleClass.getSuperclass(), annotationClass ) :
                     null;
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
        public static void reset() {
            if (instance == null)
            {
                //already reset
                return;
            }
            
            instance.injectors.clear();
            instance.resourceListeners.clear();
            
            resetDroidGuice();
            
            RoboGuice.instance = null;
            RoboGuice.type = null;
        }

        @SuppressWarnings("rawtypes")
        private static void resetDroidGuice() {
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

package roboguice.java.inject;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Comparator;

import roboguice.base.inject.InjectResource;
import roboguice.base.inject.ResourceListener;
import roboguice.java.util.ResourceManager;

import com.google.inject.TypeLiteral;

public class JavaResourceListener extends ResourceListener {

    public JavaResourceListener ( String... resourcePaths )
    {
        addResourcePath(resourcePaths);                      
    }
    
    @Override
    protected <I> ResourceMemberInjector<I> newResourceMemberInjector( TypeLiteral<I> typeLiteral, Field field ) {
        return new JavaMemberResourceInjector<I>(field, field.getAnnotation(InjectResource.class) );
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    protected ResourceMemberInjector newResourceMemberInjector( Field field ) {
        return new JavaMemberResourceInjector( field, field.getAnnotation(InjectResource.class) );
    }
    
    public JavaResourceListener addResourcePath( String... paths )
    {
        ResourceManager.instance().addResourcePath(paths);
        
        return this;
    }
    
    public JavaResourceListener addResourceComparator( Comparator<URL> comparator )
    {
        ResourceManager.instance().addComparator(comparator);
        return this;
    }
    
    public JavaResourceListener removeResourcePath( String... paths )
    {
        ResourceManager.instance().removeResourcePath(paths);
        
        return this;
    }
    
    public JavaResourceListener removeAllPaths()
    {
        ResourceManager.instance().reset();
        return this;
    }
    
    protected static class JavaMemberResourceInjector<T> extends ResourceMemberInjector<T>
    {
        public JavaMemberResourceInjector(Field field, InjectResource annotation ) 
        {
            super(field, annotation);
        }

        @Override
        protected Object getValue() 
        {
            String namedValue = ResourceManager.instance().getValue( annotation.name() );
            String idValue = ResourceManager.instance().getValue( String.valueOf( annotation.value() ) );
            
            return namedValue != null ? namedValue : idValue;
        }
       
    }

}

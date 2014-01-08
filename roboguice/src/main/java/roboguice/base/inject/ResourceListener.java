package roboguice.base.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public abstract class ResourceListener implements TypeListener {
    
    public ResourceListener() {
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        
        for( Class<?> c = typeLiteral.getRawType(); c!=Object.class; c = c.getSuperclass() )
            for (Field field : c.getDeclaredFields())
                if ( field.isAnnotationPresent(InjectResource.class) && !Modifier.isStatic(field.getModifiers()) )
                    typeEncounter.register( newResourceMember( typeLiteral, field ) );

    }

    @SuppressWarnings("unchecked")
    public void requestStaticInjection(Class<?>... types) {
        
        for (Class<?> c : types)
            for( ; c!=Object.class; c=c.getSuperclass() )
                for (Field field : c.getDeclaredFields())
                    if (Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(InjectResource.class))
                        newResourceMember( field ).injectMembers(null);

    }
    
    protected abstract <I> ResourceMemberInjector<I> newResourceMember( TypeLiteral<I> typeLiteral /*Here to figure out the type*/, Field field );
    
    @SuppressWarnings("rawtypes")
    protected abstract ResourceMemberInjector newResourceMember( Field field );
    
    
    protected static abstract class ResourceMemberInjector<T> implements MembersInjector<T>
    {

        protected Field field;
        protected InjectResource annotation;
        
        public ResourceMemberInjector(Field field, InjectResource annotation) {
            this.field = field;
            this.annotation = annotation;
        }

        public void injectMembers( T instance ) {

            Object value = null;

            try {

                value = getValue();
                
                if (value == null && Nullable.notNullable(field) ) {
                    throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field
                            .getName()));
                }

                field.setAccessible(true);
                field.set(instance, value);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (IllegalArgumentException f) {
                throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
                        field.getType(), field.getName()));
            }
        }

        protected abstract Object getValue();
        
    }
}

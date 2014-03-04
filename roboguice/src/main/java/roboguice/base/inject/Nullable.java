package roboguice.base.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import roboguice.base.util.Strings;

public class Nullable {
    private Nullable() {
    }
    
    public static boolean notNullable( Field field ) {
        return !isNullable( field );
    }

    public static boolean isNullable(Field field) {
        for( Annotation a : field.getAnnotations() )
            if( Strings.equals("Nullable",a.annotationType().getSimpleName()))
                return true;
        
        return false;
    }
}

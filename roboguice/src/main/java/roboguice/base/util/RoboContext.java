package roboguice.base.util;

import java.util.Map;

import com.google.inject.Key;

/**
 * Represents a context by which injection can be scoped, so instances can be controlled by the current context (in case they are context singletons instead of normal singletons).
 */
public interface RoboContext<A> {
    Map<Key<?>,Object> getScopedObjectMap();
    
    public A getApplicationContext();
}

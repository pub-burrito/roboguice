package roboguice.base.inject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import roboguice.android.inject.AndroidContextScope;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

public abstract class RoboScope<A, C> implements Scope
{
    protected ThreadLocal<Stack<WeakReference<C>>> contextThreadLocal = new ThreadLocal<Stack<WeakReference<C>>>();
    protected Map<Key<?>,Object> applicationScopedObjects = new HashMap<Key<?>, Object>();
    protected A application;

    public RoboScope(A application, C context) {
        this.application = application;
        enter(context);
    }

    /**
     * You MUST perform any injector operations inside a synchronized(ContextScope.class) block that starts with
     * scope.enter(context) if working in a multithreaded environment
     *
     * @see AndroidContextScope
     * @see ContextScopedRoboInjector
     * @see ContextScopedProvider
     * @param context the context to enter
     */
    public void enter(C context) {

        // BUG synchronizing on ContextScope.class may be overly conservative
        synchronized (AndroidContextScope.class) {

            final Stack<WeakReference<C>> stack = getContextStack();
            final Map<Key<?>,Object> map = getScopedObjectMap(context);

            // Mark this thread as for this context
            stack.push(new WeakReference<C>(context));

            // Add the context to the scope for key Context, Activity, etc.
            Class<?> c = context.getClass();
            do {
                map.put(Key.get(c), context);
                c = c.getSuperclass();
            } while( c!=Object.class );
        }

    }

    public void exit(C context) {
        synchronized (AndroidContextScope.class) {
            final Stack<WeakReference<C>> stack = getContextStack();
            final C c = stack.pop().get();
            if( c!=null && c!=context )
                throw new IllegalArgumentException(String.format("Scope for %s must be opened before it can be closed",context));
        }
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                synchronized (AndroidContextScope.class) {
                    final Stack<WeakReference<C>> stack = getContextStack();
                    final C context = stack.peek().get(); // The context should never be finalized as long as the provider is still in memory
                    final Map<Key<?>, Object> objectsForScope = getScopedObjectMap(context);
                    if( objectsForScope==null )
                        return null;  // May want to consider throwing an exception here (if provider is used after onDestroy())

                    @SuppressWarnings({"unchecked"}) T current = (T) objectsForScope.get(key);
                    if (current==null && !objectsForScope.containsKey(key)) {
                        current = unscoped.get();
                        objectsForScope.put(key, current);
                    }

                    return current;
                }
            }
        };

    }


    public Stack<WeakReference<C>> getContextStack() {
        Stack<WeakReference<C>> stack = contextThreadLocal.get();
        if( stack==null ) {
            stack = new Stack<WeakReference<C>>();
            contextThreadLocal.set(stack);
        }
        return stack;
    }

    protected abstract Map<Key<?>,Object> getScopedObjectMap(final C origContext);
}

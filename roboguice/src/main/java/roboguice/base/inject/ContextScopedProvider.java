package roboguice.base.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;

public class ContextScopedProvider<A,C,T> {
    @Inject protected RoboScope scope;
    @Inject protected Provider<T> provider;

    public T get(C context) {
        synchronized (RoboScope.class) {
            scope.enter(context);
            try {
                return provider.get();
            } finally {
                scope.exit(context);
            }
        }
    }
}

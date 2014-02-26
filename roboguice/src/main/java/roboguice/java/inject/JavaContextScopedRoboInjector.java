package roboguice.java.inject;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roboguice.base.util.RoboContext;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverterBinding;

public class JavaContextScopedRoboInjector implements Injector {
    protected Injector delegate;
    protected RoboContext context;
    protected JavaContextScope scope;

    public JavaContextScopedRoboInjector(RoboContext context, Injector applicationInjector ) {
        this.delegate = applicationInjector;
        this.context = context;
        this.scope = delegate.getInstance(JavaContextScope.class);
    }

    @Override
    public Injector createChildInjector(Iterable<? extends Module> modules) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.createChildInjector(modules);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Injector createChildInjector(Module... modules) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.createChildInjector(modules);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.findBindingsByType(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Map<Key<?>, Binding<?>> getAllBindings() {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getAllBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getBinding(Key<T> key) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getBinding(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getBinding(Class<T> type) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getBinding(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Map<Key<?>, Binding<?>> getBindings() {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getExistingBinding(Key<T> key) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getExistingBinding(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> T getInstance(Key<T> key) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getInstance(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getInstance(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getMembersInjector(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getMembersInjector(typeLiteral);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Injector getParent() {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getParent();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getProvider(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getProvider(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getScopeBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Set<TypeConverterBinding> getTypeConverterBindings() {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getTypeConverterBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public void injectMembers(Object instance) {
        synchronized (JavaContextScope.class) {
            scope.enter(context);
            try {
                delegate.injectMembers(instance);
            }finally {
                scope.exit(context);
            }
        }
    }

}

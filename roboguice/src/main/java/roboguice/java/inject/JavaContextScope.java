/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.java.inject;

import java.util.Map;

import roboguice.base.inject.RoboScope;
import roboguice.base.util.RoboContext;

import com.google.inject.Key;

/**
 * Scopes the injector based on the current context.
 *
 * Any usage of this class must call #enter(Context) before performing any operations with the
 * injector, and do so within a synchronized block on the ContextScope.class, eg:
 *
 * synchronized(ContextScope.class) {
 *     scope.enter(context);
 *
 *     // do something, eg.
 *     // injector.injectMembers(this);
 * }
 *
 * If you're using ContextScopedRoboInjector (which is the RoboGuice default), this is done for you automatically.
 *
 * If you're trying to use a Provider, you must either use ContextScopedProvider instead, or do your own synchronization
 * and scope.enter() call.
 *
 * @see ContextScopedRoboInjector
 */
public class JavaContextScope extends RoboScope<RoboApplication, RoboContext> {
    
    public JavaContextScope(RoboApplication application) {
        super(application, application);
    }

    @Override
    protected Map<Key<?>,Object> getScopedObjectMap(final RoboContext origContext) {
        
        // Special case for application so that users don't have to manually set up application subclasses
        if( origContext instanceof RoboApplication )
            return applicationScopedObjects;

        return origContext.getScopedObjectMap();
    }
}

package roboguice.base.config;

import roboguice.base.inject.ResourceListener;

import com.google.inject.AbstractModule;

public abstract class DefaultRoboModule<L extends ResourceListener> extends AbstractModule {

    protected L resourceListener;
    
    public DefaultRoboModule( L listener )
    {
        this.resourceListener = listener;
    }
}

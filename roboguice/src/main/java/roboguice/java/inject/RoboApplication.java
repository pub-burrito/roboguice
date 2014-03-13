package roboguice.java.inject;

import java.util.Map;

import roboguice.base.util.RoboContext;

import com.google.inject.Key;

public class RoboApplication implements RoboContext<RoboApplication> {

    private String configurationPath;
    
    public RoboApplication( String configurationPath )
    {
        this.configurationPath = configurationPath;
    }
    
    public String configurationPath()
    {
        return configurationPath;
    }
    
    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return null;
    }

    @Override
    public RoboApplication getApplicationContext() {
        return this;
    }

}

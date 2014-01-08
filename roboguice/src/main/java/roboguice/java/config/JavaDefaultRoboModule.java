package roboguice.java.config;

import roboguice.base.config.DefaultRoboModule;
import roboguice.java.inject.JavaResourceListener;

public class JavaDefaultRoboModule extends DefaultRoboModule<JavaResourceListener> {

    public JavaDefaultRoboModule(JavaResourceListener listener) {
        super(listener);
    }

    @Override
    protected void configure() {
        //FIXME What to bind. Hrmm...
    }

}

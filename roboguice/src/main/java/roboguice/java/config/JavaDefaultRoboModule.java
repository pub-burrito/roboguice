package roboguice.java.config;

import roboguice.base.config.DefaultRoboModule;
import roboguice.base.util.logging.BaseConfig;
import roboguice.base.util.logging.Ln;
import roboguice.base.util.logging.Print;
import roboguice.java.inject.JavaResourceListener;
import roboguice.java.util.logging.JavaBaseConfig;
import roboguice.java.util.logging.JavaPrint;

import com.google.inject.matcher.Matchers;

public class JavaDefaultRoboModule extends DefaultRoboModule<JavaResourceListener> {

    public JavaDefaultRoboModule(JavaResourceListener listener) {
        super(listener);
    }

    @Override
    protected void configure() {
        
        bindListener(Matchers.any(), resourceListener);
        
        bind(BaseConfig.class).to(JavaBaseConfig.class);
        bind(Print.class).to(JavaPrint.class);
        requestStaticInjection(Ln.class);
    }

}

package roboguice.java.config;

import roboguice.base.config.DefaultRoboModule;
import roboguice.base.inject.ContextSingleton;
import roboguice.base.inject.RoboScope;
import roboguice.base.util.logging.BaseConfig;
import roboguice.base.util.logging.Ln;
import roboguice.base.util.logging.Writer;
import roboguice.java.inject.JavaContextScope;
import roboguice.java.inject.JavaResourceListener;
import roboguice.java.util.logging.JavaBaseConfig;
import roboguice.java.util.logging.JavaWriter;

import com.google.inject.matcher.Matchers;

public class JavaDefaultRoboModule extends DefaultRoboModule<JavaResourceListener> {

    private  JavaContextScope contextScope;
    public JavaDefaultRoboModule( JavaContextScope contextScope, JavaResourceListener listener) {
        super(listener);
        this.contextScope = contextScope;
    }

    @Override
    protected void configure() {
        
     // ContextSingleton bindings
        bindScope(ContextSingleton.class, contextScope);
        bind(RoboScope.class).toInstance(contextScope);
        
        bindListener(Matchers.any(), resourceListener);
        
        bind(BaseConfig.class).to(JavaBaseConfig.class);
        bind(Writer.class).to(JavaWriter.class);
        requestStaticInjection(Ln.class);
    }

}

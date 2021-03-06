package roboguice.android.event;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import roboguice.android.DroidGuice;
import roboguice.android.test.RobolectricRoboTestRunner;
import roboguice.base.RoboGuice;
import roboguice.base.event.EventManager;

import com.google.inject.Inject;
import com.google.inject.Injector;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricRoboTestRunner.class)
public class ObservesTypeListenerTest {

    protected EventManager eventManager;
    protected Application app;
    protected Injector injector;
    protected List<Method> eventOneMethods;
    protected List<Method> eventTwoMethods;
    protected Context context = new Activity();

    @Before
    public void setup() throws NoSuchMethodException {
        app = Robolectric.application;
        injector = RoboGuice.<DroidGuice>instance().getInjector(app);

        eventManager = injector.getInstance(EventManager.class);

        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);
    }

    @Test
    public void simulateInjection() {
        final InjectedTestClass testClass = new InjectedTestClass();
        injector.injectMembers(testClass);

        eventManager.fire(new EventOne());

        testClass.tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
        testClass.tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test(expected = RuntimeException.class)
    public void invalidObservesMethodSignature(){
        injector.getInstance(MalformedObserves.class);
    }

    static public class InjectedTestClass{
        @Inject public ContextObserverTesterImpl tester;
    }

    public class MalformedObserves{
        public void malformedObserves(int val, @Observes EventOne event){}
    }
}

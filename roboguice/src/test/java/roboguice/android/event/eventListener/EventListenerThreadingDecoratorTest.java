package roboguice.android.event.eventListener;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createMock;

import org.junit.Before;
import org.junit.Test;

import roboguice.android.event.EventThread;
import roboguice.android.event.eventListener.factory.EventListenerThreadingDecorator;
import roboguice.base.event.EventListener;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import android.os.Handler;

/**
 * Tests for the EventListenerThreadingDecorator class
 *
 * @author John Ericksen
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EventListenerThreadingDecoratorTest {

    protected EventListenerThreadingDecorator eventListenerDecorator;
    protected EventListener<Void> eventListener;

    @Before
    public void setup(){

        //noinspection unchecked
        eventListener = createMock(EventListener.class);


        final Module testFactoryModule = new AbstractModule() {
            public void configure() {
                bind(Handler.class).toInstance(createMock(Handler.class));
            }
        };

        final Injector injector = Guice.createInjector(testFactoryModule);

        eventListenerDecorator = injector.getInstance(EventListenerThreadingDecorator.class);
    }

    @Test
    public void buildCurrentThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.CURRENT, eventListener);
        assertEquals(eventListener, outputListener);
    }

    @Test
    public void buildUIThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.UI, eventListener);
        assertEquals( eventListener, ((UIThreadEventListenerDecorator)outputListener).eventListener);
    }

    @Test
    public void buildAsyncThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.BACKGROUND, eventListener);
        assertEquals( eventListener, ((AsynchronousEventListenerDecorator)outputListener).eventListener);
    }
}

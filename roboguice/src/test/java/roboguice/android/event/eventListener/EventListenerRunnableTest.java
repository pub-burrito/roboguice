package roboguice.android.event.eventListener;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import roboguice.android.event.EventOne;
import roboguice.base.event.EventListener;

/**
 * Tests for the EventListenerRunnable class
 *
 * @author John Ericksen
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EventListenerRunnableTest {

    protected EventOne event;
    protected EventListener<EventOne> eventListener;

    protected EventListenerRunnable eventListenerRunnable;

    @Before
    public void setup(){
        //noinspection unchecked
        eventListener = createMock(EventListener.class);
        event = new EventOne();

        eventListenerRunnable = new EventListenerRunnable<EventOne>(event, eventListener);
    }

    @Test
    public void runTest(){
        reset(eventListener);

        eventListener.onEvent(event);

        replay(eventListener);

        eventListenerRunnable.run();

        verify(eventListener);
    }
}

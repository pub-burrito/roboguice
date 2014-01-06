package roboguice.android.event.eventListener;

import org.junit.Before;
import org.junit.Test;

import roboguice.android.event.eventListener.EventListenerRunnable;
import roboguice.android.event.eventListener.RunnableAsyncTaskAdaptor;

import static org.easymock.EasyMock.*;

/**
 * Tests for the RunnableAsyncTaskAdaptor class
 *
 * @author John Ericksen
 */
public class RunnableAsyncTaskAdaptorTest {

    protected EventListenerRunnable runnable;

    protected RunnableAsyncTaskAdaptor runnableAdaptor;

    @Before
    public void setup(){
        runnable = createMock(EventListenerRunnable.class);

        runnableAdaptor = new RunnableAsyncTaskAdaptor(runnable);
    }

    @Test
    public void test() throws Exception {
        reset(runnable);

        runnable.run();

        replay(runnable);

        runnableAdaptor.call();

        verify(runnable);
    }

}

package roboguice.android.event;

import roboguice.android.event.Observes;

/**
 * ContextSingleton Observer testing interface
 *
 * @author John Ericksen
 */
public interface ContextObserverTester {

    void observesImplementedEvent(@Observes EventOne event);

    void observesImplementedEvent(@Observes EventTwo event);
}

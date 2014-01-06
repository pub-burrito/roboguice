package roboguice.android.event.eventListener.factory;

import roboguice.android.event.EventListener;
import roboguice.android.event.EventThread;
import roboguice.android.event.eventListener.AsynchronousEventListenerDecorator;
import roboguice.android.event.eventListener.UIThreadEventListenerDecorator;

import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author John Ericksen
 */
public class EventListenerThreadingDecorator {

    @Inject protected Provider<Handler> handlerProvider;

    public <T> EventListener<T> decorate(EventThread threadType, EventListener<T> eventListener){
        switch (threadType){
            case UI:
                return new UIThreadEventListenerDecorator<T>(eventListener, handlerProvider.get() );
            case BACKGROUND:
                return new AsynchronousEventListenerDecorator<T>(eventListener);
            default:
                return eventListener;
        }
    }
}

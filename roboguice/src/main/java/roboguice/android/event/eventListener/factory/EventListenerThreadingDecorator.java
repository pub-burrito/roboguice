package roboguice.android.event.eventListener.factory;

import roboguice.android.event.EventThread;
import roboguice.android.event.eventListener.AsynchronousEventListenerDecorator;
import roboguice.android.event.eventListener.UIThreadEventListenerDecorator;
import roboguice.base.event.EventListener;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.os.Handler;

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

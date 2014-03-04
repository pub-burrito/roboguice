package roboguice.android.inject;

import roboguice.android.activity.event.OnCreateEvent;
import roboguice.android.event.Observes;
import roboguice.base.inject.ContextSingleton;

import com.google.inject.Inject;

import android.app.Activity;
import android.content.Context;

@SuppressWarnings("UnusedParameters")
@ContextSingleton
public class ContentViewListener {
    @Inject protected Activity activity;

    public void optionallySetContentView( @Observes OnCreateEvent ignored ) {
        Class<?> c = activity.getClass();
        while( c != Context.class ) {
            final ContentView annotation = c.getAnnotation(ContentView.class);
            if( annotation!=null ) {
                activity.setContentView(annotation.value());
                return;
            }
            c = c.getSuperclass();
        }
    }
}

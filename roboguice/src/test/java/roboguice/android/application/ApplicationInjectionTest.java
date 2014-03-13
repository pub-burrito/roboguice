package roboguice.android.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import roboguice.android.DroidGuice;
import roboguice.android.test.RobolectricRoboTestRunner;
import roboguice.base.RoboGuice;

import com.google.inject.Inject;

import android.app.Application;
import android.content.Context;

@RunWith(RobolectricRoboTestRunner.class)
public class ApplicationInjectionTest {

    @Test
    public void shouldBeAbleToInjectIntoApplication() {
        Robolectric.application = new AppA();
        Robolectric.application.onCreate();

        final AppA a = (AppA)Robolectric.application;
        assertNotNull(a.random);
    }


    @Test
    public void shouldBeAbleToInjectContextScopedItemsIntoApplication() {
        Robolectric.application = new AppB();
        Robolectric.application.onCreate();

        final AppB a = (AppB)Robolectric.application;
        assertThat( a.context, equalTo((Context)a) );
    }




    public static class AppA extends Application {
        @Inject Random random;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.<DroidGuice>instance().getInjector(this).injectMembers(this);
        }
    }

    public static class AppB extends Application {
        @Inject Context context;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.<DroidGuice>instance().getInjector(this).injectMembers(this);
        }
    }


}

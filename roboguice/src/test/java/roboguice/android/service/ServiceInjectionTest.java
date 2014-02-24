package roboguice.android.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import roboguice.android.inject.InjectView;
import roboguice.android.test.RobolectricRoboTestRunner;

import com.google.inject.ConfigurationException;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

@RunWith(RobolectricRoboTestRunner.class)
public class ServiceInjectionTest {

    @Test
    public void shouldBeAbleToInjectInRoboService() {
        final RoboServiceA roboService = new RoboServiceA();
        roboService.onCreate();

        assertThat( roboService.context, equalTo((Context)roboService) );
    }

    @Test
    public void shouldBeAbleToInjectInRoboIntentService() {
        final RoboIntentServiceA roboService = new RoboIntentServiceA("");
        roboService.onCreate();

        assertThat( roboService.context, equalTo((Context)roboService) );
    }

    @Test(expected=ConfigurationException.class)
    public void shouldNotAllowViewsInServices() {
        final RoboServiceB roboService = new RoboServiceB();
        roboService.onCreate();
    }

    static public class RoboServiceA extends RoboService {
        @Inject Context context;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public String configurationLocation() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    static public class RoboIntentServiceA extends RoboIntentService {
        @Inject Context context;

        public RoboIntentServiceA(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
        }

        @Override
        public String configurationLocation() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    static public class RoboServiceB extends RoboService {
        @InjectView(100) View v;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public String configurationLocation() {
            // TODO Auto-generated method stub
            return null;
        }
    }

}

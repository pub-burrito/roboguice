package roboguice.android.inject;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import roboguice.android.activity.RoboActivity;
import roboguice.android.service.RoboService;
import roboguice.android.test.RobolectricRoboTestRunner;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

@RunWith(RobolectricRoboTestRunner.class)
public class ExtrasListenerTest {

    @Test
    public void shouldInjectActivity() {
        final MyRoboActivity a1 = new MyRoboActivity();

        a1.onCreate(null);

        assertThat(a1.foo, equalTo(10));
    }

    @Test
    public void shouldInjectService() {
        final MyRoboService s1 = new MyRoboService();
        try {
            s1.onCreate();
            assertTrue(false);
        } catch( Exception e ) {
            // great
        }

    }

    protected static class MyRoboActivity extends RoboActivity {
        @InjectExtra("foo") protected int foo;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public Intent getIntent() {
            return new Intent(this,RoboActivity.class).putExtra("foo", 10);
        }
    }

    protected static class MyRoboService extends RoboService {
        @InjectExtra("foo") protected int foo;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }
}

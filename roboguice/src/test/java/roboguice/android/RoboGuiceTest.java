package roboguice.android;

import com.xtremelabs.robolectric.Robolectric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import roboguice.android.DroidGuice;
import roboguice.android.activity.RoboActivity;
import roboguice.android.test.RobolectricRoboTestRunner;

import android.app.Activity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricRoboTestRunner.class)
public class RoboGuiceTest {
    
    @Before
    public void setup() {
        DroidGuice.injectors.clear();
    }
    
    @Test
    public void destroyInjectorShouldRemoveContext() {
        final Activity activity = new RoboActivity();
        DroidGuice.getInjector(activity);
        
        assertThat(DroidGuice.injectors.size(), equalTo(1));
        
        DroidGuice.destroyInjector(activity);
        assertThat(DroidGuice.injectors.size(), equalTo(1));

        DroidGuice.destroyInjector(Robolectric.application);
        assertThat(DroidGuice.injectors.size(), equalTo(0));
    }

    @Test
    public void resetShouldRemoveContext() {
        final Activity activity = new RoboActivity();
        DroidGuice.getInjector(activity);
        
        assertThat(DroidGuice.injectors.size(), equalTo(1));
        
        DroidGuice.util.reset();
        assertThat(DroidGuice.injectors.size(), equalTo(0));
    }
}

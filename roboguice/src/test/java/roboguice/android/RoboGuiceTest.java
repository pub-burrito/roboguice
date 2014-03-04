package roboguice.android;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import roboguice.android.activity.RoboActivity;
import roboguice.android.test.RobolectricRoboTestRunner;
import roboguice.base.RoboGuice;
import roboguice.base.RoboGuice.RoboGuiceType;

import com.google.inject.AbstractModule;
import com.google.inject.Stage;

import android.app.Activity;

@RunWith(RobolectricRoboTestRunner.class)
@SuppressWarnings("unchecked")
public class RoboGuiceTest {
    
    @Before
    public void setup() {
        RoboGuice.type = RoboGuiceType.ANDROID; 
        DroidGuice.instance().injectors().clear();
    }
    
    @Test
    public void destroyInjectorShouldRemoveContext() {
        final Activity activity = new RoboActivity();
        DroidGuice.instance().getInjector(activity);
        
        assertThat(DroidGuice.instance().injectors().size(), equalTo(1));
        
        DroidGuice.instance().destroyInjector(activity);
        assertThat(DroidGuice.instance().injectors().size(), equalTo(1));

        DroidGuice.instance().destroyInjector(Robolectric.application);
        assertThat(DroidGuice.instance().injectors().size(), equalTo(0));
    }

    @Test
    public void resetShouldRemoveContext() {
        final Activity activity = new RoboActivity();
        DroidGuice.instance().getInjector(activity);
        
        assertThat(DroidGuice.instance().injectors().size(), equalTo(1));
        
        DroidGuice.util.reset();
        assertThat(DroidGuice.instance().injectors().size(), equalTo(0));
    }
    
    // https://github.com/roboguice/roboguice/issues/87
    @Test
    public void shouldOnlyCallConfigureOnce() {
        final int[] i = {0};
        
        RoboGuice.instance()
            .setScopedInjector(
                   Robolectric.application, 
                   Stage.DEVELOPMENT, 
                   RoboGuice.instance().newDefaultRoboModule(Robolectric.application), 
                   //module1
                   new AbstractModule() {
                        @Override
                        protected void configure() {
                            ++i[0];
                        }
                   },
                   //module2
                   new AbstractModule() {
                       @Override
                       protected void configure() {
                           ++i[0];
                       }
                  }
             );
        
        assertThat( i[0], equalTo(1) );
    }

}

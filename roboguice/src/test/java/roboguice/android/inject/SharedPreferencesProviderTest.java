package roboguice.android.inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.tester.android.content.TestSharedPreferences;

import roboguice.android.DroidGuice;
import roboguice.android.activity.RoboActivity;
import roboguice.android.test.RobolectricRoboTestRunner;
import roboguice.base.RoboGuice;
import roboguice.base.util.Strings;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

@RunWith(RobolectricRoboTestRunner.class)
public class SharedPreferencesProviderTest {

    @Test
    public void shouldInjectDefaultSharedPrefs() throws Exception {
        final A a = new A();
        a.onCreate(null);

        final Field f = TestSharedPreferences.class.getDeclaredField("filename");
        f.setAccessible(true);
        
        assertTrue(Strings.notEmpty(f.get(a.prefs)));
        assertThat(f.get(a.prefs), equalTo(f.get(PreferenceManager.getDefaultSharedPreferences(a))));
    }

    @Test
    public void shouldInjectNamedSharedPrefs() throws Exception {
        RoboGuice.<DroidGuice>instance().setScopedInjector(Robolectric.application,DroidGuice.DEFAULT_STAGE, RoboGuice.<DroidGuice>instance().newDefaultRoboModule(Robolectric.application), new ModuleA() );
        try {
            
            final A a = new A();
            a.onCreate(null);
    
            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);
            
            assertEquals("FOOBAR",f.get(a.prefs));
            
            
        } finally {
            DroidGuice.util.reset();
        }
    }
    
    @Test
    public void shouldFallbackOnOldDefaultIfPresent() throws Exception {
        final File oldDefault = new File("shared_prefs/default.xml");
        final File oldDir = new File("shared_prefs");

        oldDir.mkdirs();
        oldDefault.createNewFile();
        try {
            final A a = new A();
            a.onCreate(null);

            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);

            assertTrue(Strings.notEmpty(f.get(a.prefs)));
            assertEquals("default.xml", f.get(a.prefs) );

        } finally {
            oldDefault.delete();
            oldDir.delete();
        }
    }

    @Test
    public void shouldNotFallbackOnOldDefaultIfNamedFileSpecified() throws Exception {
        RoboGuice.<DroidGuice>instance().setScopedInjector(Robolectric.application,DroidGuice.DEFAULT_STAGE, RoboGuice.<DroidGuice>instance().newDefaultRoboModule(Robolectric.application), new ModuleA() );

        final File oldDefault = new File("shared_prefs/default.xml");
        final File oldDir = new File("shared_prefs");

        oldDir.mkdirs();
        oldDefault.createNewFile();
        try {
            final A a = new A();
            a.onCreate(null);

            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);

            assertTrue(Strings.notEmpty(f.get(a.prefs)));
            assertEquals("FOOBAR", f.get(a.prefs) );

        } finally {
            DroidGuice.util.reset();
            oldDefault.delete();
            oldDir.delete();
        }
    }
    

    public static class A extends RoboActivity {
        @Inject SharedPreferences prefs;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class ModuleA extends AbstractModule {
        @Override
        protected void configure() {
            bindConstant().annotatedWith(SharedPreferencesName.class).to("FOOBAR");
        }
    }
}


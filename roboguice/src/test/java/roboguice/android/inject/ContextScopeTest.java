package roboguice.android.inject;

import com.xtremelabs.robolectric.Robolectric;
import org.junit.Test;
import org.junit.runner.RunWith;

import roboguice.android.DroidGuice;
import roboguice.android.activity.RoboActivity;
import roboguice.android.inject.ContextScope;
import roboguice.android.test.RobolectricRoboTestRunner;
import roboguice.base.inject.ContextSingleton;

import android.app.Activity;
import android.os.Bundle;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Singleton;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricRoboTestRunner.class)
public class ContextScopeTest {


    @Test
    public void shouldHaveContextInScopeMapAfterOnCreate() throws Exception {
        final A a = new A();

        assertThat(a.getScopedObjectMap().size(), equalTo(0));
        a.onCreate(null);

        boolean found=false;
        for( Object o : a.getScopedObjectMap().values() )
            if( o==a )
                found = true;

        assertTrue("Couldn't find context in scope map", found);
    }

    @Test
    public void shouldBeAbleToOpenMultipleScopes() {
        final ContextScope scope = DroidGuice.instance().getScopedInjector(Robolectric.application).getInstance(ContextScope.class);
        final Activity a = new A();
        final Activity b = new B();

        scope.enter(a);
        scope.enter(b);
        scope.exit(b);
        scope.exit(a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToExitTheWrongScope() {
        final ContextScope scope = DroidGuice.instance().getScopedInjector(Robolectric.application).getInstance(ContextScope.class);
        final Activity a = new A();
        final Activity b = new B();

        scope.enter(a);
        scope.enter(b);
        scope.exit(a);
    }

    @Test
    public void shouldHaveTwoItemsInScopeMapAfterOnCreate() throws Exception {
        final B b = new B();

        assertThat(b.getScopedObjectMap().size(), equalTo(0));
        b.onCreate(null);

        boolean found=false;
        for( Object o : b.getScopedObjectMap().values() )
            if( o==b )
                found = true;

        assertTrue("Couldn't find context in scope map", found);
        assertTrue(b.getScopedObjectMap().containsKey(Key.get(C.class)));
    }

    public static class A extends RoboActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class B extends RoboActivity {
        @Inject C c; // context scoped
        @Inject D d; // unscoped
        @Inject E e; // singleton

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    @ContextSingleton
    public static class C {}

    public static class D {}

    @Singleton
    public static class E {}


}

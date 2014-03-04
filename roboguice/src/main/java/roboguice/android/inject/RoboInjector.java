package roboguice.android.inject;

import com.google.inject.Injector;

import android.app.Activity;
import android.support.v4.app.Fragment;

public interface RoboInjector extends Injector {
    void injectViewMembers(Activity activity);
    void injectViewMembers(Fragment fragment);
    void injectMembersWithoutViews(Object instance);
}

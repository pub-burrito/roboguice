package roboguice.android.fragment;

import roboguice.android.DroidGuice;
import roboguice.base.RoboGuice;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

public abstract class RoboListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.<DroidGuice>instance().getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.<DroidGuice>instance().getInjector(getActivity()).injectViewMembers(this);
    }
}

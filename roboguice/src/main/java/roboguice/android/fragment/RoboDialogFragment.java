package roboguice.android.fragment;

import roboguice.android.DroidGuice;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

public abstract class RoboDialogFragment extends DialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DroidGuice.instance().getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DroidGuice.instance().getInjector(getActivity()).injectViewMembers(this);
    }
}

package roboguice.android.inject;

import roboguice.base.inject.ContextSingleton;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.accounts.AccountManager;
import android.content.Context;

@ContextSingleton
public class AccountManagerProvider implements Provider<AccountManager> {
    @Inject protected Context context;

    public AccountManager get() {
        return AccountManager.get(context);
    }
}

package roboguice.android.test;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

public class RobolectricRoboTestRunner extends RobolectricTestRunner {

    public RobolectricRoboTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

}

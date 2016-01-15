package dream;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dream.common.utils.AtomicDependencyDetectorTest;
import dream.common.utils.CompleteGlitchFreeDependencyDetectorTest;
import dream.common.utils.DependencyGraphUtilsTest;
import dream.common.utils.FinalExpressionsDetectorTest;
import dream.common.utils.IntraSourceDependencyDetectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ //
    IntraSourceDependencyDetectorTest.class, //
    DependencyGraphUtilsTest.class, //
    CompleteGlitchFreeDependencyDetectorTest.class, //
    AtomicDependencyDetectorTest.class, //
    FinalExpressionsDetectorTest.class, //
    LocalTest.class })

public class RegressionTests {

}
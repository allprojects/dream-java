package javareact;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import javareact.common.utils.AtomicDependencyDetectorTest;
import javareact.common.utils.CompleteGlitchFreeDependencyDetectorTest;
import javareact.common.utils.DependencyGraphUtilsTest;
import javareact.common.utils.IntraSourceDependencyDetectorTest;
import javareact.token_service.FinalExpressionsDetectorTest;

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
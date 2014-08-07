package javareact;

import javareact.server.DependencyDetectorTest;
import javareact.token_service.FinalExpressionsDetectorTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ LocalTest.class, //
    DependencyDetectorTest.class, //
    FinalExpressionsDetectorTest.class //
})
public class RegressionTests {

}
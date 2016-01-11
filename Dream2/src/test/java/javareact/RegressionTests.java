package javareact;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import javareact.client.DependencyDetectorTest;
import javareact.token_service.FinalExpressionsDetectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DependencyDetectorTest.class, //
    FinalExpressionsDetectorTest.class, //
    LocalTest.class })

public class RegressionTests {

}
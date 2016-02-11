package dream;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import dream.common.utils.AtomicDependencyDetectorTest;
import dream.common.utils.CompleteGlitchFreeDependencyDetectorTest;
import dream.common.utils.DependencyGraphUtilsTest;
import dream.common.utils.FinalNodesDetectorTest;
import dream.common.utils.IntraSourceDependencyDetectorTest;
import dream.locking.LockManagerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ //
		IntraSourceDependencyDetectorTest.class, //
		DependencyGraphUtilsTest.class, //
		CompleteGlitchFreeDependencyDetectorTest.class, //
		AtomicDependencyDetectorTest.class, //
		FinalNodesDetectorTest.class, //
		LockManagerTest.class, //
		LocalTest.class })

public class RegressionTests {

}
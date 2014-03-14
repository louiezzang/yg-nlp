package com.yglab.nlp.parser.dep;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite.
 * 
 * @author Younggue Bae
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	DependencyParserTest.class, 
	DependencyParserEvaluatorTest.class 
})
public class DependencyParserTestSuite {
	
}

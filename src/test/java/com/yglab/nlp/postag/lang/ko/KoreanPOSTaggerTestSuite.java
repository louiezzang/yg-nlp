package com.yglab.nlp.postag.lang.ko;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.yglab.nlp.postag.morph.PlainDictionaryTest;

/**
 * Test suite.
 * 
 * @author Younggue Bae
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	KoreanLemmatizerTest.class,
	KoreanPOSTaggerTest.class
})
public class KoreanPOSTaggerTestSuite {
	
}

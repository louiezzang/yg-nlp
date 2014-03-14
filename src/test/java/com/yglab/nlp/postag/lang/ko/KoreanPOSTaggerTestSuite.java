package com.yglab.nlp.postag.lang.ko;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite.
 * 
 * @author Younggue Bae
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	MorphemeDicGeneratorTest.class, 
	MorphemeDictionaryTest.class,
	KoreanStemmerTest.class,
	KoreanPOSTaggerTest.class
})
public class KoreanPOSTaggerTestSuite {
	
}

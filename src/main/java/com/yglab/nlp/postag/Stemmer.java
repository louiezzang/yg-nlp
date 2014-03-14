package com.yglab.nlp.postag;

/**
 * The stemmer extracts its stem from the surface word.
 * 
 * @author Younggue Bae
 */
public interface Stemmer {
  
  public CharSequence stem(CharSequence word, String rule);
}

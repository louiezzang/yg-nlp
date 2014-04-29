package com.yglab.nlp.postag.morph;

/**
 * The stemmer extracts the stem word from the surface word.
 * 
 * @author Younggue Bae
 */
public interface Stemmer {
  
  public CharSequence stem(CharSequence word);
}

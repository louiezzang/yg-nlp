package com.yglab.nlp.postag.morph;

/**
 * The lemmatizer extracts the lemma from the token object.
 * 
 * @author Younggue Bae
 */
public interface Lemmatizer<V> {
  
  public V lemmatize(V value);
}

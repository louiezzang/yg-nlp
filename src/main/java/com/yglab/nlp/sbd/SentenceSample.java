package com.yglab.nlp.sbd;

import java.util.Arrays;
import java.util.List;

import com.yglab.nlp.model.Span;


/**
 * Sample instance for sentence detector.
 * 
 * @author Younggue Bae
 */
public class SentenceSample {

	private final String[] document;
	private final List<Span> sentences;

	/**
	 * Creates a sentence sample.
	 * 
	 * @param document The tokens of a document
	 * @param sentences The end positions of each sentence
	 */
	public SentenceSample(String[] document, Span... sentences) {
		if (document == null) {
			throw new IllegalArgumentException("document must not be null!");
		}
		this.document = document;
		this.sentences = Arrays.asList(sentences);
	}

  /**
   * Retrieves the tokens of a document.
   *
   * @return the tokens of a document
   */
	public String[] getDocument() {
		return this.document;
	}
	
  /**
   * Retrieves the sentences.
   *
   * @return the end positions of the sentences in the document.
   */
  public Span[] getSentences() {
    return sentences.toArray(new Span[sentences.size()]);
  }

}

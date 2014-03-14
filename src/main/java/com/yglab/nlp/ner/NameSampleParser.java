package com.yglab.nlp.ner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yglab.nlp.io.SampleParser;
import com.yglab.nlp.tokenizer.WhitespaceTokenizer;
import com.yglab.nlp.util.InvalidFormatException;
import com.yglab.nlp.util.Span;

/**
 * The parser for NER sample.
 * 
 * @author Younggue Bae
 */
public class NameSampleParser implements SampleParser<NameSample> {

	protected static final Pattern START_TAG_PATTERN = Pattern.compile("<START(:([^:>\\s]*))?>");
	protected static final String END_TAG = "<END>";

	protected WhitespaceTokenizer tokenizer;

	public NameSampleParser() {
		tokenizer = new WhitespaceTokenizer();
	}

	@Override
	public NameSample parse(String sentence) throws InvalidFormatException {

		String[] parts = tokenizer.tokenize(sentence);

		List<String> tokens = new ArrayList<String>(parts.length);
		List<Span> labels = new ArrayList<Span>();

		String nameType = "defaultType";
		int startIndex = -1;
		int wordIndex = 0;

		boolean catchingName = false;

		for (int pi = 0; pi < parts.length; pi++) {
			Matcher startMatcher = START_TAG_PATTERN.matcher(parts[pi]);
			if (startMatcher.matches()) {
				if (catchingName) {
					throw new InvalidFormatException("Found unexpected annotation" + " while handling a label sequence: "
							+ errorTokenWithContext(parts, pi));
				}
				catchingName = true;
				startIndex = wordIndex;
				String nameTypeFromSample = startMatcher.group(2);
				if (nameTypeFromSample != null) {
					if (nameTypeFromSample.length() == 0) {
						throw new InvalidFormatException("Missing a name type: " + errorTokenWithContext(parts, pi));
					}
					nameType = nameTypeFromSample;
				}

			} else if (parts[pi].equals(END_TAG)) {
				if (catchingName == false) {
					throw new InvalidFormatException("Found unexpected annotation: " + errorTokenWithContext(parts, pi));
				}
				catchingName = false;
				labels.add(new Span(startIndex, wordIndex, nameType));

			} else {
				tokens.add(parts[pi]);
				wordIndex++;
			}
		}
		String[] arrSentence = tokens.toArray(new String[tokens.size()]);
		Span[] arrLabels = labels.toArray(new Span[labels.size()]);

		return new NameSample(arrSentence, arrLabels);
	}
	
  protected static String errorTokenWithContext(String sentence[], int index) {
    
    StringBuilder errorString = new StringBuilder();
    
    // two token before
    if (index > 1)
      errorString.append(sentence[index -2]).append(" ");
    
    if (index > 0)
      errorString.append(sentence[index -1]).append(" ");
    
    // token itself
    errorString.append("###");
    errorString.append(sentence[index]);
    errorString.append("###").append(" ");
    
    // two token after
    if (index + 1 < sentence.length)
      errorString.append(sentence[index + 1]).append(" ");

    if (index + 2 < sentence.length)
      errorString.append(sentence[index + 2]);
    
    return errorString.toString();
  }
  
}

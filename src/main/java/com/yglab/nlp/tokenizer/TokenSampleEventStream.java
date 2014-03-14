package com.yglab.nlp.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;
import com.yglab.nlp.util.Span;

/**
 * This class handles token sample events stream.
 * 
 * @author Younggue Bae
 */
public class TokenSampleEventStream implements EventStream<TokenSample, Datum> {

	private TokenFeatureGenerator featureGenerator;
	private List<TokenSample> samples;
	private WhitespaceTokenizer whitespaceTokenizer;
	private final Pattern skipPattern;
	private boolean useSkipPattern;
	
	public TokenSampleEventStream(TokenFeatureGenerator featureGenerator, List<TokenSample> samples) {
		this(featureGenerator, samples, null);
	}
	
	public TokenSampleEventStream(TokenFeatureGenerator featureGenerator, List<TokenSample> samples, Pattern skipPattern) {
		this.featureGenerator = featureGenerator;
		this.samples = samples;
		this.whitespaceTokenizer = new WhitespaceTokenizer();
		this.skipPattern = skipPattern;
		
		if (skipPattern != null) {
			useSkipPattern = true;
		}
	}
	
	@Override
	public List<TokenSample> getInputStream() {
		return this.samples;
	}
	
	@Override
	public List<Datum> getOutputStream() {
		List<Datum> trainData = new ArrayList<Datum>();

		for (TokenSample sample : samples) {
			Span tokens[] = sample.getTokenSpans();
	    String text = sample.getText();

	    if (tokens.length > 0) {

	      int start = tokens[0].getStart();
	      int end = tokens[tokens.length - 1].getEnd();

	      String sentence = text.substring(start, end);

	      Span[] whitespaceTokens = whitespaceTokenizer.tokenizePos(sentence);
	      
	      int firstTrainingToken = -1;
	      int lastTrainingToken = -1;
	      for (Span whitespaceToken : whitespaceTokens) {
	        Span whitespaceSpan = whitespaceToken;
	        String currentToken = sentence.substring(whitespaceSpan.getStart(), whitespaceSpan.getEnd());
	        //adjust whitespaceSpan to text offsets
	        whitespaceSpan = new Span(whitespaceSpan.getStart() + start, whitespaceSpan.getEnd() + start);
	        
	        if (currentToken.length() > 1 && (!useSkipPattern || !skipPattern.matcher(currentToken).matches())) {
	          //find offsets of annotated tokens inside of candidate tokens
	          boolean foundTrainingTokens = false;
	          for (int ti = lastTrainingToken + 1; ti < tokens.length; ti++) {
	            if (whitespaceSpan.contains(tokens[ti])) {
	              if (!foundTrainingTokens) {
	                firstTrainingToken = ti;
	                foundTrainingTokens = true;
	              }
	              lastTrainingToken = ti;
	            }
	            else if (whitespaceSpan.getEnd() < tokens[ti].getEnd()) {
	              break;
	            }
	            else if (tokens[ti].getEnd() < whitespaceSpan.getStart()) {
	              //keep looking
	            }
	            else {
                System.err.println("Bad training token: " + tokens[ti] + " whitespaceSpan: " + whitespaceSpan +
                    " token="+text.substring(tokens[ti].getStart(), tokens[ti].getEnd()));
	            }
	          }
	          
	          // create training data
	          if (foundTrainingTokens) {
	            for (int ti = firstTrainingToken; ti <= lastTrainingToken; ti++) {
	              Span trainSpan = tokens[ti];
	              int whitespaceStart = whitespaceSpan.getStart();
	              for (int i = trainSpan.getStart() + 1; i < trainSpan.getEnd(); i++) {
	              	String[] features = featureGenerator.getFeatures(i - whitespaceStart, currentToken);
	              	Datum datum = new Datum(currentToken, MaxentTokenizer.NO_SPLIT);
	              	datum.setFeatures(Arrays.asList(features));
	              	
	              	trainData.add(datum);
	              }

	              if (trainSpan.getEnd() != whitespaceSpan.getEnd()) {
	              	String[] features = featureGenerator.getFeatures(trainSpan.getEnd() - whitespaceStart, currentToken);
	                Datum datum = new Datum(currentToken, MaxentTokenizer.SPLIT);
	                datum.setFeatures(Arrays.asList(features));

	                trainData.add(datum);
	              }
	            }
	          }
	        }
	      }
	    }
		}
		
		return trainData;
	}

}

package com.yglab.nlp.tokenizer;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.io.SampleParser;
import com.yglab.nlp.util.InvalidFormatException;
import com.yglab.nlp.util.Span;

/**
 * The parser for tokenizer sample.
 * 
 * @author Younggue Bae
 */
public class TokenSampleParser implements SampleParser<TokenSample> {

	public static final String DEFAULT_SEPARATOR_CHARS = "<SPLIT>";
	private final String separatorChars = DEFAULT_SEPARATOR_CHARS;

	private WhitespaceTokenizer whitespaceTokenizer;

	public TokenSampleParser() {
		whitespaceTokenizer = new WhitespaceTokenizer();
	}

	private static void addToken(StringBuilder sample, List<Span> tokenSpans, String token,
			boolean isNextMerged) {
		int tokenSpanStart = sample.length();
		sample.append(token);
		int tokenSpanEnd = sample.length();

		tokenSpans.add(new Span(tokenSpanStart, tokenSpanEnd));

		if (!isNextMerged) {
			sample.append(" ");
		}
	}

	@Override
	public TokenSample parse(String text) throws InvalidFormatException {
		if (text == null) {
			throw new IllegalArgumentException("sampleString must not be null!");
		}

		Span[] whitespaceTokenSpans = whitespaceTokenizer.tokenizePos(text);

		// pre-allocate 20% for newly created tokens
		List<Span> realTokenSpans = new ArrayList<Span>((int) (whitespaceTokenSpans.length * 1.2d));

		StringBuilder untaggedSampleString = new StringBuilder();

		for (Span whiteSpaceTokenSpan : whitespaceTokenSpans) {
			String whitespaceToken = whiteSpaceTokenSpan.getCoveredText(text).toString();

			boolean foundSplitChar = false;

			int tokenStart = 0;
			int tokenEnd = -1;
			while ((tokenEnd = whitespaceToken.indexOf(separatorChars, tokenStart)) >= 0) {
				String token = whitespaceToken.substring(tokenStart, tokenEnd);
				addToken(untaggedSampleString, realTokenSpans, token, true);

				tokenStart = tokenEnd + separatorChars.length();
				foundSplitChar = true;
			}

			if (foundSplitChar) {
				// If the token contains the split chars at least once
				// a span for the last token must still be added
				String token = whitespaceToken.substring(tokenStart);

				addToken(untaggedSampleString, realTokenSpans, token, false);
			} else {
				// If it does not contain the split chars at lest once
				// just copy the original token span
				addToken(untaggedSampleString, realTokenSpans, whitespaceToken, false);
			}
		}
		
		return new TokenSample(untaggedSampleString.toString(), realTokenSpans.toArray(new Span[realTokenSpans.size()]));
	}

}

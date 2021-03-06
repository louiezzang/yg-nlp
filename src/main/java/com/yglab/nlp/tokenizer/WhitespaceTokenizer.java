/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yglab.nlp.tokenizer;

import java.util.ArrayList;
import java.util.List;

import com.yglab.nlp.model.Span;
import com.yglab.nlp.util.StringUtil;

/**
 * This tokenizer uses white spaces to tokenize the input text.
 * 
 */
public class WhitespaceTokenizer extends AbstractTokenizer {

	public WhitespaceTokenizer() {
	}

	@Override
	public Span[] tokenizePos(String d) {
		int tokStart = -1;
		List<Span> tokens = new ArrayList<Span>();
		boolean inTok = false;

		// gather up potential tokens
		int end = d.length();
		for (int i = 0; i < end; i++) {
			if (StringUtil.isWhitespace(d.charAt(i))) {
				if (inTok) {
					tokens.add(new Span(tokStart, i));
					inTok = false;
					tokStart = -1;
				}
			} else {
				if (!inTok) {
					tokStart = i;
					inTok = true;
				}
			}
		}

		if (inTok) {
			tokens.add(new Span(tokStart, end));
		}

		return tokens.toArray(new Span[tokens.size()]);
	}
}

//   Copyright 2012 Digipom Inc.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.digipom.android.library.evaluator;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import com.digipom.android.library.evaluator.exception.ParseException;
import com.digipom.android.library.evaluator.lexer.Identifier;
import com.digipom.android.library.evaluator.lexer.Lexer;
import com.digipom.android.library.evaluator.lexer.NumberLiteral;
import com.digipom.android.library.evaluator.lexer.Operator;
import com.digipom.android.library.evaluator.lexer.Parenthesis;
import com.digipom.android.library.evaluator.lexer.PredefinedFunction;
import com.digipom.android.library.evaluator.lexer.Separator;
import com.digipom.android.library.evaluator.lexer.Token;
import com.digipom.android.library.evaluator.lexer.Operator.Associativity;

public class ShuntingYardParser {
	private final Lexer lexer;
	private final List<Token> outputList = new ArrayList<Token>();
	private final Stack<Token> stack = new Stack<Token>();

	private Token nextToken;

	ShuntingYardParser(String input, NumberPrecision numberPrecision) throws ParseException {
		this.lexer = new Lexer(input, numberPrecision);
		consume();
	}

	List<Token> parse() throws ParseException {
		while (nextToken != Token.EOF) {
			if (nextToken instanceof NumberLiteral || nextToken instanceof Identifier) {
				outputList.add(nextToken);
			} else if (nextToken instanceof PredefinedFunction || nextToken == Parenthesis.OPEN) {
				stack.push(nextToken);
			} else if (nextToken == Separator.COMMA) {
				try {
					popToLeftParenthesisToOutput();
				} catch (EmptyStackException e) {
					throw new ParseException("Misplaced comma or mis-matched parenthesis.");
				}
			} else if (nextToken instanceof Operator) {
				final Operator o1 = (Operator) nextToken;

				while (!stack.isEmpty() && stack.peek() instanceof Operator) {
					final Operator o2 = (Operator) stack.peek();

					if ((o1.associativity == Associativity.LEFT && o1.precedence <= o2.precedence)
					 || (o1.associativity == Associativity.RIGHT && o1.precedence < o2.precedence)) {
						popStackTopToOutput();
					} else {
						break;
					}
				}

				stack.push(nextToken);
			} else if (nextToken == Parenthesis.CLOSE) {
				try {
					popToLeftParenthesisToOutput();
				} catch (EmptyStackException e) {
					throw new ParseException("Mis-matched parenthesis.");
				}

				// Pop the left parenthesis from the stack, but not to the
				// output queue.
				stack.pop();

				if (!stack.isEmpty() && stack.peek() instanceof PredefinedFunction) {
					popStackTopToOutput();
				}
			}

			consume();
		}

		while (!stack.isEmpty()) {
			if (stack.peek() instanceof Parenthesis) {
				throw new ParseException("Mis-matched parenthesis.");
			} else {
				popStackTopToOutput();
			}
		}

		return outputList;
	}

	private void popStackTopToOutput() {
		outputList.add(stack.pop());
	}

	private void popToLeftParenthesisToOutput() throws EmptyStackException {
		while (stack.peek() != Parenthesis.OPEN) {
			popStackTopToOutput();
		}

	}

	private void consume() throws ParseException {
		nextToken = lexer.nextToken();
	}
}

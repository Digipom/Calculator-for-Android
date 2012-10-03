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

import java.util.List;

import android.test.AndroidTestCase;

import com.digipom.android.library.evaluator.exception.ParseException;
import com.digipom.android.library.evaluator.lexer.Identifier;
import com.digipom.android.library.evaluator.lexer.NumberLiteral;
import com.digipom.android.library.evaluator.lexer.Operator;
import com.digipom.android.library.evaluator.lexer.Token;

public class TestShuntingYardParser extends AndroidTestCase {
	public void testSimpleExpression() throws ParseException {
		final ShuntingYardParser shuntingYardParser = new ShuntingYardParser("a + b", NumberPrecision.FLOAT);
		final List<Token> output = shuntingYardParser.parse();

		assertTrue(output.get(0) instanceof Identifier);
		assertTrue(output.get(1) instanceof Identifier);
		assertTrue(output.get(2) == Operator.ADD);
	}

	public void testOperatorPrecedence() throws ParseException {
		final ShuntingYardParser shuntingYardParser = new ShuntingYardParser("a + b * 5", NumberPrecision.FLOAT);
		final List<Token> output = shuntingYardParser.parse();

		assertTrue(output.get(0) instanceof Identifier);
		assertTrue(output.get(1) instanceof Identifier);
		assertTrue(output.get(2) instanceof NumberLiteral);
		assertTrue(output.get(3) == Operator.MULTIPLY);
		assertTrue(output.get(4) == Operator.ADD);
	}

	public void testNegation() throws ParseException {
		final ShuntingYardParser shuntingYardParser = new ShuntingYardParser("-a + b", NumberPrecision.FLOAT);
		final List<Token> output = shuntingYardParser.parse();

		assertTrue(output.get(0) instanceof Identifier);
		assertTrue(output.get(1) == Operator.NEGATION);
		assertTrue(output.get(2) instanceof Identifier);
		assertTrue(output.get(3) == Operator.ADD);
	}

	public void testBrackets() throws ParseException {

		final ShuntingYardParser shuntingYardParser = new ShuntingYardParser("(a+b)*(5-x)/(-y-2)",
				NumberPrecision.FLOAT);
		// a b + 5 x - * y .- 2 - /
		final List<Token> output = shuntingYardParser.parse();

		assertTrue(output.get(0) instanceof Identifier);
		assertTrue(output.get(1) instanceof Identifier);
		assertTrue(output.get(2) == Operator.ADD);
		assertTrue(output.get(3) instanceof NumberLiteral);
		assertTrue(output.get(4) instanceof Identifier);
		assertTrue(output.get(5) == Operator.SUBTRACT);
		assertTrue(output.get(6) == Operator.MULTIPLY);
		assertTrue(output.get(7) instanceof Identifier);
		assertTrue(output.get(8) == Operator.NEGATION);
		assertTrue(output.get(9) instanceof NumberLiteral);
		assertTrue(output.get(10) == Operator.SUBTRACT);
		assertTrue(output.get(11) == Operator.DIVIDE);
	}
}

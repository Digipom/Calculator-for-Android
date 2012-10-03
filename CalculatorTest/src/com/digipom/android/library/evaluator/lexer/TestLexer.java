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

package com.digipom.android.library.evaluator.lexer;

import junit.framework.TestCase;

import com.digipom.android.library.evaluator.NumberPrecision;
import com.digipom.android.library.evaluator.exception.ParseException;

public class TestLexer extends TestCase {
	public void testLexSimple() throws ParseException {
		final Lexer lexer = new Lexer("x + y", NumberPrecision.FLOAT);

		final Token x = lexer.nextToken();
		assertTrue(x instanceof Identifier);
		assertEquals(((Identifier) x).name, "x");

		final Token add = lexer.nextToken();
		assertTrue(add == Operator.ADD);

		final Token y = lexer.nextToken();
		assertTrue(y instanceof Identifier);
		assertEquals(((Identifier) y).name, "y");

		assertEquals(lexer.nextToken(), Token.EOF);
	}

	public void testLexMoreComplex() throws ParseException {
		final Lexer lexer = new Lexer("sin(y) + cos(x)", NumberPrecision.FLOAT);

		assertEquals(lexer.nextToken(), PredefinedFunction.SIN);
		assertEquals(lexer.nextToken(), Parenthesis.OPEN);

		final Token y = lexer.nextToken();
		assertTrue(y instanceof Identifier);
		assertEquals(((Identifier) y).name, "y");

		assertEquals(lexer.nextToken(), Parenthesis.CLOSE);
		assertEquals(lexer.nextToken(), Operator.ADD);
		assertEquals(lexer.nextToken(), PredefinedFunction.COS);
		assertEquals(lexer.nextToken(), Parenthesis.OPEN);

		final Token x = lexer.nextToken();
		assertTrue(x instanceof Identifier);
		assertEquals(((Identifier) x).name, "x");

		assertEquals(lexer.nextToken(), Parenthesis.CLOSE);
		assertEquals(lexer.nextToken(), Token.EOF);
	}

	public void testLexYetMoreComplex() throws ParseException {
		final Lexer lexer = new Lexer("pow(abs(cos(x) + cos(y)), 0.5)", NumberPrecision.FLOAT);

		assertEquals(lexer.nextToken(), PredefinedFunction.POW);
		assertEquals(lexer.nextToken(), Parenthesis.OPEN);
		assertEquals(lexer.nextToken(), PredefinedFunction.ABS);
		assertEquals(lexer.nextToken(), Parenthesis.OPEN);
		assertEquals(lexer.nextToken(), PredefinedFunction.COS);
		assertEquals(lexer.nextToken(), Parenthesis.OPEN);

		final Token x = lexer.nextToken();
		assertTrue(x instanceof Identifier);
		assertEquals(((Identifier) x).name, "x");

		assertEquals(lexer.nextToken(), Parenthesis.CLOSE);
		assertEquals(lexer.nextToken(), Operator.ADD);
		assertEquals(lexer.nextToken(), PredefinedFunction.COS);
		assertEquals(lexer.nextToken(), Parenthesis.OPEN);

		final Token y = lexer.nextToken();
		assertTrue(y instanceof Identifier);
		assertEquals(((Identifier) y).name, "y");

		assertEquals(lexer.nextToken(), Parenthesis.CLOSE);
		assertEquals(lexer.nextToken(), Parenthesis.CLOSE);
		assertEquals(lexer.nextToken(), Separator.COMMA);

		final Token literal = lexer.nextToken();
		assertTrue(literal instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) literal).value, 0.5, 0.001);

		assertEquals(lexer.nextToken(), Parenthesis.CLOSE);
		assertEquals(lexer.nextToken(), Token.EOF);
	}

	public void testLexWithNegation() throws ParseException {
		final Lexer lexer = new Lexer("-3 * 5", NumberPrecision.FLOAT);

		assertEquals(lexer.nextToken(), Operator.NEGATION);

		final Token three = lexer.nextToken();
		assertTrue(three instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) three).value, 3, 0.001);

		assertEquals(lexer.nextToken(), Operator.MULTIPLY);

		final Token five = lexer.nextToken();
		assertTrue(five instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) five).value, 5, 0.001);

		assertEquals(lexer.nextToken(), Token.EOF);
	}

	public void testLexWithNegation2() throws ParseException {
		final Lexer lexer = new Lexer("3 - -5", NumberPrecision.FLOAT);

		final Token three = lexer.nextToken();
		assertTrue(three instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) three).value, 3, 0.001);

		assertEquals(lexer.nextToken(), Operator.SUBTRACT);
		assertEquals(lexer.nextToken(), Operator.NEGATION);

		final Token five = lexer.nextToken();
		assertTrue(five instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) five).value, 5, 0.001);

		assertEquals(lexer.nextToken(), Token.EOF);
	}

	public void testLexWithNegation3() throws ParseException {
		final Lexer lexer = new Lexer("-sin(x) / -(-3 * -5)", NumberPrecision.FLOAT);

		assertEquals(lexer.nextToken(), Operator.NEGATION);
		assertEquals(lexer.nextToken(), PredefinedFunction.SIN);
		assertEquals(lexer.nextToken(), Parenthesis.OPEN);

		final Token x = lexer.nextToken();
		assertTrue(x instanceof Identifier);
		assertEquals(((Identifier) x).name, "x");

		assertEquals(lexer.nextToken(), Parenthesis.CLOSE);
		assertEquals(lexer.nextToken(), Operator.DIVIDE);
		assertEquals(lexer.nextToken(), Operator.NEGATION);
		assertEquals(lexer.nextToken(), Parenthesis.OPEN);
		assertEquals(lexer.nextToken(), Operator.NEGATION);

		final Token three = lexer.nextToken();
		assertTrue(three instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) three).value, 3, 0.001);

		assertEquals(lexer.nextToken(), Operator.MULTIPLY);
		assertEquals(lexer.nextToken(), Operator.NEGATION);

		final Token five = lexer.nextToken();
		assertTrue(five instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) five).value, 5, 0.001);

		assertEquals(lexer.nextToken(), Parenthesis.CLOSE);
		assertEquals(lexer.nextToken(), Token.EOF);
	}

	public void testLexWithBigNumberBigDecimal() throws ParseException {
		final Lexer lexer = new Lexer("123.456789E+12 * 123.456789E+12", NumberPrecision.BIG_DECIMAL);

		final Token firstNumber = lexer.nextToken();
		assertTrue(firstNumber instanceof NumberLiteral);
		assertEquals(((BigDecimalNumberLiteral) firstNumber).value.doubleValue(), 123456789000000.0, 0.001);

		assertEquals(lexer.nextToken(), Operator.MULTIPLY);

		final Token secondNumber = lexer.nextToken();
		assertTrue(secondNumber instanceof NumberLiteral);
		assertEquals(((BigDecimalNumberLiteral) secondNumber).value.doubleValue(), 123456789000000.0, 0.001);

		assertEquals(lexer.nextToken(), Token.EOF);
	}

	public void testLexWithBigNumberDouble() throws ParseException {
		final Lexer lexer = new Lexer("123.456789E+12 * 123.456789E+12", NumberPrecision.DOUBLE);

		final Token firstNumber = lexer.nextToken();
		assertTrue(firstNumber instanceof NumberLiteral);
		assertEquals(((DoubleNumberLiteral) firstNumber).value, 123456789000000.0, 0.001);

		assertEquals(lexer.nextToken(), Operator.MULTIPLY);

		final Token secondNumber = lexer.nextToken();
		assertTrue(secondNumber instanceof NumberLiteral);
		assertEquals(((DoubleNumberLiteral) secondNumber).value, 123456789000000.0, 0.001);

		assertEquals(lexer.nextToken(), Token.EOF);
	}

	public void testLexWithBigNumberFloat() throws ParseException {
		final Lexer lexer = new Lexer("123.456000E+12 * 123.456000E+12", NumberPrecision.FLOAT);

		final Token firstNumber = lexer.nextToken();
		assertTrue(firstNumber instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) firstNumber).value, 123456000000000f, 0.001);

		assertEquals(lexer.nextToken(), Operator.MULTIPLY);

		final Token secondNumber = lexer.nextToken();
		assertTrue(secondNumber instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) secondNumber).value, 123456000000000f, 0.001);

		assertEquals(lexer.nextToken(), Token.EOF);
	}
	
	public void testLexWithBigNumberFloatSpaceAroundE() throws ParseException {
		final Lexer lexer = new Lexer("123.456000E+12 * 123.456000E+12", NumberPrecision.FLOAT);

		final Token firstNumber = lexer.nextToken();
		assertTrue(firstNumber instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) firstNumber).value, 123456000000000f, 0.001);

		assertEquals(lexer.nextToken(), Operator.MULTIPLY);

		final Token secondNumber = lexer.nextToken();
		assertTrue(secondNumber instanceof NumberLiteral);
		assertEquals(((FloatNumberLiteral) secondNumber).value, 123456000000000f, 0.001);

		assertEquals(lexer.nextToken(), Token.EOF);
	}
}

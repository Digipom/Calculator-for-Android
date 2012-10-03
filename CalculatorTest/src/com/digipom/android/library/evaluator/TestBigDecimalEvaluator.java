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

import java.math.BigDecimal;

import junit.framework.TestCase;

import com.digipom.android.library.evaluator.PostfixEvaluator.FlatToken;
import com.digipom.android.library.evaluator.exception.ParseException;

public class TestBigDecimalEvaluator extends TestCase {
	// BigDecimal is far slower than float or double, so reduce the # of
	// iterations.
	private static final int RESOLUTION = 16;

	interface ZCommand {
		BigDecimal execute(BigDecimal x, BigDecimal y);
	}

	public void testSimpleEvaluate() throws ParseException {
		BigDecimalPostfixEvaluator evaluator = new BigDecimalPostfixEvaluator(
				"x + y");

		testCombos(RESOLUTION, evaluator, new ZCommand() {

			@Override
			public BigDecimal execute(BigDecimal x, BigDecimal y) {
				return x.add(y);
			}
		});
	}

	public void testMoreComplexEvaluate() throws ParseException {
		BigDecimalPostfixEvaluator evaluator = new BigDecimalPostfixEvaluator(
				"sin(y) + cos(x)");

		testCombos(RESOLUTION, evaluator, new ZCommand() {

			@Override
			public BigDecimal execute(BigDecimal x, BigDecimal y) {
				return new BigDecimal(Math.sin(y.doubleValue()))
						.add(new BigDecimal(Math.cos(x.doubleValue())));
			}
		});
	}

	public void testYetMoreComplexEvaluate() throws ParseException {
		BigDecimalPostfixEvaluator evaluator = new BigDecimalPostfixEvaluator(
				"pow(abs(cos(x) + cos(y)), 0.5)");
		testCombos(RESOLUTION, evaluator, new ZCommand() {

			@Override
			public BigDecimal execute(BigDecimal x, BigDecimal y) {
				return new BigDecimal(Math.pow(
						new BigDecimal(Math.cos(x.doubleValue()))
								.add(new BigDecimal(Math.cos(y.doubleValue())))
								.abs().doubleValue(), 0.5));
			}
		});
	}

	public void testYetMoreComplexEvaluateJava() {
		final BigDecimal[][] result = new BigDecimal[RESOLUTION][RESOLUTION];

		for (int y = 0; y < RESOLUTION; y++) {
			for (int x = 0; x < RESOLUTION; x++) {
				result[y][x] = new BigDecimal(Math.pow(
						new BigDecimal(
								Math.cos(new BigDecimal(x).doubleValue()))
								.add(new BigDecimal(Math.cos(new BigDecimal(y)
										.doubleValue()))).abs().doubleValue(),
						0.5));
			}
		}
	}

	public void testCaretWithNoSpace() throws ParseException {
		BigDecimalPostfixEvaluator evaluator = new BigDecimalPostfixEvaluator(
				"x ^ y");
		testCombos(RESOLUTION, evaluator, new ZCommand() {

			@Override
			public BigDecimal execute(BigDecimal x, BigDecimal y) {
				return new BigDecimal(
						Math.pow(x.doubleValue(), y.doubleValue()));
			}
		});
	}

	public void testCaretWithNestedExpression() throws ParseException {
		BigDecimalPostfixEvaluator evaluator = new BigDecimalPostfixEvaluator(
				"abs(cos(x) + cos(y)) ^ 0.5");
		testCombos(RESOLUTION, evaluator, new ZCommand() {

			@Override
			public BigDecimal execute(BigDecimal x, BigDecimal y) {
				return new BigDecimal(Math.pow(
						new BigDecimal(Math.cos(x.doubleValue()))
								.add(new BigDecimal(Math.cos(y.doubleValue())))
								.abs().doubleValue(), 0.5));
			}
		});
	}

	public void testNestedCaretsSequential() throws ParseException {
		BigDecimalPostfixEvaluator evaluator = new BigDecimalPostfixEvaluator(
				"x^y^2");
		// 16 otherwise our numbers get into a ridiculous range.
		testCombos(16, evaluator, new ZCommand() {

			@Override
			public BigDecimal execute(BigDecimal x, BigDecimal y) {
				return new BigDecimal(Math.pow(x.doubleValue(), new BigDecimal(
						Math.pow(y.doubleValue(), 2)).doubleValue()));
			}
		});
	}

	public void testSequentialCarets() throws ParseException {
		BigDecimalPostfixEvaluator evaluator = new BigDecimalPostfixEvaluator(
				"x^2 * y^2");
		testCombos(RESOLUTION, evaluator, new ZCommand() {

			@Override
			public BigDecimal execute(BigDecimal x, BigDecimal y) {
				return (new BigDecimal(Math.pow(x.doubleValue(), 2))
						.multiply(new BigDecimal(Math.pow(y.doubleValue(), 2))));
			}
		});
	}

	public void testCaretPrecedence() throws ParseException {
		BigDecimalPostfixEvaluator evaluator = new BigDecimalPostfixEvaluator(
				"-3^2");

		testCombos(RESOLUTION, evaluator, new ZCommand() {

			@Override
			public BigDecimal execute(BigDecimal x, BigDecimal y) {
				return new BigDecimal(Math.pow(new BigDecimal(3).doubleValue(),
						new BigDecimal(2).doubleValue())).negate();
			}
		});
	}

	private void testCombos(int range, BigDecimalPostfixEvaluator evaluator,
			ZCommand zCommand) throws ParseException {
		FlatToken yIdentifier = evaluator.getIdentifier("y");
		FlatToken xIdentifier = evaluator.getIdentifier("x");

		for (int y = 0; y < range; y++) {
			yIdentifier.bigDecimalValue = BigDecimal.valueOf(y);

			for (int x = 0; x < range; x++) {
				xIdentifier.bigDecimalValue = BigDecimal.valueOf(x);

				assertEquals(
						zCommand.execute(new BigDecimal(x), new BigDecimal(y))
								.doubleValue(), evaluator.evaluate()
								.doubleValue(), 0.001);
			}
		}
	}
}

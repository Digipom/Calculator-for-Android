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

import junit.framework.TestCase;

import com.digipom.android.library.evaluator.PostfixEvaluator.FlatToken;
import com.digipom.android.library.evaluator.exception.ParseException;

public class TestDoubleEvaluator extends TestCase {
	private static final int RESOLUTION = 32;

	interface ZCommand {
		double execute(double x, double y);
	}

	public void testSimpleEvaluate() throws ParseException {
		DoublePostfixEvaluator evaluator = new DoublePostfixEvaluator("x + y");

		testCombos(RESOLUTION, evaluator,  new ZCommand() {

			@Override
			public double execute(double x, double y) {
				return x + y;
			}
		});
	}

	public void testMoreComplexEvaluate() throws ParseException {
		DoublePostfixEvaluator evaluator = new DoublePostfixEvaluator("sin(y) + cos(x)");
		testCombos(RESOLUTION, evaluator,  new ZCommand() {

			@Override
			public double execute(double x, double y) {
				return Math.sin(y) + Math.cos(x);
			}
		});
	}

	public void testYetMoreComplexEvaluate() throws ParseException {
		DoublePostfixEvaluator evaluator = new DoublePostfixEvaluator("pow(abs(cos(x) + cos(y)), 0.5)");
		testCombos(RESOLUTION, evaluator,  new ZCommand() {

			@Override
			public double execute(double x, double y) {
				return Math.pow(Math.abs(Math.cos(x) + Math.cos(y)), 0.5);
			}
		});
	}

	public void testYetMoreComplexEvaluateJava() {
		final double[][] result = new double[RESOLUTION][RESOLUTION];

		for (int y = 0; y < RESOLUTION; y++) {
			for (int x = 0; x < RESOLUTION; x++) {
				result[y][x] = Math.pow(Math.abs(Math.cos(x) + Math.cos(y)), 0.5);
			}
		}
	}

	public void testCaretWithNoSpace() throws ParseException {
		DoublePostfixEvaluator evaluator = new DoublePostfixEvaluator("x ^ y");
		testCombos(RESOLUTION, evaluator,  new ZCommand() {

			@Override
			public double execute(double x, double y) {
				return Math.pow(x, y);
			}
		});
	}

	public void testCaretWithNestedExpression() throws ParseException {
		DoublePostfixEvaluator evaluator = new DoublePostfixEvaluator("abs(cos(x) + cos(y)) ^ 0.5");
		testCombos(RESOLUTION, evaluator,  new ZCommand() {

			@Override
			public double execute(double x, double y) {
				return Math.pow(Math.abs(Math.cos(x) + Math.cos(y)), 0.5);
			}
		});
	}

	public void testNestedCaretsSequential() throws ParseException {
		DoublePostfixEvaluator evaluator = new DoublePostfixEvaluator("x^y^2");
		testCombos(RESOLUTION, evaluator,  new ZCommand() {

			@Override
			public double execute(double x, double y) {				
				return Math.pow(x, Math.pow(y, 2));
			}
		});
	}

	public void testSequentialCarets() throws ParseException {
		DoublePostfixEvaluator evaluator = new DoublePostfixEvaluator("x^2 * y^2");
		testCombos(RESOLUTION, evaluator,  new ZCommand() {

			@Override
			public double execute(double x, double y) {
				return (Math.pow(x, 2) * Math.pow(y, 2));
			}
		});
	}

	public void testCaretPrecedence() throws ParseException {
		DoublePostfixEvaluator evaluator = new DoublePostfixEvaluator("-3^2");

		
		testCombos(RESOLUTION, evaluator,  new ZCommand() {

			@Override
			public double execute(double x, double y) {
				return -Math.pow(3, 2);
			}
		});
	}
	
	private void testCombos(int range, DoublePostfixEvaluator evaluator, ZCommand zCommand) throws ParseException {
		FlatToken yIdentifier = evaluator.getIdentifier("y");
		FlatToken xIdentifier = evaluator.getIdentifier("x");
		
		for (int y = 0; y < range; y++) {			
			yIdentifier.doubleValue = y;
			
			for (int x = 0; x < range; x++) {				
				xIdentifier.doubleValue = x;
				 				
				assertEquals(zCommand.execute(x, y), evaluator.evaluate(), 0.001);
			}
		}
	}	
}

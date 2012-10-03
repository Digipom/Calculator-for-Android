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

import com.digipom.android.library.evaluator.exception.ParseException;

public class DoublePostfixEvaluator extends PostfixEvaluator {
	private final double[] operandStack = new double[4096];
	private int stackPointer = -1;
	
	public DoublePostfixEvaluator(String input) throws ParseException {
		super(input, NumberPrecision.DOUBLE);
	}

	public double evaluate() throws ParseException {
		for (final FlatToken token : postfixExpression) {
			switch (token.type) {
				case FlatToken.TYPE_IDENTIFIER:
				case FlatToken.TYPE_NUMBER_LITERAL:
					operandStack[++stackPointer] = token.doubleValue;
					break;
				case FlatToken.TYPE_OPERATOR:
					if (token.typeEnum == FlatToken.OPERATOR_NEGATE) {
						final double a = operandStack[stackPointer--];
						operandStack[++stackPointer] = -a;
					} else {
						final double b = operandStack[stackPointer--];
						final double a = operandStack[stackPointer--];

						switch (token.typeEnum) {
							case FlatToken.OPERATOR_ADD:
								operandStack[++stackPointer] = a + b;
								break;
							case FlatToken.OPERATOR_SUBTRACT:
								operandStack[++stackPointer] = a - b;
								break;
							case FlatToken.OPERATOR_MULTIPLY:
								operandStack[++stackPointer] = a * b;
								break;
							case FlatToken.OPERATOR_DIVIDE:
								operandStack[++stackPointer] = a / b;
								break;
							case FlatToken.OPERATOR_POWER:
								operandStack[++stackPointer] = Math.pow(a, b);
								break;
						}
					}
					break;
				case FlatToken.TYPE_PREDEF_FUNCTION:
					if (token.typeEnum == FlatToken.FUNCTION_POW) {
						final double b = operandStack[stackPointer--];
						final double a = operandStack[stackPointer--];
						operandStack[++stackPointer] = Math.pow(a, b);
					} else {
						final double a = operandStack[stackPointer--];

						switch (token.typeEnum) {
							case FlatToken.FUNCTION_ABS:
								operandStack[++stackPointer] = Math.abs(a);
								break;
							case FlatToken.FUNCTION_SIN:
								operandStack[++stackPointer] = Math.sin(a);
								break;
							case FlatToken.FUNCTION_COS:
								operandStack[++stackPointer] = Math.cos(a);
								break;
							case FlatToken.FUNCTION_TAN:
								operandStack[++stackPointer] = Math.tan(a);
								break;
							case FlatToken.FUNCTION_LN:
								operandStack[++stackPointer] = Math.log(a);
								break;
							case FlatToken.FUNCTION_SQRT:
								operandStack[++stackPointer] = Math.sqrt(a);
								break;
						}
					}
					break;
			}
		}

		if (stackPointer != 0) {
			stackPointer = -1;
			throw new ParseException("Error evaluating expression");
		}

		return operandStack[stackPointer--];
	}
}

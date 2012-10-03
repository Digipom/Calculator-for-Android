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

package com.digipom.android.library.evaluator.builder;

import com.digipom.android.library.evaluator.lexer.NumberLiteral;

class StringNumberLiteral extends NumberLiteral {
	private static final char NEGATIVE_CHAR = '-';
	private static final String DECIMAL_STR = ".";
	final StringBuilder value;

	StringNumberLiteral() {
		this.value = new StringBuilder();
	}

	StringNumberLiteral(String value) {
		this.value = new StringBuilder(value);
	}

	void addDecimalIfNeeded() {
		if (value.indexOf(DECIMAL_STR) == -1) {
			value.append(DECIMAL_STR);
		}
	}

	void togglePlusMinus() {
		if (value.length() > 0 && value.charAt(0) == NEGATIVE_CHAR) {
			value.deleteCharAt(0);
		} else {
			value.insert(0, NEGATIVE_CHAR);
		}
	}

	void appendDigit(int digit) {
		value.append(digit);
	}

	void deleteChar() {
		final int length = value.length();

		if (length > 0) {
			value.deleteCharAt(length - 1);
		}
	}

	boolean isEmpty() {
		return value.length() == 0;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
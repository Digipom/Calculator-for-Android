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

package com.digipom.calculator.logic;

import java.math.BigDecimal;
import java.util.Stack;

import android.util.Log;

import com.digipom.calculator.config.LoggerConfig;

class Memory {
	private static final String TAG = "Memory";
	private final Stack<BigDecimal> answerStack = new Stack<BigDecimal>();
	private final String[] storedExpressions = new String[10];

	void clearAnswers() {
		answerStack.clear();		
	}	

	BigDecimal getMostRecentAnswer() {
		if (answerStack.isEmpty()) {
			return BigDecimal.ZERO;
		} else {
			return answerStack.peek();
		}
	}

	void addAnswer(BigDecimal result) {
		answerStack.push(result);
	}

	void addExpressionToStore(int position, String expression) {
		if (position < 0 || position >= storedExpressions.length) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "addToStore(): Invalid position: " + position);
			}
		} else {
			storedExpressions[position] = expression;
		}
	}
	
	String readExpressionFromStore(int position) {
		if (position < 0 || position >= storedExpressions.length) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "addToStore(): Invalid position: " + position);
			}
			
			return "";
		} else {			
			return storedExpressions[position] == null ? "" : storedExpressions[position];
		}
	}
	
	int getStoreSize() {
		return storedExpressions.length;
	}
}

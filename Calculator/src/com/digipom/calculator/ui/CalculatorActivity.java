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

package com.digipom.calculator.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.digipom.calculator.R;
import com.digipom.calculator.logic.Calculator;

public class CalculatorActivity extends Activity {
	private static final int VIBRATE_TIME = 25;

	/** Internal state for calculator. */
	private Calculator calculator;

	/** For vibration of buttons. */
	private Vibrator vibrator;

	/** Output display for calculator. */
	private TextView modeView;
	private TextView outputView;

	/** UI States. */
	private boolean inSecondFunction = false;

	class VibratorTouchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event != null && event.getAction() == MotionEvent.ACTION_DOWN) {
				vibrator.vibrate(VIBRATE_TIME);
			}

			return false;
		}
	}

	private void updateOutput() {
		modeView.setText(calculator.getModeHeader());
		outputView.setText(calculator.getExpression());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/** Initialize variables. */
		calculator = new Calculator(this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		modeView = (TextView) findViewById(R.id.modeView);
		outputView = (TextView) findViewById(R.id.outputView);

		/** Add vibrate listeners. */
		addVibrateListenersToButtons((ViewGroup) findViewById(R.id.rootView), new VibratorTouchListener());
	}

	private void addVibrateListenersToButtons(ViewGroup root, VibratorTouchListener listener) {
		final int childCount = root.getChildCount();

		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);

			if (child instanceof Button) {
				child.setOnTouchListener(listener);
			} else if (child instanceof ViewGroup) {
				addVibrateListenersToButtons((ViewGroup) child, listener);
			}
		}
	}

	// @Override -- From XML layout
	public void onButtonClick(View v) {
		if (v != null) {
			final int id = v.getId();

			if (inSecondFunction) {
				switch (id) {
					case R.id.second:
						// TODO
						break;
					case R.id.cpt:
						// TODO
						break;
					case R.id.enter:
						// TODO
						break;
					case R.id.up_arrow:
						// TODO
						break;
					case R.id.down_arrow:
						// TODO
						break;
					case R.id.cf:
						// TODO
						break;
					case R.id.n:
						// TODO
						break;
					case R.id.i_y:
						// TODO
						break;
					case R.id.pv:
						// TODO
						break;
					case R.id.pmt:
						// TODO
						break;
					case R.id.fv:
						// TODO
						break;
					case R.id.npv:
						// TODO
						break;
					case R.id.percent:
						// TODO
						break;
					case R.id.sqrt_x:
						// TODO
						break;
					case R.id.x_squared:
						// TODO
						break;
					case R.id.right_arrow:
						// TODO
						break;
					case R.id.ac:
						// TODO
						break;
					case R.id.inv:
						// TODO
						break;
					case R.id.left_parenthesis:
						// TODO
						break;
					case R.id.right_parenthesis:
						// TODO
						break;
					case R.id.y_to_power_of_x:
						// TODO
						break;
					case R.id.divide:
						// TODO
						break;
					case R.id.ln:
						// TODO
						break;
					case R.id.seven:
						// TODO
						break;
					case R.id.eight:
						// TODO
						break;
					case R.id.nine:
						// TODO
						break;
					case R.id.multiply:
						// TODO
						break;
					case R.id.sto:
						// TODO
						break;
					case R.id.four:
						// TODO
						break;
					case R.id.five:
						// TODO
						break;
					case R.id.six:
						// TODO
						break;
					case R.id.minus:
						// TODO
						break;
					case R.id.rcl:
						// TODO
						break;
					case R.id.one:
						// TODO
						break;
					case R.id.two:
						// TODO
						break;
					case R.id.three:
						// TODO
						break;
					case R.id.plus:
						// TODO
						break;
					case R.id.ce_c:
						// TODO
						break;
					case R.id.zero:
						calculator.selectMemMode();
						break;
					case R.id.dot:
						// TODO
						break;
					case R.id.plusMinus:
						// TODO
						break;
					case R.id.equals:
						// TODO
						break;
				}

				inSecondFunction = false;
			} else {
				switch (id) {
					case R.id.second:
						inSecondFunction = true;
						break;
					case R.id.cpt:
						// TODO
						break;
					case R.id.enter:
						calculator.selectEnter();
						break;
					case R.id.up_arrow:
						calculator.selectUpArrow();
						break;
					case R.id.down_arrow:
						calculator.selectDownArrow();
						break;
					case R.id.cf:
						// TODO
						break;
					case R.id.n:
						// TODO
						break;
					case R.id.i_y:
						// TODO
						break;
					case R.id.pv:
						// TODO
						break;
					case R.id.pmt:
						// TODO
						break;
					case R.id.fv:
						// TODO
						break;
					case R.id.npv:
						// TODO
						break;
					case R.id.percent:
						// TODO
						break;
					case R.id.sqrt_x:
						calculator.selectSqrtX();
						break;
					case R.id.x_squared:
						calculator.selectXSquared();
						break;
					case R.id.right_arrow:
						calculator.selectRightArrow();
						break;
					case R.id.ac:
						calculator.selectAc();
						break;
					case R.id.inv:
						// TODO
						break;
					case R.id.left_parenthesis:
						calculator.selectLeftParenthesis();
						break;
					case R.id.right_parenthesis:
						calculator.selectRightParenthesis();
						break;
					case R.id.y_to_power_of_x:
						calculator.selectYPowX();
						break;
					case R.id.divide:
						calculator.selectDivide();
						break;
					case R.id.ln:
						calculator.selectLn();
						break;
					case R.id.seven:
						calculator.selectDigit(7);
						break;
					case R.id.eight:
						calculator.selectDigit(8);
						break;
					case R.id.nine:
						calculator.selectDigit(9);
						break;
					case R.id.multiply:
						calculator.selectMultiply();
						break;
					case R.id.sto:
						calculator.selectSto();
						break;
					case R.id.four:
						calculator.selectDigit(4);
						break;
					case R.id.five:
						calculator.selectDigit(5);
						break;
					case R.id.six:
						calculator.selectDigit(6);
						break;
					case R.id.minus:
						calculator.selectSubtract();
						break;
					case R.id.rcl:
						calculator.selectRcl();
						break;
					case R.id.one:
						calculator.selectDigit(1);
						break;
					case R.id.two:
						calculator.selectDigit(2);
						break;
					case R.id.three:
						calculator.selectDigit(3);
						break;
					case R.id.plus:
						calculator.selectAdd();
						break;
					case R.id.ce_c:
						calculator.selectCe();
						break;
					case R.id.zero:
						calculator.selectDigit(0);
						break;
					case R.id.dot:
						calculator.selectDecimal();
						break;
					case R.id.plusMinus:
						calculator.selectPlusMinus();
						break;
					case R.id.equals:
						calculator.selectEquals();
						break;
				}
			}
		}

		updateOutput();
	}
}
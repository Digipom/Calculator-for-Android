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

import static com.digipom.calculator.logic.Calculator.ExpressionState.DISPLAY;
import static com.digipom.calculator.logic.Calculator.ExpressionState.EDIT;
import static com.digipom.calculator.logic.Calculator.ExpressionState.ERROR;

import java.math.BigDecimal;
import java.util.EnumMap;

import android.content.Context;
import android.util.Log;

import com.digipom.android.library.evaluator.BigDecimalPostfixEvaluator;
import com.digipom.android.library.evaluator.builder.ExpressionBuilder;
import com.digipom.android.library.evaluator.exception.ParseException;
import com.digipom.android.library.evaluator.lexer.Operator;
import com.digipom.android.library.evaluator.lexer.Parenthesis;
import com.digipom.android.library.evaluator.lexer.PredefinedFunction;
import com.digipom.calculator.R;
import com.digipom.calculator.config.LoggerConfig;

public class Calculator {
	private static final String TAG = "Calculator";

	private final Context context;

	/** Calculator internals. */
	
	private final InputBuffer inputBuffer = new InputBuffer();
	private final Memory memory = new Memory();

	/** Command definitions. */

	private final Command[] digitCommands = new Command[] { new DigitCommand(0), new DigitCommand(1),
			new DigitCommand(2), new DigitCommand(3), new DigitCommand(4), new DigitCommand(5), new DigitCommand(6),
			new DigitCommand(7), new DigitCommand(8), new DigitCommand(9) };

	private final Command addCommand = new OperatorCommand(Operator.ADD);
	private final Command subtractCommand = new OperatorCommand(Operator.SUBTRACT);
	private final Command multiplyCommand = new OperatorCommand(Operator.MULTIPLY);
	private final Command divideCommand = new OperatorCommand(Operator.DIVIDE);
	private final Command powerCommand = new OperatorCommand(Operator.POWER);

	private final Command openParenthesisCommand = new ParenthesisCommand(Parenthesis.OPEN);
	private final Command closeParenthesisCommand = new ParenthesisCommand(Parenthesis.CLOSE);

	private final Command equalsCommand = new EqualsCommand();

	private final Command sqrtCommand = new FunctionCommand(PredefinedFunction.SQRT);
	private final Command xSquaredCommand = new XSquaredCommand();
	private final Command lnCommand = new FunctionCommand(PredefinedFunction.LN);

	private final Command dotCommand = new DotCommand();
	private final Command plusMinusCommand = new PlusMinusCommand();
	private final Command deleteCommand = new DeleteCommand();

	private final Command selectMemModeCommand = new SwitchModeCommand(InputMode.MEMORY);

	private final Command acCommand = new AcCommand();
	private final Command ceCommand = new CeCommand();

	private final Command stoCommand = new StoCommand();
	private final Command rclCommand = new RclCommand();

	/** States and Input Modes. */
	
	enum DigitState {
		DEFAULT, RECALL, STORE
	}

	enum InputMode {
		NORMAL, MEMORY
	}

	private final EnumMap<InputMode, AbstractInputMode> inputModes = new EnumMap<InputMode, AbstractInputMode>(
			InputMode.class);

	private DigitState digitState = DigitState.DEFAULT;
	private InputMode inputMode = InputMode.NORMAL;	
	
	// TODO I'm getting there, but this thing is jacked. Fix it.
	// Basically anything that changed the input buffer until enter, up, down, or CE is pressed should set this to true.
	private boolean modeDataModified;

	/** Constructor. */

	public Calculator(Context context) {
		this.context = context;

		inputModes.put(InputMode.NORMAL, new NormalInputMode());
		inputModes.put(InputMode.MEMORY, new MemoryInputMode());
	}

	/** Input buffer methods. */

	public String getModeHeader() {
		return inputModes.get(inputMode).getHeader() + (inputMode != InputMode.NORMAL && !modeDataModified ? " = " : "");
	}

	public String getExpression() {
		return inputBuffer.getExpression();
	}

	/** Arithmetic commands. */

	public void selectAdd() {
		addCommand.execute();
	}

	public void selectSubtract() {
		subtractCommand.execute();
	}

	public void selectMultiply() {
		multiplyCommand.execute();
	}

	public void selectDivide() {
		divideCommand.execute();
	}

	public void selectYPowX() {
		powerCommand.execute();
	}

	public void selectLeftParenthesis() {
		openParenthesisCommand.execute();
	}

	public void selectRightParenthesis() {
		closeParenthesisCommand.execute();
	}

	public void selectEquals() {
		equalsCommand.execute();
	}

	/** Functions. */

	public void selectSqrtX() {
		sqrtCommand.execute();
	}

	public void selectXSquared() {
		xSquaredCommand.execute();
	}

	public void selectLn() {
		lnCommand.execute();
	}

	/** Digit commands. */

	public void selectDigit(int digit) {
		digitCommands[digit].execute();
	}

	public void selectDecimal() {
		dotCommand.execute();
	}

	public void selectPlusMinus() {
		plusMinusCommand.execute();
	}

	/** Input commands. */

	public void selectRightArrow() {
		deleteCommand.execute();
	}

	/** InputMode commands. */

	public void selectEnter() {
		inputModes.get(inputMode).store(inputBuffer.validateExpressionAndGet());
		modeDataModified = false;
	}

	public void selectUpArrow() {
		inputModes.get(inputMode).selectNext();		
		inputBuffer.setExpression(inputModes.get(inputMode).get());
		modeDataModified = false;
	}

	public void selectDownArrow() {
		inputModes.get(inputMode).selectPrevious();
		inputBuffer.setExpression(inputModes.get(inputMode).get());
		modeDataModified = false;
	}

	public void selectMemMode() {
		selectMemModeCommand.execute();
	}

	/** State commands. */

	public void selectAc() {
		acCommand.execute();
	}

	public void selectCe() {
		ceCommand.execute();
	}

	/** Memory commands. */

	public void selectSto() {
		stoCommand.execute();
	}

	public void selectRcl() {
		rclCommand.execute();
	}

	/** Commands */

	abstract class Command {
		boolean resetToDefaultDigitStateAfterExecute = true;
		boolean canRunInErrorState = false;

		final void execute() {
			if (canRunInErrorState || !(inputBuffer.state == ERROR)) {
				doCommand();

				if (resetToDefaultDigitStateAfterExecute) {
					digitState = DigitState.DEFAULT;
				}
			}
		}

		protected abstract void doCommand();
	}

	abstract class ExpressionModifyingCommand {

	}

	class DigitCommand extends Command {
		final int digit;

		DigitCommand(int digit) {
			this.digit = digit;
		}

		@Override
		protected void doCommand() {
			switch (digitState) {
				case DEFAULT:
					inputBuffer.appendDigit(digit);
					break;
				case RECALL:
					inputBuffer.setExpression(memory.readExpressionFromStore(digit));
					break;
				case STORE:
					memory.addExpressionToStore(digit, inputBuffer.validateExpressionAndGet());
					break;
			}
		}
	}

	class OperatorCommand extends Command {
		final Operator operator;

		OperatorCommand(Operator operator) {
			this.operator = operator;
		}

		@Override
		protected void doCommand() {
			inputBuffer.appendOperator(operator);
		}
	}

	class ParenthesisCommand extends Command {
		final Parenthesis parenthesis;

		ParenthesisCommand(Parenthesis parenthesis) {
			this.parenthesis = parenthesis;
		}

		@Override
		protected void doCommand() {
			inputBuffer.appendParenthesis(parenthesis);
		}
	}

	class EqualsCommand extends Command {
		@Override
		protected void doCommand() {
			try {
				String input = inputBuffer.getExpression();

				if (input.length() > 0) {
					String validatedInput = inputBuffer.validateExpressionAndGet();

					if (input.equals(validatedInput)) {
						final BigDecimal result = new BigDecimalPostfixEvaluator(input).evaluate();
						memory.addAnswer(result);
						inputBuffer.setExpression(result.toPlainString());
					}
				}
			} catch (ParseException pe) {
				if (LoggerConfig.ON) {
					Log.v(TAG, pe.toString(), pe);
				}
				inputBuffer.enterErrorState();
			} catch (RuntimeException re) {
				if (LoggerConfig.ON) {
					Log.w(TAG, re);
				}
				inputBuffer.enterErrorState();
			}
		}
	}

	class FunctionCommand extends Command {
		final PredefinedFunction function;

		FunctionCommand(PredefinedFunction function) {
			this.function = function;
		}

		@Override
		protected void doCommand() {
			inputBuffer.appendFunction(function);
		}
	}

	class XSquaredCommand extends Command {
		@Override
		protected void doCommand() {
			inputBuffer.appendXSquared();
		}
	}

	class DotCommand extends Command {
		@Override
		protected void doCommand() {
			inputBuffer.appendDecimal();
		}
	}

	class PlusMinusCommand extends Command {
		@Override
		protected void doCommand() {
			inputBuffer.togglePlusMinus();
		}
	}

	class DeleteCommand extends Command {
		@Override
		protected void doCommand() {
			inputBuffer.deleteElement();
		}
	}

	class SwitchModeCommand extends Command {
		final InputMode mode;

		SwitchModeCommand(InputMode mode) {
			this.mode = mode;
		}

		@Override
		protected void doCommand() {
			Calculator.this.inputMode = mode;
			inputBuffer.setExpression(inputModes.get(mode).get());
			modeDataModified = false;
		}
	}

	class AcCommand extends Command {
		AcCommand() {
			canRunInErrorState = true;
		}

		@Override
		protected void doCommand() {			
			memory.clearAnswers();
			inputBuffer.clear();
			inputMode = InputMode.NORMAL;
			digitState = DigitState.DEFAULT;
		}
	}

	class CeCommand extends Command {
		CeCommand() {
			canRunInErrorState = true;
		}

		@Override
		protected void doCommand() {
			if (inputBuffer.state == ERROR) {				
				inputBuffer.clear();
			} else if (inputMode != InputMode.NORMAL) {
				if (modeDataModified) {					
					inputBuffer.setExpression(inputModes.get(inputMode).get());
					modeDataModified = false;
				} else {
					inputMode = InputMode.NORMAL;
				}
			} else {
				
				inputBuffer.clear();
			}

			digitState = DigitState.DEFAULT;
		}
	}

	class StoCommand extends Command {
		StoCommand() {
			resetToDefaultDigitStateAfterExecute = false;
		}

		@Override
		protected void doCommand() {
			digitState = DigitState.STORE;
		}
	}

	class RclCommand extends Command {
		RclCommand() {
			resetToDefaultDigitStateAfterExecute = false;
		}

		@Override
		protected void doCommand() {
			digitState = DigitState.RECALL;
		}
	}

	/** Modes. */

	abstract class AbstractInputMode {
		abstract String getHeader();

		abstract void store(String toStore);

		abstract String get();

		abstract void selectNext();

		abstract void selectPrevious();
	}

	class NormalInputMode extends AbstractInputMode {
		@Override
		String getHeader() {
			return "";
		}

		@Override
		void store(String toStore) {
			// No-op
		}

		@Override
		String get() {
			return "";
		}

		@Override
		void selectNext() {
			// No-op
		}

		@Override
		void selectPrevious() {
			// No-op
		}
	}

	class MemoryInputMode extends AbstractInputMode {
		int selectedSlot = 0;

		@Override
		String getHeader() {
			return "M" + selectedSlot;
		}

		@Override
		void store(String toStore) {
			memory.addExpressionToStore(selectedSlot, toStore);
		}

		@Override
		String get() {
			return memory.readExpressionFromStore(selectedSlot);
		}

		@Override
		void selectNext() {
			selectedSlot++;

			if (selectedSlot > memory.getStoreSize() - 1) {
				selectedSlot = 0;
			}
		}

		@Override
		void selectPrevious() {
			selectedSlot--;

			if (selectedSlot < 0) {
				selectedSlot = memory.getStoreSize() - 1;
			}
		}
	}

	enum ExpressionState {
		DISPLAY, EDIT, ERROR
	}

	/** Input buffer helper */
	
	// TODO Move to separate class to clean up the way that the error states are being used a bit.
	class InputBuffer {
		private final ExpressionBuilder expressionBuilder = new ExpressionBuilder();
		private final StringBuilder builder = new StringBuilder();

		ExpressionState state = DISPLAY;

		void enterErrorState() {
			state = ERROR;
		}

		String getExpression() {
			if (state == ERROR) {
				return context.getString(R.string.error);
			} else {
				return expressionBuilder.toString();
			}
		}

		String validateExpressionAndGet() {
			if (state == ERROR) {
				return context.getString(R.string.error);
			} else {
				state = DISPLAY;
				return expressionBuilder.build();
			}
		}

		void setExpression(String expression) {
			state = DISPLAY;
			expressionBuilder.setExpression(expression);
			modeDataModified = true;
		}

		void clear() {
			state = DISPLAY;
			expressionBuilder.clear();
			modeDataModified = false;
		}

		private boolean enterEdit() {
			if (state == ERROR) {
				return false;
			} else {
				if (state == DISPLAY) {
					state = EDIT;
					modeDataModified = true;
				}
				return true;
			}
		}

		private boolean enterEditAndClearIfNecessary() {
			if (state == ERROR) {
				return false;
			} else {
				if (state == DISPLAY) {
					expressionBuilder.clear();
					state = EDIT;
					modeDataModified = true;
				}
				return true;
			}
		}

		void appendOperator(Operator operator) {
			if (enterEdit()) {
				expressionBuilder.appendOperator(operator);
			}
		}

		void appendXSquared() {
			if (enterEdit()) {
				expressionBuilder.appendXSquared();
			}
		}

		void appendFunction(PredefinedFunction function) {
			boolean shouldWrapOldExpression = state == DISPLAY && !expressionBuilder.isEmpty();

			if (enterEdit()) {
				if (shouldWrapOldExpression) {
					builder.setLength(0);
					builder.append(function.toString() + Parenthesis.OPEN.toString());
					builder.append(expressionBuilder.toString());
					builder.append(Parenthesis.CLOSE);
					expressionBuilder.setExpression(builder.toString());
				} else {
					expressionBuilder.appendFunction(function);
				}
			}
		}

		void togglePlusMinus() {
			if (enterEdit()) {
				expressionBuilder.togglePlusMinus();
			}
		}

		void appendDigit(int digit) {
			if (enterEditAndClearIfNecessary()) {
				expressionBuilder.appendDigit(digit);
			}
		}

		void appendDecimal() {
			if (enterEditAndClearIfNecessary()) {
				expressionBuilder.appendDecimal();
			}
		}

		void appendParenthesis(Parenthesis parenthesis) {
			if (enterEditAndClearIfNecessary()) {
				expressionBuilder.appendParenthesis(parenthesis);
			}
		}

		void deleteElement() {
			if (enterEditAndClearIfNecessary()) {
				expressionBuilder.deleteElement();
			}
		}
	}
}

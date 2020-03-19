package pt.ulisboa.tecnico.learnjava.sibs.domain;

import TransferOperationState.RETRY;
import TransferOperationState.TransferOperationState;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferOperation extends Operation {
	private final String sourceIban;
	private final String targetIban;
	private StatesChain stateContext;

	public TransferOperation(String sourceIban, String targetIban, int value) throws OperationException {
		super(Operation.OPERATION_TRANSFER, value);

		if (invalidString(sourceIban) || invalidString(targetIban)) {
			throw new OperationException();
		}
		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.stateContext = new StatesChain(this.sourceIban, this.targetIban, getValue(), commission());
	}

	@Override
	public void process() throws SibsException, AccountException, OperationException {
		try {
			this.stateContext.process();
		} catch (Exception e) {
			this.stateContext.setState(new RETRY());
		}
	}

	public StatesChain getStateContext() {
		return this.stateContext;
	}

	public TransferOperationState getState() {
		return this.stateContext.getCurrentState();
	}

	private boolean invalidString(String name) {
		return name == null || name.length() == 0;
	}

	@Override
	public int commission() {
		return (int) Math.round(super.commission() + getValue() * 0.05);
	}

	public String getSourceIban() {
		return this.sourceIban;
	}

	public String getTargetIban() {
		return this.targetIban;
	}

	public void cancel() throws OperationException, AccountException {

		this.stateContext.cancel();
	}

}

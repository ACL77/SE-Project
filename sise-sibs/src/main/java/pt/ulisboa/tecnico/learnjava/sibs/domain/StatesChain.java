package pt.ulisboa.tecnico.learnjava.sibs.domain;

import TransferOperationState.REGISTERED;
import TransferOperationState.RETRY;
import TransferOperationState.TransferOperationState;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class StatesChain {
	private final String sourceIban;
	private final String targetIban;
	private final int value;
	private final int commission;

	private Services services;

	private TransferOperationState currentState;
	private TransferOperationState PreviousState;

	public StatesChain(String sourceIban, String targetIban, int value, int commission) {

		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.value = value;
		this.commission = commission;
		this.services = new Services();
		this.currentState = new REGISTERED();
		this.PreviousState = this.currentState;
	}

	public void setState(TransferOperationState state) {
		if (state instanceof RETRY) {
			this.PreviousState = this.currentState;
		}
		this.currentState = state;
	}

	public void process() throws AccountException, OperationException {

		this.currentState.process(this);
	}

	public void deposit() throws AccountException {
		this.services.deposit(this.targetIban, this.value);
	}

	public void undoDeposit() throws AccountException {
		this.services.withdraw(this.targetIban, this.value);
	}

	public void withdrawValue() throws AccountException {
		this.services.withdraw(this.sourceIban, this.value);
	}

	public void undoWithdrawValue() throws AccountException {
		this.services.deposit(this.sourceIban, this.value);
	}

	public void withdrawCommission() throws AccountException {
		this.services.withdraw(this.sourceIban, this.commission);
	}

	public Boolean verifySameBank() {
		return this.services.verifySameBank(this.sourceIban, this.targetIban);
	}

	public void cancel() throws OperationException, AccountException {
		this.currentState.cancel(this);
	}

	public TransferOperationState getCurrentState() {
		return this.currentState;
	}

	public TransferOperationState getPreviousState() {
		return this.PreviousState;
	}

}

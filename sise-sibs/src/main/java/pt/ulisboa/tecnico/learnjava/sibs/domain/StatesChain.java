package pt.ulisboa.tecnico.learnjava.sibs.domain;

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
	
	public StatesChain(String sourceIban, String targetIban, int value, int commission){
	
		
		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.value = value;
		this.commission = commission;
		this.services = new Services();
		this.currentState = new REGISTERED();
	}
	
	public void setState(TransferOperationState state) {
		currentState = state;
	}
	
	public void process() throws AccountException, OperationException {
		currentState.process(this);
	}
	
	public void deposit() throws AccountException {
		services.deposit(targetIban, value);
	}
	
	public void undoDeposit() throws AccountException {
		services.withdraw(targetIban, value);
	}
	
	public void withdrawValue() throws AccountException {
		services.withdraw(sourceIban, value);
	}
	
	public void undoWithdrawValue() throws AccountException {
		services.deposit(sourceIban, value);
	}
	
	public void withdrawCommission() throws AccountException {
		services.withdraw(sourceIban, this.commission);
	}
	
	public Boolean verifySameBank() {
		return services.verifySameBank(this.sourceIban, this.targetIban);
	}
	
	public void cancel() throws OperationException, AccountException {
		this.currentState.cancel(this);
	}

	public TransferOperationState getCurrentState() {
		return currentState;
	}


}

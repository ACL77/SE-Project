package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public interface TransferOperationState {
	
	void process(StatesChain state) throws AccountException, OperationException;
	void cancel(StatesChain state) throws OperationException, AccountException;
//	public void withdraw(StatesChain aa);
//	public void deposit(StatesChain aa);

	
}

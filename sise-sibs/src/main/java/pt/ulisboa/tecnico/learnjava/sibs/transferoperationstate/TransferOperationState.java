package pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public interface TransferOperationState {
	
	void process(StatesChain state) throws AccountException, OperationException;
	void cancel(StatesChain state) throws OperationException, AccountException;

	
}

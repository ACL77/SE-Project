package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class COMPLETED implements TransferOperationState {

	@Override
	public void process(StatesChain state) throws AccountException {
		// TODO do nothing!? stays COMPLETED

	}

	@Override
	public void cancel(StatesChain state) throws OperationException {
		//not possible to cancel COMPLETED operation
		throw new OperationException();
		
		
	}

}

package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class CANCELLED implements TransferOperationState{

	@Override
	public void process(StatesChain state) throws AccountException, OperationException {
		throw new OperationException();
		
	}

	@Override
	public void cancel(StatesChain state) throws OperationException {
		// TODO Do nothing?? Stays in CANCELED mode...
		
		
	}

}

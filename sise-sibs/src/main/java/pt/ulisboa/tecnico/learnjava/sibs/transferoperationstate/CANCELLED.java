package pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class CANCELLED implements TransferOperationState {

	@Override
	public void process(StatesChain state) throws AccountException, OperationException {
		// Do nothing. Stays in CANCELED mode...

	}

	@Override
	public void cancel(StatesChain state) throws OperationException {
		// Do nothing. Stays in CANCELED mode...

	}

}

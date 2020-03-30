package pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class ERROR implements TransferOperationState {

	@Override
	public void process(StatesChain state) throws AccountException, OperationException {
		// TODO does nothing?! stays as ERROR

	}

	@Override
	public void cancel(StatesChain state) throws OperationException, AccountException {
		// TODO does nothing?! stays as ERROR

	}

}

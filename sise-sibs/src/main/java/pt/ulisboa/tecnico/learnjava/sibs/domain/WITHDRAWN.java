package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class WITHDRAWN implements TransferOperationState {

	@Override
	public void process(StatesChain state) throws AccountException, OperationException {
		
		state.deposit();
		state.setState(new DEPOSITED());
	}

	@Override
	public void cancel(StatesChain state) throws AccountException {
		state.undoWithdrawValue();
		state.setState(new CANCELLED());
		
		
	}

}

package pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.domain.StatesChain;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class RETRY implements TransferOperationState {
	private int count = 3;
	private TransferOperationState previousState;

	public RETRY(TransferOperationState previousState) {
		this.previousState = previousState;
	}

	@Override
	public void process(StatesChain state) throws AccountException, OperationException {
		try {
			state.setState(this.previousState);
			state.process();
		} catch (Exception e) {
			this.count--;
			state.setState(this);
		}
		if (this.count == 0) {
			state.setState(new ERROR());
		}
	}

	@Override
	public void cancel(StatesChain state) throws OperationException, AccountException {
		state.setState(new CANCELLED());
	}

}
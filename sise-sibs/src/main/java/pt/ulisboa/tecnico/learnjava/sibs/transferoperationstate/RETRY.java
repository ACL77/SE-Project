package pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class RETRY implements TransferOperationState {
	private int count = 2;
	private TransferOperationState previousState;

	public RETRY(TransferOperationState previousState) {
		this.previousState = previousState;
	}

	@Override
	public void process(StatesChain state) throws AccountException, OperationException {
		try {
			state.setState(this.previousState);
			state.process(state.getServices());
		} catch (Exception e) {
			this.count--;
			state.setState(this);
		}
		if (this.count == 0) {
			this.cancel(state);
			state.setState(new ERROR());
			
		}
	}

	@Override
	public void cancel(StatesChain state) throws OperationException, AccountException {
		state.setState(this.previousState);
		//is going to undo previous operations (before entering RETRY state)
		state.cancel();
	}

}
package pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.domain.StatesChain;

public class DEPOSITED implements TransferOperationState {

	@Override
	public void process(StatesChain state) throws AccountException {

		if (!state.verifySameBank()) {
			state.withdrawCommission();
		}
		state.setState(new COMPLETED());

	}

	@Override
	public void cancel(StatesChain state) throws AccountException {
		state.undoWithdrawValue();
		state.undoDeposit();
		state.setState(new CANCELLED());

	}

}

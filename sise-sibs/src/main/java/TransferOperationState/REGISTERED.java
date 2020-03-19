package TransferOperationState;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.domain.StatesChain;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class REGISTERED implements TransferOperationState {

	@Override
	public void process(StatesChain state) throws AccountException {
		
		state.withdrawValue();
		state.setState(new WITHDRAWN());
	
	}

	
	@Override
	public void cancel(StatesChain state) throws OperationException {
		state.setState(new CANCELLED());
		
	}

}

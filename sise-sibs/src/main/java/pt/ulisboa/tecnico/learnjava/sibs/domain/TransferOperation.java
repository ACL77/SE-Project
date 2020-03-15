package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferOperation extends Operation {
	private final String sourceIban;
	private final String targetIban;
	
	//store this because inside this class is the attribute with the current state of
	//the transfer -> REGISTERED, WITHDRAWN, DEPOITED, COMPLETED, CANCELLED
	protected StatesChain stateContext;
	
//	private TransferState state;
//
//	public enum TransferState {
//		REGISTERED("RE"), WITHDRAWN("WI"), DEPOSITED("DE"), COMPLETED("CO"), CANCELLED("CA");
//
	
//		private String state;
//
//		TransferState(String state) {
//			this.state = state;
//		}
//
//		public String getState() {
//			return this.state;
//		}
//	}

	public TransferOperation(String sourceIban, String targetIban, int value) throws OperationException {
		super(Operation.OPERATION_TRANSFER, value);

		if (invalidString(sourceIban) || invalidString(targetIban)) {
			throw new OperationException();
		}
		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.stateContext = new StatesChain(this.sourceIban,this.targetIban,getValue(),commission()); 
		//this.state = TransferState.REGISTERED;
	}

//	public TransferState getState() {
//		return this.state;
//	}

	@Override
	public void process() throws SibsException, AccountException, OperationException {

		
		this.stateContext.process();
		
//		int amount = getValue();
//
//		switch (this.state) {
//		case REGISTERED:
//			services.withdraw(this.sourceIban, amount);
//			this.state = TransferState.WITHDRAWN;
//			break;
//		case WITHDRAWN:
//			try {
//				services.deposit(this.targetIban, amount);
//				this.state = TransferState.DEPOSITED;
//			} catch (AccountException e) {
//				// TODO do something?
//				return;
//			}
//			break;
//		case DEPOSITED:
//			// remove fee from source account if banks are different
//			if (!services.verifySameBank(this.sourceIban, this.targetIban)) {
//				services.withdraw(this.sourceIban, this.commission());
//			}
//			this.state = TransferState.COMPLETED;
//			return;
//		default:
//			//this will work for all other cases, including CANCELED
//			throw new OperationException();
//		}

	}

	public StatesChain getStateContext() {
		return stateContext;
	}

	private boolean invalidString(String name) {
		return name == null || name.length() == 0;
	}

	@Override
	public int commission() {
		return (int) Math.round(super.commission() + getValue() * 0.05);
	}

	public String getSourceIban() {
		return this.sourceIban;
	}

	public String getTargetIban() {
		return this.targetIban;
	}

	public void cancel() throws OperationException, AccountException {
		

		this.stateContext.cancel();
		//		switch (this.state) {
//		case REGISTERED:
//			//Do nothing
//			break;
//		case WITHDRAWN:
//			//undo the withdraw operation
//			services.deposit(this.sourceIban, getValue());
//			break;
//		case DEPOSITED:
//			//undo the deposit & withdraw operations
//			services.deposit(this.sourceIban, getValue());
//			services.withdraw(this.targetIban, getValue());	
//			break;
//		default:
//			//this will work for all other cases, including CANCELLED & COMPLETED
//			throw new OperationException();
//		}
//		
//		this.state = TransferState.CANCELLED;
		
	}

}

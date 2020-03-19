package pt.ulisboa.tecnico.learnjava.sibs.domain;

import TransferOperationState.CANCELLED;
import TransferOperationState.COMPLETED;
import TransferOperationState.ERROR;
import TransferOperationState.RETRY;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class Sibs {
	final Operation[] operations;
	Services services;

	public Sibs(int maxNumberOfOperations, Services services) {
		this.operations = new Operation[maxNumberOfOperations];
		this.services = services;
	}

	public void transfer(String sourceIban, String targetIban, int amount)
			throws SibsException, AccountException, OperationException {

		// verifies if the account exists and if the bank in the iban is the bank where
		// the account is
		if (!this.services.verifyAccountExistanceInBank(targetIban)
				|| !this.services.verifyAccountExistanceInBank(sourceIban)) {
			throw new SibsException();
		}

		addOperation(Operation.OPERATION_TRANSFER, sourceIban, targetIban, amount);
	}

	public void processOperations() throws SibsException, AccountException, OperationException {
		for (Operation operation : this.operations) {
			if (operation != null && operation.getType().equals(Operation.OPERATION_TRANSFER)) {
				TransferOperation transfer = (TransferOperation) operation;
				while (!(transfer.getStateContext().getCurrentState() instanceof COMPLETED)
						&& !(transfer.getStateContext().getCurrentState() instanceof CANCELLED)
						&& !(transfer.getStateContext().getCurrentState() instanceof ERROR)) {
					try {
						transfer.process();
					} catch (Exception e) {
						if (transfer.getStateContext().getCurrentState() instanceof RETRY) {
							transfer.process();
						} else {
							transfer.getStateContext().setState(new RETRY());
						}
					}
				}
			}
		}
	}

	// assuming that the id is the operation number on the array
	public void cancelOperation(int n) throws OperationException, AccountException, SibsException {
		TransferOperation operation = (TransferOperation) this.getOperation(n);
		operation.cancel();
	}

	public int addOperation(String type, String sourceIban, String targetIban, int value)
			throws OperationException, SibsException {
		int position = -1;
		for (int i = 0; i < this.operations.length; i++) {
			if (this.operations[i] == null) {
				position = i;
				break;
			}
		}

		if (position == -1) {
			throw new SibsException();
		}

		Operation operation;
		if (type.equals(Operation.OPERATION_TRANSFER)) {
			operation = new TransferOperation(sourceIban, targetIban, value);

		} else {
			operation = new PaymentOperation(targetIban, value);
		}

		this.operations[position] = operation;
		return position;
	}

	public void removeOperation(int position) throws SibsException {
		if (position < 0 || position > this.operations.length) {
			throw new SibsException();
		}
		this.operations[position] = null;
	}

	public Operation getOperation(int position) throws SibsException {
		if (position < 0 || position > this.operations.length) {
			throw new SibsException();
		}
		return this.operations[position];
	}

	public int getNumberOfOperations() {
		int result = 0;
		for (int i = 0; i < this.operations.length; i++) {
			if (this.operations[i] != null) {
				result++;
			}
		}
		return result;
	}

	public int getTotalValueOfOperations() {
		int result = 0;
		for (int i = 0; i < this.operations.length; i++) {
			if (this.operations[i] != null) {
				result = result + this.operations[i].getValue();
			}
		}
		return result;
	}

	public int getTotalValueOfOperationsForType(String type) {
		int result = 0;
		for (int i = 0; i < this.operations.length; i++) {
			if (this.operations[i] != null && this.operations[i].getType().equals(type)) {
				result = result + this.operations[i].getValue();
			}
		}
		return result;
	}
}

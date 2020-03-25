package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.MBWayException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class MBWayController {

	private MBWayModel model;
	private MBWayView view;

	public MBWayController(MBWayModel model, MBWayView view) {
		this.model = model;
		this.view = view;
	}

	public void associateMBWay(String iban, String phoneNumber) throws MBWayException {
		try {
			this.view.returnCode(this.model.associateMBWay(iban, phoneNumber));
		} catch (MBWayException e) {
			this.view.somethingWrong(e.getErrorMessage());
		}

	}

	public void confirmMBWay(String phoneNumber, String code) throws MBWayException {
		try {
			if (this.model.confirmMBWay(phoneNumber, code)) {
				this.view.confirmedMBWay();
			} else {
				this.view.declinedMBWay();
			}
		} catch (MBWayException e) {
			this.view.somethingWrong(e.getErrorMessage());
		}
	}

	public void mbWayTransfer(String SourcephoneNumber, String targetPhoneNumber, int amount) {
		try {
			if (this.model.mbWayTransfer(SourcephoneNumber, targetPhoneNumber, amount)) {
				this.view.acceptedTransferMBWay();
			}
			this.view.declinedTransferMBWay();
		} catch (Exception e) {
			this.view.exceptionTransferMBWay();
		}
	}

	public void addFriend(String number, int amount) {
		this.model.addFriend(number, amount);
	}

	public void splitBill(int numberOfFriends, int amount)
			throws MBWayException, SibsException, AccountException, OperationException {
		try {
			this.model.verifyFriends(amount);
			this.model.verifyNumberOfFriends(numberOfFriends);
		} catch (MBWayException e) {
			this.view.somethingWrong(e.getErrorMessage());
			return;
		}
		this.model.splitbill();
		this.view.successSplitBill();
	}
}

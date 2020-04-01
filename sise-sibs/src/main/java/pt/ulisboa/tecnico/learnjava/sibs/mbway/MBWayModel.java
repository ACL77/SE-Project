package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import java.util.HashMap;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.MBWayException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class MBWayModel {

	Services services = new Services();
	private Sibs sibs = new Sibs(100, this.services);
	private static HashMap<String, MBWayAccount> mbWay = new HashMap<String, MBWayAccount>();
	private String firstFriendPhoneNumber = null;

	/*
	 * Associate the PhoneNumber to the Account through the IBAN Assuming that for
	 * each phoneNumber is possible to associate just one account
	 */
	public String associateMBWay(String iban, String phoneNumber) throws MBWayException {
		if (this.services.getAccountByIban(iban) == null) {
			throw new MBWayException("Wrong IBAN!Try again!");
		}
		if (phoneNumber.length() != 9) {
			throw new MBWayException("The phone number is not correct!");
		}
		if (this.mbWay.containsKey(phoneNumber)) {
			throw new MBWayException("The phone is already associated with an account!");
		}
		verificateIBAN(iban);
		MBWayModel.mbWay.put(phoneNumber, new MBWayAccount(iban));
		return mbWay.get(phoneNumber).getCode();

	}

	// IBAN verification
	private void verificateIBAN(String iban) throws MBWayException {
		for (String phoneNumber : this.mbWay.keySet()) {
			if (this.mbWay.get(phoneNumber).getIban() == iban) {
				throw new MBWayException("This IBAN is aready associated with another number.");
			}
		}
	}

	/*
	 * Checks the code provided by the user. Validates the account if the code is
	 * correct or deleats the account if the code is wrong. Assuming that if the
	 * usar fails to validate the account he has to associate the number to the iban
	 * again.
	 */
	public Boolean confirmMBWay(String phoneNumber, String code) throws MBWayException {
		if (!(this.mbWay.containsKey(phoneNumber))) {
			throw new MBWayException("The phone number is not valid.");
		} else if (!code.equals(mbWay.get(phoneNumber).getCode())) {
			MBWayModel.mbWay.remove(phoneNumber);
			return false;
		} else {
			mbWay.get(phoneNumber).ValidateAccount();
			return true;
		}

	}

	// returns true if transfer was performed successfully
	// Refactor for Write Short Units of code
	public Boolean mbWayTransfer(String SourcephoneNumber, String targetPhoneNumber, int amount)
			throws SibsException, AccountException, OperationException, MBWayException {
		if (this.mbWay.containsKey(SourcephoneNumber) && this.mbWay.containsKey(targetPhoneNumber)
				&& mbWay.get(SourcephoneNumber).isValidated() && mbWay.get(targetPhoneNumber).isValidated()) {
			String SourceIban = mbWay.get(SourcephoneNumber).getIban();
			String TargetIban = mbWay.get(targetPhoneNumber).getIban();
			// perform transfer if ibans really exist in banks
			return wasTransferSuccessfull(SourceIban, TargetIban, amount);

		} else {
			throw new MBWayException("At least one of the accounts was not validated");
		}
	}

	private Boolean wasTransferSuccessfull(String SourceIban, String TargetIban, int amount)
			throws SibsException, AccountException, OperationException, MBWayException {
		if ((SourceIban != null) && (TargetIban != null)) {
			int balanceBeforeTransfer = this.services.getAccountByIban(SourceIban).getBalance();
			// perform the transfer with the next two commands
			this.sibs.transfer(SourceIban, TargetIban, amount);
			this.sibs.processOperations();
			// throws exception if the account did not have enough money for comission OR
			// returns true, meaning it was successful
			return verifyEnoughMoneyForTransferAndComission(SourceIban, balanceBeforeTransfer);

		} else {
			return false;
		}
	}

	private Boolean verifyEnoughMoneyForTransferAndComission(String iban, int balanceBeforeTransfer)
			throws MBWayException {
		if (this.services.getAccountByIban(iban).getBalance() == balanceBeforeTransfer) {
			/*
			 * sibs will undo all the operations if the source account has not enough money
			 * for both transfer and commission. This means the money in the account before
			 * and after transfer is the same, so it did not occur
			 */
			throw new MBWayException("Not enough money");
		}
		return true;
	}

	/* verifies if the number of frinds is correct */
	public void verifyNumberOfFriends(int numberOfFriends) throws MBWayException {
		int numberOfAddedFriends = this.mbWay.get(this.firstFriendPhoneNumber).getFriends().size();
		if (numberOfFriends > numberOfAddedFriends) {
			throw new MBWayException("Oh no! One or more friends are missing.");
		}
		if (numberOfFriends < numberOfAddedFriends) {
			throw new MBWayException("Oh no! Too many friends.");
		}
		return;
	}

	/* verifies if the amount is correct */
	private void verifyAmount(int totalAmount) throws MBWayException {
		int total = 0;
		for (String i : this.mbWay.get(this.firstFriendPhoneNumber).getFriends().keySet()) {
			total += this.mbWay.get(this.firstFriendPhoneNumber).getFriends().get(i);
		}

		if (total != totalAmount) {
			throw new MBWayException("Something is wrong. Did you set the bill amount right?");
		}
		;
	}

	/* adds friend for a respective mbway user */
	public void addFriend(String friendPhone, int amount) {
		if (this.firstFriendPhoneNumber == null) {
			this.firstFriendPhoneNumber = friendPhone;
		}
		this.mbWay.get(this.firstFriendPhoneNumber).addFriend(friendPhone, amount);
	}

	/* verifis the existance of a mbway account trough the phone number */
	private void verifyMbWayAccount(String phoneNumber) throws MBWayException {
		int count = 0;
		if (this.mbWay.get(phoneNumber) == null) {
			throw new MBWayException(phoneNumber, "is not registered");
		}

	}

	/* veifies if the account has enought money */
	private void verifyEnoughMoney(String phoneNumber, int amount) throws MBWayException {
		if (this.services.getAccountByIban(this.mbWay.get(phoneNumber).getIban()).getBalance() < amount) {
			resetFriends();
			throw new MBWayException("Oh no! Not enough money to perform the transfer. Please, isert friends again");
		}
	}

	/* friends validation */
	public void verifyFriends(int totalAmount) throws MBWayException {
		int total = 0;
		if (this.firstFriendPhoneNumber == null) {
			throw new MBWayException("You have to add some friends first!");
		}
		HashMap<String, Integer> friends = this.mbWay.get(this.firstFriendPhoneNumber).getFriends();
		// verifies if the user has friends associated to the account
		if (friends.size() <= 1) {
			throw new MBWayException("You can't split the bill with only you!");
		}
		// verifies each of the friends
		for (String phoneNumber : friends.keySet()) {
			verifyMbWayAccount(phoneNumber);
			verifyEnoughMoney(phoneNumber, friends.get(phoneNumber));
			total += friends.get(phoneNumber);
		}
		// amount validation
		verifyAmount(totalAmount);
	}

	/* splits the bill with the respective friends */
	public void splitbill() throws SibsException, AccountException, OperationException, MBWayException {
		HashMap<String, Integer> friends = this.mbWay.get(this.firstFriendPhoneNumber).getFriends();
		for (String phoneNumber : friends.keySet()) {
			if (phoneNumber != this.firstFriendPhoneNumber) {
				this.mbWayTransfer(phoneNumber, this.firstFriendPhoneNumber, friends.get(phoneNumber));
			}
		}
		// reset split bill information
		resetFriends();

	}

	// clears the friends associated to the user
	private void resetFriends() {
		if (this.firstFriendPhoneNumber != null) {
			this.mbWay.get(this.firstFriendPhoneNumber).getFriends().clear();
			this.firstFriendPhoneNumber = null;
		}

	}

	public static HashMap<String, MBWayAccount> getMbWay() {
		return mbWay;
	}

}

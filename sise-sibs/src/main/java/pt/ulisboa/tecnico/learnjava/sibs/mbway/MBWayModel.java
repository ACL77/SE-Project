package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import java.util.HashMap;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.MBWayException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class MBWayModel {

	private Sibs sibs;
	private static HashMap<String, MBWayAccount> mbWay = new HashMap<String, MBWayAccount>();
	private String firstFriendPhoneNumber = null;
	Services services = new Services();

	// Associate the PhoneNumber to the Account through the IBAN
	// Assuming that for each phoneNumber is possible to associate just one account
	public String associateMBWay(String iban, String phoneNumber) throws MBWayException {
		if (this.services.getAccountByIban(iban) == null) {
			throw new MBWayException("Wrong IBAN!Try again!");
		}
		if (phoneNumber.length() != 9) {
			throw new MBWayException("The phone number is not correct!");
		}
		MBWayModel.mbWay.put(phoneNumber, new MBWayAccount(iban));
		return mbWay.get(phoneNumber).getCode();
	}

	public Boolean confirmMBWay(String phoneNumber, String code) throws MBWayException {
		if (!(this.mbWay.containsKey(phoneNumber))) {
			throw new MBWayException("The number is not yet associated with an account.");
		} else if (!code.equals(mbWay.get(phoneNumber).getCode())) {
			MBWayModel.mbWay.remove(phoneNumber);
			return false;
		} else {
			mbWay.get(phoneNumber).ValidateAccount();
			return true;
		}

	}

	public Boolean mbWayTransfer(String SourcephoneNumber, String targetPhoneNumber, int amount)
			throws SibsException, AccountException, OperationException, MBWayException {
		if (mbWay.get(SourcephoneNumber).isValidated() && mbWay.get(targetPhoneNumber).isValidated()) {
			String SourceIban = mbWay.get(SourcephoneNumber).getIban();
			String TargetIban = mbWay.get(targetPhoneNumber).getIban();
			if ((SourceIban != null) && (TargetIban != null)) {
				this.sibs.transfer(SourceIban, TargetIban, amount);
				return true;
			} else {
				return false;
			}
		} else {
			throw new MBWayException("At least one of the accounts was not validated");
		}
	}

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

	public void addFriend(String friendPhone, int amount) {
		if (this.firstFriendPhoneNumber == null) {
			this.firstFriendPhoneNumber = friendPhone;
		}
		this.mbWay.get(this.firstFriendPhoneNumber).addFriend(friendPhone, amount);
	}

	private void verifyMbWayAccount(String phoneNumber) throws MBWayException {
		int count = 0;
		if (this.mbWay.get(phoneNumber) == null) {
			throw new MBWayException(phoneNumber, "is not registered");
		}

	}

	private void verifyEnoughMoney(String phoneNumber, int amount) throws MBWayException {
		if (this.services.getAccountByIban(this.mbWay.get(phoneNumber).getIban()).getBalance() < amount) {
			throw new MBWayException("Oh no! One friend does not have money to pay!");
		}
	}

	public void verifyFriends(int totalAmount) throws MBWayException {
		int total = 0;
		if (this.firstFriendPhoneNumber == null) {
			throw new MBWayException("You have to add some friends first!");
		}
		HashMap<String, Integer> friends = this.mbWay.get(this.firstFriendPhoneNumber).getFriends();
		if (friends.size() <= 1) {
			throw new MBWayException("You can't split the bill with only you!");
		}
		for (String phoneNumber : friends.keySet()) {
			verifyMbWayAccount(phoneNumber);
			verifyEnoughMoney(phoneNumber, friends.get(phoneNumber));
			total += friends.get(phoneNumber);
		}

		verifyAmount(totalAmount);
	}

	public void splitbill() throws SibsException, AccountException, OperationException, MBWayException {
		HashMap<String, Integer> friends = this.mbWay.get(this.firstFriendPhoneNumber).getFriends();
		for (String phoneNumber : friends.keySet()) {
			this.mbWayTransfer(phoneNumber, this.firstFriendPhoneNumber, friends.get(phoneNumber));
		}
	}
}

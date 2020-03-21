package MBWay;

import java.util.HashMap;

import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;

public class MBWayModel {

	private static HashMap<String, String> mbWay = new HashMap<String, String>();
	private HashMap<String, String> codeMBWay = new HashMap<String, String>();
	private Sibs sibs;

	// Associate the PhoneNumber to the Account through the IBAN
	// Assuming that for each phoneNumber is possible to associate just one account
	public String associateMBWay(String iban, String phoneNumber) {
		MBWayModel.mbWay.put(phoneNumber, iban);
		String code = generateCode();
		this.codeMBWay.put(phoneNumber, code);
		return code;
	}

	public String confirmMBWay(String phoneNumber, String code) {
		if (!code.equals(this.codeMBWay.get(phoneNumber))) {
			MBWayModel.mbWay.remove(phoneNumber);
			return "Wrong confirmation code. Try association again.";
		}
		return "MBWAY association confirmed successfully!";
	}

	public String mbWayTransfer(String SourcephoneNumber, String targetPhoneNumber, int amount) {
		String SourceIban = mbWay.get(SourcephoneNumber);
		String TargetIban = mbWay.get(targetPhoneNumber);
		if (!SourceIban.equals(null) && !TargetIban.equals(null)) {
			try {
				this.sibs.transfer(SourceIban, TargetIban, amount);
			} catch (Exception e) {
				return "Not enough money on the source account.";
			}
			return "Transfer performed successfully!";
		} else {
			return "Wrong phone number.";
		}
	}

	public String mbWaySplitBill(String phoneNumber, String friendPhoneNumber, int ammount) {

	}

	private String generateCode() {
		String code = "";
		for (int i = 0; i <= 3; i++) {
			int digit = (int) Math.random() * 9;
			code += digit;
		}
		return code;
	}
}

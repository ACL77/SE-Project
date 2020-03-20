package MBWay;

import java.util.HashMap;

public class MBWayModel {

	private HashMap<String, String> mbWay = new HashMap<String, String>();
	private HashMap<String, String> codeMBWay = new HashMap<String, String>();

	// Associate the PhoneNumber to the Account through the IBAN
	// Assuming that for each phoneNumber is possible to associate just one account
	public String associateMBWay(String iban, String phoneNumber) {
		this.mbWay.put(phoneNumber, iban);
		String code = generateCode();
		this.codeMBWay.put(phoneNumber, code);
		return code;
	}

	public String confirmMBWay(String phoneNumber, String code) {
		if (!code.equals(this.codeMBWay.get(phoneNumber))) {
			this.mbWay.remove(phoneNumber);
			return "Wrong confirmation code. Try association again.";
		}
		return "MBWAY association confirmed successfully!";
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

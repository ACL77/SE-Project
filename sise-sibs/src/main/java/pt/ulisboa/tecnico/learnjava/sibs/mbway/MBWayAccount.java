package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import java.util.HashMap;
import java.util.Random;

public class MBWayAccount {

	private Boolean isValidated;
	private String Iban;
	private final String code;
	private HashMap<String, Integer> friends = new HashMap<String, Integer>();
	private static Random random = new Random();

	public MBWayAccount(String Iban) {
		this.Iban = Iban;
		this.code = generateCode();
	}

	public Boolean isValidated() {
		return this.isValidated;
	}

	public void ValidateAccount() {
		this.isValidated = true;
	}

	public String getIban() {
		return this.Iban;
	}

	public String getCode() {
		return this.code;
	}

	public HashMap<String, Integer> getFriends() {
		return this.friends;
	}

	public void addFriend(String number, int amount) {
		this.friends.put(number, amount);
	}

	public void clearFriends() {
		this.friends.clear();
	}

	private static String generateCode() {
		String code = "";
		for (int i = 0; i <= 3; i++) {
			int digit = Math.abs(random.nextInt() % 10);
			code += digit;
		}
		return code;
	}

}

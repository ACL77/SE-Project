package pt.ulisboa.tecnico.learnjava.bank.domain;

//Class created for Keep Unit Interfaces Small

public class PersonComplemetarInformation {
	
	private final String nif;
	private String phoneNumber;
	private String address;
	private int age;
	
	public PersonComplemetarInformation (String nif, String phoneNumber, String address, int age) {
		this.nif = nif;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.age = age;
		
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNif() {
		return nif;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getAge() {
		return age;
	}

}

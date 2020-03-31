package pt.ulisboa.tecnico.learnjava.bank.domain;

//Class created for Keep Unit Interfaces Small

public class Person {
	private final String firstName;
	private final String lastName;
	private final PersonComplemetarInformation complInfo;
	
	public Person (String firstName, String lastName, PersonComplemetarInformation complementarInfo) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.complInfo = complementarInfo;
	}

	public PersonComplemetarInformation getComplInfo() {
		return complInfo;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}



}

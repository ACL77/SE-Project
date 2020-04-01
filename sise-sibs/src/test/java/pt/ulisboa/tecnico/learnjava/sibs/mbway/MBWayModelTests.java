package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.Person;
import pt.ulisboa.tecnico.learnjava.bank.domain.PersonComplemetarInformation;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.MBWayException;

public class MBWayModelTests {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";

	private Sibs sibs;
	private Bank sourceBank;
	private Bank targetBank;
	private Client sourceClient;
	private Client targetClient;
	private Services services;
	private String sourceIban;
	private String targetIban;
	private MBWayModel model;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.services = new Services();
		this.sibs = new Sibs(100, this.services);
		this.model = new MBWayModel();
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		PersonComplemetarInformation info1 = new PersonComplemetarInformation("123456789", "987654321", "Street", 33);
		PersonComplemetarInformation info2 = new PersonComplemetarInformation("123456780", "987654321", "Street", 20);

		Person person1 = new Person("José", "Manuel", info1);
		Person person2 = new Person("José", "Manuel", info2);

		this.sourceClient = new Client(this.sourceBank, person1);
		this.targetClient = new Client(this.targetBank, person2);
		this.sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		this.targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);

	}

	@Test
	public void AssociateMbWay() throws MBWayException {
		this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		assertTrue(this.model.getMbWay().containsKey(PHONE_NUMBER));
	}

	@Test(expected = Exception.class)
	// trying to associate an account that doesn't exist
	public void AssociateMbWayFalseAccount() throws MBWayException {
		this.model.associateMBWay("CGDCK99", PHONE_NUMBER);
	}

	@Test(expected = Exception.class)
	// trying to associate an invalid phone number
	public void AssociateMbWayInvalidNumber() throws MBWayException {
		this.model.associateMBWay(this.sourceIban, "12345678");
	}

	@Test(expected = Exception.class)
	// trying to associate twice the same phone number
	public void AssociateMbWayTwiceNumber() throws MBWayException {
		this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		this.model.associateMBWay(this.targetIban, PHONE_NUMBER);
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}

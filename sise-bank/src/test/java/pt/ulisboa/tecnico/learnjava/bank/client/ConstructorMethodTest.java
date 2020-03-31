package pt.ulisboa.tecnico.learnjava.bank.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.Person;
import pt.ulisboa.tecnico.learnjava.bank.domain.PersonComplemetarInformation;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class ConstructorMethodTest {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";
	private static final int AGE = 33;

	private Bank bank;

	@Before
	public void setUp() throws BankException {
		this.bank = new Bank("CGD");
	}

	@Test
	public void success() throws ClientException {
		PersonComplemetarInformation info1 = new PersonComplemetarInformation(NIF, PHONE_NUMBER, ADDRESS, 33);		
		Person person1 = new Person(FIRST_NAME, LAST_NAME,info1);		
		Client client = new Client(this.bank, person1);

		assertEquals(this.bank, client.getBank());
		assertEquals(FIRST_NAME, client.getFirstName());
		assertEquals(LAST_NAME, client.getLastName());
		assertEquals(NIF, client.getNif());
		assertEquals(PHONE_NUMBER, client.getPhoneNumber());
		assertEquals(ADDRESS, client.getAddress());
		assertTrue(this.bank.isClientOfBank(client));
	}

	@Test(expected = ClientException.class)
	public void negativeAge() throws ClientException {
		PersonComplemetarInformation info1 = new PersonComplemetarInformation("12345678A", PHONE_NUMBER, ADDRESS, -1);		
		Person person1 = new Person(FIRST_NAME, LAST_NAME,info1);		
		new Client(this.bank, person1);
	}

	@Test(expected = ClientException.class)
	public void no9DigitsNif() throws ClientException {
		PersonComplemetarInformation info1 = new PersonComplemetarInformation("12345678A", PHONE_NUMBER, ADDRESS, AGE);		
		Person person1 = new Person(FIRST_NAME, LAST_NAME,info1);		
		new Client(this.bank, person1);
	}

	@Test(expected = ClientException.class)
	public void no9DigitsPhoneNumber() throws ClientException {
		PersonComplemetarInformation info1 = new PersonComplemetarInformation(NIF, "A87654321", ADDRESS, 33);		
		Person person1 = new Person(FIRST_NAME, LAST_NAME,info1);		
		new Client(this.bank, person1);
	}

	public void twoClientsSameNif() throws ClientException {
		PersonComplemetarInformation info1 = new PersonComplemetarInformation("A87654321", PHONE_NUMBER, ADDRESS, AGE);		
		Person person1 = new Person(FIRST_NAME, LAST_NAME,info1);		
		new Client(this.bank, person1);
		try {
			PersonComplemetarInformation info2 = new PersonComplemetarInformation("A87654321", PHONE_NUMBER, ADDRESS, AGE);		
			Person person2 = new Person(FIRST_NAME, LAST_NAME,info2);		
			new Client(this.bank, person2);
			fail();
		} catch (ClientException e) {
			assertEquals(1, this.bank.getTotalNumberOfClients());
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}

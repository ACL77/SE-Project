package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferMethodMockitoTest {

	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "Antonio";

	private Bank sourceBank;
	private Bank targetBank;
	private Client sourceClient;
	private Client targetClient;
	private String sourceIban;
	private String targetIban;

	// I need to keep this before because i will need the account's IBANs, and
	// without this
	//
	@Before
	public void setUp() throws BankException, AccountException, ClientException {
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
	public void invalidAccounts()
			throws ClientException, BankException, AccountException, SibsException, OperationException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(false);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(false);

		Sibs sibs = new Sibs(100, serviceMock);

		try {
			sibs.transfer(this.sourceIban, this.targetIban, 100);
			fail();
		} catch (SibsException e) {

		}
		assertEquals(0, sibs.getNumberOfOperations());
		assertEquals(0, sibs.getTotalValueOfOperations());
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));

	}

	@Test
	public void invalidSourceAccount()
			throws ClientException, BankException, AccountException, SibsException, OperationException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(false);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);

		Sibs sibs = new Sibs(100, serviceMock);

		try {
			sibs.transfer(this.sourceIban, this.targetIban, 100);
			fail();
		} catch (SibsException e) {

		}
		assertEquals(0, sibs.getNumberOfOperations());
		assertEquals(0, sibs.getTotalValueOfOperations());
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
	}

	@Test
	public void invalidTargetAccount()
			throws ClientException, BankException, AccountException, SibsException, OperationException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(false);

		Sibs sibs = new Sibs(100, serviceMock);

		try {
			sibs.transfer(this.sourceIban, this.targetIban, 100);
			fail();
		} catch (SibsException e) {

		}
		assertEquals(0, sibs.getNumberOfOperations());
		assertEquals(0, sibs.getTotalValueOfOperations());
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}

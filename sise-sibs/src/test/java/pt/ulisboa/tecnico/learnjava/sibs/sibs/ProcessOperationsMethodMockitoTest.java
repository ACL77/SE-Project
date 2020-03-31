package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.COMPLETED;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.ERROR;

public class ProcessOperationsMethodMockitoTest {

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
		PersonComplemetarInformation info1 = new PersonComplemetarInformation(NIF, PHONE_NUMBER, ADDRESS, 33);
		PersonComplemetarInformation info2 = new PersonComplemetarInformation(NIF, PHONE_NUMBER, ADDRESS, 22);
		Person person1 = new Person(FIRST_NAME, LAST_NAME, info1);
		Person person2 = new Person(FIRST_NAME, LAST_NAME, info2);
		this.sourceClient = new Client(this.sourceBank, person1);
		this.targetClient = new Client(this.targetBank, person2);
		this.sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		this.targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
	}

	@Test
	public void noFeeWithSameBank()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(true);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		assertEquals(1, sibs.getNumberOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
		verify(serviceMock, never()).withdraw(this.sourceIban, 6);

	}

	@Test
	public void feeFromSourceAccount() throws SibsException, AccountException, OperationException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);

		Sibs obj = new Sibs(10, serviceMock);
		obj.transfer(this.sourceIban, this.targetIban, 100);
		obj.processOperations();

		assertEquals(1, obj.getNumberOfOperations());
		assertEquals(100, obj.getTotalValueOfOperations());
		assertEquals(100, obj.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		verify(serviceMock).withdraw(this.sourceIban, 6);

	}

	@Test
	public void noWithdrawWithFailedDepositSameBank() throws AccountException, SibsException, OperationException {
		Services servicesMock = mock(Services.class);

		when(servicesMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(servicesMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(servicesMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(true);
		doThrow(new AccountException()).when(servicesMock).deposit(this.sourceIban, 100);

		Sibs testSibs = new Sibs(10, servicesMock);
		testSibs.transfer(this.sourceIban, this.targetIban, 100);
		testSibs.processOperations();

		verify(servicesMock, never()).withdraw(this.targetIban, 100);
	}

	@Test
	public void noWithdrawWithFailedDepositDifferentBanks() throws AccountException, SibsException, OperationException {
		Services servicesMock = mock(Services.class);

		when(servicesMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(servicesMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(servicesMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).when(servicesMock).deposit(this.sourceIban, 100);

		Sibs testSibs = new Sibs(10, servicesMock);
		testSibs.transfer(this.sourceIban, this.targetIban, 100);
		testSibs.processOperations();

		verify(servicesMock, never()).withdraw(this.targetIban, 100);
	}

	@Test
	public void processMultipleOperations() throws AccountException, SibsException, OperationException {
		Services servicesMock = mock(Services.class);

		when(servicesMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(servicesMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(servicesMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);

		Sibs testSibs = new Sibs(10, servicesMock);
		testSibs.transfer(this.sourceIban, this.targetIban, 100);
		testSibs.transfer(this.sourceIban, this.targetIban, 50);
		testSibs.processOperations();

		TransferOperation transfer1 = (TransferOperation) testSibs.getOperation(0);
		TransferOperation transfer2 = (TransferOperation) testSibs.getOperation(1);

		verify(servicesMock).withdraw(this.sourceIban, 100);
		verify(servicesMock).deposit(this.targetIban, 100);
		verify(servicesMock).withdraw(this.sourceIban, 50);
		verify(servicesMock).deposit(this.targetIban, 50);
		assertTrue(transfer1.getState() instanceof COMPLETED);
		assertTrue(transfer2.getState() instanceof COMPLETED);
	}

	@Test
	public void processMultipleOperationsAfterOneFail() throws AccountException, SibsException, OperationException {
		Services servicesMock = mock(Services.class);

		when(servicesMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(servicesMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(servicesMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).when(servicesMock).deposit(this.targetIban, 100);

		Sibs testSibs = new Sibs(10, servicesMock);
		testSibs.transfer(this.sourceIban, this.targetIban, 100);
		testSibs.transfer(this.sourceIban, this.targetIban, 50);
		testSibs.processOperations();

		TransferOperation transfer1 = (TransferOperation) testSibs.getOperation(0);
		TransferOperation transfer2 = (TransferOperation) testSibs.getOperation(1);

		verify(servicesMock).withdraw(this.sourceIban, 100);
		verify(servicesMock, times(3)).deposit(this.targetIban, 100);
		verify(servicesMock).withdraw(this.sourceIban, 50);
		verify(servicesMock).deposit(this.targetIban, 50);
		assertTrue(transfer1.getState() instanceof ERROR);
		assertTrue(transfer2.getState() instanceof COMPLETED);
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}

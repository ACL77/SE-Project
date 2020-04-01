package pt.ulisboa.tecnico.learnjava.sibs.operation;

import static org.junit.Assert.assertEquals;
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
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.CANCELLED;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.COMPLETED;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.DEPOSITED;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.ERROR;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.REGISTERED;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.WITHDRAWN;

public class TransferOperationProcessMethodTest {

	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";

	private Bank sourceBank;
	private Bank targetBank;
	private Client sourceClient;
	private Client targetClient;
	private Services services;
	private String sourceIban;
	private String targetIban;
	private String zeroSourceIban;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.services = new Services();

		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		PersonComplemetarInformation info1 = new PersonComplemetarInformation(NIF, PHONE_NUMBER, ADDRESS, 33);
		Person person1 = new Person(FIRST_NAME, LAST_NAME, info1);
		PersonComplemetarInformation info2 = new PersonComplemetarInformation(NIF, PHONE_NUMBER, ADDRESS, 22);
		Person person2 = new Person(FIRST_NAME, LAST_NAME, info2);
		this.sourceClient = new Client(this.sourceBank, person1);
		this.targetClient = new Client(this.targetBank, person2);
		this.sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		this.targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.zeroSourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 0, 0);

	}

	@Test
	public void confirmOperationRegisteredState() throws OperationException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof REGISTERED);
		assertEquals(1000, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void confirmOperationWithdrawnState() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(this.services);

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof WITHDRAWN);
		assertEquals(900, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void confirmOperationDepositedState() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(this.services);
		transferOperation.process(this.services);

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof DEPOSITED);
		assertEquals(900, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1100, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void confirmOperationCompletedState() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(this.services);
		transferOperation.process(this.services);
		transferOperation.process(this.services);

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof COMPLETED);
		assertEquals(894, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1100, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void failCancelCompletedTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		// REGISTERED -> WITHDRAWN
		transferOperation.process(this.services);
		// WITHDRAWN -> DEPOSITED
		transferOperation.process(this.services);
		// DEPOSITED -> COMPLETED
		transferOperation.process(this.services);
		transferOperation.cancel();
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof COMPLETED);
		assertEquals(894, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1100, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelRegisteredTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.cancel();

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof CANCELLED);
		assertEquals(1000, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelWithdrawnTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		// REGISTERED -> WITHDRAWN
		transferOperation.process(this.services);
		transferOperation.cancel();

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof CANCELLED);
		assertEquals(1000, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelDepositedTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		// REGISTERED -> WITHDRAWN
		transferOperation.process(this.services);
		// WITHDRAWN -> DEPOSITED
		transferOperation.process(this.services);
		transferOperation.cancel();

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof CANCELLED);
		assertEquals(1000, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void failFinalizeCanceledOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.cancel();
		transferOperation.process(this.services);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof CANCELLED);
		assertEquals(1000, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void processCompletedTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.cancel();
		transferOperation.cancel();

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof CANCELLED);
		assertEquals(1000, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelCancelledTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		// REGISTERED -> WITHDRAWN
		transferOperation.process(this.services);
		// WITHDRAWN -> DEPOSITED
		transferOperation.process(this.services);
		// DEPOSITED -> COMPLETED
		transferOperation.process(this.services);
		transferOperation.process(this.services);

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof COMPLETED);
		assertEquals(894, this.services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1100, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void errorTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.zeroSourceIban, this.targetIban, 100);
		// 3 times to RETRY
		transferOperation.process(this.services);
		transferOperation.process(this.services);
		transferOperation.process(this.services);
		// reach ERROR
		transferOperation.process(this.services);

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof ERROR);
		assertEquals(0, this.services.getAccountByIban(this.zeroSourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void processErrorTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.zeroSourceIban, this.targetIban, 100);
		// 3 times to RETRY
		transferOperation.process(this.services);
		transferOperation.process(this.services);
		transferOperation.process(this.services);
		// reach ERROR
		transferOperation.process(this.services);
		// try to process ERROR
		transferOperation.process(this.services);

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof ERROR);
		assertEquals(0, this.services.getAccountByIban(this.zeroSourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelErrorTransferOperation() throws OperationException, SibsException, AccountException {

		TransferOperation transferOperation = new TransferOperation(this.zeroSourceIban, this.targetIban, 100);
		// 3 times to RETRY
		transferOperation.process(this.services);
		transferOperation.process(this.services);
		transferOperation.process(this.services);
		// reach ERROR
		transferOperation.process(this.services);
		// try to cancel ERROR
		transferOperation.cancel();

		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof ERROR);
		assertEquals(0, this.services.getAccountByIban(this.zeroSourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(this.targetIban).getBalance());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}

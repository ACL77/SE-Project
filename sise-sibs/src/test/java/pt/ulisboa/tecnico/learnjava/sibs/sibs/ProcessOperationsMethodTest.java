package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TransferOperationState.CANCELLED;
import TransferOperationState.COMPLETED;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class ProcessOperationsMethodTest {
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

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.services = new Services();
		this.sibs = new Sibs(100, this.services);
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		this.sourceClient = new Client(this.sourceBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		this.targetClient = new Client(this.targetBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
	}

	@Test
	public void processDifferentOperations()
			throws BankException, AccountException, ClientException, SibsException, OperationException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);

		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.transfer(targetIban, sourceIban, 100);

		this.sibs.processOperations();
		TransferOperation firstOperation = (TransferOperation) this.sibs.getOperation(0);
		TransferOperation secondOperation = (TransferOperation) this.sibs.getOperation(1);

		assertEquals(this.sibs.getTotalValueOfOperations(), 200);
		assertEquals(this.sibs.getNumberOfOperations(), 2);
		assertTrue(firstOperation.getStateContext().getCurrentState() instanceof COMPLETED);
		assertTrue(secondOperation.getStateContext().getCurrentState() instanceof COMPLETED);
		assertEquals(994, this.services.getAccountByIban(sourceIban).getBalance());
		assertEquals(994, this.services.getAccountByIban(targetIban).getBalance());
	}

	@Test
	public void processCanceledOperations()
			throws BankException, AccountException, ClientException, SibsException, OperationException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);

		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.transfer(targetIban, sourceIban, 100);
		this.sibs.cancelOperation(1);
		this.sibs.processOperations();
		TransferOperation firstOperation = (TransferOperation) this.sibs.getOperation(0);
		TransferOperation secondOperation = (TransferOperation) this.sibs.getOperation(1);

		assertEquals(this.sibs.getTotalValueOfOperations(), 200);
		assertEquals(this.sibs.getNumberOfOperations(), 2);
		assertTrue(firstOperation.getStateContext().getCurrentState() instanceof COMPLETED);
		assertTrue(secondOperation.getStateContext().getCurrentState() instanceof CANCELLED);
		assertEquals(894, this.services.getAccountByIban(sourceIban).getBalance());
		assertEquals(1100, this.services.getAccountByIban(targetIban).getBalance());
	}

	@Test
	public void processErrorOperations()
			throws BankException, AccountException, ClientException, SibsException, OperationException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);

		this.sibs.transfer(sourceIban, targetIban, 100);
		this.sibs.transfer(targetIban, sourceIban, 100);
		this.sibs.cancelOperation(1);
		this.sibs.processOperations();
		TransferOperation firstOperation = (TransferOperation) this.sibs.getOperation(0);
		TransferOperation secondOperation = (TransferOperation) this.sibs.getOperation(1);
		// TODO

		assertEquals(this.sibs.getTotalValueOfOperations(), 200);
		assertEquals(this.sibs.getNumberOfOperations(), 2);
		assertTrue(firstOperation.getStateContext().getCurrentState() instanceof COMPLETED);
		assertTrue(secondOperation.getStateContext().getCurrentState() instanceof CANCELLED);
		assertEquals(894, this.services.getAccountByIban(sourceIban).getBalance());
		assertEquals(1100, this.services.getAccountByIban(targetIban).getBalance());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
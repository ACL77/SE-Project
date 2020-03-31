package pt.ulisboa.tecnico.learnjava.sibs.sibs;

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
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.COMPLETED;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.ERROR;

public class RetryStateMockitoTest {

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
	public void invalidDepositRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(true);
		doThrow(new AccountException()).when(serviceMock).deposit(this.targetIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock, times(3)).deposit(this.targetIban, 100);
		verify(serviceMock, never()).withdraw(this.sourceIban, 6);
		verify(serviceMock).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof ERROR);
	}

	@Test
	public void invalidWithdrawRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(true);
		doThrow(new AccountException()).when(serviceMock).withdraw(this.sourceIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		verify(serviceMock, times(3)).withdraw(this.sourceIban, 100);
		verify(serviceMock, never()).deposit(this.targetIban, 100);
		verify(serviceMock, never()).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof ERROR);
	}

	@Test
	public void invalidWithdrawComissionRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).when(serviceMock).withdraw(this.sourceIban, 6);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock).deposit(this.targetIban, 100);
		verify(serviceMock, times(3)).withdraw(this.sourceIban, 6);
		verify(serviceMock).deposit(this.sourceIban, 100);
		verify(serviceMock).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof ERROR);
	}

	@Test
	public void invalidWithdrawComissionOneRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(serviceMock).withdraw(this.sourceIban, 6);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		// After the first attempt to withdraw the value and fail, the trasfer is in the
		// RETRY state, so it is invoked the second time
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock).deposit(this.targetIban, 100);
		verify(serviceMock, times(2)).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof COMPLETED);
	}

	@Test
	public void invalidWithdrawOneRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(serviceMock).withdraw(this.sourceIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		// After the first attempt to withdraw the value and fail, the trasfer is in the
		// RETRY state, so it is invoked the second time
		verify(serviceMock, times(2)).withdraw(this.sourceIban, 100);
		verify(serviceMock).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof COMPLETED);
	}

	@Test
	public void invalidDepositOneRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(serviceMock).deposit(this.targetIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		// After the first attempt to deposit the value and fail, the trasfer is in the
		// RETRY state, so it is invoked the second time
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock, times(2)).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof COMPLETED);
	}

	public void multipleTransfersCompletedAfterOneRetry()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(serviceMock).deposit(this.targetIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.transfer(this.sourceIban, this.targetIban, 50);
		sibs.processOperations();

		TransferOperation transfer1 = (TransferOperation) sibs.getOperation(0);
		TransferOperation transfer2 = (TransferOperation) sibs.getOperation(1);
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock, times(2)).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 50);
		verify(serviceMock).deposit(this.targetIban, 50);
		verify(serviceMock).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer1.getState() instanceof COMPLETED);
		assertTrue(transfer2.getState() instanceof COMPLETED);
	}

	@Test
	public void multipleTransfersCompletedAfterOneRetryAndFail()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).when(serviceMock).withdraw(this.sourceIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.transfer(this.sourceIban, this.targetIban, 50);
		sibs.processOperations();

		TransferOperation transfer1 = (TransferOperation) sibs.getOperation(0);
		TransferOperation transfer2 = (TransferOperation) sibs.getOperation(1);
		verify(serviceMock, times(3)).withdraw(this.sourceIban, 100);
		verify(serviceMock, never()).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 50);
		verify(serviceMock).deposit(this.targetIban, 50);
		verify(serviceMock, never()).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer1.getState() instanceof ERROR);
		assertTrue(transfer2.getState() instanceof COMPLETED);
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}

package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.COMPLETED;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.ERROR;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.RETRY;

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
		this.sourceClient = new Client(this.sourceBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		this.targetClient = new Client(this.targetBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
		this.sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		this.targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
	}
	
	@Test
	public void invalidDepositRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {
		
		Services serviceMock = mock(Services.class);
		
		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(sourceIban, targetIban)).thenReturn(true);
		doThrow(new AccountException()).when(serviceMock).deposit(this.targetIban, 100);
	
		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();
		
		TransferOperation transfer= (TransferOperation) sibs.getOperation(0);
		verify(serviceMock).withdraw(sourceIban, 100);
		verify(serviceMock,times(3)).deposit(targetIban, 100);
		verify(serviceMock,never()).withdraw(sourceIban, 6);
		verify(serviceMock).deposit(sourceIban,100);
		verify(serviceMock,never()).withdraw(targetIban,100);
		verify(serviceMock,never()).deposit(sourceIban, 6);
		assertTrue(transfer.getState() instanceof ERROR);
	}
	
	@Test
	public void invalidWithdrawRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {
		
		Services serviceMock = mock(Services.class);
		
		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(sourceIban, targetIban)).thenReturn(true);
		doThrow(new AccountException()).when(serviceMock).withdraw(this.sourceIban, 100);
	
		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();
		
		TransferOperation transfer= (TransferOperation) sibs.getOperation(0);
		verify(serviceMock,times(3)).withdraw(sourceIban, 100);
		verify(serviceMock,never()).deposit(targetIban, 100);
		verify(serviceMock,never()).withdraw(sourceIban, 6);
		verify(serviceMock,never()).deposit(sourceIban,100);
		verify(serviceMock,never()).withdraw(targetIban,100);
		verify(serviceMock,never()).deposit(sourceIban, 6);
		assertTrue(transfer.getState() instanceof ERROR);
	}
	
	@Test
	public void invalidWithdrawComissionRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {
		
		Services serviceMock = mock(Services.class);
		
		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(sourceIban, targetIban)).thenReturn(false);
		doThrow(new AccountException()).when(serviceMock).withdraw(sourceIban, 6);
	
		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();
		
		TransferOperation transfer= (TransferOperation) sibs.getOperation(0);
		verify(serviceMock).withdraw(sourceIban, 100);
		verify(serviceMock).deposit(targetIban, 100);
		verify(serviceMock,times(3)).withdraw(sourceIban, 6);
		verify(serviceMock).deposit(sourceIban,100);
		verify(serviceMock).withdraw(targetIban,100);
		verify(serviceMock,never()).deposit(sourceIban, 6);
		assertTrue(transfer.getState() instanceof ERROR);
	}
	
	@Test
	public void invalidWithdrawComissionOneRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {
		
		Services serviceMock = mock(Services.class);
		
		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(sourceIban, targetIban)).thenReturn(false);
		when(serviceMock.withdraw(sourceIban, 6)).thenThrow(new AccountException()).thenReturn();
	
		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();
		
		TransferOperation transfer= (TransferOperation) sibs.getOperation(0);
		verify(serviceMock,times(3)).withdraw(sourceIban, 6);
		assertTrue(transfer.getState() instanceof ERROR);
	}
		
	
	@After
	public void tearDown() {
		Bank.clearBanks();
	}
	
	

}

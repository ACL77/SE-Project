package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;


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

	//I need to keep this before because i will need the account's IBANs, and without this 
	//
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
	public void noFeeWithSameBank() throws SibsException, AccountException, OperationException, BankException, ClientException {
		
		Services serviceMock = mock(Services.class);
		
		when(serviceMock.verifyAccountExistanceInBank(sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(sourceIban, targetIban)).thenReturn(true);
		
		
		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		
		//no other parameter inside verify is the same as times(1)
		verify(serviceMock).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 100);	
		//verify of addOperations!?!
		
		assertEquals(1, sibs.getNumberOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
		
	}
	
	@Test
	public void invalidAccounts() throws ClientException, BankException, AccountException, SibsException, OperationException  {
		
		Services serviceMock = mock(Services.class);
				
		when(serviceMock.verifyAccountExistanceInBank(sourceIban)).thenReturn(false);
		when(serviceMock.verifyAccountExistanceInBank(targetIban)).thenReturn(false);
		
		Sibs sibs = new Sibs(100, serviceMock);
		
		try {
			sibs.transfer(this.sourceIban, this.targetIban, 100);
			fail();
		}catch (SibsException e) {
			
		}
		verify(serviceMock, never()).deposit(targetIban, 100);	
		verify(serviceMock, never()).withdraw(sourceIban, 100);	
	}
	
	@Test
	public void invalidSourceAccount() throws ClientException, BankException, AccountException, SibsException, OperationException  {
		
		Services serviceMock = mock(Services.class);
				
		when(serviceMock.verifyAccountExistanceInBank(sourceIban)).thenReturn(false);
		when(serviceMock.verifyAccountExistanceInBank(targetIban)).thenReturn(true);
		
		Sibs sibs = new Sibs(100, serviceMock);
		
		try {
			sibs.transfer(this.sourceIban, this.targetIban, 100);
			fail();
		}catch (SibsException e) {
			
		}
		verify(serviceMock, never()).deposit(targetIban, 100);	
		verify(serviceMock, never()).withdraw(sourceIban, 100);	
	}
	
	@Test
	public void invalidTargetAccount() throws ClientException, BankException, AccountException, SibsException, OperationException  {
		
		Services serviceMock = mock(Services.class);
				
		when(serviceMock.verifyAccountExistanceInBank(sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(targetIban)).thenReturn(false);
		
		Sibs sibs = new Sibs(100, serviceMock);
		
		try {
			sibs.transfer(this.sourceIban, this.targetIban, 100);
			fail();
		}catch (SibsException e) {
			
		}
		verify(serviceMock, never()).deposit(targetIban, 100);	
		verify(serviceMock, never()).withdraw(sourceIban, 100);	
	}
	
	@Test
	public void feeFromSourceAccount() throws SibsException, AccountException, OperationException {
		
		Services serviceMock = mock(Services.class);
		
		when(serviceMock.verifyAccountExistanceInBank(sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(sourceIban, targetIban)).thenReturn(false);
		
		Sibs sibs = new Sibs(100, serviceMock);
		Sibs obj = new Sibs(10,serviceMock);
		obj.transfer(this.sourceIban, this.targetIban, 100);
		
		verify(serviceMock).deposit(targetIban, 100);
		verify(serviceMock).withdraw(sourceIban, 106);
				
		
	}
	
	@Test
	public void noWithdrawWithFailedDepositSameBank() 
			throws AccountException, SibsException, OperationException {
		Services servicesMock = mock(Services.class);
		
		when(servicesMock.verifyAccountExistanceInBank(sourceIban)).thenReturn(true);
		when(servicesMock.verifyAccountExistanceInBank(targetIban)).thenReturn(true);
		when(servicesMock.verifySameBank(sourceIban, targetIban)).thenReturn(true);
		doThrow(new AccountException()).when(servicesMock).deposit(sourceIban, 100);
		
		Sibs testSibs = new Sibs(10,servicesMock);
		testSibs.transfer(sourceIban, targetIban, 100);
		
		verify(servicesMock, never()).withdraw(targetIban, 100);
		
	}
	
	@Test
	public void noWithdrawWithFailedDepositDifferentBanks() 
			throws AccountException, SibsException, OperationException {
		Services servicesMock = mock(Services.class);
		
		when(servicesMock.verifyAccountExistanceInBank(sourceIban)).thenReturn(true);
		when(servicesMock.verifyAccountExistanceInBank(targetIban)).thenReturn(true);
		when(servicesMock.verifySameBank(sourceIban, targetIban)).thenReturn(false);
		doThrow(new AccountException()).when(servicesMock).deposit(sourceIban, 100);
		
		Sibs testSibs = new Sibs(10,servicesMock);
		testSibs.transfer(sourceIban, targetIban, 100);
		
		verify(servicesMock, never()).withdraw(targetIban, 100);
		
	}
	
	
	@After
	public void tearDown() {
		Bank.clearBanks();
	}
	
}

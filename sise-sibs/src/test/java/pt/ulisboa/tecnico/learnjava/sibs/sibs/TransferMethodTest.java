package pt.ulisboa.tecnico.learnjava.sibs.sibs;

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
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;
import pt.ulisboa.tecnico.learnjava.sibs.transferoperationstate.REGISTERED;

public class TransferMethodTest {
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
		PersonComplemetarInformation info1 = new PersonComplemetarInformation("123456789", "987654321", "Street", 33);
		PersonComplemetarInformation info2 = new PersonComplemetarInformation("123456780", "987654321", "Street", 20);
		
		Person person1 = new Person("José", "Manuel",info1);
		Person person2 = new Person("José", "Manuel", info2);
		
		this.sourceClient = new Client(sourceBank, person1);
		this.targetClient = new Client(targetBank, person2);
	}

	@Test
	public void createTransferOperationWithNoMoneyTransfer()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);

		this.sibs.transfer(sourceIban, targetIban, 100);

		TransferOperation testOperation = (TransferOperation) this.sibs.getOperation(0);
		
		assertEquals(1000, this.services.getAccountByIban(sourceIban).getBalance());
		assertEquals(1000, this.services.getAccountByIban(targetIban).getBalance());
		assertEquals(1, this.sibs.getNumberOfOperations());
		assertEquals(100, this.sibs.getTotalValueOfOperations());
		assertEquals(100, this.sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertEquals(0, this.sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
		assertTrue(testOperation.getStateContext().getCurrentState() instanceof REGISTERED);
	}

//TODO
	//	@Test
//	public void moneyTransferedWhenTransferoperationStateIsCompleted() throws BankException, AccountException, SibsException, OperationException, ClientException {
//		String sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
//		String targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
//
//		this.sibs.transfer(sourceIban, targetIban, 100);
//		this.sibs.processOperations();
//
//		//TODO como verifico que o estado da TransferOperation � o devido? nao consigo aceder
//		assertEquals(894/*900*/, this.services.getAccountByIban(sourceIban).getBalance());
//		assertEquals(1100, this.services.getAccountByIban(targetIban).getBalance());
//		assertEquals(1, this.sibs.getNumberOfOperations());
//		assertEquals(100, this.sibs.getTotalValueOfOperations());
//		assertEquals(100, this.sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
//		assertEquals(0, this.sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
//	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}

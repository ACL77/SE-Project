package pt.ulisboa.tecnico.learnjava.sibs.mbway;

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
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.MBWayException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

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

	@Test(expected = MBWayException.class)
	// trying to associate an invalid phone number
	public void AssociateMbWayInvalidNumber() throws MBWayException {
		this.model.associateMBWay(this.sourceIban, "12345678");
	}

	@Test(expected = MBWayException.class)
	// trying to associate twice the same phone number
	public void AssociateMbWayTwiceNumber() throws MBWayException {
		this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		this.model.associateMBWay(this.targetIban, PHONE_NUMBER);
	}

	@Test(expected = MBWayException.class)
	// trying to associate twice the same IBAN
	public void AssociateMbWayTwiceIBAN() throws MBWayException {
		this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		this.model.associateMBWay(this.sourceIban, "917834788");
	}

	@Test
	// confirming MBWayAccount
	public void ConfirmMBWay() throws MBWayException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		this.model.confirmMBWay(PHONE_NUMBER, code);
		assertTrue(this.model.getMbWay().get(PHONE_NUMBER).isValidated());
	}

	@Test
	// confirming MBWayAccount with false code
	public void ConfirmMBWayFalseCode() throws MBWayException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		this.model.confirmMBWay(PHONE_NUMBER, "AAAA");
		assertTrue(this.model.getMbWay().size() == 0);
	}

	@Test(expected = MBWayException.class)
	// confirming MBWayAccount with false code
	public void ConfirmMBWayFalseNumber() throws MBWayException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		this.model.confirmMBWay("917834788", code);
	}

	@Test
	// transferMBWay operation
	public void TransferMBWay() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay(PHONE_NUMBER, code);
		this.model.confirmMBWay("917834788", code2);
		this.model.mbWayTransfer(PHONE_NUMBER, "917834788", 100);
		assertEquals(this.services.getAccountByIban(this.sourceIban).getBalance(), 894);
		assertEquals(this.services.getAccountByIban(this.targetIban).getBalance(), 1100);
	}

	@Test(expected = MBWayException.class)
	// transfer with not cofirmed target account
	public void TransferMBWayNoTarget() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay(PHONE_NUMBER, code);
		this.model.mbWayTransfer(PHONE_NUMBER, "917834788", 100);
		assertEquals(this.services.getAccountByIban(this.sourceIban).getBalance(), 1000);
	}

	@Test(expected = MBWayException.class)
	// trasfer with not confirmed source account
	public void TransferMBWayNoSource() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay("917834788", code2);
		this.model.mbWayTransfer(PHONE_NUMBER, "917834788", 100);
		assertEquals(this.services.getAccountByIban(this.sourceIban).getBalance(), 1000);
		assertEquals(this.services.getAccountByIban(this.targetIban).getBalance(), 1000);
	}

	@Test(expected = MBWayException.class)
	// transefrfrom account with not enought money
	public void TransferMBWayNoMoney() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay(PHONE_NUMBER, code);
		this.model.confirmMBWay("917834788", code2);
		this.model.mbWayTransfer(PHONE_NUMBER, "917834788", 10000);
		assertEquals(this.services.getAccountByIban(this.sourceIban).getBalance(), 1000);
		assertEquals(this.services.getAccountByIban(this.targetIban).getBalance(), 1000);
	}

	@Test
	// add friend
	public void addFriend() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay("917834788", code2);
		this.model.confirmMBWay(PHONE_NUMBER, code);
		this.model.addFriend("917834788", 10);
		this.model.addFriend(PHONE_NUMBER, 10);
		assertTrue(this.model.getMbWay().get("917834788").getFriends().containsKey(PHONE_NUMBER));
	}

	@Test
	// splitting bill
	public void splitBill() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay("917834788", code2);
		this.model.confirmMBWay(PHONE_NUMBER, code);
		this.model.addFriend("917834788", 100);
		this.model.addFriend(PHONE_NUMBER, 100);
		this.model.verifyFriends(200);
		this.model.verifyNumberOfFriends(2);
		this.model.splitbill();
		assertEquals(this.services.getAccountByIban(this.targetIban).getBalance(), 1100);
		assertEquals(this.services.getAccountByIban(this.sourceIban).getBalance(), 894);
	}

	@Test(expected = MBWayException.class)
	// splitting bill with no friends
	public void splitBillNoFriends() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay("917834788", code2);
		this.model.addFriend("917834788", 100);
		this.model.verifyFriends(100);
		this.model.verifyNumberOfFriends(1);
		this.model.splitbill();
	}

	@Test(expected = MBWayException.class)
	// error in number of friends
	public void splitBillNoFriends2() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay("917834788", code2);
		this.model.addFriend("917834788", 100);
		this.model.verifyFriends(100);
		this.model.verifyNumberOfFriends(4);
		this.model.splitbill();
	}

	@Test(expected = MBWayException.class)
	// error in ammount to split
	public void splitBillFalseAmmount() throws MBWayException, SibsException, AccountException, OperationException {
		String code = this.model.associateMBWay(this.sourceIban, PHONE_NUMBER);
		String code2 = this.model.associateMBWay(this.targetIban, "917834788");
		this.model.confirmMBWay("917834788", code2);
		this.model.confirmMBWay(PHONE_NUMBER, code);
		this.model.addFriend("917834788", 100);
		this.model.addFriend(PHONE_NUMBER, 100);
		this.model.verifyFriends(6000);
		this.model.verifyNumberOfFriends(4);
		this.model.splitbill();
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
		this.model.getMbWay().clear();
	}
}

package pt.ulisboa.tecnico.learnjava.bank.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class Client {
	private final Set<Account> accounts = new HashSet<Account>();

	private final Bank bank;
	private final Person person;

	public Client(Bank bank, Person person)
			throws ClientException {
		checkParameters(bank, person.getComplInfo().getNif(), person.getComplInfo().getPhoneNumber(), person.getComplInfo().getAge());

		this.bank = bank;
		this.person = person;

		bank.addClient(this);
	}

	private void checkParameters(Bank bank, String nif, String phoneNumber, int age) throws ClientException {
		if (age < 0) {
			throw new ClientException();
		}

		if (nif.length() != 9 || !nif.matches("[0-9]+")) {
			throw new ClientException();
		}

		if (phoneNumber.length() != 9 || !phoneNumber.matches("[0-9]+")) {
			throw new ClientException();
		}

		if (bank.getClientByNif(nif) != null) {
			throw new ClientException();
		}
	}

	public void addAccount(Account account) throws ClientException {
		if (this.accounts.size() == 5) {
			throw new ClientException();
		}

		this.accounts.add(account);
	}

	public void deleteAccount(Account account) {
		this.accounts.remove(account);
	}

	public boolean hasAccount(Account account) {
		return this.accounts.contains(account);
	}

	public int getNumberOfAccounts() {
		return this.accounts.size();
	}

	public Stream<Account> getAccounts() {
		return this.accounts.stream();
	}

	public void happyBirthDay() throws BankException, AccountException, ClientException {
		this.person.getComplInfo().setAge(this.person.getComplInfo().getAge()+1);

		if (this.person.getComplInfo().getAge() == 18) {
			Set<Account> accounts = new HashSet<Account>(this.accounts);
			for (Account account : accounts) {
				YoungAccount youngAccount = (YoungAccount) account;
				youngAccount.upgrade();
			}
		}
	}

	public boolean isInactive() {
		return this.accounts.stream().allMatch(a -> a.isInactive());
	}

	public int numberOfInactiveAccounts() {
		return (int) this.accounts.stream().filter(a -> a.isInactive()).count();
	}

	public Bank getBank() {
		return this.bank;
	}

	public String getFirstName() {
		return this.person.getFirstName();
	}

	public String getLastName() {
		return this.person.getLastName();
	}

	public String getNif() {
		return this.person.getComplInfo().getNif();
	}

	public String getPhoneNumber() {
		return this.person.getComplInfo().getPhoneNumber();
	}

	public String getAddress() {
		return this.person.getComplInfo().getAddress();
	}

	public int getAge() {
		return this.person.getComplInfo().getAge();
	}

	public void setAge(int age) {
		this.person.getComplInfo().setAge(age);
		
	}

}

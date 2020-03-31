package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import java.util.Scanner;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.MBWayException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class MBWayPattern {

	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";

	public String[] splitParameters(String string) {
		return string.split(" ", 3);
	}

	private static int stringToNumber(String string) {
		return Integer.parseInt(string);
	}

	public static void main(String[] args)
			throws MBWayException, SibsException, AccountException, OperationException, BankException, ClientException {
		Services services = new Services();
		new Sibs(100, services);
		Bank bank1 = new Bank("CGD");
		Bank bank2 = new Bank("BPI");
		Client client1 = new Client(bank1, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		Client client2 = new Client(bank2, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
		Client client3 = new Client(bank1, FIRST_NAME, LAST_NAME, "999999999", PHONE_NUMBER, ADDRESS, 20);
		String sourceIban = bank1.createAccount(Bank.AccountType.CHECKING, client1, 1000, 0);
		String targetIban = bank2.createAccount(Bank.AccountType.CHECKING, client2, 1000, 0);
		String zeroSourceIban = bank1.createAccount(Bank.AccountType.CHECKING, client1, 0, 0);
		String client3Iban = bank1.createAccount(Bank.AccountType.CHECKING, client3, 100, 0);

		MBWayModel model = new MBWayModel();
		MBWayView view = new MBWayView();
		MBWayController controller = new MBWayController(model, view);

		System.out.println(sourceIban);
		System.out.println(targetIban);
		System.out.println(zeroSourceIban);
		System.out.println(client3Iban);

		Scanner newScanner = new Scanner(System.in);
		System.out.println("Enter the command:");
		boolean running = true;

		while (running) {

			String newCommand = newScanner.nextLine();
			String[] parameters=null;
			try {
				parameters = newCommand.split(" ");
			}catch(Exception e) {
				System.out.println("Wrong command. Try again");
			}
			
			
			String commandType = parameters[0];

			switch (commandType) {
			case "exit":
				System.out.println("The programm has terminated.");
				running = false;
				break;

			case "associate-mbway":
				controller.associateMBWay(parameters[1], parameters[2]);
				break;

			case "confirm-mbway":
				controller.confirmMBWay(parameters[1], parameters[2]);
				break;

			case "mbway-transfer":
				String sourceNumber = parameters[1];
				String targetNumber = parameters[2];
				int amount = stringToNumber(parameters[3]);
				controller.mbWayTransfer(sourceNumber, targetNumber, amount);
				break;

			case "friend":
				int friendamount = stringToNumber(parameters[2]);
				controller.addFriend(parameters[1], friendamount);
				break;

			case "mbway-split-bill":
				int numberOfFriends = stringToNumber(parameters[1]);
				int totalAmount = stringToNumber(parameters[2]);
				controller.splitBill(numberOfFriends, totalAmount);
				break;

			default:
				System.out.println("There is no command for your request!");
				break;

			}
		}

	}

}
package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import java.util.Scanner;

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

public class MBWayPattern {

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
		
		MBWayModel model = new MBWayModel();
		MBWayView view = new MBWayView();
		MBWayController controller = new MBWayController(model, view);
		
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
				controller.mbWayTransfer(parameters[1], parameters[2], stringToNumber(parameters[3]));
				break;

			case "friend":
				controller.addFriend(parameters[1], stringToNumber(parameters[2]));
				break;

			case "mbway-split-bill":
				controller.splitBill(stringToNumber(parameters[1]), stringToNumber(parameters[2]));
				break;

			default:
				System.out.println("There is no command for your request!");
				break;

			}
		}
		
		newScanner.close();

	}

}
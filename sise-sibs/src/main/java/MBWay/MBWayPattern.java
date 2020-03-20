package MBWay;

import java.util.Scanner;

public class MBWayPattern {

	public static void main(String[] args) {
		MBWayModel model = new MBWayModel();

		Scanner newScanner = new Scanner(System.in);
		System.out.println("Enter the command:");
		while (true) {
			String newCommand = newScanner.nextLine();
			if (newCommand.equals("exit")) {
				System.out.println("The programm has terminated.");
				break;
				// TODO JLabel??
			}
			if (newCommand.startsWith("associate-mbway")) {
				String[] parameters = newCommand.split(" ", 3);
				model.associateMBWay(parameters[1], parameters[2]);
			}
		}

	}
}

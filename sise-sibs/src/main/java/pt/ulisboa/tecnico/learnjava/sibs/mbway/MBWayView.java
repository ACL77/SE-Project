package pt.ulisboa.tecnico.learnjava.sibs.mbway;

public class MBWayView {

	public void returnCode(String code) {
		System.out.println(" Code: " + code + " (don’t share it with anyone)");
	}

	public void confirmedMBWay() {
		System.out.println("MBWAY association confirmed successfully");
	}

	public void declinedMBWay() {
		System.out.println("Wrong confirmation code. Try association again.");
	}

	public void acceptedTransferMBWay() {
		System.out.println("Transfer performed successfully!");
	}

	public void declinedTransferMBWay() {
		System.out.println("Wrong phone number.");
	}

	public void exceptionTransferMBWay() {
		System.out.println("Not enough money on the source account.");
	}

	public void successSplitBill() {
		System.out.println("Bill payed successfully!");
	}

	public void somethingWrong(String message) {
		System.out.println(message);
	}

}

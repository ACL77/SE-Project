package MBWay;

public class MBWayController {

	private MBWayModel model;
	private MBWayView view;

	public MBWayController(MBWayModel model, MBWayView view) {
		this.model = model;
		this.view = view;
	}

	public void associateMBWay(String iban, String phoneNumber) {
		this.model.associateMBWay(iban, phoneNumber);
	}

}

package MBWay;

import java.util.ArrayList;
import java.util.HashMap;

public class MBWayModel {

	private HashMap<String, ArrayList> mbWay = new HashMap<String, ArrayList>();

	public int associateMBWay(String iban, String phonenumber) {
		if (this.mbWay.containsKey(phonenumber)) {
			this.mbWay.get(phonenumber).add(iban);
		} else {
			this.mbWay.put(phonenumber, new ArrayList<String>().add(iban));
		}
		return (int) (Math.random() * 9999);
	}
}

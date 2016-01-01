package javareact.examples.remote;

import java.util.ArrayList;
import java.util.Random;

import javareact.common.Consts;
import javareact.common.types.ObservableInteger;
import javareact.common.types.ObservableList;
import javareact.common.types.ObservableString;

public class RemoteObservable {

	public static void main(String args[]) {
		Consts.hostName = "Remote";
		ObservableInteger obInt = new ObservableInteger("obInt", 1);
		ObservableString obString1 = new ObservableString("obString1", "a");
		ObservableString obString2 = new ObservableString("obString2", "b");
		ObservableList<Integer> obList = new ObservableList<Integer>("obList", new ArrayList<Integer>());
		Random random = new Random();

		while (true) {
			obInt.set(random.nextInt(1000));
			obString1.set(String.valueOf(random.nextInt(10)) + " ");
			obString2.set(String.valueOf(random.nextInt(10)) + "!");
			obList.add(random.nextInt(1000));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
